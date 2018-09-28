<<<<<<< HEAD
package combat.effect;

import org.w3c.dom.Element;

import actor.Actor;
import actor.npc.NPC;
import view.ViewScene;

public class Effect_Submit extends Effect {

	private boolean resolve;
	
	public Effect_Submit(Element e) {
		if (e.getAttribute("resolve").equals("true"))
		{
			resolve=true;
		}
	}

	public Effect_Submit() {
	}

	@Override
	public int applyEffect(Actor origin, Actor target, boolean critical) {
		if (NPC.class.isInstance(target))
		{
			NPC npc=(NPC)target;
			if (!resolve)
			{
				if (npc.getConversation(NPC.CONVERSATIONVICTORY)!=null)
				{
					ViewScene.m_interface.DrawText("You surrender to "+target.getName());	
					ViewScene.m_interface.PlayerBeaten(npc, false);
				}
				else
				{
					ViewScene.m_interface.DrawText("You can't surrender to "+target.getName());

				}
			}
			if (resolve)
			{
				if (npc.getConversation(NPC.CONVERSATIONSEDUCER)!=null)
				{
					ViewScene.m_interface.DrawText("You fantasize about and submit to them "+target.getName());	
					ViewScene.m_interface.PlayerBeaten(npc, true);	
				}
				else
				{
					ViewScene.m_interface.DrawText("You can't fantasize about "+target.getName());				
				}
			}		
		}
		return 0;
	}

	@Override
	public Effect clone() {
		Effect_Submit s=new Effect_Submit();
		s.resolve=resolve;
		return s;
	}

	@Override
	public void applyChange(Effect effect, int rank) {
		// TODO Auto-generated method stub

	}
	
	@Override 
	public boolean harmless()
	{
		return true;
	}
}
=======
package combat.effect;

import org.w3c.dom.Element;

import actor.Actor;
import actor.npc.NPC;
import view.ViewScene;

public class Effect_Submit extends Effect {

	private boolean resolve;
	
	public Effect_Submit(Element e) {
		if (e.getAttribute("resolve").equals("true"))
		{
			resolve=true;
		}
	}

	public Effect_Submit() {
	}

	@Override
	public int applyEffect(Actor origin, Actor target, boolean critical) {
		if (NPC.class.isInstance(target))
		{
			NPC npc=(NPC)target;
			if (!resolve)
			{
				if (npc.getConversation(NPC.CONVERSATIONVICTORY)!=null)
				{
					ViewScene.m_interface.DrawText("You surrender to "+target.getName());	
					ViewScene.m_interface.PlayerBeaten(npc, false);
				}
				else
				{
					ViewScene.m_interface.DrawText("You can't surrender to "+target.getName());
					ViewScene.m_interface.PlayerBeaten(npc, true);	
				}
			}
			if (resolve)
			{
				if (npc.getConversation(NPC.CONVERSATIONSEDUCER)!=null)
				{
					ViewScene.m_interface.DrawText("You fantasize about and submit to them "+target.getName());					
				}
				else
				{
					ViewScene.m_interface.DrawText("You can't fantasize about "+target.getName());				
				}
			}		
		}
		return 0;
	}

	@Override
	public Effect clone() {
		Effect_Submit s=new Effect_Submit();
		s.resolve=resolve;
		return s;
	}

	@Override
	public void applyChange(Effect effect, int rank) {
		// TODO Auto-generated method stub

	}
	
	@Override 
	public boolean harmless()
	{
		return true;
	}
}
>>>>>>> d03d62eefaf313f2194d04c619401f2957db2368
