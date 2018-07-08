package shipsystem.conversionSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import shared.ParserHelper;
import spaceship.SpaceshipResource;
import spaceship.stats.SpaceshipStats;

public class ConverterProduct {

	private String outputResource;
	List<ProductInput> inputs;
	private float timePerUnit;
	private boolean isSolar;
	
	public ConverterProduct(Element element)
	{
		inputs=new ArrayList<ProductInput>();
		outputResource=element.getAttribute("output");
		timePerUnit=Float.parseFloat(element.getAttribute("timePerUnit"));
		NodeList children=element.getElementsByTagName("input");
		for (int i=0;i<children.getLength();i++)
		{
			Element e=(Element)children.item(i);
			inputs.add(new ProductInput(e));
			if (inputs.get(i).getResourceName().equals("SOLAR"))
			{
				isSolar=true;
			}
		}	
	}
	
	public ConverterProduct(DataInputStream dstream) throws IOException
	{
		inputs=new ArrayList<ProductInput>();
		outputResource=ParserHelper.LoadString(dstream);
		timePerUnit=dstream.readFloat();
		int c=dstream.readInt();
		for (int i=0;i<c;i++)
		{
			inputs.add(new ProductInput(dstream));
			if (inputs.get(i).getResourceName().equals("SOLAR"))
			{
				isSolar=true;
			}
		}	
	}
	
	public void save(DataOutputStream dstream) throws IOException
	{
		ParserHelper.SaveString(dstream, outputResource);
		dstream.writeFloat(timePerUnit);
		dstream.writeInt(inputs.size());
		for (int i=0;i<inputs.size();i++)
		{
			inputs.get(i).save(dstream);
		}		
	}

	private float calcProportionOfUnit(int time)
	{
		float t=(float)time;
		t=t/timePerUnit;
		return t;
	}
	
	private int calcResourceConstraints(SpaceshipStats stats, int time) {
		
		float proportion=calcProportionOfUnit(time);
		
		time=calcCapacityConstraint(stats,time,proportion);
		
		for (int i=0;i<inputs.size();i++)
		{
			time=inputs.get(i).calcResourceConstraint(stats, time, proportion);
		}
		return time;
	}
	
	private int calcCapacityConstraint(SpaceshipStats stats, int time, float proportion) {
		float freeCapacity=stats.getResource(outputResource).getResourceCap()-stats.getResource(outputResource).getResourceAmount();
		if (proportion<=freeCapacity)
		{
			return time;
		}
		else
		{
			float adjusted=proportion/freeCapacity;
			time=(int) (time*adjusted);
		}
		
		return time;
	}

	public void runConverter(SpaceshipStats stats, int time)
	{	

		if (stats.getResource(outputResource)==null || missingInput(stats))
		{
			return;
		}
		if (stats.getResource(outputResource).getResourceAmount()==stats.getResource(outputResource).getResourceCap())
		{
			return;
		}
		if (isSolar())
		{
			runSolar(stats, time);
			return;
		}
		
		time=calcResourceConstraints(stats,time);
		//do the conversion here
		float proportion=calcProportionOfUnit(time);		
		subtractResources(stats, time,proportion);
		addOutput(stats, time,proportion);
		
	}

	private void runSolar(SpaceshipStats stats, int time) {
		float max = stats.getSolar();
		ProductInput input=inputs.get(0);
		if (max > input.getCostPerUnit()) {
			max = input.getCostPerUnit();
		}		
		float t=time;
		float production=t*max/timePerUnit;
		
		SpaceshipResource resource=stats.getResource(outputResource);
		resource.setResourceAmount(resource.getResourceAmount()+production);
		
		if (stats.getResource(outputResource).getResourceAmount()>=stats.getResource(outputResource).getResourceCap())
		{
			stats.getResource(outputResource).setResourceAmount(stats.getResource(outputResource).getResourceCap());
		}
	}

//	private void runSolar(SpaceshipStats stats) {
//	int time = 1;
//	SpaceshipResource to = stats.getResource(convertTo);
//	if (to.getResourceAmount()>=to.getResourceCap())
//	{
//		return;
//	}
//	float max = stats.getSolar();
//	if (max > conversionEfficiency) {
//		max = conversionEfficiency;
//	}
//	double outputProduced = time * conversionRate * max;
//	// outputProduced=Math.round(outputProduced * 100d) / 100d;
//	to.setResourceAmount((float) (to.getResourceAmount() + outputProduced));
//}	
	
	private boolean missingInput(SpaceshipStats stats) {
		for (int i=0;i<inputs.size();i++)
		{
			if (stats.getResource(inputs.get(i).getResourceName())==null)
			{
				return true;
			}
		}
		return false;
	}

	private void addOutput(SpaceshipStats stats, int time, float proportion) {
		SpaceshipResource resource=stats.getResource(outputResource);
		resource.setResourceAmount(resource.getResourceAmount()+proportion);
	}

	private void subtractResources(SpaceshipStats stats, int time,float proportion) {
		for (int i=0;i<inputs.size();i++)
		{
			inputs.get(i).subtractResource(stats,time,proportion);
		}
	}

	public boolean isSolar() {
		return isSolar;
	}

	public String getOutput() {
		return outputResource;
	}

	public List<ProductInput> getInput() {
		return inputs;
	}
	
}
