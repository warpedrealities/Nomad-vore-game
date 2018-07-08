package shipsystem.conversionSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import nomad.universe.Universe;
import shared.ParserHelper;
import shipsystem.ShipAbility;
import shipsystem.ShipAbility.AbilityType;
import spaceship.Spaceship;
import spaceship.SpaceshipResource;
import spaceship.stats.SpaceshipAnalyzer;
import spaceship.stats.SpaceshipStats;

public class ShipConverter extends ShipAbility {

	private boolean boostable;
	private ConverterProduct[] products;
	private int productInUse;
	private long lastAccessTimestamp;
	private boolean active;
	private String widgetName;

	public ShipConverter(Element node, String name) {
		abilityType = AbilityType.SA_CONVERTER;
		widgetName = name;
		//active=true;
		NodeList children = node.getChildNodes();

		if (node.getAttribute("boostable").equals("true")) {
			boostable = true;
		}
		
		productInUse=0;
		products=new ConverterProduct[Integer.parseInt(node.getAttribute("count"))];
		int index=0;

		for (int i = 0; i < children.getLength(); i++) {
			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;

				if (Enode.getTagName() == "product") {
					products[index]=new ConverterProduct(Enode);
					index++;
				}
			}
		}

	}

	public String getWidgetName() {
		return widgetName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		if (active == true) {
			lastAccessTimestamp = Universe.getClock();
		}
		this.active = active;
	}

	public ShipConverter(DataInputStream dstream, String m_name) throws IOException {
		abilityType = AbilityType.SA_CONVERTER;
	
		lastAccessTimestamp = dstream.readLong();
		boostable = dstream.readBoolean();
		active = dstream.readBoolean();
		widgetName = ParserHelper.LoadString(dstream);
		
		productInUse=dstream.readInt();
		
		products=new ConverterProduct[dstream.readInt()];
		for (int i=0;i<products.length;i++)
		{
			products[i]=new ConverterProduct(dstream);
		}
		
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {

		dstream.writeLong(lastAccessTimestamp);

		dstream.writeBoolean(boostable);
		dstream.writeBoolean(active);
		ParserHelper.SaveString(dstream, widgetName);
		dstream.writeInt(productInUse);
		
		dstream.writeInt(products.length);
		for (int i=0;i<products.length;i++)
		{
			products[i].save(dstream);
		}		
	}

	public long getLastAccessTimestamp() {
		return lastAccessTimestamp;
	}

	public void setLastAccessTimestamp(long lastAccessTimestamp) {
		this.lastAccessTimestamp = lastAccessTimestamp;
	}

	private int calcTotalTime() {
		return (int) (Universe.getClock() - lastAccessTimestamp);
	}
//
//	private int calcFillTime(SpaceshipStats stats) {
//		float max = stats.getResource(convertTo).getResourceCap();
//		float current = stats.getResource(convertTo).getResourceAmount();
//		float empty = max - current;
//		float interim = empty / conversionEfficiency / conversionRate;
//		return (int) interim;
//	}
//
//	private int calcExpenditureTime(SpaceshipStats stats) {
//		float current = stats.getResource(convertFrom).getResourceAmount();
//		float interim = current / conversionRate;
//		return (int) interim;
//	}
//
//	private void runSolar(SpaceshipStats stats) {
//		int time = 1;
//		SpaceshipResource to = stats.getResource(convertTo);
//		if (to.getResourceAmount()>=to.getResourceCap())
//		{
//			return;
//		}
//		float max = stats.getSolar();
//		if (max > conversionEfficiency) {
//			max = conversionEfficiency;
//		}
//		double outputProduced = time * conversionRate * max;
//		// outputProduced=Math.round(outputProduced * 100d) / 100d;
//		to.setResourceAmount((float) (to.getResourceAmount() + outputProduced));
//	}
//
//	public void run(SpaceshipStats stats,boolean battle) {
//		if ("SOLAR".equals(convertFrom)) {
//			runSolar(stats);
//			return;
//		}
//		if (stats.getResource(convertFrom) == null || stats.getResource(convertTo) == null) {
//			return;
//		}
//		int time = 1;
//		if (boostable && battle)
//		{
//			time=2;
//		}
//		SpaceshipResource from = stats.getResource(convertFrom);
//		SpaceshipResource to = stats.getResource(convertTo);
//		float intakeUse = time * conversionRate;
//		double outputProduced = time * conversionRate * conversionEfficiency;
//		// outputProduced=Math.round(outputProduced * 100d) / 100d;
//		if (from.getResourceAmount() < intakeUse || to.getResourceCap() - to.getResourceAmount() < outputProduced) {
//			return;
//		}
//		from.setResourceAmount(from.getResourceAmount() - intakeUse);
//		to.setResourceAmount((float) (to.getResourceAmount() + outputProduced));
//
//	}
//
//	public void doConversion(SpaceshipStats stats) {
//		if (stats.getResource(convertFrom) == null || stats.getResource(convertTo) == null) {
//			return;
//		}
//
//		int total = calcTotalTime(stats);
//		int fill = calcFillTime(stats);
//		int expend = calcExpenditureTime(stats);
//
//		if (total <= fill && total <= expend) {
//			convertForTime(total, stats);
//		}
//		if (fill <= total && fill <= expend) {
//			convertForTime(fill, stats);
//		}
//		if (expend <= total && expend <= fill) {
//			convertForTime(expend, stats);
//		}
//		lastAccessTimestamp = Universe.getClock();
//	}
//
//	private void convertForTime(int time, SpaceshipStats stats) {
//		float intakeUse = time * conversionRate;
//		double outputProduced = intakeUse * conversionEfficiency;
//		SpaceshipResource from = stats.getResource(convertFrom);
//		SpaceshipResource to = stats.getResource(convertTo);
//		from.setResourceAmount(from.getResourceAmount() - intakeUse);
//		outputProduced = Math.round(outputProduced * 100d) / 100d;
//		to.setResourceAmount((float) (to.getResourceAmount() + outputProduced));
//	}

	public void runConversion() {

		if (active) {
			if (Spaceship.class.isInstance(Universe.getInstance().getCurrentZone().getZoneEntity())) {
				Spaceship ship = (Spaceship) Universe.getInstance().getCurrentZone().getZoneEntity();
				SpaceshipAnalyzer analyzer = new SpaceshipAnalyzer();
				SpaceshipStats stats = analyzer.generateStats(ship);
				doConversion(stats);
				analyzer.decomposeResources(stats, ship);
			}
		}

	}

	public void run(SpaceshipStats spaceshipStats, boolean battle) {
		if (battle && boostable)
		{
			products[productInUse].runConverter(spaceshipStats, 2);
		}
		else
		{
			products[productInUse].runConverter(spaceshipStats, 1);		
		}
	}

	public void doConversion(SpaceshipStats spaceshipStats) {
		int total=calcTotalTime();
		products[productInUse].runConverter(spaceshipStats, total);
		lastAccessTimestamp = Universe.getClock();
	}

	public String getProduct() {
		return products[productInUse].getOutput();
	}

	public ConverterProduct getProductInfo() {
		return products[productInUse];
	}

}
