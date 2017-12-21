package spaceship.boarding;

import spaceship.Spaceship;
import view.ZoneInteractionHandler;
import vmo.GameManager;
import widgets.WidgetPortal;
import widgets.WidgetReformer;
import zone.TileDef.TileMovement;
import zone.Zone;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import actor.npc.NPC;
import actor.npc.Temp_NPC;
import nomad.Universe;
import shared.ParserHelper;
import shared.Vec2f;
import shared.Vec2i;

public class BoardingHelper {

	private Spaceship ship;
	List<Vec2i> entries;
	
	public BoardingHelper(Spaceship ship)
	{
		this.ship=ship;
		entries=new ArrayList<Vec2i>();
		Zone z=ship.getZone(0);
		for (int i=0;i<z.getWidth();i++)
		{
			for (int j=0;j<z.getHeight();j++)
			{
				if (z.getTile(i, j)!=null && 
						WidgetPortal.class.isInstance(z.getTile(i, j).getWidgetObject()))
				{
					entries.add(new Vec2i(i,j));
				}
				if (z.getTile(i, j)!=null && 
						WidgetReformer.class.isInstance(z.getTile(i, j).getWidgetObject()))
				{
					WidgetReformer r=(WidgetReformer)z.getTile(i, j).getWidgetObject();
					r.setSuppressed(true);
				}
			}
			
		}
	}
	
	private Vec2f getAdjacent(Vec2f p)
	{
		for (int i=0;i<8;i++)
		{
			Vec2f a=ZoneInteractionHandler.getPos(i, p);
			if (ship.getZone(0).getTile((int)a.x, (int)a.y)!=null &&
				ship.getZone(0).getTile((int)a.x,(int)a.y).getDefinition().getMovement()==TileMovement.WALK 
				&& ship.getZone(0).getTile((int)a.x,(int)a.y).getActorInTile()==null)
			{
				return a;
			}
		}
		return null;
	}
	
	private Vec2f getPosition()
	{
		//pick an entry point
		int r=0;
		if (entries.size()>1)
		{
			r=GameManager.m_random.nextInt(entries.size());
		}
		
		Vec2f p=new Vec2f(entries.get(r).x,entries.get(r).y);

		if (ship.getZone(0).getTile((int)p.x, (int)p.y).getActorInTile()==null)
		{
			return p;
		}
		else
		{
				//find adjacent location
			p=getAdjacent(p);
			if (p!=null)
			{
				return p;			
			}
		}
		return null;
	}
	
	public void addNPCs(String [] filenames)
	{
		Vec2f position=getPosition();
		for (int i=0;i<filenames.length;i++)
		{

			Document doc = ParserHelper.LoadXML("assets/data/npcs/" + filenames[i] + ".xml");
			Element n = (Element) doc.getFirstChild();
			Vec2f p = Universe.getInstance().getCurrentZone().getEmptyTileNearP(position);
			position=p;
			NPC npc =new NPC(n, p.replicate(), filenames[i]);		
			Universe.getInstance().getCurrentZone().getActors().add(npc);
			npc.setCollisioninterface(Universe.getInstance().getCurrentZone());
		}
	}

}
