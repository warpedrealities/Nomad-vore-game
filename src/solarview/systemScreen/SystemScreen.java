package solarview.systemScreen;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import gui.Button;
import gui.Text;
import gui.Window;
import input.MouseHook;
import nomad.Universe;
import shared.Callback;
import shared.MyListener;
import shared.Screen;
import shared.Vec2f;
import shared.Vec2i;
import shipsystem.ShipConverter;
import spaceship.Spaceship;
import spaceship.stats.WarpHandler;
import view.ZoneInteractionHandler;
import rendering.SpriteRotatable;

public class SystemScreen extends Screen implements MyListener {

	private Spaceship ship;
	private Callback callback;
	private Window window;
	private Window navWindow;
	private Button[] buttons;
	private boolean navMode;
	private MouseHook hook;
	private Button warpButton;
	private WarpData data;
	private float clock;
	
	public SystemScreen(Spaceship ship, Callback callback) {
		this.callback = callback;
		this.ship = ship;
	}

	@Override
	public void update(float DT) {
		if (clock>0)
		{
			clock-=DT;
		}
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		if (navMode)
		{
			navWindow.Draw(buffer, matrixloc);	
		}
		else
		{
			window.Draw(buffer, matrixloc);		
		}
		
	}

	@Override
	public void discard(MouseHook mouse) {
		mouse.Remove(window);
		window.discard();
		if (navWindow!=null)
		{
			mouse.Remove(navWindow);
			navWindow.discard();
		}
	}

	private boolean handleButtons(int id)
	{
		switch (id)
		{
		case 0:
			callback.Callback();
			return true;
		case 20:
			window.setActive(false);
			navWindow.setActive(true);
			navMode=true;
			clock=0.1F;
			return true;
		}
		return false;
	}
	
	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		if (clock<=0)
		{
			if (!navMode)
			{
				if (!handleButtons(ID)) {
					toggleConverter(ID - 1);
					clock=0.1F;
				}		
			}
			else
			{
				switch (ID)
				{
				case 22:
					ship.setWarpHandler(new WarpHandler(data.destination,data.stress));
					((SpriteRotatable)ship.getSpriteObj()).setFacing(data.facing);
					callback.Callback();	
					break;
				case 21:
					window.setActive(true);
					navWindow.setActive(false);
					navMode=false;
					clock=0.1F;
					break;
				}
			}			
		}


	}

	private void toggleConverter(int id) {
		ArrayList<ShipConverter> list = ship.getShipStats().getConverters();
		list.get(id).setActive(!list.get(id).isActive());
		if (list.get(id).isActive()) {
			buttons[id].setString("on");
		} else {
			buttons[id].setString("off");
		}
	}

	@Override
	public void start(MouseHook hook) {
		this.hook=hook;
		hook.Register(window);
		if (navWindow!=null)
		{
			hook.Register(navWindow);
		}
	}

	@Override
	public void initialize(int[] textures, Callback callback) {
		// 0 is bar
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint
		window = new Window(new Vec2f(0, -10), new Vec2f(11, 26), textures[1], true);
		buildList(textures[2], textures[4]);

		Button button = new Button(new Vec2f(6.1F, 0.1F), new Vec2f(4.8F, 2), textures[2], this, "exit", 0);
		window.add(button);
		if (ship.getShipStats().getFTL()>0)
		{
			button = new Button(new Vec2f(0.1F, 0.1F), new Vec2f(4.8F, 2), textures[2], this, "nav", 20);
			window.add(button);		
			genNavigation(textures,callback);
			
		}
	}
	class WarpData
	{
		public boolean canWarp;
		public Vec2i position;
		public Vec2i destination;
		public float stress;
		public int facing;
		
	};
	
	private WarpData genWarpData()
	{
		float navBonus=0.05F*((float)ship.getShipStats().getCrewStats().getNavigation());
		WarpNavigator navigator=new WarpNavigator();
		WarpData warpData=new WarpData();
		warpData.position=Universe.getInstance().getcurrentSystem().getPosition();
		warpData.stress=navigator.calculateStress(ship.getPosition())*(1-navBonus);
		warpData.facing=navigator.calcFacing(ship.getPosition());
		warpData.destination=navigator.calculateDestination(warpData.facing);

		if (warpData.destination!=null)
		{
			warpData.canWarp=true;
		}
		return warpData;
	}
	
	private void genNavigation(int[] textures, Callback callback)
	{
		navWindow = new Window(new Vec2f(-4, -10), new Vec2f(15, 26), textures[1], true);
		navWindow.setActive(false);
		Button button = new Button(new Vec2f(10.1F, 0.1F), new Vec2f(4.8F, 2), textures[2], this, "back", 21);
		navWindow.add(button);
		
		if (ship.getWarpHandler()!=null)
		{
			Text text = new Text(new Vec2f(0.1F, 12.5F), 
					"charging FTL:"+ship.getWarpHandler().getCharge()+"%", 
					1.0F, textures[4]);
			navWindow.add(text);			
		}
		else
		{
			data=genWarpData();
			Text []text=new Text[5];
			
			text[0]= new Text(new Vec2f(0.1F, 12.5F), 
					"plotting FTL track", 
					1.0F, textures[4]);
			text[1]= new Text(new Vec2f(0.1F, 11.5F), 
					"pos:"+data.position.x+" "+data.position.y, 
					1.0F, textures[4]);
			text[2]= new Text(new Vec2f(0.1F, 10.5F), 
					"GSI:"+data.stress, 
					1.0F, textures[4]);		
		
			for (int i=0;i<3;i++)
			{
				navWindow.add(text[i]);
			}
			if (data.canWarp)
			{
				text[3]= new Text(new Vec2f(0.1F, 9.5F), 
						"destination:"+data.destination.x+" "+data.destination.y, 
						1.0F, textures[4]);	
				text[4]= new Text(new Vec2f(0.1F, 8.5F), 
						"warp jump plotted", 
						1.0F, textures[4]);		
				navWindow.add(text[3]);	
				navWindow.add(text[4]);	
				warpButton = new Button(new Vec2f(0.1F, 0.1F), new Vec2f(4.8F, 2), textures[2], this, "warp", 22);
				navWindow.add(warpButton);					
			}
			else
			{
				text[3]= new Text(new Vec2f(0.1F, 9.5F), 
						"no star within warp range", 
						1.0F, textures[4]);	
				text[4]= new Text(new Vec2f(0.1F, 8.5F), 
						"warp jump cannot be attempted", 
						1.0F, textures[4]);
				navWindow.add(text[3]);	
				navWindow.add(text[4]);
			}
		}

	}

	private void buildList(int buttonTex, int tint) {
		ArrayList<ShipConverter> list = ship.getShipStats().getConverters();

		buttons = new Button[list.size()];
		for (int i = 0; i < list.size(); i++) {
			Text text = new Text(new Vec2f(0.1F, 12.5F - (i * 1)), list.get(i).getWidgetName(), 1.0F, tint);
			buttons[i] = new Button(new Vec2f(6.1F, 24 - (i * 2)), new Vec2f(4.8F, 2), buttonTex, this, "off", 1 + i);
			if (list.get(i).isActive()) {
				buttons[i].setString("on");
			}
			window.add(text);
			window.add(buttons[i]);
		}

	}
}
