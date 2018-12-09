package faction.violation;

import java.util.ArrayList;

import actor.Actor;
import faction.violation.FactionRule.ViolationType;
import nomad.universe.Universe;
import shared.Vec2f;
import vmo.GameManager;

public class FactionListener {

	private FactionRules ruleset;
	private int cooldownClock;
	private FactionViolation currentViolation;
	private ArrayList<Actor> witnesses;

	public FactionListener(String zoneRules, ArrayList<Actor> actors) {
		// TODO Auto-generated constructor stub
		witnesses = new ArrayList<Actor>();
		ruleset = new FactionRules(zoneRules);
		cooldownClock = 0;

		for (int i = 0; i < actors.size(); i++) {
			if (ruleset.getWitness(actors.get(i).getName())) {
				if (actors.get(i).getRPGHandler().getActive()) {
					witnesses.add(actors.get(i));
				}
			}
		}

		if (ruleset.getFaction() != null) {
			currentViolation = new FactionViolation(ruleset.getFaction().getViolation(),
					Universe.getInstance().getPlayer().getPosition());
		}
	}

	public void update() {
		if (cooldownClock > 0) {
			cooldownClock--;
		}
	}

	private boolean setViolation(String targetname, Vec2f p, int violationLevel) {
		// check if the violation can be seen

		// if violation is regards a witness of course it can be seen
		if (ruleset.getWitness(targetname)) {
			currentViolation = new FactionViolation(violationLevel, p);

			cooldownClock = 10;
			return true;
		}
		// if not, check if witnesses can see the location
		for (int i = 0; i < witnesses.size(); i++) {
			if (witnesses.get(i).getRPGHandler().getActive()) {
				if (witnesses.get(i).getPosition().getDistance(p)<10 && GameManager.m_los.existsLineOfSight(Universe.getInstance().getCurrentZone().getBoard(0), (int) p.x, (int) p.y,
						(int) witnesses.get(i).getPosition().x, (int) witnesses.get(i).getPosition().y, true)) {
					currentViolation = new FactionViolation(violationLevel, p);
					cooldownClock = 10;
					return true;
				}
			}

		}
		return false;
	}

	public boolean checkViolation(String targetname, Vec2f p, ViolationType vtype) {
		if (cooldownClock == 0) {
			FactionRule rule = ruleset.violationLevel(targetname, vtype);
			if (rule != null) {
				if (currentViolation == null) {
					if (setViolation(targetname, p, rule.getViolationLevel())) {
						rule.violationAction();
					}
				} else {
					if (currentViolation.getViolationLevel() < rule.getViolationLevel()) {
						if (setViolation(targetname, p, rule.getViolationLevel())) {
							rule.violationAction();
						}
					}
				}
			}
		}
		return false;
	}

	public int getViolationLevel() {

		if (currentViolation != null) {
			return currentViolation.getViolationLevel();
		}
		return 0;
	}

	public Vec2f getViolationLocation() {

		if (currentViolation != null) {
			return currentViolation.getViolationPosition();
		}
		return null;
	}

	public void setViolation(int violationLevel, Vec2f position) {
		currentViolation = new FactionViolation(violationLevel, position);
		if (ruleset.getFaction() != null) {
			ruleset.getFaction().setViolation(violationLevel);
		}
	}

}
