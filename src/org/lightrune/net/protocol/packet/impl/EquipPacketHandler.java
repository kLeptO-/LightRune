package org.lightrune.net.protocol.packet.impl;

import org.lightrune.content.item.ItemDefinition;
import org.lightrune.content.item.container.Inventory;
import org.lightrune.net.protocol.packet.Packet;
import org.lightrune.net.protocol.packet.PacketHandler;
import org.lightrune.player.Player;

/**
 * Equip packet handler.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class EquipPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		int itemIndex = packet.getUShort();
		int itemSlot = packet.getUShortA();
		int interfaceIndex = packet.getUShortA();

		/*
		 * Check if this is equipment packet.
		 */
		if (ItemDefinition.get(itemIndex).equipmentSlot() > -1 && interfaceIndex == Inventory.INVENTORY_INTERFACE) {
			/*
			 * Attempt to equip the item.
			 */
			player.equipment().equip(itemIndex, itemSlot);
		}
	}

}