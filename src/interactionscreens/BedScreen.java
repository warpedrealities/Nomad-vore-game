package interactionscreens;

import graphics.Screen_Fade;
import gui.Button;
import gui.Window;
import input.MouseHook;
import nomad.universe.Universe;

import java.nio.FloatBuffer;

import actorRPG.player.Player_RPG;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;
import view.ViewScene;

public class BedScreen extends Screen implements Callback {

	Callback callback;
	Window window;
	Screen_Fade screenFade;
	int sleepLength;

	@Override
	public void update(float DT) {
		window.update(DT);
		screenFade.update(DT);
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		window.Draw(buffer, matrixloc);
		screenFade.draw(matrixloc, buffer);
	}

	@Override
	public void discard(MouseHook mouse) {

		mouse.Remove(window);
		window.discard();
		screenFade.discard();
	}

	@Override
	public void start(MouseHook hook) {

		hook.Register(window);

	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub
		if (screenFade.active() == false) {
			switch (ID) {

			case 1:
				// nap
				sleepLength = 100;
				screenFade.run();
				break;
			case 2:
				// sleep
				sleepLength = 300;
				screenFade.run();
				break;
			case 3:

				callback.Callback();
				break;
			}
		}

	}

	@Override
	public void initialize(int[] textures, Callback callback) {
		// TODO Auto-generated method stub
		// 0 is font
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint

		window = new Window(new Vec2f(-20, -16), new Vec2f(40, 15), textures[1], true);
		Button[] buttons = new Button[3];
		buttons[0] = new Button(new Vec2f(17.0F, 1.0F), new Vec2f(6, 1.8F), textures[2], this, "Exit", 3, 1);
		buttons[1] = new Button(new Vec2f(17.0F, 4.0F), new Vec2f(6, 1.8F), textures[2], this, "sleep", 2, 1);
		buttons[2] = new Button(new Vec2f(17.0F, 7.0F), new Vec2f(6, 1.8F), textures[2], this, "nap", 1, 1);
		// add buttons to move things to and from the container
		for (int i = 0; i < 3; i++) {
			window.add(buttons[i]);
		}
		this.callback = callback;

		screenFade = new Screen_Fade(this);
	}

	@Override
	public void Callback() {
		// TODO Auto-generated method stub
		((Player_RPG) Universe.getInstance().getPlayer().getRPG()).sleep(sleepLength);
		Universe.AddClock(sleepLength);
		sleepLength = 0;
		ViewScene.m_interface.UpdateInfo();
	}

}
