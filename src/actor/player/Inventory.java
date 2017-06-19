package actor.player;

import item.Item;
import item.ItemBlueprintInstance;
import item.ItemExpositionInstance;
import item.ItemHasEnergy;
import item.ItemKeyInstance;
import item.ItemStack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nomad.Universe;

public class Inventory {

	List <Item> m_items;
	int m_capacity;
	float m_weight;
	Item m_slots[];
	int playerCredits;
	int playerGold;
	
	public final static int HAND=0;
	public final static int ACCESSORY=1;
	public final static int BODY=2;
	public final static int HEAD=3;
	public final static int QUICK=4;
	
	public List<Item> getItems()
	{
		return m_items;
	}
	
	public Inventory(int capacity)
	{
		m_items=new ArrayList<Item>();
		m_weight=0;
		m_capacity=capacity;
		
		m_slots=new Item[5];
		
	}


	public Item getSlot(int i)
	{
		return m_slots[i];
	}
	
	public int getWeight()
	{
		return (int)m_weight;
	}
	
	public int getCapacity()
	{
		return m_capacity;
	}
	
	public void AddItem(Item item)
	{
		if (Stack(item)==false)
		{
			m_items.add(item);
		}
		m_weight+=item.getWeight();
	}

	
	boolean Stack(Item item)
	{
		if (ItemExpositionInstance.class.isInstance(item))
		{
			return false;
		}
		if (ItemKeyInstance.class.isInstance(item))
		{
			return false;
		}
		if (ItemBlueprintInstance.class.isInstance(item))
		{
			return false;
		}
		int count=1;
		if (ItemStack.class.isInstance(item))
		{
			count=((ItemStack)item).getCount();
			
		}
		if (ItemHasEnergy.class.isInstance(item.getItem())==true)
		{
			ItemHasEnergy it=(ItemHasEnergy)item.getItem();
			if (it.getEnergy()!=null)
			{
				return false;	
			}	
		}
		//detect if an item is identical
		Item c=null;
		for (int i=0;i<m_items.size();i++)
		{
			if (m_items.get(i).getClass().getName().contains("Stack"))
			{
				ItemStack stack=(ItemStack)m_items.get(i);
				if (stack.getItem().equals(item.getItem()))
				{
					
					//add to stack
					stack.setCount(stack.getCount()+count);
					return true;
				}
			}
			else
			{
				if (m_items.get(i).getItem()==item.getItem())
				{
					m_items.remove(i);
					ItemStack stack=new ItemStack(item,1+count);
					m_items.add(stack);
					return true;
					//remove this item
					//then build a stack with 2 blocks
				}
			}
			
		}
		return false;
	}
	
	public Item RemoveItemStack(Item item)
	{
		ItemStack stack=(ItemStack)item;
		
		stack.setCount(stack.getCount()-1);
		
		if (stack.getCount()==0)
		{
			m_items.remove(item);
		}
		m_weight-=item.getItem().getWeight();
		return stack.getItem();
	}
	
	public Item RemoveItem(Item item)
	{
		//check if item is a stack
		if (item.getClass().getName().contains("Stack"))
		{
			Item it=RemoveItemStack(item);
			//m_weight-=it.getWeight();
			return it;
		}
		else
		{
			m_items.remove(item);
			m_weight-=item.getWeight();
			return item;
		}
	}
	
	public int getEncumbrance()
	{
		float w=m_weight/m_capacity;

		return (int)w+1;
	}
	
	public Item getItem(int i)
	{
		if (i>=m_items.size())
		{
			return null;
		}
		return m_items.get(i);
	}
	
	public int getNumItems()
	{
		return m_items.size();
	}

	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		//save capacity
		dstream.write(m_capacity);
		//save weight
		dstream.writeFloat(m_weight);
		//save slots
		for (int i=0;i<m_slots.length;i++)
		{
			if (m_slots[i]!=null)
			{
				dstream.write(1);
				m_slots[i].save(dstream);
			}
			else
			{
				dstream.write(0);
			}
		}
		//save items
		dstream.write(m_items.size());
		for (int i=0;i<m_items.size();i++)
		{
			m_items.get(i).save(dstream);
		}
		
