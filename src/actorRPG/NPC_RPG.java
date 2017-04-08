package actorRPG;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Element;

import combat.CombatMove;
import combat.statusEffects.StatusEffect;
import combat.statusEffects.StatusLoader;
import combat.statusEffects.Status_Bind;
import nomad.Universe;
import shared.ParserHelper;
import view.ViewScene;

import actor.Actor;
import actor.Modifier;
import actor.NPC;

public class NPC_RPG implements Actor_RPG{

	Actor actor;
	NPC_RPG_statblock statBlock;
	int []statValues;
	private short []attributeMods;
//	ArrayList<StatusEffect> statusEffects;
	int busy;
//	int bindState;
	StatusEffectHandler statusEffectHandler;
	
	public NPC_RPG(NPC_RPG RPG, Actor actor)
	{
	//	bindState=-1;
		statusEffectHandler=new StatusEffectHandler();
		this.actor=actor;
		this.statBlock=RPG.statBlock;
		statValues=new int[2];
		statValues[0]=statBlock.getStatMaximum(Actor_RPG.HEALTH);
		statValues[1]=statBlock.getStatMaximum(Actor_RPG.RESOLVE);	
	//	statusEffects=new ArrayList<StatusEffect>();
		attributeMods=new short[13];
	}
	
	public NPC_RPG(Element rpgnode, String name, Actor actor)
	{
	//	bindState=-1;
		statusEffectHandler=new StatusEffectHandler();
		this.actor=actor;
		statBlock=NPCStatblockLibrary.getInstance().getStatblock(rpgnode, name);	
		//calculate health	
		statValues=new int[2];
		statValues[0]=statBlock.getStatMaximum(Actor_RPG.HEALTH);
		statValues[1]=statBlock.getStatMaximum(Actor_RPG.RESOLVE);
	//statusEffects=new ArrayList<StatusEffect>();
		attributeMods=new short[12];
	}
	
	@Override
	public int getAttribute(int i) {

		return statBlock.getAttributes(i)+attributeMods[i];
	}

	@Override
	public int getStat(int i) {

		return statValues[i];
	}

	@Override
	public int getAbility(int i) {
		return statBlock.getAbility(i);
	}

	@Override
	public int getStatMax(int i) {
		return statBlock.getStatMaximum(i);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		if (busy>0)
		{
			busy--;
		}
		statusEffectHandler.update(1,this);

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
		return (statBlock.getAbility(ability)-10)/2;
	}

	@Override
	public void setStat(int stat, int value) 
	{
	
		
	}

	@Override
	public void Heal(float amount) {
		float bonus=statBlock.getStatMaximum(HEALTH)*amount;
		IncreaseStat(HEALTH,(int)bonus);
		bonus=statBlock.getStatMaximum(RESOLVE)*amount;
		IncreaseStat(RESOLVE,(int)bonus);
	}

	@Override
	public void IncreaseStat(int stat, int value) {
		statValues[stat]+=value;
		if (statValues[stat]>statBlock.getStatMaximum(stat))
		{
			statValues[stat]=statBlock.getStatMaximum(stat);
		}
	}

	@Override
	public void ReduceStat(int stat, int value) {
		statValues[stat]-=value;
		((NPC)actor).setPeace(false);
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {

		dstream.writeInt(busy);
		//save values
		for (int i=0;i<statValues.length;i++)
		{
			dstream.writeInt(statValues[i]);
		}
		//save statblock name
		ParserHelper.SaveString(dstream,statBlock.getNPCName());
		
		//save status effects
		statusEffectHandler.save(dstream);
	}

	public NPC_RPG(DataInputStream dstream, Actor actor) throws IOException {
		
		this.actor=actor;
		busy=dstream.readInt();
		statValues=new int[2];
		for (int i=0;i<2;i++)
		{
			statValues[i]=dstream.readInt();
		}
		statBlock=NPCStatblockLibrary.getInstance().getStatblock(ParserHelper.LoadString(dstream));
		
		statusEffectHandler=new StatusEffectHandler();
		statusEffectHandler.load(dstream);
		attributeMods=new short[13];
		//apply statusmods again
		for (int i=0;i<statusEffectHandler.getStatusEffects().size();i++)
		{
			statusEffectHandler.getStatusEffects().get(i).apply(this);
		}
		
	}

	public int getExpValue() {
		
		return statBlock.getExpValue();
	}

	public String getSpriteName() {
		// TODO Auto-generated method stub
		return statBlock.getSpriteName();
	}

	public NPC_RPG_statblock getstatBlock() {

		return statBlock;
	}

	@Override
	public CombatMove getCombatMove(int index) {

		return statBlock.getCombatMove(index);
	}

	@Override
	public boolean getTagged(String tag) {
		// TODO Auto-generated method stub
		return statBlock.getTagged(tag);
	}

	public boolean applyStatus(StatusEffect effect, boolean replace)
	{
		if (statusEffectHandler.applyStatus(effect,replace,this))
		{
			return true;
		}
		return false;
	}
	public NPCItemDrop getItemDrop() {
		return statBlock.getItemDrop();
	}

	@Override
	public void modAttribute(int attribute, int modifier) {
	
		attributeMods[attribute]+=modifier;
	}
	public String getName()
	{
		return actor.getName();
	}

	@Override
	public int getBusy() {
		
		return busy;
	}

	@Override
	public void setBusy(int busy) {

		this.busy=busy;
	}

	@Override
	public void addBusy(int value) {
	
		busy+=value;
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
		
		busy+=2;
		statusEffectHandler.struggle(this, actor);
	}
	
	public boolean stealthCheck(Actor_RPG observer)
	{
		return statusEffectHandler.stealthCheck(observer,this);
	}
	
	@Override
	public boolean hasStatus(int uid)
	{
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
	public int getStealthState() {
		
		return statusEffectHandler.getStealthState();
	}

	@Override
	public void setStealthState(int stealthstate) {
		statusEffectHandler.setStealthState(stealthstate);
		
	}

	@Override
	public Actor getActor() {
		// TODO Auto-generated method stub
		return actor;
	}

	@Override
	public void recover(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getSubAbility(int i) {
		return 0;
	}
}
