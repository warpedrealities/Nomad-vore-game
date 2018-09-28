package item.instances;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import item.Item;
import item.ItemConsumable;
import item.ItemContainer;
import item.ItemResource;
import nomad.universe.Universe;
import shared.ParserHelper;

public class ItemContainerInstance extends Item {

	private ItemContainer hostItem;
	private List<Item> containedItems;
	private float containedWeight;
	
	public ItemContainerInstance(Item item) {
		super(item.getUID());
		hostItem=(ItemContainer)item;
		
		containedItems=new ArrayList<Item>();
		
	}
	
	@Override
	public String getName() {
		return hostItem.getName();
	}
	
	@Override
	public ItemUse getUse() {
		return hostItem.getUse();
	}
	
	@Override
	public String getDescription() {
		return hostItem.getDescription();
	}
	
	@Override
	public float getWeight() {
		return hostItem.getWeight()+(containedWeight*hostItem.getWeightRatio());
	}
	
	
	public void load(DataInputStream dstream) throws IOException {

		int c=dstream.readInt();
		for (int i=0;i<c;i++)
		{
			Item item=Universe.getInstance().getLibrary().getItem(dstream);
			containedWeight+=item.getWeight();
			containedItems.add(item);
		}
	}
	
	@Override
	public void save(DataOutputStream dstream) throws IOException
	{
		dstream.write(8);
		ParserHelper.SaveString(dstream, hostItem.getName());
		
		dstream.writeInt(containedItems.size());
		
		for (int i=0;i<containedItems.size();i++)
		{
			containedItems.get(i).save(dstream);
		}
	}

	public List<Item> getItems() {
		return containedItems;
	}

	public int getContainedWeight() {
		return (int) containedWeight;
	}
	

	public int getAdjustedContentWeight() {
		return (int) (containedWeight*hostItem.getWeightRatio());
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

	private boolean stack(Item item) {
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
	}	
	
	public boolean addItem(Item item) {
		if (item.getTag()!=hostItem.getContainedTag())
		{
			return false;
		}
		if (item.getWeight() + containedWeight > hostItem.getCapacity()) {
			return false;
		} else {
			if (stack(item) == false) {
				containedItems.add(item);
				containedWeight += item.getWeight();
			}
			return true;

		}

	}
	
	public float getItemValue() {
		return hostItem.getItemValue();
	}
	@Override
	public boolean canStack() {
		return false;
	}
}
