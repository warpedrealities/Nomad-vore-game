package solarview.spaceEncounter.retreatHandler;

import nomad.StarSystem;
import nomad.universe.Universe;
import shared.Geometry;
import shared.Vec2f;
import solarview.SolarScene;
import solarview.spaceEncounter.EncounterEntities.EncounterShipImpl;
import vmo.Game;

public class RetreatHandler {

	public void run(EncounterShipImpl encounterShip) {
		
		Vec2f o=encounterShip.getShip().getPosition();
		StarSystem system=Universe.getInstance().getCurrentStarSystem();
		Vec2f p=getPosition(system,encounterShip.getShip().getPosition());
		encounterShip.getShip().setPosition(p);
		
		Game.sceneManager.SwapScene(new SolarScene(Geometry.getDirection(o, p),encounterShip.getShip(),0));
	}
	
	private Vec2f getPosition(StarSystem system, Vec2f position)
	{
		Vec2f p=null;
		while(p==null) {
			Vec2f c=Geometry.getPos(Universe.m_random.nextInt(8), new Vec2f(0,0));
			c.normalize();
			c.x=c.x*16;
			c.y=c.y*16;
			if (suitableSpot(c,system))
			{
				p=c;
			}
		}
		
		return p;
	}

	private boolean suitableSpot(Vec2f c,StarSystem system) {
		for (int i=0;i<system.getEntities().size();i++)
		{
			if (system.getEntities().get(i).getPosition().getDistance(c)<2)
			{
				return false;
			}
		}
		return true;
	}

	
}
