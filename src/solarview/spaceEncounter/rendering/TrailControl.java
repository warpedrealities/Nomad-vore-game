package solarview.spaceEncounter.rendering;

import java.nio.FloatBuffer;

import particlesystem.ParticleConeEmitter;
import shared.Vec2f;
import solarview.spaceEncounter.EncounterEntities.CombatManouver;
import solarview.spaceEncounter.EncounterEntities.EncounterShipImpl;
import solarview.spaceEncounter.EncounterEntities.ShipEmitters;

public class TrailControl {

	private float clock;
	private ParticleConeEmitter [][] emitters;
	private ShipEmitters [] shipEmitters;
	private EncounterShipImpl [] ships;
	public TrailControl(EncounterShipImpl [] ships)
	{
		this.ships=ships;
		emitters=new ParticleConeEmitter[ships.length][];
		shipEmitters=new ShipEmitters[ships.length];
		
		//build each emitter group
		for (int i=0;i<ships.length;i++)
		{
			String sprite="particles/red";
			if (i==0)
			{
				sprite="particles/blue";
			}
			shipEmitters[i]=ships[i].getEmitters();
			if (shipEmitters[i].getEngineEmitters().size()>0)
			{
				emitters[i]=new ParticleConeEmitter[shipEmitters[i].getEngineEmitters().size()];
				
				for (int j=0;j<ships[i].getEmitters().getEngineEmitters().size();j++)
				{
					emitters[i][j]=new ParticleConeEmitter(256,sprite,new Vec2f(0,0),4,0.1F);;
				}	
			}
	
		}
		
	}
	
	public void update(float dt)
	{
		clock+=dt;
		boolean spawn=false;
		if (clock>0.025F)
		{

			spawn=true;
	
			clock=0;
		}
		for (int i=0;i<emitters.length;i++)
		{
			if (emitters[i]!=null && emitters[i].length>0)
			{
				int thrust=0;
				if (spawn==true && (ships[i].getCourse() & CombatManouver.half) >0)
				{
					thrust=1;
				}
				else if (spawn==true && (ships[i].getCourse() & CombatManouver.full) >0)
				{
					thrust=2;
				}
				for (int j=0;j<emitters[i].length;j++)
				{
					emitters[i][j].Update(dt);
					if (thrust>0)
					{
						emitters[i][j].spawnCone(thrust,ships[i].getHeading(),0.00F);		
					}
				}			
			}
		}
	}
	
	public void reposition()
	{
		for (int i=0;i<emitters.length;i++)
		{
			if (emitters[i]!=null)
			{
				for (int j=0;j<emitters[i].length;j++)
				{
					Vec2f p=new Vec2f(shipEmitters[i].getEngineEmitters().get(j));
					p.rotate(ships[i].getHeading()* 0.785398F);
					emitters[i][j].setPosition(p.add(ships[i].getPosition()));
				}		
			}
		}
	}
	
	public void draw(FloatBuffer matrix44fbuffer, int objmatrix, int tint) 
	{
		for (int i=0;i<emitters.length;i++)
		{
			if (emitters[i]!=null)
			{
				for (int j=0;j<emitters[i].length;j++)
				{
					emitters[i][j].draw(matrix44fbuffer,objmatrix,tint);
				}		
			}
		}
	}
	
	public void discard()
	{
		for (int i=0;i<emitters.length;i++)
		{
			if (emitters[i]!=null)
			{
				for (int j=0;j<emitters[i].length;j++)
				{
					emitters[i][j].Discard();
				}			
			}
		}
	}
}
