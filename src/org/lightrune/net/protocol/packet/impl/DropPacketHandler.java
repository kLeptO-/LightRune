package org.lightrune.net.protocol.packet.impl;

import org.lightrune.content.item.Item;
import org.lightrune.net.protocol.packet.Packet;
import org.lightrune.net.protocol.packet.PacketHandler;
import org.lightrune.player.Player;
import org.lightrune.world.item.GroundItem;
import org.lightrune.world.item.GroundItems;

/**
 * Drop item packet handler.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class DropPacketHandler implements PacketHandler {

	@SuppressWarnings("unused")
	@Override
	public void handle(Player player, Packet packet) {
		int itemIndex = packet.getUShortA();
		int interfaceIndex = packet.getUShort();
		int itemSlot = packet.getUShortA();

		Item item = player.inventory().items()[itemSlot];

		/*
		 * Check if inventory slot is not empty.
		 */
		if (item.index() != -1) {
			/*
			 * Define ground item.
			 */
			GroundItem groundItem = new GroundItem(item.index(), item.amount(), player);

			/*
			 * Drop the item.
			 */
			GroundItems.register(groundItem);
			player.packetSender().sendGroundItem(groundItem);
			player.inventory().set(itemSlot, -1, 0);
			player.inventory().refreshItems();
		}
	}

}