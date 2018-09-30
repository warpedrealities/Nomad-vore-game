package combat.effect;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import actor.Actor;
import view.ViewScene;

public class Effect_RemoveStatus extends Effect {

	private int []statusIds;
	private String description;

	public Effect_RemoveStatus(Element enode) {
		statusIds=new int[Integer.parseInt(enode.getAttribute("count"))];
		NodeList children=enode.getElementsByTagName("statusId");
		int index=0;
		for (int i=0;i<children.getLength();i++)
		{
			Element e=(Element)children.item(i);
			statusIds[index]=Integer.parseInt(e.getAttribute("value"));
			index++;
		}
		children=enode.getElementsByTagName("description");
	}

	@Override
	public int applyEffect(Actor origin, Actor target, boolean critical) {
		int r=0;
		for (int i=0;i<statusIds.length;i++)
		{
			if (target.getRPG().getStatusEffectHandler().hasStatus(statusIds[i]))
			{
				target.getRPG().getStatusEffectHandler().removeStatus(statusIds[i], target.getRPG());
				r++;
			}
		}
		ViewScene.m_interface.DrawText(description);
		return r;
	}

	@Override
	public Effect clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyChange(Effect effect, int rank, boolean proportionate) {
		// TODO Auto-generated method stub

	}

}
