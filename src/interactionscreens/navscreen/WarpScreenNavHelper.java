package interactionscreens.navscreen;

import nomad.universe.Universe;
import solarview.SolarScene;
import spaceship.Spaceship;
import spaceship.stats.SpaceshipAnalyzer;
import spaceship.stats.SpaceshipStats;
import vmo.Game;

public class WarpScreenNavHelper {

	public static void takeControl(Spaceship ship) {
		SpaceshipStats shipStats = new SpaceshipAnalyzer().generateStats(ship);
		Universe.getInstance().setShipName(ship.getName());
		ship.setShipStats(shipStats);
		// spaceship.setShipState(ShipState.SPACE);
		Game.sceneManager.SwapScene(new SolarScene(0, ship));
		return;
	}

}
