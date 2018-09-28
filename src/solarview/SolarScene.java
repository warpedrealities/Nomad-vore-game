package solarview;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import input.MouseHook;
import nomad.StarSystem;
import nomad.universe.Universe;
import rendering.SpriteRotatable;
import shared.Callback;
import shared.Geometry;
import shared.MyListener;
import shared.SceneBase;
import shared.Screen;
import shared.Vec2f;
import shared.Vec2i;
import solarBackdrop.StarScape;
import solarBackdrop.decorations.DecorationLayer;
import solarview.systemScreen.SystemScreen;
import spaceship.PlayerShipController;
import spaceship.Spaceship;
import spaceship.stats.SpaceshipAnalyzer;
import view.ViewScene;
import vmo.Game;

public class SolarScene extends SceneBase implements MyListener, Solar_Interface, Callback {

	static Solar_Interface solarInt;

	public static Solar_Interface getInterface() {
		return solarInt;
	}

	Spaceship playerShip;
	MouseHook mouseHook;
	SolarRenderer renderer;
	SolarGUI GUI;
	SolarController controller;
	private WarpController warpRenderer;
	PlayerShipController shipController;
	float clock;
	Screen screen;
	StarScape starscape;
	DecorationLayer decorationLayer;
	float incrementCounter;
	
	private boolean warpCheck(Spaceship spaceship)
	{
		if (spaceship.getWarpHandler()!=null)
		{
			if (spaceship.getWarpHandler().getCharge()>=100 && spaceship.getWarpHandler().flightElapsed())
			{
				Vec2i origin=Universe.getInstance().getcurrentSystem().getPosition();
				Universe.getInstance().getSystem().getEntities().remove(spaceship);
				StarSystem system=Universe.getInstance().getSystem(spaceship.getWarpHandler().getDestination().x, spaceship.getWarpHandler().getDestination().y);
				
				Universe.getInstance().setSystem(system);
				system.arrival();
				Universe.getInstance().getSystem().getEntities().add(spaceship);
			
				Vec2i destination=Universe.getInstance().getcurrentSystem().getPosition();
				
				int x=(origin.x-destination.x)*24;
				int y=(destination.y-origin.y)*24;
				Vec2f v=new Vec2f(x,y); v.normalize();
				v.x*=24;
				v.y*=24;
				v.x=(int)v.x;
				v.y=(int)v.y;
				spaceship.setPosition(v);
				spaceship.setWarpHandler(null);
				return true;
			}
		}
		return false;
	}
	
	public SolarScene(int r, Spaceship spaceship) {

		if (spaceship.getShipStats()==null)
		{
			spaceship.setShipStats(new SpaceshipAnalyzer().generateStats(spaceship));
		}
		boolean warp=warpCheck(spaceship);
		incrementCounter = 1.0F;
		solarInt = this;
		playerShip = spaceship;
		shipController = new PlayerShipController(Universe.getInstance().getSystem());
		playerShip.setShipController(shipController);
		StarSystem system=Universe.getInstance().getcurrentSystem();
		system.systemEntry();
		renderer = new SolarRenderer(Universe.getInstance().getSystem());
		controller = new SolarController(Universe.getInstance().getSystem(),playerShip);
		GUI = new SolarGUI(controller.getMessageQueue());
		GUI.generate(spaceship, this);
		starscape = new StarScape();
		decorationLayer=new DecorationLayer(Universe.getInstance().getSystem().getName());
		renderer.setCurrentPosition(playerShip.getPosition());
		starscape.setCurrentPosition(playerShip.getPosition());
		decorationLayer.setCurrentPosition(playerShip.getPosition());
		((SpriteRotatable) (playerShip.getSpriteObj())).setFacing(r);
		warpRenderer=new WarpController(renderer.getParticleEmitter(),playerShip);
		if (spaceship.getShipStats().getCrewStats().getNavigation()>=1)
		{
			generateWarpHelpers();
		}
		
		if (warp)
		{
			int dir=(int) Geometry.getAngle(0, 0, playerShip.getPosition().x,playerShip.getPosition().y*-1);
			if (dir>7){dir=dir-8;}
			((SpriteRotatable) (playerShip.getSpriteObj())).setFacing(dir);
			warpRenderer.warpIn();
		}
	}

	public SolarScene(int direction, Spaceship ship, int i) {
		this(direction,ship);
		warpRenderer.warpBurst();
	}

