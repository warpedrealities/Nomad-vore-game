package widgets;

import interactionscreens.ContainerScreen;
import item.Item;
import item.ItemCoin;
import item.ItemConsumable;
import item.ItemResource;
import item.handlers.StackHandler;
import item.instances.ItemStack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.player.Player;
import nomad.Universe;

import shared.ParserHelper;
import view.ViewScene;
import actorRPG.RPG_Helper;

public class WidgetContainer extends WidgetBreakable {

	private int maxWeight = 100;
	private List<Item> containedItems;
	private float containedWeight;

	public WidgetContainer(int sprite, String description, String name, Item[] contains, int hp, int[] resistances) {
		super(sprite, description, name, contains, hp, resistances);

	}

	public void setItems(List<Item> items) {
		containedItems = items;
		CalcWeight();
	}

	private void CalcWeight() {
		containedWeight = 0;
		if (containedItems != null) {
			if (containedItems.size() > 0) {
				for (int i = 0; i < containedItems.size(); i++) {
					containedWeight += containedItems.get(i).getWeight();
				}
			}
		}
	}

	private boolean stack(Item item) {
		return StackHandler.handleStacking(item, containedItems);
		/*
		if (ItemResource.class.isInstance(item) || ItemConsumable.class.isInstance(item)) {
			Item id = item.getItem();
			for (int i = 0; i < containedItems.size(); i++) {
				if (containedItems.get(i).getItem() == id) {
					if (ItemStack.class.isInstance(containedItems.get(i))) {
						ItemStack stack = (ItemStack) containedItems.get(i);
						stack.setCount(stack.getCount() + 1);
						containedWeight += item.getWeight();
						return true;
					} else {
						ItemStack stack = new ItemStack(containedItems.get(i).getItem(), 2);
						containedItems.remove(i);
						containedItems.add(stack);
						containedWeight += item.getWeight();
						return true;
					}
				}
			}
		}

		return false;
		*/
	}

	public boolean addItem(Item item) {
		if (ItemCoin.class.isInstance(item)) {
			if (addToCoins((ItemCoin) item)) {
				return true;
			}
		}
		if (item.getWeight() + containedWeight > maxWeight) {
			return false;
		} else {
			if (stack(item) == false) {
				containedItems.add(item);
				containedWeight += item.getWeight();
			}
			return true;

		}

	}

	private boolean addToCoins(ItemCoin item) {
		for (int i = 0; i < containedItems.size(); i++) {
			if (ItemCoin.class.isInstance(containedItems.get(i))) {
				ItemCoin ic = (ItemCoin) containedItems.get(i);
				ic.setCount(ic.getCount() + item.getCount());
				return true;
			}
		}
		return false;
	}

	public Item takeItem(int index) {
		if (index < containedItems.size()) {
			Item item = containedItems.get(index);
			if (ItemStack.class.isInstance(item)) {
				ItemStack stack = (ItemStack) item;

				Item it = stack.getItem();
				stack.setCount(stack.getCount() - 1);
				if (stack.getCount() <= 0) {
					containedItems.remove(index);
				}
				containedWeight -= it.getWeight();
				return it;
			} else {
				containedItems.remove(index);
				containedWeight -= item.getWeight();
				return item;
			}

		}
		return null;
	}

	public int getContainedWeight() {
		return (int) containedWeight;
	}

	public List<Item> getItems() {
		return containedItems;
	}

	@Override
	protected void destroy() {
		if (slottedWidget == false) {
			WidgetItemPile pile = null;
			if (m_contains != null) {
				pile = new WidgetItemPile(2, "a pile of items containing ", m_contains[0]);
			} else {
				pile = new WidgetItemPile(2, "a pile of items containing ", null);
			}

			if (m_contains != null) {

				if (m_contains.length > 1) {
					for (int j = 1; j < m_contains.length; j++) {
						pile.AddItem(m_contains[j]);
					}
				}

			}
			if (containedItems != null) {
				if (containedItems.size() > 0) {
					for (int j = 0; j < containedItems.size(); j++) {
						pile.AddItem(containedItems.get(j));
					}
				}
			}
			if (pile.getCount() > 0) {
				ViewScene.m_interface.ReplaceWidget(this, pile);
			} else {
				ViewScene.m_interface.RemoveWidget(this);
			}
		}
	}

	@Override
	public Item[] getContained() {
		Item[] list = new Item[m_contains.length + containedItems.size()];
		int index = 0;
		for (int i = 0; i < m_contains.length; i++) {
			list[index] = m_contains[i];
			index++;
		}
		for (int i = 0; i < containedItems.size(); i++) {
			list[index] = containedItems.get(i);
			index++;
		}
		return list;
	}

	@Override
	public boolean Interact(Player player) {
		// open container view

		ViewScene.m_interface.setScreen(new ContainerScreen(this));

		return true;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.write(7);
		commonSave(dstream);
		super.saveBreakable(dstream);
		if (containedItems != null) {
			dstream.writeInt(containedItems.size());
			for (int i = 0; i < containedItems.size(); i++) {
				containedItems.get(i).save(dstream);
			}
		} else {
			dstream.writeInt(0);
		}
		dstream.writeInt(maxWeight);
	}

	public WidgetContainer(DataInputStream dstream) throws IOException {

		commonLoad(dstream);
		load(dstream);
		int c = dstream.readInt();
		if (c > 0) {
			containedItems = new ArrayList<Item>();
			for (int i = 0; i < c; i++) {
				containedItems.add(Universe.getInstance().getLibrary().getItem(dstream));
			}
		}
		maxWeight=dstream.readInt();
		CalcWeight();

	}

	public WidgetContainer(Element node) {
		super(node);
		if (node.getAttribute("capacity").length()>0)
		{
			maxWeight=Integer.parseInt(node.getAttribute("capacity"));
		}
	}

	public void addItems(ArrayList<Item> generateLoot) {
		if (containedItems == null) {
			containedItems = new ArrayList<Item>();
		}
		for (int i = 0; i < generateLoot.size(); i++) {
			addItem(generateLoot.get(i));
		}
	}

	public int getMaxWeight() {
		return maxWeight;
	}

	public void sort() {
		//recalc weight
		containedWeight=0;
		for (int i=0;i<containedItems.size();i++)
		{
			float w=containedItems.get(i).getWeight();
			containedWeight+=w;
		}
		containedItems.sort(null);
	}

}
