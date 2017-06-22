package spaceship;

import solarview.spaceEncounter.CombatController;

public interface ShipController {

	public void update(Spaceship Ship);

	public boolean canAct();

	public CombatController getCombat();
}
