package item.instances;

import item.Item;
import item.ItemExposition;
import item.Item.ItemUse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.ParserHelper;

public class ItemExpositionInstance extends Item {

	String exposition;
	ItemExposition hostItem;

	public ItemExpositionInstance(int UID) {
		super(UID);
		// TODO Auto-generated constructor stub
	}

	public ItemExpositionInstance(ItemExposition item) {
		super(item.getUID());
		// TODO Auto-generated constructor stub
		hostItem = item;
	}

	@Override
	public ItemUse getUse() {
		return hostItem.getUse();
	}

	@Override
	public String getName() {
		return hostItem.getName() + ":" + exposition;
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

	public String getExposition() {
		return exposition;
	}

	public void setExposition(String exposition) {
		this.exposition = exposition;
	}

	public void save(DataOutputStream dstream) throws IOException {
		dstream.write(3);
		ParserHelper.SaveString(dstream, hostItem.getName());
		ParserHelper.SaveString(dstream, exposition);

	}
	@Override
	public boolean canStack() {
		return false;
	}
}
