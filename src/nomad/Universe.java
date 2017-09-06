package nomad;


import item.ItemLibrary;
import nomad.integrityChecking.SaveIntegrityCheck;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.player.CompanionTool;
import actor.player.Player;
import description.BodyLoader;
import faction.FactionLibrary;


import actorRPG.NPCStatblockLibrary;


import shared.FileTools;
import shared.ParserHelper;
import shared.Vec2f;
import shop.ShopList;
import spaceship.Spaceship;
import vmo.Config;
import vmo.Game;
import vmo.GameManager;
import worldgentools.ZoneBuildTools;
import zone.Zone;

public class Universe extends GameManager 
{
	private UIDGenerator uidGenerator;

	
	public static Universe universeInstance;

	public ArrayList <StarSystem> starSystems;
	public StarSystem currentStarSystem;
	private Entity currentEntity;
	public Zone currentZone;
	public Player player;
	String saveName;
	
	boolean isPlaying;
	
	ItemLibrary itemLibrary;
	
	Preferences preferences;

	static public Universe getInstance()
	{
		return universeInstance;
	}
	
	public UIDGenerator getUIDGenerator() {
		if (uidGenerator==null)
		{
			uidGenerator=new UIDGenerator();
		}
		return uidGenerator;
	}

	public Preferences getPrefs()
	{
		return preferences;
	}
	
	public Universe()
	{
		universeInstance=this;
		itemLibrary=new ItemLibrary();
		preferences=new Preferences();
	}

