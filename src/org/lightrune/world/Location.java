package org.lightrune.world;

/**
 * Represents coordinates of world entity.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class Location {

	private int x, y, z;

	/**
	 * Creates a new location with given x and y coordinates and default z
	 * coordinate.
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            they y coordinate
	 */
	public Location(int x, int y) {
		x(x).y(y).z(0);
	}

	/**
	 * Creates a new location with given coordinates.
	 * 
	 * @param x
	 *            the x coordinate
	 * 
	 * @param y
	 *            the y coordinate
	 * 
	 * @param z
	 *            the z coordinate
	 */
	public Location(int x, int y, int z) {
		x(x).y(y).z(z);
	}

	/**
	 * Sets the x coordinate.
	 * 
	 * @param x
	 *            the x coordinate
	 */
	public Location x(int x) {
		this.x = x;
		return this;
	}

	/**
	 * Gets the x coordinate.
	 * 
	 * @return the x coordinate
	 */
	public int x() {
		return x;
	}

	/**
	 * Sets the y coordinate.
	 * 
	 * @param y
	 *            the y coordinate
	 */
	public Location y(int y) {
		this.y = y;
		return this;
	}

	/**
	 * Gets the y coordinate.
	 * 
	 * @return the y coordinate
	 */
	public int y() {
		return y;
	}

	/**
	 * Sets the z coordinate.
	 * 
	 * @param z
	 *            the z coordinate
	 */
	public Location z(int z) {
		this.z = z;
		return this;
	}

	/**
	 * Gets the z coordinate.
	 * 
	 * @return the z coordinate
	 */
	public int z() {
		return z;
	}

	/**
	 * Gets the region coordinate of this location.
	 * 
	 * @return the region x coordinate
	 */
	public int regionX() {
		return (x >> 3) - 6;
	}

	/**
	 * Gets the region coordinate of this location.
	 * 
	 * @return the region y coordinate
	 */
	public int regionY() {
		return (y >> 3) - 6;
	}

	/**
	 * Gets the local coordinate of this location.
	 * 
	 * @return the local x coordinate
	 */
	public int localX() {
		return localX(this);
	}

	/**
	 * Gets the local coordinate of this location.
	 * 
	 * @return the local y coordinate
	 */
	public int localY() {
		return localY(this);
	}

	/**
	 * Gets the relative local coordinate to another region.
	 * 
	 * @return the local x coordinate
	 */
	public int localX(Location location) {
		return x - 8 * location.regionX();
	}

	/**
	 * Gets the relative local coordinate to another region.
	 * 
	 * @return the local y coordinate
	 */
	public int localY(Location location) {
		return y - 8 * location.regionY();
	}

	/**
	 * Transforms this location.
	 * 
	 * @param x
	 *            the x difference
	 * @param y
	 *            the y difference
	 * 
	 * @return the transformed location
	 */
	public Location transform(int x, int y) {
		return x(x() + x).y(y() + y);
	}

	/**
	 * Transforms this location.
	 * 
	 * @param x
	 *            the x difference
	 * @param y
	 *            the y difference
	 * @param z
	 *            the z difference
	 * 
	 * @return the transformed location
	 */
	public Location transform(int x, int y, int z) {
		return x(x() + x).y(y() + y).z(z() + z);
	}

	/**
	 * Gets the copy of this location.
	 * 
	 * @return the copy of this location
	 */
	public Location copy() {
		return new Location(x(), y(), z());
	}

	/**
	 * Checks if other location is within distance with this location.
	 * 
	 * @param location
	 *            the other location
	 */
	public boolean withinDistance(Location location) {
		if (z != location.z()) {
			return false;
		}
		int diffX = Math.abs(location.x() - x);
		int diffY = Math.abs(location.y() - y);
		return diffX < 15 && diffY < 15;
	}

	/**
	 * Checks if other location is within distance with this location.
	 * 
	 * @param location
	 *            the other location
	 * 
	 * @param distance
	 *            the distance
	 */
	public boolean withinDistance(Location location, int distance) {
		if (z != location.z()) {
			return false;
		}
		int diffX = Math.abs(location.x() - x);
		int diffY = Math.abs(location.y() - y);
		return diffX < distance && diffY < distance;
	}

	/**
	 * Checks if two locations represent the same spot in the world.
	 * 
	 * @param location
	 *            the other location
	 */
	public boolean sameAs(Location location) {
		return location.x() == x && location.y() == y && location.z() == z;
	}

}