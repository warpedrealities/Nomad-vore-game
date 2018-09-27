package spaceship.npcShips;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import dialogue.DialogueScene;
import dialogue.DialogueScene.dialogueOrigin;
import faction.Faction;
import nomad.Entity;
import nomad.FlagField;
import nomad.GameOver;
import nomad.StarSystem;
import nomad.universe.Universe;
import rendering.SpriteRotatable;
import shared.SceneBase;
import shared.Vec2f;
import solarview.SolarActionHandler;
import solarview.spaceEncounter.SpaceCombatInitializer;
import spaceship.Spaceship;
import spaceship.SpaceshipActionHandler;
import spaceship.stats.SpaceshipAnalyzer;
import view.ZoneInteractionHandler;
import vmo.Game;

public class NpcShipSpaceAI {
	private int busy;
	private Globals m_globals;
	private LuaValue m_script;
	private NpcShipControllerSense sense;
	private Spaceship ship;
	
	public NpcShipSpaceAI(String scriptName,FlagField flags,Faction faction)
	{
		sense=new NpcShipControllerSense(flags,faction);
		busy=0;
		m_globals = JsePlatform.standardGlobals();
		try {
			m_script = m_globals.load(new FileReader("assets/data/shipControllers/scripts/" + scriptName + ".lua"), "main.lua");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (LuaError e) {
			e.printStackTrace();
		}
	}
	
	public Vec2f getPosition()
	{
		return ship.getPosition();
	}
	
	public void reposition(int x, int y)
	{
		ship.setPosition(new Vec2f(x,y));
	}
	
	public void setShip(Spaceship ship)
	{
		this.ship=ship;
	}
	
	public void removeShip()
	{
		Universe.getInstance().getcurrentSystem().getEntities().remove(ship);
	}

	public void decrementBusy()
	{
		if (busy>0)
		{
			busy--;			
		}
	}
	
	public void update(Spaceship ship)
	{
		this.ship=ship;
		try {
			m_script.call();
			LuaValue luaScript = CoerceJavaToLua.coerce(this);
			LuaValue luaSense = CoerceJavaToLua.coerce(sense);
			LuaValue luacontrol = m_globals.get("main");
			if (!luacontrol.isnil()) {
				luacontrol.call(luaScript,luaSense);
			} else {
				System.out.println("Lua function not found");
			}
		} catch (LuaError e) {
			e.printStackTrace();
		}
	}

	public void doNothing(int time)
	{
		busy=time;
	}
	
	public void startFight()
	{
		new SpaceCombatInitializer((Spaceship)Universe.getInstance().getCurrentEntity(),ship).run();
	}
	
	public void startConversation(String filename)
	{
		DialogueScene scene=new DialogueScene(filename,dialogueOrigin.Space);
		scene.setShip(ship);
		Game.sceneManager.SwapScene(scene);
	}
	
	public void dock()
	{
		new SpaceshipActionHandler().join((Spaceship)Universe.getInstance().getCurrentEntity(),ship);
	}
	
	private Entity collisionCheck(int x, int y, Spaceship ship) {
		StarSystem currentSystem=sense.getSystem();
		for (int i = 0; i < currentSystem.getEntities().size(); i++) {
			if (currentSystem.getEntities().get(i) != ship) {
				int x0 = (int) currentSystem.getEntities().get(i).getPosition().x;
				int y0 = (int) currentSystem.getEntities().get(i).getPosition().y;
				if (x == x0 && y0 == y) {
					return currentSystem.getEntities().get(i);
				}
			}
		}

		return null;
	}	
	
	private boolean handleCollision(int x, int y, Spaceship ship) {
		Entity e = collisionCheck(x, y, ship);
		if (e != null && !Spaceship.class.isInstance(e)) {

			return true;
		}

		return false;
	}
	
	public boolean move(int v) {
		Vec2f p = ZoneInteractionHandler.getPos(v, ship.getPosition());
		if (!handleCollision((int) p.x, (int) p.y, ship)) {
			// drain fuel, move player
//			useFuel(ship);
			ship.setPosition(p);
			((SpriteRotatable) ship.getSpriteObj()).setFacing(v);
			busy += ship.getBaseStats().getMoveCost();
//			calcSolar(Universe.getInstance().getcurrentSystem(), ship);
			if (ship.getWarpHandler()!=null)
			{
				ship.setWarpHandler(null);
			}
			return true;
		}
		return false;
	}
	
	public boolean isBusy() {
			if (busy>0)
			{
				return true;
			}
			else
			{
				return false;
			}
	}

	public NpcShipControllerSense getSense() {
		return sense;
	}
	
}
