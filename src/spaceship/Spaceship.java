package spaceship;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;


import nomad.Entity;
import nomad.Station;
import nomad.Universe;
import nomad.World;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shared.ParserHelper;
import shared.Vec2f;

import widgets.WidgetPortal;
import zone.Landing;
import zone.Zone;




public class Spaceship extends Entity{

	private int UID;
	private int maxHullPoints; 
	private float thrustCost;
	private float moveCost;
	public enum ShipState { SPACE , LAND , DOCK, ADRIFT }
	Zone interiorZone;
	boolean isWrecked;
	String exteriorSprite;
	Vec2f shipSize;

	ShipState shipState;
	SpaceshipStats shipStats;
	ShipController shipController;
	
	public Spaceship(String name, int x, int y,ShipState state)
	{
		UID=Universe.getInstance().getUIDGenerator().getShipUID();
		shipState=state;

		entityPosition=new Vec2f(x,y);
		entityName=name;
		interiorZone=new Zone(name+UID,0,0,false,this);
		entityVisibility=true;
		Document doc=ParserHelper.LoadXML("assets/data/ships/"+entityName+".xml");
		
		//read through the top level nodes
		Element root=doc.getDocumentElement();
	    Element n=(Element)doc.getFirstChild();

	    shipSize=new Vec2f((float)Integer.parseInt(n.getAttribute("width")),(float)Integer.parseInt(n.getAttribute("height")));
	    exteriorSprite=n.getAttribute("sprite");
	    if (n.getAttribute("wrecked")!=null)
	    {
	    	if (Integer.parseInt(n.getAttribute("wrecked"))>0)
	    	{
		    	isWrecked=true;  		
	    	}
	    }
		NodeList children=n.getChildNodes();
		

	}
	
