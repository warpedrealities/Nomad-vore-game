package interactionscreens;

import gui.Button;
import gui.Text;
import gui.Window;
import input.MouseHook;

import java.nio.FloatBuffer;
import java.util.Iterator;

import actor.CompanionTool;

import nomad.Station;
import nomad.Universe;
import nomad.World;

import shared.Callback;
import shared.Screen;
import shared.Vec2f;
import solarview.SolarScene;
import spaceship.Spaceship;
import spaceship.Spaceship.ShipState;
import spaceship.stats.SpaceshipAnalyzer;
import spaceship.stats.SpaceshipStats;
import spaceship.SpaceshipActionHandler;
import spaceship.SpaceshipResource;
import vmo.Game;
import vmo.GameManager;

public class NavScreen extends Screen implements Callback {

	Spaceship spaceship;
	SpaceshipStats shipStats;
	Callback callback;
	Window window;
	Window statWindow;
	boolean canLaunch;
	String statustext;
	
	public NavScreen(Spaceship ship)
	{
		spaceship=ship;
		shipStats=new SpaceshipAnalyzer().generateStats(spaceship);
		setCanLaunch();
		
	}
	
	public void setCanLaunch()
	{
		canLaunch=true;
		if (spaceship.isWrecked()==true)
		{
			canLaunch=false;
			String str=spaceship.getUnusableState();
			statustext="flight impossible: "+str;
			return;
		}
		if (shipStats.isLooseItems())
		{
			canLaunch=false;
			statustext="unsecured items, thrust unsafe, secure items";
			return;
		}
		if (shipStats.getResource("HULL").getResourceAmount()<=0)
		{
			canLaunch=false;
			statustext="thrust unsafe, hull compromised, repairs required";
			return;
		}
		if (shipStats.getResource("FUEL")==null)
		{
			statustext="no fuel tanks connected to drive system, correct fault";
			canLaunch=false;
			return;
		}
		if (shipStats.getResource("FUEL").getResourceAmount()<=spaceship.getBaseStats().getThrustCost() && spaceship.getShipState()==ShipState.LAND)
		{
			canLaunch=false;
			statustext="insufficient fuel for launch, more fuel required";
			return;
		}
		if (shipStats.getResource("FUEL").getResourceAmount()<=shipStats.getFuelEfficiency() && spaceship.getShipState()!=ShipState.LAND)
		{
			canLaunch=false;
			statustext="fuel pressure low, more fuel required";
			return;
		}	

		if (canLaunch==true)
		{
			if (shipStats.getCrewCapacity()<shipStats.getCrewCount())
			{
				statustext="insufficient accomodation for current crew";
			}
			else
			{
				statustext="system okay";
			}
			
		}
	}
	
