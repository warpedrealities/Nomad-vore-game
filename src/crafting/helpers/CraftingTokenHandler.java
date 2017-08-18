
package crafting.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import actorRPG.player.Player_RPG;
import crafting.CraftingIngredient;
import perks.Perk;
import perks.PerkCraftingToken;
import perks.PerkInstance;

public class CraftingTokenHandler {

	private Map <String,Integer> map;
	
	public CraftingTokenHandler(Player_RPG playerRPG)
	{
		map=new HashMap<String,Integer>();
		
		for (int i=0;i<playerRPG.getNumPerks();i++)
		{
			PerkInstance pi=playerRPG.getPerk(i);
			Perk p=pi.getPerk();
			for (int j=0;j<p.getNumElements();j++)
			{
				if (PerkCraftingToken.class.isInstance(p.getElement(j)))
				{
					PerkCraftingToken pct=(PerkCraftingToken)p.getElement(j);
					Integer index=map.get(pct.getToken());
					if (index!=null)
					{
						map.put(pct.getToken(), pi.getPerkRank()+index);
					}
					else
					{
						map.put(pct.getToken(), pi.getPerkRank());			
					}
				}
			}		
		}
	}
	
	public Map<String, Integer> getMap() {
		return map;
	}

	public int getToken(String token)
	{
		return map.get(token);
	}
}
