package actorRPG.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import combat.CombatMove;
import combat.CombatMove.AttackPattern;
import combat.CooldownHandler;
import combat.effect.Effect_Damage;
import combat.statusEffects.StatusEffect;
import combat.statusEffects.StatusLoader;
import combat.statusEffects.Status_Bind;

import perks.Perk;
import perks.PerkCraftingToken;
import perks.PerkElement;
import perks.PerkInstance;
import perks.PerkLoader;
import perks.PerkProcessor;

import nomad.GameOver;
import nomad.universe.Universe;
import shared.ParserHelper;
import shared.SceneBase;
import view.ViewScene;
import vmo.Game;
import item.Item;
import item.ItemEquip;
import item.ItemWeapon;
import item.instances.ItemDepletableInstance;
import item.instances.ItemStack;
import actor.Actor;

import actor.Modifier;
import actor.Modifier_Element;
import actor.player.Inventory;
import actor.player.Player;
import actorRPG.Actor_RPG;
import actorRPG.StatusEffectHandler;


public class Player_RPG implements Actor_RPG {

	Actor actor;
	int playerExperience, playerLevel;
	int abilities[];
	float stats[];
	int statMax[];
	int attributes[];
	float subAbilities[];
	Inventory playerInventory;
	ArrayList<PerkInstance> playerPerks;
	ArrayList<CombatMove> moveList;
	private int moveLists[]=new int[4];
	CooldownHandler cooldownHandler;
	String quickAction;
	
	private CombatMove[] defaultMoves;
	private List <String> conditionImmunities;

	private int regenDelay;
	int moveChoice;
	
	StatusEffectHandler statusEffectHandler;

	int busy;
	float karmaMeter;
	boolean regenAction;

	private static final float REGENCOST=0.05F;
	
	public void genMoveList()
	{
		//clear
		moveList.clear();
		for (int i=0;i<4;i++)
		{
			moveLists[i]=0;
		}
		//read inventory

		if (playerInventory.getSlot(0)!=null)
		{
			if (ItemStack.class.isInstance(playerInventory.getSlot(0)) && 
					ItemWeapon.class.isInstance(playerInventory.getSlot(0).getItem()))
			{
				ItemWeapon weapon=(ItemWeapon)playerInventory.getSlot(0).getItem();
				for (int i=0;i<weapon.getMoveCount();i++)
				{
					moveList.add(weapon.getMove(i));
				}
			}
			if (ItemDepletableInstance.class.isInstance(playerInventory.getSlot(0)) && 
					ItemWeapon.class.isInstance((playerInventory.getSlot(0).getItem())))
			{
				ItemDepletableInstance di=(ItemDepletableInstance)playerInventory.getSlot(0);
				ItemWeapon weapon=(ItemWeapon)di.getItem();
				moveList.add(weapon.getMove(0));
				if (weapon.getMoveCount()>1)
				{
					for (int i=1;i<weapon.getMoveCount();i++)
					{
						addMove(weapon.getMove(i));
					}		
				}

				//check if weapon move 0 has enough energy to use
				if (moveList.get(0).getAmmoCost()>di.getEnergy())
				{
					moveList.remove(0);
					moveList.add(defaultMoves[0]);
				}
			}
			else if (ItemWeapon.class.isInstance((playerInventory.getSlot(0))))
			{
				ItemWeapon weapon=(ItemWeapon)playerInventory.getSlot(0);
				moveList.add(weapon.getMove(0));
				if (weapon.getMoveCount()>1)
				{
					for (int i=1;i<weapon.getMoveCount();i++)
					{
						addMove(weapon.getMove(i));
					}	
				}
	
	
			}
			else
			{
				moveList.add(defaultMoves[0]);		
			}
		}
		else
		{
			moveList.add(defaultMoves[0]);
		}
		for (int i=1;i<4;i++)
		{
			if (playerInventory.getSlot(i)!=null)
			{
				if (ItemDepletableInstance.class.isInstance(playerInventory.getSlot(i)) && 
						ItemEquip.class.isInstance((playerInventory.getSlot(i).getItem())))
				{
					ItemDepletableInstance di=(ItemDepletableInstance)playerInventory.getSlot(i);
					ItemEquip weapon=(ItemEquip)di.getItem();
					for (int j=0;j<weapon.getMoveCount();j++)
					{
						addMove(weapon.getMove(j));
					}
				}
				else if (ItemEquip.class.isInstance((playerInventory.getSlot(i))))
				{
					ItemEquip weapon=(ItemEquip)playerInventory.getSlot(i).getItem();
					for (int j=0;j<weapon.getMoveCount();j++)
					{
						addMove(weapon.getMove(j));
					}			
				}
			}		
		}
		//read seduction
		addMove(defaultMoves[1]);
		//read perks
	//	handlePerkBasedMoves();
		new Player_RPG_moveHandler().handlePerkBasedMoves((Player)actor,moveList,moveLists, playerPerks);
		
		cooldownHandler.updateList(Arrays.copyOf(moveList.toArray(),moveList.size(),CombatMove[].class));
	}

