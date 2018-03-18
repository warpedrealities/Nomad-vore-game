package widgets.capsules;

import java.nio.FloatBuffer;

import gui.Button;
import gui.Window;
import input.MouseHook;
import landingScreen.LandingGrid;
import nomad.World;
import nomad.universe.Universe;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;
import spaceship.Spaceship;
import view.ViewScene;
import vmo.Game;
import zone.Zone;

public class CapsuleLandingScreen extends Screen implements Callback {
	Window window;
	LandingGrid grid;
	World world;
	Spaceship ship;
	Callback callback;
	private CapsuleBehaviour capsuleBehaviour;
	
	public CapsuleLandingScreen(CapsuleBehaviour capsuleBehaviour, World world) {
		this.world = world;
		this.capsuleBehaviour=capsuleBehaviour;
	}

	@Override
	public void update(float DT) {
		window.update(DT);

	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		window.Draw(buffer, matrixloc);
		grid.Draw(buffer, matrixloc);
	}

	@Override
	public void discard(MouseHook mouse) {
		mouse.Remove(grid);
		mouse.Remove(window);
		window.discard();
		grid.discard();
		mouse.Remove(window);
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		switch (ID) {

		case 1:
			// land
			attemptLanding();
			break;
		case 2:
			// exit
			callback.Callback();
			break;

		}
	}

	private void attemptLanding() {
	
		Zone z=world.getZone(grid.getxSelect(), grid.getySelect());
		capsuleBehaviour.performLanding(z);
		

		capsuleBehaviour.getCapsule().setDeployed(true);
		capsuleBehaviour.finish();
	}

	@Override
	public void start(MouseHook hook) {
		hook.Register(window);
		hook.Register(grid);
	}

	@Override
	public void initialize(int[] textures, Callback callback) {

		// 0 is font
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint
		window = new Window(new Vec2f(-10, -10), new Vec2f(20, 20), textures[1], true);

		grid = new LandingGrid(world);
		Button[] buttons = new Button[2];
		buttons[0] = new Button(new Vec2f(4.2F, 0.2F), new Vec2f(6, 1.8F), textures[2], this, "Land", 1, 1);
		buttons[1] = new Button(new Vec2f(10.2F, 0.2F), new Vec2f(6, 1.8F), textures[2], this, "Cancel", 2, 1);
		// add buttons to move things to and from the container
		for (int i = 0; i < 2; i++) {
			window.add(buttons[i]);
		}

		this.callback = callback;
	}	
	
	@Override
	public void Callback() {
	
	}

}
