package nomad;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shared.ParserHelper;
import shared.Vec2f;
import spaceship.Spaceship;
import spaceship.Spaceship.ShipState;

import widgets.WidgetPortal;
import zone.Landing;
import zone.Tile;
import zone.Zone;

public class Station extends Entity {

	ArrayList <Zone> stationZones;
	Spaceship [] dockedShips;
	
	float spriteSize;
	String spriteName;
	
	public Station(String name, int x, int y) {
		entityName=name;
		entityPosition=new Vec2f(x,y);

	}

	@Override
	public void postLoad(Zone zone) {
		Spaceship dockedship=null;
		for (int i=0;i<stationZones.size();i++)
		{
			if (i>=dockedShips.length)
			{
				break;
			}
			if (zone==stationZones.get(i))
			{
				dockedship=dockedShips[i];
				break;
			}
		}
		if (dockedship!=null)
		{
			for (int i=0;i<zone.getWidth();i++)
			{
				for (int j=0;j<zone.getHeight();j++)
				{
					if (zone.getTile(i, j)!=null && zone.getTile(i, j).getWidgetObject()!=null)
					{
						if (WidgetPortal.class.isInstance(zone.getTile(i, j).getWidgetObject()))
						{
							WidgetPortal widget=(WidgetPortal)zone.getTile(i, j).getWidgetObject();
							if (widget.getPortalID()==-101)
							{
								widget.setDestination(dockedship.getZone(0).getName(), -101);
							}
						}
					}
				}
			}
		}
	}