	private void addMove(CombatMove move)
	{
		int index=0;
		index=getMoveCategoryOffset(move.getMoveType().getValue()+1);
		moveList.add(index,move);
		moveLists[move.getMoveType().getValue()]++;
	}
	
	public int getBindState() {
		return statusEffectHandler.getBindState();
	}



	public void setBindState(int bindState) {
		statusEffectHandler.setBindState(bindState);
	}



	private void genDefaultMoves()
	{
		Document doc=ParserHelper.LoadXML("assets/data/defaultmoves.txt");
		defaultMoves=new CombatMove[2];
		Element root=(Element)doc.getFirstChild();
		//generate punch
		defaultMoves[0]=new CombatMove((Element)root.getElementsByTagName("attack").item(0));
		defaultMoves[1]=new CombatMove((Element)root.getElementsByTagName("seduce").item(0));

	}
	
	public Player_RPG(Actor actor)
	{
		statusEffectHandler=new StatusEffectHandler();
//		bindState=-1;
		this.actor=actor;
		abilities=new int[6];
		stats=new float[4];
		statMax=new int[4];
		attributes=new int[21];
		subAbilities=new float[6];
		moveChoice=0;
		for (int i=0;i<21;i++)
		{
			attributes[i]=0;
		}
		//set beginner abilities
		for (int i=0;i<6;i++)
		{
			abilities[i]=5;
		}
		
		playerPerks=new ArrayList<PerkInstance>();
		conditionImmunities=new ArrayList<String>();
	
		//calculate stats

//		currentAttack=new Attack(new Damage(KINETIC,2,0), STRENGTH, 1.0F,false);
		playerExperience=100;

		genDefaultMoves();
		moveList=new ArrayList<CombatMove>();
//		statusEffects=new ArrayList<StatusEffect>();
		cooldownHandler=new CooldownHandler();
		Calcstats();
		SetInitialValues();
		karmaMeter=50.0F;

	}
	
	private void defaultStats()
	{
		conditionImmunities.clear();
		for (int i=0;i<21;i++)
		{
			attributes[i]=0;
		}
		//set beginner abilities
		for (int i=0;i<6;i++)
		{
			abilities[i]=5;
		}	
		
		subAbilities[METABOLISM]=0.025F;
		subAbilities[REGENERATION]=0.05F;
		subAbilities[REGENTHRESHOLD]=0.5F;
		subAbilities[MOVECOST]=1.0F;
		subAbilities[MOVEAPCOST]=0;	
		subAbilities[CARRY]=8.0F;
		quickAction=null;
	}
	
	public int getPlayerExperience()
	{
		return playerExperience;
	}
	
	public int getPlayerLevel()
	{
		return playerLevel;
	}
	
	public int getNextLevel()
	{
		int pl=playerLevel+1;
		return pl*(5*(pl*5));
		
	}
	
	
	public void setInventory(Inventory inventory)
	{
		playerInventory=inventory;
		genMoveList();
	}

	
	void SetInitialValues()
	{
		
		for (int i=0;i<4;i++)
		{
			stats[i]=statMax[i]*1.0F;
		}	
	}

