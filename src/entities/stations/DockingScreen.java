package entities.stations;

import java.nio.FloatBuffer;

import gui.Button;
import gui.SpriteImage;
import gui.Window;
import input.MouseHook;
import nomad.universe.Universe;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;
import spaceship.Spaceship;
import spaceship.Spaceship.ShipState;
import view.ViewScene;
import vmo.Game;

public class DockingScreen extends Screen implements Callback {
	private Spaceship ship;
	private Station station;
	private Window window;
	private Callback callback;
	private SpriteImage image;
	private DockingUI docking;

	public DockingScreen(Spaceship ship, Station station) {
		this.ship = ship;
		this.station = station;

	}

	@Override
	public void update(float DT) {

		window.update(DT);
		docking.update(DT);
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {

		window.Draw(buffer, matrixloc);
		docking.Draw(buffer, matrixloc);
	}

	@Override
	public void discard(MouseHook mouse) {

		mouse.Remove(window);
		window.discard();
		docking.discard();
		mouse.Remove(docking);
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		switch (ID) {

		case 1:
			dock();
			break;
		case 2:
			// exit
			callback.Callback();
			break;

		}
	}

	private void dock() {
		if (station.dock(ship, docking.getIndex())) {
			Universe.getInstance().getcurrentSystem().getEntities().remove(ship);
			// move ship to colocate with world
			ship.setPosition(new Vec2f(station.getPosition().x, station.getPosition().y));
			// write ship into zone
			ship.setShipState(ShipState.DOCK);
			// connect ship

			// switch current entity

			Universe.getInstance().setCurrentEntity(station);

			// switch view to viewscene
			Game.sceneManager.SwapScene(new ViewScene());

		}
	}

	@Override
	public void start(MouseHook hook) {
		hook.Register(window);
		hook.Register(docking);

	}

	@Override
	public void initialize(int[] textures, Callback callback) {

		// 0 is font
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint
		window = new Window(new Vec2f(-16, -10), new Vec2f(26, 24), textures[1], true);

		Button[] buttons = new Button[2];
		buttons[0] = new Button(new Vec2f(4.2F, 0.2F), new Vec2f(6, 1.8F), textures[2], this, "Dock", 1, 1);
		buttons[1] = new Button(new Vec2f(10.2F, 0.2F), new Vec2f(6, 1.8F), textures[2], this, "Cancel", 2, 1);
		// add buttons to move things to and from the container
		for (int i = 0; i < 2; i++) {
			window.add(buttons[i]);
		}

		image = new SpriteImage(new Vec2f(13, 12), new Vec2f(22, 20),
				"assets/art/dockingImages/" + station.getDocked().getDockingImage() + ".png", 1);
		this.window.add(image);
		image.AdjustPos(new Vec2f(13 + window.getPosition().x, 12 + window.getPosition().y));
		this.callback = callback;
		this.docking = new DockingUI(station, station.getDocked(), new Vec2f(-3.5F, 2));
	}

	@Override
	public void Callback() {
		// TODO Auto-generated method stub

	}
}
