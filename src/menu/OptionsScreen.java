package menu;

import java.nio.FloatBuffer;

import gui.Button;
import gui.List;
import gui.Text;
import gui.Textwindow;
import gui.Window;
import input.MouseHook;
import shared.Callback;
import shared.SceneManager;
import shared.Screen;
import shared.Vec2f;
import vmo.Game;

public class OptionsScreen extends Screen implements Callback {

	Callback m_callback;
	float clock;
	Window m_window;
	Button[] buttons;

	public OptionsScreen(int font, int frame, int button, int tint, boolean save, Callback callback) {
		m_callback = callback;

		m_callback = callback;
		clock = 0.5F;
		// generate window
		m_window = new Window(new Vec2f(-6.5F, -12), new Vec2f(14, 18), frame, true);

		Button exit = new Button(new Vec2f(8.5F, 0.25F), new Vec2f(5, 1.5F), button, this, "back", 0);
		m_window.add(exit);

		Text[] text = new Text[2];
		buttons = new Button[2];
		for (int i = 0; i < 2; i++) {
			String str = "";
			String tag = "no";
			switch (i) {
			case 0:
				str = "verbose combat";
				if (Game.sceneManager.getConfig().isVerboseCombat()) {
					tag = "yes";
				}
				break;
			case 1:
				str = "disable autosave";
				if (Game.sceneManager.getConfig().isDisableAutosave()) {
					tag = "yes";
				}
				break;

			}
			text[i] = new Text(new Vec2f(0.2F, 8.5F - (i * 1)), str, tint);
			buttons[i] = new Button(new Vec2f(9.5F, 16 - (2 * i)), new Vec2f(4, 1.5F), button, this, tag, 1 + i);
			m_window.add(buttons[i]);
			m_window.add(text[i]);
		}
	}

	@Override
	public void Callback() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub
		if (clock > 0) {
			clock -= DT;
		}
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {

		m_window.Draw(buffer, matrixloc);

	}

	@Override
	public void discard(MouseHook mouse) {
		mouse.Remove(m_window);
		m_window.discard();

	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {

		if (clock <= 0) {
			switch (ID) {
			case 0:
				m_callback.Callback();
				break;
			case 1:
				toggleVerboseCombat();
				break;
			case 2:
				toggleDisableAutosave();
				break;
			}
		}
	}

	private void toggleVerboseCombat() {

		boolean b = Game.sceneManager.getConfig().isVerboseCombat();
		if (b) {
			Game.sceneManager.getConfig().setVerboseCombat(false);
			buttons[0].setString("no");
		} else {
			Game.sceneManager.getConfig().setVerboseCombat(true);
			buttons[0].setString("yes");
		}

	}

	private void toggleDisableAutosave() {

		boolean b = Game.sceneManager.getConfig().isDisableAutosave();
		if (b) {
			Game.sceneManager.getConfig().setDisableAutosave(false);
			buttons[1].setString("no");
		} else {
			Game.sceneManager.getConfig().setDisableAutosave(true);
			buttons[1].setString("yes");
		}

	}

	@Override
	public void start(MouseHook hook) {
		// TODO Auto-generated method stub
		hook.Register(m_window);

	}

}
