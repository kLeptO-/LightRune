package org.lightrune.player;

import org.lightrune.content.item.Item;
import org.lightrune.content.item.ItemContainer;
import org.lightrune.content.item.container.Equipment;
import org.lightrune.content.item.container.Inventory;
import org.lightrune.net.protocol.packet.PacketBuilder;
import org.lightrune.world.Location;
import org.lightrune.world.item.GroundItem;

/**
 * Protocol packets sender.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class PacketSender {

	private final Player player;

	/**
	 * Creates a new packet sender for player.
	 * 
	 * @param player
	 *            the player object
	 */
	public PacketSender(Player player) {
		this.player = player;
	}

	/**
	 * Sends initialization packet to the client.
	 */
	public PacketSender sendInitPacket() {
		PacketBuilder out = PacketBuilder.allocate(4);
		out.createFrame(249, player.channelContext().encryption());
		out.putByteA(1);
		out.putLEShortA(player.index());
		out.sendTo(player.channelContext().channel());
		sendSidebar(0, 2423); // attack tab
		sendSidebar(1, 3917); // skills tab
		sendSidebar(2, 638); // quest tab
		sendSidebar(3, 3213); // backpack tab
		sendSidebar(4, 1644); // items wearing tab
		sendSidebar(5, 5608); // pray tab
		sendSidebar(6, 1151); // magic tab
		sendSidebar(7, -1); // clan chat
		sendSidebar(8, 5065); // friend
		sendSidebar(9, 5715); // ignore
		sendSidebar(10, 2449); // logout tab
		sendSidebar(11, 904); // wrench tab
		sendSidebar(12, 147); // run tab
		sendSidebar(13, -1); // harp tab
		sendPlayerMenuOption(2, "Attack");
		sendPlayerMenuOption(3, "Follow");
		sendPlayerMenuOption(4, "Trade With");
		sendItemContainer(player.inventory(), Inventory.INVENTORY_INTERFACE);
		sendItemContainer(player.equipment(), Equipment.EQUIPMENT_INTERFACE);
		return this;
	}

	/**
	 * Sends the logout packet.
	 */
	public PacketSender sendLogout() {
		PacketBuilder out = PacketBuilder.allocate(1);
		out.createFrame(109, player.channelContext().encryption());
		out.sendTo(player.channelContext().channel());
		return this;
	}

	/**
	 * Sends map region to the client.
	 */
	public PacketSender sendMapRegion() {
		PacketBuilder out = PacketBuilder.allocate(5);
		out.createFrame(73, player.channelContext().encryption());
		out.putShortA(player.location().regionX() + 6);
		out.putShort(player.location().regionY() + 6);
		out.sendTo(player.channelContext().channel());
		player.currentRegion(player.location().copy());
		return this;
	}

	/**
	 * Sends message to the chat window.
	 * 
	 * @param message
	 *            the message
	 */
	public PacketSender sendMessage(String message) {
		PacketBuilder out = PacketBuilder.allocate(256);
		out.createSizedFrame(253, player.channelContext().encryption());
		out.putString(message);
		out.finishSizedFrame();
		out.sendTo(player.channelContext().channel());
		return this;
	}

	/**
	 * Sends string to the interface.
	 * 
	 * @param stringIndex
	 *            the string index
	 * 
	 * @param string
	 *            the string
	 */
	public PacketSender sendString(int stringIndex, String string) {
		PacketBuilder out = PacketBuilder.allocate(1024);
		out.createShortSizedFrame(126, player.channelContext().encryption());
		out.putString(string);
		out.putShortA(stringIndex);
		out.finishShortSizedFrame();
		out.sendTo(player.channelContext().channel());
		return this;
	}

	/**
	 * Sends packet which closes all currently opened interfaces.
	 */
	public PacketSender sendCloseInterface() {
		PacketBuilder out = PacketBuilder.allocate(1);
		out.createFrame(219, player.channelContext().encryption());
		out.sendTo(player.channelContext().channel());
		return this;
	}

	/**
	 * Sends interface set to the client.
	 * 
	 * @param interfaceIndex
	 *            the game window interface
	 * 
	 * @param sidebarInterfaceIndex
	 *            the sidebar interface
	 */
	public PacketSender sendInterfaceSet(int interfaceIndex, int sidebarInterfaceIndex) {
		PacketBuilder out = PacketBuilder.allocate(5);
		out.createFrame(248, player.channelContext().encryption());
		out.putShortA(interfaceIndex);
		out.putShort(sidebarInterfaceIndex);
		out.sendTo(player.channelContext().channel());
		return this;
	}

	/**
	 * Sends a player right click menu option.
	 * 
	 * @param menuIndex
	 *            the menu index
	 * 
	 * @param menuName
	 *            the menu option name
	 */
	public PacketSender sendPlayerMenuOption(int menuIndex, String menuName) {
		PacketBuilder out = PacketBuilder.allocate(256);
		out.createSizedFrame(104, player.channelContext().encryption());
		out.putByteC(menuIndex).putByteA(0);
		out.putString(menuName);
		out.finishSizedFrame();
		out.sendTo(player.channelContext().channel());
		return this;
	}

	/**
	 * Sends the interface which shuold be associated with the sidebar.
	 * 
	 * @param sidebarId
	 *            the sidebar index
	 * 
	 * @param interfaceId
	 *            the interface index
	 */
	public PacketSender sendSidebar(int sidebarId, int interfaceId) {
		PacketBuilder out = PacketBuilder.allocate(4);
		out.createFrame(71, player.channelContext().encryption());
		out.putShort(interfaceId);
		out.putByteA(sidebarId);
		out.sendTo(player.channelContext().channel());
		return this;
	}

	/**
	 * Sends the client configuration.
	 * 
	 * @param configIndex
	 *            the configuration index
	 * 
	 * @param state
	 *            the configuration state
	 */
	public PacketSender sendConfig(int configIndex, int state) {
		PacketBuilder out = PacketBuilder.allocate(4);
		out.createFrame(36, player.channelContext().encryption());
		out.putLEShort(configIndex);
		out.putByte(state);
		out.sendTo(player.channelContext().channel());
		return this;
	}

	/**
	 * Sends item container to the client interface.
	 * 
	 * @param container
	 *            the item container
	 * 
	 * @param interfaceIndex
	 *            the interface index
	 */
	public PacketSender sendItemContainer(ItemContainer container, int interfaceIndex) {
		PacketBuilder out = PacketBuilder.allocate(5 + (container.capacity() * 7));
		out.createShortSizedFrame(53, player.channelContext().encryption());
		out.putShort(interfaceIndex);
		out.putShort(container.capacity());
		for (Item item : container.items()) {
			if (item.amount() > 254) {
				out.putByte(255);
				out.putMESmallInt(item.amount());
			} else {
				out.putByte(item.amount());
			}
			out.putLEShortA(item.index() + 1);
		}
		out.finishShortSizedFrame();
		out.sendTo(player.channelContext().channel());
		return this;
	}

	/**
	 * Sends a single entity location.
	 * 
	 * @param location
	 *            the entity location
	 */
	public PacketSender sendEntityLocation(Location location) {
		PacketBuilder out = PacketBuilder.allocate(3);
		out.createFrame(85, player.channelContext().encryption());
		out.putByteC(location.y() - 8 * player.currentRegion().regionY());
		out.putByteC(location.x() - 8 * player.currentRegion().regionX());
		out.sendTo(player.channelContext().channel());
		return this;
	}

	/**
	 * Sends a ground item.
	 * 
	 * @param groundItem
	 *            the ground item
	 */
	public PacketSender sendGroundItem(GroundItem groundItem) {
		sendEntityLocation(groundItem.location());
		PacketBuilder out = PacketBuilder.allocate(6);
		out.createFrame(44, player.channelContext().encryption());
		out.putLEShortA(groundItem.item().index());
		out.putShort(groundItem.item().amount());
		out.putByte(0);
		out.sendTo(player.channelContext().channel());
		return this;
	}

	/**
	 * Sends a remove ground item packet.
	 * 
	 * @param groundItem
	 *            the ground item
	 */
	public PacketSender sendRemoveGroundItem(GroundItem groundItem) {
		sendEntityLocation(groundItem.location());
		PacketBuilder out = PacketBuilder.allocate(4);
		out.createFrame(156, player.channelContext().encryption());
		out.putByteS(0);
		out.putShort(groundItem.item().index());
		out.sendTo(player.channelContext().channel());
		return this;
	}

	/**
	 * Gets the associated player
	 * 
	 * @return the associated player
	 */
	public Player player() {
		return player;
	}

}