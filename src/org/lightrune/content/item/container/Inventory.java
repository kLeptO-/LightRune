package org.lightrune.content.item.container;

import org.lightrune.content.item.ItemContainer;
import org.lightrune.player.Player;

/**
 * RuneScape inventory implementation.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class Inventory extends ItemContainer {

	/**
	 * Allocates a new inventory for specified player.
	 * 
	 * @param player
	 *            the player instance
	 */
	public Inventory(Player player) {
		super(player);
		resetItems();
	}

	@Override
	public int capacity() {
		return 28;
	}

	@Override
	public boolean stack() {
		return false;
	}

	@Override
	public Inventory refreshItems() {
		player().packetSender().sendItemContainer(this, INVENTORY_INTERFACE);
		return this;
	}

	@Override
	public Inventory noSpace() {
		player().packetSender().sendMessage("Not enough space in your inventory.");
		return this;
	}

	public static final int INVENTORY_INTERFACE = 3214;

}