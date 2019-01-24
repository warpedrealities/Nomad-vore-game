package shop.shipyard;

import java.nio.FloatBuffer;

import gui.Button;
import gui.Window;
import input.MouseHook;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;

public class ShipyardScreen extends Screen {

	private ShopShipyard shipyard;
	private Window window;
	private Callback callback;

	public ShipyardScreen(ShopShipyard shopShipyard) {
		this.shipyard = shopShipyard;

	}

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub
		window.update(DT);
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		// TODO Auto-generated method stub
		window.Draw(buffer, matrixloc);
	}

	@Override
	public void discard(MouseHook mouse) {
		// TODO Auto-generated method stub
		mouse.Remove(window);
		window.discard();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub
		switch (ID) {
		case 0:
			this.callback.Callback();
			break;
		case 1:

			break;
		case 2:

			break;
		case 3:

			break;
		}
	}

	@Override
	public void start(MouseHook hook) {
		// TODO Auto-generated method stub
		hook.Register(window);
	}

	@Override
	public void initialize(int[] textures, Callback callback) {
		// 0 is font
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint

		// TODO Auto-generated method stub
		window = new Window(new Vec2f(-20, -16), new Vec2f(40, 15), textures[1], true);
		Button[] buttons = new Button[4];
		buttons[0] = new Button(new Vec2f(0.5F, 6.5F), new Vec2f(4, 1.8F), textures[2], this, "prev", 2, 1);
		buttons[1] = new Button(new Vec2f(0.5F, 4.5F), new Vec2f(4, 1.8F), textures[2], this, "next", 3, 1);
		buttons[2] = new Button(new Vec2f(0.5F, 2.5F), new Vec2f(4, 1.8F), textures[2], this, "buy", 1, 1);
		buttons[3] = new Button(new Vec2f(0.5F, 0.5F), new Vec2f(4, 1.8F), textures[2], this, "exit", 0, 1);

		// add buttons to move things to and from the container
		for (int i = 0; i < 4; i++) {
			window.add(buttons[i]);
		}
		this.callback = callback;
	}
}