	public void rest(int duration) {

		if (regenDelay>0)
		{
			duration-=regenDelay;
			if (duration<=0)
			{
				return;
			}
		}
		
		stats[SATIATION]-=calcMetabolism(subAbilities[METABOLISM])*duration;
		if (stats[SATIATION]<=0)
		{
			stats[HEALTH]-=0.25F*duration;
			if (stats[Actor_RPG.HEALTH]<0)
			{
				Game.sceneManager.SwapScene(new GameOver(SceneBase.getVariables(),"you have succumbed to starvation", null, false));
			}
		}
		
		if (stats[HEALTH]<statMax[HEALTH])
		{
			stats[HEALTH]+=subAbilities[REGENERATION]*duration; stats[SATIATION]-=calcMetabolism(REGENCOST)*2*duration;				
		}

		if (stats[RESOLVE]<statMax[RESOLVE])
		{
			stats[RESOLVE]+=0.1F*duration;	
		}
		if (stats[ACTION]<statMax[ACTION])
		{
			stats[ACTION]+=statMax[ACTION];	
		}	
		statusEffectHandler.clearStatusEffects(actor, this,false);
		for (int i=0;i<stats.length;i++)
		{
			if (stats[i]>statMax[i])
			{
				stats[i]=statMax[i];
			}
		}
		cooldownHandler.update(duration);
	}
	public void sleep(int duration)
	{
		stats[SATIATION]-=calcMetabolism(subAbilities[METABOLISM])*duration;
		
		if (stats[SATIATION]<=0)
		{
			stats[HEALTH]-=0.25F*duration;
			if (stats[Actor_RPG.HEALTH]<0)
			{
				Game.sceneManager.SwapScene(new GameOver(SceneBase.getVariables(),"you have succumbed to starvation", null, false));
			}
		}
		
		if (stats[HEALTH]<statMax[HEALTH])
		{
			stats[HEALTH]+=subAbilities[REGENERATION]*duration*4; stats[SATIATION]-=calcMetabolism(REGENCOST)*duration;				
		}

		if (stats[RESOLVE]<statMax[RESOLVE])
		{
			stats[RESOLVE]+=0.1F*duration;	
		}
		if (stats[ACTION]<statMax[ACTION])
		{
			stats[ACTION]+=statMax[ACTION];	
		}
		statusEffectHandler.clearStatusEffects(actor, this,false);
		for (int i=0;i<stats.length;i++)
		{
			if (stats[i]>statMax[i])
			{
				stats[i]=statMax[i];
			}
		}
		cooldownHandler.update(duration);
	}
	
	private float calcMetabolism(float v)
	{
		float minor=v*0.5F;
		float major=v*0.5F;
		float karma=(((float)karmaMeter)/100);
		return minor+(major*karma);
		
	}
	@Override
	public void update()
	{
		if (busy>0)
		{
			busy--;
		}
		//satiation
		stats[SATIATION]-=calcMetabolism(subAbilities[METABOLISM]);

		if (stats[SATIATION]<=0)
		{
			stats[HEALTH]-=0.25F;
			if (stats[Actor_RPG.HEALTH]<0)
			{
				Game.sceneManager.SwapScene(new GameOver(SceneBase.getVariables(),"you have succumbed to starvation", null, false));
			}
		}
		if (regenDelay>0)
		{
			regenDelay--;
		}
		else
		{
			if (stats[HEALTH]<statMax[HEALTH])
			{
				if ( stats[SATIATION]>statMax[SATIATION]*subAbilities[REGENTHRESHOLD])
				{
					float bonus=((float)statMax[HEALTH])/30;
					stats[HEALTH]+=subAbilities[REGENERATION]*bonus; 
					stats[SATIATION]-=calcMetabolism(REGENCOST);		
				}			
			}		
			if (stats[RESOLVE]<statMax[RESOLVE])
			{
				float bonus=((float)statMax[RESOLVE])/30;
				stats[RESOLVE]+=0.05F*bonus;	
			}
		}



		statusEffectHandler.update(1, this);
		
		cooldownHandler.update(1);
		
		if (regenAction && stats[ACTION]<statMax[ACTION])
		{
			stats[ACTION]+=getActionRegen();
		}
		if (busy<=0)
		{
			regenAction=true;
		}
	
	}

