package org.lightrune.net.protocol.packet.impl;

import org.lightrune.content.item.Item;
import org.lightrune.content.item.container.Banking;
import org.lightrune.content.item.container.Inventory;
import org.lightrune.net.protocol.packet.Packet;
import org.lightrune.net.protocol.packet.PacketHandler;
import org.lightrune.player.Player;

/**
 * Switch item packet handler.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class SwitchItemPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {

		int interfaceIndex = packet.getLEShortA();
		packet.getByteC(); // Always 0
		int fromSlot = packet.getLEShortA();
		int toSlot = packet.getLEShort();

		switch (interfaceIndex) {

		case Inventory.INVENTORY_INTERFACE:
		case Banking.BANK_INVENTORY_INTERFACE:
			Item item = player.inventory().items()[fromSlot];
			player.inventory().items()[fromSlot] = player.inventory().items()[toSlot];
			player.inventory().items()[toSlot] = item;
			player.packetSender().sendItemContainer(player.inventory(), Inventory.INVENTORY_INTERFACE);
			player.packetSender().sendItemContainer(player.inventory(), Banking.BANK_INVENTORY_INTERFACE);
			break;

		case Banking.BANK_INTERFACE:
			Item fromItem = player.banking().items()[fromSlot].copy();
			Item toItem = player.banking().items()[toSlot].copy();

			/*
			 * Check if to slot is not empty as we can't move items to empty
			 * slots.
			 */
			if (toItem.index() != -1) {
				if (player.banking().insertItems()) {
					/*
					 * Insert items mode.
					 */
					if (fromSlot < toSlot) {
						for (int i = fromSlot + 1; i <= toSlot; i++) {
							item = player.banking().items()[i];
							player.banking().items()[i - 1].index(item.index()).amount(item.amount());
						}
					} else {
						for (int i = fromSlot - 1; i >= toSlot; i--) {
							item = player.banking().items()[i];
							player.banking().items()[i + 1].index(item.index()).amount(item.amount());
						}
					}
					player.banking().items()[toSlot].index(fromItem.index()).amount(fromItem.amount());
				} else {
					/*
					 * Swap items mode.
					 */
					player.banking().items()[toSlot].index(fromItem.index()).amount(fromItem.amount());
					player.banking().items()[fromSlot].index(toItem.index()).amount(toItem.amount());
				}
			} else {
				player.packetSender().sendItemContainer(player.banking(), Banking.BANK_INTERFACE);
			}

			break;
		}
	}

}