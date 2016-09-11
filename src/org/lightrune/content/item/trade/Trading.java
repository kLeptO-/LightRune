package org.lightrune.content.item.trade;

import org.lightrune.content.item.Item;
import org.lightrune.content.item.ItemContainer;
import org.lightrune.content.item.ItemDefinition;
import org.lightrune.content.item.container.Inventory;
import org.lightrune.player.Player;
import org.lightrune.world.World;

/**
 * RuneScape player trading support implementation.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class Trading extends ItemContainer {

	private int trader = 0, request = 0, traderRequest = 0;

	private ItemContainer inventoryContainer;

	private TradingState state = TradingState.WAITING_REQUEST;

	/**
	 * Creates a new trading manager for player.
	 * 
	 * @param player
	 *            the player reference
	 */
	public Trading(Player player) {
		super(player);
		inventoryContainer = new Inventory(player);
		resetItems();
	}

	/**
	 * Handles the trade request.
	 * 
	 * @param playerIndex
	 *            the other player index
	 */
	public Trading handleTrade(int playerIndex) {
		/*
		 * Check if such player exists.
		 */
		if (World.players()[playerIndex] == null) {
			return this;
		}

		/*
		 * Get other player reference.
		 */
		Player otherPlayer = World.players()[playerIndex];

		/*
		 * Check if other player already requested the trade.
		 */
		if (traderRequest == playerIndex && otherPlayer.trading().request == player().index()) {
			acceptTradeRequest(playerIndex);
			otherPlayer.trading().acceptTradeRequest(player().index());
		} else {
			/*
			 * Else send the trade request to him.
			 */
			request = playerIndex;
			otherPlayer.trading().requestTrade(player());
			player().packetSender().sendMessage("Sending trade offer....");
		}

		return this;
	}

	/**
	 * Accepts the latest trade request.
	 */
	public Trading acceptTradeRequest(int playerIndex) {
		/*
		 * Reset trade options.
		 */
		traderRequest = request = 0;

		/*
		 * Check if player is still online.
		 */
		if (World.players()[playerIndex] == null) {
			return this;
		}

		/*
		 * Prepare the trade.
		 */
		trader = playerIndex;
		state = TradingState.MANAGING_TRADE;
		inventoryContainer().copy(player().inventory());
		resetItems();
		
		/*
		 * And open the trade interface.
		 */
		player().packetSender().sendInterfaceSet(3323, 3321)
				.sendString(3417, "Trading With: " + World.players()[playerIndex].username())
				.sendString(3431, "");
		refreshItems();
		return this;
	}

	/**
	 * Declines current trade.
	 * 
	 * @param byOther
	 *            the indicator that is declined by other player
	 */
	public Trading declineTrade(boolean byOther) {
		Player otherPlayer = World.players()[trader];

		/*
		 * If trade is declined by this player, let's decline trade for other
		 * player too.
		 */
		if (!byOther && otherPlayer != null) {
			otherPlayer.trading().declineTrade(true);
		}

		/*
		 * Reset the trade.
		 */
		player().packetSender().sendCloseInterface()
				.sendMessage(byOther ?
						"Other player has declined the trade." :
						"You have declined the trade.");
		trader = request = traderRequest = 0;
		state = TradingState.WAITING_REQUEST;
		return this;
	}

	/**
	 * Requests trade by a given player.
	 * 
	 * @param otherPlayer
	 *            the player reference.
	 */
	public Trading requestTrade(Player otherPlayer) {
		traderRequest = otherPlayer.index();
		player().packetSender().sendMessage(otherPlayer.username() + ":tradereq:");
		return this;
	}

	/**
	 * Accepts the trade stage.
	 */
	public Trading acceptTrade() {
		Player otherPlayer = World.players()[trader];

		/*
		 * Check if other player is still online.
		 */
		if (otherPlayer == null) {
			declineTrade(true);
		}

		/*
		 * If we are still managing the trade.
		 */
		if (state == TradingState.MANAGING_TRADE) {
			/*
			 * Check if you have enough space to receive trade items.
			 */
			if (!checkSpace()) {
				player().packetSender().sendMessage("Other player does not have enough space to accept this trade.");
				return this;
			}

			/*
			 * Check if other player has already accepted.
			 */
			if (otherPlayer.trading().state() == TradingState.WAITING_ACCEPT) {
				/*
				 * Open the confirmation screen.
				 */
				state = TradingState.WAITING_CONFIRM;
				confirmScreen();
				otherPlayer.trading().state = TradingState.WAITING_CONFIRM;
				otherPlayer.trading().confirmScreen();
				return this;
			} else {
				/*
				 * If other player haven't accepted yet, let's wait for his
				 * accept.
				 */
				state = TradingState.WAITING_ACCEPT;
				player().packetSender().sendString(3431, "Waiting for other player...");
				otherPlayer.packetSender().sendString(3431, "Other player has accepted.");
				return this;
			}
		}

		/*
		 * If we are in confirmation screen.
		 */
		if (state == TradingState.WAITING_CONFIRM) {
			/*
			 * Check if other player has already accepted.
			 */
			if (otherPlayer.trading().state() == TradingState.CONFIRMED) {
				/*
				 * Finish the trade.
				 */
				finishTrade();
				otherPlayer.trading().finishTrade();
				resetContainers();
				otherPlayer.trading().resetContainers();
				return this;
			} else {
				/*
				 * If other player haven't accepted yet, let's wait for his
				 * accept.
				 */
				state = TradingState.CONFIRMED;
				player().packetSender().sendString(3535, "Waiting for other player...");
				otherPlayer.packetSender().sendString(3535, "Other player has accepted.");
				return this;
			}
		}
		return this;
	}

	/**
	 * Switches back to managing state after managing trade.
	 */
	public Trading manageTrade() {
		Player otherPlayer = World.players()[trader];

		/*
		 * Check if other player is still online.
		 */
		if (otherPlayer == null) {
			declineTrade(true);
		}

		/*
		 * Switch back to managing state.
		 */
		state = TradingState.MANAGING_TRADE;
		otherPlayer.trading().state = TradingState.MANAGING_TRADE;
		player().packetSender().sendString(3431, "");
		otherPlayer.packetSender().sendString(3431, "");
		return this;
	}

	/**
	 * Opens up confirmation screen.
	 */
	public Trading confirmScreen() {
		Player otherPlayer = World.players()[trader];

		/*
		 * Check if other player is still online.
		 */
		if (otherPlayer == null) {
			declineTrade(true);
		}

		/*
		 * Build the interface strings.
		 */
		String offer = "", traderOffer = "";
		boolean first = true;
		for (Item item : items()) {
			if (item.index() != -1) {
				if (first) {
					offer = formatOffer(item);
					first = false;
				} else {
					offer = offer + "\\n" + formatOffer(item);
				}
			}
		}
		if (offer.length() == 0) {
			offer = "Absolutely nothing!";
		}
		first = true;
		for (Item item : otherPlayer.trading().items()) {
			if (item.index() != -1) {
				if (first) {
					traderOffer = formatOffer(item);
					first = false;
				} else {
					traderOffer = traderOffer + "\\n" + formatOffer(item);
				}
			}
		}
		if (traderOffer.length() == 0) {
			traderOffer = "Absolutely nothing!";
		}
		player().packetSender().sendInterfaceSet(3443, 3213)
				.sendString(3557, offer)
				.sendString(3558, traderOffer)
				.sendString(3535, "");
		return this;
	}

	/**
	 * Formats offer string.
	 * 
	 * @param item
	 *            the offer item
	 * 
	 * @return the formatted offer string
	 */
	public String formatOffer(Item item) {
		ItemDefinition def = ItemDefinition.get(item.index());
		String amount = "";
		if (def.stackable() && item.amount() > 1) {
			if (item.amount() > 999999) {
				amount = " x @gre@" + (item.amount() / 1000000) + "M @whi@(" + item.amount() + ")";
			} else if (item.amount() > 99999) {
				amount = " x @cya@" + (item.amount() / 100000) + "K @whi@(" + item.amount() + ")";
			} else {
				amount = " x " + item.amount();
			}
		}
		return "@or1@" + def.name() + "@whi@" + amount;
	}

	/**
	 * Finishes the trade.
	 */
	public Trading finishTrade() {
		Player otherPlayer = World.players()[trader];

		/*
		 * Check if other player is still online.
		 */
		if (otherPlayer == null) {
			declineTrade(true);
		}

		/*
		 * Let's remove our offer from the inventory.
		 */
		for (Item item : items()) {
			if (item.index() != -1) {
				player().inventory().delete(item.index(), item.amount(), false);
			}
		}

		/*
		 * Let's add trader's offer to our inventory.
		 */
		for (Item item : otherPlayer.trading().items()) {
			if (item.index() != -1) {
				player().inventory().add(item.index(), item.amount(), false);
			}
		}

		/*
		 * Let's reset the trade.
		 */
		player().packetSender().sendCloseInterface().sendMessage("Accepted trade.");
		player().inventory().refreshItems();
		trader = request = traderRequest = 0;
		state = TradingState.WAITING_REQUEST;
		return this;
	}

	/**
	 * Trades an item from a given slot.
	 * 
	 * @param slot
	 *            the item slot
	 * 
	 * @param amount
	 *            the item amount
	 */
	public Trading trade(int slot, int amount) {
		/*
		 * Check if item exists.
		 */
		Item item = inventoryContainer().items()[slot];
		if (item.index() == -1) {
			return this;
		}

		/*
		 * Trade the item.
		 */
		amount = item.amount() < amount ? item.amount() : amount;
		add(item.index(), amount, false);
		inventoryContainer().delete(item.index(), amount, false);
		refreshItems().manageTrade();
		return this;
	}

	/**
	 * Removes an item from the trade.
	 * 
	 * @param slot
	 *            the item slot
	 * 
	 * @param amount
	 *            the item amount
	 */
	public Trading remove(int slot, int amount) {
		/*
		 * Check if item exists.
		 */
		Item item = items()[slot];
		if (item.index() == -1) {
			return this;
		}

		/*
		 * Remove the item.
		 */
		inventoryContainer().add(item.index(), item.amount(), false);
		set(slot, -1, 0);
		refreshItems().manageTrade();
		return this;
	}

	/**
	 * Resets the trading containers.
	 */
	public Trading resetContainers() {
		resetItems();
		inventoryContainer().resetItems();
		return this;
	}

	/**
	 * Checks if player is currently in the trade.
	 * 
	 * @return true if player is trading
	 */
	public boolean isTrading() {
		return state != TradingState.WAITING_REQUEST;
	}

	/**
	 * Checks if other player has enough space to accept this trade.
	 * 
	 * @return true only if other player has enough space
	 */
	public boolean checkSpace() {
		Player otherPlayer = World.players()[trader];

		/*
		 * Check if other player is still online.
		 */
		if (otherPlayer == null) {
			declineTrade(true);
			return false;
		}

		/*
		 * Check the required space.
		 */
		int requiredSpace = 0;
		for (Item item : items()) {
			if (item.index() == -1) {
				continue;
			}
			if (ItemDefinition.get(item.index()).stackable()) {
				int slot = otherPlayer.trading().inventoryContainer().itemSlot(item.index());
				if (slot == -1) {
					requiredSpace++;
				} else {
					if (((long) item.amount()
					+ (long) otherPlayer.trading().inventoryContainer().items()[slot].amount())
					> Integer.MAX_VALUE) {
						return false;
					}
				}
			} else {
				requiredSpace++;
			}
		}
		return otherPlayer.trading().inventoryContainer().freeSpace() >= requiredSpace;
	}

	/**
	 * Gets the trader index.
	 * 
	 * @return the other player index
	 */
	public int trader() {
		return trader;
	}

	/**
	 * Gets the inventory container.
	 * 
	 * @return the inventory items container
	 */
	public ItemContainer inventoryContainer() {
		return inventoryContainer;
	}

	/**
	 * Gets the current trading state.
	 * 
	 * @return the trading state
	 */
	public TradingState state() {
		return state;
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
	public Trading refreshItems() {
		/*
		 * Send the trading inventory.
		 */
		player().packetSender().sendItemContainer(inventoryContainer(), TRADING_INVENTORY_INTERFACE);

		/*
		 * Send your trade offer.
		 */
		player().packetSender().sendItemContainer(this, TRADING_LEFT_SIDE_INTERFACE);

		/*
		 * Send the other player's offer only if player is still online.
		 */
		Player otherPlayer = World.players()[player().trading().trader()];
		if (otherPlayer != null) {
			otherPlayer.packetSender().sendItemContainer(this, TRADING_RIGHT_SIDE_INTERFACE);
		}
		return this;
	}

	@Override
	public Trading noSpace() {
		return this;
	}

	public static final int TRADING_INVENTORY_INTERFACE = 3322;
	public static final int TRADING_LEFT_SIDE_INTERFACE = 3415;
	public static final int TRADING_RIGHT_SIDE_INTERFACE = 3416;

}