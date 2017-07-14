package item.instances;

import item.Item;
import item.ItemKey;
import item.Item.ItemUse;

import java.io.DataOutputStream;
import java.io.IOException;

import shared.ParserHelper;

public class ItemKeyInstance extends Item {

	private Item hostItem;
	private String lock;

	public ItemKeyInstance(ItemKey item) {
		super(item.getUID());

		hostItem = item;
	}

	public void setLock(String string) {

		lock = string;
	}

	@Override
	public ItemUse getUse() {
		return hostItem.getUse();
	}

	@Override
	public String getName() {
		return lock + " " + hostItem.getName();
	}

	@Override
	public String getDescription() {

		return hostItem.getDescription();
	}

	@Override
	public float getWeight() {
		return hostItem.getWeight();
	}

	@Override
	public Item getItem() {
		return hostItem;
	}

	public String getLock() {
		return lock;
	}

	public void save(DataOutputStream dstream) throws IOException {

		dstream.write(4);
		ParserHelper.SaveString(dstream, hostItem.getName());
		ParserHelper.SaveString(dstream, lock);

	}
}
