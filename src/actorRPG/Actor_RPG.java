package actorRPG;

import java.io.DataOutputStream;
import java.io.IOException;

import combat.CombatMove;
import combat.statusEffects.StatusEffect;
import faction.Faction;
import actor.Actor;
import actor.Modifier;

public interface Actor_RPG {
	public static final int STRENGTH = 0; // melee attack, melee defence, carry
											// capacity
	public static final int ENDURANCE = 1; // health
	public static final int DEXTERITY = 2; // ranged attack, some skills
	public static final int AGILITY = 3; // ranged defence, melee defence
	public static final int INTELLIGENCE = 4; // resolve, some skills
	public static final int CHARM = 5; // seduction attack

	public static final int HEALTH = 0;
	public static final int RESOLVE = 1;
	public static final int SATIATION = 2;
	public static final int ACTION = 3;

	public static final int KINETIC = 0;
	public static final int THERMAL = 1;
	public static final int SHOCK = 2;
	public static final int TEASE = 3;
	public static final int PHEREMONE = 4;
	public static final int PSYCHIC = 5;
	public static final int PARRY = 6;
	public static final int DODGE = 7;
	public static final int MELEE = 8;
	public static final int RANGED = 9;
	public static final int WILLPOWER = 10;
	public static final int SEDUCTION = 11;
	public static final int STRUGGLE = 12;
	public static final int PLEASURE = 13;
	public static final int PERSUADE = 14;
	public static final int TECH = 15;
	public static final int SCIENCE = 16;
	public static final int NAVIGATION = 17;
	public static final int GUNNERY = 18;
	public static final int PERCEPTION = 19;
	public static final int LEADERSHIP = 20;

	public static final int REGENERATION = 0;
	public static final int REGENTHRESHOLD = 1;
	public static final int METABOLISM = 2;
	public static final int MOVECOST = 3;
	public static final int MOVEAPCOST = 4;
	public static final int CARRY=5;

	public CombatMove getCombatMove(int index);

	public int getAttribute(int i);

	public int getStat(int i);

	public int getAbility(int i);

	public int getStatMax(int i);

	public void update();

	void RemoveModifier(Modifier modifier);

	void AddModifier(Modifier modifier);

	public boolean getStarving();

	public int getAbilityMod(int ability);

	public void setStat(int stat, int value);

	public void Heal(float amount);

	public void IncreaseStat(int stat, int value);

	public void ReduceStat(int stat, int value);

	public void save(DataOutputStream dstream) throws IOException;

	public boolean getTagged(String tag);

	public boolean applyStatus(StatusEffect effect, boolean replace);

	public void modAttribute(int attribute, int modifier);

	public String getName();

	public int getBusy();

	public void setBusy(int busy);

	public void addBusy(int value);

	public int getBindState();

	public void setBindState(int bindState);

	public Actor getBindOrigin();

	public int getStealthState();

	public void setStealthState(int stealthstate);

	public void struggle();

	public boolean stealthCheck(int spot);

	public boolean hasStatus(int uid);

	public void removeStatus(int uid);

	public Actor getActor();

	public StatusEffectHandler getStatusEffectHandler();

	public void recover(int i);

	public float getSubAbility(int i);
	
	public default boolean isThreatening(Faction faction)
	{
		return false;
	};	
}
