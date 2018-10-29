package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.player.Player;
import actorRPG.RPG_Helper;
import nomad.universe.Universe;
import shared.ParserHelper;
import view.ViewScene;
import vmo.GameManager;
import zone.Zone;

public class WidgetHarvestable extends Widget {


	private class HarvestSkillCheck
	{
		public int attribute;
		public String item;
		public int min,max,dc;
		public String successText;

		public HarvestSkillCheck()
		{

		}

		public HarvestSkillCheck(Element enode)
		{
			attribute=RPG_Helper.AttributefromString(enode.getAttribute("attribute"));
			item=enode.getAttribute("item");
			min=Integer.parseInt(enode.getAttribute("min"));
			max=Integer.parseInt(enode.getAttribute("max"));
			dc=Integer.parseInt(enode.getAttribute("dc"));
			successText=enode.getTextContent();
		}

		public void save(DataOutputStream dstream) throws IOException
		{
			ParserHelper.SaveString(dstream, item);
			ParserHelper.SaveString(dstream, successText);
			dstream.writeInt(min);
			dstream.writeInt(max);
			dstream.writeInt(dc);
			dstream.writeInt(attribute);
		}

		public void load(DataInputStream dstream) throws IOException
		{
			item=ParserHelper.LoadString(dstream);
			successText=ParserHelper.LoadString(dstream);
			min=dstream.readInt();
			max=dstream.readInt();
			dc=dstream.readInt();
			attribute=dstream.readInt();
		}

	}

	String m_contains;
	int m_min, m_max;
	int m_recharge;
	boolean m_picked;
	long m_timepicked=0;
	String m_use;
	HarvestSkillCheck skillCheck;


	public WidgetHarvestable(Element node) {
		widgetSpriteNumber = Integer.parseInt(node.getAttribute("sprite"));
		isVisionBlocking = false;
		if (node.getAttribute("blocksVision").equals("true"))
		{
			isVisionBlocking = true;
		}

		isWalkable = false;
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;
				// run each step successively
				if (Enode.getTagName() == "description") {
					widgetDescription = Enode.getTextContent();
				}
				if (Enode.getTagName() == "contains") {
					m_min = Integer.parseInt(Enode.getAttribute("minimum"));
					m_max = Integer.parseInt(Enode.getAttribute("maximum"));
					m_contains = Enode.getTextContent();
				}
				if (Enode.getTagName() == "usedescription") {
					m_use = Enode.getTextContent();
				}
				if (Enode.getTagName() == "regeneration") {
					m_recharge = Integer.parseInt(Enode.getAttribute("regentime"));
				}
				if (Enode.getTagName().equals("skillCheck")) {
					skillCheck=new HarvestSkillCheck(Enode);
				}
			}
		}
	}

	public WidgetHarvestable(int sprite, String description, String grabdesc, String ItemName, int min, int max,
			int recharge) {

		widgetSpriteNumber = sprite;
		isVisionBlocking = false;
		isWalkable = false;
		widgetDescription = description;
		m_min = min;
		m_max = max;
		m_recharge = recharge;
		m_contains = ItemName;
		m_use = grabdesc;

	}

	@Override
	public String getDescription() {
		if (m_picked == true) {
			return widgetDescription + " already harvested";
		} else {
			return widgetDescription;
		}

	}

	@Override
	public boolean Interact(Player player) {
		if (m_picked == false) {
			int r = m_min;
			if (m_max - m_min > 0) {
				r += (GameManager.m_random.nextInt(m_max - m_min));
			}

			for (int i = 0; i < r; i++) {
				player.getInventory().AddItem(Universe.getInstance().getLibrary().getItem(m_contains));
			}
			ViewScene.m_interface.DrawText(m_use);
			m_picked = true;
			m_timepicked = GameManager.getClock();

			if (skillCheck!=null)
			{
				int roll=Universe.m_random.nextInt(20)+player.getRPG().getAttribute(skillCheck.attribute);
				if (roll>=skillCheck.dc)
				{
					r=skillCheck.min;
					if (skillCheck.max - skillCheck.min > 0) {
						r += (GameManager.m_random.nextInt(skillCheck.max - skillCheck.min));
					}

					for (int i = 0; i < r; i++) {
						player.getInventory().AddItem(Universe.getInstance().getLibrary().getItem(skillCheck.item));
					}
					ViewScene.m_interface.DrawText(skillCheck.successText);
				}

			}
			return true;
		}
		ViewScene.m_interface.DrawText("Nothing to harvest here");
		return false;
	}

	@Override
	public void Regen(long clock, Zone zone) {
		if (m_picked == true) {
			if (clock > m_recharge + m_timepicked) {
				m_picked = false;
			}
		}
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.write(3);
		commonSave(dstream);
		ParserHelper.SaveString(dstream, m_contains);
		dstream.writeInt(m_min);
		dstream.writeInt(m_max);
		dstream.writeInt(m_recharge);
		dstream.writeLong(m_timepicked);
		ParserHelper.SaveString(dstream, m_use);
		dstream.writeBoolean(m_picked);
		if (skillCheck!=null)
		{
			dstream.writeBoolean(true);
			skillCheck.save(dstream);
		}
		else
		{
			dstream.writeBoolean(false);
		}
	}

	@Override
	public boolean safeOnly() {
		return true;
	}

	public WidgetHarvestable(DataInputStream dstream) throws IOException {
		commonLoad(dstream);
		m_contains = ParserHelper.LoadString(dstream);
		m_min = dstream.readInt();
		m_max = dstream.readInt();
		m_recharge = dstream.readInt();
		m_timepicked = dstream.readLong();
		m_use = ParserHelper.LoadString(dstream);
		m_picked=dstream.readBoolean();
		if (dstream.readBoolean())
		{
			skillCheck=new HarvestSkillCheck();
			skillCheck.load(dstream);
		}
	}

}
