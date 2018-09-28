package research;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.soap.Node;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import actor.player.Player;
import shared.ParserHelper;

public class Entry implements Comparable<Entry> {

	private String filename;
	private String entryName;
	private boolean unlocked;

	private List<Entry_Requirement> requirements;
	private List<Entry_Reward> rewards;

	public Entry(String filename) {
		requirements = new ArrayList<Entry_Requirement>();
		rewards = new ArrayList<Entry_Reward>();
		this.filename = filename;
		Document doc = ParserHelper.LoadXML("assets/data/encyclopedia/" + filename);
		Element first = (Element) doc.getFirstChild();
		entryName = first.getAttribute("name");
		NodeList children = first.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) children.item(i);

				if (e.getTagName().equals("requirement")) {
					requirements.add(new Entry_Requirement(e));
				}

				if (e.getTagName().equals("recipe_reward")) {
					rewards.add(new Reward_Recipe(e));
				}

			}
		}

	}

	public String getEntryName() {
		return entryName;
	}

	public String getFilename() {
		return filename;
	}

	public boolean isUnlocked() {
		return unlocked;
	}

	public String getText() {
		Document doc = ParserHelper.LoadXML("assets/data/encyclopedia/" + filename);
		Element first = (Element) doc.getFirstChild();
		entryName = first.getAttribute("name");
		NodeList children = first.getElementsByTagName("text");
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) children.item(i);
				return e.getTextContent();
			}
		}
		return null;
	}

	public void runRewards(Player player) {
		for (int i = 0; i < rewards.size(); i++) {
			rewards.get(i).runReward(player);
		}
	}

	public boolean checkUnlock(Map<String, Data> dataMap) {
		for (int i = 0; i < requirements.size(); i++) {
			if (requirements.get(i).met(dataMap) == false) {
				return false;

			}
		}
		unlocked = true;
		return true;
	}

	@Override
	public int compareTo(Entry o) {
		// TODO Auto-generated method stub
		return entryName.compareTo(o.getEntryName());

	}

}
