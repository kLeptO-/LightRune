package org.lightrune.player;

import org.lightrune.content.item.container.Banking;
import org.lightrune.content.item.container.Equipment;
import org.lightrune.content.item.container.Inventory;
import org.lightrune.content.item.trade.Trading;
import org.lightrune.net.protocol.RSChannelContext;
import org.lightrune.util.task.Task;
import org.lightrune.world.CharacterEntity;
import org.lightrune.world.Location;

/**
 * Represents a human controlled character.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class Player extends CharacterEntity {

	private int index;
	private String username, password;
	private int rights;

	private RSChannelContext channelContext;
	private PacketSender packetSender = new PacketSender(this);
	private PlayerUpdating updating = new PlayerUpdating(this);
	private PlayerUtils utils = new PlayerUtils(this);

	private Inventory inventory = new Inventory(this);
	private Equipment equipment = new Equipment(this);
	private Banking banking = new Banking(this);
	private Trading trading = new Trading(this);

	private Task walkToAction;

	/**
	 * Creates a new player with given username and password.
	 * 
	 * @param username
	 *            the player name
	 * @param password
	 *            the player password
	 * 
	 * @param channelContext
	 *            the associated channel context
	 */
	public Player(String username, String password, RSChannelContext channelContext) {
		channelContext(channelContext);
		username(username).password(password).location(new Location(3222, 3222));
	}

	/**
	 * Sets the player index.
	 * 
	 * @param index
	 *            the player index
	 */
	public Player index(int index) {
		this.index = index;
		return this;
	}

	/**
	 * Gets the player index.
	 * 
	 * @return the player index
	 */
	public int index() {
		return index;
	}

	/**
	 * Sets the player name.
	 * 
	 * @param username
	 *            the player name
	 */
	public Player username(String username) {
		this.username = username;
		return this;
	}

	/**
	 * Gets the player name.
	 * 
	 * @return the player name
	 */
	public String username() {
		return username;
	}

	/**
	 * Sets the player password.
	 * 
	 * @param password
	 *            the player password
	 */
	public Player password(String password) {
		this.password = password;
		return this;
	}

	/**
	 * Gets the player password.
	 * 
	 * @return the player password
	 */
	public String password() {
		return password;
	}

	/**
	 * Sets the player rights.
	 * 
	 * @param rights
	 *            the player rights
	 */
	public Player rights(int rights) {
		this.rights = rights;
		return this;
	}

	/**
	 * Gets the player rights.
	 * 
	 * @return the player rights
	 */
	public int rights() {
		return rights;
	}

	/**
	 * Sets the associated channel context.
	 * 
	 * @param channelContext
	 *            the socket channel context
	 */
	public Player channelContext(RSChannelContext channelContext) {
		this.channelContext = channelContext;
		return this;
	}

	/**
	 * Gets the associated socket channel context.
	 * 
	 * @return the associated channel context
	 */
	public RSChannelContext channelContext() {
		return channelContext;
	}

	/**
	 * Gets the associated packet sender.
	 * 
	 * @return the packet sender for this player
	 */
	public PacketSender packetSender() {
		return packetSender;
	}

	/**
	 * Gets the associated player updating manager.
	 * 
	 * @return the player updating manager
	 */
	public PlayerUpdating updating() {
		return updating;
	}

	/**
	 * Gets the associated player utilities.
	 * 
	 * @return the associated player utilities
	 */
	public PlayerUtils utils() {
		return utils;
	}

	/**
	 * Gets the associated inventory manager.
	 * 
	 * @return the inventory manager
	 */
	public Inventory inventory() {
		return inventory;
	}

	/**
	 * Gets the associated equipment manager.
	 * 
	 * @return the equipment manager
	 */
	public Equipment equipment() {
		return equipment;
	}

	/**
	 * Gets the associated banking manager.
	 * 
	 * @return the banking manager
	 */
	public Banking banking() {
		return banking;
	}

	/**
	 * Gets the associated trading manager.
	 * 
	 * @return the trading manager
	 */
	public Trading trading() {
		return trading;
	}

	/**
	 * Sets the walk to action task.
	 * 
	 * @param walkToAction
	 *            the walk to action task
	 */
	public Player walkToAction(Task walkToAction) {
		this.walkToAction = walkToAction;
		return this;
	}

	/**
	 * Gets the walk to action task.
	 * 
	 * @return the walk to action task
	 */
	public Task walkToAction() {
		return walkToAction;
	}

}