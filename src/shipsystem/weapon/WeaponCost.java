package shipsystem.weapon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import shared.ParserHelper;
import shipsystem.ShipAbility.AbilityType;

public class WeaponCost {

	private String resourceName;
	private float cost;

	public WeaponCost(Element element) {
		resourceName = element.getAttribute("resource");
		cost = Float.parseFloat(element.getAttribute("cost"));
	}

	public WeaponCost(DataInputStream dstream) throws IOException {
		resourceName = ParserHelper.LoadString(dstream);
		cost = dstream.readFloat();
	}

	public void save(DataOutputStream dstream) throws IOException {
		ParserHelper.SaveString(dstream, resourceName);
		dstream.writeFloat(cost);
	}

	public String getResourceName() {
		return resourceName;
	}

	public float getCost() {
		return cost;
	}

	@Override
	public String toString() {
		return resourceName + ":" + cost;

	}

}
