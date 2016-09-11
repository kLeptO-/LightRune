package org.lightrune.content.item;

import org.lightrune.player.Player;

/**
 * Item container implementation.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public abstract class ItemContainer {

	private Player player;
	private Item[] items;

	/**
	 * Creates a new item container for specified player.
	 * 
	 * @param player
	 *            the player reference
	 */
	public ItemContainer(Player player) {
		this.player = player;
	}

	/**
	 * Adds item to this item container.
	 * 
	 * @param item
	 *            the item object
	 * 
	 * @return amount of items that were not added or 0 if all items were added
	 */
	public int add(Item item) {
		return add(item.index(), item.amount(), true);
	}

	/**
	 * Adds item to this item container.
	 * 
	 * @param index
	 *            the item index
	 * 
	 * @param amount
	 *            the item amount
	 * 
	 * @return amount of items that were not added or 0 if all items were added
	 */
	public int add(int index, int amount) {
		return add(index, amount, true);
	}

	/**
	 * Adds item to this item container.
	 * 
	 * @param index
	 *            the item index
	 * 
	 * @param amount
	 *            the item amount
	 * 
	 * @param refresh
	 *            indicates if items should be sent to client
	 * 
	 * @return amount of items that were not added or 0 if all items were added
	 */
	public int add(int index, int amount, boolean refresh) {
		int notAdded = amount;
		if (ItemDefinition.get(index).stackable() || stack()) {
			int slot = itemSlot(index);
			if (slot == -1) {
				slot = emptySlot();
			}
			if (slot != -1) {
				long totalAmount = (long) items[slot].amount() + (long) amount;
				if (totalAmount > Integer.MAX_VALUE) {
					notAdded = (int) totalAmount - Integer.MAX_VALUE;
					totalAmount = Integer.MAX_VALUE;
					noSpace();
				} else {
					notAdded -= amount;
				}
				items[slot].index(index);
				items[slot].amount((int) totalAmount);
			} else {
				noSpace();
			}
		} else {
			for (; amount > 0; amount--) {
				int slot = emptySlot();
				if (slot != -1) {
					items[slot] = items[slot].index(index).amount(1);
					notAdded--;
				} else {
					noSpace();
					break;
				}
			}
		}
		if (refresh) {
			refreshItems();
		}
		return notAdded;
	}

	/**
	 * Deletes item from this item container.
	 * 
	 * @param item
	 *            the item object
	 * 
	 * @return amount of items that were not deleted or 0 if all items were
	 *         deleted
	 */
	public int delete(Item item) {
		return delete(item.index(), item.amount());
	}

	/**
	 * Deletes item to from item container.
	 * 
	 * @param index
	 *            the item index
	 * 
	 * @param amount
	 *            the item amount
	 * 
	 * @return amount of items that were not deleted or 0 if all items were
	 *         deleted
	 */
	public int delete(int index, int amount) {
		return delete(index, amount, true);
	}

	/**
	 * Deletes item to from item container.
	 * 
	 * @param index
	 *            the item index
	 * 
	 * @param amount
	 *            the item amount
	 * 
	 * @param refresh
	 *            indicates if items should be sent to client
	 * 
	 * @return amount of items that were not deleted or 0 if all items were
	 *         deleted
	 */
	public int delete(int index, int amount, boolean refresh) {
		int notDeleted = amount;
		if (ItemDefinition.get(index).stackable() || stack()) {
			int slot = itemSlot(index);
			if (slot != -1) {
				items[slot].amount(items[slot].amount() - amount);
				notDeleted -= items[slot].amount() - amount;
				if (items[slot].amount() < 1) {
					items[slot].index(-1);
					notDeleted = 0;
				}
			} else {
				return notDeleted;
			}
		} else {
			for (; amount > 0; amount--) {
				int slot = itemSlot(index);
				if (slot != -1) {
					items[slot].index(-1);
					items[slot].amount(0);
					notDeleted--;
				} else {
					break;
				}
			}
		}
		if (refresh) {
			refreshItems();
		}
		return notDeleted;
	}

	/**
	 * Sets item properties at the given slot.
	 * 
	 * @param slot
	 *            the item slot
	 * 
	 * @param item
	 *            the item
	 */
	public ItemContainer set(int slot, Item item) {
		return set(slot, item.index(), item.amount());
	}

	/**
	 * Sets item properties at the given slot.
	 * 
	 * @param slot
	 *            the item slot
	 * 
	 * @param index
	 *            the item index
	 * 
	 * @param amount
	 *            the item amount
	 */
	public ItemContainer set(int slot, int index, int amount) {
		items[slot].index(index).amount(amount);
		return this;
	}

	/**
	 * Gets free space of this container.
	 * 
	 * @return the free space
	 */
	public int freeSpace() {
		int totalSpace = 0;
		for (Item item : items) {
			if (item.index() == -1) {
				totalSpace++;
			}
		}
		return totalSpace;
	}

	/**
	 * Checks if item container is full.
	 * 
	 * @return true only if there are no available slots
	 */
	public boolean isFull() {
		return emptySlot() == -1;
	}

	/**
	 * Gets the empty item slot in the item container, returns -1 if item
	 * container is full.
	 * 
	 * @return the empty item slot
	 */
	public int emptySlot() {
		for (int i = 0; i < items.length; i++) {
			if (items[i].index() == -1) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Gets the item slot in the item container, returns -1 if item is not
	 * found.
	 * 
	 * @param itemIndex
	 *            the item index
	 * 
	 * @return the item slot
	 */
	public int itemSlot(int itemIndex) {
		for (int i = 0; i < capacity(); i++) {
			if (items[i].index() == itemIndex) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Gets the total item amount.
	 * 
	 * @param itemIndex
	 *            the item index
	 * 
	 * @return the total item amount of specified item in this container
	 */
	public int itemAmount(int itemIndex) {
		int totalAmount = 0;
		for (Item item : items) {
			if (item.index() == itemIndex) {
				totalAmount += item.amount();
			}
		}
		return totalAmount;
	}

	/**
	 * Resets items of this container.
	 */
	public ItemContainer resetItems() {
		items = new Item[capacity()];
		for (int i = 0; i < capacity(); i++) {
			items[i] = new Item(-1, 0);
		}
		return this;
	}

	/**
	 * Copies items from another container.
	 * 
	 * @param container
	 *            the other container
	 */
	public ItemContainer copy(ItemContainer container) {
		resetItems();
		int capacity = capacity() > container.capacity() ? container.capacity() : capacity();
		for (int i = 0; i < capacity; i++) {
			items[i].index(container.items()[i].index()).amount(container.items()[i].amount());
		}
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

	/**
	 * Gets the items array.
	 * 
	 * @return the items array
	 */
	public Item[] items() {
		return items;
	}

	/**
	 * Gets the container capacity.
	 * 
	 * @return the container capacity
	 */
	public abstract int capacity();

	/**
	 * Check if this container is stack, meaning that all same items goes into
	 * same stack.
	 * 
	 * @return true if this container is stack
	 */
	public abstract boolean stack();

	/**
	 * Refreshes items after certain action is done.
	 */
	public abstract ItemContainer refreshItems();

	/**
	 * Method that gets triggered whenever player is trying to add items to the
	 * container and there is no space available. Can be used to send chat
	 * message to the player or trigger other events.
	 */
	public abstract ItemContainer noSpace();

}