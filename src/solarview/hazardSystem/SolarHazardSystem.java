package solarview.hazardSystem;

import java.util.List;
import java.util.Queue;

import nomad.universe.Universe;
import spaceship.Spaceship;
import spaceship.SpaceshipResource;

public class SolarHazardSystem {

	private Spaceship ship;
	private int clock;
	private Queue<String> queue;
	private List<Hazard_Base> [] hazards;
	
	public SolarHazardSystem(Spaceship ship, Queue<String> queue) {
		this.ship=ship;
		clock=0;
		this.queue=queue;
		hazards=new List[3];
		for (int i=0;i<hazards.length;i++)
		{
			hazards[i]=new HazardArrayBuilder(i).build();
			
		}
	}

	public void update() {
		clock++;
		if (clock>200)
		{
			clock=0;
			runEvent();
		}
	}

	private void runEvent() {
		float baseChance=calcBaseChance();
		if (baseChance<1)
		{
			return;
		}
		int roll=Universe.m_random.nextInt(100);
		if (roll<baseChance)
		{
			runHazard();
		}
	}

	private void runHazard() {
		int severity=calcSeverity();
		int span=hazards[0].size();
		if (severity>=1)
		{
			span+=hazards[1].size();
		}
		if (severity>=2)
		{
			span+=hazards[2].size();
		}
		int r=Universe.m_random.nextInt(span);
		Hazard_Base hazard=getHazard(r);
		
		hazard.runHazard(ship,queue);
	}

	private Hazard_Base getHazard(int r) {
		if (r<hazards[0].size())
		{
			return hazards[0].get(r);
		}
		int threshold=hazards[0].size();
		if (r<hazards[1].size()+threshold)
		{
			return hazards[1].get(r-threshold);
		}
		threshold+=hazards[1].size();
		if (r<hazards[2].size()+threshold)
		{
			return hazards[2].get(r-threshold);
		}		
		return null;
	}

	private int calcSeverity() {
		SpaceshipResource r=ship.getShipStats().getResource("HULL");
		float p=r.getResourceAmount()/r.getResourceCap();
		if (p>=0.75F)
		{
			return 0;
		}
		if (p>=0.5F)
		{
			return 1;
		}
		return 2;
	}

	private float calcBaseChance() {
		SpaceshipResource r=ship.getShipStats().getResource("HULL");
		float p=r.getResourceAmount()/r.getResourceCap();
		return (1-p)*10;
	}

	
}
