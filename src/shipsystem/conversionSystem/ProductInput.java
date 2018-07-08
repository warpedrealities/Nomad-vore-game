package shipsystem.conversionSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import shared.ParserHelper;
import spaceship.SpaceshipResource;
import spaceship.stats.SpaceshipStats;

public class ProductInput {
	private String resourceName;
	private float costPerUnit;
	
	public ProductInput(Element element)
	{
		resourceName=element.getAttribute("resource");
		costPerUnit=Float.parseFloat(element.getAttribute("perUnit"));
	}
	
	public ProductInput(DataInputStream dstream) throws IOException
	{
		resourceName=ParserHelper.LoadString(dstream);
		costPerUnit=dstream.readFloat();
	}
	
	public void save(DataOutputStream dstream) throws IOException
	{
		ParserHelper.SaveString(dstream, resourceName);
		dstream.writeFloat(costPerUnit);
	}
	
	public String getResourceName()
	{
		return resourceName;
	}
	
	public float getCostPerUnit()
	{
		return costPerUnit;
	}

	public int calcResourceConstraint(SpaceshipStats stats, int time, float proportion) {
		
		float resource=stats.getResource(resourceName).getResourceAmount();
		float rUse=proportion*costPerUnit;
		if (rUse<=resource)
		{
			return time;
		}
		else
		{
			float adjusted=rUse/resource;
			time=(int) (time*adjusted);				
		}
		return time;
	}

	public void subtractResource(SpaceshipStats stats, int time, float proportion) {
		float rUse=proportion*costPerUnit;
		SpaceshipResource resource=stats.getResource(resourceName);
		resource.setResourceAmount(resource.getResourceAmount()-rUse);

	}
}
