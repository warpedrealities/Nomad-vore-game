package widgets;

import interactionscreens.SlotScreen;
import item.Item;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import actor.player.Player;
import shared.Vec2f;
import view.ViewScene;

public class WidgetSlot extends Widget {

	private WidgetBreakable widget;
	private int facing;
	private boolean hardpoint;
	
	public WidgetSlot(Element root) {

		isWalkable=true;
		isVisionBlocking=false;
		if (root.getAttribute("hardpoint").equals("true"))
		{
			hardpoint=true;
			
		}
		if (root.getAttribute("facing").length()>0)
		{
			facing=Integer.parseInt(root.getAttribute("facing"));
		}
			setDescSprite();
		
		
	
	}
	
	private void setDescSprite()
	{

			if (hardpoint==true)
			{
				widgetSpriteNumber=6;
				widgetDescription="a hardpoint for weapon systems or other devices that require access to the outside of the ship";
			}
			else
			{
				widgetSpriteNumber=5;
				widgetDescription="a system slot";
			}		
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.write(12);
		commonSave(dstream);
		dstream.writeBoolean(hardpoint);
		dstream.writeInt(facing);
		//save widget
		if (widget!=null)
		{
			dstream.writeBoolean(true);
			widget.save(dstream);
		}
		else
		{
			dstream.writeBoolean(false);
		}
	}
	
	public WidgetSlot(DataInputStream dstream) throws IOException {
		commonLoad(dstream);
		
		hardpoint=dstream.readBoolean();
		
		facing=dstream.readInt();
		
		//load widget
		boolean b=dstream.readBoolean();
		if (b==true)
		{
			widget=(WidgetBreakable) WidgetLoader.loadWidget(dstream);
		}
		
		if (hardpoint==true)
		{
			widgetSpriteNumber=6;
			widgetDescription="a hardpoint, anything here can extend outside the ship";
		}
		else
		{
			widgetSpriteNumber=5;
			widgetDescription="a system slot";
		}	
	}
	
	public WidgetBreakable getWidget()
	{
		return widget;
	}

	public void setWidget(WidgetBreakable widget) {
		this.widget = widget;
		//trigger global redraw to show changes
		if (ViewScene.m_interface!=null)
		{
			ViewScene.m_interface.redraw();		
		}
	}

	public String getDescription()
	{
		if (widget!=null)
		{
			return widget.getDescription();
		}
		return widgetDescription;
	}
	
	public boolean Walkable()
	{
		if (widget!=null)
		{
			return widget.isWalkable;
		}
		return isWalkable;
	}
	
	public boolean BlockVision()
	{
		if (widget!=null)
		{
			return widget.BlockVision();
		}
		return isVisionBlocking;
	}
	
	public int getSprite()
	{
		if (widget!=null)
		{
			return widget.getSprite();
		}
		return widgetSpriteNumber;
	}
	
	public boolean Interact(Player player)
	{
		if (widget!=null)
		{
			return widget.Interact(player);
		}
		else
		{
			ViewScene.m_interface.setScreen(new SlotScreen(this));			
			return true;
		}

	}
	
	@Override
	public boolean safeOnly()
	{
		return true;
	}
	
	public void handleAttack() {
		if (widget!=null)
		{
			if (widget.getHitpoints()<=0)
			{
				Item [] stack=widget.getContained();
				
				WidgetItemPile Pile=new WidgetItemPile(2,"a pile of items containing ",stack[0]);
				if (stack.length>1)
				{
					for (int j=1;j<stack.length;j++)
					{
						Pile.AddItem(stack[j]);
					}
				}
				Vec2f p=ViewScene.m_interface.getSceneController().getActiveZone().getWidgetPosition(this);
				ViewScene.m_interface.placeWidget(Pile, (int)p.x, (int)p.y, true);
				widget=null;
				ViewScene.m_interface.redraw();
			}			
		}
	}

	public int getFacing() {
		return facing;
	}

	public void setFacing(int facing) {
		this.facing = facing;
	}

	public boolean isHardpoint() {
		return hardpoint;
	}
	
}
