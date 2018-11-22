package actorRPG.npc;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actorRPG.RPG_Helper;
import actorRPG.npc.conditionalDescription.ConditionalDescription;
import combat.CombatMove;
import faction.Faction;
import shared.ParserHelper;

public class NPC_RPG_statblock {

	private String NPCName;
	private String spriteName;
	private int[] abilities;
	private int[] attributes;
	private int[] statMaximum;
	private int challengeLevel;
	// private Attack []attackPool;
	private int expValue;
	private List<NPCItemDrop> itemDrops;
	private Value_Calculator valueCalculator;
	private ArrayList<CombatMove> moveList;
	private String[] statusTags;
	private String flushScript;

	private ConditionalDescription conditionalDescription;

	private int threatMove=-1;
	private float spriteSize = 1;

	public String getNPCName() {
		return NPCName;
	}

	private void generate(Element rpgnode) {
		itemDrops=new ArrayList<NPCItemDrop>();
		abilities = new int[6];
		statMaximum = new int[2];
		attributes = new int[13];
		NodeList children = rpgnode.getChildNodes();

		moveList = new ArrayList<CombatMove>();
		// attackPool=new
		// Attack[Integer.parseInt(rpgnode.getAttribute("numattacks"))];

		for (int i = 0; i < children.getLength(); i++) {
			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;
				String name = Enode.getTagName();
				if (Enode.getTagName() == "challengeLevel") {
					challengeLevel=Integer.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName() == "ability") {
					abilities[RPG_Helper.abilityFromString(Enode.getAttribute("ability"))] = Integer
							.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName() == "attribute") {
					attributes[RPG_Helper.AttributefromString(Enode.getAttribute("attribute"))] = Integer
							.parseInt(Enode.getAttribute("value"));
				}

				if (Enode.getTagName() == "combatMove") {
					moveList.add(new CombatMove(Enode));
				}

				if (Enode.getTagName() == "stat") {
					statMaximum[RPG_Helper.statFromString(Enode.getAttribute("stat"))] = Integer
							.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName() == "experiencevalue") {
					expValue = Integer.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName() == "statusTags") {
					genStatusTags(Enode);
				}
				if (Enode.getTagName() == "itemDrop") {
					itemDrops.add(new NPCItemDrop(Enode));
				}
				if (Enode.getTagName().equals("valueCalculator"))
				{
					valueCalculator=new Value_Calculator(Enode);
				}
				if (Enode.getTagName().equals("conditionalDescription")) {
					conditionalDescription = new ConditionalDescription(Enode);
				}
				if (Enode.getTagName().equals("flushScript")) {
					flushScript = Enode.getAttribute("value");
				}
			}
		}
	}

	private void genStatusTags(Element enode) {
		// TODO Auto-generated method stub
		statusTags = new String[Integer.parseInt(enode.getAttribute("count"))];
		NodeList children = enode.getChildNodes();
		int index = 0;
		for (int i = 0; i < children.getLength(); i++) {
			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;
				statusTags[index] = Enode.getTextContent();
				index++;
			}
		}
	}

	public NPC_RPG_statblock(Element rpgnode, String NPCName) {
		this.NPCName = NPCName;
		generate(rpgnode);
	}

	public NPC_RPG_statblock(String name) {
		// TODO Auto-generated constructor stub
		NPCName = name;
		Document doc = ParserHelper.LoadXML("assets/data/npcs/" + name + ".xml");
		Element root = doc.getDocumentElement();
		Element node = (Element) doc.getFirstChild();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {

			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;
				// run each step successively
				if (Enode.getTagName() == "rpg") {
					generate(Enode);

				}
				if (Enode.getTagName().equals("sprite")) {
					spriteName = Enode.getAttribute("value");
					if (Enode.getAttribute("size").length() > 0) {
						spriteSize = Float.parseFloat(Enode.getAttribute("size"));
					}
				}
			}
		}

	}

	public int getAbility(int index) {
		return abilities[index];
	}

	public int getAttributes(int index) {
		return attributes[index];
	}

	public int getStatMaximum(int index) {
		return statMaximum[index];
	}

	public int getExpValue() {

		return expValue;
	}

	public String getSpriteName() {

		return spriteName;
	}

	public void setSpriteName(String spriteName) {
		this.spriteName = spriteName;
	}

	public float getSpriteSize() {
		return spriteSize;
	}

	public void setSpriteSize(float value) {
		this.spriteSize = value;
	}

	public CombatMove getCombatMove(int index) {

		return moveList.get(index);
	}

	public int getNumCombatMoves()
	{
		return moveList.size();
	}

	public boolean getTagged(String tag) {
		if (statusTags == null) {
			return false;
		}
		for (int i = 0; i < statusTags.length; i++) {
			if (tag.equals(statusTags[i])) {
				return true;
			}
		}
		return false;
	}

	public void resetThreat()
	{
		threatMove=-1;
	}

	public List <NPCItemDrop> getItemDrops() {
		return itemDrops;
	}

	public boolean isThreatening(Faction faction)
	{
		if (threatMove==-1)
		{
			if (faction.getRelationship("player")>50)
			{
				threatMove=-2;
				return false;
			}
			for (int i=0;i<moveList.size();i++)
			{
				if (moveList.get(i).isThreatening())
				{
					threatMove=i;
					return true;
				}
			}
			if (threatMove==-1)
			{
				threatMove=-2;
				return false;
			}
		}
		if (threatMove>=0)
		{
			return true;
		}
		return false;

	}

	public int getThreatMove()
	{
		return threatMove;
	}

	public Value_Calculator getValueCalculator() {
		return valueCalculator;
	}

	public int getChallengeLevel()
	{
		return challengeLevel;
	}

	public ConditionalDescription getConditionalDescription() {
		return conditionalDescription;
	}

	public String getFlushScript() {
		return flushScript;
	}
}
