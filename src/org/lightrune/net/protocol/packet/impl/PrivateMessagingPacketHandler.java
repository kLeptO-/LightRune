package org.lightrune.net.protocol.packet.impl;

import org.lightrune.net.protocol.packet.Packet;
import org.lightrune.net.protocol.packet.PacketHandler;
import org.lightrune.player.Player;

/**
 * Private messaging packet handler.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class PrivateMessagingPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		@SuppressWarnings("unused")
		long username;

		switch (packet.opcode()) {
		/*
		 * Add friend packet.
		 */
		case ADD_FRIEND_OPCODE:
			username = packet.getLong();
			break;

		/*
		 * Add ignore packet.
		 */
		case ADD_IGNORE_OPCODE:
			username = packet.getLong();
			break;

		/*
		 * Remove friend packet.
		 */
		case REMOVE_FRIEND_OPCODE:
			username = packet.getLong();
			break;

		/*
		 * Remove ignore packet.
		 */
		case REMOVE_IGNORE_OPCODE:
			username = packet.getLong();
			break;
		}
	}

	public static final int ADD_FRIEND_OPCODE = 0;
	public static final int ADD_IGNORE_OPCODE = 1;
	public static final int REMOVE_FRIEND_OPCODE = 2;
	public static final int REMOVE_IGNORE_OPCODE = 3;

}