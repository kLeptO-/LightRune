package org.lightrune.world;

import java.util.Deque;
import java.util.LinkedList;

import org.lightrune.util.RS2Utils;
import org.lightrune.player.Player;

/**
 * Entity movement manager.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class MovementQueue {

	private CharacterEntity entity;
	private Deque<Location> movementSteps;

	private int walkingDirection = -1, runningDirection = -1;
	private boolean running = true;

	/**
	 * Creates a new movement manager for entity.
	 * 
	 * @param entity
	 *            the entity instance
	 * 
	 * @return a new movement manager
	 */
	public MovementQueue(CharacterEntity entity) {
		movementSteps = new LinkedList<Location>();
		entity(entity);
	}

	/**
	 * Adds movement destination to the queue.
	 * 
	 * @param destination
	 *            the movement destination   
	 */
	public MovementQueue queueDestination(Location destination) {
		Location lastStep = movementSteps.peekLast();
		int diffX = destination.x() - lastStep.x();
		int diffY = destination.y() - lastStep.y();
		int stepsAmount = Math.max(Math.abs(diffX), Math.abs(diffY));
		for (int i = 0; i < stepsAmount; i++) {
			if (diffX < 0) {
				diffX++;
			} else if (diffX > 0) { 
				diffX--;
			}
			if (diffY < 0) {
				diffY++;
			} else if (diffY > 0) {
				diffY--;
			}
			queueStep(destination.x() - diffX, destination.y() - diffY);
		}
		return this;
	}
	
	/**
	 * Adds movement point to the queue.
	 * 
	 * @param x
	 *            the x coordinate
	 * 
	 * @param y
	 *            the y coordinate
	 */
	public MovementQueue queueStep(int x, int y) {
		Location currentStep = movementSteps.peekLast();
		int diffX = x - currentStep.x();
		int diffY = y - currentStep.y();
		if (RS2Utils.getDirection(diffX, diffY) > -1) {
			movementSteps.add(new Location(x, y));
		}
		return this;
	}

	/**
	 * Processes entity movement.
	 */
	public void processMovement() {
		if (movementSteps.isEmpty()) {
			return;
		}
		walkingDirection(generateDirection());
		if (running() && !movementSteps.isEmpty()) {
			runningDirection(generateDirection());
		}
		if (entity instanceof Player) {
			int diffX = entity.location().x() - entity.currentRegion().regionX() * 8;
			int diffY = entity.location().y() - entity.currentRegion().regionY() * 8;
			boolean changed = diffX < 16 || diffX >= 88 || diffY < 16 || diffY >= 88;
			((Player) entity).updating().mapRegionChanging(changed);
		}
		return;
	}

	/**
	 * Generates next walking direction.
	 * 
	 * @return next walking direction for player updating
	 */
	public int generateDirection() {
		Location nextStep = movementSteps.poll();
		Location currentStep = entity.location();
		int diffX = nextStep.x() - currentStep.x();
		int diffY = nextStep.y() - currentStep.y();
		int direction = RS2Utils.getDirection(diffX, diffY);
		if (direction > -1) {
			entity.location().transform(
					RS2Utils.DIRECTION_DELTA_X[direction],
					RS2Utils.DIRECTION_DELTA_Y[direction]);
		}
		return direction;
	}

	/**
	 * Prepares the movement queue for new steps.
	 */
	public MovementQueue prepare() {
		walkingDirection(-1).runningDirection(-1);
		movementSteps.clear();
		movementSteps.add(entity.location());
		return this;
	}

	/**
	 * Finishes queue preparation.
	 */
	public MovementQueue finish() {
		movementSteps.removeFirst();
		return this;
	}

	/**
	 * Sets the associated entity with this movement manager.
	 * 
	 * @param entity
	 *            the associated entity
	 */
	public MovementQueue entity(CharacterEntity entity) {
		this.entity = entity;
		return this;
	}

	/**
	 * Gets the associated character
	 * 
	 * @return the associated character
	 */
	public CharacterEntity entity() {
		return entity;
	}

	/**
	 * Sets the walking direction.
	 * 
	 * @param direction
	 *            the direction
	 */
	public MovementQueue walkingDirection(int direction) {
		this.walkingDirection = direction;
		return this;
	}

	/**
	 * Gets the walking direction.
	 * 
	 * @return the walking direction
	 */
	public int walkingDirection() {
		return walkingDirection;
	}

	/**
	 * Sets the running direction.
	 * 
	 * @param direction
	 *            the direction
	 */
	public MovementQueue runningDirection(int direction) {
		this.runningDirection = direction;
		return this;
	}

	/**
	 * Gets the running direction.
	 * 
	 * @return the running direction
	 */
	public int runningDirection() {
		return runningDirection;
	}

	/**
	 * Sets the running flag.
	 * 
	 * @param running
	 *            the running flag
	 */
	public MovementQueue running(boolean running) {
		this.running = running;
		return this;
	}

	/**
	 * Gets the running flag.
	 * 
	 * @return the running flag
	 */
	public boolean running() {
		return running;
	}

}