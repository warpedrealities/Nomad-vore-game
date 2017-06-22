package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.npc.NPC;
import actor.player.Player;
import interactionscreens.CaptureScreen;
import interactionscreens.SystemScreen;
import view.ViewScene;

public class WidgetCapture extends WidgetBreakable {

	int capacity;
	private NPC[] npcs;

	public WidgetCapture(Element node) {
		super(node);
		NodeList children = node.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;
				// run each step successively
				if (Enode.getTagName() == "capacity") {
					capacity = Integer.parseInt(Enode.getAttribute("value"));
					npcs = new NPC[capacity];
				}
			}
		}
	}

	public WidgetCapture(DataInputStream dstream) throws IOException {
		super(dstream);
		capacity = dstream.readInt();
		npcs = new NPC[capacity];
		for (int i = 0; i < capacity; i++) {
			if (dstream.readBoolean()) {
				int c = dstream.read();
				npcs[i] = new NPC();
				npcs[i].Load(dstream);
			}
		}
	}

	@Override
	public boolean Interact(Player player) {

		ViewScene.m_interface.setScreen(new CaptureScreen(this));

		return true;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.write(21);
		commonSave(dstream);
		saveBreakable(dstream);

		dstream.writeInt(capacity);
		for (int i = 0; i < capacity; i++) {
			if (npcs[i] != null) {
				dstream.writeBoolean(true);
				npcs[i].Save(dstream);
			} else {
				dstream.writeBoolean(false);
			}
		}

	}

	public int getCapacity() {
		return capacity;
	}

	public void setNPC(NPC npc, int index) {
		npcs[index] = npc;
	}

	public NPC getNPC(int index) {
		return npcs[index];
	}
}
