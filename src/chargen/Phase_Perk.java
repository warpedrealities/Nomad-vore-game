package chargen;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import perks.Perk;
import perks.PerkInstance;
import perks.PerkLibrary;

import actor.Player;
import actorRPG.Player_RPG;

public class Phase_Perk implements Phase {

	ArrayList<Perk> perkList;
	String phaseName;
	
	public Phase_Perk(Element n)
	{
		perkList=new ArrayList<Perk>();
		//populate perklist
		NodeList children=n.getChildNodes();
		phaseName=n.getAttribute("name");
		for (int i=0;i<children.getLength();i++)
		{
			Node node=children.item(i);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)node;
				//run each step successively
				if (Enode.getTagName()=="perk")
				{
					perkList.add(PerkLibrary.getInstance().findPerk(Enode.getAttribute("name")));
				}
			}
		}
	}
	
	@Override
	public String[] getChoices() {
		
		String [] l=new String[perkList.size()];

		for (int i=0;i<perkList.size();i++)
		{
			l[i]=perkList.get(i).getName();
		}
		return l;
	}

	@Override
	public int getChoiceCount() {
		return perkList.size();
	}

	@Override
	public String getChoiceDescription(int index) {
		return perkList.get(index).getDescription();
	}

	@Override
	public void performChoice(int index, Player player) {
		Player_RPG rpg=(Player_RPG)player.getRPG();
		rpg.addPerk(perkList.get(index));
	}

	@Override
	public String getName() {

		return phaseName;
	}

	@Override
	public void rollback(Player player) {
		
		ArrayList<PerkInstance> playerPerkList=((Player_RPG)player.getRPG()).getPlayerPerks();
		playerPerkList.remove(playerPerkList.size()-1);
		
	}

}
