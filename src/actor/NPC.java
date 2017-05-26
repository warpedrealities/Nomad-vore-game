package actor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


import nomad.FlagField;
import nomad.Universe;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import combat.CombatMove;
import combat.effect.Effect;
import combat.statusEffects.StatusEffect;
import faction.FactionLibrary;
import faction.violation.FactionRule.ViolationType;

import actorRPG.Actor_RPG;
import actorRPG.NPC_RPG;
import actorRPG.Player_RPG;
import actorRPG.RPGActionHandler;
import artificial_intelligence.BrainBank;
import artificial_intelligence.Code_AI;
import artificial_intelligence.Controllable;
import artificial_intelligence.Controller;
import artificial_intelligence.Script_AI;
import artificial_intelligence.Sense;
import artificial_intelligence.SpecialCommandHandler;
import artificial_intelligence.pathfinding.Path;


import shared.ParserHelper;
import shared.Vec2f;
import view.ViewScene;
import view.ZoneInteractionHandler;
import vmo.GameManager;
import zone.Tile;

public class NPC extends Actor implements Controllable {

	Controller controllerScript;

	int uid;
	int attackIndex;
	int controllermemory[];
	Path actorPath;

	int clock;
	RespawnControl respawnController;
	Sense senseInterface;
	
	String conversations[];

	boolean peace;
	boolean isCompanion;
	FlagField npcFlags;
	
	static public final int CONVERSATIONDEFEAT=0;
	static public final int CONVERSATIONSEDUCED=1;
	static public final int CONVERSATIONVICTORY=2;
	static public final int CONVERSATIONSEDUCER=3;
	static public final int CONVERSATIONTALK=4;
	static public final int CONVERSATIONCAPTIVE=5;
	
	ScriptPackage scripts;
	
	public boolean isCompanion() {
		return isCompanion;
	}



	public void setCompanion(boolean isCompanion) {
		this.isCompanion = isCompanion;
	}

	@Override
	public int getUID()
	{
		return uid;
	}

	public NPC(NPC npc,Vec2f p) //clone
	{
		uid=Universe.getInstance().getUIDGenerator().getnpcUID();
		moveCost=npc.moveCost;
		actorPosition=p;
		actorVisibility=false;
		attackIndex=0;
		conversations=new String[6];
		for (int i=0;i<6;i++)
		{
			conversations[i]=npc.conversations[i];
		}

	
		actorName=npc.actorName;
		actorDescription=npc.actorDescription;
		controllerScript=npc.controllerScript;
		actorRPG=new NPC_RPG((NPC_RPG)npc.actorRPG,this);

		isFlying=npc.isFlying;
		
		controllermemory=new int[8];
		
		for (int i=0;i<8;i++)
		{
			controllermemory[i]=0;
		}		
		RPGHandler=new RPGActionHandler(actorRPG,this);
		if (npc.respawnController!=null)
		{
			respawnController=new RespawnControl(npc.respawnController,p);		
		}
		actorFaction=npc.actorFaction;
		peace=npc.peace;
	}


	public boolean isPeace() {
		return peace;
	}

	public boolean getPeace()
	{
		return peace;
	}

	public void setPeace(boolean peace) {
		this.peace = peace;
	}



