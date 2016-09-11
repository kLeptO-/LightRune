package org.lightrune.world;

/**
 * Represents single world character.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class CharacterEntity extends Entity {

	private MovementQueue movementQueue = new MovementQueue(this);

	/**
	 * Sets the associated movement queue.
	 * 
	 * @param movementQueue
	 *            the movement queue
	 */
	public CharacterEntity movementQueue(MovementQueue movementQueue) {
		this.movementQueue = movementQueue;
		return this;
	}

	/**
	 * Gets the associated movement queue.
	 * 
	 * @return the movement queue
	 */
	public MovementQueue movementQueue() {
		return movementQueue;
	}

}