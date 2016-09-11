package org.lightrune.player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.lightrune.content.item.Item;
import org.lightrune.world.Location;

/**
 * Player saving manager.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class PlayerSaving {

	/**
	 * Loads the player parameters.
	 * 
	 * @param player
	 *            the player object reference
	 * 
	 * @return false if password is incorrect or load was unsuccessful
	 */
	public static boolean load(Player player) {
		String folder = SAVES_FOLDER + player.username().toLowerCase().charAt(0) + "/";
		File save = new File(folder + player.username() + ".sav");
		if (!save.exists()) {
			save(player);
			return true;
		}
		try {
			String data = "", state = "";
			String[] args;
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(save)));
			while ((data = in.readLine()) != null) {
				/*
				 * Comment line.
				 */
				if (data.contains("//")) {
					continue;
				}

				/*
				 * Read current loading state.
				 */
				args = data.split("\t");
				if (args[0].length() > 0) {
					state = args[0];
				}

				/*
				 * Skip this line if it's just state indicator.
				 */
				if (args.length < 2) {
					continue;
				}

				if (state.equals("username")) {
					/*
					 * Username check.
					 */
					if (!args[1].toLowerCase().equals(player.username().toLowerCase())) {
						return false;
					}
				} else if (state.equals("password")) {
					/*
					 * Password check.
					 */
					if (!args[1].toLowerCase().equals(player.password().toLowerCase())) {
						return false;
					}
				} else if (state.equals("location")) {
					/*
					 * Location loading.
					 */
					int x = Integer.parseInt(args[1]);
					int y = Integer.parseInt(args[2]);
					int z = Integer.parseInt(args[3]);
					player.location(new Location(x, y, z));
				} else if (state.equals("rights")) {
					/*
					 * Player rights loading.
					 */
					player.rights(Integer.parseInt(args[1]));
				} else if (state.equals("inventory")) {
					/*
					 * Load the player inventory.
					 */
					int slot = Integer.parseInt(args[1]);
					int index = Integer.parseInt(args[2]);
					int amount = Integer.parseInt(args[3]);
					player.inventory().set(slot, new Item(index, amount));
				} else if (state.equals("equipment")) {
					/*
					 * Load the player equipment.
					 */
					int slot = Integer.parseInt(args[1]);
					int index = Integer.parseInt(args[2]);
					int amount = Integer.parseInt(args[3]);
					player.equipment().set(slot, new Item(index, amount));
				} else if (state.equals("bank")) {
					/*
					 * Load the player bank.
					 */
					int index = Integer.parseInt(args[1]);
					int amount = Integer.parseInt(args[2]);
					player.banking().add(index, amount, false);
				}
			}
			in.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Saves the current player parameters.
	 */
	public static void save(Player player) {
		String folder = SAVES_FOLDER + player.username().toLowerCase().charAt(0) + "/";
		File save = new File(folder + player.username() + ".sav");
		if (!save.exists()) {
			System.out.println("Creating player save file.");
			try {
				new File(folder).mkdir();
				save.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			PrintWriter out = new PrintWriter(save);
			out.println("username\t" + player.username());
			out.println("password\t" + player.password());
			out.println();
			out.println("location\t" + player.location().x() + "\t" + player.location().y() + "\t" + player.location().z());
			out.println("rights  \t" + player.rights());
			out.println();
			out.println("inventory");
			for (int i = 0; i < player.inventory().capacity(); i++) {
				Item item = player.inventory().items()[i];
				if (item.index() != -1) {
					String index = item.index() + "";
					if (item.index() < 10) {
						index = "000" + item.index();
					} else if (item.index() < 100) {
						index = "00" + item.index();
					} else if (item.index() < 1000) {
						index = "0" + item.index();
					}
					out.println("\t" + i + "\t" + index + "\t" + item.amount());
				}
			}
			out.println();
			out.println("equipment");
			for (int i = 0; i < player.equipment().capacity(); i++) {
				Item item = player.equipment().items()[i];
				if (item.index() != -1) {
					String index = item.index() + "";
					if (item.index() < 10) {
						index = "000" + item.index();
					} else if (item.index() < 100) {
						index = "00" + item.index();
					} else if (item.index() < 1000) {
						index = "0" + item.index();
					}
					out.println("\t" + i + "\t" + index + "\t" + item.amount());
				}
			}
			out.println();
			out.println("bank");
			for (Item item : player.banking().items()) {
				if (item.index() != -1) {
					String index = item.index() + "";
					if (item.index() < 10) {
						index = "000" + item.index();
					} else if (item.index() < 100) {
						index = "00" + item.index();
					} else if (item.index() < 1000) {
						index = "0" + item.index();
					}
					out.println("\t" + index + "\t" + item.amount());
				}
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static final String SAVES_FOLDER = "./data/saves/";

}