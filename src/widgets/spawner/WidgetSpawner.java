package widgets.spawner;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.Actor;
import actor.NPC;
import faction.FactionLibrary;
import nomad.Universe;
import shared.ParserHelper;
import shared.Vec2f;
import shared.Vec2i;
import widgets.Widget;
import zone.Zone;
import zone.TileDef.TileMovement;

public class WidgetSpawner extends Widget {

	private int spawnIDs[];
	private int numSpawns;
	private Spawn_Data data[];
	private int radius;
	private String script;
	
	public WidgetSpawner(Element element)
	{
		isVisionBlocking=false;
		isWalkable=true;
		NodeList nodes=element.getChildNodes();
		for (int i=0;i<nodes.getLength();i++)
		{
			if (nodes.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element e=(Element)nodes.item(i);
				if (e.getTagName().equals("spawns"))
				{
					genSpawnData(e);
				}
				if (e.getTagName().equals("script"))
				{
					script=e.getAttribute("value");
				}
				if (e.getTagName().equals("radius"))
				{
					radius=Integer.parseInt(e.getAttribute("value"));
				}
				if (e.getTagName().equals("spawnLimit"))
				{
					spawnIDs=new int[Integer.parseInt(e.getAttribute("value"))];
					for (int j=0;j<spawnIDs.length;j++)
					{
						spawnIDs[j]=-1;
					}
				}
			}
		}
	}
	
	private void genSpawnData(Element e)
	{
		int c=Integer.parseInt(e.getAttribute("count"));
		data=new Spawn_Data[c];
		NodeList children=e.getElementsByTagName("data");
		data=new Spawn_Data[c];
		for (int i=0;i<c;i++)
		{
			Element d=(Element)children.item(i);
			data[i]=new Spawn_Data(d);
		}
	}
	
	@Override
	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.write(18);
		
		commonSave(dstream);
		
		//save spawn ids
		dstream.writeInt(spawnIDs.length);
		for (int i=0;i<spawnIDs.length;i++)
		{
			dstream.writeInt(spawnIDs[i]);
		}
		//save numpspawned
		dstream.writeInt(numSpawns);
		//save data
		dstream.writeInt(data.length);
		for (int i=0;i<data.length;i++)
		{
			data[i].save(dstream);
		}
		//save radius
		dstream.writeInt(radius);
		//save script
		ParserHelper.SaveString(dstream, script);
	}
	

	public WidgetSpawner(DataInputStream dstream) throws IOException
	{
		isVisionBlocking=false;
		isWalkable=true;
		commonLoad(dstream);
		
		int c=dstream.readInt();
		spawnIDs=new int[c];
		for (int i=0;i<c;i++)
		{
			spawnIDs[i]=dstream.readInt();
		}
		numSpawns=dstream.readInt();
		c=dstream.readInt();
		data=new Spawn_Data[c];
		for (int i=0;i<c;i++)
		{
			data[i]=new Spawn_Data();
			data[i].load(dstream);
		}
		radius=dstream.readInt();
		script=ParserHelper.LoadString(dstream);
	}

	@Override
	public void Regen(long clock, Zone zone)
	{
		checkLive(zone.getActors());
		
		checkSpawn(zone);
	}

	
	private void checkLive(ArrayList<Actor> list)
	{
		if (numSpawns>0)
		{
			for (int j=0;j<spawnIDs.length;j++)
			{
				if (spawnIDs[j]>=0)
				{
					boolean b=false;
					for (int i=0;i<list.size();i++)
					{
						if (list.get(i).getUID()==spawnIDs[j])
						{
							b=true;
							break;
						}
					}
					if (b==false)
					{
						numSpawns--;
						spawnIDs[j]=-1;
					}
				}
			}	
		}
	}
	
	private void checkSpawn(Zone zone)
	{
		if (checkScript() && numSpawns==0)
		{
			Vec2i p=null;
			for (int i=0;i<zone.getWidth();i++)
			{
				for (int j=0;j<zone.getHeight();j++)
				{
					if (zone.getTile(i,j)!=null && zone.getTile(i, j).getWidgetObject()==this)
					{
						p=new Vec2i(i,j);
					}
				}
			}
			for (int i=0;i<data.length;i++)
			{
				spawn(zone,data[i],p);
			}
		}
	}
	
	private void spawn(Zone zone,Spawn_Data data,Vec2i p)
	{
		//roll
		int r=Universe.getInstance().m_random.nextInt(100);
		if (r<data.getChance())
		{
			int count=data.getMinCount()+Universe.getInstance().m_random.nextInt(data.getMaxCount()-data.getMinCount());
			Document doc=ParserHelper.LoadXML("assets/data/npcs/"+data.getFilename()+".xml");
		    Element n=(Element)doc.getFirstChild();
			NPC npc=new NPC(n,new Vec2f(0,0),data.getFilename());
			for (int i=0;i<count;i++)
			{
				placeNPC(zone,npc,p);	
			}		
		}
	}
	
	private void placeNPC(Zone zone,NPC npc,Vec2i p)
	{
		while (true)
		{

			int x=Universe.m_random.nextInt(radius*2)-radius+p.x;

			int y=Universe.m_random.nextInt(radius*2)-radius+p.y;
			
			if (zone.getTile(x, y)!=null && zone.getTile(x, y).getDefinition().getMovement()==TileMovement.WALK)
			{
				NPC nunpc=new NPC(npc,new Vec2f(x,y));
				zone.getActors().add(nunpc);
				nunpc.setCollisioninterface(zone);
				for (int i=0;i<spawnIDs.length;i++)
				{
					if (spawnIDs[i]==-1)
					{
						spawnIDs[i]=nunpc.getUID();
						break;
					}
				}
				numSpawns++;
				break;
			}
		}
	}
	
	private boolean checkScript()
	{
		Globals globals = JsePlatform.standardGlobals();
	
		try
		{
			LuaValue luaScript = globals.load(new FileReader("assets/data/scripts/" + script + ".lua"), "main.lua");
			 luaScript.call();
			LuaValue factionlibrary = CoerceJavaToLua.coerce(FactionLibrary.getInstance());
			LuaValue player = CoerceJavaToLua.coerce(Universe.getInstance().getPlayer());
			LuaValue mainFunc = globals.get("main");
			LuaValue returnVal = mainFunc.call(factionlibrary, player);
			return (boolean) CoerceLuaToJava.coerce(returnVal, Boolean.class);
		}
		catch (Exception e)
		{  
	        e.printStackTrace();  
		}
		return false;
	}
}