	private boolean FileExists()
	{
		if (Universe.getInstance().getSaveName()!=null)
		{
			File file=new File("saves/"+Universe.getInstance().getSaveName()+"/"+entityName+".sav");
			if (file.exists())
			{
				return true;
			}		
		}

		return false;
	}
	
	
	@Override
	public void Generate() {
		// TODO Auto-generated method stub
		if (stationZones==null)
		{
			//check if there exists a world file in the folder
			if (FileExists())
			{
				loadGenerate();
				try {
					load();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			//if not normal load
			{
				firstGenerate();
			}
		}
	}

	void firstGenerate()
	{
			stationZones=new ArrayList<Zone>();
			Document doc=ParserHelper.LoadXML("assets/data/stations/"+entityName+".xml");
			
		    Element n=(Element)doc.getFirstChild();
			NodeList children=n.getChildNodes();
			dockedShips=new Spaceship[Integer.parseInt(n.getAttribute("numDocks"))];
			for (int i=0;i<children.getLength();i++)
			{
				Node node=children.item(i);
				if (node.getNodeType()==Node.ELEMENT_NODE)
				{
					Element Enode=(Element)node;
					if (Enode.getTagName()=="zone")
					{
						//add this world
						boolean surface=false;
	
						stationZones.add(new Zone(node.getTextContent(),
								Integer.parseInt(Enode.getAttribute("x")),
								Integer.parseInt(Enode.getAttribute("y")),
								surface,this));
					}
					if (Enode.getTagName()=="ship")
					{
						int index=Integer.parseInt(Enode.getAttribute("dock"));
						dockedShips[index]=new Spaceship(Enode.getAttribute("name"),
								(int)entityPosition.x,(int)entityPosition.y, ShipState.DOCK);
						dockedShips[index].setPosition(new Vec2f(entityPosition.x,entityPosition.y));
					}
				}

			}			
	}
	
	public void loadGenerate()
	{
		if (stationZones==null)
		{

			stationZones=new ArrayList<Zone>();
			Document doc=ParserHelper.LoadXML("assets/data/stations/"+entityName+".xml");
			
		    Element n=(Element)doc.getFirstChild();
			NodeList children=n.getChildNodes();
			for (int i=0;i<children.getLength();i++)
			{
				Node node=children.item(i);
				if (node.getNodeType()==Node.ELEMENT_NODE)
				{
					Element Enode=(Element)node;
					if (Enode.getTagName()=="zone")
					{
						//add this world
						boolean surface=false;
						if (Enode.getAttribute("surface")!=null)
						{
							if (Integer.parseInt(Enode.getAttribute("surface"))>0)
							{
								surface=true;
							}
						}
						stationZones.add(new Zone(node.getTextContent(),
								Integer.parseInt(Enode.getAttribute("x")),
								Integer.parseInt(Enode.getAttribute("y")),
								surface,this));
					}
				}

			}		
		}		
	}
	
	@Override
	public Zone getZone(int index)
	{
		return stationZones.get(index);
		
	}
	
	@Override
	public Zone getZone(String name, int x, int y) {

		return getZone(name);
	}

	@Override
	public Zone getZone(String name) {
		for (int i=0;i<stationZones.size();i++)
		{
			if (stationZones.get(i).getName().compareTo(name)==0)
			{
				return stationZones.get(i);

			}
		}
		
		for (int i=0;i<dockedShips.length;i++)
		{
			if (dockedShips[i]!=null)
			{
				if (dockedShips[i].getZone(0).getName().contains(name))
				{
					return dockedShips[i].getZone(0);
				}
			}
		}
		return null;
	}
	
	@Override
	public int getNumZones()
	{
		return stationZones.size();
	}

	@Override
	public Element LoadZone(Zone zone)
	{
		Document doc=ParserHelper.LoadXML("assets/data/stations/"+entityName+"/"+zone.zoneName+".xml");

		//read through the top level nodes
		
	    Element n=(Element)doc.getFirstChild();	
	
		return n;
	}

	@Override
	public void Save(String filename) throws IOException {
		// TODO Auto-generated method stub
		if (stationZones!=null)
		{	
			File file=new File("saves/"+filename+"/"+entityName+".sav");
			if (file.exists()==false)
			{
				file.createNewFile();
			}
	
			FileOutputStream fstream=new FileOutputStream(file);
			DataOutputStream dstream=new DataOutputStream(fstream);
	
			//save zones
			if (stationZones!=null)
			{
				dstream.writeInt(stationZones.size());
				for (int i=0;i<stationZones.size();i++)
				{
					stationZones.get(i).Save(dstream);
				}
				
			}
			else
			{
				dstream.writeInt(0);
			}
			if (dockedShips==null)
			{
				dstream.writeInt(0);
			}
			else
			{
				dstream.writeInt(dockedShips.length);
				for (int i=0;i<dockedShips.length;i++)
				{
					if (dockedShips[i]==null)
					{
						dstream.writeBoolean(false);
					}
					else
					{
						dstream.writeBoolean(true);
						dockedShips[i].save(dstream);
					}
				}
			}
		}

		
	}
	void load() throws IOException
	{

		File file=new File("saves/"+Universe.getInstance().getSaveName()+"/"+entityName+".sav");

		FileInputStream fstream=new FileInputStream(file);
		DataInputStream dstream=new DataInputStream(fstream);
		
		//read number of zone entries
		int count=dstream.readInt();
		for (int i=0;i<count;i++)
		{
			String name=ParserHelper.LoadString(dstream);
			//find zone
			Zone zone=getZone(name);
			if (zone!=null)
			{
				zone.load(dstream);
			}
		}
		count=dstream.readInt();
		dockedShips=new Spaceship[count];
		for (int i=0;i<count;i++)
		{
			boolean b=dstream.readBoolean();
			if (b==true)
			{
				dockedShips[i]=new Spaceship();
				int mystery=dstream.readInt();
				dockedShips[i].load(dstream);
			}
		}

	}
	@Override
	public boolean isStatic() {
		
		return true;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		

	}

	private void loadSprite()
	{
		Document doc=ParserHelper.LoadXML("assets/data/stations/"+entityName+".xml");

	    Element n=(Element)doc.getFirstChild();
		
		NodeList nodeList=n.getElementsByTagName("entitysprite");
		
		Element e=(Element)nodeList.item(0);
		
		spriteSize=Float.parseFloat(e.getAttribute("size"));
		spriteName=e.getAttribute("filename");
		
	}
	
	@Override
	public String getSprite() {
		if (spriteName==null)
		{
			loadSprite();
		}
		return spriteName;
	}

	@Override
	public float getSpriteSize() {
		if (spriteName==null)
		{
			loadSprite();
		}
		return spriteSize;
	}

	@Override
	public Zone getLandableZone(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean dock(Spaceship ship) {

		for (int i=0;i<dockedShips.length;i++)
		{
			if (dockedShips[i]==null)
			{
				dockedShips[i]=ship;
				//link up
				WidgetPortal portal=ship.getZone(0).getPortalWidget(-101);
				portal.setDestination(stationZones.get(i).getName(), -101);
				stationZones.get(i).LoadZone();
				portal=stationZones.get(i).getPortalWidget(-101);
				portal.setDestination(ship.getZone(0).getName(), -101);
				return true;
			}
		}
		return false;
	}

	public Spaceship [] getDocked() {
		return dockedShips;
	}

	public void unDock(Spaceship ship) {
		// TODO Auto-generated method stub
		for (int i=0;i<dockedShips.length;i++)
		{
			if (dockedShips[i]==ship)
			{
				Zone z=stationZones.get(i);
				for (int j=0;j<z.getWidth();j++)
				{
					for (int k=0;k<z.getHeight();k++)
					{
						if (z.getTile(j, k)!=null)
						{
							Tile t=z.getTile(j,k);
							if (t.getWidgetObject()!=null)
							{
								if (WidgetPortal.class.isInstance(t.getWidgetObject()))
								{
									WidgetPortal portal=(WidgetPortal)t.getWidgetObject();
									if (portal.getPortalID()==-101)
									{
										portal.setDestination(null, -101);
										break;
									}
									
								}
							}
						}
				
					}
				}
				dockedShips[i]=null;
			}
		}
	}

}
