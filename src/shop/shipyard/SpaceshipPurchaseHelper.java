package shop.shipyard;

import entities.Entity;
import entities.stations.DockingPort;
import entities.stations.Station;
import nomad.universe.Universe;
import shared.Vec2f;
import spaceship.Spaceship;
import spaceship.Spaceship.ShipState;
import view.ViewScene;

public class SpaceshipPurchaseHelper {
	private String file;
	private Spaceship ship;

	public SpaceshipPurchaseHelper(String file) {
		this.file = file;
	}

	public boolean run() {
		Vec2f p = Universe.getInstance().getCurrentEntity().getPosition();
		ship = new Spaceship(file, (int) p.x, (int) p.y, ShipState.SPACE);

		Entity entity = Universe.getInstance().getCurrentEntity();
		if (Station.class.isInstance(entity)) {
			return dockAtStation((Station) entity, ship);
		}
		return false;
	}

	private boolean dockAtStation(Station station, Spaceship ship) {

		for (int i = 0; i < station.getDocked().getDockingPorts().length; i++) {
			DockingPort dock = station.getDocked().getDockingPorts()[i];
			if (dock.getDockedShip() == null && (dock.isStartOpen()
					|| !dock.isStartOpen() && !station.getZone(dock.getZoneAssociation()).isVisited())) {
				dock.setDockedShip(ship);
				ship.setShipState(ShipState.DOCK);
				return true;
			}
		}
		return false;
	}

	public void warp() {
		// TODO Auto-generated method stub
		ViewScene.m_interface.Transition(ship.getZone(0).getName(), -101);
	}

}
