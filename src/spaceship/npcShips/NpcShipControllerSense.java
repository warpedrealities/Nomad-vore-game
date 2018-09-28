package spaceship.npcShips;

import faction.Faction;
import nomad.FlagField;
import nomad.StarSystem;
import nomad.universe.Universe;
import spaceship.Spaceship;
import spaceship.boarding.BoardingHelper;
import spaceship.stats.SpaceshipAnalyzer;
import view.ViewScene;
import vmo.Game;

public class NpcShipControllerSense {

	private FlagField flags;
	private Faction faction;
	private StarSystem system;
	private NpcShipControllerSenseDetection detection;
	
	public NpcShipControllerSense(FlagField flags, Faction faction) {
		this.flags=flags;
		this.faction=faction;
		this.system=Universe.getInstance().getcurrentSystem();
		this.detection=new NpcShipControllerSenseDetection(faction,system);
	}

	public Spaceship getPlayer()
	{
		return (Spaceship)Universe.getInstance().getCurrentEntity();
	}

	public FlagField getFlags() {
		return flags;
	}

	public long getTime()
	{
		return Universe.getClock();
	}

	public Faction getFaction() {
		return faction;
	}
	
	public void spawnBoarders(String [] filenames)
	{
		
		new BoardingHelper(getPlayer()).addNPCs(filenames);
	}
	
	public void toView()
	{
		if (getPlayer().getShipStats()!=null)
		{
			new SpaceshipAnalyzer().decomposeResources(getPlayer().getShipStats(), getPlayer());
			getPlayer().setShipStats(null);
			getPlayer().setShipController(null);			
		}
		Game.sceneManager.SwapScene(new ViewScene());
	}

	public StarSystem getSystem() {
		return system;
	}

	public NpcShipControllerSenseDetection getDetection() {
		return detection;
	}

	
}
