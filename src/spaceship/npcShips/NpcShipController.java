package spaceship.npcShips;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import faction.Faction;
import faction.FactionLibrary;
import nomad.FlagField;
import nomad.universe.Universe;
import rendering.SpriteRotatable;
import shared.ParserHelper;
import shared.Vec2f;
import solarview.spaceEncounter.EncounterEntities.combatControllers.CombatController;
import solarview.spaceEncounter.EncounterEntities.combatControllers.NpcCombatController;
import spaceship.ShipController;
import spaceship.Spaceship;
import spaceship.ShipController.scriptEvents;
import view.ZoneInteractionHandler;

public class NpcShipController implements ShipController {

	private Faction faction;
	private String[] scripts;
	private FlagField flags;
	private NpcShipSpaceAI ai;
	private int experience;
	
	private void commonConstruction(Element element)
	{
		flags=new FlagField();		
		NodeList children=element.getChildNodes();
		scripts=new String[6];
		for (int i=0;i<children.getLength();i++)
		{
			if (children.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element e=(Element)children.item(i);
				
				if (e.getTagName().equals("faction"))
				{
					faction=FactionLibrary.getInstance().getFaction(e.getAttribute("value"));
				}
				if (e.getTagName().equals("systemEntry"))
				{
					scripts[scriptEvents.systemEntry.getValue()]=e.getAttribute("value");
				}
				if (e.getTagName().equals("contact"))
				{
					scripts[scriptEvents.contact.getValue()]=e.getAttribute("value");
				}
				if (e.getTagName().equals("ai"))
				{
					scripts[scriptEvents.ai.getValue()]=e.getAttribute("value");
					ai=new NpcShipSpaceAI(scripts[scriptEvents.ai.getValue()],flags,faction);
				}
				if (e.getTagName().equals("combat"))
				{
					scripts[scriptEvents.combat.getValue()]=e.getAttribute("value");
				}
				if (e.getTagName().equals("victory"))
				{
					scripts[scriptEvents.victory.getValue()]=e.getAttribute("value");
				}
				if (e.getTagName().equals("loss"))
				{
					scripts[scriptEvents.loss.getValue()]=e.getAttribute("value");
				}
				if (e.getTagName().equals("experienceReward"))
				{
					experience=Integer.parseInt(e.getAttribute("value"));
				}
			}
		}

	}
	
	public NpcShipController(Element element)
	{
		commonConstruction(element);
	}
	
	public NpcShipController()
	{
		
	}
	
	public void setShip(Spaceship ship)
	{
		ai.setShip(ship);
		if (ship.getBaseStats()==null)
		{
			ship.Generate();
		}
	}
	
	public NpcShipController(String filename) {
		Document doc0 = ParserHelper.LoadXML("assets/data/shipControllers/" + filename + ".xml");
		Element root0 = doc0.getDocumentElement();
		Element n0 = (Element) doc0.getFirstChild();
		commonConstruction(n0);
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException
	{
		ParserHelper.SaveString(dstream,faction.getFilename());
		flags.save(dstream);
		
		for (int i=0;i<6;i++)
		{
			if (scripts[i]!=null)
			{
				dstream.writeBoolean(true);
				ParserHelper.SaveString(dstream, scripts[i]);
			}
			else
			{
				dstream.writeBoolean(false);
			}
		}
		
		dstream.writeInt(experience);
	}
	
	@Override
	public void load(DataInputStream dstream) throws IOException
	{
		faction=FactionLibrary.getInstance().getFaction(ParserHelper.LoadString(dstream));
		flags=new FlagField();
		flags.load(dstream);
		scripts=new String[6];
		for (int i=0;i<6;i++)
		{
			if (dstream.readBoolean())
			{
				scripts[i]=ParserHelper.LoadString(dstream);
			}
		}
		if (scripts[ShipController.scriptEvents.ai.getValue()]!=null)
		{
			ai=new NpcShipSpaceAI(scripts[scriptEvents.ai.getValue()],flags,faction);
		}

		experience=dstream.readInt();
	}
	
	@Override
	public void update(Spaceship ship) {
		if (ai!=null)
		{
			if (!ai.isBusy())
			{
				ai.update(ship);
			}
			else
			{
				ai.decrementBusy();
			}
		}
	
	}

	@Override
	public boolean canAct() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CombatController getCombat() {
		return new NpcCombatController(scripts[scriptEvents.combat.getValue()]);
	}

	@Override
	public Faction getFaction() {
		return faction;
	}
	public FlagField getflags()
	{
		return flags;
	}

	@Override
	public void event(scriptEvents event)
	{
		if (scripts[event.getValue()]!=null)
		{
			Globals globals=JsePlatform.standardGlobals();;
			try {

				LuaValue script=globals.load(
						new FileReader("assets/data/shipControllers/scripts/" + 
								scripts[event.getValue()] + ".lua"), "main.lua");
				script.call();
				LuaValue luaScript = CoerceJavaToLua.coerce(ai);
				LuaValue luaSense = CoerceJavaToLua.coerce(ai.getSense());
				LuaValue luacontrol = globals.get("main");
				if (!luacontrol.isnil()) {
					luacontrol.call(luaScript,luaSense);
				} else {
					System.out.println("Lua function not found");
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	}

	public int getExperience() {
		return experience;
	}
	

}
