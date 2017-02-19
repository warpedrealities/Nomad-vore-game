package worldgentools;

import item.Item;
import item.ItemCoin;
import item.ItemExpositionInstance;
import item.ItemKeyInstance;

import java.util.ArrayList;

import nomad.Universe;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LootTable {

	ArrayList <LootEntry> loot;
	
	public LootTable(Node node) {
		if (node==null)
		{
			return;
		}
		loot=new ArrayList<LootEntry>();
		
	NodeList children=node.getChildNodes();	
		for (int i=0;i<children.getLength();i++)
		{
			Node N=children.item(i);
			if (N.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)N;
				//run each step successively
				if (Enode.getTagName()=="loot")
				{
					loot.add(new LootEntry(Enode));
				}
				if (Enode.getTagName()=="lootSubTable")
				{
					loot.add(new LootSubTable(Enode));
				}
			}
		}
	}
	
	private boolean addCoins(ItemCoin coins, ArrayList<Item> list)
	{
		for (int i=0;i<list.size();i++)
		{
			if (ItemCoin.class.isInstance(list.get(i)))
			{
				ItemCoin ic=(ItemCoin)list.get(i);
				ic.setCount(ic.getCount()+coins.getCount());
				return true;
			}
		}
		return false;
	}

	public ArrayList<Item> generateLoot() {
		ArrayList<Item> list=new ArrayList<Item>();
		if (loot==null)
		{
			return list;
		}
		boolean oneper=false;
		for (int i=0;i<loot.size();i++)
		{
			if (loot.get(i).isUnique()!=1)
			{
				if (!(loot.get(i).isUnique()==3 && oneper))
				{
					//roll to see if its part of this loot batch
					float chance=loot.get(i).getChance()*100;
					int roll=Universe.m_random.nextInt(100);
					Item item=loot.get(i).genLoot();
					if (item!=null)
					{

						if (loot.get(i).isUnique()==2)
						{
							loot.get(i).setUnique(1);
						}
						if (loot.get(i).isUnique()==3)
						{
							loot.get(i).setUnique(1);
							oneper=true;
						}
						if (ItemCoin.class.isInstance(item))
						{
							ItemCoin ic=(ItemCoin)item;
							
							if (!addCoins(ic,list))
							{
								list.add(item);
							}
						}
						else
						{
							list.add(item);
						}
						
					}			
				}
			}

		}
		return list;
	}

}
