package view.menuSystem;

import java.nio.FloatBuffer;

import gui.Button2;
import gui.Window;
import input.MouseHook;
import shared.ButtonListener;
import shared.Vec2f;

public class MenuSystem {

	private boolean active;
	private Window window;
	private Vec2f innerCorner, outerCorner;
	private Button2 buttons[];
	public MenuSystem(int windowTexture, int button0, int button1, ButtonListener buttonListener) {
		active = false;
		window = new Window(new Vec2f(13.0F, -1.0F), new Vec2f(7, 15), windowTexture, false);
		buttons = new Button2[7];
		buttons[0] = new Button2(new Vec2f(0.5F, 0.5F), new Vec2f(6, 2), button0, buttonListener, "inventory(F6)", 30,
				button1, 0.8F);
		buttons[1] = new Button2(new Vec2f(0.5F, 2.5F), new Vec2f(6, 2), button0, buttonListener, "character(f5)", 31,
				button1, 0.8F);
		buttons[2] = new Button2(new Vec2f(0.5F, 4.5F), new Vec2f(6, 2), button0, buttonListener, "appearance(f4)", 32,
				button1, 0.8F);
		buttons[3] = new Button2(new Vec2f(0.5F, 6.5F), new Vec2f(6, 2), button0, buttonListener, "journal(f3)", 33,
				button1, 0.8F);
		buttons[4] = new Button2(new Vec2f(0.5F, 8.5F), new Vec2f(6, 2), button0, buttonListener, "actionbar(f2)", 34,
				button1, 0.8F);
		buttons[5] = new Button2(new Vec2f(0.5F, 10.5F), new Vec2f(6, 2), button0, buttonListener, "help(f1)", 35,
				button1, 0.8F);
		buttons[6] = new Button2(new Vec2f(0.5F, 12.5F), new Vec2f(6, 2), button0, buttonListener, "file(ESC)", 36,
				button1, 0.8F);
		for (int i = 0; i < 7; i++) {
			window.add(buttons[i]);
		}
		innerCorner = new Vec2f(window.getPosition().x - 0, window.getPosition().y + 0);
		outerCorner = innerCorner.replicate().add(window.getSize());
		outerCorner.x += 4;
	}

	public boolean isActive() {
		return active;
	}

	public void activate() {
		active = true;
		window.setActive(true);
	}

	public void deactivate() {
		active = false;
		window.setActive(false);
	}

	public void discard(MouseHook m_hook) {
		m_hook.Remove(window);
		window.discard();
	}

	public void draw(FloatBuffer matrix44Buffer, int matrixloc) {
		window.Draw(matrix44Buffer, matrixloc);
	}

	public void update(float dt) {
		Vec2f p = MouseHook.getInstance().getPosition();
		window.update(dt);
		if (p.x < innerCorner.x || p.x > outerCorner.x) {
			deactivate();
		}
		if (p.y < innerCorner.y || p.y > outerCorner.y) {
			deactivate();
		}
	}

	public void start(MouseHook mouse) {
		mouse.Register(window);
	}

}