	void Calcstats()
	{		

		defaultStats();
		statMax[0]=10+(abilities[ENDURANCE]*4);
		statMax[1]=10+(abilities[INTELLIGENCE]*4);
		statMax[2]=50+(abilities[ENDURANCE]*20);
		statMax[3]=30;
			
		float v=(abilities[ENDURANCE]-3)*playerLevel;
		statMax[0]+=v;
		v=(abilities[INTELLIGENCE]-3)*playerLevel;
		statMax[1]+=v;
		v=(abilities[ENDURANCE]-3)*playerLevel*2;
		statMax[2]+=v;
		v=playerLevel*2;
		statMax[3]+=v;
		
		PerkProcessor processor=new PerkProcessor(attributes,statMax,abilities,subAbilities);
		for (int i=0;i<playerPerks.size();i++)
		{
			processor.processPerkPhaseOne(playerPerks.get(i));
		}
		
		attributes[MELEE]=getAbilityMod(STRENGTH);
		attributes[RANGED]=getAbilityMod(DEXTERITY);	
		attributes[SEDUCTION]=getAbilityMod(CHARM);	
		attributes[DODGE]=getAbilityMod(AGILITY);	
		attributes[PARRY]=getAbilityMod(AGILITY);			
		attributes[STRUGGLE]=getAbilityMod(STRENGTH);
		attributes[PLEASURE]=getAbilityMod(DEXTERITY);
		attributes[PERSUADE]=getAbilityMod(INTELLIGENCE);
		attributes[WILLPOWER]=getAbilityMod(INTELLIGENCE);
		attributes[SCIENCE]=getAbilityMod(INTELLIGENCE);
		attributes[WILLPOWER]=getAbilityMod(INTELLIGENCE);
		attributes[TECH]=getAbilityMod(INTELLIGENCE);
		attributes[LEADERSHIP]=1;
		attributes[PERCEPTION]=getAbilityMod(INTELLIGENCE);
		

		for (int i=0;i<playerPerks.size();i++)
		{
			processor.processPerk(playerPerks.get(i));
		}
		statusEffectHandler.applyStatusEffects(this);
		if (playerInventory!=null)
		{
			playerInventory.setCapacity((int) (10+(abilities[STRENGTH]*subAbilities[CARRY])));	
			
			for (int i=0;i<4;i++)
			{
				if (playerInventory.getSlot(i)!=null)
				{
					Item item=playerInventory.getSlot(i);
					if (ItemEquip.class.isInstance(item)) {
						ItemEquip equip = (ItemEquip) item.getItem();
						if (equip.getModifier() != null) {
							actor.getRPG().AddModifier(equip.getModifier());
						}
					}
				}
			}	
			attributes[DODGE]-=(playerInventory.getEncumbrance()-1);
			if (attributes[DODGE]<-1)
			{
				attributes[DODGE]=-1;
			}
		}

		statusEffectHandler.reApplyStatuses(this);
		((Player)actor).reCalc();
	}
	public void addEXP(int value)
	{
		playerExperience+=value;
		if (ViewScene.m_interface!=null)
		{
			if (playerExperience>=getNextLevel())
			{
				ViewScene.m_interface.DrawText("you gain "+value+ " experience and can now level up");
			}
			else
			{
				ViewScene.m_interface.DrawText("you gain "+value+ " experience");
			}		
		}		
	}

	@Override
	public int getAttribute(int i) {
		if (i>=0)
		{
			return attributes[i];
		}
		return 0;
	}



	@Override
	public int getStat(int i) {

		return (int)stats[i];
	}



	@Override
	public int getAbility(int i) {

		return abilities[i];
	}

	public float getSubAbility(int i)
	{
		return subAbilities[i];
	}

	@Override
	public int getStatMax(int i) {
		return statMax[i];
	}

	public void IncreaseStat(int target, int value) {
		stats[target]+=value;
		if (stats[target]>statMax[target])
		{
			stats[target]=statMax[target];
		}
	}



	@Override
	public void RemoveModifier(Modifier modifier) {
		for (int i=0;i<modifier.numModifiers();i++)
		{
			Modifier_Element element=modifier.getModifier(i);
			switch(element.getType())
			{
			case 0:
				attributes[element.getIndex()]-=element.getValue();
				break;
			case 1:
				subAbilities[element.getIndex()]-=element.getValue();
				calcInventoryCapacity();
				break;
			}
		}
		if (modifier.getImmunity()!=null)
		{
			conditionImmunities.remove(modifier.getImmunity());
		
		}
	}



	@Override
	public void AddModifier(Modifier modifier) {
		for (int i=0;i<modifier.numModifiers();i++)
		{
			Modifier_Element element=modifier.getModifier(i);
			switch(element.getType())
			{
			case 0:
				attributes[element.getIndex()]+=element.getValue();
				break;
			case 1:
				subAbilities[element.getIndex()]+=element.getValue();
				calcInventoryCapacity();
				break;
			}
		}
		if (modifier.getImmunity()!=null)
		{
			conditionImmunities.add(modifier.getImmunity());
		
		}

	}
	
