package solarview;

import particlesystem.ParticleEmitterAdvanced;
import particlesystem.particleEffectors.ParticleDraw;
import rendering.SpriteRotatable;
import shared.Vec2f;
import spaceship.Spaceship;
import view.ViewScene;
import vmo.Game;

public class WarpController {

	
	enum WarpMode{charge,out,in};
	private float clock;
	private ParticleEmitterAdvanced particleEmitter;
	private Spaceship ship;
	private Vec2f [] positions;
	private Vec2f velocity;
	private SpriteRotatable sprite;
	private WarpMode warpMode;

	
	public WarpController(ParticleEmitterAdvanced emitter,Spaceship ship)
	{
		this.particleEmitter=emitter;
		this.ship=ship;
		positions=new Vec2f[2];
		positions[0]=new Vec2f(0,0);
		positions[1]=new Vec2f(0,0);	
		sprite=(SpriteRotatable)ship.getSpriteObj();
	}
	
	public void warpIn()
	{
		ship.getShipController().setBusy(40);
		warpMode=WarpMode.in;
		velocity=new Vec2f(0,16);
		velocity.rotate(sprite.getFacing()*0.785398F);	
		positions[0]=ship.getPosition().replicate();	
		positions[1]=ship.getPosition().replicate();	
		positions[1].x+=0.5F;
		positions[1].y+=0.5F;
		positions[1].x-=velocity.x*1;
		positions[1].y-=velocity.y*1;
		sprite.setCentered(true);
	}
	
	private void doWarpIn(float dt)
	{
		positions[1].x+=velocity.x*dt;
		positions[1].y+=velocity.y*dt;
		positions[0].x=positions[1].x;
		positions[0].y=positions[1].y;
		sprite.repositionF(positions[1]);
		clock+=dt;
		particleEmitter.setPosition(positions[0]);	
		particleEmitter.SpawnParticles(1, new Vec2f(0,0));			
		
		if (clock>1)
		{
			particleEmitter.SpawnParticles(128);
			clock=0;
			warpMode=null;
			particleEmitter.Update(0.1F);
			sprite.setCentered(false);
			particleEmitter.runEffector(new ParticleDraw(positions[1],-8));
		}	
	}
	
	public void update(float dt)
	{
		if (warpMode==WarpMode.in)
		{
			doWarpIn(dt);
		}
		
		if (ship.getWarpHandler()!=null && ship.getWarpHandler().getCharge()>0)
		{
			if (ship.getWarpHandler().getCharge()>=100)
			{

				if (warpMode==null)
				{
					particleEmitter.runEffector(new ParticleDraw(positions[0].replicate(),-8));
					ship.getShipController().setBusy(50);
					positions[1]=ship.getPosition().replicate();
					warpMode=WarpMode.out;
					velocity=new Vec2f(0,16);
					velocity.rotate(sprite.getFacing()*0.785398F);
				}
				if (warpMode==WarpMode.out)
				{
					positions[0].x=positions[1].x+0.5F;
					positions[0].y=positions[1].y+0.5F;
					positions[1].x+=velocity.x*dt;
					positions[1].y+=velocity.y*dt;
					sprite.repositionF(positions[1]);
					particleEmitter.SpawnParticles(1, new Vec2f(0,0));
					clock+=dt;
					if (clock>3)
					{
						Game.sceneManager.SwapScene(new ViewScene());
					}
				}

	
			}
			else
			{
				positions[0].x=ship.getPosition().x+0.5F;
				positions[0].y=ship.getPosition().y+0.5F;
				particleEmitter.setPosition(positions[0]);	
				float t=5/ship.getWarpHandler().getCharge();
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

	public void warpBurst() {
		particleEmitter.setPosition(ship.getPosition().replicate().add(new Vec2f(0.5F,0.5F)));
		particleEmitter.SpawnParticles(128);
		clock=0;
		warpMode=null;
		particleEmitter.Update(0.1F);
		sprite.setCentered(false);
		particleEmitter.runEffector(new ParticleDraw(ship.getPosition().replicate().add(new Vec2f(0.5F,0.5F)),-8));	
	}


	
}
