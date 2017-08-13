package combat.effect;

import org.w3c.dom.Element;

import actor.Actor;
import nomad.Universe;
import view.ViewScene;

public class Effect_Spawn extends Effect {

	private String filename;
	private String description;
	public Effect_Spawn()
	{
		
	}
	
	public Effect_Spawn(Element e) {
		filename=e.getAttribute("value");
		description = e.getTextContent().replace("\n", "");
	}

	@Override
	public int applyEffect(Actor origin, Actor target, boolean critical) {
		//spawn temp npc
		ViewScene.m_interface.createNPC(filename, Universe.getInstance().getPlayer().getPosition(),true);
		ViewScene.m_interface.DrawText(description);
		return 1;
	}

	@Override
	public Effect clone() {
		Effect_Spawn spawn=new Effect_Spawn();
		spawn.filename=filename;
		spawn.description=description;
		return spawn;
	}

	@Override
	public void applyChange(Effect effect, int rank) {
		// TODO Auto-generated method stub

	}

}
