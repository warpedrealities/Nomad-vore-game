package widgets.capsules;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import nomad.StarSystem;
import nomad.Universe;
import nomad.World;
import shared.Geometry;
import shared.ParserHelper;
import shared.Vec2i;
import spaceship.Spaceship;
import spaceship.Spaceship.ShipState;
import spaceship.stats.SpaceshipAnalyzer;
import view.ViewScene;
import vmo.Game;
import zone.Zone;
import zone.TileDef.TileMovement;
import zone.Zone.zoneType;

public class CapsuleBehaviour {

	private WidgetCapsuleSystem capsule;
	private Spaceship spaceship;
	private SpaceshipAnalyzer analyzer;
	private World world;
	
	public CapsuleBehaviour(WidgetCapsuleSystem capsule, Spaceship spaceship) {
		this.capsule=capsule;
		this.spaceship=spaceship;
		this.analyzer=new SpaceshipAnalyzer();
		spaceship.setShipStats(this.analyzer.generateStats(spaceship));
	}
	
	public boolean sufficientFuel()
	{
		if (this.spaceship.getShipStats().getResource("FUEL")!=null &&
			this.spaceship.getShipStats().getResource("FUEL").getResourceAmount()>=this.capsule.getFuelCost())
		{
			return true;
		}
		return false;
	}
	
	
	
	public void finish()
	{
		analyzer.decomposeResources(spaceship.getShipStats(), spaceship);
		spaceship.setShipStats(null);
	}

	private boolean inSpace()
	{
		if (this.spaceship.getState()==ShipState.LAND || this.spaceship.getState()==ShipState.DOCK ||
			this.spaceship.getState()==ShipState.SHIPDOCK)
		{
			return false;
		}
		return true;	
	}
	
	public boolean canLaunch() {
		if (!inSpace())
		{
			ViewScene.m_interface.DrawText("cannot launch pod except in orbit");
			return false;
		}
		if (!inRange())
		{
			ViewScene.m_interface.DrawText("no destination in range of pod");
			return false;			
		}
		return true;
	}

	private boolean inRange() {
		StarSystem system=Universe.getInstance().getcurrentSystem();
		for (int i=0;i<system.getEntities().size();i++)
		{
			float d=system.getEntities().get(i).getPosition().getDistance(spaceship.getPosition());
			if (World.class.isInstance(system.getEntities().get(i)) &&
				d<1.5F)
			{
				world=(World)system.getEntities().get(i);
				
				return true;
			}
		}
		return false;
	}
	
	private boolean needLandingScreen(Spaceship ship, World world) {
		int visitedCount = 0;
		if (world.getNumZones() > 0) {
			for (int i = 0; i < world.getNumZones(); i++) {
				if (world.getZone(i) != null) {
					Zone zone = world.getZone(i);
					if (zone.getType() == zoneType.SURFACE && zone.getTiles() != null) {
						visitedCount++;
					}
				}
			}

		}
		if (visitedCount >= 2) {
			return true;
		}
		return false;
	}
	
	public boolean landing() {
		if (needLandingScreen(spaceship,world))
		{
			ViewScene.m_interface.setScreen(new CapsuleLandingScreen(this, world));
			return false;
		}
		else
		{
			defaultLand();
			finish();
			return true;
		}

	}

	public void performLanding(Zone zone)
	{
		if (zone.getTiles()==null)
		{
			zone.LoadZone();
		}
		capsule.setDeployed(true);
		spaceship.getShipStats().getResource("FUEL").setResourceAmount(spaceship.getShipStats().getResource("FUEL").getResourceAmount()-capsule.getFuelCost());
		
		Vec2i position=null;
		
		//find landing zone
		if (zone.getLandingSite()!=null)
		{
			position=new Vec2i ((int)zone.getLandingSite().x,(int)zone.getLandingSite().y);
		}
		else
		{
			while (position==null)
			{
				int x=Universe.getInstance().m_random.nextInt(zone.getWidth()-8)+4;
				int y=Universe.getInstance().m_random.nextInt(zone.getHeight()-8)+4;
				if (zone.getTile(x, y)!=null && zone.getTile(x, y).getWidgetObject()==null &&
					zone.getTile(x, y).getDefinition().getMovement()==TileMovement.WALK)
				{
					position=new Vec2i(x,y);
				}
			}
		}
		//place capsule
		WidgetCapsule capsule=genCapsule();
		zone.getTile(position.x,position.y).setWidget(capsule);
		//place player
		Vec2i playerPos=getPlayerPos(zone,position);
		Universe.getInstance().setCurrentEntity(world);
		ViewScene.m_interface.Transition(zone.getName(), playerPos.x, playerPos.y);
		ViewScene.m_interface.DrawText("You have used the capsule to travel to the planet surface");

	}
	
	private Vec2i getPlayerPos(Zone zone, Vec2i position) {
		for (int i=0;i<8;i++)
		{
			Vec2i p=Geometry.getPos(i, position);
			if (zone.getTile(p.x, p.y)!=null && zone.getTile(p.x,p.y).getDefinition().getMovement()==TileMovement.WALK &&
					zone.getTile(p.x, p.y).getWidgetObject()==null)
			{
				return p;
			}
		}
		return null;
	}

	private WidgetCapsule genCapsule()
	{
		Document doc = ParserHelper.LoadXML("assets/data/widgets/" + "capsule" + ".xml");
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		WidgetCapsule capsule=new WidgetCapsule(n);	
		capsule.setSpaceship(spaceship.getName());
		return capsule;
	}
	
	private void defaultLand() {
		// TODO Auto-generated method stub
		world.Generate();
		for (int i=0;i<world.getNumZones();i++)
		{
			if (world.getZone(i).getType()==zoneType.SURFACE)
			{
				performLanding(world.getZone(i));
				break;
			}
		}
	}

	public WidgetCapsuleSystem getCapsule() {
		return capsule;
	}
}
