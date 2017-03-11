package actor;

import faction.FactionLibrary;
import input.Keyboard;
import item.Item;
import item.ItemDepletableInstance;
import item.ItemEquip;
import item.ItemHasEnergy;
import item.ItemStack;
import item.ItemWeapon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import nomad.FlagField;
import nomad.Universe;


import perks.Perk;
import perks.PerkLibrary;
import research.Encyclopedia;
import actorRPG.Actor_RPG;
import actorRPG.Player_RPG;
import actorRPG.RPGActionHandler;



import shared.ParserHelper;
import shared.Vec2f;
import view.ViewScene;
import view.ModelController_Int;
import view.ZoneInteractionHandler;
import zone.Tile;

import org.lwjgl.glfw.GLFWKeyCallback;

import combat.CombatMove;
import combat.CombatMove.AttackPattern;
import combat.ThrownWeaponHandler;
import combat.effect.Effect;
import combat.effect.Effect_Damage;
import combat.effect.Effect_Recover;
import combat.effect.Effect_Status;
import crafting.CraftingLibrary;
import static org.lwjgl.glfw.GLFW.*;

public class Player extends Actor
{
	
	private Player_LOOK playerLook;
	
	private Inventory playerInventory;
	
	private CraftingLibrary craftingLibrary;
	
	private Encyclopedia encyclopedia;
	
	private FlagField globalFlags;

	private float controlClock;

	 NPC [] companionSlots;
	
	public void reCalc()
	{
		if (actorRPG!=null)
		{
			int leadership=actorRPG.getAttribute(Actor_RPG.LEADERSHIP);
			if (leadership>0)
			{
				if (companionSlots==null)
				{
					companionSlots=new NPC [leadership];
				}
				else if (leadership>companionSlots.length)
				{
					NPC [] temp=companionSlots;
					companionSlots=new NPC[leadership];
					for (int i=0;i<temp.length;i++)
					{
						companionSlots[i]=temp[i];
					}
				}
			}		
		}
	}
	
	public boolean isFreeCompanion()
	{
		if (companionSlots!=null)
		{
			for (int i=0;i<companionSlots.length;i++)
			{
				if (companionSlots[i]==null)
				{
					return true;
				}
			
			}
		}
		return false;
	}
	
	int getMoveCost()
	{
		if (actorRPG.getStarving())
		{
			return moveCost*2;
		}
		return playerInventory.getEncumbrance()+1;
	}
	
	public void setActorName(String name)
	{
		actorName=name;
	}
	
	public Inventory getInventory()
	{
		return playerInventory;
	}
	
	
	
	public Player_LOOK getPlayerLook() {
		return playerLook;
	}



	public Inventory getPlayerInventory() {
		return playerInventory;
	}



	public CraftingLibrary getCraftingLibrary() {
		return craftingLibrary;
	}



	public Player()
	{
	
		actorVisibility=true;
	
		craftingLibrary=new CraftingLibrary();
		craftingLibrary.load();
		
		globalFlags=new FlagField();
		
		actorFaction=FactionLibrary.getInstance().getFaction("player");
	}
	
	public Player(Vec2f p)
	{
		
		encyclopedia=new Encyclopedia();
		encyclopedia.generateEntries();
		
		actorName="you";

		actorPosition=p;

		actorVisibility=true;
		
		actorRPG=new Player_RPG(this);
		playerInventory=new Inventory(actorRPG.getAbility(Actor_RPG.STRENGTH)*8);
		Player_RPG prpg=(Player_RPG)actorRPG;
		prpg.setInventory(playerInventory);

		playerLook=new Player_LOOK();
		actorDescription="This, is you.";

		moveCost=playerInventory.getEncumbrance();
		
		
		RPGHandler=new RPGActionHandler(actorRPG,this);
		craftingLibrary=new CraftingLibrary();
		craftingLibrary.load();	
		globalFlags=new FlagField();
		
		actorFaction=FactionLibrary.getInstance().getFaction("player");
	}
	

	
	public Player_LOOK getLook()
	{
		return playerLook;
	}

	@Override
	public void Update()
	{
		actorRPG.update();
	}
	
