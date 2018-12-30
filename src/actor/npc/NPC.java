package actor.npc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.Actor;
import actor.Attackable;
import actor.ThreatAssessment;
import actor.npc.observerVore.VoreScript;
import actor.npc.observerVore.impl.VoreScript_Impl;
import actor.player.Player;
import actorRPG.Actor_RPG;
import actorRPG.RPGActionHandler;
import actorRPG.npc.NPCItemDrop;
import actorRPG.npc.NPC_RPG;
import actorRPG.npc.conditionalDescription.ConditionalDescription;
import actorRPG.player.Player_RPG;
import artificial_intelligence.BrainBank;
import artificial_intelligence.Code_AI;
import artificial_intelligence.Controllable;
import artificial_intelligence.Controller;
import artificial_intelligence.Script_AI;
import artificial_intelligence.SpecialCommandHandler;
import artificial_intelligence.detection.Sense;
import artificial_intelligence.pathfinding.Path;
import combat.CombatMove;
import combat.CombatMove.AttackPattern;
import combat.CombatMove.MoveResult;
import combat.effect.Effect;
import faction.FactionLibrary;
import nomad.FlagField;
import nomad.universe.Universe;
import shared.ParserHelper;
import shared.Vec2f;
import view.ViewScene;
import view.ZoneInteractionHandler;
import zone.Tile;
import zone.Zone_int;

public class NPC extends Actor implements Controllable {

	protected Controller controllerScript;
	VoreScript voreScript;

	protected int uid;
	protected int attackIndex;
	protected int controllermemory[];
	Path actorPath;

	protected int clock;
	protected RespawnControl respawnController;
	Sense senseInterface;

	protected String conversations[];

	protected boolean peace;
	protected boolean isCompanion;
	boolean isBusy;
	protected FlagField npcFlags;

	static public final int CONVERSATIONDEFEAT = 0;
	static public final int CONVERSATIONSEDUCED = 1;
	static public final int CONVERSATIONVICTORY = 2;
	static public final int CONVERSATIONSEDUCER = 3;
	static public final int CONVERSATIONTALK = 4;
	static public final int CONVERSATIONCAPTIVE = 5;

	protected ScriptPackage scripts;
	protected Crew crewSkill;

	public Crew getCrewSkill() {
		return crewSkill;
	}

	public boolean isCompanion() {
		return isCompanion;
	}

	public void setCompanion(boolean isCompanion) {
		this.isCompanion = isCompanion;
	}

	@Override
	public int getUID() {
		return uid;
	}

	public NPC(NPC npc, Vec2f p) // clone
	{
		threatAssessment=new ThreatAssessment();
		uid = Universe.getInstance().getUIDGenerator().getnpcUID();
		moveCost = npc.moveCost;
		actorPosition = p;
		actorVisibility = false;
		attackIndex = 0;
		conversations = new String[6];
		for (int i = 0; i < 6; i++) {
			conversations[i] = npc.conversations[i];
		}

		actorName = npc.actorName;
		actorDescription = npc.actorDescription;
		controllerScript = npc.controllerScript;
		actorRPG = new NPC_RPG((NPC_RPG) npc.actorRPG, this);

		isFlying = npc.isFlying;

		controllermemory = new int[8];

		for (int i = 0; i < 8; i++) {
			controllermemory[i] = 0;
		}
		RPGHandler = new RPGActionHandler(actorRPG, this);
		if (npc.respawnController != null) {
			respawnController = new RespawnControl(npc.respawnController, p);
		}
		actorFaction = npc.actorFaction;
		peace = npc.peace;
		crewSkill=npc.crewSkill;
	}

	public boolean isPeace() {
		return peace;
	}

	@Override
	public boolean getPeace() {
		return peace;
	}

	@Override
	public void setPeace(boolean peace) {
		this.peace = peace;
	}

	public NPC(Element node, Vec2f p, String filename) {
		threatAssessment=new ThreatAssessment();
		uid = Universe.getInstance().getUIDGenerator().getnpcUID();
		actorPosition = p;
		actorVisibility = false;
		attackIndex = 0;
		String spritename = null;
		float spriteSize = 1;
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
					actorRPG = new NPC_RPG(Enode, filename, this);
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
					if (Enode.getAttribute("size").length() > 0) {
						spriteSize = Float.parseFloat(Enode.getAttribute("size"));
					}
				}
				if (Enode.getTagName().equals("faction")) {
					actorFaction = FactionLibrary.getInstance().getFaction(Enode.getAttribute("value"));
				}
				if (Enode.getTagName().equals("peacebond")) {
					peace = true;
				}
				if (Enode.getTagName().equals("crew")) {
					crewSkill=new Crew(Enode);
				}
				if (Enode.getTagName() == "scripts") {
					String death=null;
					if (Enode.getAttribute("death").length()>0)
					{
						death=Enode.getAttribute("death");
					}
					scripts=new ScriptPackage(null,death);
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
		((NPC_RPG) actorRPG).getstatBlock().setSpriteSize(spriteSize);
	}

