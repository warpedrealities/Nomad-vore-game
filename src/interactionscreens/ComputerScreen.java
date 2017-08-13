package interactionscreens;

import java.nio.FloatBuffer;

import actorRPG.player.Player_RPG;
import graphics.Screen_Fade;
import gui.Button;
import gui.Window;
import input.MouseHook;
import interactionscreens.subscreens.ComputerPedia;
import interactionscreens.subscreens.ComputerResearch;
import nomad.Universe;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;
import view.ViewScene;

public class ComputerScreen extends Screen implements Callback {

	Callback callback;
	ComputerResearch researchPanel;
	ComputerPedia encyclopediaPanel;
	Window subWindow;
	Screen_Fade screenFade;
	boolean side;
	float clock;

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub
		if (side) {
			encyclopediaPanel.update(DT);
		} else {
			researchPanel.update(DT);
		}
		if (clock > 0) {
			clock -= DT;
		}
		screenFade.update(DT);

	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {

		subWindow.Draw(buffer, matrixloc);
		if (side == false) {
			researchPanel.draw(buffer, matrixloc);
		} else {
			encyclopediaPanel.draw(buffer, matrixloc);
		}

		screenFade.draw(matrixloc, buffer);

	}

	@Override
	public void discard(MouseHook mouse) {

		mouse.Remove(subWindow);
		subWindow.discard();
		screenFade.discard();
		researchPanel.discard(mouse);
		encyclopediaPanel.discard(mouse);
	}

	@Override
	public void start(MouseHook hook) {

		hook.Register(subWindow);
		researchPanel.start(hook);
		encyclopediaPanel.start(hook);
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub
		if (screenFade.active() == false && clock <= 0) {
			switch (ID) {
			case 1:
				side = !side;
				if (side == true) {
					encyclopediaPanel.genList();
				}
				clock += 0.2F;
				break;

			case 2:
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

		subWindow = new Window(new Vec2f(-20, -16), new Vec2f(5, 5), textures[1], true);
		Button[] buttons = new Button[2];
		buttons[0] = new Button(new Vec2f(0.5F, 0.5F), new Vec2f(4, 1.8F), textures[2], this, "Exit", 2, 1);
		buttons[1] = new Button(new Vec2f(0.5F, 2.5F), new Vec2f(4, 1.8F), textures[2], this, "flip", 1, 1);
		// add buttons to move things to and from the container
		for (int i = 0; i < 2; i++) {
			subWindow.add(buttons[i]);
		}
		this.callback = callback;

		screenFade = new Screen_Fade(this);

		researchPanel = new ComputerResearch(textures, this);
		encyclopediaPanel = new ComputerPedia(textures, this);

	}

	@Override
	public void Callback() {
		// TODO Auto-generated method stub

		screenFade.run();
		ViewScene.m_interface.UpdateInfo();

	}

}
