package combat;

import actor.Actor;
import actor.Attackable;
import actor.Player;
import actorRPG.Actor_RPG;
import faction.violation.FactionRule.ViolationType;
import nomad.Universe;
import shared.Vec2f;
import view.ViewScene;
import view.ZoneInteractionHandler;
import vmo.Game;
import vmo.GameManager;
import widgets.WidgetSlot;
import zone.Tile;
import zone.Zone;

public class CombatAura {

	
	
	static public boolean doExplosion(CombatMove move, Actor origin, Attackable target) {
		
		Zone zone=Universe.getInstance().getCurrentZone();
		
		if (move.isNonViolent())
		{
			ViewScene.m_interface.Flash(target.getPosition(), 4);
			for (int i=0;i<8;i++)
			{
				Vec2f p=ZoneInteractionHandler.getPos(i, target.getPosition());
				explodeTile(zone,(int)p.x,(int)p.y,move,origin);
				ViewScene.m_interface.Flash(p, 4);
				
			}
		}
		else
		{
			ViewScene.m_interface.Flash(target.getPosition(), 3);		
			for (int i=0;i<8;i++)
			{
				Vec2f p=ZoneInteractionHandler.getPos(i, target.getPosition());
				explodeTile(zone,(int)p.x,(int)p.y,move,origin);
				ViewScene.m_interface.Flash(p, 3);
				
			}
		}
		return false;
	}
	
	
	static private void explodeTile(Zone zone, int x, int y, CombatMove move, Actor origin)
	{
		if (zone.zoneTileGrid[x][y]==null)
		{
			return;
		}
		if (zone.zoneTileGrid[x][y].getWidgetObject()!=null)
		{
			if (Attackable.class.isInstance(zone.zoneTileGrid[x][y].getWidgetObject()))
			{
				Attackable attackable=(Attackable)zone.zoneTileGrid[x][y].getWidgetObject();
				//attack this		
		//		attackable.Harm(player.getAttack().getDamage(0), player,0);
				ViewScene.m_interface.getSceneController().getHandler().violationCheck(attackable.getName(),new Vec2f(x,y),ViolationType.Attack);
				attack(origin,move,attackable);
			}		
			if (WidgetSlot.class.isInstance(zone.zoneTileGrid[x][y].getWidgetObject()))
			{
				WidgetSlot ws=(WidgetSlot)zone.zoneTileGrid[x][y].getWidgetObject();
				if (ws.getWidget()!=null)
				{
					Attackable attackable=(Attackable)ws.getWidget();
					ViewScene.m_interface.getSceneController().getHandler().violationCheck(attackable.getName(),new Vec2f(x,y),ViolationType.Attack);
					attack(origin,move,attackable);
				}
			}
		}	
		//check for actor in tile
		if (zone.zoneTileGrid[x][y]!=null && zone.zoneTileGrid[x][y].getActorInTile()!=null)
		{
			Attackable attackable=(Attackable)zone.zoneTileGrid[x][y].getActorInTile();
			if (move.isNonViolent())
			{
				ViewScene.m_interface.getSceneController().getHandler().violationCheck(attackable.getName(),new Vec2f(x,y),ViolationType.Seduce);
			}
			else
			{
				ViewScene.m_interface.getSceneController().getHandler().violationCheck(attackable.getName(),new Vec2f(x,y),ViolationType.Attack);	
			}
			attack(origin,move,attackable);
		}
		/*
		for (int i=0;i<zone.zoneActors.size();i++)
		{
			if (zone.zoneActors.get(i)!=origin && zone.zoneActors.get(i).getVisible()==true && zone.zoneActors.get(i).getAttackable())
			{
				int xt=(int)zone.zoneActors.get(i).getPosition().x;
				int yt=(int)zone.zoneActors.get(i).getPosition().y;
				if (xt==x && yt==y)
				{
					//conduct attack
					Attackable attackable=(Attackable)zone.zoneActors.get(i);
//					player.Attack(attackable,m_view);
					
					if (move.isNonViolent())
					{
						ViewScene.m_interface.getSceneController().getHandler().violationCheck(attackable.getName(),new Vec2f(x,y),ViolationType.Seduce);
					}
					else
					{
						ViewScene.m_interface.getSceneController().getHandler().violationCheck(attackable.getName(),new Vec2f(x,y),ViolationType.Attack);	
					}
					attack(origin,move,attackable);
				}
			}
		}	
		*/
	}

	static private boolean getVisible(Vec2f p)
	{
		Tile t=Universe.getInstance().getCurrentZone().getTile((int)p.x, (int)p.y);
		
		if (t!=null && t.getVisible())
		{
			return true;
		}
		return false;
	}
	
	
	static private void attack(Actor origin, CombatMove move, Attackable target)
	{
		int defAttribute=Actor_RPG.DODGE;
		if (move.isNonViolent())
		{
			defAttribute=Actor_RPG.WILLPOWER;
		}
		//get defence
		int def=CombatLookup.getBaseDefence(0, defAttribute)+target.getAttribute(defAttribute);
		//get attack bonus
		int bonus=origin.getRPG().getAttribute(move.getBonusAttribute());
		boolean visible=getVisible(target.getPosition());
		int r=GameManager.m_random.nextInt(20)+bonus;
		if (def<=r)
		{
			if (Game.sceneManager.getConfig().isVerboseCombat() && r<100 && origin.getVisible())
			{
				ViewScene.m_interface.DrawText(origin.getName()+" attacks "+target.getName()+" "+(r-bonus)+"+"+bonus+" DC:"+def+"=hit");	
			}
			int value=0;
			//apply effect to target	
			boolean critical=false;
			
			if (r>=def+10 && r<100 && Player.class.isInstance(origin) && Actor.class.isInstance(target))
			{
				critical=true;			
			}
			for (int i=0;i<move.getEffects().size();i++)
			{
				value+=target.applyEffect(move.getEffects().get(i), origin,critical);
			}
			//show hit text
			
			if (target.getAttackable() && visible)
			{
				String text=move.getHitText()[GameManager.m_random.nextInt(move.getHitText().length)];
				if (text.length()>0)
				{
					String str=text.replace("TARGET", target.getName()).replace("VALUE",Integer.toString(value));
					if (critical)
					{
						ViewScene.m_interface.DrawText(str+" (CRITICAL!)");		
					}
					else
					{
						ViewScene.m_interface.DrawText(str);		
					}	
				}
			}
		}
		else
		{
			if (visible)
			{
				if (Game.sceneManager.getConfig().isVerboseCombat() && r<100 && origin.getVisible())
				{
					ViewScene.m_interface.DrawText(origin.getName()+" attacks "+target.getName()+" "+(r-bonus)+"+"+bonus+" DC:"+def+"=miss");	
				}
				//show miss text
				String text=move.getMissText()[GameManager.m_random.nextInt(move.getMissText().length)];
				String str=text.replace("TARGET", target.getName());
				ViewScene.m_interface.DrawText(str);
		
			}
		}
		
	}
}