	public String getSprite()
	{
		return exteriorSprite;
	}

	
	public void Generate()
	{
		Document doc=ParserHelper.LoadXML("assets/data/ships/"+entityName+".xml");

		//read through the top level nodes
		Element root=doc.getDocumentElement();
	    Element n=(Element)doc.getFirstChild();
		NodeList children=n.getChildNodes();
		
		for (int i=0;i<children.getLength();i++)
		{
			Node node=children.item(i);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)node;
				if (Enode.getTagName().contains("layout"))
				{
					interiorZone.LoadZone(Enode);
				}	
				if (Enode.getTagName().contains("shipstats"))
				{
					loadStats(Enode);
				}
			}
			
		}
	
	}
	public void generateStats()
	{
		Document doc=ParserHelper.LoadXML("assets/data/ships/"+entityName+".xml");

		//read through the top level nodes
		Element root=doc.getDocumentElement();
	    Element n=(Element)doc.getFirstChild();
		NodeList children=n.getChildNodes();
		
		for (int i=0;i<children.getLength();i++)
		{
			Node node=children.item(i);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)node;
				if (Enode.getTagName().contains("shipstats"))
				{
					loadStats(Enode);
				}
			}
			
		}
	
	}
	private void loadStats(Element n) {

		NodeList children=n.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
			Node node=children.item(i);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)node;
				if (Enode.getTagName().contains("hullpoints"))
				{		
					maxHullPoints=Integer.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName().contains("movecost"))
				{	
					moveCost=Float.parseFloat(Enode.getAttribute("value"));
				}
				if (Enode.getTagName().contains("thrustcost"))
				{		
					thrustCost=Float.parseFloat(Enode.getAttribute("value"));
				}
			}
		}		
	}

	@Override
	public Zone getZone(int index)
	{
		return interiorZone;
		
	}
	
	@Override
	public int getNumZones()
	{
		return 1;
	}
	
	@Override
	public Zone getZone(String name)
	{
		if (interiorZone!=null)
		{
			if (interiorZone.getName().contains(name))
			{
				return interiorZone;
			}
		}
		
		return null;
	}
	
	public Vec2f getSize()
	{
		return shipSize;
	}
	
	@Override
	public Element LoadZone(Zone zone)
	{
		Document doc=ParserHelper.LoadXML("assets/data/ships/"+entityName+".xml");

		//read through the top level nodes
		Element root=doc.getDocumentElement();
	    Element n=(Element)doc.getFirstChild();
		NodeList children=n.getElementsByTagName("layout");
		
		for (int i=0;i<children.getLength();i++)
		{
			Node node=children.item(i);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)node;
				if (Enode.getTagName().contains("layout"))
				{
					return Enode;
				}
			}
		}
		return null;
	}
	
	public int getUID()
	{
		return UID;
	}

	public Element getExterior()
	{
		Document doc=ParserHelper.LoadXML("assets/data/ships/"+entityName+".xml");

		//read through the top level nodes
		Element root=doc.getDocumentElement();
	    Element n=(Element)doc.getFirstChild();
		NodeList children=n.getElementsByTagName("exterior");
		
		for (int i=0;i<children.getLength();i++)
		{
			Node node=children.item(i);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)node;
				if (Enode.getTagName().contains("exterior"))
				{
					return Enode;
				}
			}
		}		
		
		
		
		return null;
	}
	
	public ShipState getState()
	{
		return shipState;
	}

	public void save(DataOutputStream dstream) throws IOException 
	{
		
		//save type
		dstream.writeInt(1);
		dstream.writeInt(UID);
		
		ParserHelper.SaveString(dstream,shipState.toString());
		//save name
		ParserHelper.SaveString(dstream, entityName);
		//save position
		entityPosition.Save(dstream);
		//save wrecked
		dstream.writeBoolean(isWrecked);
		//save sprite
		ParserHelper.SaveString(dstream, exteriorSprite);
		//save size
		shipSize.Save(dstream);
		//save zones
		interiorZone.Save(dstream);
		

		dstream.writeInt(maxHullPoints);
		dstream.writeFloat(thrustCost);
		dstream.writeFloat(moveCost);
	}

	public void load(DataInputStream dstream) throws IOException
	{

		UID=dstream.readInt();
		String str=ParserHelper.LoadString(dstream);
		shipState=ShipState.valueOf(str);
		entityName=ParserHelper.LoadString(dstream);
		entityPosition=new Vec2f(dstream);
		isWrecked=dstream.readBoolean();
		exteriorSprite=ParserHelper.LoadString(dstream);
		shipSize=new Vec2f(dstream);
		interiorZone=new Zone(entityName+UID,0,0,false,this);
		ParserHelper.LoadString(dstream);
		interiorZone.load(dstream);
		

		maxHullPoints=dstream.readInt();
		thrustCost=dstream.readFloat();
		moveCost=dstream.readFloat();
	}
	
	public Spaceship()
	{
		
	}
	
	@Override
	public void Save(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isStatic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Zone getZone(String name, int x, int y) {
		if (interiorZone!=null)
		{
			if (interiorZone.getName().contains(name))
			{
				return interiorZone;
			}
		}
		
		return null;
	}


	public int getMaxHullPoints() {
		return maxHullPoints;
	}



	public float getThrustCost() {
		return thrustCost;
	}

	public boolean isWrecked() {
		return isWrecked;
	}

	public ShipState getShipState() {
		return shipState;
	}

	public SpaceshipStats getShipStats() {
		return shipStats;
	}

	public void setShipStats(SpaceshipStats shipStats) {
		this.shipStats = shipStats;
	}

	public void setShipState(ShipState shipState) {
		this.shipState = shipState;
	}

	public void setPosition(Vec2f pos) {
		this.entityPosition=pos;
		if (spriteObj!=null)
		{
			spriteObj.reposition(pos);
		}
	}

	@Override
	public float getSpriteSize() {
		// TODO Auto-generated method stub
		if (shipSize.x>shipSize.y)
		{
			return shipSize.x/16;
		}
		else
		{
			return shipSize.y/16;
		}
	}

	@Override
	public void update() {

		if (shipStats!=null)
		{
			shipStats.run();
		}
	}

	public ShipController getShipController() {
		return shipController;
	}

	public void setShipController(ShipController shipController) {
		this.shipController = shipController;
	}

	public boolean canThrust() {

		if (shipStats!=null && shipStats.getResource("FUEL")!=null && 
				shipStats.getResource("FUEL").getResourceAmount()>=shipStats.getFuelEfficiency())
		{
			return true;
		}
		return false;
	}

	@Override
	public void postLoad(Zone zone) {
		// TODO Auto-generated method stub
		if (shipState==ShipState.LAND)
		{
			ArrayList<WidgetPortal> portals=zone.getPortalWidgets();
			//find destination zone
			Landing landing=null;
			Entity world=Universe.getInstance().getCurrentWorld(this);

			for (int i=0;i<world.getLandings().size();i++)
			{
				if (world.getLandings().get(i).getShip()==this)
				{
					landing=world.getLandings().get(i);
					break;
				}
			}
			Zone destination=world.getLandableZone(landing.getX(), landing.getY());
			for (int i=0;i<portals.size();i++)
			{
				portals.get(i).setDestination(destination.getName(),portals.get(i).getID());
			}
			
		}
		if (shipState==ShipState.DOCK)
		{
			ArrayList<WidgetPortal> portals=zone.getPortalWidgets();
			
			Station station=(Station)Universe.getInstance().getCurrentWorld(this);
			for (int i=0;i<station.getDocked().length;i++)
			{
				if (station.getDocked()[i]==this)
				{
					portals.get(0).setDestination(station.getZone(i).getName(),portals.get(i).getID());
				}
			}
		}
	}

	@Override
	public Zone getLandableZone(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