	public ItemLibrary getLibrary()
	{
		return itemLibrary;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	void LoadUniverse()
	{
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
					starSystems.add(new StarSystem(node.getTextContent(),
							Integer.parseInt(Enode.getAttribute("x")),
							Integer.parseInt(Enode.getAttribute("y"))
							));
				}
								
			}	
		}
	}
	
	
	
	@Override
	public void Newgame()
	{
		getUIDGenerator().reset();
		saveName=null;
		starSystems=new ArrayList<StarSystem>();
		//load system list
		LoadUniverse();
		
		Document doc=ParserHelper.LoadXML("assets/data/start.xml");
		Element root=doc.getDocumentElement();
		Element n=(Element)doc.getFirstChild();	
		NodeList children=n.getChildNodes();
		FactionLibrary.getInstance().clean();
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
							currentStarSystem=starSystems.get(j);
							break;
						}
					}
					currentStarSystem.GenerateSystem(true);
				}
				if (Enode.getTagName().equals("world"))
				{
					//set starting starting world
					for (int j=0;j<currentStarSystem.getEntities().size();j++)
					{
						if (currentStarSystem.getEntities().get(j).getName().equals(Enode.getAttribute("value")))
						{
							currentEntity=currentStarSystem.getEntities().get(j);
							currentEntity.Generate();
						}
					}
					
				}
				if (Enode.getTagName().equals("zone"))
				{
					//set starting zone
					currentZone=currentEntity.getZone(Enode.getAttribute("value"));
					currentZone.LoadZone();	
				}
				if (Enode.getTagName().equals("position"))
				{
					//set starting coordinates		
					player=new Player(new Vec2f(Integer.parseInt(Enode.getAttribute("x")),
							Integer.parseInt(Enode.getAttribute("y"))));
				}
				if (Enode.getTagName().equals("item"))
				{
					//set items
					String value=Enode.getAttribute("value");
					player.getInventory().AddItem(itemLibrary.getItem(value));		
				}
				if (Enode.getTagName().equals("appearance"))
				{
					BodyLoader.loadBody(Enode, player);
				}
			}
		}
		

	}
	

	
	public void GameOver()
	{
		isPlaying=false;
	}
	
	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public boolean getPlaying()
	{
		return isPlaying;
	}
	
	public void setZone(Zone zone)
	{
		currentZone=zone;
	}
	
	public void setCurrentEntity(Entity entity)
	{
		currentEntity=entity;
	}
	
	public Entity getCurrentWorld(Spaceship ship)
	{
		for (int i=0;i<currentStarSystem.entitiesInSystem.size();i++)
		{
				if (currentStarSystem.entitiesInSystem.get(i).entityPosition.x==ship.entityPosition.x
						&&
						currentStarSystem.entitiesInSystem.get(i).entityPosition.y==ship.entityPosition.y
						)
				{
					if (currentStarSystem.entitiesInSystem.get(i).getClass().getName().contains("World"))
					{
						return (World)currentStarSystem.entitiesInSystem.get(i);			
					}
					if (currentStarSystem.entitiesInSystem.get(i).getClass().getName().contains("Spaceship"))
					{
						return currentStarSystem.entitiesInSystem.get(i);			
					}
					if (currentStarSystem.entitiesInSystem.get(i).getClass().getName().contains("Station"))
					{
						return currentStarSystem.entitiesInSystem.get(i);
					}
				}
		}
		return null;
		
	}
	
	public Entity getCurrentEntity() {
		return currentEntity;
	}

	public Zone getZone(String name)
	{
		//check existing world
		Zone z=null;
		if (currentZone!=null)
		{
			z=currentEntity.getZone(name,(int)currentZone.getPosition().x,(int)currentZone.getPosition().y);
		}
		else
		{
			z=currentEntity.getZone(name);
		}
		if (z!=null)
		{
			//currentEntity=(Entity)z.zoneEntity;
			return z;
		}
		
		for (int i=0;i<currentStarSystem.entitiesInSystem.size();i++)
		{
			if (currentEntity!=currentStarSystem.entitiesInSystem.get(i))
			{
				if (currentStarSystem.entitiesInSystem.get(i).entityPosition.x==currentEntity.entityPosition.x
						&&
						currentStarSystem.entitiesInSystem.get(i).entityPosition.y==currentEntity.entityPosition.y
						)
				{
					for (int j=0;j<currentStarSystem.entitiesInSystem.get(i).getNumZones();j++)
					{
						if (currentStarSystem.entitiesInSystem.get(i).getZone(j).getName().contains(name))
						{
							currentEntity=currentStarSystem.entitiesInSystem.get(i);
							currentZone=currentStarSystem.entitiesInSystem.get(i).getZone(j);
							return currentStarSystem.entitiesInSystem.get(i).getZone(j);
						}
					}
				}
	
			}
		}
		return null;
	}

	public StarSystem getSystem()
	{
		return currentStarSystem;
	}
	public void setSystem(StarSystem system)
	{
		currentStarSystem=system;
	}

	public StarSystem getSystem(int x, int y)
	{
		for (int i=0;i<starSystems.size();i++)
		{
			if (starSystems.get(i).getPosition().x==x && starSystems.get(i).getPosition().y==y)
			{
				return starSystems.get(i);
			}
		}
		return null;
	}
	void copyTemp(String filename) throws IOException
	{
		File temp=new File("saves/temp");
		File newSave=new File("saves/"+filename);
		
		//copy all files from savename folder
		//and copy them to the filename folder
		FileTools.copyFolder(temp, newSave);		
	}
	
	
	void saveCopy(String filename) throws IOException
	{
		if (saveName!=null)
		{
			if (!saveName.equals(filename))
			{
				File oldSave=new File("saves/"+saveName);
				File newSave=new File("saves/"+filename);
				
				//copy all files from savename folder
				//and copy them to the filename folder
				FileTools.copyFolderOverwrite(oldSave, newSave);
			}		
		}
	}
	
	public String getSaveName()
	{
		return saveName;
	}
	
	public char autoSave() throws IOException
	{
		if (!Game.sceneManager.getConfig().isDisableAutosave())
		{
			File file=new File("saves/autosave");
			if (!file.exists())
			{
				
				file.mkdir();
			}
			if (save("autosave"))
			{
				return 2;
			}
			else
			{
				return 1;
			}
	
		}
		return 0;
	}
	
	void buildTemp()
	{
		File file = new File("saves/temp");
		if (file.exists()) {
			FileTools.deleteFolder(file);
			file.mkdir();
		} else {
			file.mkdir();
		}
	}

	private void saveRoutine(String filename) throws IOException
	{
	
		
		File file=new File("saves/"+filename+"/verse.sav");
		if (file.exists()==false)
		{
			file.createNewFile();
		}
		FileOutputStream fstream=new FileOutputStream(file);
		DataOutputStream dstream=new DataOutputStream(fstream);
		
		//save version
		dstream.writeInt(Config.VERSION);
		//save time
		dstream.writeLong(worldClock);

		//save config
		dstream.writeBoolean(Game.sceneManager.getConfig().isVerboseCombat());
		dstream.writeBoolean(Game.sceneManager.getConfig().isDisableAutosave());
		//save faction library
		FactionLibrary.getInstance().save(dstream);		
		//save current system name
		ParserHelper.SaveString(dstream, currentStarSystem.getName());
		//save current entity name
		ParserHelper.SaveString(dstream, currentEntity.getName());
		//save current zone
		ParserHelper.SaveString(dstream, currentZone.getName());
		//save shops
		ShopList.getInstance().save(dstream);

		//save player
		player.Save(filename);
		//save systems		
		dstream.writeInt(starSystems.size());
		for (int i=0;i<starSystems.size();i++)
		{
			starSystems.get(i).Save(filename);
		}
		//save ship uid
		uidGenerator.save(dstream);
		
		dstream.close();
		fstream.close();
	}
	
	public boolean save(String filename) throws IOException
	{
		buildTemp();
//		String saveRetain=saveName;
		saveRoutine("temp");
		SaveIntegrityCheck check=new SaveIntegrityCheck("temp",this);
		if (check.isOkay())
		{
			if (!filename.equals(saveName) && filename.length()>0)
			{
				File file=new File("saves/"+filename);
				FileTools.deleteFolder(file);
				if (!file.mkdir())
				{
					
					throw new IOException("failed to create new save directory");
				}
			}
			saveCopy(filename);
			copyTemp(filename);
			saveName=filename;		
			return true;
		}
		else
		{
			return false;
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
		saveName=null;
		if (uidGenerator==null)
		{
			uidGenerator=new UIDGenerator();
		}

		uidGenerator.reset();
		//load player
		//load player
		player=new Player();
		player.Load(filename);
		
		//remove flags from player
		player.getFlags().clear();
		//move player to the first area.
		Document doc=ParserHelper.LoadXML("assets/data/start.xml");
		Element root=doc.getDocumentElement();
		Element n=(Element)doc.getFirstChild();	
		NodeList children=n.getChildNodes();
		
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
							currentStarSystem=starSystems.get(j);
							break;
						}
					}
					currentStarSystem.GenerateSystem(true);
				}
				if (Enode.getTagName().equals("world"))
				{
					//set starting starting world
					for (int j=0;j<currentStarSystem.getEntities().size();j++)
					{
						if (currentStarSystem.getEntities().get(j).getName().equals(Enode.getAttribute("value")))
						{
							currentEntity=currentStarSystem.getEntities().get(j);
							currentEntity.Generate();
						}
					}
					
				}
				if (Enode.getTagName().equals("zone"))
				{
					//set starting zone
					currentZone=currentEntity.getZone(Enode.getAttribute("value"));
					currentZone.LoadZone();	
				}
				if (Enode.getTagName().equals("position"))
				{
					//set starting coordinates		
					player.setPosition(new Vec2f(Integer.parseInt(Enode.getAttribute("x")),Integer.parseInt(Enode.getAttribute("y"))));
				}

			}
		}
		
		isPlaying=true;
		File file=new File("saves/"+filename);
		FileTools.deleteFolder(file);
		file.mkdir();
		saveName=null;
	}
	
	public void Load(String filename, boolean forceReset) throws IOException
	{
		saveName=filename;
		File file=new File("saves/"+filename+"/"+"verse.sav");
		if (file.exists()==false)
		{
			file.createNewFile();
		}
		FileInputStream fstream=new FileInputStream(file);
		DataInputStream dstream=new DataInputStream(fstream);
		
		starSystems=new ArrayList<StarSystem>();
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
		worldClock=dstream.readLong();

		Game.sceneManager.getConfig().setVerboseCombat(dstream.readBoolean());
		Game.sceneManager.getConfig().setDisableAutosave(dstream.readBoolean());
		//load factions
		FactionLibrary.getInstance().load(dstream);	
		NPCStatblockLibrary.getInstance().resetThreat();
		//load current system name
		String s=ParserHelper.LoadString(dstream);
		//find system
		for (int i=0;i<starSystems.size();i++)
		{
			if (starSystems.get(i).getName().equals(s))
			{
				currentStarSystem=starSystems.get(i);
				break;
			}
		}
		//generate system
		currentStarSystem.GenerateSystem(false);
		currentStarSystem.load(filename);
		//load current entity name
		String e=ParserHelper.LoadString(dstream);
		for (int i=0;i<currentStarSystem.entitiesInSystem.size();i++)
		{
			if (currentStarSystem.entitiesInSystem.get(i).getName().equals(e))
			{
				currentEntity=currentStarSystem.entitiesInSystem.get(i);
				break;
			}
		}
		//find current entity
		//generate entity
		currentEntity.Generate();
		//load current zone
		String z=ParserHelper.LoadString(dstream);
		currentZone=currentEntity.getZone(z);
		//generate zone
		currentZone.LoadZone();
		//load shops
		ShopList.getInstance().load(dstream);
		uidGenerator=new UIDGenerator();
		uidGenerator.load(dstream);
		
		//load player
		player=new Player();
		player.Load(filename);
		isPlaying=true;
		//load ship uid

		
		CompanionTool.loadCompanions(player, currentZone);
		
		dstream.close();
		fstream.close();
	}

	public Zone getCurrentZone() {
		return currentZone;
	}

	public Entity getCurrentWorld() {

		return currentEntity;
	}

	public void setCurrentWorld(Entity currentWorld) {
		this.currentEntity = currentWorld;
	}

	public StarSystem getcurrentSystem() {
		return currentStarSystem;
		
	}
	
	
}
