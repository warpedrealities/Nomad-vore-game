package solarview.spaceEncounter.EncounterEntities;

import shared.Vec2f;
import spaceship.SpaceshipResource;

public class CombatManouver {

	private EncounterShip ship;
	private float speed;
	private float turn;
	private Vec2f position;
	private float heading;
	private byte course;

	public final static char stop = 0;
	public final static char left = 1;
	public final static char right = 2;
	public final static char half = 4;
	public final static char full = 8;

	public CombatManouver(EncounterShip ship, Vec2f position, int heading) {
		this.heading = heading;
		this.ship = ship;
		this.position = new Vec2f(position.x, position.y);
		genStats();
	}

	private void genStats() {
		speed = 100 / ((float) ship.getShip().getShipStats().getMoveCost());
		turn = 8 + ship.getShip().getShipStats().getManouverability();
		if (turn < 1) {
			turn = 1;
		}
		turn = turn / 16;
	}

	public float getSpeed() {
		return speed;
	}

	public float getTurn() {
		return turn;
	}

	public Vec2f getPosition() {
		return position;
	}

	public float getHeading() {
		return heading;
	}

	public byte getCourse() {
		return course;
	}

	public void setCourse(byte course) {
		this.course = course;
	}

	public void update(float dt) {
		doMove(dt);
		doTurn(dt);
		ship.getSprite().setFacing(heading);
		useFuel(dt);
	}

	public Vec2f lead(float dt)
	{
		Vec2f vector = getVector(dt);
		return new Vec2f(position.x+vector.x,position.y+vector.y);
	}
	
	private Vec2f getVector(float dt)
	{
		Vec2f vector = new Vec2f(0, 0);
		if ((course & half) != 0) {
			vector.y = (speed / 2) * dt;
		}
		if ((course & full) != 0) {
			vector.y = speed * dt;
		}
		vector.rotate(((float) heading) * 0.785398F);	
		return vector;
	}
	
	private void doMove(float dt) {
	
		Vec2f vector=getVector(dt);

		position.x += vector.x;
		position.y += vector.y;
		ship.getSprite().repositionF(position);
	}

	private void doTurn(float dt) {
		boolean full = false;
		float r = turn * dt;
		if ((course & half) > 0) {
			r += turn * dt;
		}
		if ((course & left) > 0) {
			heading -= r;

		}
		if ((course & right) > 0) {
			heading += r;

		}
	}

	private void useFuel(float dt) {
		float fUse = 0;
		float f = ship.getShip().getShipStats().getFuelEfficiency() / 20;
		if ((course & half) != 0) {
			fUse = dt * f / 2;
		}
		if ((course & full) != 0) {
			fUse = dt * f;
		}
		if (fUse > 0) {
			SpaceshipResource fuel = ship.getShip().getShipStats().getResource("FUEL");
			if (fuel.getResourceAmount() < fUse) {
				if ((course & half) != 0) {
					course -= half;
				} else {
					course -= full;
				}
			}
			fuel.setResourceAmount(fuel.getResourceAmount() - fUse);
		}

	}
}
