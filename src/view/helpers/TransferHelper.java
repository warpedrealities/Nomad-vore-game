package view.helpers;

import shared.Geometry;
import shared.Vec2f;
import view.SceneController;

public class TransferHelper {


	static private boolean passable(Vec2f p, int side, SceneController sceneController) {
		if (sceneController.getActiveZone().passable((int) p.x, (int) p.y, false)) {

			int scanThis[] = getScan(side);
			for (int i = 0; i < 3; i++) {
				Vec2f pScan = Geometry.getPos(scanThis[i], p);
				if (sceneController.getActiveZone().passable((int) pScan.x, (int) pScan.y, false)) {
					return true;
				}
			}
		}
		return false;
	}

	private static int[] getScan(int side) {
		int array[] = null;
		switch (side) {
		case 0:
			array = new int[] { 3, 4, 5 };
			break;
		case 1:
			array = new int[] { 7, 6, 5 };
			break;
		case 2:
			array = new int[] { 0, 1, 7 };
			break;
		case 3:
			array = new int[] { 1, 2, 3 };
			break;
		}
		return array;
	}

	static public void safety(int side, SceneController sceneController) {
		Vec2f p = new Vec2f(sceneController.getUniverse().player.getPosition().x,
				sceneController.getUniverse().player.getPosition().y);
		if (!passable(p, side, sceneController)) {
			if (side == 0 || side == 2) {
				if (p.x > sceneController.getActiveZone().zoneWidth / 2) {
					p.x += -1;
					while (!passable(p, side, sceneController)) {
						p.x = p.x - 1;
					}
				} else {
					p.x += 1;
					while (!passable(p, side, sceneController)) {
						p.x = p.x + 1;
					}
				}
			}
			if (side == 1 || side == 3) {
				if (p.y > sceneController.getActiveZone().zoneHeight / 2) {
					p.y += -1;
					while (!passable(p, side, sceneController)) {
						p.y = p.y - 1;
					}
				} else {

					p.y -= 1;
					while (!passable(p, side, sceneController)) {
						p.y = p.y + 1;
					}
				}
			}
			sceneController.getUniverse().player.setPosition(p);
		}
	}
}
