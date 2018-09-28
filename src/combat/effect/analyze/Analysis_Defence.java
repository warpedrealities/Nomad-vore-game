package combat.effect.analyze;

import org.w3c.dom.Element;

import actor.Actor;
import actorRPG.Actor_RPG;
import view.ViewScene;

public class Analysis_Defence implements Analysis_Type {

	private int stat;
	private boolean resolve;
	private int []thresholds;	
	
	private int getLevel(int attributeValue)
	{
		for (int i=2;i>=0;i--)
		{
			if (thresholds[i]!=-1 && thresholds[i]<=attributeValue)
			{
				return i;
			}
		}
		return -1;
	}	
	
	public Analysis_Defence(Element e) {
		thresholds=new int[3]; for (int i=0;i<3;i++){thresholds[i]=-1;}
		resolve=e.getAttribute("resolve").equals("true");

		if (e.getAttribute("vague").length()>0)
		{
			thresholds[VAGUE]=Integer.parseInt(e.getAttribute("vague"));			
		}
		if (e.getAttribute("detail").length()>0)
		{
			thresholds[DETAIL]=Integer.parseInt(e.getAttribute("detail"));			
		}
		if (e.getAttribute("precise").length()>0)
		{
			thresholds[PRECISE]=Integer.parseInt(e.getAttribute("precise"));			
		}
	}
	
	@Override
	public boolean analyze(Actor target, int attributeValue) {
		// TODO Auto-generated method stub
		int values[]=calcValues(target);
		String name=target.getName();
		int threshold=getLevel(attributeValue);
		if (threshold==-1)
		{
			ViewScene.m_interface.DrawText("You lack the skill to analyze a target this way");
			return false;
		}
		switch (threshold)
		{
		case VAGUE:
			runVague(name,values);
			break;
		case DETAIL:
			runDetail(name,values);
			break;
		case PRECISE:
			runPrecise(name,values);
			break;
		}
		return true;
	}


	private int [] calcValues(Actor target) {
		int v[]=new int[3];
		if (resolve)
		{
			v[0]=target.getRPG().getAttribute(Actor_RPG.TEASE);
			v[1]=target.getRPG().getAttribute(Actor_RPG.PHEREMONE);
			v[2]=target.getRPG().getAttribute(Actor_RPG.PSYCHIC);
		}
		else
		{
			v[0]=target.getRPG().getAttribute(Actor_RPG.KINETIC);
			v[1]=target.getRPG().getAttribute(Actor_RPG.THERMAL);
			v[2]=target.getRPG().getAttribute(Actor_RPG.SHOCK);		
		}	
		return v;
	}


	private void runPrecise(String name, int[] values) {
		ViewScene.m_interface.DrawText(name+" defences "+ getDefence(0)+":"+ values[0]+
				" "+getDefence(1)+":"+ values[1]+" "+getDefence(2)+":"+ values[2]);	
	}
	
	private void runDetail(String name, int[] values) {
		int median=(values[0]+values[1]+values[2])/3;
		for (int i=0;i<3;i++)
		{
			detailElement(name,values[i],i,median);
		}
	}
	
	private void detailElement(String name, int value, int index, int median)
	{
		if (value==0)
		{
			ViewScene.m_interface.DrawText(name+" has no "+getDefence(index)+" protection");		
		}
		if (value<median)
		{
			ViewScene.m_interface.DrawText(name+" is weak to "+getDefence(index));				
		}
		if (value==median)
		{
			ViewScene.m_interface.DrawText(name+" has average resistance to "+getDefence(index));				
		}
		if (value>median)
		{
			ViewScene.m_interface.DrawText(name+" is highly resistant to "+getDefence(index));				
		}
	}

	private void runVague(String name, int[] values) {
		int highest=getHighest(values);
		if (values[highest]>0)
		{
			ViewScene.m_interface.DrawText(name+" has its strongest protection against "+getDefence(highest));			
		}
		else
		{
			ViewScene.m_interface.DrawText(name+" has no protections");		
		}

	}
	
	private int getHighest(int[] values)
	{
		if (values[0]> values[1] && values[0] >= values[2])
		{
			return 0;
		}
		if (values[1]> values[0] && values[1] >= values[2])
		{
			return 1;
		}
		if (values[2]> values[0] && values[2] >= values[1])
		{
			return 2;
		}
		return 0;
	}

	private String getDefence(int index)
	{
		switch (index)
		{
		case 0:
			if (resolve) {
				return "tease";
			}
			else
			{
				return "kinetic";
			}
		case 1:
			if (resolve) {
				return "pheremone";
			}
			else
			{
				return "thermal";
			}
		case 2:
			if (resolve) {
				return "psychic";
			}
			else
			{
				return "shock";
			}			
		}
		return "";
	}

}
