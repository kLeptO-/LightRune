package org.lightrune.player;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.lightrune.content.item.ItemDefinition;
import org.lightrune.content.item.container.Equipment;
import org.lightrune.net.protocol.packet.PacketBuilder;
import org.lightrune.util.RS2Utils;
import org.lightrune.world.World;

/**
 * Player updating procedure manager.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class PlayerUpdating {

	private final Player player;
	private List<Player> localPlayers;

	private boolean teleporting = true;
	private boolean mapRegionChanging = true;

	private boolean updateRequired = true;
	private boolean chatUpdateRequired = false;
	private boolean appearanceUpdateRequired = true;

	public byte chatText[] = new byte[256];
	public int chatTextEffects = 0, chatTextColor = 0;

	/**
	 * Creates a new player updating manager.
	 * 
	 * @param player
	 *            the player object
	 */
	public PlayerUpdating(Player player) {
		this.player = player;
		localPlayers = new LinkedList<Player>();
	}

	/**
	 * Updates player which is associated with this updating manager.
	 */
	public void updateThisPlayer() {
		if (mapRegionChanging) {
			player.packetSender().sendMapRegion();
		}
		PacketBuilder out = PacketBuilder.allocate(2048);
		PacketBuilder block = PacketBuilder.allocate(1024);
		out.createShortSizedFrame(81, player.channelContext().encryption());
		out.bitAccess();
		updateThisPlayerMovement(out);
		updatePlayer(block, player, false);
		out.putBits(8, localPlayers.size()); // Local players list size
		for (Iterator<Player> iterator$ = localPlayers.iterator(); iterator$.hasNext();) {
			Player otherPlayer = iterator$.next();
			if (World.containsPlayers(otherPlayer) && !otherPlayer.updating().teleporting && otherPlayer.location().withinDistance(player.location())) {
				updatePlayerMovement(out, otherPlayer);
				if (otherPlayer.updating().updateRequired) {
					updatePlayer(block, otherPlayer, false);
				}
			} else {
				iterator$.remove();
				out.putBits(1, 1); // Update Requierd
				out.putBits(2, 3); // Remove Player
			}
		}
		for (Player otherPlayer : World.players()) {
			if (localPlayers.size() >= 255) {
				break;
			}
			if (otherPlayer == null) {
				continue;
			}
			if (otherPlayer == player || localPlayers.contains(otherPlayer) || !otherPlayer.location().withinDistance(player.location())) {
				continue;
			}
			localPlayers.add(otherPlayer);
			addPlayer(out, otherPlayer);
			updatePlayer(block, otherPlayer, true);
		}
		if (block.buffer().position() > 0) {
			out.putBits(11, 2047);
			out.byteAccess();
			out.put(block.buffer());
		} else {
			out.byteAccess();
		}
		out.finishShortSizedFrame();
		out.sendTo(player.channelContext().channel());
	}

	/**
	 * Updates associated player movement.
	 * 
	 * @param out
	 *            the packet builder
	 */
	public void updateThisPlayerMovement(PacketBuilder out) {
		if (teleporting || mapRegionChanging) {
			out.putBits(1, 1); // Update Required
			out.putBits(2, 3); // Player Teleported
			out.putBits(2, player.location().z()); // current height
			out.putBits(1, teleporting); // if teleport discard walking
			out.putBits(1, updateRequired); // update required
			out.putBits(7, player.location().localY());
			out.putBits(7, player.location().localX());
		} else {
			if (player.movementQueue().walkingDirection() == -1) {
				if (updateRequired) {
					out.putBits(1, 1); // update required
					out.putBits(2, 0); // we didn't move
				} else {
					out.putBits(1, 0); // Nothing changed
				}
			} else {
				if (player.movementQueue().runningDirection() == -1) {
					out.putBits(1, 1); // Walked
					out.putBits(2, 1); // Only walked
					out.putBits(3, player.movementQueue().walkingDirection()); // Direction
					out.putBits(1, updateRequired); // Update block
				} else {
					out.putBits(1, 1); // Walked
					out.putBits(2, 2); // Player is running
					out.putBits(3, player.movementQueue().walkingDirection()); // Walking
					out.putBits(3, player.movementQueue().runningDirection()); // Running
					out.putBits(1, updateRequired); // Update block
				}
			}
		}
	}

	/**
	 * Updates other player movement.
	 * 
	 * @param out
	 *            the packet builder
	 */
	public void updatePlayerMovement(PacketBuilder out, Player player) {
		if (player.movementQueue().walkingDirection() == -1) {
			if (player.updating().updateRequired) {
				out.putBits(1, 1); // Update required
				out.putBits(2, 0); // No movement
			} else {
				out.putBits(1, 0); // Nothing changed
			}
		} else if (player.movementQueue().runningDirection() == -1) {
			out.putBits(1, 1); // Update required
			out.putBits(2, 1); // Player walking one tile
			out.putBits(3, player.movementQueue().walkingDirection()); // Walking
			out.putBits(1, player.updating().updateRequired); // Update
		} else {
			out.putBits(1, 1); // Update Required
			out.putBits(2, 2); // Moved two tiles
			out.putBits(3, player.movementQueue().walkingDirection()); // Walking
			out.putBits(3, player.movementQueue().runningDirection()); // Running
			out.putBits(1, player.updating().updateRequired); // Update
		}
	}

	/**
	 * Applies player update block.
	 * 
	 * @param out
	 *            the packet builder
	 * 
	 * @param player
	 *            the player instance
	 * 
	 * @param forceUpdate
	 *            the force update flag used for newly added players
	 */
	public void updatePlayer(PacketBuilder out, Player player, boolean forceUpdate) {
		if (!player.updating().updateRequired && !forceUpdate) {
			return;
		}

		int mask = 0x0;

		if (player.updating().chatUpdateRequired && player != player()) {
			mask |= 0x80;
		}

		if (player.updating().appearanceUpdateRequired || forceUpdate) {
			mask |= 0x10;
		}

		if (mask >= 0x100) {
			mask |= 0x40;
			out.putByte(mask & 0xFF);
			out.putByte(mask >> 8);
		} else {
			out.putByte(mask);
		}

		if (player.updating().chatUpdateRequired && player != player()) {
			updatePlayerChat(out, player);
		}

		if (player.updating().appearanceUpdateRequired || forceUpdate) {
			updatePlayerAppearance(out, player);
		}
	}

	/**
	 * Updates player chat.
	 * 
	 * @param out
	 *            the packet builder
	 * 
	 * @param player
	 *            the player instance
	 */
	public void updatePlayerChat(PacketBuilder out, Player player) {
		int effects = ((player.updating().chatTextColor & 0xff) << 8) + (player.updating().chatTextEffects & 0xff);
		out.putLEShort(effects);
		out.putByte(0); // TODO: Player Rights.
		out.putByteC(player.updating().chatText.length);
		out.put(player.updating().chatText);
	}

	/**
	 * Updates player appearance.
	 * 
	 * @param out
	 *            the packet builder
	 * 
	 * @param player
	 *            the player instance
	 */
	public void updatePlayerAppearance(PacketBuilder out, Player player) {
		PacketBuilder props = PacketBuilder.allocate(128);
		props.putByte(0); // TODO: Player Sex
		props.putByte(-1); // TODO: Player Skull
		props.putByte(-1); // TODO: Player Headicon
		
		int[] equip = new int[player.equipment().capacity()];
		for (int i = 0; i < player.equipment().capacity(); i++) {
			equip[i] = player.equipment().items()[i].index();
		}

		if (equip[Equipment.HAT_SLOT] > -1) {
			props.putShort(0x200 + equip[Equipment.HAT_SLOT]);
		} else {
			props.putByte(0); // Player Hat
		}

		if (equip[Equipment.CAPE_SLOT] > -1) {
			props.putShort(0x200 + equip[Equipment.CAPE_SLOT]);
		} else {
			props.putByte(0); // Player Cape
		}

		if (equip[Equipment.AMULET_SLOT] > -1) {
			props.putShort(0x200 + equip[Equipment.AMULET_SLOT]);
		} else {
			props.putByte(0); // Player Amulet
		}

		if (equip[Equipment.WEAPON_SLOT] > -1) {
			props.putShort(0x200 + equip[Equipment.WEAPON_SLOT]);
		} else {
			props.putByte(0); // Player Weapon
		}

		if (equip[Equipment.BODY_SLOT] > -1) {
			props.putShort(0x200 + equip[Equipment.BODY_SLOT]);
		} else {
			props.putShort(0x100 + 19); // Player Body
		}

		if (equip[Equipment.SHIELD_SLOT] > -1) {
			props.putShort(0x200 + equip[Equipment.SHIELD_SLOT]);
		} else {
			props.putByte(0); // Player Shield
		}

		if (ItemDefinition.fullBody(equip[Equipment.BODY_SLOT])) {
			props.putByte(0);
		} else {
			props.putShort(0x100 + 29); // Player Arms
		}

		if (equip[Equipment.LEGS_SLOT] > -1) {
			props.putShort(0x200 + equip[Equipment.LEGS_SLOT]);
		} else {
			props.putShort(0x100 + 39); // Player Legs
		}

		if (ItemDefinition.fullHat(equip[Equipment.HAT_SLOT])) {
			props.putByte(0);
		} else {
			props.putShort(0x100 + 3); // Player Head
		}

		if (equip[Equipment.HANDS_SLOT] > -1) {
			props.putShort(0x200 + equip[Equipment.HANDS_SLOT]);
		} else {
			props.putShort(0x100 + 35); // Player Hands
		}

		if (equip[Equipment.FEET_SLOT] > -1) {
			props.putShort(0x200 + equip[Equipment.FEET_SLOT]);
		} else {
			props.putShort(0x100 + 44); // Player Feet
		}

		props.putByte(0);
		props.putByte(7); // TODO: Hair color
		props.putByte(8); // TODO: Body color.
		props.putByte(9); // TODO: Leg color
		props.putByte(5); // TODO: Feet color
		props.putByte(0); // TODO: Skin color
		props.putShort(0x328); // TODO: standAnimIndex
		props.putShort(0x337); // TODO: standTurnAnimIndex
		props.putShort(0x333); // TODO: walkAnimIndex
		props.putShort(0x334); // TODO: turn180AnimIndex
		props.putShort(0x335); // TODO: turn90CWAnimIndex
		props.putShort(0x336); // TODO: turn90CCWAnimIndex
		props.putShort(0x338); // TODO: runAnimIndex
		props.putLong(RS2Utils.getLongString(player.username()));
		props.putByte(3);
		props.putShort(0);
		out.putByteC(props.buffer().position());
		out.put(props.buffer());
	}

	/**
	 * Adds new player character to the local player list.
	 * 
	 * @param out
	 *            the output packet builder
	 * 
	 * @param otherPlayer
	 *            the other player instance
	 */
	public void addPlayer(PacketBuilder out, Player otherPlayer) {
		out.putBits(11, otherPlayer.index()); // Writing player index.
		out.putBits(1, 1); // Update required.
		out.putBits(1, 1); // Discard walking.
		int yPos = otherPlayer.location().y() - player.location().y();
		int xPos = otherPlayer.location().x() - player.location().x();
		out.putBits(5, yPos); // The relative coordinates.
		out.putBits(5, xPos); // The relative coordinates.
	}

	/**
	 * Clears the update flags.
	 */
	public void resetUpdateFlags() {
		teleporting = mapRegionChanging = false;
		updateRequired = chatUpdateRequired = appearanceUpdateRequired = false;
		chatTextEffects = chatTextColor = 0;
		chatText = new byte[256];
		player.movementQueue().walkingDirection(-1).runningDirection(-1);
	}

	/**
	 * Gets the associated player.
	 * 
	 * @return the associated player
	 */
	public Player player() {
		return player;
	}

	/**
	 * Sets the local player list.
	 * 
	 * @param localPlayers
	 *            the local player list
	 */
	public PlayerUpdating localPlayers(List<Player> localPlayers) {
		this.localPlayers = localPlayers;
		return this;
	}

	/**
	 * Sets the map region change status.
	 * 
	 * @param status
	 *            the status flag
	 */
	public PlayerUpdating mapRegionChanging(boolean status) {
		this.mapRegionChanging = status;
		return this;
	}

	/**
	 * Sets the teleporting status.
	 * 
	 * @param status
	 *            the status flag
	 */
	public PlayerUpdating teleporting(boolean status) {
		this.teleporting = status;
		return this;
	}

	/**
	 * Sets the updating status.
	 * 
	 * @param status
	 *            the status flag
	 */
	public PlayerUpdating updateRequired(boolean status) {
		this.updateRequired = status;
		return this;
	}

	/**
	 * Sets the chat updating status.
	 * 
	 * @param status
	 *            the status flag
	 */
	public PlayerUpdating chatUpdateRequired(boolean status) {
		this.chatUpdateRequired = status;
		return updateRequired(true);
	}

	/**
	 * Sets the appearance updating status.
	 * 
	 * @param status
	 *            the status flag
	 */
	public PlayerUpdating appearanceUpdateRequired(boolean status) {
		this.appearanceUpdateRequired = status;
		return updateRequired(true);
	}

	/**
	 * Sets the chat text for chat updating.
	 * 
	 * @param chatText
	 *            the chat text bytes
	 */
	public PlayerUpdating chatText(byte[] chatText) {
		this.chatText = chatText;
		return this;
	}

	/**
	 * Sets the chat text effects for chat updating.
	 * 
	 * @param chatTextEffects
	 *            the chat text effects
	 */
	public PlayerUpdating chatTextEffects(int chatTextEffects) {
		this.chatTextEffects = chatTextEffects;
		return this;
	}

	/**
	 * Sets the chat text color for chat updating.
	 * 
	 * @param chatTextColor
	 *            the chat text color
	 */
	public PlayerUpdating chatTextColor(int chatTextColor) {
		this.chatTextColor = chatTextColor;
		return this;
	}

}