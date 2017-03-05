package zone;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import nomad.Entity_Int;
import nomad.Universe;
import nomad.World;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.w3c.dom.Element;

import actor.Actor;
import actor.ActorLoader;
import actor.NPC;
import actor.Player;
import actorRPG.Actor_RPG;
import artificial_intelligence.pathfinding.Path;
import artificial_intelligence.pathfinding.Pathfinder;


import rlforj.los.ILosBoard;
import rlforj.util.BresenhamLine;
import shared.ParserHelper;
import shared.Vec2f;
import spaceship.Spaceship;
import spaceship.Spaceship.ShipState;
import view.ModelController_Int;
import view.ZoneInteractionHandler;
import vmo.GameManager;
import widgets.Widget;
import widgets.WidgetPortal;
import widgets.spawner.WidgetSpawner;
import worldgentools.ZoneBuildTools;
import zone.TileDef.TileMovement;
import zonePreload.ZonePreload;
import zonePreload.ZonePreloadController;

public class Zone implements ILosBoard, Zone_int{

	public String zoneName;
	public String zoneDescription;
	private String zoneRules;
	public TileDefLibrary zoneTileLibrary;
	public String tilesetName;
	public Vec2f zonePosition;
	public boolean isSurfaceZone;
	private boolean isVisited;
	public Tile [][]zoneTileGrid;
	public ZonePreloadController preload;
	private int violationLevel;
	
	public void setZoneTileGrid(Tile[][] zoneTileGrid) {
		this.zoneTileGrid = zoneTileGrid;
	}

	public ArrayList <Actor> zoneActors;
	public int zoneWidth,zoneHeight;	
	
	public String adjacentZoneNames[];
	
	public Entity_Int zoneEntity;
	
	public ModelController_Int viewInterface;
	
	Vec2f fixedLandingSite;
	
	public void addPreload(ZonePreload zonePreload)
	{
		if (zoneTileGrid!=null)
		{
			return;
		}
		if (preload==null)
		{
			preload=new ZonePreloadController();
		}
		preload.addPreload(zonePreload);
		
	}
	
	public TileDefLibrary getZoneTileLibrary() {
		return zoneTileLibrary;
	}


	public String getZoneRules() {
		return zoneRules;
	}



	public Zone (String filename,int x, int y,boolean surface,Entity_Int entityint)
	{
		adjacentZoneNames=new String[4];
		zoneName=filename;
		isSurfaceZone=surface;
		zonePosition=new Vec2f(x,y);
		zoneActors=new ArrayList<Actor>();
		zoneEntity=entityint;
	}
	
	
	
