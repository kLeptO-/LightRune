package org.lightrune.net.protocol.packet;

import org.lightrune.player.Player;

/**
 * Packet handler interface.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public interface PacketHandler {

	/**
	 * Handles an incoming packet.
	 * 
	 * @param player
	 *            the player reference
	 * 
	 * @param packet
	 *            the packet
	 */
	public void handle(Player player, Packet packet);

}