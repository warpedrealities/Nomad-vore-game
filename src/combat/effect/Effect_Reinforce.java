package combat.effect;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.Actor;
import actor.npc.NPC;
import nomad.universe.Universe;
import shared.Vec2f;
import view.ViewScene;
import view.ZoneInteractionHandler;
import vmo.GameManager;
import zone.Tile;
import zone.Zone;

public class Effect_Reinforce extends Effect {

	private String filename;
	private int min,max;	
	private char directions;
	private int distance;
	public Effect_Reinforce(Element e) {
		filename=e.getAttribute("npc");
		min=Integer.parseInt(e.getAttribute("min"));
		max=Integer.parseInt(e.getAttribute("max"));

		NodeList children=e.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
			if (children.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element enode=(Element)children.item(i);
				if (enode.getTagName().equals("direction"))
				{
					String s=enode.getTextContent();
					distance=Integer.parseInt(enode.getAttribute("distance"));
					if (s.contains("NORTH"))
					{
						directions=1;
					}
					if (s.contains("EAST"))
					{
						directions=2;
					}
					if (s.contains("SOUTH"))
					{
						directions=4;
					}
					if (s.contains("WEST"))
					{
						directions=8;
					}
				}
			}
		}
	}

	@Override
	public int applyEffect(Actor origin, Actor target, boolean critical) {
		int number=min;
		if (max>min)
		{
			number+=GameManager.m_random.nextInt(max-min);
		}
		
		for (int i=0;i<number;i++)
		{
			Vec2f p=getPosition(origin.getPosition());	
			NPC npc=ViewScene.m_interface.createNPC(filename, p, true);
			npc.setValue(0, (int)origin.getPosition().x);
			npc.setValue(1, (int)origin.getPosition().y);
		}
		
		return number;
	}
	
	private int getDirection()
	{
		int r=GameManager.m_random.nextInt(4);
		while (true)
		{
			switch (r)
			{
			case 0:
				if ((directions & 1) !=0)
				{
					return r;
				}
				break;
			case 1:
				if ((directions & 2) !=0)
				{
					return r;
				}
				break;
			case 2:
				if ((directions & 4) !=0)
				{
					return r;
				}	
				break;
			case 3:
				if ((directions & 8) !=0)
				{
					return r;
				}
				break;			
			}
			r=GameManager.m_random.nextInt(4);
		}

	}
	
	private Vec2f getPositionForDirection(Vec2f origin,int d)
	{
		
		Vec2f p=ZoneInteractionHandler.getPos(d*2, new Vec2f(0,0));
		Vec2f c=origin.replicate();
		Zone z=Universe.getInstance().getCurrentZone();
		for (int i=0;i<distance;i++)
		{
			c.add(p);
			Tile t=z.getTile((int)c.x, (int)c.y);
			if (t!=null && !t.getVisible() && t.getActorInTile()==null && t.getWidgetObject()==null)
			{
				return c;
			}
		}
		return null;
	}
	
	private Vec2f getPosition(Vec2f origin)
	{
		int d=getDirection();
		Vec2f r=getPositionForDirection(origin,d);
		int failsafe=0;
		while (r==null && failsafe<4)
		{
			d++;
			failsafe++;
			if (d>3){
				d=0;
			}
			r=getPositionForDirection(origin,d);
		}
		return r;
	}

	@Override
	public Effect clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyChange(Effect effect, int rank) {
		// TODO Auto-generated method stub
		
	}

}
