package org.lightrune.net.protocol.packet.impl;

import org.lightrune.net.protocol.packet.Packet;
import org.lightrune.net.protocol.packet.PacketHandler;
import org.lightrune.player.Player;
import org.lightrune.world.item.GroundItems;

/**
 * Region load packet handler.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class RegionPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		/*
		 * Refresh the ground items.
		 */
		GroundItems.sendRemoveGroundItems(player);
		GroundItems.sendGroundItems(player);
	}

}