	public boolean CanAct()
	{
		if (actorRPG.getBusy()==0)
		{
			return true;
		}
		return false;
	}
	
	public void controlUpdate(float dt)
	{
		if (controlClock>0)
		{
			controlClock+=dt;

		}
	}
	
	public boolean altControl()
	{
		
	
		if (!Keyboard.isKeyDown(GLFW_KEY_UP) && !Keyboard.isKeyDown(GLFW_KEY_W) &&
			!Keyboard.isKeyDown(GLFW_KEY_DOWN) &&  !Keyboard.isKeyDown(GLFW_KEY_S) &&
			!Keyboard.isKeyDown(GLFW_KEY_LEFT) && !Keyboard.isKeyDown(GLFW_KEY_A ) &&
			!Keyboard.isKeyDown(GLFW_KEY_RIGHT) && !Keyboard.isKeyDown(GLFW_KEY_D ))
		{
			controlClock=0;
			return false;
		}
		else
		{
			if (controlClock<=0)
			{
				controlClock=0.01F;
			}
			else if (controlClock>0.1F)
			{
				controlClock=0.01F;
				return altControlFinal();
			}
		}
		
		return false;
	}
	
	@Override
	public boolean move(int direction)
	{
		Vec2f p=ZoneInteractionHandler.getPos(direction, getPosition());
		Actor actor=Universe.getInstance().getCurrentZone().getActor((int)p.x, (int)p.y);
		if (actor!=null && NPC.class.isInstance(actor))
		{
			if (actor.isBlocking()==false)
			{
				actor.setPosition(new Vec2f(actorPosition.x,actorPosition.y));
				return super.move(direction);
			}
			else
			{
				NPC npc=(NPC) actor;
				if (npc.isHostile(actorFaction.getFilename()) && npc.getAttackable())
				{
					return useMove(((Player_RPG)actorRPG).getMoveChoice(),npc);			
				}		
			}

		}
		return super.move(direction);
	}

	private boolean altControlFinal()
	{
		boolean up=(Keyboard.isKeyDown(GLFW_KEY_UP) || Keyboard.isKeyDown(GLFW_KEY_W));
		boolean right=(Keyboard.isKeyDown(GLFW_KEY_RIGHT) || Keyboard.isKeyDown(GLFW_KEY_D ));
		boolean down=(Keyboard.isKeyDown(GLFW_KEY_DOWN) || Keyboard.isKeyDown(GLFW_KEY_S));
		boolean left=(Keyboard.isKeyDown(GLFW_KEY_LEFT) || Keyboard.isKeyDown(GLFW_KEY_A ));
		if (up && right)
		{
			return move(1);
		}
		if (down && right)
		{
			return move(3);		
		}		
		if (up && left)
		{
			return move(7);
		}
		if (down && left)
		{
			return move(5);
		}	
		
		if (up)
		{
			return move(0);
		}
		if (down)
		{
			return move(4);		
		}		
		if (left)
		{
			return move(6);
		}
		if (right)
		{
			return move(2);
		}		
		
		return false;
	}
	
	public boolean Control()
	{
		if (Keyboard.isKeyDown(GLFW_KEY_KP_8))
		{
			return move(0);
		}
		if (Keyboard.isKeyDown(GLFW_KEY_KP_2))
		{
			return move(4);		
		}		
		if (Keyboard.isKeyDown(GLFW_KEY_KP_4 ))
		{
			return move(6);
		}
		if (Keyboard.isKeyDown(GLFW_KEY_KP_6 ))
		{
			return move(2);
		}			
		if (Keyboard.isKeyDown(GLFW_KEY_KP_9 ))
		{
			return move(1);
		}
		if (Keyboard.isKeyDown(GLFW_KEY_KP_3 ))
		{
			return move(3);
		}	
		if (Keyboard.isKeyDown(GLFW_KEY_KP_1 ))
		{
			return move(5);
		}
		if (Keyboard.isKeyDown(GLFW_KEY_KP_7 ))
		{
			return move(7);
		}	
		if (Keyboard.isKeyDown(GLFW_KEY_KP_5))
		{
			actorRPG.addBusy(2);
			return true;
		}	
		if (Keyboard.isKeyDown(GLFW_KEY_R))
		{
			actorRPG.addBusy(20);
			ViewScene.m_interface.screenFade(0.5F);
			return true;
		}
		return altControl();
	}
	
