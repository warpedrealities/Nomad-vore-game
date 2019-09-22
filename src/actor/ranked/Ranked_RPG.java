package actor.ranked;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import actor.Actor;
import actor.Modifier;
import actor.npc.Crew;
import actor.npc.NPC;
import actorRPG.Actor_RPG;
import actorRPG.StatusEffectHandler;
import actorRPG.npc.NPCStatblockLibrary;
import actorRPG.npc.NPC_RPG_statblock;
import combat.CombatMove;
import combat.statusEffects.StatusEffect;
import shared.ParserHelper;

public class Ranked_RPG implements Actor_RPG {

	private List <NPC_Rank> ranks;
	private String rankUnlocks="";
	private List <CombatMove> moves;
	private NPC_RPG_statblock statBlock;
	private Crew crewSkill;
	private int abilities[];
	private float stats[];
	private int statMax[];
	private int attributes[];
	private Actor actor;
	private StatusEffectHandler statusEffectHandler;
	private int busy;



	private void loadRanks(String filename)
	{

	}

	private void initialize()
	{
		moves.clear();
		for (int i=0;i<13;i++)
		{
			attributes[i]=statBlock.getAttributes(i);
		}
		for (int i=0;i<2;i++)
		{
			statMax[i]=statBlock.getStatMaximum(i);
		}
		for (int i=0;i<statBlock.getNumCombatMoves();i++)
		{
			moves.add(statBlock.getCombatMove(i));
		}
	}

	private void calcRanks()
	{
		initialize();

	}

	public Ranked_RPG(Element enode, String filename,RankedNPC rankedNPC) {
		ranks=new ArrayList<NPC_Rank>();
		moves=new ArrayList<CombatMove>();
		statusEffectHandler = new StatusEffectHandler();
		this.actor = actor;
		statBlock = NPCStatblockLibrary.getInstance().getStatblock(enode, filename);
		loadRanks(statBlock.getNPCName());
		stats = new float[2];
		attributes=new int[13];
		abilities=new int[6];

		calcRanks();
		for (int i=0;i<2;i++)
		{
			stats[i]=statMax[i];
		}
	}

	@Override
	public CombatMove getCombatMove(int index) {
		return moves.get(index);
	}

	@Override
	public int getAttribute(int i) {
		return attributes[i];
	}

	@Override
	public int getStat(int i) {
		return (int)stats[i];
	}

	@Override
	public int getAbility(int i) {
		return abilities[i];
	}

	@Override
	public int getStatMax(int i) {
		return statMax[i];
	}

	@Override
	public void update() {
		if (busy > 0) {
			busy--;
		}
		statusEffectHandler.update(1, this);

	}

	@Override
	public void RemoveModifier(Modifier modifier) {
		// TODO Auto-generated method stub

	}

	@Override
	public void AddModifier(Modifier modifier) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getStarving() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getAbilityMod(int ability) {
		return abilities[ability]-5;
	}

	@Override
	public void setStat(int stat, int value) {
		// TODO Auto-generated method stub

	}


	@Override
	public void Heal(float amount) {
		float bonus = statBlock.getStatMaximum(HEALTH) * amount;
		IncreaseStat(HEALTH, (int) bonus, false);
		bonus = statBlock.getStatMaximum(RESOLVE) * amount;
		IncreaseStat(RESOLVE, (int) bonus, false);
	}

	@Override
	public void IncreaseStat(int stat, float value, boolean canReact) {
		stats[stat] += value;
		if (stats[stat] > statMax[stat]) {
			stats[stat] = statMax[stat];
		}
	}

	@Override
	public void ReduceStat(int stat, float value, boolean canReact) {
		stats[stat] -= value;
		((NPC) actor).setPeace(false);
	}


	@Override
	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.writeInt(busy);
		for (int i=0;i<2;i++)
		{
			dstream.writeFloat(stats[i]);
		}
		ParserHelper.SaveString(dstream, statBlock.getNPCName());
		ParserHelper.SaveString(dstream, rankUnlocks);
		statusEffectHandler.save(dstream);
	}

	public Ranked_RPG(DataInputStream dstream, Actor actor) throws IOException {
		this.actor = actor;
		ranks=new ArrayList<NPC_Rank>();
		moves=new ArrayList<CombatMove>();
		busy = dstream.readInt();
		stats = new float[2];
		attributes=new int[13];
		abilities=new int[6];
		for (int i = 0; i < 2; i++) {
			stats[i] = dstream.readFloat();
		}
		statBlock = NPCStatblockLibrary.getInstance().getStatblock(ParserHelper.LoadString(dstream));
		loadRanks(statBlock.getNPCName());

		statusEffectHandler = new StatusEffectHandler();
		statusEffectHandler.load(dstream);
		calcRanks();
		// apply statusmods again
		for (int i = 0; i < statusEffectHandler.getStatusEffects().size(); i++) {
			statusEffectHandler.getStatusEffects().get(i).apply(this);
		}
	}
	@Override
	public boolean getTagged(String tag) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyStatus(StatusEffect effect, boolean replace) {
		if (statusEffectHandler.applyStatus(effect, replace, this)) {
			return true;
		}
		return false;
	}

	@Override
	public void modAttribute(int attribute, int modifier) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBusy() {
		// TODO Auto-generated method stub
		return busy;
	}

	@Override
	public void setBusy(int busy) {

		this.busy = busy;
	}

	@Override
	public void addBusy(int value) {

		busy += value;
	}

	@Override
	public int getBindState() {
		return statusEffectHandler.getBindState();
	}

	@Override
	public void setBindState(int bindState) {
		statusEffectHandler.setBindState(bindState);
	}

	@Override
	public void struggle() {

		busy += 2;
		statusEffectHandler.struggle(this, actor);
	}

	@Override
	public boolean stealthCheck(int spot,boolean remove) {
		return statusEffectHandler.stealthCheck(spot, this,remove);
	}

	@Override
	public boolean hasStatus(int uid) {
		return statusEffectHandler.hasStatus(uid);
	}

	@Override
	public Actor getBindOrigin() {
		return statusEffectHandler.getBindOrigin();
	}

	@Override
	public void removeStatus(int uid) {
		statusEffectHandler.removeStatus(uid, this);
	}

	@Override
	public StatusEffectHandler getStatusEffectHandler() {
		return statusEffectHandler;
	}


	@Override
	public void recover(int i) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getSubAbility(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Crew getCrew() {
		// TODO Auto-generated method stub
		return crewSkill;
	}


	@Override
	public int getStealthState() {

		return statusEffectHandler.getStealthState();
	}

	@Override
	public void setStealthState(int stealthstate) {
		statusEffectHandler.setStealthState(stealthstate);

	}

	@Override
	public Actor getActor() {
		return actor;
	}

	@Override
	public void modSubAbility(int ability, float modifier) {
		// TODO Auto-generated method stub

	}

}
