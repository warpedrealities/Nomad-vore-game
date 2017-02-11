package shipsystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shared.ParserHelper;


public class ShipResource extends ShipAbility{

	float amountContained;
	int containedCapacity;
	String containsWhat;
	ArrayList<ResourceConversion> resourceConversions;

	public ShipResource(Element node) {
		abilityType=AbilityType.SA_RESOURCE;
		NodeList children=node.getChildNodes();
		resourceConversions=new ArrayList<ResourceConversion>();
		amountContained=00;
		containedCapacity=Integer.parseInt(node.getAttribute("capacity"));
		containsWhat=node.getAttribute("resource");
		
		for (int i=0;i<children.getLength();i++)
		{
			Node N=children.item(i);
			if (N.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)N;
				//run each step successively
				if (Enode.getTagName()=="resourceConversion")
				{
					resourceConversions.add(new ResourceConversion(Enode));
				}
			}		
		}
	}

	public ShipResource(DataInputStream dstream) throws IOException {
		abilityType=AbilityType.SA_RESOURCE;
		
		containsWhat=ParserHelper.LoadString(dstream);
		amountContained=dstream.readFloat();
		containedCapacity=dstream.readInt();
		
		resourceConversions=new ArrayList<ResourceConversion>();
		
		int count=dstream.readInt();
		if (count>0)
		{
			for (int i=0;i<count;i++)
			{
				resourceConversions.add(new ResourceConversion(dstream));
			}		
		}
	
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		
		ParserHelper.SaveString(dstream, containsWhat);
		dstream.writeFloat(amountContained);
		dstream.writeInt(containedCapacity);
		
		dstream.writeInt(resourceConversions.size());
		for (int i=0;i<resourceConversions.size();i++)
		{
			resourceConversions.get(i).save(dstream);
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

	public ArrayList<ResourceConversion> getResourceConversions() {
		return resourceConversions;
	}

	public void addResource(float itemValue) {
		amountContained+=itemValue;
		if (amountContained>containedCapacity)
		{
			amountContained=containedCapacity;
		}
	}

	public void setAmountContained(float amountContained) {
		this.amountContained = amountContained;
	}
	
	
	
}
