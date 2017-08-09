package combat.effect;

import org.w3c.dom.Element;

import actor.Actor;
import nomad.Universe;
import view.ViewScene;

public class Effect_Spawn extends Effect {

	private String filename;
	
	public Effect_Spawn()
	{
		
	}
	
	public Effect_Spawn(Element e) {
		filename=e.getAttribute("value");
	}

	@Override
	public int applyEffect(Actor origin, Actor target, boolean critical) {
		//spawn temp npc
		ViewScene.m_interface.createNPC(filename, Universe.getInstance().getPlayer().getPosition(),true);
		
		return 1;
	}

	@Override
	public Effect clone() {
		// TODO Auto-generated method stub
		Effect_Spawn spawn=new Effect_Spawn();
		spawn.filename=filename;
		return spawn;
	}

	@Override
	public void applyChange(Effect effect, int rank) {
		// TODO Auto-generated method stub

	}

}
