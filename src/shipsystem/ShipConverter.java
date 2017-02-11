package shipsystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import nomad.Universe;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shared.ParserHelper;
import shipsystem.ShipAbility.AbilityType;
import spaceship.Spaceship;
import spaceship.SpaceshipAnalyzer;
import spaceship.SpaceshipResource;
import spaceship.SpaceshipStats;

public class ShipConverter extends ShipAbility {

	private boolean spaceOnly;
	private String convertFrom;
	private String convertTo;
	private float conversionRate;
	private float conversionEfficiency;
	private long lastAccessTimestamp;
	private boolean active;
	private String widgetName;
	
	public ShipConverter(Element node,String name) {
		abilityType=AbilityType.SA_CONVERTER;
		widgetName=name;
		NodeList children=node.getChildNodes();
		
		if (node.getAttribute("spaceOnly").equals("true"))
		{
			spaceOnly=true;
		}
		
		for (int i=0;i<children.getLength();i++)
		{
			Node N=children.item(i);
			if (N.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)N;
				
				if (Enode.getTagName()=="convertFrom")
				{
					convertFrom=Enode.getAttribute("value");
				}
				if (Enode.getTagName()=="convertTo")
				{
					convertTo=Enode.getAttribute("value");
				}		
				if (Enode.getTagName()=="conversionEfficiency")
				{
					conversionEfficiency=Float.parseFloat(Enode.getAttribute("value"));
				}
				if (Enode.getTagName()=="conversionRate")
				{
					conversionRate=Float.parseFloat(Enode.getAttribute("value"));
				}
			}
		}
		
	}
	
	
	
	public String getWidgetName() {
		return widgetName;
	}



	public String getConvertFrom() {
		return convertFrom;
	}



	public String getConvertTo() {
		return convertTo;
	}



	public boolean isActive() {
		return active;
	}



	public void setActive(boolean active) {
		if (active==true)
		{
			lastAccessTimestamp=Universe.getClock();
		}
		this.active = active;
	}



	public ShipConverter(DataInputStream dstream, String m_name) throws IOException {
		abilityType=AbilityType.SA_CONVERTER;
		convertFrom=ParserHelper.LoadString(dstream);
		convertTo=ParserHelper.LoadString(dstream);
		conversionRate=dstream.readFloat();
		conversionEfficiency=dstream.readFloat();
		lastAccessTimestamp=dstream.readLong();
		spaceOnly=dstream.readBoolean();
		active=dstream.readBoolean();
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		
		ParserHelper.SaveString(dstream, convertFrom);
		ParserHelper.SaveString(dstream, convertTo);
		dstream.writeFloat(conversionRate);
		dstream.writeFloat(conversionEfficiency);
		dstream.writeLong(lastAccessTimestamp);
		dstream.writeBoolean(spaceOnly);
		dstream.writeBoolean(active);
	}
	
	public long getLastAccessTimestamp() {
		return lastAccessTimestamp;
	}



	public void setLastAccessTimestamp(long lastAccessTimestamp) {
		this.lastAccessTimestamp = lastAccessTimestamp;
	}
	
	private int calcTotalTime(SpaceshipStats stats)
	{
		return (int) (Universe.getClock()-lastAccessTimestamp);
	}
	
	private int calcFillTime(SpaceshipStats stats)
	{
		float max=stats.getResource(convertTo).getResourceCap();
		float current=stats.getResource(convertTo).getResourceAmount();
		float empty=max-current;
		float interim=empty/conversionEfficiency/conversionRate;
		return (int)interim;
	}
	
	private int calcExpenditureTime(SpaceshipStats stats)
	{
		float current=stats.getResource(convertFrom).getResourceAmount();
		float interim=current/conversionRate;
		return (int)interim;
	}

	public void run(SpaceshipStats stats)
	{
		if (stats.getResource(convertFrom)==null || stats.getResource(convertTo)==null)
		{
			return;
		}		
		int time=10;
		SpaceshipResource from=stats.getResource(convertFrom);
		SpaceshipResource to=stats.getResource(convertTo);
		float intakeUse=time*conversionRate;
		double outputProduced=time*conversionRate*conversionEfficiency;
		outputProduced=Math.round(outputProduced * 100d) / 100d;
		if (from.getResourceAmount()<intakeUse || to.getResourceCap()-to.getResourceAmount()<outputProduced)
		{
			return;
		}
		from.setResourceAmount(from.getResourceAmount()-intakeUse);
		to.setResourceAmount((float) (to.getResourceAmount()+outputProduced));

	}

			
	public void doConversion(SpaceshipStats stats)
	{
		if (stats.getResource(convertFrom)==null || stats.getResource(convertTo)==null)
		{
			return;
		}

		int total=calcTotalTime(stats);
		int fill=calcFillTime(stats);
		int expend=calcExpenditureTime(stats);
		
		if (total<=fill && total<=expend)
		{
			convertForTime(total,stats);
		}
		if (fill<=total && fill<=expend)
		{
			convertForTime(fill,stats);
		}
		if (expend<=total && expend<=fill)
		{
			convertForTime(expend,stats);
		}
		lastAccessTimestamp=Universe.getClock();
	}
	
	private void convertForTime(int time, SpaceshipStats stats)
	{
		float intakeUse=time*conversionRate;
		double outputProduced=intakeUse*conversionEfficiency;
		SpaceshipResource from=stats.getResource(convertFrom);
		SpaceshipResource to=stats.getResource(convertTo);
		from.setResourceAmount(from.getResourceAmount()-intakeUse);
		outputProduced=Math.round(outputProduced * 100d) / 100d;
		to.setResourceAmount((float) (to.getResourceAmount()+outputProduced));
	}
	
	public void runConversion() {

		if (active)
		{
			if (Spaceship.class.isInstance(Universe.getInstance().getCurrentZone().getZoneEntity()))
			{
				Spaceship ship=(Spaceship)Universe.getInstance().getCurrentZone().getZoneEntity();
				SpaceshipAnalyzer analyzer=new SpaceshipAnalyzer();
				SpaceshipStats stats=analyzer.generateStats(ship);
					doConversion(stats);
					analyzer.decomposeResources(stats, ship);
			}
					
		}
	
	}

}
