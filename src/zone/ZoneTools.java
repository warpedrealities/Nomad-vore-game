package zone;

import shared.Vec2f;
import zone.Zone.zoneType;

public class ZoneTools {

	public static Zone.zoneType zoneTypeFromString(String string) {
		if (string.equals("CLOSED")) {
			return zoneType.CLOSED;
		}
		if (string.equals("SURFACE")) {
			return zoneType.SURFACE;
		}
		if (string.equals("LIMITED")) {
			return zoneType.LIMITED;
		}
		return null;
	}

	public static Zone.zoneType zoneTypeFromInt(int v) {
		switch (v) {
		case 0:
			return zoneType.SURFACE;
		case 1:
			return zoneType.LIMITED;
		case 2:
			return zoneType.CLOSED;
		}
		return null;
	}

	static boolean LandingLocationSizeCheck(int width, int height, int x, int y, Zone zone) {
		for (int i = x; i < x + width; i++) {
			for (int j = y; j < y + height; j++) {
				Tile t=zone.zoneTileGrid[i][j];
				if (t.getDefinition().getMovement() != TileDef.TileMovement.WALK) {
					return false;
				}
			}
		}

		return true;
	}

	public static Vec2f getLandingLocation(int width, int height, Zone zone, float sw, float sh) {
		Vec2f landingSite=zone.getLandingSite();
		if (landingSite != null) {
			Vec2f p = new Vec2f(landingSite.x - (sw / 2), landingSite.y - (sh / 2));
			if (zone.getTiles() != null) {
				if (LandingLocationSizeCheck(width, height, (int) p.x, (int) p.y, zone)) {
					return new Vec2f(p.x, p.y);
				}
			} else {
				return new Vec2f(zone.getLandingSite().x, zone.getLandingSite().y);
			}

		}

		for (int i = 1; i < zone.zoneWidth - width; i++) {
			for (int j = 1; j < zone.zoneHeight - height; j++) {
				if (zone.zoneTileGrid[i][j].getDefinition().getMovement() == TileDef.TileMovement.WALK) {
					// check to see if its wide enough
					if (LandingLocationSizeCheck(width, height, i, j, zone)) {
						return new Vec2f(i, j);
					}

				}
			}
		}
		return null;
	}
}
