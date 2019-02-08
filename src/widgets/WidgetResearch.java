package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.player.Player;
import actorRPG.Actor_RPG;
import nomad.universe.Universe;
import shared.ParserHelper;
import view.ViewScene;

public class WidgetResearch extends Widget {

	private String data;
	private String research;
	private String group;
	private int difficultyClass;

	public WidgetResearch(Element node) {
		isWalkable = false;
		isVisionBlocking = true;
		widgetSpriteNumber = Integer.parseInt(node.getAttribute("sprite"));
		NodeList children = node.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;
				// run each step successively
				if (Enode.getTagName() == "description") {
					widgetDescription = Enode.getTextContent();
				}
				if ("research".equals(Enode.getTagName())) {
					difficultyClass = Integer.parseInt(Enode.getAttribute("DC"));
					data = Enode.getAttribute("data");
					research = Enode.getAttribute("research");
					if (Enode.getAttribute("group").length() > 0) {
						group = Enode.getAttribute("group");
					}
				}
			}
		}
	}

	@Override
	public boolean Interact(Player player) {
		if (player.getEncyclopedia().hasEntry(research) || player.getEncyclopedia().hasData(data)
				|| player.getEncyclopedia().hasResearch(data)) {
			ViewScene.m_interface.DrawText("No more data can be acquired here at the present time");
			return false;
		} else {
			int r = Universe.m_random.nextInt(20) + player.getRPG().getAttribute(Actor_RPG.SCIENCE);
			player.getEncyclopedia().addResearch(data, difficultyClass, r, group);
			ViewScene.m_interface
			.DrawText("You study your findings and have acquired some research data for further analysis");
			return true;
		}
	}

	@Override
	public boolean safeOnly() {
		return true;
	}

	public WidgetResearch(DataInputStream dstream) throws IOException {
		commonLoad(dstream);
		data = ParserHelper.LoadString(dstream);
		research = ParserHelper.LoadString(dstream);
		difficultyClass = dstream.readInt();
		if (dstream.readBoolean()) {
			group = ParserHelper.LoadString(dstream);
		}

	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.write(26);
		commonSave(dstream);
		ParserHelper.SaveString(dstream, data);
		ParserHelper.SaveString(dstream, research);
		dstream.writeInt(difficultyClass);
		if (group != null) {
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, group);
		} else {
			dstream.writeBoolean(false);
		}
	}

	public void setData(String data, String research, int DC, String group) {
		this.data = data;
		this.research = research;
		this.difficultyClass = DC;
		this.group = group;
	}

	public String getData() {
		return data;
	}

	public String getResearch() {
		return research;
	}

	public int getDifficultyClass() {
		return difficultyClass;
	}

}
