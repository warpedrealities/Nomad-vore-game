package landingScreen;

import java.nio.FloatBuffer;

import gui.Button;
import gui.Window;
import input.MouseHook;
import nomad.Universe;
import nomad.World;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;
import spaceship.Spaceship;
import spaceship.Spaceship.ShipState;
import view.ViewScene;
import vmo.Game;

public class LandingScreen extends Screen implements Callback{

	Window window;
	LandingGrid grid;
	World world;
	Spaceship ship;
	Callback callback;
	
	public LandingScreen(Spaceship ship, World world) {
		this.ship=ship;
		this.world=world;
	
		
	}

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
	
		window.Draw(buffer, matrixloc);
		grid.Draw(buffer, matrixloc);
	}

	@Override
	public void discard(MouseHook mouse) {
	
		mouse.Remove(window);
		window.discard();
		grid.discard();
		mouse.Remove(window);
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		switch (ID)
		{
		
		case 1:
			//land
			attemptLanding();
			break;
		case 2:
			//exit
			callback.Callback();
			break;

		}
	}
	
	public void attemptLanding()
	{
		if (grid.getxSelect()<0 && grid.getySelect()<0)
		{
			return;
		}
		if (world.Land(ship,grid.getxSelect(),grid.getySelect()))
		{
			//remove ship from starsystem
			Universe.getInstance().getcurrentSystem().getEntities().remove(ship);
			//move ship to colocate with world
			ship.setPosition(new Vec2f(world.getPosition().x,world.getPosition().y));
			//write ship into zone
			ship.setShipState(ShipState.LAND);
			//connect ship
			
			//switch current entity
				
			Universe.getInstance().setCurrentEntity(world);			

			//switch view to viewscene
			Game.sceneManager.SwapScene(new ViewScene());
			
			//dont need to decompose, happens on scene switch			
		}	
		else
		{
			grid.failedLanding();
		}
	}

	@Override
	public void start(MouseHook hook) {

		hook.Register(window);
		hook.Register(grid);
	}
	
	@Override
	public void initialize(int[] textures, Callback callback) {
	
		//0 is font
		//1 is frame 
		//2 button
		//3 is button alt
		//4 tint
		window=new Window(new Vec2f(-10,-10),new Vec2f(20,20),textures[1],true);
		
		grid=new LandingGrid(world);
		Button [] buttons=new Button[2];
		buttons[0]=new Button(new Vec2f(4.2F,0.2F),new Vec2f(6,1.8F),textures[2],this,"Land",1,1);
		buttons[1]=new Button(new Vec2f(10.2F,0.2F),new Vec2f(6,1.8F),textures[2],this,"Cancel",2,1);
		//add buttons to move things to and from the container
		for (int i=0;i<2;i++)
		{
			window.add(buttons[i]);
		}
		
		this.callback=callback;
	}

	@Override
	public void Callback() {
		// TODO Auto-generated method stub
		
	}
}