	public NPC(Element node,Vec2f p,String filename)
	{
		uid=Universe.getInstance().getUIDGenerator().getnpcUID();
		actorPosition=p;
		actorVisibility=false;
		attackIndex=0;
		String spritename=null;
		conversations=new String[6];
		moveCost=2;
		//sprite
	
		NodeList children=node.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
			Node N=children.item(i);
			if (N.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)N;
				//run each step successively

				if (Enode.getTagName()=="name")
				{
					actorName=Enode.getTextContent();
				}
				if (Enode.getTagName()=="description")
				{
					actorDescription=Enode.getTextContent();
				}
				if (Enode.getTagName()=="rpg")
				{
					actorRPG=new NPC_RPG(Enode,filename,this);
				}
	
				if (Enode.getTagName()=="controller")
				{
					controllerScript=BrainBank.getInstance().getAI(Enode.getAttribute("AI"));
				}

				if (Enode.getTagName()=="flying")
				{
					isFlying=true;
				}

				if (Enode.getTagName()=="defeated")
				{
					conversations[CONVERSATIONDEFEAT]=Enode.getAttribute("conversation");
				}
				if (Enode.getTagName()=="seduced")
				{
					conversations[CONVERSATIONSEDUCED]=Enode.getAttribute("conversation");
				}
				if (Enode.getTagName()=="victory")
				{
					conversations[CONVERSATIONVICTORY]=Enode.getAttribute("conversation");
				}
				if (Enode.getTagName()=="seducer")
				{
					conversations[CONVERSATIONSEDUCER]=Enode.getAttribute("conversation");		
				}
				if (Enode.getTagName()=="talk")
				{
					conversations[CONVERSATIONTALK]=Enode.getAttribute("conversation");
				}
				if (Enode.getTagName()=="captive")
				{
					conversations[CONVERSATIONCAPTIVE]=Enode.getAttribute("conversation");
				}
				if (Enode.getTagName()=="respawn")
				{
					respawnController=new RespawnControl(Enode,actorPosition);
				}
				if (Enode.getTagName().equals("sprite"))
				{
					spritename=Enode.getAttribute("value");
				}
				if (Enode.getTagName().equals("faction"))
				{
					actorFaction=FactionLibrary.getInstance().getFaction(Enode.getAttribute("value"));
				}
				if (Enode.getTagName().equals("peacebond"))
				{
					peace=true;
				}
			}
		}
		
		if (controllerScript==null)
		{
			controllerScript=new Code_AI();
		}
		controllermemory=new int[8];
		
		for (int i=0;i<8;i++)
		{
			controllermemory[i]=0;
		}	
	
		RPGHandler=new RPGActionHandler(actorRPG,this);
		((NPC_RPG)actorRPG).getstatBlock().setSpriteName(spritename);
		
		
	}
	
	public NPC() {

		controllermemory=new int[8];
		
		for (int i=0;i<8;i++)
		{
			controllermemory[i]=0;
		}	
	}

	public void setSense(Sense sense)
	{
		senseInterface=sense;
	}

	@Override
	public void Update()
	{
		super.Update();
		actorRPG.update();
		if (actorRPG.getBusy()==0)
		{
			if (RPGHandler.getActive())
			{
				controllerScript.RunAI(this,senseInterface);		
	
	
			}
		}	
		
		if (clock>0)
		{
			
			clock--;
			if (clock==0)
			{
				if (actorRPG.getStat(Actor_RPG.HEALTH)>0)
				{
					actorRPG.setStat(Actor_RPG.RESOLVE, actorRPG.getStatMax(Actor_RPG.RESOLVE));
				}
				else
				{
					Remove();	
				}
		
			}
		}
	
	}

	public void Remove()
	{
		
		actorVisibility=false;
		spriteInterface.setVisible(false);
		if (((NPC_RPG)actorRPG).getItemDrop()!=null)
		{
			((NPC_RPG)actorRPG).getItemDrop().useDrop(actorPosition);
		}
		if (collisionInterface!=null && collisionInterface.getTile((int)actorPosition.x, (int)actorPosition.y)!=null)
		{
			collisionInterface.getTile((int)actorPosition.x, (int)actorPosition.y).setActorInTile(null);
		}
		actorPosition.x=-99;
		actorPosition.y=-99;
		actorRPG.setStat(Actor_RPG.HEALTH, 0);
		spriteInterface.reposition(actorPosition);	
		if (respawnController!=null)
		{
			respawnController.setGone();
		}
		if (scripts!=null && scripts.getOnDeath()!=null)
		{
			runOnDeath();
		}
	}
	
	private void runOnDeath()
	{
		Globals globals = JsePlatform.standardGlobals();

		try
		{
			LuaValue script = globals.load(new FileReader("assets/data/scripts/" + scripts.getOnDeath() + ".lua"), "main.lua");
			script.call();
			LuaValue factionlibrary = CoerceJavaToLua.coerce(FactionLibrary.getInstance());
			LuaValue player = CoerceJavaToLua.coerce(Universe.getInstance().getPlayer());
			LuaValue mainFunc = globals.get("main");
			mainFunc.call(factionlibrary, player);
	
		}
		catch (Exception e)
		{  
	        e.printStackTrace();  
		}
	}
	
	@Override
	public boolean Pathto(int x, int y,int steps) {
		// TODO Auto-generated method stub
		actorPath=collisionInterface.getPath((int)actorPosition.x, (int)actorPosition.y, x, y, isFlying,steps);
		if (actorPath!=null)
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean Pathto(int x, int y) {
		// TODO Auto-generated method stub
		actorPath=collisionInterface.getPath((int)actorPosition.x, (int)actorPosition.y, x, y, isFlying,4);
		if (actorPath!=null)
		{
			return true;
		}
		return false;
	}
	
	
	public void setActorPath(Path actorPath) {
		this.actorPath = actorPath;
	}

	@Override
	public boolean FollowPath() {

		if (actorPath!=null)
		{
			if (move(actorPath.getDirection())==true)
			{
				actorPath=actorPath.getNext();
				return true;
			}
			else
			{
				actorPath=actorPath.getNext();
				return false;
			}

		}

		return false;
	}

	@Override
	public boolean HasPath() {
		// TODO Auto-generated method stub
		if (actorPath!=null)
		{
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

		controllermemory[index]=value;
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
	public void Defeat(Actor victor,boolean resolve)
	{
		if (Player.class.isInstance(victor) && victor.isHostile(this.getActorFaction().getFilename()))
		{
			((Player_RPG)Universe.getInstance().getPlayer().getRPG()).addEXP(((NPC_RPG)actorRPG).getExpValue());		
		}
	

		clock=100;
		if (resolve==true)
		{
			spriteInterface.setImage(3);
		}
		else
		{
			spriteInterface.setImage(2);		
		}
	
	}

	@Override
	public boolean Respawn(long time)
	{
		actorRPG.getStatusEffectHandler().clearStatusEffects(this, actorRPG);
		if (respawnController!=null)
		{
			
			if (respawnController.Canrespawn()==true)
			{
				//heal
				if (spriteInterface!=null)
				{
					spriteInterface.setImage(0);		
				}
				actorRPG.Heal(1);
				//reposition
				actorPosition=new Vec2f(respawnController.startPosition.x,respawnController.startPosition.y);
				
				//reset respawn
				respawnController.Reset();
				

			}
			else
			{
				checkSpawnable();
			}
		}
		else
		{
			if (actorRPG.getStat(Actor_RPG.HEALTH)<=0)
			{
				return true;
			}
			if (actorRPG.getStat(Actor_RPG.RESOLVE)<=0)
			{
				actorRPG.setStat(Actor_RPG.RESOLVE, actorRPG.getStatMax(Actor_RPG.RESOLVE));
			}
		}
		return false;
	}
	
	public void Heal()
	{
		actorRPG.Heal(1);
		spriteInterface.setImage(0);
	}
	
	@Override
	public void Interact(Player player)
	{

		if (actorRPG.getStat(Actor_RPG.HEALTH)<=0 && conversations[CONVERSATIONDEFEAT]!=null)
		{
			ViewScene.m_interface.StartConversation(conversations[CONVERSATIONDEFEAT], this,false);
		}
		if (actorRPG.getStat(Actor_RPG.RESOLVE)<=0 && conversations[CONVERSATIONSEDUCED]!=null)
		{
			ViewScene.m_interface.StartConversation(conversations[CONVERSATIONSEDUCED], this,false);
		}
		if (conversations[CONVERSATIONTALK]!=null && isHostile(player.getActorFaction().getFilename())==false)
		{
			ViewScene.m_interface.StartConversation(conversations[CONVERSATIONTALK], this,false);
		}
	}
	
	public String getConversation(int index)
	{
		return conversations[index];
	}

	@Override
	public boolean AttackPlayer(int attackindex) {
		// TODO Auto-generated method stub
		
		setAttack(attackindex);
		Vec2f p=null;//senseInterface.getPlayerPosition();
		
		return Attack((int)p.x,(int)p.y);

	}

	@Override
	public void Save(DataOutputStream dstream) throws IOException
	{
		dstream.write(1);
		
		//save position
		actorPosition.Save(dstream);
		//save visibility
		dstream.writeBoolean(actorVisibility);

		//save flying
		dstream.writeBoolean(isFlying);
		//save uid
		dstream.writeInt(uid);
		//save attack index
		dstream.writeInt(attackIndex);
		//save conversations
		for (int i=0;i<conversations.length;i++)
		{
			if (conversations[i]!=null)
			{
				dstream.writeBoolean(true);
				ParserHelper.SaveString(dstream, conversations[i]);
			}
			else
			{
				dstream.writeBoolean(false);
			}
		}
		//save clock
		dstream.writeInt(clock);

		//save respawn controller
		if (respawnController!=null)
		{
			dstream.writeBoolean(true);
			respawnController.save(dstream);
		}
		else
		{
			dstream.writeBoolean(false);
		}
		//save controllername
		if (controllerScript.getClass().getName().contains("Script_AI"))
		{
			Script_AI script=(Script_AI)controllerScript;
			ParserHelper.SaveString(dstream, script.getName());
		}
		else
		{
			ParserHelper.SaveString(dstream, "code");
		}
		//save rpg
		actorRPG.save(dstream);
		//save name
		ParserHelper.SaveString(dstream, actorName);
		//save description
		ParserHelper.SaveString(dstream, actorDescription);

		if (npcFlags!=null)
		{
			dstream.writeBoolean(true);
			npcFlags.save(dstream);
		}
		else
		{
			dstream.writeBoolean(false);
		}
		
		ParserHelper.SaveString(dstream, actorFaction.getFilename());
		
		dstream.writeBoolean(isCompanion);
		dstream.writeBoolean(peace);
		
		if (scripts!=null)
		{
			dstream.writeBoolean(true);
			scripts.save(dstream);
		}
		else
		{
			dstream.writeBoolean(false);
		}
	}


	@Override
	public void Load(DataInputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		//load position
		actorPosition=new Vec2f(dstream);
		//load visibility
		actorVisibility=dstream.readBoolean();


		//load flying
		isFlying=dstream.readBoolean();
		//load uid
		uid=dstream.readInt();
		//load attack index
		attackIndex=dstream.readInt();
		//load conversations
		conversations=new String[6];
		for (int i=0;i<6;i++)
		{
			boolean b=dstream.readBoolean();
			if (b==true)
			{
				conversations[i]=ParserHelper.LoadString(dstream);		
			}

		}
		//load clock
		clock=dstream.readInt();

		//load respawn controller
		boolean b=dstream.readBoolean();
		if (b==true)
		{
			respawnController=new RespawnControl(dstream);
		}
		//load controller
		String s=ParserHelper.LoadString(dstream);
		controllerScript=BrainBank.getInstance().getAI(s);
		//load rpg
		actorRPG=new NPC_RPG(dstream,this);
		//load name
		actorName=ParserHelper.LoadString(dstream);
		//load description
		actorDescription=ParserHelper.LoadString(dstream);

		if (dstream.readBoolean()==true)
		{
			npcFlags=new FlagField();
			npcFlags.load(dstream);
		}
		
		RPGHandler=new RPGActionHandler(actorRPG,this);
		
		actorFaction=FactionLibrary.getInstance().getFaction(ParserHelper.LoadString(dstream));
		
		isCompanion=dstream.readBoolean();
		peace=dstream.readBoolean();
		
		if (dstream.readBoolean())
		{
			scripts=new ScriptPackage(dstream);
		}
	}


	@Override
	public boolean getAttackable() {
		return RPGHandler.getAttackable();
	}

	@Override
	public int getAttackIndex() {

		return attackIndex;
	}

	@Override
	public String getSpriteName() {
		// TODO Auto-generated method stub
		return ((NPC_RPG)actorRPG).getSpriteName();
	}

	public FlagField getFlags()
	{
		if (npcFlags==null)
		{
			npcFlags=new FlagField();
		}
		return npcFlags;
		
	}

	@Override
	public int applyEffect(Effect effect, Actor origin, boolean critical) {
		
		return effect.applyEffect(origin, this, critical);
	}

	public boolean useSelfMove(int move)
	{
		
		return useMove(move,this);
	}
	
	@Override
	public boolean Attack(int x, int y) {

	
		for (int i=0;i<Universe.getInstance().getCurrentZone().zoneActors.size();i++)
		{
			if (Universe.getInstance().getCurrentZone().zoneActors.get(i)!=
					this && Universe.getInstance().getCurrentZone().zoneActors.get(i).getVisible()==true && 
					Universe.getInstance().getCurrentZone().zoneActors.get(i).getAttackable())
			{
				int xt=(int)Universe.getInstance().getCurrentZone().zoneActors.get(i).getPosition().x;
				int yt=(int)Universe.getInstance().getCurrentZone().zoneActors.get(i).getPosition().y;
				if (xt==x && yt==y)
				{
					//conduct attack
					Attackable attackable=(Attackable)Universe.getInstance().getCurrentZone().zoneActors.get(i);
//					player.Attack(attackable,m_view);
					peace=false;
					return useMove(attackIndex,attackable);
				}
			}
		}	
		
		return false;
	}

	private boolean useMove(int attackIndex2, Attackable attackable) {
		CombatMove move=actorRPG.getCombatMove(attackIndex2);
		if (move.useMove(this,attackable))
		{
			actorRPG.addBusy(move.getTimeCost());
			actorVisibility=true;
			return true;
		}
		return false;
	}

	@Override
	public int getAttribute(int defAttribute) {
		// TODO Auto-generated method stub
		return actorRPG.getAttribute(defAttribute);
	}
	
	
	@Override
	public CombatMove getCombatMove()
	{
		return actorRPG.getCombatMove(attackIndex);
	}
	

	public boolean setAttack(int attack) {
			attackIndex=attack;
			return true;
	}

	@Override
	public boolean specialCommand(String command) {

		return SpecialCommandHandler.getInstance().performSpecial(command,this, collisionInterface);
	}

	@Override
	public boolean canSave() {
		if (isCompanion==true)
		{
			return false;
		}
		return true;
	}



	@Override
	public boolean isBlocking() {
		
		if (isCompanion==true)
		{
			return false;
		}
		return true;
	}



	@Override
	public int getFlag(String flagname) {
		
		if (npcFlags!=null)
		{
			return npcFlags.readFlag(flagname);
		}
		return 0;
	}



	@Override
	public void startConversation() {
		ViewScene.m_interface.StartConversation(conversations[CONVERSATIONTALK], this, false);
	}



	@Override
	void checkSpawnable() {
		
		if (scripts!=null && scripts.getIsSpawnable()!=null)
		{
			Globals globals = JsePlatform.standardGlobals();
			boolean evaluatedScriptValue = false;
			try
			{
				LuaValue script = globals.load(new FileReader("assets/data/scripts/" + scripts.getIsSpawnable() + ".lua"), "main.lua");
				script.call();
				LuaValue factionlibrary = CoerceJavaToLua.coerce(FactionLibrary.getInstance());
				LuaValue player = CoerceJavaToLua.coerce(Universe.getInstance().getPlayer());
				LuaValue mainFunc = globals.get("main");
				LuaValue returnVal = mainFunc.call(factionlibrary, player);
				evaluatedScriptValue = (boolean) CoerceLuaToJava.coerce(returnVal, Boolean.class);
			}
			catch (Exception e)
			{  
		        e.printStackTrace();  
			}
			if (evaluatedScriptValue==true)
			{
				//spawn in
				if (RPGHandler.getActive() && actorPosition.x<0)
				{
					actorRPG.Heal(1);
					//reposition
					actorPosition=new Vec2f(respawnController.startPosition.x,respawnController.startPosition.y);
				}
			}
			else
			{
				//spawn out
				actorVisibility=false;
				if (collisionInterface!=null && collisionInterface.getTile((int)actorPosition.x, (int)actorPosition.y)!=null)
				{
					collisionInterface.getTile((int)actorPosition.x, (int)actorPosition.y).setActorInTile(null);
				}
				
				actorPosition.x=-99;
				actorPosition.y=-99;
				if (spriteInterface!=null)
				{
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
	boolean visible(Tile tile) {

		if (actorVisibility==false  && tile.getVisible()==true)
		{
			if (actorRPG.getStealthState()>-1)
			{
				
				return actorRPG.stealthCheck(Universe.getInstance().getPlayer().getRPG());
			}
			else
			{
				return true;
			}
		}
		return tile.getVisible();
	}
	
	
}
