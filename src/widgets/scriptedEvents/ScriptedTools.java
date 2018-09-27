package widgets.scriptedEvents;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import actor.npc.NPC;
import actor.npc.ScriptPackage;
import shared.ParserHelper;
import shared.Vec2f;
import view.ViewScene;
import widgets.Widget;
import widgets.WidgetDoor;
import zone.Zone;

public class ScriptedTools {

	private Zone zone;

	public ScriptedTools(Zone zone) {
		this.zone = zone;
	}

	public void placeWidget(int x, int y, String filename, String addendum, int variable) {
		Document doc = ParserHelper.LoadXML("assets/data/widgets/" + filename + ".xml");
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		Widget widget = null;
		if (root.getTagName().contains("door")) {
			WidgetDoor door = new WidgetDoor(n);
			if (addendum != null) {
				door.setLockKey(addendum);
				door.setLockStrength(variable);
			}
			widget = door;
		}
		zone.getTile(x, y).setWidget(widget);
	}
	
	public NPC placeNPC(int x, int y, String filename,boolean temp)
	{
		return ViewScene.m_interface.createNPC(filename, new Vec2f(x,y), temp);
	}
	
	public ScriptPackage genNpcScripts(String spawn, String death)
	{
		return new ScriptPackage(spawn,death);
	}
	
	public void startConversation(String conversation)
	{
		ViewScene.m_interface.StartConversation(conversation, null);
	}
}
