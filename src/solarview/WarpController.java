package solarview;

import particlesystem.ParticleEmitter;
import particlesystem.ParticleEmitterAdvanced;
import particlesystem.particleEffectors.ParticleDraw;
import shared.Vec2f;
import spaceship.Spaceship;
import view.ViewScene;
import vmo.Game;
import rendering.SpriteRotatable;

public class WarpController {

	private float clock;
	private ParticleEmitterAdvanced particleEmitter;
	private Spaceship ship;
	private Vec2f position;
	private Vec2f velocity;
	private SpriteRotatable sprite;
	private boolean warping;
	public WarpController(ParticleEmitterAdvanced emitter,Spaceship ship)
	{
		this.particleEmitter=emitter;
		this.ship=ship;
		position=new Vec2f(0,0);
		sprite=(SpriteRotatable)ship.getSpriteObj();
	}
	
	
	public void update(float dt)
	{
		if (ship.getWarpHandler()!=null && ship.getWarpHandler().getCharge()>0)
		{
			if (ship.getWarpHandler().getCharge()>=100)
			{

				if (!warping)
				{
					particleEmitter.runEffector(new ParticleDraw(position.replicate(),-8));
					ship.getShipController().setBusy(50);
					position=ship.getPosition().replicate();
					warping=true;
					velocity=new Vec2f(16,0);
					velocity.rotate(sprite.getFacing()/50.785398F);
		
				}
				else
				{
					position.x+=velocity.x*dt;
					position.y+=velocity.y*dt;
					sprite.repositionF(position);
				}

				clock+=dt;
				if (clock>3)
				{
					Game.sceneManager.SwapScene(new ViewScene());
				}
			}
			else
			{
				position.x=ship.getPosition().x+0.5F;
				position.y=ship.getPosition().y+0.5F;
				particleEmitter.setPosition(position);	
				float t=10/ship.getWarpHandler().getCharge();
				clock+=dt;
				if (clock>t)
				{
					int c=(int)ship.getWarpHandler().getCharge()/25;
					if (c<1)
					{
						c=1;
					}
					particleEmitter.SpawnParticles(c);				
					
					clock=0;
				}
		
			}

		}
		particleEmitter.Update(dt);

	}
	
}
