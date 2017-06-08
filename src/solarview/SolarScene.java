package solarview;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import nomad.Universe;
import input.MouseHook;
import rendering.SpriteBatch;
import shared.Callback;
import shared.MyListener;
import shared.SceneBase;
import shared.Screen;
import shared.Vec2f;
import solarBackdrop.StarScape;
import solarview.systemScreen.SystemScreen;
import spaceship.PlayerShipController;
import spaceship.Spaceship;
import spaceship.SpaceshipAnalyzer;
import view.ViewScene;
import vmo.Game;

public class SolarScene extends SceneBase implements MyListener, Solar_Interface,Callback {

	
	static Solar_Interface solarInt;
	
	public static Solar_Interface getInterface()
	{
		return solarInt;
	}
	
	Spaceship playerShip;
	MouseHook mouseHook;
	SolarRenderer renderer;
	SolarGUI GUI;
	SolarController controller;
	PlayerShipController shipController;
	float clock;
	Screen screen;
	StarScape starscape;
	float incrementCounter;
	
	public SolarScene(int r,Spaceship spaceship) {

		incrementCounter=1.0F;
		solarInt=this;
		playerShip=spaceship;
		shipController=new PlayerShipController(Universe.getInstance().getSystem());
		playerShip.setShipController(shipController);
		renderer=new SolarRenderer(Universe.getInstance().getSystem());
		controller=new SolarController(Universe.getInstance().getSystem());
		GUI=new SolarGUI();
		GUI.generate(spaceship,this);
		starscape=new StarScape();
		renderer.setCurrentPosition(playerShip.getPosition());
		starscape.setCurrentPosition(playerShip.getPosition());
		((SpriteRotatable)(playerShip.getSpriteObj())).setFacing(r);

	}

	@Override
	public void Update(float dt) {

		if (screen!=null)
		{
			screen.update(dt);
		}
		else
		{
			shipController.controlUpdate(dt);
			if (clock<=0)
			{
				if (shipController.canAct())
				{
					if (shipController.control(playerShip))
					{
						GUI.update();
						renderer.setCurrentPosition(playerShip.getPosition());
						starscape.setCurrentPosition(playerShip.getPosition());
						clock+=0.05F;
					}
					//clicking control
					if (mouseHook.buttonDown())
					{
						handleClick();
						clock+=0.25F;
						
					}		
				}
				else
				{
					controller.update();
					clock+=0.05F;
				}
				
			}
			else
			{
				clock-=dt;
				if (clock<0)
				{
					clock=0;
				}
			}				
		}

	}
	
	
	
	private void handleClick()
	{
		Vec2f p=new Vec2f(mouseHook.getPosition().x,mouseHook.getPosition().y);
		if (p.x<11)
		{
			p=MouseConverter.convertMousePointer(p,playerShip.getPosition(), renderer.getScale());

		}

	}


	@Override
	public void Draw() {
		
		GL20.glUseProgram(Game.m_pshader);

		 GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glDisable(GL11.GL_DEPTH_TEST); 
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		m_viewmatrix.store(matrix44Buffer); matrix44Buffer.flip();
		GL20.glUniform1fv(m_variables[1], matrix44Buffer);	
		starscape.draw(m_variables[1],m_variables[2],m_variables[0],matrix44Buffer);

		GL20.glUniform4f(m_variables[0],1.0F,1.0F,1.0F, 1);		
		renderer.solarDraw(m_variables[1],m_variables[2],m_variables[0],matrix44Buffer);
		GL20.glUniform4f(m_variables[0],1.0F,1.0F,1.0F, 1);		
		GUI.draw(m_variables[1], m_variables[2], m_variables[0], matrix44Buffer);
		if (screen!=null)
		{
			screen.draw(matrix44Buffer, SceneBase.getVariables()[2]);
		}
	}

	@Override
	public void start(MouseHook mouse) {
	
		mouseHook=mouse;
		GUI.start(mouseHook);
	}

	@Override
	public void end() {
		playerShip.getShipStats().runDecompose();
		new SpaceshipAnalyzer().decomposeResources(playerShip.getShipStats(), playerShip);
		playerShip.setShipStats(null);
		playerShip.setShipController(null);
		GUI.discard(mouseHook);
		renderer.end();
		
		if (screen!=null)
		{
			screen.discard(mouseHook);
		}
		solarInt=null;
		
		starscape.discard();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub
		switch (ID)
		{
		case 0:
			//exit to view screen

			Game.sceneManager.SwapScene(new ViewScene());
			break;
			
		case 2:
			//magnify
			if (renderer.getScale()>1)
			{
				renderer.setScale(renderer.getScale()/2);
				starscape.setScale(renderer.getScale()/2);
			}
			break;
			
		case 1:
			//reduce
			if (renderer.getScale()<8)
			{
				renderer.setScale(renderer.getScale()*2);
				starscape.setScale(renderer.getScale()*2);
			}
			break;
		case 3:
			setScreen(new SystemScreen(playerShip,this));
			break;
		}
	}

	@Override
	public void setScreen(Screen screen) {
		
		if (this.screen==null)
		{
			int values[]=new int[5];
			//values[0]=m_textureIds[6];
			values[1]=GUI.getTextures()[0];
			values[2]=GUI.getTextures()[1];
		//	values[3]=m_textureIds[8];
			values[4]=m_variables[0];
			this.screen=screen;
			this.screen.initialize(values, this);
			this.screen.start(mouseHook);	
			this.screen=screen;
		}
	}

	@Override
	public void Callback() {
		
		if (screen!=null)
		{			
			screen.discard(mouseHook);
			screen=null;		
		}
	}

}
