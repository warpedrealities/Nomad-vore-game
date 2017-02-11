package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import shared.ParserHelper;
import shared.Vec2f;
import view.ViewScene;
import view.ModelController_Int;

public class WidgetPortal extends Widget{

	String targetZone;
	Vec2f targetPosInZone;
	int portalID;
	byte portalFacing;
	public WidgetPortal(int sprite, String description)
	{
		widgetSpriteNumber=sprite;
		isWalkable=true;
		isVisionBlocking=true;
		widgetDescription=description;
	}
	
	public WidgetPortal(Element element)
	{
		widgetSpriteNumber=Integer.parseInt(element.getAttribute("sprite"));
		isWalkable=true;
		isVisionBlocking=true;
		widgetDescription=element.getTextContent();
		portalFacing=(byte) Integer.parseInt(element.getAttribute("facing"));
	}
	
	public int getPortalID() {
		return portalID;
	}


	public void setFacing(int facing)
	{
		portalFacing=(byte)facing;
	}
	
	public int getPortalFacing()
	{
		return (int)portalFacing;
	}
	
	public WidgetPortal(int sprite, String description, int id)
	{
		widgetSpriteNumber=sprite;
		widgetDescription=description;
		portalID=id;
		isWalkable=true;
		isVisionBlocking=true;
	}
	
	public WidgetPortal(int sprite, String target, Vec2f targetxy,String description)
	{
		widgetSpriteNumber=sprite;
		targetZone=target;
		targetPosInZone=targetxy;
		isWalkable=true;
		isVisionBlocking=true;
		widgetDescription=description;
	}
	

	public String getTarget()
	{
		return targetZone;
	}
	
	public Vec2f getTargetXY()
	{
		return targetPosInZone;
	}
	
	public void setDestination(String target, Vec2f targetxy)
	{
		targetZone=target;
		targetPosInZone=targetxy;
		checkWalkable();
	}

	private void checkWalkable()
	{
		if (targetZone==null)
		{
			isWalkable=false;
		}
		else
		{
			isWalkable=true;
		}
	}
	
	public void setDestination(String target, int id)
	{
		targetZone=target;
		portalID=id;
		checkWalkable();
	}	
	
	public int getID()
	{
		return portalID;
	}
	
	@Override
	public boolean Step()
	{
		if (targetZone!=null && targetPosInZone!=null)
		{
			ViewScene.m_interface.Transition(targetZone,(int)targetPosInZone.x,(int)targetPosInZone.y);
		}
		if (targetZone!=null && targetPosInZone==null)
		{
			//perform id search
			targetPosInZone=ViewScene.m_interface.Transition(targetZone, portalID);
			
		}
		return false;
	}

	@Override
	public
	void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.write(1);
		commonSave(dstream);
		dstream.writeByte(portalFacing);
		dstream.writeInt(portalID);		
		if (targetZone!=null)
		{
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, targetZone);
		}
		else
		{
			dstream.writeBoolean(false);
		}
		if (targetPosInZone!=null)
		{
			dstream.writeBoolean(true);
			targetPosInZone.Save(dstream);
		}
		else
		{
			dstream.writeBoolean(false);
		}
	}
	public WidgetPortal(DataInputStream dstream) throws IOException
	{
		// TODO Auto-generated constructor stub
		commonLoad(dstream);
		portalFacing=dstream.readByte();
		portalID=dstream.readInt();
		boolean b=dstream.readBoolean();
		if (b==true)
		{
			targetZone=ParserHelper.LoadString(dstream);
		}

		b=dstream.readBoolean();
		if (b==true)
		{
			targetPosInZone=new Vec2f(dstream);
		}
		checkWalkable();

	}

}
