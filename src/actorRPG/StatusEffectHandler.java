package actorRPG;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import actor.Actor;
import actorRPG.player.Player_RPG;
import combat.statusEffects.StatusEffect;
import combat.statusEffects.StatusFaction;
import combat.statusEffects.StatusLoader;
import combat.statusEffects.Status_Bind;
import combat.statusEffects.Status_Defence;
import combat.statusEffects.Status_Stealth;
import nomad.Universe;
import view.ViewScene;

public class StatusEffectHandler {

	ArrayList<StatusEffect> statusEffects;
	ArrayList<Status_Defence> statusDefences;
	int bindState;
	int stealthState;
	private int factionState;
	
	public StatusEffectHandler()
	{
		bindState=-1;
		stealthState=-1;
		factionState = -1;
		statusEffects=new ArrayList<StatusEffect>();
		statusDefences=new ArrayList<Status_Defence>();
	}
	
		
	public void update(int time,Actor_RPG actor)
	{
		if (statusEffects.size()>0)
		{
			for (int i=statusEffects.size()-1;i>=0;i--)
			{
				statusEffects.get(i).update(actor);
				if (statusEffects.get(i).maintain()==true)
				{
					statusDefences.remove(statusEffects.get(i));
					statusEffects.get(i).remove(actor);
					statusEffects.remove(i);
				}
			}
		}		
	}
	
	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(statusEffects.size());
		for (int i = 0; i < statusEffects.size(); i++) {
			statusEffects.get(i).save(dstream);
		}
		stealthState = -1;
		bindState=-1;
		factionState=-1;
		for (int i = 0; i < statusEffects.size(); i++) {
			if (stealthState==-1 && Status_Stealth.class.isInstance(statusEffects.get(i))) {
				stealthState = i;
			}
			if (bindState==-1 && Status_Bind.class.isInstance(statusEffects.get(i))) {
				bindState = i;
			}
			if (factionState==-1 && StatusFaction.class.isInstance(statusEffects.get(i))) {
				factionState = i;
			}
		}
		dstream.writeInt(bindState);	
		dstream.writeInt(stealthState);
		dstream.writeInt(factionState);
	}

	public void load(DataInputStream dstream) throws IOException {
		statusEffects = new ArrayList<StatusEffect>();
		// load status effects
		int c = dstream.readInt();
		if (c > 0) {
			for (int i = 0; i < c; i++) {
				StatusEffect effect = StatusLoader.loadStatusEffect(dstream);
				statusEffects.add(effect);
				if (Status_Defence.class.isInstance(effect)) {
					statusDefences.add((Status_Defence) effect);
				}
			}
		}
		bindState = dstream.readInt();
		stealthState = dstream.readInt();
		factionState=dstream.readInt();
	}

	
	public ArrayList<StatusEffect> getStatusEffects()
	{
		return statusEffects;
	}
	
	public void reApplyStatuses(Actor_RPG subject)
	{
		for (int i=0;i<statusEffects.size();i++)
		{
			statusEffects.get(i).apply(subject);
		}
	}
	
	public boolean applyStatus(StatusEffect effect, boolean replace,Actor_RPG subject)
	{
		boolean r=false;
		for (int i=0;i<statusEffects.size();i++)
		{
			if (statusEffects.get(i).equals(effect))
			{
				if (replace)
				{
					statusEffects.get(i).remove(subject);
					statusDefences.remove(statusEffects.get(i));
					statusEffects.remove(i);
					r=true;
					break;
				}
				else
				{
					return false;
				}
				
			}
		}
		
		effect.apply(subject);	
		statusEffects.add(effect);
		if (Status_Stealth.class.isInstance(effect))
		{
			stealthState=statusEffects.indexOf(effect);
		}
		if (Status_Bind.class.isInstance(effect))
		{
			bindState=statusEffects.indexOf(effect);
		}
		if (StatusFaction.class.isInstance(effect)) {
			factionState = statusEffects.indexOf(effect);
		}
		if (Status_Defence.class.isInstance(effect))
		{
			statusDefences.add((Status_Defence)effect);
		}
		return true;	
	}


	public int getBindState() {
		return bindState;
	}


	public void setBindState(int bindState) {
		this.bindState = bindState;
	}
	
	public boolean hasStatus(int uid)
	{
		for (int i=0;i<statusEffects.size();i++)
		{
			if (statusEffects.get(i).getUID()==uid)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void struggle(Actor_RPG rpg,Actor actor)
	{
		int roll=Universe.m_random.nextInt(20)+rpg.getAttribute(Actor_RPG.STRUGGLE);
		if (Status_Bind.class.isInstance(statusEffects.get(bindState)))
		{
			Status_Bind sb=(Status_Bind)statusEffects.get(bindState);
			if (sb.struggle(roll,actor.getName()))
			{
				if (sb.isOriginDependent() && sb.getOrigin()!=null)
				{
					sb.getOrigin().addBusy(2);
				}
				sb.remove(rpg);
				statusEffects.remove(sb);
			}		
		}	
	}
	
	public Actor getBindOrigin()
	{
		if (bindState!=-1)
		{
			if (Status_Bind.class.isInstance(statusEffects.get(bindState)))
			{
				Status_Bind bind=(Status_Bind)statusEffects.get(bindState);
				return bind.getOrigin();
			}
		}
		return null;
	}
	
	public void applyStatusEffects(Actor_RPG rpg)
	{
		for (int i=0;i<statusEffects.size();i++)
		{
			statusEffects.get(i).apply(rpg);
		}
	}
	
	public void clearStatusEffects(Actor actor, Actor_RPG rpg)
	{
		if (statusEffects.size()>0)
		{
			for (int i=statusEffects.size()-1;i>=0;i--)
			{
					statusEffects.get(i).remove(rpg);
					statusEffects.remove(i);
					if (Player_RPG.class.isInstance(actor))
					{
						ViewScene.m_interface.UpdateInfo();
					}			
			}
		}		
	}
	
	public void removeStatus(int uid,Actor_RPG rpg)
	{
		for (int i=0;i<statusEffects.size();i++)
		{
			if (statusEffects.get(i).getUID()==uid)
			{
				statusEffects.get(i).remove(rpg);
				statusEffects.remove(i);
				break;
			}
		}
	}


	public int getStealthState() {
		return stealthState;
	}


	public void setStealthState(int stealthState) {
		this.stealthState = stealthState;
	}


	public boolean stealthCheck(int spot,Actor_RPG actor, boolean remove) {

		if (stealthState>=statusEffects.size() || !Status_Stealth.class.isInstance(statusEffects.get(stealthState)))
		{
			stealthState=-1;
			for (int i=0;i<statusEffects.size();i++)
			{
				if (Status_Stealth.class.isInstance(statusEffects.get(i)))
				{
					stealthState=i;
					break;
				}		
			}
			return true;
		}
		Status_Stealth status=(Status_Stealth)statusEffects.get(stealthState);
		
		if (status.spotCheck(spot))
		{
			if (remove)
			{
				status.remove(actor);
				statusEffects.remove(stealthState);
				stealthState=-1;				
			}
			return true;
		}
		return false;

	}

	public int getFactionState() {
		if (factionState>-1)
		{
			if (factionState>=statusEffects.size()|| !StatusFaction.class.isInstance(statusEffects.get(factionState)))
			{
				factionState=-1;
				for (int i = 0; i < statusEffects.size(); i++) {
					if (StatusFaction.class.isInstance(statusEffects.get(i))) {
						factionState = i;
						break;
					}
				}		
			}		
		}

		return factionState;
	}
	
	public int runDefenceStack(int damage, int damageType)
	{
		for (int i=0;i<statusDefences.size();i++)
		{
			damage=statusDefences.get(i).runDefence(damage, damageType);
		}
		return damage;
	}
	
}