	public void ApplyEffect(Effect effect)
	{
		effect.applyEffect(this,this, false);

	}
	
	public void TakeAction()
	{
		actorRPG.addBusy(2);

	}
	
	public void calcMove()
	{
		moveCost=(int) (playerInventory.getEncumbrance()+((Player_RPG)actorRPG).getSubAbility(Actor_RPG.MOVECOST));
	}
	
	public Item Equip(int slot, Item item)
	{
		Item slotitem=playerInventory.m_slots[slot];
		if (slotitem!=null)
		{
			playerInventory.m_weight-=slotitem.getWeight();
		}
		if (ItemEquip.class.isInstance(item))
		{
			ItemEquip equip=(ItemEquip)item.getItem();
			if (equip.getModifier()!=null)
			{
				actorRPG.AddModifier(equip.getModifier());	
			}		
		}

		playerInventory.m_slots[slot]=item;	
		
		playerInventory.m_weight+=item.getWeight();
		Player_RPG rpg=(Player_RPG)actorRPG;

		rpg.genMoveList();
		return slotitem;
	}
	
	public Item UnEquip(int slot)
	{
		Item item=playerInventory.m_slots[slot].getItem();
		if (ItemEquip.class.isInstance(item))
		{
			ItemEquip Eitem=(ItemEquip)playerInventory.m_slots[slot].getItem();
			if (Eitem.getModifier()!=null)
			{
				actorRPG.RemoveModifier(Eitem.getModifier());			
			}

		}
	
		Item r_item=playerInventory.m_slots[slot];
		playerInventory.m_slots[slot]=null;	
		playerInventory.m_weight-=item.getWeight();
		if (slot!=Inventory.QUICK)
		{
			Player_RPG rpg=(Player_RPG)actorRPG;
			rpg.genMoveList();
		}
		return r_item;
	}
	
	@Override
	public CombatMove getCombatMove()
	{
		if (playerInventory.getSlot(Inventory.HAND)!=null)
		{
			if (playerInventory.getSlot(Inventory.HAND).getItem().getClass().getName().contains("Weapon"))
			{
				ItemWeapon weapon=(ItemWeapon)playerInventory.getSlot(Inventory.HAND).getItem();
				ItemDepletableInstance depletable=(ItemDepletableInstance)playerInventory.getSlot(Inventory.HAND);
				if (weapon.getEnergy()!=null)
				{
					if (depletable.getEnergy()>=weapon.getMove(0).getAmmoCost())
					{
						return weapon.getMove(0);
					}
					else
					{
						return actorRPG.getCombatMove(0);
					}
				}
				else
				{
					return actorRPG.getCombatMove(0);
				}
	
			}
		}
		
		return actorRPG.getCombatMove(0);
	}
/*	
	@Override
	public int Harm(Damage attack, Actor actor,int bonus) 
	{
		int v=RPGHandler.Harm(attack, actor, bonus);
		
		if (actorRPG.getStat(Actor_RPG.HEALTH)<=0)
		{
			if (NPC.class.isInstance(actor))
			{
				ViewScene.m_interface.PlayerBeaten((NPC)actor, false);		
			}

		}
		return v;
	}
	
	@Override
	public int Weaken(int strength, int type, Actor actor) 
	{
		int v=RPGHandler.Weaken(strength, type, actor);
		
		if (actorRPG.getStat(Actor_RPG.RESOLVE)<=0)
		{
			if (NPC.class.isInstance(actor))
			{
				ViewScene.m_interface.PlayerBeaten((NPC)actor, true);		
			}

		}	
		
		
		return v;
	}
*/
	
	
	public void healTo(float proportion)
	{
		spriteInterface.setFlashing(0);
		
		float h=actorRPG.getStatMax(Actor_RPG.HEALTH)*proportion;
		float r=actorRPG.getStatMax(Actor_RPG.RESOLVE)*proportion;
		if (actorRPG.getStat(Actor_RPG.HEALTH)<h)
		{
			actorRPG.setStat(Actor_RPG.HEALTH, (int) h);		
		}
		if (actorRPG.getStat(Actor_RPG.RESOLVE)<r)
		{
			actorRPG.setStat(Actor_RPG.RESOLVE, (int) r);
		}
	}
	
