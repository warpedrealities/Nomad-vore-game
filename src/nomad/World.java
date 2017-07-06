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
import worldgentools.ZoneBuildTools;
import zone.Landing;
import zone.Zone;
import zone.ZoneTools;
import zone.Zone.zoneType;

public class World extends Entity {

	ArrayList <Zone> m_zones;
	ArrayList <Landing> m_landings ;

	float spriteSize;
	String spriteName;
	
	
	public World(String name, int x, int y)
	{
		entityName=name;
		entityPosition=new Vec2f(x,y);

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
	public void Generate()
	{
		if (m_zones==null)
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
			m_landings=new ArrayList<Landing>();
			m_zones=new ArrayList<Zone>();
			Document doc=ParserHelper.LoadXML("assets/data/worlds/"+entityName+".xml");
			Element root=doc.getDocumentElement();
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
						zoneType z=zoneType.SURFACE;
						if (Enode.getAttribute("type").length()>0)
						{
							z=ZoneTools.zoneTypeFromString(Enode.getAttribute("type"));

						}
						m_zones.add(new Zone(node.getTextContent(),
								Integer.parseInt(Enode.getAttribute("x")),
								Integer.parseInt(Enode.getAttribute("y")),
								z,this));
					}
					if (Enode.getTagName()=="ship")
					{
						Spaceship ship=new Spaceship(Enode.getAttribute("name"),
								(int)entityPosition.x,(int)entityPosition.y, ShipState.LAND);
						if (Enode.getAttribute("unusableState").length()>0)
						{
							ship.setUnusableState(Enode.getAttribute("unusableState"));
						}								
						forceLand(ship,Integer.parseInt(Enode.getAttribute("posX")),Integer.parseInt(Enode.getAttribute("posY")));
					}
				}

			}			
	}
	
	public void loadGenerate()
	{
		if (m_zones==null)
		{
			m_landings=new ArrayList<Landing>();
			m_zones=new ArrayList<Zone>();
			Document doc=ParserHelper.LoadXML("assets/data/worlds/"+entityName+".xml");
			Element root=doc.getDocumentElement();
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
						zoneType z=zoneType.SURFACE;
						if (Enode.getAttribute("type").length()>0)
						{
							z=ZoneTools.zoneTypeFromString(Enode.getAttribute("type"));

						}
						m_zones.add(new Zone(node.getTextContent(),
								Integer.parseInt(Enode.getAttribute("x")),
								Integer.parseInt(Enode.getAttribute("y")),
								z,this));
					}
				}

			}		
		}		
	}
	
	@Override
	public Zone getZone(int index)
	{
		return m_zones.get(index);
		
	}
	
	@Override
	public int getNumZones()
	{
		if (m_zones==null)
		{
			return 0;
		}
		return m_zones.size();
	}
	
	public Zone getLandedShipZone(String filename, int x, int y)
	{
		if (m_landings.size()>0)
		{
			for (int i=0;i<m_landings.size();i++)
			{
				if (m_landings.get(i).getShip().getZone(0).getName().contains(filename))
				{
					return m_landings.get(i).getShip().getZone(0);
				}
			}	
		}
		return null;
	}
	
	@Override
	public Zone getZone(String name) {
		for (int i=0;i<m_zones.size();i++)
		{
			if (m_zones.get(i).getName().compareTo(name)==0)
			{
				return m_zones.get(i);

			}
		}
		
		if (m_landings.size()>0)
		{
			for (int i=0;i<m_landings.size();i++)
			{
				if (m_landings.get(i).getShip().getZone(0).getName().equals(name))
				{
					return m_landings.get(i).getShip().getZone(0);
				}
			}
		}
		
		return null;
	}
	
	@Override
	public Zone getZone(String name, int x, int y)
	{
		for (int i=0;i<m_zones.size();i++)
		{
			if (m_zones.get(i).getName().compareTo(name)==0)
			{
				return m_zones.get(i);

			}
		}
		
		if (m_landings.size()>0)
		{
			for (int i=0;i<m_landings.size();i++)
			{
				if (m_landings.get(i).getShip().getZone(0).getName().contains(name))
				{
					if (m_landings.get(i).getX()==x && m_landings.get(i).getY()==y)
					{
						return m_landings.get(i).getShip().getZone(0);	
					}		
				}
			}
		}
		
		return null;
	}
	
	public boolean Land(Spaceship ship)
	{
		
		for (int i=0;i<m_zones.size();i++)
		{
			if (m_zones.get(i).getType()==zoneType.SURFACE)
			{
				if (Land(ship,m_zones.get(i))==true)
				{
					return true;
				}			
			}
		}
		return false;
	}
	
	public boolean LandFree(int x, int y)
	{
		for (int i=0;i<m_landings.size();i++)
		{
			if (m_landings.get(i).getX()==x && m_landings.get(i).getY()==y)
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean Land(Spaceship ship,Zone zone)
	{
		if (LandFree((int)zone.getPosition().x,(int)zone.getPosition().y)==false)
		{
			return false;
		}
		if (zone.getTiles()==null)
		{
			
			int x=8;
			if (zone.getWidth()>16)
			{
				x=Universe.m_random.nextInt(zone.getWidth()-16)+8;
			}
			int y=Universe.m_random.nextInt(zone.getHeight()-16)+8;
			if (zone.getLandingSite()!=null)
			{
				x=(int) (zone.getLandingSite().x-(ship.getSize().x/2));
				y=(int) (zone.getLandingSite().y-(ship.getSize().y/2));
			}
			
			Vec2f p=new Vec2f(x,y);
			Landing l=new Landing(ship, (int)zone.zonePosition.x, (int)zone.zonePosition.y,p);
			
			WidgetPortal portal=ship.getZone(0).getPortalWidget(-101);
			if (portal!=null)
			{
				portal.setDestination(zone.getName(), -101);
			}
			
			m_landings.add(l);
			return true;

		}
		//find a spot in zone	
		int width=(int)ship.getSize().x; int height=(int)ship.getSize().y;
		
		//if we cant do land
		Vec2f p=ZoneTools.getLandingLocation(width,height,zone,ship.getSize().x,ship.getSize().y);
		if (p==null)
		{
			return false;
		}
		else
		{
			Landing l=new Landing(ship, (int)zone.zonePosition.x, (int)zone.zonePosition.y,p);
			m_landings.add(l)
			;		
			//paint the ship onto the map using tile 0
			ZoneBuildTools tools=new ZoneBuildTools(zone,zone.zoneTileGrid);
			tools.AddShip(l);
			ship.Generate();
			ship.getZone(0).getPortalWidget(-101).setDestination(zone.getName(), -101);
			return true;
		}

	}
	
	public boolean forceLand(Spaceship ship, int x, int y)
	{

		for (int i=0;i<m_zones.size();i++)
		{
			if (m_zones.get(i).getType()!=zoneType.CLOSED)
			{
				int x0=(int)m_zones.get(i).zonePosition.x;
				int y0=(int)m_zones.get(i).zonePosition.y;
				if (x==x0 && y==y0)
				{
					return Land(ship,m_zones.get(i));
				}		
			}

		}		
		return false;
	}
	
	public boolean Land(Spaceship ship, int x, int y)
	{

		for (int i=0;i<m_zones.size();i++)
		{
			if (m_zones.get(i).getType()==zoneType.SURFACE)
			{
				int x0=(int)m_zones.get(i).zonePosition.x;
				int y0=(int)m_zones.get(i).zonePosition.y;
				if (x==x0 && y==y0)
				{
					return Land(ship,m_zones.get(i));
				}
				
			}
		}		
		return false;
	}
	
	@Override
	public Element LoadZone(Zone zone)
	{
		Document doc=ParserHelper.LoadXML("assets/data/worlds/"+entityName+"/"+zone.zoneName+".xml");

		//read through the top level nodes
		
	    Element n=(Element)doc.getFirstChild();	
		
		
		return n;
	}
	
	@Override
	public ArrayList<Landing> getLandings() {
		
		return m_landings;
	}

	@Override
	public void Save(String filename) throws IOException
	{
		if (m_zones!=null)
		{	
			File file=new File("saves/"+filename+"/"+entityName+".sav");
			if (file.exists()==false)
			{
				file.createNewFile();
			}
	
			FileOutputStream fstream=new FileOutputStream(file);
			DataOutputStream dstream=new DataOutputStream(fstream);
	
			//save zones
			if (m_zones!=null)
			{
				dstream.writeInt(m_zones.size());
				for (int i=0;i<m_zones.size();i++)
				{
					m_zones.get(i).Save(dstream);
				}
				
			}
			else
			{
				dstream.writeInt(0);
			}
			//save landings
			if (m_landings!=null)
			{
				dstream.writeInt(m_landings.size());
				for (int i=0;i<m_landings.size();i++)
				{
					m_landings.get(i).Save(dstream);
				}
			}
			else
			{
				dstream.writeInt(0);
			}
			dstream.close();
			
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
		if (count>0)
		{
			for (int i=0;i<count;i++)
			{
				m_landings.add(new Landing(dstream));
			}
		}
		count=count+1;
	
		dstream.close();
		fstream.close();
	}

	@Override
	public
	boolean isStatic() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {

	}

	private void loadSprite()
	{
		Document doc=ParserHelper.LoadXML("assets/data/worlds/"+entityName+".xml");
		
	    Element n=(Element)doc.getFirstChild();
		
		NodeList nodeList=n.getElementsByTagName("entitysprite");
		
		Element e=(Element)nodeList.item(0);
		
		spriteSize=Float.parseFloat(e.getAttribute("size"));
		spriteName=e.getAttribute("filename");
		
	}
	
	@Override
	public String getSprite() {
		// TODO Auto-generated method stub
		if (spriteName==null)
		{
			loadSprite();
		}
		return spriteName;
	}

	@Override
	public float getSpriteSize() {
		// TODO Auto-generated method stub
		if (spriteName==null)
		{
			loadSprite();
		}
		return spriteSize;
	}

	@Override
	public void postLoad(Zone zone) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Zone getLandableZone(int x, int y) {

		for (int i=0;i<m_zones.size();i++)
		{
			if (m_zones.get(i).getPosition().x==x && m_zones.get(i).getPosition().y==y
					&& m_zones.get(i).getType()==zoneType.SURFACE)
			{
				return m_zones.get(i);
			}
			
		}
		return null;
	}

	@Override
	public Zone getZone(int x, int y) {
		for (int i=0;i<m_zones.size();i++)
		{
			if (m_zones.get(i).getPosition().x==x && m_zones.get(i).getPosition().y==y)
			{
				return m_zones.get(i);
			}
			
		}
		return null;
	}

	public boolean isLoaded()
	{
		if (m_zones==null)
		{
			return false;
		}
		return true;
	}
}
