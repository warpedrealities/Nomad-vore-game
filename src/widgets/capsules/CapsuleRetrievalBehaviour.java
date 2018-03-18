package widgets.capsules;

import nomad.StarSystem;
import nomad.universe.Universe;
import shared.Geometry;
import shared.Vec2f;
import shared.Vec2i;
import spaceship.Spaceship;
import view.ViewScene;
import widgets.Widget;
import widgets.WidgetSlot;
import zone.TileDef.TileMovement;

public class CapsuleRetrievalBehaviour {

	private WidgetCapsule capsule;
	private WidgetCapsuleSystem system;
	private Spaceship ship;
	public CapsuleRetrievalBehaviour(WidgetCapsule widgetCapsule) {
		this.capsule=widgetCapsule;
		
	}

	public void retrieve() {
		if (canRetrieve())
		{
			Vec2i p = getPosition();
			if (p!=null)
			{
				system.setDeployed(false);
				removeCapsuleFromWorld();
				Universe.getInstance().setCurrentEntity(ship);
				ViewScene.m_interface.Transition(ship.getName(), p.x, p.y);
				ViewScene.m_interface.DrawText("You have used the capsule to return to your ship");

			}
		}
		else
		{
			ViewScene.m_interface.DrawText("vessel not in range");
		}
	}
	
	private void removeCapsuleFromWorld() {
		ViewScene.m_interface.RemoveWidget(capsule);
	}

	private Vec2i getPosition()
	{
		for (int i=0;i<ship.getZone(0).getWidth();i++)
		{
			for (int j=0;j<ship.getZone(0).getHeight();j++)
			{
				if (ship.getZone(0).getTile(i, j)!=null &&
						ship.getZone(0).getTile(i, j).getWidgetObject()!=null)
				{
					Widget w=ship.getZone(0).getTile(i, j).getWidgetObject();
					if (WidgetSlot.class.isInstance(w))
					{
						WidgetSlot ws=(WidgetSlot)w;
						if (ws.getWidget()!=null && WidgetCapsuleSystem.class.isInstance(ws.getWidget()))
						{
							WidgetCapsuleSystem wcs=(WidgetCapsuleSystem)ws.getWidget();
							if (wcs.isDeployed())
							{
								return calcPosition(i,j,wcs);
							}
						}
					}
					if (WidgetCapsuleSystem.class.isInstance(w))
					{
						WidgetCapsuleSystem wcs=(WidgetCapsuleSystem)w;
						if (wcs.isDeployed())
						{
							return calcPosition(i,j,wcs);
						}					
					}
				}
			}
		}
		return null;
	}
	
	private Vec2i calcPosition(int x, int y, WidgetCapsuleSystem system)
	{
		this.system=system;
		for (int i=0;i<8;i++)
		{
			Vec2i p=Geometry.getPos(i, x, y);
			if (ship.getZone(0).getTile(p.x, p.y)!=null && 
					ship.getZone(0).getTile(p.x, p.y).getDefinition().getMovement()==TileMovement.WALK &&
					ship.getZone(0).getTile(p.x, p.y).getWidgetObject()==null)
			{
				return p;
			}
		}
		return null;
	}
	
	private boolean canRetrieve()
	{
		Vec2f p=Universe.getInstance().getCurrentEntity().getPosition();
		StarSystem system=Universe.getInstance().getcurrentSystem();
		for (int i=0;i<system.getEntities().size();i++)
		{
			if (Spaceship.class.isInstance(system.getEntities().get(i)) && 
					system.getEntities().get(i).getPosition().getDistance(p)<1.5F &&
				system.getEntities().get(i).getName().equals(capsule.getSpaceship()))
			{
				this.ship=(Spaceship)system.getEntities().get(i);
				
				return true;
			}
		}
		return false;
	}

}
