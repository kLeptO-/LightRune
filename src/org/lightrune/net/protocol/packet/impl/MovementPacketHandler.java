package org.lightrune.net.protocol.packet.impl;

import org.lightrune.net.protocol.packet.Packet;
import org.lightrune.net.protocol.packet.PacketHandler;
import org.lightrune.player.Player;
import org.lightrune.world.Location;
import org.lightrune.world.MovementQueue;

/**
 * Movement packet handler.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class MovementPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		if (packet.opcode() == 248) {
			packet.length(packet.length() - 14);
		}

		MovementQueue queue = player.movementQueue();
		queue.prepare();
		int steps = (packet.length() - 5) / 2;
		int[][] path = new int[steps][2];
		int firstStepX = packet.getLEShortA();
		for (int i = 0; i < steps; i++) {
			path[i][0] = packet.getByte();
			path[i][1] = packet.getByte();
		}
		int firstStepY = packet.getLEShort();
		queue.queueDestination(new Location(firstStepX, firstStepY));
		for (int i = 0; i < steps; i++) {
			path[i][0] += firstStepX;
			path[i][1] += firstStepY;
			queue.queueDestination(new Location(path[i][0], path[i][1]));
		}
		queue.finish();

		/*
		 * Reset the walk to action task.
		 */
		if (player.walkToAction() != null) {
			player.walkToAction().stop();
			player.walkToAction(null);
		}
	}

	public static final int COMMAND_MOVEMENT_OPCODE = 98;
	public static final int GAME_MOVEMENT_OPCODE = 164;
	public static final int MINIMAP_MOVEMENT_OPCODE = 248;

}