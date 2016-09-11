package org.lightrune.content.item.container;

import org.lightrune.content.item.Item;
import org.lightrune.content.item.ItemContainer;
import org.lightrune.content.item.ItemDefinition;
import org.lightrune.player.Player;

/**
 * RuneScape equipment container implementation.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class Equipment extends ItemContainer {

	/**
	 * Allocates a new equipment container for specified player().
	 * 
	 * @param player
	 *            the player instance
	 */
	public Equipment(Player player) {
		super(player);
		resetItems();
	}

	/**
	 * Equips the item from a given slot.
	 * 
	 * @param index
	 *            the item index
	 * 
	 * @param slot
	 *            the item slot
	 */
	public Equipment equip(int index, int slot) {
		/*
		 * Inventory item reference.
		 */
		Item item = player().inventory().items()[slot].copy();

		/*
		 * Check if this was a real packet.
		 */
		if (item.index() != index) {
			return this;
		}

		/*
		 * Get the equipment slot.
		 */
		int equipmentSlot = ItemDefinition.get(index).equipmentSlot();

		/*
		 * Check if we are trying to equip two handed weapon while wielding a
		 * shield.
		 */
		if (ItemDefinition.get(index).twoHanded()
				&& equipmentSlot == WEAPON_SLOT
				&& items()[SHIELD_SLOT].index() != -1) {
			if (player().inventory().freeSpace() == 0) {
				/*
				 * Check is we have enough space to unequip the shield.
				 */
				player().inventory().noSpace();
				return this;
			} else {
				/*
				 * Unequip the shield if weapon is two handed.
				 */
				player().inventory().add(items()[SHIELD_SLOT].index(), items()[SHIELD_SLOT].amount(), false);
				set(SHIELD_SLOT, -1, 0);
			}
		}

		/*
		 * Item to be added to the inventory.
		 */
		Item unequipItem = null;

		if (ItemDefinition.get(index).stackable() && items()[equipmentSlot].index() == index) {
			/*
			 * If we are trying to equip stackable item and we are already
			 * equipping the same item, let's add their total amount.
			 */
			long totalAmount = (long) item.amount() + (long) items()[equipmentSlot].amount();
			if (totalAmount > Integer.MAX_VALUE) {
				/*
				 * If total amount exceeds max stack value, let's only equip the
				 * amount to add up to the max stack and leave the rest in the
				 * inventory.
				 */
				item.amount(Integer.MAX_VALUE);
				unequipItem = items()[equipmentSlot].copy().amount((int) (totalAmount - Integer.MAX_VALUE));
			} else {
				/*
				 * Other-wise let's set equipping amount to the total amount of
				 * two items.
				 */
				item.amount((int) totalAmount);
			}
		} else {
			if (equipmentSlot == SHIELD_SLOT && ItemDefinition.get(items()[WEAPON_SLOT].index()).twoHanded()) {
				/*
				 * If we are wielding two handed weapon and trying to equip
				 * shield now, let's unequip two handed weapon.
				 */
				unequipItem = items()[WEAPON_SLOT].copy();
				set(WEAPON_SLOT, -1, 0);
			} else if (items()[equipmentSlot].index() != -1) {
				/*
				 * If we are wielding another item in the equipment slot, let's
				 * unequip it.
				 */
				unequipItem = items()[equipmentSlot].copy();
			}
		}

		/*
		 * Set the equipment item.
		 */
		set(equipmentSlot, item.index(), item.amount());

		/*
		 * Change inventory item with the item that forces unequip or if such
		 * item doesn't exists let's set the inventory item to nothing.
		 */
		if (unequipItem != null) {
			player().inventory().set(slot, unequipItem);
		} else {
			player().inventory().set(slot, -1, 0);
		}

		/*
		 * And finally send interface items to the client.
		 */
		player().inventory().refreshItems();
		refreshItems();
		return this;
	}

	/**
	 * Unequips the item from a given slot.
	 * 
	 * @param index
	 *            the item index
	 * 
	 * @param slot
	 *            the item slot
	 */
	public Equipment unequip(int index, int slot) {
		/*
		 * The Equipment item reference.
		 */
		Item item = items()[slot].copy();

		/*
		 * Check if this was a real packet.
		 */
		if (item.index() != index) {
			return this;
		}

		/*
		 * Check if item is stackable and if it is let's handle it in a
		 * different way.
		 */
		if (ItemDefinition.get(index).stackable()) {
			/*
			 * Getting slot of the same inventory item, if it exists.
			 */
			int otherItemSlot = player().inventory().itemSlot(index);

			/*
			 * Check if there is available space for stackable item.
			 */
			if (player().inventory().isFull() && otherItemSlot == -1) {
				player().inventory().noSpace();
				return this;
			}

			if (otherItemSlot != -1) {
				/*
				 * If we have the same item in the inventory, let's calculate
				 * their total amount.
				 */
				int otherItemAmount = player().inventory().items()[otherItemSlot].amount();
				long totalAmount = (long) item.amount() + (long) otherItemAmount;
				if (totalAmount > Integer.MAX_VALUE) {
					/*
					 * If total amount exceeds max stack value, let's only add
					 * up to the max stack and leave the rest.
					 */
					set(slot, index, item.amount() - (int) (Integer.MAX_VALUE - otherItemAmount));
					item.amount((int) (Integer.MAX_VALUE - otherItemAmount));
				} else {
					/*
					 * Delete the current equipment.
					 */
					set(slot, -1, 0);
				}
			} else {
				/*
				 * Delete the current equipment.
				 */
				set(slot, -1, 0);
			}

			/*
			 * Unequip the item.
			 */
			player().inventory().add(item.index(), item.amount(), false);
		} else {
			/*
			 * Check if there is space available.
			 */
			if (player().inventory().isFull()) {
				player().inventory().noSpace();
				return this;
			}

			/*
			 * Unequip the item.
			 */
			player().inventory().add(item.index(), item.amount(), false);
			set(slot, -1, 0);
		}

		/*
		 * Finally let's refresh the interfaces.
		 */
		player().inventory().refreshItems();
		refreshItems();
		return this;
	}

	@Override
	public int capacity() {
		return 14;
	}

	@Override
	public boolean stack() {
		return false;
	}

	@Override
	public Equipment refreshItems() {
		player().packetSender().sendItemContainer(this, EQUIPMENT_INTERFACE);
		player().updating().appearanceUpdateRequired(true);
		return this;
	}

	@Override
	public Equipment noSpace() {
		return this;
	}

	public static final int HAT_SLOT = 0;
	public static final int CAPE_SLOT = 1;
	public static final int AMULET_SLOT = 2;
	public static final int WEAPON_SLOT = 3;
	public static final int BODY_SLOT = 4;
	public static final int SHIELD_SLOT = 5;
	public static final int LEGS_SLOT = 7;
	public static final int HANDS_SLOT = 9;
	public static final int FEET_SLOT = 10;
	public static final int RING_SLOT = 12;
	public static final int AMMUNITION_SLOT = 13;

	public static final int EQUIPMENT_INTERFACE = 1688;

}