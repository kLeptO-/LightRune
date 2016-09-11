package org.lightrune.player;

import org.lightrune.content.item.ItemDefinition;
import org.lightrune.world.Location;
import org.lightrune.world.World;
import org.lightrune.world.item.GroundItem;
import org.lightrune.world.item.GroundItems;

/**
 * Player utility methods.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class PlayerUtils {

	private final Player player;

	/**
	 * Creates a new utilities the for player.
	 * 
	 * @param player
	 *            the player object
	 */
	public PlayerUtils(Player player) {
		this.player = player;
	}

	/**
	 * Attempts to pickup an item.
	 * 
	 * @param location
	 *            the item location
	 * 
	 * @param index
	 *            the item index
	 */
	public PlayerUtils pickupItem(Location location, int index) {
		/*
		 * Let's check if ground item exists.
		 */
		GroundItem groundItem = GroundItems.find(location, index);
		if (groundItem == null) {
			return this;
		}

		/*
		 * Check if it is stackable and to pick it up or discard if it exceeds
		 * max stack size.
		 */
		if (ItemDefinition.get(index).stackable()) {
			int itemSlot = player.inventory().itemSlot(index);

			/*
			 * Check if we have enough space in the inventory.
			 */
			if (itemSlot == -1 && player.inventory().freeSpace() == 0) {
				player.inventory().noSpace();
				return this;
			}

			if (itemSlot != -1) {
				/*
				 * If we already have the same item in the inventory let's
				 * calculate the total amount and discard if it exceeds max
				 * stack.
				 */
				long totalAmount = (long) player.inventory().items()[itemSlot].amount() + (long) groundItem.item().amount();
				if (totalAmount > Integer.MAX_VALUE) {
					player.inventory().noSpace();
					return this;
				}
			}

			/*
			 * Pickup the stackable item.
			 */
			player.inventory().add(groundItem.item());
			GroundItems.deregister(groundItem);
			if (groundItem.timer() <= GroundItem.LIFE_SPAN / 2) {
				GroundItems.sendRemoveGroundItem(groundItem);
			} else {
				player.packetSender().sendRemoveGroundItem(groundItem);
			}
		} else {
			/*
			 * Else if item is non-stackable let's check if we have enough
			 * space.
			 */
			if (player.inventory().freeSpace() == 0) {
				player.inventory().noSpace();
				return this;
			}

			/*
			 * And finally just pick it up.
			 */
			player.inventory().add(groundItem.item());
			GroundItems.deregister(groundItem);
			if (groundItem.timer() <= GroundItem.LIFE_SPAN / 2) {
				GroundItems.sendRemoveGroundItem(groundItem);
			} else {
				player.packetSender().sendRemoveGroundItem(groundItem);
			}
		}
		return this;
	}

	/**
	 * Resets player on logout.
	 */
	public PlayerUtils resetLogout() {
		/*
		 * Reset trading.
		 */
		if (player.trading().isTrading()) {
			Player otherPlayer = World.players()[player.trading().trader()];
			if (otherPlayer != null) {
				otherPlayer.trading().declineTrade(true);
			}
		}
		
		/*
		 * Save game.
		 */
		PlayerSaving.save(player);
		return this;
	}

	/**
	 * Gets the associated player.
	 * 
	 * @return the associated player
	 */
	public Player player() {
		return player;
	}

}