package org.lightrune.net.protocol.packet.impl;

import org.lightrune.net.protocol.packet.Packet;
import org.lightrune.net.protocol.packet.PacketHandler;
import org.lightrune.player.Player;

/**
 * Button packet handler.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class ButtonPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		int buttonIndex = packet.getShort();

		switch (buttonIndex) {
		case 2458:
			/*
			 * Logout button.
			 */
			player.packetSender().sendLogout();
			break;
		case 8130:
			/*
			 * Banking switch items mode.
			 */
			player.banking().insertItems(false);
			break;
		case 8131:
			/*
			 * Banking insert items mode.
			 */
			player.banking().insertItems(true);
			break;
		case 5387:
			/*
			 * Banking withdraw items mode.
			 */
			player.banking().withdrawNote(false);
			break;
		case 5386:
			/*
			 * Banking withdraw note items mode.
			 */
			player.banking().withdrawNote(true);
			break;
		case 3420:
		case 3546:
			/*
			 * Accept trade button.
			 */
			player.trading().acceptTrade();
			break;
		default:
			System.out.println("Action Button: " + buttonIndex);
			break;
		}
	}

}