	@Override
	public void Callback() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		
		window.Draw(buffer, matrixloc);
		statWindow.Draw(buffer, matrixloc);
	}

	@Override
	public void discard(MouseHook mouse) {

		mouse.Remove(window);
		window.discard();
		statWindow.discard();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub
		switch (ID)
		{
		case 0:
			callback.Callback();
			break;
			
		case 1:
			launch();
			break;
		
		
		}
	}

	
	
	private void launch() {
		if (canLaunch==true)
		{
			if (spaceship.getState()==ShipState.LAND)
			{
				if (shipStats.getCrewCapacity()<shipStats.getCrewCount())
				{
					offLoadCrew();
				}
				spaceship.setShipStats(shipStats);
				spaceship.setShipState(ShipState.SPACE);
				shipStats.getResource("FUEL").subtractResourceAmount(shipStats.getFuelEfficiency());
				int r=new SpaceshipActionHandler().Launch(spaceship,(World)Universe.getInstance().getCurrentWorld(spaceship));
				
				Game.sceneManager.SwapScene(new SolarScene(r,spaceship));	
				return;
			}
			if (spaceship.getState()==ShipState.SPACE)
			{
				spaceship.setShipStats(shipStats);
				//spaceship.setShipState(ShipState.SPACE);
				Game.sceneManager.SwapScene(new SolarScene(0,spaceship));		
				return;
			}
			if (spaceship.getState()==ShipState.DOCK)
			{
				if (shipStats.getCrewCapacity()<shipStats.getCrewCount())
				{
					offLoadCrew();
				}
				spaceship.setShipStats(shipStats);
				spaceship.setShipState(ShipState.SPACE);
				int r=new SpaceshipActionHandler().undock(spaceship,(Station)Universe.getInstance().getCurrentWorld(spaceship));
					
				Game.sceneManager.SwapScene(new SolarScene(r,spaceship));	
				return;
			}
			if (spaceship.getState()==ShipState.SHIPDOCK)
			{
				int r=new SpaceshipActionHandler().separate(spaceship,(Spaceship)Universe.getInstance().getCurrentWorld(spaceship));
				spaceship.setShipStats(shipStats);
				spaceship.setShipState(ShipState.SPACE);				
				Game.sceneManager.SwapScene(new SolarScene(r,spaceship));	
				return;
			}
		}
	}

	private void offLoadCrew() {
		//figure out how many crew we need to offload
		int offload=shipStats.getCrewCount()-shipStats.getCrewCapacity();
		for (int i=0;i<offload;i++)
		{
			CompanionTool.removePassenger(shipStats.getCrewList().get(i),spaceship);
		}
	}

	@Override
	public void start(MouseHook hook) {

		hook.Register(window);
	}
	
	@Override
	public void initialize(int[] textures, Callback callback) {	
		this.callback=callback;
		//0 is bar
		//1 is frame 
		//2 button
		//3 is button alt
		//4 tint
		
		//build window
		window=new Window(new Vec2f(-20,-16),new Vec2f(40,15),textures[1],true);
		
		//build buttons
		Button []buttons=new Button[2];
		
		buttons[0]=new Button(new Vec2f(34.0F,0.0F),new Vec2f(6,1.8F),textures[2],this,"Exit",0,1);
		
		switch (spaceship.getShipState())
		{
		case LAND:
			buttons[1]=new Button(new Vec2f(34.0F,2.0F),new Vec2f(6,1.8F),textures[2],this,"launch",1,1);
			break;
			
		case DOCK:
			buttons[1]=new Button(new Vec2f(34.0F,2.0F),new Vec2f(6,1.8F),textures[2],this,"undock",1,1);	
			break;
		case SHIPDOCK:
			buttons[1]=new Button(new Vec2f(34.0F,2.0F),new Vec2f(6,1.8F),textures[2],this,"undock",1,1);	
			break;
		case SPACE:
			buttons[1]=new Button(new Vec2f(34.0F,2.0F),new Vec2f(6,1.8F),textures[2],this,"control",1,1);
			break;
		}
		
		
		for (int i=0;i<2;i++)
		{
			window.add(buttons[i]);
		}
		//build info texts
		Text []texts=new Text[10];
		int count=0;
		Iterator<SpaceshipResource> iterator=shipStats.getIterator();
		
		for (int i=0;i<10;i++)
		{
			texts[i]=new Text(new Vec2f(0.5F,7.0F-(0.7F*i)),"texts",0.7F,textures[4]);
			if (iterator.hasNext())
			{
				SpaceshipResource res=iterator.next();
				texts[i].setString(res.getResourceName()+":"+res.getResourceAmount()+"/"+res.getResourceCap());
				count++;
			}
			else
			{
				texts[i].setString("");
			}
			window.add(texts[i]);
		}
		
		
		if (count<10 && shipStats.isLooseItems())
		{
			texts[count].setString("unsecured objects detected, acceleration hazard");
		}
		
		Text status=new Text(new Vec2f(10.5F,7.0F),statustext,0.7F,textures[4]);
		window.add(status);
		buildStatWindow(textures);
	}
	
	private void buildStatWindow(int []textures)
	{
		
		statWindow=new Window(new Vec2f(3,-1),new Vec2f(17,17),textures[1],true);
		Text []texts=new Text[4];
		for (int i=0;i<4;i++)
		{
			texts[i]=new Text(new Vec2f(0.5F,8.0F-(0.7F*i)),"texts",0.7F,textures[4]);
			switch (i)
			{
			case 0:
				texts[i].setString("fuel efficiency:"+shipStats.getFuelEfficiency());
				break;
			case 1:
				float speed=((10.0F/((float)shipStats.getMoveCost()))*100);
				speed-=(speed)%1;
				texts[i].setString("speed:"+speed);
				break;	
			case 2:
				texts[i].setString("manouver:"+shipStats.getManouverability());
				break;
			case 3:
				texts[i].setString("armour:"+shipStats.getArmour());
				break;
			}
			statWindow.add(texts[i]);
		}
		
		if (shipStats.getShield()!=null)
		{
			texts=new Text[5];
			for (int i=0;i<5;i++)
			{
				texts[i]=new Text(new Vec2f(5.5F,8.0F-(0.7F*i)),"texts",0.7F,textures[4]);
				switch (i)
				{
				case 0:
					texts[i].setString("hitpoints:"+shipStats.getShield().getHitpoints());
					break;
				case 1:
					texts[i].setString("absorption:"+shipStats.getShield().getAbsorption());
					break;	
				case 2:
					texts[i].setString("regeneration:"+shipStats.getShield().getRegeneration());
					break;
				case 3:
					texts[i].setString("restartTime:"+shipStats.getShield().getRestartTime());
					break;
				case 4:
					texts[i].setString("energyCost:"+shipStats.getShield().getEnergyCost());
					break;
				}
				statWindow.add(texts[i]);
			}
		}
	}

}
