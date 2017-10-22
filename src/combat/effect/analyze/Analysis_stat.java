package combat.effect.analyze;

import org.w3c.dom.Element;

import actor.Actor;
import actorRPG.Actor_RPG;
import actorRPG.RPG_Helper;
import view.ViewScene;

public class Analysis_stat implements Analysis_Type {

	private int stat;
	private int attribute;
	private int []thresholds;
	
	private final static int VAGUE=0;
	private final static int DETAIL=1;
	private final static int PRECISE=2;
	
	public Analysis_stat(Element e) {
		thresholds=new int[3]; for (int i=0;i<3;i++){thresholds[i]=-1;}
		stat=RPG_Helper.statFromString(e.getAttribute("stat"));
		attribute=RPG_Helper.AttributefromString(e.getAttribute("attribute"));
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
	public int getAttribute() {
		return attribute;
	}

	private int getLevel(int attributeValue)
	{
		for (int i=2;i>=0;i++)
		{
			if (thresholds[i]!=-1 && thresholds[i]<=attributeValue)
			{
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public boolean analyze(Actor target, int attributeValue) {
		// TODO Auto-generated method stub
		float value=target.getRPG().getStat(stat);
		float max=target.getRPG().getStatMax(stat);
		float proportion=value/max;
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
			runVague(name,proportion);
			break;
		case DETAIL:
			runDetail(name,proportion);
			break;
		case PRECISE:
			runPrecise(name,value,max);
			break;
		}
		return true;
	}

	private void runPrecise(String name, float value, float max) {
		String token=name+" ";
		switch (stat)
		{
		case Actor_RPG.HEALTH:
			token=token+"health:";
			break;		
		case Actor_RPG.RESOLVE:
			token=token+"resolve:";
			break;
		}	
		ViewScene.m_interface.DrawText(token+value+"/"+max);	
	}

	private void runDetail(String name, float proportion) {
		String token=name+" ";
		switch (stat)
		{
		case Actor_RPG.HEALTH:
			token=token+"health:";
			break;		
		case Actor_RPG.RESOLVE:
			token=token+"resolve:";
			break;
		}	
		ViewScene.m_interface.DrawText(token+(proportion*100)+"%");		
	}

	private void runVague(String name, float proportion) {
		String token=name+" ";
		switch (stat)
		{
		case Actor_RPG.HEALTH:
			token=token+"health is";
			break;		
		case Actor_RPG.RESOLVE:
			token=token+"resolve is";
			break;
		}
		if (proportion>0.90F)
		{
			ViewScene.m_interface.DrawText(token+"stable");
		}
		if (proportion>0.50F && proportion<=0.9F)
		{
			ViewScene.m_interface.DrawText(token+"weakened");			
		}
		if (proportion>0.25F && proportion<=0.5F)
		{
			ViewScene.m_interface.DrawText(token+"weak");			
		}	
		if (proportion<=0.25F)
		{
			ViewScene.m_interface.DrawText(token+"critical");			
		}
	}

}