	public NPC() {
		threatAssessment=new ThreatAssessment();
		controllermemory = new int[8];

		for (int i = 0; i < 8; i++) {
			controllermemory[i] = 0;
		}
	}

	public void setSense(Sense sense) {
		senseInterface = sense;
	}

	public boolean hasObserverScript()
	{
		if (voreScript!=null)
		{
			return true;
		}
		return false;
	}



	@Override
	public void Update() {
		super.Update();
		threatAssessment.update();
		actorRPG.update();
		if (actorRPG.getBusy() == 0 && !isBusy()) {
			if (voreScript!=null)
			{
				voreScript.update(getVisible(),senseInterface.getHostile(this, 4,true)==null);
				if (!voreScript.isActive())
				{
					voreScript=null;
				}
				else if (!voreScript.blocksAI())
				{
					if (RPGHandler.getActive()) {
						controllerScript.RunAI(this, senseInterface);
					}
				}
				if (actorRPG.getBusy()==0)
				{
					actorRPG.setBusy(4);
				}
			}
			else
			{
				if (RPGHandler.getActive()) {
					controllerScript.RunAI(this, senseInterface);
				}
			}
		}

		if (clock > 0 && !isBusy) {

			clock--;
			if (clock == 0) {
				if (actorRPG.getStat(Actor_RPG.HEALTH) > 0 && actorRPG.getStat(Actor_RPG.RESOLVE) <= 0) {
					lustRemove();
				} else {
					Remove(true,false);
				}
			}
		}

	}


	@Override
	protected int getMoveCost() {
		return (int) actorRPG.getSubAbility(Actor_RPG.MOVECOST);
	}

	@Override
	public boolean move(int direction) {
		if (actorRPG.getBindState() > -1) {
			actorRPG.struggle();

			return true;
		}
		boolean threat=actorRPG.isThreatening(actorFaction);
		boolean b = false;
		Vec2f p = ZoneInteractionHandler.getPos(direction, getPosition());

		int x=(int) actorPosition.x;
		int y=(int) actorPosition.y;

		b=super.move(direction);
		if (threat && b)
		{
			collisionInterface.removeThreat(x,y,this);
			collisionInterface.addThreat((int)actorPosition.x,(int)actorPosition.y,this);

		}
		return b;
	}

