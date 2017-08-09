package shipsystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import shared.ParserHelper;

public class ResourceConversion {

	String itemName;
	float itemValue;

	public ResourceConversion(Element enode) {

		itemName = enode.getAttribute("itemName");
		itemValue = Float.parseFloat(enode.getAttribute("value"));
	}

	public ResourceConversion(DataInputStream dstream) throws IOException {

		itemName = ParserHelper.LoadString(dstream);
		itemValue = dstream.readFloat();
	}

	public void save(DataOutputStream dstream) throws IOException {

		ParserHelper.SaveString(dstream, itemName);
		dstream.writeFloat(itemValue);
	}

	public String getItemName() {
		return itemName;
	}

	public float getItemValue() {
		return itemValue;
	}

}
