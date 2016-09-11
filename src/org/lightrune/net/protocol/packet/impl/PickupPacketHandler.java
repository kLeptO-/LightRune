package org.lightrune.net.protocol.packet.impl;

import org.lightrune.net.protocol.packet.Packet;
import org.lightrune.net.protocol.packet.PacketHandler;
import org.lightrune.player.Player;
import org.lightrune.util.task.Task;
import org.lightrune.util.task.TaskScheduler;
import org.lightrune.world.Location;

/**
 * Pickup packet handler.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class PickupPacketHandler implements PacketHandler {

	@Override
	public void handle(final Player player, Packet packet) {
		final int itemY = packet.getLEShort();
		final int itemIndex = packet.getUShort();
		final int itemX = packet.getLEShort();

		final Location location = new Location(itemX, itemY, player.location().z());

		/*
		 * Post new walk to action task.
		 */
		player.walkToAction(new Task(true) {
			boolean arrived = false;

			@Override
			protected void execute() {
				if (arrived) {
					player.utils().pickupItem(location, itemIndex);
					player.walkToAction(null);
					stop();
				}
				if (player.location().sameAs(location)) {
					arrived = true;
				}
			}
		});
		TaskScheduler.get().schedule(player.walkToAction());
	}

}