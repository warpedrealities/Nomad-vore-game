package item.instances;

import java.io.DataOutputStream;
import java.io.IOException;

import item.Item;
import item.ItemCapture;
import item.Item.ItemUse;
import shared.ParserHelper;

public class ItemCaptureInstance extends Item {

	ItemCapture hostItem;
	String ship;

	public ItemCaptureInstance(Item item) {
		super(item.getUID());
		this.hostItem = (ItemCapture) item;
	}

	public void setShip(String ship) {
		this.ship = ship;
	}

	public String getShip() {
		return ship;
	}

	@Override
	public ItemUse getUse() {
		return hostItem.getUse();
	}

	@Override
	public String getName() {
		if (ship != null) {
			return hostItem.getName() + ":" + ship;
		} else {
			return hostItem.getName() + ":" + "unset";
		}

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

	public void save(DataOutputStream dstream) throws IOException {
		dstream.write(7);
		ParserHelper.SaveString(dstream, hostItem.getName());
		if (ship != null) {
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, ship);
		} else {
			dstream.writeBoolean(false);
		}
	}

}
