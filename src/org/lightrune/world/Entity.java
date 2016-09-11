package org.lightrune.world;

/**
 * Represents single world entity.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class Entity {

	private Location location;
	private Location currentRegion;

	/**
	 * Sets the entity location
	 * 
	 * @param location
	 *            the world location
	 */
	public Entity location(Location location) {
		this.location = location;
		return this;
	}

	/**
	 * Gets the entity location.
	 * 
	 * @return the entity world location
	 */
	public Location location() {
		return location;
	}

	/**
	 * Sets the current region of this entity.
	 * 
	 * @param currentRegion
	 *            the current region location
	 */
	public Entity currentRegion(Location currentRegion) {
		this.currentRegion = currentRegion;
		return this;
	}

	/**
	 * Gets the current region of this entity.
	 * 
	 * @return the current region location
	 */
	public Location currentRegion() {
		return currentRegion;
	}

}