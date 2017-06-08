package solarview.systemScreen;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import gui.Button;
import gui.Text;
import gui.Window;
import input.MouseHook;
import shared.Callback;
import shared.MyListener;
import shared.Screen;
import shared.Vec2f;
import shipsystem.ShipConverter;
import spaceship.Spaceship;

public class SystemScreen extends Screen implements MyListener {

	private Spaceship ship;
	private Callback callback;
	private Window window;
	private Button []buttons;
	
	public SystemScreen(Spaceship ship,Callback callback)
	{
		this.callback=callback;
		this.ship=ship;
	}
	
	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub

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

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		if (ID==0)
		{
			callback.Callback();
		}
		else
		{
			toggleConverter(ID-1);
		}
	}

	private void toggleConverter(int id)
	{
		ArrayList<ShipConverter> list=ship.getShipStats().getConverters();
		list.get(id).setActive(!list.get(id).isActive());
		if (list.get(id).isActive())
		{
			buttons[id].setString("on");
		}
		else
		{
			buttons[id].setString("off");
		}
	}
	
	@Override
	public void start(MouseHook hook) {
		hook.Register(window);
	}
	
	@Override
	public void initialize(int[] textures, Callback callback) {
		//0 is bar
		//1 is frame 
		//2 button
		//3 is button alt
		//4 tint		
		window=new Window(new Vec2f(0,-10),new Vec2f(11,26),textures[1],true);
		buildList(textures[2],textures[4]);
		
		Button button=new Button(new Vec2f(6.1F,0.1F), new Vec2f(4.8F,2), textures[2], this, "exit", 0);
		window.add(button);
	}
	
	private void buildList(int buttonTex,int tint)
	{
		ArrayList<ShipConverter> list=ship.getShipStats().getConverters();
		
		buttons=new Button[list.size()];
		for (int i=0;i<list.size();i++)
		{
			Text text=new Text(new Vec2f(0.1F,12.5F-(i*1)),list.get(i).getWidgetName(),1.0F,tint);
			buttons[i]=new Button(new Vec2f(6.1F,24-(i*2)), new Vec2f(4.8F,2), buttonTex, this, "off", 1+i);
			if (list.get(i).isActive())
			{
				buttons[i].setString("on");
			}
			window.add(text);
			window.add(buttons[i]);
		}
		
	}
}
