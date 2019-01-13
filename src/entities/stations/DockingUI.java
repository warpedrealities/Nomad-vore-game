package entities.stations;

import java.nio.FloatBuffer;

import gui.GUIBase;
import gui.SpriteImage;
import shared.Vec2f;

public class DockingUI extends GUIBase {

	SpriteImage[] images;
	Station station;
	DockingModel dock;
	Vec2f position;
	int index;

	public DockingUI(Station station, DockingModel docked, Vec2f position) {
		this.position = position;
		this.dock = docked;
		this.station = station;
		index = 0;
		images = new SpriteImage[dock.getDockingPorts().length];
		for (int i = 0; i < images.length; i++) {
			Vec2f p = new Vec2f(position.x + dock.getDockingPorts()[i].getDisplayPosition().x,
					position.y + dock.getDockingPorts()[i].getDisplayPosition().y);
			images[i] = new SpriteImage(p,
					new Vec2f(2, 2),
					"assets/art/solar/dockingmarker.png", 4);
			images[i].setFrameIndex(getFrame(i));
		}
	}

	private int getFrame(int i) {
		if (dock.getDockingPorts()[i].getDockedShip() != null) {
			return 2;
		}
		if (i == index) {
			return 1;
		}
		if (!dock.getDockingPorts()[i].isStartOpen()
				&& !station.getZone(dock.getDockingPorts()[i].getZoneAssociation()).isVisited()) {
			return 3;
		}
		return 0;
	}

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean ClickEvent(Vec2f pos) {
		for (int i = 0; i < dock.getDockingPorts().length; i++) {
			Vec2f p = pos.replicate().subtract(position);
			if (p.getDistance(dock.getDockingPorts()[i].getDisplayPosition()) < 1.5F && canSelect(i)) {
				index = i;
				resetImages();
				break;
			}
		}
		return false;
	}

	private boolean canSelect(int i) {
		if (dock.getDockingPorts()[i].getDockedShip() != null) {
			return false;
		}
		if (!dock.getDockingPorts()[i].isStartOpen()
				&& !station.getZone(dock.getDockingPorts()[i].getZoneAssociation()).isVisited()) {
			return false;
		}
		return true;
	}

	private void resetImages() {
		for (int i = 0; i < images.length; i++) {
			images[i].setFrameIndex(getFrame(i));
		}
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		for (int i = 0; i < images.length; i++) {
			images[i].Draw(buffer, matrixloc);
		}
	}

	@Override
	public void discard() {

		for (int i = 0; i < images.length; i++) {
			images[i].discard();
		}
	}

	@Override
	public void AdjustPos(Vec2f p) {
		// TODO Auto-generated method stub

	}

	public int getIndex() {
		return index;
	}

}
