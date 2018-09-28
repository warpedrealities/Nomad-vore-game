package nomad.universe;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.player.CompanionTool;
import actor.player.Player;
import actorRPG.npc.NPCStatblockLibrary;
import description.BodyLoader;
import faction.FactionLibrary;
import nomad.StarSystem;
import nomad.universe.salvageShip.SalvageShip;
import shared.FileTools;
import shared.ParserHelper;
import shared.Vec2f;
import shop.ShopList;
import spaceship.Spaceship;
import vmo.Config;
import vmo.Game;

public class UniverseStateChanger {

	private Universe universe;
	
	public UniverseStateChanger(Universe universe)
	{
		this.universe=universe;	
	}
	
	public void LoadUniverse()
	{
		universe.getStarSystems().clear();
		Document doc=ParserHelper.LoadXML("assets/data/universe.xml");
		//read through the top level nodes
		Element root=doc.getDocumentElement();
	    Element n=(Element)doc.getFirstChild();
		NodeList children=n.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
			Node node=children.item(i);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)node;
				if (Enode.getTagName()=="System")
				{
					universe.getStarSystems().add(new StarSystem(node.getTextContent(),
							Integer.parseInt(Enode.getAttribute("x")),
							Integer.parseInt(Enode.getAttribute("y"))
							));
				}
								
			}	
		}
	}
	
	private boolean versionCheck(int version)
	{
		if (version!=Config.VERSION)
		{
			return true;
		}
		return false;
	}

	private void versionShift(String filename) throws IOException
	{
		universe.setSaveName(null);
		

		universe.getUIDGenerator().reset();
		//load player
		//load player
		Player player=new Player();
		universe.setPlayer(player);
		player.Load(filename);
		FactionLibrary.getInstance().clean();
		//remove flags from player
		player.getFlags().clear();
		player.getEncyclopedia().generateEntries();
		new CompanionTool().removeAllCompanions(player);
		//move player to the first area.
		if (!salvageShip(filename))
		{
			startGame(true);
		}		
		
		File file=new File("saves/"+filename);
		FileTools.deleteFolder(file);
		file.mkdir();
	}
		
	
	private boolean salvageShip(String filename) throws IOException {
		SalvageShip salvager=new SalvageShip();
		Spaceship ship=salvager.salvageShip(filename);
		if (ship!=null)
		{
			LoadUniverse();
			ship.setUID(-1);
			
			startGame(true);
			
			universe.setCurrentEntity(ship);
			universe.setCurrentZone(ship.getZone(0));
			ship.setPosition(new Vec2f(0,-20));
			universe.getCurrentStarSystem().getEntities().add(ship);
			
			return true;
		}
		return false;
	}

	public void loadGame(String filename, boolean forceReset) throws IOException
	{
		File file=new File("saves/"+filename+"/"+"verse.sav");
		if (file.exists()==false)
		{
			file.createNewFile();
		}
		FileInputStream fstream=new FileInputStream(file);
		DataInputStream dstream=new DataInputStream(fstream);


		//load system list
		LoadUniverse();	
		//version
		int version=dstream.readInt();
		if (versionCheck(version)||forceReset)
		{
			versionShift(filename);	
			dstream.close();
			return;
		}
		//now load time
		universe.setWorldClock(dstream.readLong());

		Game.sceneManager.getConfig().setVerboseCombat(dstream.readBoolean());
		Game.sceneManager.getConfig().setDisableAutosave(dstream.readBoolean());

		NPCStatblockLibrary.getInstance().resetThreat();
		//load current system name
		String s=ParserHelper.LoadString(dstream);
		//find system
		for (int i=0;i<universe.getStarSystems().size();i++)
		{
			if (universe.getStarSystems().get(i).getName().equals(s))
			{
				universe.setCurrentStarSystem(universe.getStarSystems().get(i));
				break;
			}
		}

		//load current entity name
		String e=ParserHelper.LoadString(dstream);


		//load current zone
		String z=ParserHelper.LoadString(dstream);
	
		//load shops
		ShopList list=ShopList.getInstance();
		ShopList.getInstance().load(dstream);
		int safety=dstream.readInt();
		//load factions
		FactionLibrary factions=FactionLibrary.getInstance();
		factions.load(dstream);	
		
		//generate system
		universe.getCurrentStarSystem().GenerateSystem(false);
		universe.getCurrentStarSystem().load(filename);
		
		
		//find current entity
		StarSystem currentStarSystem=universe.getCurrentStarSystem();
		for (int i=0;i<currentStarSystem.entitiesInSystem.size();i++)
		{
			if (currentStarSystem.entitiesInSystem.get(i).getName().equals(e))
			{
				universe.setCurrentEntity(currentStarSystem.entitiesInSystem.get(i));
				break;
			}
		}	
		
		//generate entity	
		universe.getCurrentEntity().Generate();
		universe.setCurrentZone(universe.getCurrentEntity().getZone(z));
		//generate zone
		universe.getCurrentZone().LoadZone();
		
		//load player
		universe.setPlayer(new Player());
		universe.getPlayer().Load(filename);

	
		safety=dstream.readInt();
		CompanionTool.loadCompanions(universe.getPlayer(), universe.getCurrentZone());
		universe.getUIDGenerator().reset();
		universe.getUIDGenerator().load(dstream);	
		dstream.close();
		fstream.close();
	}
	
	public void startGame(boolean worldOnly)
	{
		Document doc=ParserHelper.LoadXML("assets/data/start.xml");
		Element root=doc.getDocumentElement();
		Element n=(Element)doc.getFirstChild();	
		NodeList children=n.getChildNodes();

		List<StarSystem> starSystems=universe.getStarSystems();
		for (int i=0;i<children.getLength();i++)
		{
			Node N=children.item(i);
			if (N.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)N;
				//run each step successively
				if (Enode.getTagName().equals("system"))
				{
					//set starting system
					for (int j=0;j<starSystems.size();j++)
					{
						if (starSystems.get(j).getName().equals(Enode.getAttribute("value")))
						{
							universe.setCurrentStarSystem(starSystems.get(j));
							break;
						}
					}
					universe.getCurrentStarSystem().GenerateSystem(true);
				}
				if (Enode.getTagName().equals("world"))
				{
					//set starting starting world
					for (int j=0;j<universe.getCurrentStarSystem().getEntities().size();j++)
					{
						if (universe.getCurrentStarSystem().getEntities().get(j).getName().equals(Enode.getAttribute("value")))
						{
							universe.setCurrentEntity(universe.getCurrentStarSystem().getEntities().get(j));
							universe.getCurrentEntity().Generate();
						}
					}
					
				}
				if (Enode.getTagName().equals("zone"))
				{
					//set starting zone
					universe.setCurrentZone(universe.getCurrentEntity().getZone(Enode.getAttribute("value")));
					universe.getCurrentZone().LoadZone();	
				}
				if (!worldOnly && Enode.getTagName().equals("position"))
				{
					//set starting coordinates		
					universe.setPlayer(new Player(new Vec2f(Integer.parseInt(Enode.getAttribute("x")),
							Integer.parseInt(Enode.getAttribute("y")))));
				}
				if (worldOnly && Enode.getTagName().equals("position"))
				{
					//set starting coordinates		
					universe.getPlayer().setPosition(new Vec2f(Integer.parseInt(Enode.getAttribute("x")),
							Integer.parseInt(Enode.getAttribute("y"))));
				}
				if (!worldOnly && Enode.getTagName().equals("item"))
				{
					//set items
					String value=Enode.getAttribute("value");
					universe.getPlayer().getInventory().AddItem(universe.getLibrary().getItem(value));		
				}
				if (!worldOnly && Enode.getTagName().equals("appearance"))
				{
					BodyLoader.loadBody(Enode, universe.getPlayer());
				}
			}
		}
	}
}
