package org.lightrune.world;

import org.lightrune.player.Player;
import org.lightrune.world.item.GroundItems;

/**
 * World entities manager.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class World {

	private static Player[] players = new Player[2000];

	/**
	 * Registers a new player in the player list.
	 * 
	 * @param player
	 *            the player object
	 */
	public static void register(Player player) {
		int index = freePlayerIndex();
		players[index] = player.index(index);
	}

	/**
	 * Unregisters an existing player from the player list.
	 * 
	 * @param player
	 *            the player object
	 */
	public static void deregister(Player player) {
		for (int i = 1; i < players.length; i++) {
			if (players[i] == player) {
				if (players[i].utils() != null) {
					players[i].utils().resetLogout();
				}
				players[i] = null;
			}
		}
	}

	/**
	 * Gets the free player index.
	 * 
	 * @return the free player index or -1 if player list is full
	 */
	public static int freePlayerIndex() {
		int id = -1;
		for (int i = 1; i < players.length; i++) {
			if (players[i] == null) {
				id = i;
				break;
			}
		}
		return id;
	}

	/**
	 * Updates all world entities.
	 */
	public static void updateEntities() {
		/*
		 * Update players.
		 */
		for (Player player : players) {
			if (player == null) {
				continue;
			}
			player.movementQueue().processMovement();
		}
		for (Player player : players) {
			if (player == null) {
				continue;
			}
			player.updating().updateThisPlayer();
		}
		for (Player player : players) {
			if (player == null) {
				continue;
			}
			player.updating().resetUpdateFlags();
		}

		/*
		 * Update other entities.
		 */
		GroundItems.updateGroundItems();
	}

	/**
	 * Checks if player is in the player list.
	 * 
	 * @param player
	 *            the player object
	 */
	public static boolean containsPlayers(Player player) {
		for (Player p : players) {
			if (p == player) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the players list.
	 * 
	 * @return the array containing all connected players
	 */
	public static Player[] players() {
		return players;
	}

}