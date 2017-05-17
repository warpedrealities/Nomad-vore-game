package perks;

import java.util.ArrayList;

import actor.Player_LOOK;
import actorRPG.Player_RPG;
import nomad.Universe;

public class PerkQualifier {

	private Player_RPG playerRPG;
	private Player_LOOK playerAppearance;
	
	public PerkQualifier()
	{
		playerRPG=(Player_RPG)Universe.getInstance().getPlayer().getRPG();
		playerAppearance=Universe.getInstance().getPlayer().getLook();
	}
	
	public boolean perkUseable(Perk perk)
	{
		if (perk.getRequires()==null)
		{
			return true;
		}
		String [] str=perk.getRequires();
		for (int i=0;i<str.length;i++)
		{
			//check appearance matches needed bodyparts

			if (str[i].contains("BODY"))
			{
				if (playerAppearance.getPart(str[i].replace("BODY", ""))!=null)
				{
					return true;
				}	
				return false;
			}
		}

		return true;
	}
	
	public boolean perkAcquireable(Perk perk)
	{
		//check meet appearance requirements
		if (!perkUseable(perk))
		{
			return false;
		}
		
		//check maxranking
		PerkInstance extantperk=playerRPG.getPerkInstance(perk);
		if (extantperk!=null)
		{
			if (extantperk.getPerkRank()>=perk.getMaxrank())
			{
				return false;
			}

		}
		else
		//check requirements
		{
		
			if (perk.getRequires()!=null)
			{	
				
				String []str=perk.getRequires();
				for (int i=0;i<str.length;i++)
				{
					if (str[i].contains("RESEARCH"))
					{
						String find=str[i].replace("RESEARCH","");
						if (Universe.getInstance().getPlayer().getEncyclopedia().hasEntry(find))
						{
							return true;
						}
						return false;
						
					}
					if (str[i].contains("PERK"))
					{
						String find=str[i].replace("PERK","");
						ArrayList<PerkInstance> perklist=playerRPG.getPlayerPerks();
						for (int j=0;j<perklist.size();j++)
						{
							if (perklist.get(j).getPerk().getName().equals(find))
							{
								return true;
							}
						}
						return false;
					}
					if (str[i].contains("CHARGEN"))
					{
						return false;
					}		
					if (str[i].contains("FLAG"))
					{
						String find=str[i].replace("FLAG","");
						if (Universe.getInstance().getPlayer().getFlags().readFlag(find)!=0)
						{
							return true;
						}
						else
						{
							return false;	
						}						
					}
					if (str[i].contains("LEVEL"))
					{
						String nstring=str[i].replace("LEVEL", ""); nstring=nstring.replace(" ", "");
						int value=Integer.parseInt(nstring);
						if (playerRPG.getPlayerLevel()>=value)
						{
							return true;
						}
						else
						{
							return false;
						}
					}
					if (str[i].contains("BODY"))
					{
						if (playerAppearance.getPart(str[i].replace("BODY", "").replace(" ", ""))!=null)
						{
							return true;
						}	
						return false;
					}
				}

			}
		}
		return true;
	}

}
