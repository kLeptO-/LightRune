package org.lightrune.net.protocol.packet.impl;

import org.lightrune.content.item.container.Banking;
import org.lightrune.content.item.container.Equipment;
import org.lightrune.content.item.trade.Trading;
import org.lightrune.net.protocol.packet.Packet;
import org.lightrune.net.protocol.packet.PacketHandler;
import org.lightrune.player.Player;

/**
 * Item action packet handler.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class ItemActionPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		switch (packet.opcode()) {
		case FIRST_ITEM_ACTION_OPCODE:
			handleFirstItemAction(player, packet);
			break;
		case SECOND_ITEM_ACTION_OPCODE:
			handleSecondItemAction(player, packet);
			break;
		case THIRD_ITEM_ACTION_OPCODE:
			handleThirdItemAction(player, packet);
			break;
		case FOURTH_ITEM_ACTION_OPCODE:
			handleFourthItemAction(player, packet);
			break;
		}
	}

	/**
	 * Handles first item action.
	 * 
	 * @param player
	 *            the player reference
	 * 
	 * @param packet
	 *            the packet
	 */
	public void handleFirstItemAction(Player player, Packet packet) {
		int interfaceIndex = packet.getUShortA();
		int itemSlot = packet.getUShortA();
		int itemIndex = packet.getUShortA();

		switch (interfaceIndex) {
		case Equipment.EQUIPMENT_INTERFACE:
			player.equipment().unequip(itemIndex, itemSlot);
			break;
		case Banking.BANK_INTERFACE:
			player.banking().withdraw(itemIndex, 1, itemSlot);
			break;
		case Banking.BANK_INVENTORY_INTERFACE:
			player.banking().deposit(itemIndex, 1, itemSlot);
			break;
		case Trading.TRADING_INVENTORY_INTERFACE:
			player.trading().trade(itemSlot, 1);
			break;
		case Trading.TRADING_LEFT_SIDE_INTERFACE:
			player.trading().remove(itemSlot, 1);
			break;
		}
	}

	/**
	 * Handles second item action.
	 * 
	 * @param player
	 *            the player reference
	 * 
	 * @param packet
	 *            the packet
	 */
	public void handleSecondItemAction(Player player, Packet packet) {
		int interfaceIndex = packet.getLEShortA();
		int itemIndex = packet.getLEShortA();
		int itemSlot = packet.getLEShort();

		switch (interfaceIndex) {
		case Banking.BANK_INTERFACE:
			player.banking().withdraw(itemIndex, 5, itemSlot);
			break;
		case Banking.BANK_INVENTORY_INTERFACE:
			player.banking().deposit(itemIndex, 5, itemSlot);
			break;
		case Trading.TRADING_INVENTORY_INTERFACE:
			player.trading().trade(itemSlot, 5);
			break;
		case Trading.TRADING_LEFT_SIDE_INTERFACE:
			player.trading().remove(itemSlot, 5);
			break;
		}
	}

	/**
	 * Handles third item action.
	 * 
	 * @param player
	 *            the player reference
	 * 
	 * @param packet
	 *            the packet
	 */
	public void handleThirdItemAction(Player player, Packet packet) {
		int interfaceIndex = packet.getULEShort();
		int itemIndex = packet.getUShortA();
		int itemSlot = packet.getUShortA();

		switch (interfaceIndex) {
		case Banking.BANK_INTERFACE:
			player.banking().withdraw(itemIndex, 10, itemSlot);
			break;
		case Banking.BANK_INVENTORY_INTERFACE:
			player.banking().deposit(itemIndex, 10, itemSlot);
			break;
		case Trading.TRADING_INVENTORY_INTERFACE:
			player.trading().trade(itemSlot, 10);
			break;
		case Trading.TRADING_LEFT_SIDE_INTERFACE:
			player.trading().remove(itemSlot, 10);
			break;
		}
	}

	/**
	 * Handles fourth item action.
	 * 
	 * @param player
	 *            the player reference
	 * 
	 * @param packet
	 *            the packet
	 */
	public void handleFourthItemAction(Player player, Packet packet) {
		int itemSlot = packet.getUShortA();
		int interfaceIndex = packet.getUShort();
		int itemIndex = packet.getUShortA();

		switch (interfaceIndex) {
		case Banking.BANK_INTERFACE:
			player.banking().withdraw(itemIndex, player.banking().items()[itemSlot].amount(), itemSlot);
			break;
		case Banking.BANK_INVENTORY_INTERFACE:
			player.banking().deposit(itemIndex, player.inventory().itemAmount(itemIndex), itemSlot);
			break;
		case Trading.TRADING_INVENTORY_INTERFACE:
			player.trading().trade(itemSlot, player.inventory().itemAmount(itemIndex));
			break;
		case Trading.TRADING_LEFT_SIDE_INTERFACE:
			player.trading().remove(itemSlot, player.trading().itemAmount(itemIndex));
		}
	}

	public static final int FIRST_ITEM_ACTION_OPCODE = 145;
	public static final int SECOND_ITEM_ACTION_OPCODE = 117;
	public static final int THIRD_ITEM_ACTION_OPCODE = 43;
	public static final int FOURTH_ITEM_ACTION_OPCODE = 129;

}