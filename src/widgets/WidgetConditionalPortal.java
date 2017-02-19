package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import nomad.FlagField;
import nomad.Universe;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shared.ParserHelper;
import shared.Vec2f;
import view.ViewScene;

public class WidgetConditionalPortal extends WidgetPortal {

	int value;
	boolean greaterthan;
	String flag;
	String forbidText;

	
	public WidgetConditionalPortal(int sprite, String desc,int id)
	{
		super(sprite,desc,id);
	}
	
	public WidgetConditionalPortal(Element element)
	{
		super(element);
		/*
		NodeList children=element.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
			if (children.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element enode=(Element)children.item(i);
				if (enode.getTagName().equals("condition"))
				{
					flag=enode.getAttribute("flag");
					value=Integer.parseInt(enode.getAttribute("value"));
					if (enode.getAttribute("operater").equals("greaterhtan"))
					{
						greaterthan=true;
					}
				}
				if (enode.getTagName().equals("forbidtext"))
				{
					forbidText=enode.getTextContent();
				}
			}
		}
		
		*/
	}
	
	
	


	public void setValue(int value) {
		this.value = value;
	}

	public void setGreaterthan(boolean greaterthan) {
		this.greaterthan = greaterthan;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setForbidText(String forbidText) {
		this.forbidText = forbidText;
	}

	@Override
	public
	void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.write(15);
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
		
		ParserHelper.SaveString(dstream, flag);
		dstream.writeBoolean(greaterthan);
		dstream.writeInt(value);
		ParserHelper.SaveString(dstream, forbidText);
	
		
	}
	public WidgetConditionalPortal(DataInputStream dstream) throws IOException
	{
		super(dstream);
		
		flag=ParserHelper.LoadString(dstream);
		greaterthan=dstream.readBoolean();
		value=dstream.readInt();
		forbidText=ParserHelper.LoadString(dstream);
	}
	
	private boolean check()
	{
		FlagField globals=Universe.getInstance().getPlayer().getFlags();
		int flagvalue=globals.readFlag(flag);
		if (greaterthan)
		{
			if (flagvalue>=value)
			{
				return true;
			}
		}
		else
		{
			if (flagvalue<value)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean Step()
	{
		if (check())
		{
			return super.Step();
		}
		else
		{
			ViewScene.m_interface.DrawText(forbidText);
		}
		return false;
	}
}