	public boolean isVisited() {
		return isVisited;
	}



	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}



	public int getViolationLevel() {
		return violationLevel;
	}



	public void setViolationLevel(int violationLevel) {
		this.violationLevel = violationLevel;
	}



	public Vec2f getPortal(int id)
	{
		for (int i=0;i<zoneWidth;i++)
		{
			for (int j=0;j<zoneHeight;j++)
			{
				if (zoneTileGrid[i][j]!=null)
				{
					if (zoneTileGrid[i][j].getWidgetObject()!=null)
					{
						if (WidgetPortal.class.isInstance(zoneTileGrid[i][j].getWidgetObject()))
						{
							WidgetPortal portal=(WidgetPortal)zoneTileGrid[i][j].getWidgetObject();
							if (portal.getID()==id)
							{
								Vec2f p=new Vec2f(i,j);
								p=ZoneInteractionHandler.getPos(portal.getPortalFacing(),p);
								return p;
							}
						}
						
					}
				}
			}
			
		}		
		return null;
	}
	public ArrayList<WidgetPortal> getPortalWidgets()
	{
		ArrayList<WidgetPortal> portals=new ArrayList<WidgetPortal>();
		for (int i=0;i<zoneWidth;i++)
		{
			for (int j=0;j<zoneHeight;j++)
			{
				if (zoneTileGrid[i][j]!=null)
				{
					if (zoneTileGrid[i][j].getWidgetObject()!=null)
					{
						if (WidgetPortal.class.isInstance(zoneTileGrid[i][j].getWidgetObject()))
						{
							WidgetPortal portal=(WidgetPortal)zoneTileGrid[i][j].getWidgetObject();

								portals.add(portal);
						}
						
					}
				}
			}
			
		}		
		return portals;		
	}
	
	public WidgetPortal getPortalWidget(int id)
	{
		for (int i=0;i<zoneWidth;i++)
		{
			for (int j=0;j<zoneHeight;j++)
			{
				if (zoneTileGrid[i][j]!=null)
				{
					if (zoneTileGrid[i][j].getWidgetObject()!=null)
					{
						if (WidgetPortal.class.isInstance(zoneTileGrid[i][j].getWidgetObject()))
						{
							WidgetPortal portal=(WidgetPortal)zoneTileGrid[i][j].getWidgetObject();
							if (portal.getID()==id)
							{
								return portal;
							}
						}
						
					}
				}
			}
			
		}		
		return null;		
	}
	
	public void LoadZone()
	{
		if (zoneTileGrid==null)
		{
			Element enode=zoneEntity.LoadZone(this);
			LoadZone(enode);	
			zoneEntity.postLoad(this);
		}

	}
	
	public Vec2f getPosition()
	{
		return zonePosition;
	}
	
	public Vec2f getLandingSite()
	{
		
		if (fixedLandingSite!=null)
		{
			return fixedLandingSite;
		}
		
		if (zoneTileGrid==null)
		{
			Element n=zoneEntity.LoadZone(this);
			   if (n.getAttribute("spriteset")!=null)
			    {
			    	tilesetName=n.getAttribute("spriteset");
			    }		
				NodeList children=n.getChildNodes();	

				if (fixedLandingSite==null)
				{
					for (int i=0;i<children.getLength();i++)
					{
						Node node=children.item(i);
						if (node.getNodeType()==Node.ELEMENT_NODE)
						{
							Element Enode=(Element)node;
							if (Enode.getTagName()=="landingsite")
							{
								fixedLandingSite=new Vec2f((float)Integer.parseInt(Enode.getAttribute("x")),(float)Integer.parseInt(Enode.getAttribute("y")));
								return fixedLandingSite;
							}
						}
					}		
				}
		

				
				zoneWidth=Integer.parseInt(n.getAttribute("width"));
				zoneHeight=Integer.parseInt(n.getAttribute("height"));
				int spanx=zoneWidth-32;
				int spany=zoneHeight-32;
				int x=(GameManager.m_random.nextInt()%(spanx/2))+(spanx/2);
				int y=(GameManager.m_random.nextInt()%(spany/2))+(spany/2);
				int xout=x+16;
				int yout=y+16;
				return new Vec2f(xout,yout);
		}
		else
		{
			return fixedLandingSite;
		}

	}
	
	public void cleanup()
	{
		for (int i=0;i<zoneActors.size();i++)
		{
			if (zoneActors.get(i).getPosition().x>=0 && zoneActors.get(i).getPosition().x<getWidth() &&
				zoneActors.get(i).getPosition().y>=0 && zoneActors.get(i).getPosition().y<getHeight())
			{
				if (zoneActors.get(i).getRPG().getStat(Actor_RPG.HEALTH)<=0)
				{
					if (NPC.class.isInstance(zoneActors.get(i)))
					{
						((NPC)zoneActors.get(i)).Remove();	
					}
					
				}
			}
		}
		for (int i=0;i<zoneTileGrid.length;i++)
		{
			for (int j=0;j<zoneTileGrid[0].length;j++)
			{
				if (zoneTileGrid[i][j]!=null)
				{
					zoneTileGrid[i][j].setActorInTile(null);
				}
			}
		}
	}
	
	public void LoadZone(Element n)
	{
		if (zoneTileGrid==null)
		{
		    if (n.getAttribute("spriteset")!=null)
		    {
		    	tilesetName=n.getAttribute("spriteset");
		    }		
			NodeList children=n.getChildNodes();		
			zoneWidth=Integer.parseInt(n.getAttribute("width"));
			zoneHeight=Integer.parseInt(n.getAttribute("height"));

			for (int i=0;i<children.getLength();i++)
			{
				Node node=children.item(i);
				if (node.getNodeType()==Node.ELEMENT_NODE)
				{
					Element Enode=(Element)node;
					if (Enode.getTagName()=="zonerules")
					{
						zoneRules=Enode.getAttribute("ruleset");
					}
					if (Enode.getTagName()=="description")
					{
						zoneDescription=Enode.getTextContent();
					}
					if (Enode.getTagName()=="tileset")
					{
						zoneTileLibrary=new TileDefLibrary(Enode);
					}				
					if (Enode.getTagName()=="description")
					{
						zoneDescription=Enode.getTextContent();
					}
					
					if (Enode.getTagName()=="landingsite")
					{
						fixedLandingSite=new Vec2f((float)Integer.parseInt(Enode.getAttribute("x")),(float)Integer.parseInt(Enode.getAttribute("y")));
					}
					
					//layout
					if (Enode.getTagName()=="mapgen")
					{
						ZoneBuildTools buildtools=new ZoneBuildTools(zoneName,this);
						if (zoneEntity.getLandings()!=null && isSurfaceZone )
						{
							buildtools.BuildShips(zoneEntity.getLandings(), zonePosition);					
						}
						zoneTileGrid=buildtools.BuildZone(Enode);

					}	
					if (Enode.getTagName()=="transition")
					{
						String str=Enode.getAttribute("direction");
						if (str.contains("north"))
						{
							adjacentZoneNames[0]=Enode.getTextContent();
						}
						if (str.contains("east"))
						{
							adjacentZoneNames[1]=Enode.getTextContent();	
						}
						if (str.contains("south"))
						{
							adjacentZoneNames[2]=Enode.getTextContent();
						}
						if (str.contains("west"))
						{
							adjacentZoneNames[3]=Enode.getTextContent();
						}
					}
				}
	
			}
			preload=null;
		}
	}


	public int getHeight()
	{
		if (zoneHeight==0)
		{
			Element n=zoneEntity.LoadZone(this);
			zoneHeight=Integer.parseInt(n.getAttribute("height"));
		}
		return zoneHeight;
	}
	public Tile [][] getTiles()
	{
		return zoneTileGrid;
	}
	
	public int getWidth()
	{
		if (zoneWidth==0)
		{
			Element n=zoneEntity.LoadZone(this);
			zoneWidth=Integer.parseInt(n.getAttribute("width"));
		}
		return zoneWidth;
	}
	
	public String getTileset()
	{
		return "tilesets/"+tilesetName;
	}

	@Override
	public boolean contains(int x, int y) 
	{
		if (x<0 || y<0)
		{
			return false;
		}
		if (x>=zoneWidth || y>=zoneHeight)
		{
			return false;
		}
		return true;
	}

	@Override
	public boolean isObstacle(int x, int y) 
	{
		if (x>=0 && x<zoneWidth)
		{
			if (y>=0 && y<zoneHeight)
			{
				if (zoneTileGrid[x][y]!=null)
				{
					if (zoneTileGrid[x][y].getWidgetObject()!=null)
					{
						if (zoneTileGrid[x][y].getWidgetObject().BlockVision()==true)
						{
							return true;
						}					
					}

					return zoneTileGrid[x][y].getDefinition().getBlockVision();				
				}
			}
		}
		return false;
	}

	public void ClearVisibleTiles()
	{
		for (int i=0;i<zoneWidth;i++)
		{
			for (int j=0;j<zoneHeight;j++)
			{
				if (zoneTileGrid[i][j]!=null)
				{
					zoneTileGrid[i][j].Hide();
				}

			}
		}
	}
	
	@Override
	public void visit(int x, int y) 
	{
		if (x>=0 && x<zoneWidth)
		{
			if (y>=0 && y<zoneHeight)
			{
				if (zoneTileGrid[x][y]!=null)
				{
					zoneTileGrid[x][y].Explore();
					zoneTileGrid[x][y].Reveal();			
				}

			}
		}
	}

	@Override
	public Actor getActor(int x, int y) {

		if (zoneTileGrid[x][y]!=null && zoneTileGrid[x][y].getActorInTile()!=null)
		{
			return zoneTileGrid[x][y].getActorInTile();
		}
		/*
		for (int i=0;i<zoneActors.size();i++)
		{
			int x0=(int)zoneActors.get(i).getPosition().x;
			int y0=(int)zoneActors.get(i).getPosition().y;
			if (x==x0 && y==y0)
			{
				return zoneActors.get(i);
			}
		}
		*/
		return null;
	}

	@Override
	public Widget getWidget(int x, int y) {

		if (x>=0 && x<zoneWidth)
		{
			if (y>=0 && y<zoneHeight)
			{
				if (zoneTileGrid[x][y]!=null)
				{
					return zoneTileGrid[x][y].getWidgetObject();				
				}

			}
		}
		return null;
	}
	
	public String getName()
	{
		return zoneName;
	}
	
	public void AddPlayer(Player player)
	{
		boolean b=true;
		for (int i=0;i<zoneActors.size();i++)
		{
			if (zoneActors.get(i)==player)
			{
				b=false;
				break;
			}
		}
		if (b==true)
		{
			zoneActors.add(player);
		}
		player.setCollisioninterface(this);
	}

	public ArrayList <Actor> getActors()
	{
		return zoneActors;
	}
	
	@Override
	public boolean passable(int x, int y, boolean fly) {


		if (x<0 || x>=zoneWidth)
		{
			return false;
		}
		if (y<0 || y>=zoneHeight)
		{
			return false;
		}
		if (zoneTileGrid[x][y]==null)
		{
			return false;
		}
		if (getActor(x,y)!=null)
		{
			return false;
		}
		if (zoneTileGrid[x][y].getWidgetObject()!=null)
		{
			if (zoneTileGrid[x][y].getWidgetObject().Walkable()==false)
			{
				return false;
			}
		}
		switch (zoneTileGrid[x][y].getDefinition().getMovement())
		{
		case FLY:
			if (fly==true)
			{
				return true;
			}
			
		case BLOCK:
			return false;

		case WALK:	
			return true;

		}
		
		return false;
	}
	
	@Override
	public ILosBoard getBoard() {
		// TODO Auto-generated met5hod stub
		return this;
	}

	@Override
	public Path getPath(int x0, int y0, int x1, int y1, boolean fly, int length) {
		// TODO Auto-generated method stub
		if (m_astar==null)
		{
			m_astar=new Pathfinder(this);
		}
		
		return m_astar.findPath(new Vec2f(x0,y0), new Vec2f(x1,y1),length, fly);
		
	}
	
	Pathfinder m_astar;

	@Override
	public Tile getTile(int x, int y) {
		if (x>=0 && x<zoneWidth)
		{
			if (y>=0 && y<zoneHeight)
			{
				if (zoneTileGrid[x][y]!=null)
				{
					return zoneTileGrid[x][y];
				}
				return null;		
			}
		}
		return null;
	}
	
	public void RegenZone()
	{
		for (int i=zoneActors.size()-1;i>=0;i--)
		{
			if (zoneActors.get(i)!=null)
			{
				if (zoneActors.get(i).Respawn(GameManager.getClock()))
				{
					zoneActors.remove(i);
				}
				if (getTile((int)zoneActors.get(i).getPosition().x,(int)zoneActors.get(i).getPosition().y)!=null)
				{
					zoneTileGrid[(int) zoneActors.get(i).getPosition().x][(int) zoneActors.get(i).getPosition().y].setActorInTile(zoneActors.get(i));	
				}			
			}

			
		}
		
		for (int i=0;i<zoneWidth;i++)
		{
			for (int j=0;j<zoneHeight;j++)
			{
				if (zoneTileGrid[i][j]!=null)
				{
					if (zoneTileGrid[i][j].getWidgetObject()!=null)
					{
						zoneTileGrid[i][j].getWidgetObject().Regen(GameManager.getClock(),this);
					}
				}
			}
		}
	}
	
	public void Save(DataOutputStream dstream) throws IOException
	{
		ParserHelper.SaveString(dstream, zoneName);
		
	
		
		//save whether its already loaded
		if (zoneTileGrid==null)
		{
			dstream.writeBoolean(false);
			if (preload!=null)
			{
				dstream.writeBoolean(true);
				preload.save(dstream);
			}
			else
			{
				dstream.writeBoolean(false);
			}
		}
		else
		{
			dstream.writeBoolean(true);
			dstream.writeBoolean(isVisited);
			//save tilelibrary
			zoneTileLibrary.Save(dstream);
			//save tilesetname
			ParserHelper.SaveString(dstream, tilesetName);
			//save landinglocation
			if (fixedLandingSite!=null)
			{
				dstream.writeBoolean(true);
				dstream.writeFloat(fixedLandingSite.x);
				dstream.writeFloat(fixedLandingSite.y);
			}
			else
			{
				dstream.writeBoolean(false);
			}
			//save width
			dstream.write(zoneWidth);
			//save height
			dstream.write(zoneHeight);
			//save tilegrid
			for (int i=0;i<zoneWidth;i++)
			{
				for (int j=0;j<zoneHeight;j++)
				{
					if (zoneTileGrid[i][j]!=null)
					{
						dstream.writeBoolean(true);
						zoneTileGrid[i][j].Save(dstream);
					}
					else
					{
						dstream.writeBoolean(false);
					}
					
				}
			}
			//save actors
			dstream.write(zoneActors.size());
			if (zoneActors.size()>0)
			{
				for (int i=0;i<zoneActors.size();i++)
				{
					if (zoneActors.get(i).canSave())
					{
						dstream.writeBoolean(true);
						zoneActors.get(i).Save(dstream);
					}
					else
					{
						dstream.writeBoolean(false);
					}
				}
			}
			
			
			//adjacentzonenames
			for (int i=0;i<4;i++)
			{
				if (adjacentZoneNames[i]!=null)
				{
					dstream.writeBoolean(true);
					ParserHelper.SaveString(dstream, adjacentZoneNames[i]);
				}
				else
				{
					dstream.writeBoolean(false);
				}
			}
		}

		if (zoneRules!=null)
		{
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, zoneRules);
			dstream.writeInt(violationLevel);
		}
		else
		{
			dstream.writeBoolean(false);
		}
	}
	
	public void load(DataInputStream dstream) throws IOException
	{
		boolean load=dstream.readBoolean();
		
		
		
		if (load==true)
		{
			isVisited=dstream.readBoolean();
			//load tilelibrary
			zoneTileLibrary=new TileDefLibrary(dstream);
			//load tilesetname
			tilesetName=ParserHelper.LoadString(dstream);
			//load fixed landing site
			boolean c=dstream.readBoolean();
			if (c==true)
			{
				fixedLandingSite=new Vec2f(dstream.readFloat(),dstream.readFloat());
			}
			
			//load width
			zoneWidth=dstream.read();
			//load height
			zoneHeight=dstream.read();
			//load tilegrid
			zoneTileGrid=new Tile[zoneWidth][];
			for (int i=0;i<zoneWidth;i++)
			{
				zoneTileGrid[i]=new Tile[zoneHeight];
				for (int j=0;j<zoneHeight;j++)
				{
					boolean b=dstream.readBoolean();
					if (b==true)
					{
						zoneTileGrid[i][j]=new Tile(i, j, null, this, zoneTileLibrary);
						zoneTileGrid[i][j].Load(dstream, zoneTileLibrary);
					}
					
				}
			}
			//load actor count
			int count=dstream.read();
			//load actors
			zoneActors=new ArrayList<Actor>();
			for (int i=0;i<count;i++)
			{
				boolean b=dstream.readBoolean();
				if (b==true)
				{
					Actor a=ActorLoader.loadActor(dstream);

					if (a!=null)
					{
						a.setCollisioninterface(this);
						zoneActors.add(a);
					}			
				}
			}
			
			//adjacentzonenames
			for (int i=0;i<4;i++)
			{
				boolean l=dstream.readBoolean();
				if (l==true)
				{
					adjacentZoneNames[i]=ParserHelper.LoadString(dstream);
				}
				else
				{
					adjacentZoneNames[i]=null;
				}
			}
		}
		else
		{
			if (dstream.readBoolean())
			{
				preload=new ZonePreloadController();
				preload.load(dstream);
			}
		}

		if (dstream.readBoolean())
		{
			zoneRules=ParserHelper.LoadString(dstream);
			violationLevel=dstream.readInt();
		}
	}

	public Entity_Int getZoneEntity() {
		return zoneEntity;
	}

	public boolean isSurfaceZone() {

		return isSurfaceZone;
	}
	
	public Vec2f getWidgetPosition(Widget widget)
	{
		for (int i=0;i<zoneWidth;i++)
		{
			for (int j=0;j<zoneHeight;j++)
			{
				if (zoneTileGrid[i][j]!=null)
				{
					if (zoneTileGrid[i][j].getWidgetObject()==widget)
					{
						return new Vec2f(i,j);
					}
				}
			}
		}
		
		
		return null;
	}
}