	public void calcInventoryCapacity()
	{
		playerInventory.setCapacity((int) (10+(abilities[STRENGTH]*subAbilities[CARRY])));		
	}



	@Override
	public boolean getStarving() {

		if (stats[Actor_RPG.SATIATION]<=0)
		{
			return true;
		}
		return false;
	}



	@Override
	public int getAbilityMod(int ability) {
		return abilities[ability]-5;
	}





	@Override
	public void setStat(int stat, int value) {
	
		stats[stat]=value;
		if (stats[stat]>statMax[stat])
		{
			stats[stat]=statMax[stat];
		}
	}



	@Override
	public void Heal(float amount) {
		float h=getStatMax(Actor_RPG.HEALTH)*amount;
		float r=getStatMax(Actor_RPG.RESOLVE)*amount;
		setStat(Actor_RPG.HEALTH, getStat(Actor_RPG.HEALTH)+(int)h);
		setStat(Actor_RPG.RESOLVE, getStat(Actor_RPG.RESOLVE)+(int)r);
	}



	@Override
	public void ReduceStat(int stat, int value) {
		stats[stat]-=value;
		if (value>0)
		{
			regenDelay=20;	
		}
		if (stats[stat]<0)
		{
			stats[stat]=0;
		}
		if (stats[stat]>statMax[stat])
		{
			stats[stat]=statMax[stat];
		}
	}
	
	public ArrayList<PerkInstance> getPlayerPerks()
	{
		return playerPerks;
	}
	
