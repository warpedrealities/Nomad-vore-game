package widgets.traps;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.player.Player;
import actorRPG.Actor_RPG;
import nomad.universe.Universe;
import view.ViewScene;
import widgets.Widget;

public class Widget_Trap extends Widget {

	boolean discovered;
	boolean checkDone;
	int DC;
	int discoverSprite;
	Trap_Effect effect;

	public Widget_Trap(Element node) {
		widgetSpriteNumber = Integer.parseInt(node.getAttribute("sprite"));
		isVisionBlocking = false;
		isWalkable = true;
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;
				// run each step successively
				if (Enode.getTagName() == "description") {
					widgetDescription = Enode.getTextContent();
				}
				if (Enode.getTagName().equals("discoverSprite")) {
					discoverSprite = Integer.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName().equals("DC")) {
					DC = Integer.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName() == "effect") {
					String type = Enode.getAttribute("type");
					if (type.equals("COMBAT")) {
						effect = new Trap_Combat(Enode);
					}
					if (type.equals("MONSTERCLOSET")) {
						effect = new Trap_Monstercloset(Enode);
					}
				}
			}
		}

	}

	@Override
	public String getDescription() {
		if (discovered) {
			return widgetDescription;
		} else {
			return null;
		}

	}

	@Override
	public boolean Visit() {
		if (checkDone == false) {
			int r = Universe.m_random.nextInt(20)
					+ Universe.getInstance().getPlayer().getRPG().getAttribute(Actor_RPG.PERCEPTION);
			if (r > DC) {
				ViewScene.m_interface.DrawText("you have spotted a trap");
				widgetSpriteNumber = discoverSprite;
			}
			checkDone = true;
		}
		return false;
	}

	@Override
	public boolean Interact(Player player) {
		if (discovered) {
			int r = Universe.m_random.nextInt(20)
					+ Universe.getInstance().getPlayer().getRPG().getAttribute(Actor_RPG.TECH);
			if (r > DC) {
				ViewScene.m_interface.DrawText("you have disarmed a trap");
				ViewScene.m_interface.RemoveWidget(this);
			} else {
				ViewScene.m_interface.DrawText("you have set off a trap while trying to disarm it");
				effect.trigger(Universe.getInstance().getPlayer());
				ViewScene.m_interface.RemoveWidget(this);
			}

			return true;
		}
		return false;
	}

	@Override
	public boolean Step() {
		effect.trigger(Universe.getInstance().getPlayer());

		ViewScene.m_interface.RemoveWidget(this);
		return false;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.write(19);
		commonSave(dstream);
		dstream.writeInt(DC);
		dstream.writeInt(discoverSprite);
		dstream.writeBoolean(checkDone);
		dstream.writeBoolean(discovered);
		effect.save(dstream);
	}

	public Widget_Trap(DataInputStream dstream) throws IOException {
		commonLoad(dstream);
		DC = dstream.readInt();
		discoverSprite = dstream.readInt();
		checkDone = dstream.readBoolean();
		discovered = dstream.readBoolean();
		effect = Trap_Effect_Loader.loadTrap(dstream);
	}

}
