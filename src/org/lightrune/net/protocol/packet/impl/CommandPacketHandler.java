package org.lightrune.net.protocol.packet.impl;

import org.lightrune.net.protocol.packet.Packet;
import org.lightrune.net.protocol.packet.PacketBuilder;
import org.lightrune.net.protocol.packet.PacketHandler;
import org.lightrune.player.Player;
import org.lightrune.util.RS2Utils;
import org.lightrune.world.item.GroundItem;

/**
 * Command packet handler.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class CommandPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		String command = RS2Utils.getRSString(packet.buffer());
		String[] parts = command.toLowerCase().split(" ");

		if (parts[0].equals("removegrounditem") && parts.length > 2) {
			int itemIndex = Integer.parseInt(parts[1]);
			int itemAmount = Integer.parseInt(parts[2]);
			GroundItem item = new GroundItem(itemIndex, itemAmount, player);
			player.packetSender().sendRemoveGroundItem(item);
		}

		if (parts[0].equals("bank")) {
			player.banking().openBank();
		}

		if (parts[0].equals("ss") && parts.length > 2) {
			int length = parts[0].length() + parts[1].length() + 1;
			player.packetSender().sendMessage("Sending string: " + parts[1] + " " + command.substring(length));
			player.packetSender().sendString(Integer.parseInt(parts[1]), command.substring(length));
		}

		if (parts[0].equals("item") && parts.length > 2) {
			int itemIndex = Integer.parseInt(parts[1]);
			int itemAmount = Integer.parseInt(parts[2]);
			player.inventory().add(itemIndex, itemAmount);
		}

		if (parts[0].equals("allstar")) {
			for (int i = 0; i < 28; i++) {
				player.inventory().add(1038 + i, 1);
			}
		}

		if (parts[0].equals("config") && parts.length > 2) {
			int config = Integer.parseInt(parts[1]);
			int state = Integer.parseInt(parts[2]);
			PacketBuilder out = PacketBuilder.allocate(4);
			out.createFrame(36, player.channelContext().encryption());
			out.putLEShort(config);
			out.putByte(state);
			out.sendTo(player.channelContext().channel());
		}

		if (parts[0].equals("empty")) {
			player.inventory().resetItems().refreshItems();
		}
	}

}
