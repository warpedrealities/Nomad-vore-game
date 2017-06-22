package faction.violation;

import nomad.Universe;
import shared.Vec2f;

public class FactionViolation {

	private Vec2f violationPosition;
	private int violationLevel;

	public FactionViolation(int level, Vec2f position) {
		if (violationPosition != null) {
			violationPosition = new Vec2f(position.x, position.y);
		}

		violationLevel = level;
	}

	public Vec2f getViolationPosition() {
		if (violationPosition == null) {
			violationPosition = new Vec2f(Universe.getInstance().getPlayer().getPosition().x,
					Universe.getInstance().getPlayer().getPosition().y);
		}
		return violationPosition;

	}

	public int getViolationLevel() {

		return violationLevel;
	}

}
