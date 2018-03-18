package interactionscreens;

import java.nio.FloatBuffer;

import gui.Button;
import gui.Text;
import gui.Window;
import input.MouseHook;
import nomad.universe.Universe;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;
import view.ViewScene;
import widgets.WidgetReformer;

public class ReformerScreen extends Screen {

	private WidgetReformer reformer;
	private Window window;
	private Callback callback;
	
	public ReformerScreen(WidgetReformer widgetReformation) {
		this.reformer=widgetReformation;
	}

	@Override
	public void update(float DT) {
		window.update(DT);
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
	
		window.Draw(buffer, matrixloc);
	}

	@Override
	public void discard(MouseHook mouse) {
		mouse.Remove(window);
		window.discard();
	}

	private void registerMachine()
	{
		Long l=Universe.getInstance().getPlayer().getReformHandler().linkMachine(Universe.getInstance().getCurrentZone().getName());
		reformer.setUid(l);
		reformer.setActive(true);
	}
	
	@Override
	public void ButtonCallback(int ID, Vec2f p) {
	
		switch (ID)
		{
			case 0:
				callback.Callback();		
			break;
			case 1:
				if (reformer.isSuppressed())
				{
					reformer.setSuppressed(false);
					ViewScene.m_interface.DrawText("reformer now operational");
				}
				else
				{
					registerMachine();
					ViewScene.m_interface.DrawText("you've synchronized with this reformer");
				}	
				callback.Callback();
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
		// 0 is bar
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint
		window = new Window(new Vec2f(-12, -16), new Vec2f(24, 15), textures[1], true);
		this.callback = callback;
		
		Button []buttons=new Button[2];
		buttons[0] = new Button(new Vec2f(17.5F, 0.5F), new Vec2f(6, 1.8F), textures[2], this, "Exit", 0, 1);
		if (reformer.isSuppressed())
		{
			buttons[1] = new Button(new Vec2f(17.5F, 2.5F), new Vec2f(6, 1.8F), textures[2], this, "restore", 1, 1);
	
		}
		else
		{
			buttons[1] = new Button(new Vec2f(17.5F, 2.5F), new Vec2f(6, 1.8F), textures[2], this, "link", 1, 1);
		
		}
		// add buttons to move things to and from the container
		for (int i = 0; i < 2; i++) {
			window.add(buttons[i]);
		}	
		Text text=new Text(new Vec2f(0.6F, 4.5F), "converter text", 1.0F, textures[4]);
		
		if (reformer.isSuppressed())
		{
			text.setString("reformation system suppressed");			
		}
		else
		{
			if (reformer.isActive())
			{
				buttons[1].setActive(false);
				text.setString("reformation system synchronized with pattern");
			}
			else
			{
				text.setString("reformation system inactive, link pattern");	
			}		
		}

	
		window.add(text);
	}

}
