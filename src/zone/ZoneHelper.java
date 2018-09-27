package zone;

import java.util.ArrayList;

import shared.Vec2f;
import spaceship.Spaceship;
import worldgentools.ZoneBuildTools;

public class ZoneHelper {

	public boolean landShip(Spaceship ship, ArrayList<Landing> landings, Zone zone) {
		// if zone generated
		if (zone.getTiles() != null) {
			return landShipGenerated(ship, landings, zone);
		}
		// if zone ungenerated
		else {
			return landShipUnGenerated(ship, landings, zone);
		}
	}

	private boolean landShipGenerated(Spaceship ship, ArrayList<Landing> landings, Zone zone) {
		if (zone.getLandingSite() != null) {
			Vec2f p = new Vec2f(zone.getLandingSite().x - (ship.getSize().x / 2),
					zone.getLandingSite().y - (ship.getSize().y / 2));
			Landing l = new Landing(ship, (int) zone.zonePosition.x, (int) zone.zonePosition.y, p);
			landings.add(l);
			ZoneBuildTools tools = new ZoneBuildTools(zone, zone.zoneTileGrid);
			tools.AddShip(l);
			ship.Generate();
			ship.getZone(0).getPortalWidget(-101).setDestination(zone.getName(), -101);
			return true;
		} else {

		}

		return false;
	}

	private boolean landShipUnGenerated(Spaceship ship, ArrayList<Landing> landings, Zone zone) {
		if (zone.getLandingSite() != null) {
			Vec2f p = new Vec2f(zone.getLandingSite().x - (ship.getSize().x / 2),
					zone.getLandingSite().y - (ship.getSize().y / 2));
			Landing l = new Landing(ship, (int) zone.zonePosition.x, (int) zone.zonePosition.y, p);
			landings.add(l);
		} else {
			// pick a random spot not within 8 tiles of the map edge
		}

		return false;
	}
}