	public void heal(float proportion)
	{
		spriteInterface.setImage(0);

		actorRPG.Heal(proportion);
		
	}
	
	public void Save(String filename) throws IOException
	{
		File file=new File("saves/"+filename+"/"+"player.sav");
		if (file.exists()==false)
		{
			file.createNewFile();
		}
		FileOutputStream fstream=new FileOutputStream(file);
		DataOutputStream dstream=new DataOutputStream(fstream);
		SavePlayer(dstream);
		dstream.close();
		
	}

	@Override
	public void Save(DataOutputStream dstream) throws IOException 
	{
		dstream.write(0);
	}
	
	public FlagField getFlags()
	{
		return globalFlags;
	}
	
	private void SavePlayer(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		
		//save position
		actorPosition.Save(dstream);
		//save name
		ParserHelper.SaveString(dstream, actorName);
		//save inventory
		playerInventory.save(dstream);
		//save rpg 
		actorRPG.save(dstream);
		//save look
		playerLook.save(dstream);
		
		craftingLibrary.save(dstream);
		
		globalFlags.save(dstream);
		
		ParserHelper.SaveString(dstream,actorName);
		
		int c=0;
		if (companionSlots!=null)
		{
			c=companionSlots.length;
		}
		dstream.writeInt(c);
		if (companionSlots!=null)
		{
			for (int i=0;i<c;i++)
			{
				if (companionSlots[i]!=null)
				{
					dstream.writeBoolean(true);
					companionSlots[i].Save(dstream);
				}
				else
				{
					dstream.writeBoolean(false);
				}
			}	
		}

		encyclopedia.save(dstream);
		
	}
	
	@Override
	public void Load(DataInputStream dstream) throws IOException {


		actorPosition=new Vec2f(dstream);
		
		actorName=ParserHelper.LoadString(dstream);
		
		playerInventory=new Inventory(dstream);	
		
		actorRPG=new Player_RPG(dstream,this);
		
		playerLook=new Player_LOOK(dstream);
		

		RPGHandler=new RPGActionHandler(actorRPG,this);
		Player_RPG prpg=(Player_RPG)actorRPG;
		prpg.setInventory(playerInventory);
		moveCost=playerInventory.getEncumbrance();
		
		craftingLibrary.load(dstream);
		
		globalFlags.load(dstream);
		
		actorName=ParserHelper.LoadString(dstream);
		
		int c=dstream.readInt();
		if (c>0)
		{
			companionSlots=new NPC[c];
			for (int i=0;i<c;i++)
			{
				boolean b=dstream.readBoolean();
				if (b==true)
				{
					int v=dstream.read();
					companionSlots[i]=new NPC();
					companionSlots[i].Load(dstream);			
					
				}
			}
			
		}
		
		encyclopedia=new Encyclopedia();
		encyclopedia.load(dstream);
		reCalc();
	}


	public void Load(String filename) throws IOException {
		File file=new File("saves/"+filename+"/"+"player.sav");
		if (file.exists()==false)
		{
			file.createNewFile();
		}
		
		FileInputStream fstream=new FileInputStream(file);
		DataInputStream dstream=new DataInputStream(fstream);
		Load(dstream);	
		actorDescription="This, is you.";
	
	}

	
	@Override
	public boolean getAttackable() {

		return RPGHandler.getAttackable();
	}

	@Override
	public int getAttackIndex() {

		return 0;
	}

	public void levelUp(Perk perk) {

		Player_RPG rpg=(Player_RPG)actorRPG;
		rpg.levelUp(perk);
		rpg.genMoveList();
	
	}



	@Override
	public String getSpriteName() {
		return "player.png";
	}

