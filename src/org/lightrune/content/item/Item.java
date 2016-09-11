package org.lightrune.content.item;

/**
 * Represents a single item.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class Item {

	private int index, amount;

	/**
	 * Creates a new item with given data.
	 * 
	 * @param index
	 *            the item index
	 * 
	 * @param amount
	 *            the item amount
	 */
	public Item(int index, int amount) {
		index(index).amount(amount);
	}

	/**
	 * Sets the item index.
	 * 
	 * @param index
	 *            the item index
	 */
	public Item index(int index) {
		this.index = index;
		return this;
	}

	/**
	 * Gets the item index.
	 * 
	 * @return the item index
	 */
	public int index() {
		return index;
	}

	/**
	 * Sets the item amount.
	 * 
	 * @param amount
	 *            the item amount
	 */
	public Item amount(int amount) {
		this.amount = amount;
		return this;
	}

	/**
	 * Gets the item amount.
	 * 
	 * @return the item amount
	 */
	public int amount() {
		return amount;
	}

	/**
	 * Gets the copy of this item.
	 * 
	 * @return the item object clone
	 */
	public Item copy() {
		return new Item(index, amount);
	}

}