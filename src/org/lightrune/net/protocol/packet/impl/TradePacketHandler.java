package org.lightrune.net.protocol.packet.impl;

import org.lightrune.net.protocol.packet.Packet;
import org.lightrune.net.protocol.packet.PacketHandler;
import org.lightrune.player.Player;

/**
 * Trade request packet handler.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class TradePacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		int playerIndex = packet.opcode() == TRADE_OPCODE ? packet.getUShort() : packet.getLEShort();
		player.trading().handleTrade(playerIndex);
	}

	public static final int TRADE_OPCODE = 128;
	public static final int CHATBOX_TRADE_OPCODE = 139;

}