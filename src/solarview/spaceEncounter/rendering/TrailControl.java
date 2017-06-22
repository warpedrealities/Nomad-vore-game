package solarview.spaceEncounter.rendering;

import java.nio.FloatBuffer;

import particlesystem.ParticleConeEmitter;
import shared.Vec2f;
import solarview.spaceEncounter.EncounterEntities.CombatManouver;
import solarview.spaceEncounter.EncounterEntities.EncounterShip;
import solarview.spaceEncounter.EncounterEntities.ShipEmitters;

public class TrailControl {

	private ParticleConeEmitter [][] emitters;
	private ShipEmitters [] shipEmitters;
	private EncounterShip [] ships;
	public TrailControl(EncounterShip [] ships)
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
					emitters[i][j]=new ParticleConeEmitter(128,sprite,new Vec2f(0,0),2,0.2F);;
				}	
			}
	
		}
		
	}
	
	public void update(float dt)
	{
		for (int i=0;i<emitters.length;i++)
		{
			if (emitters[i]!=null && emitters[i].length>0)
			{
				int thrust=0;
				if ((ships[i].getCourse() & CombatManouver.half) >0)
				{
					thrust=1;
				}
				else if ((ships[i].getCourse() & CombatManouver.full) >0)
				{
					thrust=2;
				}
				for (int j=0;j<emitters[i].length;j++)
				{
					emitters[i][j].Update(dt);
					emitters[i][j].SpawnParticles(thrust);
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
					emitters[i][j].setPosition(ships[i].getPosition());
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
