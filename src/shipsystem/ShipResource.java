package shipsystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import nomad.universe.Universe;
import shared.ParserHelper;
import widgets.WidgetItemPile;

public class ShipResource extends ShipAbility {

	private class ResourceExtraction
	{
		public String itemName;
		public int ratio;
		public float destroyValue,dismantleValue;
		
		private void save(DataOutputStream dstream) throws IOException
		{
			ParserHelper.SaveString(dstream, itemName);
			dstream.writeInt(ratio);
			dstream.writeFloat(destroyValue);
			dstream.writeFloat(dismantleValue);
		}
		
		private void load(DataInputStream dstream) throws IOException
		{
			itemName=ParserHelper.LoadString(dstream);
			ratio=dstream.readInt();
			destroyValue=dstream.readFloat();
			dismantleValue=dstream.readFloat();
		}
		
		private ResourceExtraction()
		{
			
		}
		
		private ResourceExtraction(Element enode)
		{
			itemName=enode.getAttribute("item");
			ratio=Integer.parseInt(enode.getAttribute("ratio"));
			destroyValue=Float.parseFloat(enode.getAttribute("destroy"));
			dismantleValue=Float.parseFloat(enode.getAttribute("dismantle"));
		}
	}
	
	private float amountContained;
	private int containedCapacity;
	private String containsWhat;
	private boolean nonCombat;
	private ResourceExtraction extraction;
	
	public ShipResource(Element node) {
		abilityType = AbilityType.SA_RESOURCE;
		NodeList children = node.getChildNodes();
		amountContained = 00;
		containedCapacity = Integer.parseInt(node.getAttribute("capacity"));
		containsWhat = node.getAttribute("resource");
		if ("true".equals(node.getAttribute("nonCombat")))
		{
			nonCombat=true;
		}
		for (int i = 0; i < children.getLength(); i++) {
			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;
				// run each step successively
				if (Enode.getTagName().equals("resourceExtraction"))
				{
					extraction=new ResourceExtraction(Enode);
				}
			}
		}
	}

	public ShipResource(DataInputStream dstream) throws IOException {
		abilityType = AbilityType.SA_RESOURCE;

		containsWhat = ParserHelper.LoadString(dstream);
		amountContained = dstream.readFloat();
		containedCapacity = dstream.readInt();
		nonCombat=dstream.readBoolean();
		if (dstream.readBoolean())
		{
			extraction=new ResourceExtraction();
			extraction.load(dstream);
		}
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {

		ParserHelper.SaveString(dstream, containsWhat);
		dstream.writeFloat(amountContained);
		dstream.writeInt(containedCapacity);
		dstream.writeBoolean(nonCombat);
		if (extraction!=null)
		{
			dstream.writeBoolean(true);
			extraction.save(dstream);
		}
		else
		{
			dstream.writeBoolean(false);
		}
	}

	public float getAmountContained() {
		return amountContained;
	}

	public int getContainedCapacity() {
		return containedCapacity;
	}

	public String getContainsWhat() {
		return containsWhat;
	}

	public void addResource(float itemValue) {
		amountContained += itemValue;
		if (amountContained > containedCapacity) {
			amountContained = containedCapacity;
		}
	}

	public void setAmountContained(float amountContained) {
		this.amountContained = amountContained;
	}

	public boolean isNonCombat() {
		return nonCombat;
	}

	public void extractResources(WidgetItemPile pile, boolean dismantle) {
		if (extraction==null)
		{
			return;
		}
		float amount=amountContained;
		if (dismantle)
		{
			amount=amount*extraction.dismantleValue;
		}
		else
		{
			amount=amount*extraction.destroyValue;
		}
		int count=(int)amount/extraction.ratio;
		if (count<=0)
		{
			return;
		}
		for (int i=0;i<count;i++)
		{
			pile.AddItem(Universe.getInstance().getLibrary().getItem(extraction.itemName));
		}	
	}

}
