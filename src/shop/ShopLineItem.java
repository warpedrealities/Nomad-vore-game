package shop;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.ParserHelper;

public class ShopLineItem {

	private String name;
	private String tag;
	private int quantity;
	private float cost;

	public ShopLineItem(String itemName, int quantity2, float cost2) {
		name = itemName;
		quantity = quantity2;
		cost = cost2;
	}

	public int getQuantity() {

		return quantity;
	}

	public String getName() {
		return name;
	}

	public float getCost() {
		return cost;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void save(DataOutputStream dstream) throws IOException {

		dstream.writeInt(quantity);
		dstream.writeFloat(cost);
		if (tag != null) {
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, tag);
		} else {
			dstream.writeBoolean(false);
		}

	}

	public ShopLineItem(String name, DataInputStream dstream) throws IOException {
		this.name = name;
		quantity = dstream.readInt();
		cost = dstream.readFloat();
		boolean b = dstream.readBoolean();
		if (b) {
			tag = ParserHelper.LoadString(dstream);
		}
	}

}