	private void lustRemove()
	{
		// clock=0;
		// spriteInterface.setImage(0);
		actorVisibility = false;
		spriteInterface.setVisible(false);
		if (!((NPC_RPG) actorRPG).getItemDrop().isEmpty()) {
			List<NPCItemDrop> drops = ((NPC_RPG) actorRPG).getItemDrop();
			for (int i = 0; i < drops.size(); i++) {
				if (!drops.get(i).isDefeatOnly()) {
					drops.get(i).useDrop(actorPosition);
				}
			}

		}
		if (collisionInterface != null
				&& collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y) != null) {
			collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y).setActorInTile(null);
		}
		actorPosition.x = -(actorPosition.x + 100);
		actorPosition.y = -(actorPosition.y + 100);
		if (respawnController != null) {
			respawnController.setGone();
		}
		// this.setPeace(true);
		// this.actorRPG.Heal(1);

	}

	public void Remove(boolean defeat, boolean noDrops) {

		clock=0;
		actorVisibility = false;
		spriteInterface.setVisible(false);
		if (!noDrops && !((NPC_RPG) actorRPG).getItemDrop().isEmpty()) {
			List<NPCItemDrop> drops=((NPC_RPG) actorRPG).getItemDrop();
			for (int i=0;i<drops.size();i++)
			{
				if (!drops.get(i).isDefeatOnly() || defeat)
				{
					drops.get(i).useDrop(actorPosition);
				}
			}

		}
		if (collisionInterface != null
				&& collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y) != null) {
			collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y).setActorInTile(null);
		}
		actorPosition.x = -99;
		actorPosition.y = -99;
		actorRPG.setStat(Actor_RPG.HEALTH, 0);
		spriteInterface.reposition(actorPosition);
		if (respawnController != null) {
			respawnController.setGone();
		}
		if (scripts != null && scripts.getOnDeath() != null) {
			runOnDeath();
		}
	}

	private void runOnDeath() {
		Globals globals = JsePlatform.standardGlobals();

		try {
			LuaValue script = globals.load(new FileReader("assets/data/scripts/" + scripts.getOnDeath() + ".lua"),
					"main.lua");
			script.call();
			LuaValue factionlibrary = CoerceJavaToLua.coerce(FactionLibrary.getInstance());
			LuaValue player = CoerceJavaToLua.coerce(Universe.getInstance().getPlayer());
			LuaValue mainFunc = globals.get("main");
			LuaValue view=CoerceJavaToLua.coerce(ViewScene.m_interface);
			mainFunc.call(factionlibrary, player,view);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean Pathto(int x, int y, int steps) {
		// TODO Auto-generated method stub
		actorPath = collisionInterface.getPath((int) actorPosition.x, (int) actorPosition.y, x, y, isFlying, steps);
		if (actorPath != null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean Pathto(int x, int y) {
		// TODO Auto-generated method stub
		actorPath = collisionInterface.getPath((int) actorPosition.x, (int) actorPosition.y, x, y, isFlying, 4);
		if (actorPath != null) {
			return true;
		}
		return false;
	}

	public void setActorPath(Path actorPath) {
		this.actorPath = actorPath;
	}

	@Override
	public boolean FollowPath() {

		if (actorPath != null) {
			if (move(actorPath.getDirection()) == true) {
				actorPath = actorPath.getNext();
				return true;
			} else {
				actorPath = actorPath.getNext();
				return false;
			}

		}

		return false;
	}

	@Override
	public boolean HasPath() {
		// TODO Auto-generated method stub
		if (actorPath != null) {
			return true;
		}
		return false;
	}

	@Override
	public int getHealth() {

		return actorRPG.getStat(Actor_RPG.HEALTH);
	}

	@Override
	public int getValue(int index) {

		return controllermemory[index];
	}

	@Override
	public void setValue(int index, int value) {

		controllermemory[index] = value;
	}

	@Override
	public int getResolve() {

		return actorRPG.getStat(Actor_RPG.RESOLVE);
	}

	@Override
	public void Wait() {

		actorRPG.addBusy(2);
	}

	@Override
	public void Defeat(Actor victor, boolean resolve) {
		if (victor.getActorFaction().getFilename().equals("player") && isHostile(victor.getActorFaction().getFilename())) {
			Player_RPG rpg=(Player_RPG)Universe.getInstance().getPlayer().getRPG();
			(rpg).addEXP(((NPC_RPG) actorRPG).calcExpValue(rpg.getPlayerLevel()));
			actorRPG.getStatusEffectHandler().clearStatusEffects(this, actorRPG,true);
		}

		clock = 100;
		if (resolve == true) {
			spriteInterface.setImage(3);
		} else {
			spriteInterface.setImage(2);
		}
		collisionInterface.removeThreat((int)actorPosition.x, (int)actorPosition.y, this);
	}

	@Override
	public boolean Respawn(long time) {

		voreScript=null;

		actorRPG.getStatusEffectHandler().clearStatusEffects(this, actorRPG,true);
		if (respawnController != null) {

			if (respawnController.Canrespawn() == true) {
				// heal
				if (spriteInterface != null) {
					spriteInterface.setImage(0);
				}
				actorRPG.Heal(1);
				// reposition
				actorPosition = new Vec2f(respawnController.startPosition.x, respawnController.startPosition.y);

				// reset respawn
				respawnController.Reset();

			}
		} else {
			if (actorRPG.getStat(Actor_RPG.HEALTH) <= 0) {
				return true;
			}
			if (actorRPG.getStat(Actor_RPG.RESOLVE) <= 0) {
				if (actorPosition.x<0)
				{
					actorPosition.x = (actorPosition.x-100)*-1;
					actorPosition.y = (actorPosition.y-100)*-1;
					if (spriteInterface != null) {
						spriteInterface.setImage(0);
					}
				}

				actorRPG.setStat(Actor_RPG.RESOLVE, actorRPG.getStatMax(Actor_RPG.RESOLVE));
			}
		}
		checkSpawnable();
		return false;
	}

	public void Heal() {
		actorRPG.Heal(1);
		spriteInterface.setImage(0);
		clock=0;
	}

	public boolean isBusy() {
		return isBusy;
	}


	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}

	public void startVoreScript(String filename, Actor target)
	{
		voreScript=new VoreScript_Impl(filename,(NPC) target, this);
	}

	@Override
	public void Interact(Player player) {

		if (voreScript!=null || isBusy)
		{
			ViewScene.m_interface.DrawText(actorName+" is busy right now");
		}
		else
		{
			if (actorRPG.getStat(Actor_RPG.HEALTH) <= 0 && conversations[CONVERSATIONDEFEAT] != null) {
				ViewScene.m_interface.StartConversation(conversations[CONVERSATIONDEFEAT], this, false);
			}
			if (actorRPG.getStat(Actor_RPG.RESOLVE) <= 0 && conversations[CONVERSATIONSEDUCED] != null) {
				ViewScene.m_interface.StartConversation(conversations[CONVERSATIONSEDUCED], this, false);
			}
			if (conversations[CONVERSATIONTALK] != null && isHostile(player.getActorFaction().getFilename()) == false) {
				ViewScene.m_interface.StartConversation(conversations[CONVERSATIONTALK], this, false);
			}
		}
	}

	public String getConversation(int index) {
		return conversations[index];
	}

	@Override
	public MoveResult AttackPlayer(int attackindex) {
		// TODO Auto-generated method stub

		setAttack(attackindex);
		Vec2f p = null;// senseInterface.getPlayerPosition();

		return Attack((int) p.x, (int) p.y);

	}

	@Override
	public void Save(DataOutputStream dstream) throws IOException {
		dstream.write(1);
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
		if (crewSkill != null) {
			dstream.writeBoolean(true);
			crewSkill.save(dstream);
		} else {
			dstream.writeBoolean(false);
		}
	}

	@Override
	public void setCollisioninterface(Zone_int zinterface) {
		if (actorRPG.isThreatening(actorFaction))
		{
			zinterface.addThreat((int)actorPosition.x, (int)actorPosition.y, this);
		}
		super.setCollisioninterface(zinterface);
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
		actorRPG = new NPC_RPG(dstream, this);
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
		if (dstream.readBoolean()) {
			crewSkill = new Crew(dstream);
		}
	}

	@Override
	public boolean getAttackable() {
		if (isBusy())
		{
			return false;
		}
		return RPGHandler.getAttackable();
	}

	@Override
	public int getAttackIndex() {

		return attackIndex;
	}

	@Override
	public String getSpriteName() {
		// TODO Auto-generated method stub
		return ((NPC_RPG) actorRPG).getSpriteName();
	}

	public FlagField getFlags() {
		if (npcFlags == null) {
			npcFlags = new FlagField();
		}
		return npcFlags;

	}

	@Override
	public int applyEffect(Effect effect, Actor origin, boolean critical) {
		if (voreScript!=null && origin!=this)
		{
			voreScript.attacked();
		}
		return effect.applyEffect(origin, this, critical);
	}

	public boolean useSelfMove(int move) {

		useMove(move, this);
		return true;
	}


	@Override
	public MoveResult Attack(int x, int y) {

		if (actorRPG.getCombatMove(attackIndex).getAttackPattern() == AttackPattern.P_SELF) {
			return useMove(attackIndex, this);
		}
		for (int i = 0; i < Universe.getInstance().getCurrentZone().zoneActors.size(); i++) {
			if (Universe.getInstance().getCurrentZone().zoneActors.get(i) != this
					&& Universe.getInstance().getCurrentZone().zoneActors.get(i).getVisible() == true
					&& Universe.getInstance().getCurrentZone().zoneActors.get(i).getAttackable()) {
				int xt = (int) Universe.getInstance().getCurrentZone().zoneActors.get(i).getPosition().x;
				int yt = (int) Universe.getInstance().getCurrentZone().zoneActors.get(i).getPosition().y;
				if (xt == x && yt == y) {
					// conduct attack
					Attackable attackable = Universe.getInstance().getCurrentZone().zoneActors.get(i);
					// player.Attack(attackable,m_view);
					peace = false;
					return useMove(attackIndex, attackable);
				}
			}
		}

		return MoveResult.UNUSABLE;
	}

	private MoveResult useMove(int attackIndex2, Attackable attackable) {
		CombatMove move = actorRPG.getCombatMove(attackIndex2);
		MoveResult result = move.useMove(this, attackable);
		if (!result.equals(MoveResult.UNUSABLE)) {
			actorRPG.addBusy(move.getTimeCost());
			actorVisibility = true;
		}
		return result;
	}

	@Override
	public int getAttribute(int defAttribute) {
		// TODO Auto-generated method stub
		return actorRPG.getAttribute(defAttribute);
	}

	@Override
	public CombatMove getCombatMove() {
		return actorRPG.getCombatMove(attackIndex);
	}

	@Override
	public boolean setAttack(int attack) {
		attackIndex = attack;
		return true;
	}

	@Override
	public boolean specialCommand(String command) {

		return SpecialCommandHandler.getInstance().performSpecial(command, this, collisionInterface);
	}

	@Override
	public boolean canSave() {
		if (isCompanion == true) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isBlocking() {

		if (isCompanion == true) {
			return false;
		}
		return true;
	}

	@Override
	public int getFlag(String flagname) {

		if (npcFlags != null) {
			return npcFlags.readFlag(flagname);
		}
		return 0;
	}

	@Override
	public void startConversation() {
		ViewScene.m_interface.StartConversation(conversations[CONVERSATIONTALK], this, false);
	}

	@Override
	protected void checkSpawnable() {

		if (scripts != null && scripts.getIsSpawnable() != null) {
			Globals globals = JsePlatform.standardGlobals();
			boolean evaluatedScriptValue = false;
			try {
				LuaValue script = globals
						.load(new FileReader("assets/data/scripts/" + scripts.getIsSpawnable() + ".lua"), "main.lua");
				script.call();
				LuaValue factionlibrary = CoerceJavaToLua.coerce(FactionLibrary.getInstance());
				LuaValue player = CoerceJavaToLua.coerce(Universe.getInstance().getPlayer());
				LuaValue mainFunc = globals.get("main");
				LuaValue returnVal = mainFunc.call(factionlibrary, player);
				evaluatedScriptValue = (boolean) CoerceLuaToJava.coerce(returnVal, Boolean.class);
			} catch (Exception e) {
				System.out.println("script name"+ scripts.getIsSpawnable());
				e.printStackTrace();
			}
			if (evaluatedScriptValue == true) {
				// spawn in
				if (RPGHandler.getActive() && actorPosition.x < 0) {
					actorRPG.Heal(1);
					// reposition
					actorPosition = new Vec2f(respawnController.startPosition.x, respawnController.startPosition.y);
				}
			} else {
				// spawn out
				actorVisibility = false;
				if (collisionInterface != null
						&& collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y) != null) {
					collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y).setActorInTile(null);
				}

				actorPosition.x = -99;
				actorPosition.y = -99;
				if (spriteInterface != null) {
					spriteInterface.setVisible(false);
					spriteInterface.reposition(actorPosition);
				}
			}
		}
	}

	public void setScripts(ScriptPackage scripts) {
		this.scripts = scripts;
	}

	@Override
	protected boolean visible(Tile tile) {

		if (actorVisibility == false && tile.getVisible() == true) {
			if (actorRPG.getStealthState() > -1) {

				return actorRPG.stealthCheck(Universe.getInstance().getPlayer().getRPG().getAttribute(Actor_RPG.PERCEPTION),true);
			} else {
				return true;
			}
		}
		return tile.getVisible();
	}



	public RespawnControl getRespawnController() {
		return respawnController;
	}

	public void setRespawnController(RespawnControl respawnController) {
		this.respawnController = respawnController;
	}

	public void attackOfOpportunity(Actor target) {
		if (!RPGHandler.getActive()||isPeace())
		{
			return;
		}
		if (actorRPG.getBusy()<=getMoveCost())
		{
			int threat=((NPC_RPG)actorRPG).getThreatMove();
			if (threat>=0)
			{
				senseInterface.drawText("attack of opportunity!");
				useMove(threat,target);
			}
		}
	}

	@Override
	public String getDescription() {
		ConditionalDescription description=((NPC_RPG)actorRPG).getConditionalDescription();
		if (description != null && description.isActive(getFlags(), actorFaction)) {
			return description.getText();
		}

		return actorDescription;
	}

	@Override
	public float getSpriteSize() {
		return ((NPC_RPG) actorRPG).getSpriteSize();
	}

	public String getFlushScript() {
		// TODO Auto-generated method stub
		return ((NPC_RPG) actorRPG).getFlushScript();
	}

	private void runFlushScript() {
		Globals globals = JsePlatform.standardGlobals();

		try {
			LuaValue script = globals.load(
					new FileReader("assets/data/scripts/" + ((NPC_RPG) actorRPG).getFlushScript() + ".lua"),
					"main.lua");
			script.call();
			LuaValue factionlibrary = CoerceJavaToLua.coerce(FactionLibrary.getInstance());
			LuaValue player = CoerceJavaToLua.coerce(Universe.getInstance().getPlayer());
			LuaValue mainFunc = globals.get("main");
			LuaValue view = CoerceJavaToLua.coerce(ViewScene.m_interface);
			mainFunc.call(factionlibrary, player, view);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
