package org.lightrune.content;

import java.util.ArrayList;
import java.util.List;

import org.lightrune.player.Player;

/**
 * Private messaging manager.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class PrivateMessaging {

	private Player player;

	private List<String> friends = new ArrayList<String>();
	private List<String> ignores = new ArrayList<String>();

	/**
	 * Creates a new private messaging manager.
	 * 
	 * @param player
	 *            the player reference
	 */
	public PrivateMessaging(Player player) {
		this.player = player;
	}

	/**
	 * Gets the friends list.
	 * 
	 * @return the friends list
	 */
	public List<String> friends() {
		return friends;
	}

	/**
	 * Gets the ignores list.
	 * 
	 * @return the ignores list
	 */
	public List<String> ignores() {
		return ignores;
	}

	/**
	 * Gets the associated player.
	 * 
	 * @return the associated player
	 */
	public Player player() {
		return player;
	}

}