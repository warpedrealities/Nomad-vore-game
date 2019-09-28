package nomad.universe;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import actor.player.Player;
import entities.Entity;
import entities.StarSystem;
import faction.FactionLibrary;
import item.ItemLibrary;
import nomad.Preferences;
import nomad.UIDGenerator;
import nomad.integrityChecking.SaveIntegrityCheck;
import nomad.playerScreens.journal.JournalSystem;
import nomad.universe.actionBar.ActionBarData;
import nomad.universe.eventSystem.UniverseEventSystem;
import shared.FileTools;
import shared.ParserHelper;
import shop.ShopList;
import spaceship.Spaceship;
import vmo.Config;
import vmo.Game;
import vmo.GameManager;
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
	public UniverseEventSystem eventSystem;
	public JournalSystem journal;
	ActionBarData actionBarData;
	String saveName;
	String shipName;

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
		eventSystem = new UniverseEventSystem();
	}

	public ItemLibrary getLibrary()
	{
		return itemLibrary;
	}

	public Player getPlayer()
	{
		return player;
	}

	public StarSystem getCurrentStarSystem() {
		return currentStarSystem;
	}

	public void setCurrentStarSystem(StarSystem currentStarSystem) {
		this.currentStarSystem = currentStarSystem;
	}

	public ArrayList<StarSystem> getStarSystems() {
		return starSystems;
	}

	public void setCurrentZone(Zone currentZone) {
		this.currentZone = currentZone;
	}

	public void setSaveName(String saveName) {
		this.saveName = saveName;
	}

	@Override
	public void Newgame()
	{
		actionBarData = new ActionBarData();
		getUIDGenerator().reset();
		saveName=null;
		starSystems=new ArrayList<StarSystem>();
		FactionLibrary.getInstance().clean();
		//load system list
		UniverseStateChanger stateChanger=new UniverseStateChanger(this);
		stateChanger.LoadUniverse();
		stateChanger.startGame(false);
		journal = new JournalSystem();
		eventSystem.reset();
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
			if (currentStarSystem.entitiesInSystem.get(i).getPosition().x==ship.getPosition().x
					&&
					currentStarSystem.entitiesInSystem.get(i).getPosition().y==ship.getPosition().y
					)
			{
				if (currentStarSystem.entitiesInSystem.get(i).getClass().getName().contains("World"))
				{
					return currentStarSystem.entitiesInSystem.get(i);
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
				if (currentStarSystem.entitiesInSystem.get(i).getPosition().x==currentEntity.getPosition().x
						&&
						currentStarSystem.entitiesInSystem.get(i).getPosition().y==currentEntity.getPosition().y
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
			if (starSystems.get(i).getPosition().x == x && starSystems.get(i).getPosition().y == y
					&& starSystems.get(i).canVisit())
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

		//save current system name
		ParserHelper.SaveString(dstream, currentStarSystem.getName());
		//save current entity name
		ParserHelper.SaveString(dstream, currentEntity.getName());
		//save current zone
		ParserHelper.SaveString(dstream, currentZone.getName());
		//save shops
		ShopList list=ShopList.getInstance();
		ShopList.getInstance().save(dstream);
		//save player
		player.Save(filename);
		//save systems
		for (int i=0;i<starSystems.size();i++)
		{
			starSystems.get(i).Save(filename);
		}

		if (shipName != null) {
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, shipName);
		} else {
			dstream.writeBoolean(false);
		}

		dstream.writeInt(42);
		//save faction library
		FactionLibrary.getInstance().save(dstream);

		dstream.writeInt(42);
		//save ship uid
		uidGenerator.save(dstream);

		actionBarData.save(dstream);

		journal.save(dstream);

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

	public void Load(String filename, boolean forceReset) throws IOException
	{
		if (uidGenerator==null)
		{
			uidGenerator=new UIDGenerator();
		}
		FactionLibrary.getInstance().clean();
		starSystems=new ArrayList<StarSystem>();
		saveName=filename;

		//load system list
		UniverseStateChanger stateChanger=new UniverseStateChanger(this);
		stateChanger.LoadUniverse();
		//version
		stateChanger.loadGame(filename, forceReset);
		isPlaying=true;
		eventSystem.reset();

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

	public void setPlayer(Player player) {
		this.player=player;
	}

	public void setWorldClock(long clock) {
		worldClock=clock;
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

	public ActionBarData getActionBarData() {
		return actionBarData;
	}

	public UniverseEventSystem getEventSystem() {
		return eventSystem;
	}

	public String getShipName() {
		return shipName;
	}

	public void setShipName(String shipName) {
		this.shipName = shipName;
	}

	public JournalSystem getJournal() {
		return journal;
	}

}