	private void generateWarpHelpers()
	{
		renderer.generateWarpHelpers(40, Universe.getInstance());
	}
	
	@Override
	public void Update(float dt) {

		GUI.update(dt);
		warpRenderer.update(dt);
		if (screen != null) {
			screen.update(dt);
		} else {
			shipController.controlUpdate(dt);
			if (clock <= 0) {
				
				if (shipController.canAct()) {
					if (shipController.control(playerShip)) {
						GUI.update();
						renderer.setCurrentPosition(playerShip.getPosition());
						starscape.setCurrentPosition(playerShip.getPosition());
						decorationLayer.setCurrentPosition(playerShip.getPosition());
						clock += 0.025F;
					}
					// clicking control
					if (mouseHook.buttonDown()) {
						handleClick();
						clock += 0.25F;

					}
				} else {
					controller.update();
					clock += 0.025F;
				}

			} else {
				clock -= dt;
				if (clock < 0) {
					clock = 0;
				}
			}
		}

	}

	private void handleClick() {
		Vec2f p = new Vec2f(mouseHook.getPosition().x, mouseHook.getPosition().y);
		if (p.x < 11) {
			p = MouseConverter.convertMousePointer(p, playerShip.getPosition(), renderer.getScale());

		}

	}

	@Override
	public void Draw() {

		GL20.glUseProgram(Game.m_pshader);

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		m_viewmatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniform1fv(m_variables[1], matrix44Buffer);
		starscape.draw(m_variables[1], m_variables[2], m_variables[0], matrix44Buffer);
		GL20.glUniform1fv(m_variables[1], matrix44Buffer);	
		GL20.glUniform4f(m_variables[0], 1.0F, 1.0F, 1.0F, 1);
		decorationLayer.draw(m_variables[1], m_variables[2], m_variables[0], matrix44Buffer);
		GL20.glUniform4f(m_variables[0], 1.0F, 1.0F, 1.0F, 1);
		renderer.solarDraw(m_variables[1], m_variables[2], m_variables[0], matrix44Buffer);
		GL20.glUniform4f(m_variables[0], 1.0F, 1.0F, 1.0F, 1);
		GUI.draw(m_variables[1], m_variables[2], m_variables[0], matrix44Buffer);
		if (screen != null) {
			screen.draw(matrix44Buffer, SceneBase.getVariables()[2]);
		}
	}

	@Override
	public void start(MouseHook mouse) {

		mouseHook = mouse;
		GUI.start(mouseHook);
	}

	@Override
	public void end() {
		if (playerShip.getShipStats()!=null)
		{
			playerShip.getShipStats().runDecompose();
			new SpaceshipAnalyzer().decomposeResources(playerShip.getShipStats(), playerShip);
			playerShip.setShipStats(null);		
		}

		playerShip.setShipController(null);
		GUI.discard(mouseHook);
		renderer.end();

		if (screen != null) {
			screen.discard(mouseHook);
		}
		solarInt = null;

		starscape.discard();
		decorationLayer.discard();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub
		switch (ID) {
		case 0:
			// exit to view screen
			playerShip.setWarpHandler(null);
			Game.sceneManager.SwapScene(new ViewScene());
			break;

		case 2:
			// magnify
			if (renderer.getScale() > 1) {
				renderer.setScale(renderer.getScale() / 2);
				starscape.setScale(renderer.getScale() / 2);
				decorationLayer.setScale(decorationLayer.getScale() / 2);
			}
			break;

		case 1:
			// reduce
			if (renderer.getScale() < 8) {
				renderer.setScale(renderer.getScale() * 2);
				starscape.setScale(renderer.getScale() * 2);
				decorationLayer.setScale(decorationLayer.getScale() * 2);	
			}
			break;
		case 3:
			setScreen(new SystemScreen(playerShip, this));
			break;
		}
	}

	@Override
	public void setScreen(Screen screen) {

		if (this.screen == null) {
			int values[] = new int[5];
			// values[0]=m_textureIds[6];
			values[1] = GUI.getTextures()[0];
			values[2] = GUI.getTextures()[1];
			// values[3]=m_textureIds[8];
			values[4] = m_variables[0];
			this.screen = screen;
			this.screen.initialize(values, this);
			this.screen.start(mouseHook);
			this.screen = screen;
		}
	}

	@Override
	public void Callback() {

		if (screen != null) {
			screen.discard(mouseHook);
			screen = null;
		}
	}

	@Override
	public void updateGUI() {
		GUI.update();
	}

}