	@Override
	public void save(DataOutputStream dstream) throws IOException {

		dstream.writeInt(busy);
		//save exp
		dstream.writeInt(playerExperience);
		//save level
		dstream.writeInt(playerLevel);
		//save abilities
		for (int i=0;i<abilities.length;i++)
		{
			dstream.writeInt(abilities[i]);
		}
		//save attributes
		for (int i=0;i<attributes.length;i++)
		{
			dstream.writeInt(attributes[i]);
		}
		//save stats
		for (int i=0;i<stats.length;i++)
		{
			dstream.writeFloat(stats[i]);
		}
		//save max stats
		for (int i=0;i<statMax.length;i++)
		{
			dstream.writeInt(statMax[i]);
		}
		
		for (int i=0;i<subAbilities.length;i++)
		{
			dstream.writeFloat(subAbilities[i]);
		}
		
		dstream.writeInt(playerPerks.size());
		for (int i=0;i<playerPerks.size();i++)
		{
			playerPerks.get(i).save(dstream);
		}
		
		statusEffectHandler.save(dstream);
		dstream.writeFloat(karmaMeter);
		
		cooldownHandler.save(dstream);
		
		if (quickAction==null)
		{
			dstream.writeBoolean(false);
		}
		else
		{
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, quickAction);
		}
	}	
	

	public Player_RPG(DataInputStream dstream, Actor actor) throws IOException {
		this.actor=actor;
		abilities=new int[6];
		stats=new float[4];
		statMax=new int[4];
		subAbilities=new float[6];
		attributes=new int[21];
		moveChoice=0;
		for (int i=0;i<14;i++)
		{
			attributes[i]=0;
		}
		//set beginner abilities
		for (int i=0;i<6;i++)
		{
			abilities[i]=5;
		}
		
		busy=dstream.readInt();
		//load exp
		playerExperience=dstream.readInt();
		//load level
		playerLevel=dstream.readInt();
		//load abilities
		for (int i=0;i<abilities.length;i++)
		{
			abilities[i]=dstream.readInt();
		}
		//load attributes
		for (int i=0;i<attributes.length;i++)
		{
			attributes[i]=dstream.readInt();
		}
		//load stats
		for (int i=0;i<stats.length;i++)
		{
			stats[i]=dstream.readFloat();
		}
		//load max stats
		for (int i=0;i<statMax.length;i++)
		{
			statMax[i]=dstream.readInt();
		}
		
		for (int i=0;i<subAbilities.length;i++)
		{
			subAbilities[i]=dstream.readFloat();
		}
		
		playerPerks=new ArrayList<PerkInstance>();
		int count=dstream.readInt();
		for (int i=0;i<count;i++)
		{
			PerkInstance p=PerkLoader.loadPerk(dstream);
			if (p!=null)
			{
				playerPerks.add(p);		
			}
		}		
		conditionImmunities=new ArrayList<String>();

		genDefaultMoves();
		moveList=new ArrayList<CombatMove>();
		cooldownHandler=new CooldownHandler();
		statusEffectHandler=new StatusEffectHandler();
		statusEffectHandler.load(dstream);
		karmaMeter=dstream.readFloat();
		
		cooldownHandler.load(dstream);
		
		boolean b=dstream.readBoolean();
		if (b)
		{
			quickAction=ParserHelper.LoadString(dstream);
		}
	}

	public PerkInstance getPerkInstance(Perk perk) {
		for (int i=0;i<playerPerks.size();i++)
		{
			if (playerPerks.get(i).getPerk()==perk)
			{
				return playerPerks.get(i);
			}
		}
		return null;
	}
	
	public PerkInstance getPerkInstance(String name) {
		for (int i=0;i<playerPerks.size();i++)
		{
			if (playerPerks.get(i).getPerk().getName().equals(name) ||
				name.equals(playerPerks.get(i).getPerk().getAlias()))
			{
				return playerPerks.get(i);
			}
		}
		return null;
	}

	public void addPerk(Perk perk) {
		//check if an existing is in the list
		
		//if so rank up that perk
		for (int i=0;i<playerPerks.size();i++)
		{
			if (playerPerks.get(i).getPerk()==perk)
			{
				playerPerks.get(i).setPerkRank(playerPerks.get(i).getPerkRank()+1);
				Calcstats();
				return;
			}
		}
		//if not create new perk instance
		playerPerks.add(new PerkInstance(perk));
		Calcstats();
	}

	public int getNumPerks() {
		return playerPerks.size();
	}
	
	public PerkInstance getPerk(int index)
	{
		return playerPerks.get(index);
	}

	public void levelUp(Perk perk) {

		
		playerExperience-=getNextLevel();
		playerLevel++;
		addPerk(perk);
		stats[0]=statMax[0];
		stats[1]=statMax[1];
		stats[3]=statMax[3];
	}

	@Override
	public CombatMove getCombatMove(int index) {
		return moveList.get(index);
	}
	
	public int getNumMoves()
	{
		return moveList.size();
	}

	public void setMove(int index) {
		moveChoice=index;	
	}

	public int getMoveChoice() {
		return moveChoice;
	}

	@Override
	public boolean getTagged(String tag) {
		return false;
	}

	public boolean applyStatus(StatusEffect effect,boolean replace)
	{
		if (statusEffectHandler.applyStatus(effect,replace,this))
		{
			calcInventoryCapacity();	
			ViewScene.m_interface.UpdateInfo();
			return true;
		}
		return false;
	}

	@Override
	public void modAttribute(int attribute, int modifier) {
		attributes[attribute]+=modifier;
	}
	public String getName()
	{
		return actor.getName();
	}
	
	public ArrayList<StatusEffect> getStatusEffects()
	{
		return statusEffectHandler.getStatusEffects();
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
	
	public int getKarmaMeter()
	{
		return (int) karmaMeter;
	}
	
	public void feed(int amount, boolean predatory)
	{
		if (predatory==true)
		{
			feedPred(amount);
		}
		else
		{
			feedPrey(amount);
		}
	}
	
	private void feedPred(float amount)
	{
		float modifier=karmaMeter/100;
		float change=amount/20;
		amount=amount*modifier;
		if (amount<1)
		{
			amount=1;
		}
		stats[Actor_RPG.SATIATION]+=amount;
		karmaMeter+=change;
	
		if (karmaMeter>100)
		{
			karmaMeter=100;
		}
		if (stats[Actor_RPG.SATIATION]>statMax[Actor_RPG.SATIATION])
		{
			stats[Actor_RPG.SATIATION]=statMax[Actor_RPG.SATIATION];
		}
	}
	
	private void feedPrey(float amount)
	{
		float modifier=1-(karmaMeter/100);
		float change=amount/20;		
		amount=amount*modifier;	
		if (amount<1)
		{
			amount=1;
		}
		stats[Actor_RPG.SATIATION]+=amount;
		
		karmaMeter-=change;
		if (karmaMeter<0)
		{
			karmaMeter=0;
		}
		if (stats[Actor_RPG.SATIATION]>statMax[Actor_RPG.SATIATION])
		{
			stats[Actor_RPG.SATIATION]=statMax[Actor_RPG.SATIATION];
		}
	}
	
	public boolean hasStatus(int uid)
	{
		return statusEffectHandler.hasStatus(uid);

	}



	@Override
	public void struggle() {
		// TODO Auto-generated method stub
		busy+=2;
		statusEffectHandler.struggle(this, actor);
	}

	public boolean stealthCheck(int spot,boolean remove)
	{
		
		return true;
	}

	public void removeStatus(int uid) {
	
		statusEffectHandler.removeStatus(uid,this);

	}



	@Override
	public Actor getBindOrigin() {
		return statusEffectHandler.getBindOrigin();

	}

	public CooldownHandler getCooldownHandler() {
		return cooldownHandler;
	}



	public void modSubAbility(int attribute, int modifier) {
		float v=((float)modifier)/100;
		subAbilities[attribute]+=v;
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

		return actor;
	}



	public void modKarmaMeter(float value) {
		this.karmaMeter = karmaMeter+value;
	}

	private float getActionRegen()
	{
		if (stats[SATIATION]>statMax[SATIATION]*0.5F)
		{
			return statMax[ACTION]/120.0F;			
		}
		return statMax[ACTION]/240.0F;
	}

	@Override
	public void recover(int i) {

		if (stats[ACTION]<statMax[ACTION])
		{
			stats[ACTION]+=getActionRegen()*2*i;
			if (stats[ACTION]>statMax[ACTION])
			{
				stats[ACTION]=statMax[ACTION];
			}
		}	
		regenAction=false;
	}
	public void useAction(int amount)
	{
		stats[Actor_RPG.ACTION]-=amount;
		if (stats[Actor_RPG.ACTION]<=0)
		{
			stats[Actor_RPG.ACTION]=0;
		}
		regenAction=false;
	}

	public int getMoveCategoryOffset(int index)
	{
		switch (index)
		{
			case 0:
			return 1;
			
			case 1:
				
			return 1+moveLists[0];
			
			case 2:
				
			return 1+moveLists[0]+moveLists[1];
			
			case 3:
				
			return 1+moveLists[0]+moveLists[1]+moveLists[2];
			case 4:
				
			return 1+moveLists[0]+moveLists[1]+moveLists[2]+moveLists[3];
				
		}
		return 0;
	}

	public int getMoveCategorySize(int index) {
		return moveLists[index];
	}
	
	public String getQuickAction()
	{
		return this.quickAction;
	}
	
	public void setQuickAction(String actionName)
	{
		this.quickAction=actionName;
	}
	
	public boolean useQuickMove()
	{
		if (quickAction==null)
		{
			return false;
		}
		for (int i=0;i<moveList.size();i++)
		{
			if (moveList.get(i).getMoveName().equals(quickAction))
			{
				moveChoice=i;
				return true;
			}
		}
		
		quickAction=null;
		return false;
	}

	public void removeEquipStatus(int slot) {
	
		List<StatusEffect>statusEffects=statusEffectHandler.getStatusEffects();
		for (int i=0;i<statusEffects.size();i++)
		{
			if (statusEffects.get(i).getUID()%10==slot)
			{
				statusEffects.remove(i);
				break;
			}
		}
	}

	public int getCraftingTokenCount(String token) {
		int v=0;
		for (int i=0;i<playerPerks.size();i++)
		{
			for (int j=0;j<playerPerks.get(i).getPerk().getNumElements();j++)
			{
				if (PerkCraftingToken.class.isInstance(playerPerks.get(i).getPerk().getElement(j)))
				{
					PerkCraftingToken t=(PerkCraftingToken)playerPerks.get(i).getPerk().getElement(j);
					if (t.getToken().equals(token))
					{
						v+=playerPerks.get(i).getPerkRank();
					}
				}
			}
		}
		return v;
	}

	public boolean isConditionImmune(String identity) {
		for (int i=0;i<conditionImmunities.size();i++)
		{
			if (conditionImmunities.get(i).contains(identity))
			{
				return true;
			}
		}
		return false;
	}

	

}
