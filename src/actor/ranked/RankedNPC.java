package actor.ranked;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.ThreatAssessment;
import actor.npc.Crew;
import actor.npc.NPC;
import actor.npc.RespawnControl;
import actor.npc.ScriptPackage;
import actorRPG.RPGActionHandler;
import actorRPG.npc.NPC_RPG;
import artificial_intelligence.BrainBank;
import artificial_intelligence.Code_AI;
import artificial_intelligence.Script_AI;
import faction.FactionLibrary;
import nomad.FlagField;
import nomad.universe.Universe;
import shared.ParserHelper;
import shared.Vec2f;

public class RankedNPC extends NPC {

	
	
	
	public RankedNPC(Element node, Vec2f p, String file) {
		threatAssessment=new ThreatAssessment();
		uid = Universe.getInstance().getUIDGenerator().getnpcUID();
		actorPosition = p;
		actorVisibility = false;
		attackIndex = 0;
		String spritename = null;
		conversations = new String[6];
		moveCost = 2;
		// sprite

		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;
				// run each step successively

				if (Enode.getTagName() == "name") {
					actorName = Enode.getTextContent();
				}
				if (Enode.getTagName() == "description") {
					actorDescription = Enode.getTextContent();
				}
				if (Enode.getTagName() == "rpg") {
					actorRPG = new Ranked_RPG(Enode,file, this);
				}

				if (Enode.getTagName() == "controller") {
					controllerScript = BrainBank.getInstance().getAI(Enode.getAttribute("AI"));
				}

				if (Enode.getTagName() == "flying") {
					isFlying = true;
				}

				if (Enode.getTagName() == "defeated") {
					conversations[CONVERSATIONDEFEAT] = Enode.getAttribute("conversation");
				}
				if (Enode.getTagName() == "seduced") {
					conversations[CONVERSATIONSEDUCED] = Enode.getAttribute("conversation");
				}
				if (Enode.getTagName() == "victory") {
					conversations[CONVERSATIONVICTORY] = Enode.getAttribute("conversation");
				}
				if (Enode.getTagName() == "seducer") {
					conversations[CONVERSATIONSEDUCER] = Enode.getAttribute("conversation");
				}
				if (Enode.getTagName() == "talk") {
					conversations[CONVERSATIONTALK] = Enode.getAttribute("conversation");
				}
				if (Enode.getTagName() == "captive") {
					conversations[CONVERSATIONCAPTIVE] = Enode.getAttribute("conversation");
				}
				if (Enode.getTagName() == "respawn") {
					respawnController = new RespawnControl(Enode, actorPosition);
				}
				if (Enode.getTagName().equals("sprite")) {
					spritename = Enode.getAttribute("value");
				}
				if (Enode.getTagName().equals("faction")) {
					actorFaction = FactionLibrary.getInstance().getFaction(Enode.getAttribute("value"));
				}
				if (Enode.getTagName().equals("peacebond")) {
					peace = true;
				}
			}
		}

		if (controllerScript == null) {
			controllerScript = new Code_AI();
		}
		controllermemory = new int[8];

		for (int i = 0; i < 8; i++) {
			controllermemory[i] = 0;
		}

		RPGHandler = new RPGActionHandler(actorRPG, this);
		((NPC_RPG) actorRPG).getstatBlock().setSpriteName(spritename);
	}

	@Override
	public void Save(DataOutputStream dstream) throws IOException {
		dstream.write(3);
		saveRoutine(dstream);
	}
	
	protected void saveRoutine(DataOutputStream dstream) throws IOException
	{
		// save position
		actorPosition.Save(dstream);
		// save visibility
		dstream.writeBoolean(actorVisibility);

		// save flying
		dstream.writeBoolean(isFlying);
		// save uid
		dstream.writeInt(uid);
		// save attack index
		dstream.writeInt(attackIndex);
		// save conversations
		for (int i = 0; i < conversations.length; i++) {
			if (conversations[i] != null) {
				dstream.writeBoolean(true);
				ParserHelper.SaveString(dstream, conversations[i]);
			} else {
				dstream.writeBoolean(false);
			}
		}
		// save clock
		dstream.writeInt(clock);

		// save respawn controller
		if (respawnController != null) {
			dstream.writeBoolean(true);
			respawnController.save(dstream);
		} else {
			dstream.writeBoolean(false);
		}
		// save controllername
		if (controllerScript.getClass().getName().contains("Script_AI")) {
			Script_AI script = (Script_AI) controllerScript;
			ParserHelper.SaveString(dstream, script.getName());
		} else {
			ParserHelper.SaveString(dstream, "code");
		}
		// save rpg
		actorRPG.save(dstream);
		// save name
		ParserHelper.SaveString(dstream, actorName);
		// save description
		ParserHelper.SaveString(dstream, actorDescription);

		if (npcFlags != null) {
			dstream.writeBoolean(true);
			npcFlags.save(dstream);
		} else {
			dstream.writeBoolean(false);
		}

		ParserHelper.SaveString(dstream, actorFaction.getFilename());

		dstream.writeBoolean(isCompanion);
		dstream.writeBoolean(peace);

		if (scripts != null) {
			dstream.writeBoolean(true);
			scripts.save(dstream);
		} else {
			dstream.writeBoolean(false);
		}
	}
	@Override
	public void Load(DataInputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		// load position
		actorPosition = new Vec2f(dstream);
		// load visibility
		actorVisibility = dstream.readBoolean();

		// load flying
		isFlying = dstream.readBoolean();
		// load uid
		uid = dstream.readInt();
		// load attack index
		attackIndex = dstream.readInt();
		// load conversations
		conversations = new String[6];
		for (int i = 0; i < 6; i++) {
			boolean b = dstream.readBoolean();
			if (b == true) {
				conversations[i] = ParserHelper.LoadString(dstream);
			}

		}
		// load clock
		clock = dstream.readInt();

		// load respawn controller
		boolean b = dstream.readBoolean();
		if (b == true) {
			respawnController = new RespawnControl(dstream);
		}
		// load controller
		String s = ParserHelper.LoadString(dstream);
		controllerScript = BrainBank.getInstance().getAI(s);
		// load rpg
		actorRPG = new Ranked_RPG(dstream, this);
		// load name
		actorName = ParserHelper.LoadString(dstream);
		// load description
		actorDescription = ParserHelper.LoadString(dstream);

		if (dstream.readBoolean() == true) {
			npcFlags = new FlagField();
			npcFlags.load(dstream);
		}

		RPGHandler = new RPGActionHandler(actorRPG, this);

		actorFaction = FactionLibrary.getInstance().getFaction(ParserHelper.LoadString(dstream));

		isCompanion = dstream.readBoolean();
		peace = dstream.readBoolean();

		if (dstream.readBoolean()) {
			scripts = new ScriptPackage(dstream);
		}
	}
	
	public Crew getCrewSkill() {
		return ((Ranked_RPG)actorRPG).getCrew();
	}
}
