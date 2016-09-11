package org.lightrune.net.protocol.packet.impl;

import org.lightrune.net.protocol.packet.Packet;
import org.lightrune.net.protocol.packet.PacketHandler;
import org.lightrune.player.Player;

/**
 * Close interface packet handler.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class CloseInterfacePacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		/**
		 * If player is in the trade, decline the trade.
		 */
		if (player.trading().isTrading()) {
			player.trading().declineTrade(false);
		}
	}

}