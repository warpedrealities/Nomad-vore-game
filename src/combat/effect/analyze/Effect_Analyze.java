package combat.effect.analyze;

import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import actor.Actor;
import combat.effect.Effect;
import view.ViewScene;

public class Effect_Analyze extends Effect {

	private List<Analysis_Type> actions;
	private String tag;
	
	public Effect_Analyze(Element element)
	{
		actions=new ArrayList<Analysis_Type>();
		if (element.getAttribute("tag").length()>0)
		{
			tag=element.getAttribute("tag");
		}
		NodeList children=element.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
			if (children.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element e=(Element)children.item(i);
				if (e.getTagName().equals("stat"))
				{
					actions.add(new Analysis_stat(e));
				}
			}
		}
		
	}
	
	@Override
	public int applyEffect(Actor origin, Actor target, boolean critical) {
		if (tag!=null)
		{
			if (target.getRPG().getTagged(tag))
			{
				ViewScene.m_interface.DrawText("you cannot analyze "+target.getName());
				return 0;
			}
		}
		for (int i=0;i<actions.size();i++)
		{
			actions.get(i).analyze(target, origin.getRPG().getAttribute(actions.get(i).getAttribute()));
		}
		return 0;
	}

	@Override
	public Effect clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyChange(Effect effect, int rank) {
		// TODO Auto-generated method stub

	}

}
