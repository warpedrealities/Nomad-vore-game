package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import nomad.Universe;
import shared.ParserHelper;
import view.ViewScene;
import vmo.GameManager;
import zone.Zone;
import actor.Player;
import item.Item;

public class WidgetHarvestable extends Widget {

	String m_contains;
	int m_min,m_max;
	int m_recharge;
	boolean m_picked;
	long m_timepicked;
	String m_use;
	
	public WidgetHarvestable(Element node)
	{
		widgetSpriteNumber=Integer.parseInt(node.getAttribute("sprite"));
		isVisionBlocking=false;
		isWalkable=false;
		NodeList children=node.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
			Node N=children.item(i);
			if (N.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)N;
				//run each step successively
				if (Enode.getTagName()=="description")
				{
					widgetDescription=Enode.getTextContent();
				}
				if (Enode.getTagName()=="contains")
				{
					m_min=Integer.parseInt(Enode.getAttribute("minimum"));
					m_max=Integer.parseInt(Enode.getAttribute("maximum"));
					m_contains=Enode.getTextContent();
				}
				if (Enode.getTagName()=="usedescription")
				{
					m_use=Enode.getTextContent();
				}
				if (Enode.getTagName()=="regeneration")
				{
					m_recharge=Integer.parseInt(Enode.getAttribute("regentime"));
				}
			}
		}				
	}
	public WidgetHarvestable(int sprite, String description,String grabdesc, String ItemName,int min, int max, int recharge)
	{

		widgetSpriteNumber=sprite;
		isVisionBlocking=false;
		isWalkable=false;
		widgetDescription=description;
		m_min=min;
		m_max=max;
		m_recharge=recharge;
		m_contains=ItemName;
		m_use=grabdesc;
		
	}
	

	@Override
	public String getDescription()
	{
		if (m_picked==true)
		{
			return widgetDescription+" already harvested";
		}
		else
		{
			return widgetDescription;
		}
	
	}
	
	@Override
	public boolean Interact(Player player)
	{
		if (m_picked==false)
		{
			int r=(GameManager.m_random.nextInt(m_max-m_min))+m_min;
			
			for (int i=0;i<r;i++)
			{
				player.getInventory().AddItem(Universe.getInstance().getLibrary().getItem(m_contains));
			}
			ViewScene.m_interface.DrawText(m_use);
			m_picked=true;
			m_timepicked=GameManager.getClock();
			return true;
		}
		ViewScene.m_interface.DrawText("Nothing to harvest here");
		return false;
	}
	
	@Override
	public void Regen(long clock, Zone zone)
	{
		if (m_picked==true)
		{
			if (clock>m_recharge+m_timepicked)
			{
				m_picked=false;
			}
		}
	}
	@Override
	public
	void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.write(3);
		commonSave(dstream);
		ParserHelper.SaveString(dstream, m_contains);
		dstream.writeInt(m_min);
		dstream.writeInt(m_max);
		dstream.writeInt(m_recharge);
		dstream.writeLong(m_timepicked);
		ParserHelper.SaveString(dstream, m_use);
	}
	
	public WidgetHarvestable(DataInputStream dstream) throws IOException {
		// TODO Auto-generated constructor stub
		commonLoad(dstream);
		m_contains=ParserHelper.LoadString(dstream);
		m_min=dstream.readInt();
		m_max=dstream.readInt();
		m_recharge=dstream.readInt();
		m_timepicked=dstream.readLong();
		m_use=ParserHelper.LoadString(dstream);
	}
	
}
