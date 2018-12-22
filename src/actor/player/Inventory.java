package actor.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import item.Item;
import item.ItemAmmo;
import item.handlers.StackHandler;
import item.instances.ItemDepletableInstance;
import item.instances.ItemKeyInstance;
import item.instances.ItemStack;
import nomad.universe.Universe;

public class Inventory {

	List<Item> m_items;
	int m_capacity;
	float m_weight;
	Item m_slots[];
	int playerCredits;
	int playerGold;

	public final static int HAND = 0;
	public final static int ACCESSORY = 1;
	public final static int BODY = 2;
	public final static int HEAD = 3;
	public final static int QUICK = 4;

	public List<Item> getItems() {
		return m_items;
	}

	public Inventory(int capacity) {
		m_items = new ArrayList<Item>();
		m_weight = 0;
		m_capacity = capacity;

		m_slots = new Item[5];
		playerCredits = 00;
	}

	public Item getSlot(int i) {
		return m_slots[i];
	}

	public int getWeight() {
		return (int) m_weight;
	}

	public int getCapacity() {
		return m_capacity;
	}

	public void AddItem(Item item) {
		if (Stack(item) == false) {
			m_items.add(item);
		}
		m_weight += item.getWeight();
	}

	boolean Stack(Item item) {
		return StackHandler.handleStacking(item, m_items);

	}

	public Item RemoveItemStack(Item item) {
		ItemStack stack = (ItemStack) item;

		Item r=stack.takeItem();
		if (stack.getCount() == 0) {
			m_items.remove(item);
		}
		m_weight -= item.getItem().getWeight();
		return r;
	}

	public Item RemoveItem(Item item) {
		// check if item is a stack
		if (item.getClass().getName().contains("Stack")) {
			Item it = RemoveItemStack(item);
			return it;
		} else {
			m_items.remove(item);
			m_weight -= item.getWeight();
			return item;
		}
	}

	public int getEncumbrance() {
		float w = m_weight / m_capacity;

		return (int) w + 1;
	}

	public Item getItem(int i) {
		if (i >= m_items.size()) {
			return null;
		}
		return m_items.get(i);
	}

	public int getNumItems() {
		return m_items.size();
	}

	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		// save capacity
		dstream.write(m_capacity);
		// save weight
		dstream.writeFloat(m_weight);
		// save slots
		for (int i = 0; i < m_slots.length; i++) {
			if (m_slots[i] != null) {
				dstream.write(1);
				m_slots[i].save(dstream);
			} else {
				dstream.write(0);
			}
		}
		// save items
		dstream.write(m_items.size());
		for (int i = 0; i < m_items.size(); i++) {
			m_items.get(i).save(dstream);
		}

		dstream.writeInt(playerCredits);
		dstream.writeInt(playerGold);

	}

	public void recalc() {
		m_weight = 0;
		for (int i = 0; i < m_slots.length; i++) {
			if (m_slots[i] != null) {
				m_weight += m_slots[i].getWeight();
			}
		}
		for (int i = 0; i < m_items.size(); i++) {
			m_weight += m_items.get(i).getWeight();
		}
	}

	public Inventory(DataInputStream dstream) throws IOException {
		m_items = new ArrayList<Item>();
		m_slots = new Item[5];
		m_capacity = dstream.read();
		m_weight = dstream.readFloat();

		for (int i = 0; i < m_slots.length; i++) {
			int c = dstream.read();
			if (c == 1) {
				m_slots[i] = Universe.getInstance().getLibrary().getItem(dstream);
			} else {
				m_slots[i] = null;
			}
		}

		int count = dstream.read();
		for (int i = 0; i < count; i++) {
			Item item=Universe.getInstance().getLibrary().getItem(dstream);
			if (item!=null)
			{
				m_items.add(item);
			}

		}

		playerCredits = dstream.readInt();
		playerGold = dstream.readInt();

		recalc();
	}

	public boolean removeItems(String id, int count) {
		for (int i = m_items.size() - 1; i >= 0; i--) {
			if (ItemKeyInstance.class.isInstance(m_items.get(i)) && m_items.get(i).getName().equals(id)) {
				count--;
				m_weight -= m_items.get(i).getWeight();
				m_items.remove(i);
				break;
			}
			if (m_items.get(i).getItem().getName().equals(id)) {
				if (ItemStack.class.isInstance(m_items.get(i))) {
					ItemStack stack = (ItemStack) m_items.get(i);
					if (stack.getCount() > count) {
						m_weight -= count * stack.getItem().getWeight();
						stack.setCount(stack.getCount() - count);
						count = 0;
						break;
					} else {
						m_weight -= stack.getWeight();
						count -= stack.getCount();
						m_items.remove(i);
					}
				} else {

					count--;
					m_weight -= m_items.get(i).getWeight();
					m_items.remove(i);
					if (count <= 0) {
						break;
					}

				}

			}
		}
		if (count <= 0) {
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

	public void setWeight(float weight) {
		m_weight = weight;
	}

	public int countItem(String item) {
		int count = 0;
		for (int i = 0; i < m_items.size(); i++) {
			if (ItemKeyInstance.class.isInstance(m_items.get(i)) && m_items.get(i).getName().equals(item)) {
				count++;
			}
			if (m_items.get(i).getItem().getName().equals(item)) {
				if (ItemStack.class.isInstance(m_items.get(i))) {
					ItemStack stack = (ItemStack) m_items.get(i);
					count += stack.getCount();
				} else {
					count++;
				}
			}
		}
		return count;
	}

	public void setSlot(Item item, int index) {
		m_slots[index] = item;
	}

	public void setItems(List<Item> newlist) {
		m_items = newlist;
	}

	public void sort() {
		for (int i=0;i<m_items.size();i++)
		{
			if (ItemAmmo.class.isInstance(m_items.get(i)))
			{
				ItemAmmo item=(ItemAmmo)m_items.get(i);
				m_items.remove(i);
				m_items.add(new ItemDepletableInstance(item));
			}
		}
		m_items.sort(null);
	}

}
