package shop.services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import shared.ParserHelper;

public class ShipService {

	public enum ServiceType{repair,refuel};
	
	private int cost;
	
	private ServiceType type;
	
	public ServiceType strToType(String str)
	{
		if (str.equals("REPAIR"))
		{
			return ServiceType.repair;
		}
		if (str.equals("REFUEL"))
		{
			return ServiceType.refuel;
		}
		return null;
	}
	
	public ShipService(Element element)
	{
		type=strToType(element.getAttribute("type"));
		cost=Integer.parseInt(element.getAttribute("cost"));
	}
	
	public ShipService(DataInputStream dstream) throws IOException
	{
		type=ServiceType.valueOf(ParserHelper.LoadString(dstream));
		cost=dstream.readInt();
	}
	
	public void save(DataOutputStream dstream) throws IOException
	{
		ParserHelper.SaveString(dstream,type.toString());
		dstream.writeInt(cost);
	}
	
	public int getCost()
	{
		return cost;
	}
	
	public ServiceType getType()
	{
		return type;
	}
}
