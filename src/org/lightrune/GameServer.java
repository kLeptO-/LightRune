package org.lightrune;

import java.io.IOException;

import org.lightrune.content.item.ItemDefinition;
import org.lightrune.net.NIOSelector;
import org.lightrune.net.NIOServer;
import org.lightrune.util.task.Task;
import org.lightrune.util.task.TaskScheduler;
import org.lightrune.world.World;

import static org.lightrune.net.NIOServer.SERVER_PORT;

/**
 * Game server launcher.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class GameServer {

	/**
	 * Launches the game server.
	 * 
	 * @param args
	 *            the program arguments
	 */
	public static void main(String[] args) {
		/*
		 * Initializing required classes.
		 */
		ItemDefinition.loadDefinitions();

		/*
		 * Binding the server.
		 */
		try {
			NIOServer.bind(SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * Starting the game tick.
		 */
		TaskScheduler.get().schedule(new Task() {
			@Override
			protected void execute() {
				NIOSelector.select();
				World.updateEntities();
			}
		});
	}

}