	@Override
	public int applyEffect(Effect effect, Actor origin, boolean critical) {
		
		int value=effect.applyEffect(origin, this, false);
		if (Effect_Status.class.isInstance(effect))
		{
			calcMove();
			
		}
	
		return value;
	}

	public void setMove(int index)
	{
		Player_RPG rpg=(Player_RPG)actorRPG;
		rpg.setMove(index);
	}

	public CombatMove getMove(int index)
	{
		if (index<((Player_RPG)actorRPG).getNumMoves())
		{
			return ((Player_RPG)actorRPG).getCombatMove(index);		
		}
		return null;
	}
	
	public int getMoveCount()
	{
		return ((Player_RPG)actorRPG).getNumMoves();
	}

	@Override
	public void Interact(Player player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Defeat(Actor victor, boolean resolve) {
		if (NPC.class.isInstance(victor))
		{
			ViewScene.m_interface.PlayerBeaten((NPC)victor, resolve);		
		}
	}

	@Override
	public boolean Respawn(long time) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getAttribute(int defAttribute) {
		
		if (defAttribute>=0)
		{
			return actorRPG.getAttribute(defAttribute);
		}
		return 0;
		
	}

	public int getSpecialMove() {
		
		return ((Player_RPG)actorRPG).getMoveChoice();
	}

	public boolean useMove(int number, Attackable attackable) {
		// TODO Auto-generated method stub
		
		CombatMove move=((Player_RPG)actorRPG).getCombatMove(number);
		if (((Player_RPG)actorRPG).getCooldownHandler().moveIsUnusable(move.getMoveName()))
		{
			ViewScene.m_interface.DrawText("move "+move.getMoveName()+ " isn't usable right now");
			return false;
		}
		//check energy
		ItemDepletableInstance energy=null;
		int slot=move.getEnergySource();
		if (move.getAmmoCost()>0)
		{
			if (slot!=-2)
			{	
				actorRPG.setStat(Actor_RPG.SATIATION, actorRPG.getStat(Actor_RPG.SATIATION)-move.getAmmoCost());
			}
			if (slot!=-1)
			{			
				if (playerInventory.getSlot(slot)!=null && ItemDepletableInstance.class.isInstance(playerInventory.getSlot(slot)))
				{
					energy=(ItemDepletableInstance)playerInventory.getSlot(slot);
					if (energy.getEnergy()<move.getAmmoCost())
					{
						ViewScene.m_interface.DrawText("insufficent energy to use "+move.getMoveName());
						return false;
					}
				}
			}
		}
		//use move
		boolean b=move.useMove(this, attackable);
		//remove energy		
		if (b==true)
		{
			((Player_RPG)actorRPG).getCooldownHandler().useMove(move.getMoveName());
			if (slot==-1)
			{
				if (ItemStack.class.isInstance((playerInventory).getSlot(0)))
				{
					ItemStack stack=(ItemStack)playerInventory.getSlot(0);
					stack.setCount(stack.getCount()-move.getAmmoCost());
					
					if (stack.getCount()==0)
					{
						UnEquip(0);
						ViewScene.m_interface.UpdateInfo();	
					}
					else
					{
						playerInventory.setWeight(playerInventory.getWeight()-stack.getItem().getWeight());
					}
					ViewScene.m_interface.UpdateInfo();
				}
			}
			if (energy!=null)
			{
				energy.UseEnergy(move.getAmmoCost());
				if (move.isThrowWeapon() && energy.getEnergy()<=0)
				{		
					UnEquip(0);
					ViewScene.m_interface.UpdateInfo();
				}
			}
			else
			{
				if (move.isThrowWeapon())
				{
					ThrownWeaponHandler.throwWeapon(attackable.getPosition(), playerInventory.getSlot(0));
					UnEquip(0);
					ViewScene.m_interface.UpdateInfo();
				}
			}
			actorRPG.addBusy(move.getTimeCost());
		}	
		return b;
	}

	@Override
	public boolean canSave() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBlocking() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	void checkSpawnable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	boolean visible(Tile tile) {

		return true;
	}

	public Encyclopedia getEncyclopedia() {
		return encyclopedia;
	}

	
}
