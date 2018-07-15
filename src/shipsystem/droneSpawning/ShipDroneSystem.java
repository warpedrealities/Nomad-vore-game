package shipsystem.droneSpawning;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.xml.soap.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import shared.ParserHelper;
import shipsystem.ShipAbility;
import shipsystem.ShipAbility.AbilityType;

public class ShipDroneSystem extends ShipAbility {

	public enum DroneTrigger {DT_LANDING,DT_BOARDED};
	
	private DroneTrigger droneTrigger;
	private String fileName, npcName, resourceName;
	private int numDeployed, deployCost;
	
	
	public ShipDroneSystem(Element element, String m_name)
	{
		abilityType=AbilityType.SA_SPAWNER;
		NodeList children=element.getChildNodes();
		droneTrigger=DroneTrigger.valueOf(element.getAttribute("trigger"));
		for (int i=0;i<children.getLength();i++)
		{
			if (children.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element enode=(Element)children.item(i);
				if (enode.getTagName().equals("drone"))
				{
					numDeployed=Integer.parseInt(enode.getAttribute("count"));
					fileName=enode.getAttribute("filename");
					npcName=enode.getAttribute("npcName");
				}
				if (enode.getTagName().equals("cost"))
				{
					resourceName=enode.getAttribute("resource");
					deployCost=Integer.parseInt(enode.getAttribute("amount"));
				}
			}
		}
	}
	
	public ShipDroneSystem(DataInputStream dstream, String name) throws IOException {
		abilityType=AbilityType.SA_SPAWNER;	
		droneTrigger=DroneTrigger.values()[dstream.readInt()];
		fileName=ParserHelper.LoadString(dstream);
		npcName=ParserHelper.LoadString(dstream);
		resourceName=ParserHelper.LoadString(dstream);
		numDeployed=dstream.readInt();
		deployCost=dstream.readInt();
	}
	
	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(droneTrigger.ordinal());
		ParserHelper.SaveString(dstream, fileName);
		ParserHelper.SaveString(dstream,npcName);
		ParserHelper.SaveString(dstream, resourceName);
		dstream.writeInt(numDeployed);
		dstream.writeInt(deployCost);
	}
	
	public DroneTrigger getTrigger()
	{
		return droneTrigger;
	}

	public String getFileName() {
		return fileName;
	}

	public String getNpcName() {
		return npcName;
	}

	public String getResourceName() {
		return resourceName;
	}

	public int getNumDeployed() {
		return numDeployed;
	}

	public int getDeployCost() {
		return deployCost;
	}
	
	

}
