package org.lightrune.net.protocol.packet.impl;

import org.lightrune.net.protocol.packet.Packet;
import org.lightrune.net.protocol.packet.PacketHandler;
import org.lightrune.player.Player;
import org.lightrune.util.RS2Utils;

/**
 * Chat packet handler.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class ChatPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		int effects = packet.getUByteS();
		int color = packet.getUByteS();
		int length = packet.length() - 2;
		byte[] text = packet.getBytesA(length);
		player.updating().chatTextEffects(effects).chatTextColor(color).chatText(text);
		player.updating().chatUpdateRequired(true);
		System.out.println(player.username() + ": " + RS2Utils.unpackRSText(text, length));
	}

}