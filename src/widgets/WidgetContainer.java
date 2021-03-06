package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import actor.player.Player;
import interactionscreens.ContainerScreen;
import item.Item;
import item.ItemCoin;
import item.handlers.StackHandler;
import item.instances.ItemStack;
import nomad.universe.Universe;
import view.ViewScene;

public class WidgetContainer extends WidgetBreakable {

	private int maxWeight = 100;
	private List<Item> containedItems;
	private float containedWeight;
	private boolean opened;
	
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

	}

	public boolean addStack(ItemStack stack)
	{
		if (stack.getWeight() + containedWeight > maxWeight) {
			return false;
		} else {
			if (!stack(stack)) {
				containedItems.add(stack);
				containedWeight += stack.getWeight();
			}
			else
			{
				containedWeight += stack.getWeight();	
			}
			return true;

		}		
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
			else
			{
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

				Item it = stack.takeItem();
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
		opened=true;
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
		dstream.writeBoolean(opened);
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
		opened=dstream.readBoolean();

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

	public void setContainedWeight(float containedWeight) {
		this.containedWeight = containedWeight;
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
	
	public String getDescription() {
		if (opened)
		{
			if (containedWeight==0)
			{
				return widgetDescription+"(empty)";				
			}
			return widgetDescription+"(opened)";			
		}
		return widgetDescription;
	}
	
	@Override
	public void handleDismantle(WidgetItemPile pile) {
		while(containedItems!=null && !containedItems.isEmpty())
		{
			pile.AddItem(takeItem(0));
		}
	}
}