		dstream.writeInt(playerCredits);
		dstream.writeInt(playerGold);
	
	}
	
	
	public void recalc()
	{
		m_weight=0;
		for (int i=0;i<m_slots.length;i++)
		{
			if (m_slots[i]!=null)
			{
				m_weight+=m_slots[i].getWeight();
			}
		}
		for (int i=0;i<m_items.size();i++)
		{
			m_weight+=m_items.get(i).getWeight();
		}
	}
	
	public Inventory(DataInputStream dstream) throws IOException {
		m_items=new ArrayList<Item>();
		m_slots=new Item[5];
		m_capacity=dstream.read();
		m_weight=dstream.readFloat();
		
		for (int i=0;i<m_slots.length;i++)
		{
			int c=dstream.read();
			if (c==1)
			{
				m_slots[i]=Universe.getInstance().getLibrary().getItem(dstream);
			}
			else
			{
				m_slots[i]=null;
			}
		}
		
		int count=dstream.read();
		for (int i=0;i<count;i++)
		{
			m_items.add(Universe.getInstance().getLibrary().getItem(dstream));
		}		
		
		playerCredits=dstream.readInt();
		playerGold=dstream.readInt();
		recalc();
	}
	
	public boolean removeItems(String id, int count)
	{
		for (int i=m_items.size()-1;i>=0;i--)
		{
			if (ItemKeyInstance.class.isInstance(m_items.get(i)) && m_items.get(i).getName().equals(id))
			{
				count--;
				m_weight-=m_items.get(i).getWeight();
				m_items.remove(i);
				break;
			}
			if (m_items.get(i).getItem().getName().equals(id))
			{
				if (ItemStack.class.isInstance(m_items.get(i)))
				{
					ItemStack stack=(ItemStack)m_items.get(i);
					if (stack.getCount()>count)
					{
						m_weight-=count*stack.getItem().getWeight();
						stack.setCount(stack.getCount()-count);
						count=0;
							break;
					}
					else
					{
						m_weight-=stack.getWeight();
						count-=stack.getCount();
						m_items.remove(i);
					}
				}
				else
				{
					
					count--;
					m_weight-=m_items.get(i).getWeight();
					m_items.remove(i);
					if (count<=0)
					{
						break;
					}
					
				}
			
			}
		}
		if (count<=0)
		{
			return true;
		}
		return false;
	}

	public int getPlayerCredits() {
		return playerCredits;
	}

	public void setPlayerCredits(int playerCredits) {
		this.playerCredits = playerCredits;
	}

	public int getPlayerGold() {
		return playerGold;
	}

	public void setPlayerGold(int playerGold) {
		this.playerGold = playerGold;
	}

	public void setCapacity(int capacity) {
		this.m_capacity = capacity;
	}

	public void setWeight(float weight)
	{
		m_weight=weight;
	}
	
	public int countItem(String item) {
		int count=0;
		for (int i=0;i<m_items.size();i++)
		{
			if (ItemKeyInstance.class.isInstance(m_items.get(i)) &&
				m_items.get(i).getName().equals(item))
			{
				count++;
			}
			if (m_items.get(i).getItem().getName().equals(item))
			{
				if (ItemStack.class.isInstance(m_items.get(i)))
				{
					ItemStack stack=(ItemStack)m_items.get(i);
					count+=stack.getCount();
				}
				else
				{
					count++;
				}
			}
		}
		return count;
	}
	
	public void setSlot(Item item, int index)
	{
		m_slots[index]=item;
	}

	public void setItems(List<Item> newlist) {
		m_items=newlist;
	}

	public void sort() {
		m_items.sort(null);
	}
	
}
