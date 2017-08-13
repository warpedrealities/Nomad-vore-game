package interactionscreens;

import java.nio.FloatBuffer;

import actorRPG.player.Player_RPG;
import graphics.Screen_Fade;
import gui.Button;
import gui.Text;
import gui.Window;
import input.MouseHook;
import nomad.Universe;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;
import spaceship.Spaceship;
import view.ViewScene;

public class WarpScreen extends Screen  implements Callback{

	Callback callback;
	Window window;
	Screen_Fade screenFade;
	static final int sleepLength=300;
	private Spaceship ship;
	private Text description;
	
	public WarpScreen(Spaceship ship) {
		this.ship=ship;
		
	}

	@Override
	public void update(float DT) {
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
	public void ButtonCallback(int ID, Vec2f p) {
		if (screenFade.active() == false) {
			switch (ID) {

			case 2:
				// sleep
				screenFade.run();
				break;
			case 3:

				callback.Callback();
				break;
			}
		}

	}

	@Override
	public void start(MouseHook hook) {
		hook.Register(window);
	}

	@Override
	public void initialize(int[] textures, Callback callback) {
		// TODO Auto-generated method stub
		// 0 is font
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint
		long l=ship.getWarpHandler().getTimeLeft();
		description=new Text(new Vec2f(6.5F, 5.0F), generateString(l), 0.7F, textures[4]);
		
		window = new Window(new Vec2f(-20, -16), new Vec2f(40, 15), textures[1], true);
		Button[] buttons = new Button[2];
		buttons[0] = new Button(new Vec2f(33.7F, 0.2F), new Vec2f(6, 1.8F), textures[2], this, "Exit", 3, 1);
		buttons[1] = new Button(new Vec2f(33.7F, 2.2F), new Vec2f(6, 1.8F), textures[2], this, "wait", 2, 1);
		for (int i = 0; i < 2; i++) {
			window.add(buttons[i]);
		}
		this.callback = callback;

		screenFade = new Screen_Fade(this);
		window.add(description);
	}

	private String generateString(long l)
	{
		
		String str="warp jump in progress, time left:"+l;

		return str;
	}
	
	public void refresh()
	{
		long l=ship.getWarpHandler().getTimeLeft();
		if (l<0)
		{
			callback.Callback();
			ViewScene.m_interface.DrawText("warp jump completed, warp out when ready");
		}
		else
		{
			description.setString(generateString(l));
		}
	
	}
	
	@Override
	public void Callback() {
		((Player_RPG) Universe.getInstance().getPlayer().getRPG()).rest(sleepLength);
		Universe.AddClock(sleepLength);
		ViewScene.m_interface.UpdateInfo();

		refresh();
	}

}
