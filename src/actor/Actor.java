package actor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector4f;

import actor.player.Player;
import combat.CombatMove;
import combat.statusEffects.StatusEffect;
import combat.statusEffects.StatusFaction;
import faction.Faction;

import nomad.Universe;

import actorRPG.Actor_RPG;
import actorRPG.RPGActionHandler;

import rendering.Square_Int;
import shared.Vec2f;
import view.ViewScene;
import view.ModelController_Int;
import vmo.GameManager;
import widgets.WidgetItemPile;
import zone.Tile;
import zone.Zone_int;
import zone.TileDef.TileMovement;

public abstract class Actor implements Attackable {

	protected Vec2f actorPosition;
	protected String actorName;
	protected boolean actorVisibility;

	protected Zone_int collisionInterface;
	protected Square_Int spriteInterface;
	protected Actor_RPG actorRPG;
	protected RPGActionHandler RPGHandler;
	protected String actorDescription;
	protected Faction actorFaction;

	protected int moveCost = 2;
	protected boolean isFlying;
	protected ThreatAssessment threatAssessment;

	public RPGActionHandler getRPGHandler() {
		return RPGHandler;
	}

	public Vec2f getPosition() {
		return actorPosition;
	}

	public String getDescription() {
		return actorDescription;
	}

	public boolean getPeace() {
		return false;
	}

	public void setPosition(Vec2f position) {
		if (collisionInterface!=null)
		{
			Tile t=collisionInterface.getTile((int)actorPosition.x, (int)actorPosition.y);
			if (t!=null)
			{
				t.setActorInTile(null);
			}
			t=collisionInterface.getTile((int)position.x, (int)position.y);
			if (t!=null)
			{
				t.setActorInTile(this);
			}
		

		}
		actorPosition = position;	
		if (spriteInterface != null) {
			spriteInterface.reposition(actorPosition);
		}

	}

	public void setSpriteInterface(Square_Int sinterface) {
		spriteInterface = sinterface;
		if (actorRPG.getStat(Actor_RPG.HEALTH) <= 0) {
			spriteInterface.setImage(2);
		}
		if (actorRPG.getStat(Actor_RPG.RESOLVE) <= 0) {
			spriteInterface.setImage(3);
		}
	}

	public void setCollisioninterface(Zone_int zinterface) {
		collisionInterface = zinterface;
		if (collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y) != null) {
			collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y).setActorInTile(this);
		}
	}

	public boolean getVisible() {
		return actorVisibility;
	}

	abstract public int getAttackIndex();

	public boolean isHostile(String faction) {

		int factionRelation = getActorFaction().getRelationship(faction);
		if (factionRelation < 50) {
			if (getPeace()) {
				return false;
			}
			return true;
		}

		return false;
	}

	public boolean getFlying() {
		return isFlying;
	}

	public void Update() {
		if (RPGHandler.getActive() == true) {
			Tile tile = collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y);
			if (tile != null) {
				actorVisibility = visible(tile);
				spriteInterface.setVisible(actorVisibility);
			}
		}
	}

	abstract protected boolean visible(Tile tile);

	public Square_Int getSpriteInterface() {
		return spriteInterface;
	}

	boolean Move0() {
		if (collisionInterface.passable((int) actorPosition.x, (int) actorPosition.y + 1, getFlying())) {
			Tile t=collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y);
			t.setActorInTile(null);
			actorPosition.y += 1;
			spriteInterface.reposition(actorPosition);
			if (t.getDefinition().getMovement()==TileMovement.SLOW)
			{
				actorRPG.addBusy(getMoveCost()*4);		
			}
			else
			{
				actorRPG.addBusy(getMoveCost());			
			}
			collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y).setActorInTile(this);
			return true;
		}
		return false;
	}

	boolean Move1() {
		if (collisionInterface.passable((int) actorPosition.x + 1, (int) actorPosition.y + 1, getFlying())) {
			Tile t=collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y);
			t.setActorInTile(null);
			actorPosition.y += 1;
			actorPosition.x += 1;
			spriteInterface.reposition(actorPosition);
			if (t.getDefinition().getMovement()==TileMovement.SLOW)
			{
				actorRPG.addBusy(getMoveCost()*4);		
			}
			else
			{
				actorRPG.addBusy(getMoveCost());			
			}
			collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y).setActorInTile(this);
			return true;
		}
		return false;
	}

	boolean Move2() {
		if (collisionInterface.passable((int) actorPosition.x + 1, (int) actorPosition.y, getFlying())) {
			Tile t=collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y);
			t.setActorInTile(null);
			actorPosition.x += 1;
			spriteInterface.reposition(actorPosition);
			if (t.getDefinition().getMovement()==TileMovement.SLOW)
			{
				actorRPG.addBusy(getMoveCost()*4);		
			}
			else
			{
				actorRPG.addBusy(getMoveCost());			
			}
			collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y).setActorInTile(this);
			return true;
		}
		return false;
	}

	boolean Move3() {
		if (collisionInterface.passable((int) actorPosition.x + 1, (int) actorPosition.y - 1, getFlying())) {
			Tile t=collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y);
			t.setActorInTile(null);
			actorPosition.y -= 1;
			actorPosition.x += 1;
			spriteInterface.reposition(actorPosition);
			if (t.getDefinition().getMovement()==TileMovement.SLOW)
			{
				actorRPG.addBusy(getMoveCost()*4);		
			}
			else
			{
				actorRPG.addBusy(getMoveCost());			
			}
			collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y).setActorInTile(this);
			return true;
		}
		return false;
	}

	boolean Move4() {
		if (collisionInterface.passable((int) actorPosition.x, (int) actorPosition.y - 1, getFlying())) {
			Tile t=collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y);
			t.setActorInTile(null);
			actorPosition.y -= 1;
			spriteInterface.reposition(actorPosition);
			if (t.getDefinition().getMovement()==TileMovement.SLOW)
			{
				actorRPG.addBusy(getMoveCost()*4);		
			}
			else
			{
				actorRPG.addBusy(getMoveCost());			
			}
			collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y).setActorInTile(this);
			return true;
		}
		return false;
	}

	boolean Move5() {
		if (collisionInterface.passable((int) actorPosition.x - 1, (int) actorPosition.y - 1, getFlying())) {
			Tile t=collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y);
			t.setActorInTile(null);
			actorPosition.y -= 1;
			actorPosition.x -= 1;
			spriteInterface.reposition(actorPosition);
			if (t.getDefinition().getMovement()==TileMovement.SLOW)
			{
				actorRPG.addBusy(getMoveCost()*4);		
			}
			else
			{
				actorRPG.addBusy(getMoveCost());			
			}
			collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y).setActorInTile(this);
			return true;
		}
		return false;
	}

	boolean Move6() {
		if (collisionInterface.passable((int) actorPosition.x - 1, (int) actorPosition.y, getFlying())) {
			Tile t=collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y);
			t.setActorInTile(null);
			actorPosition.x -= 1;
			spriteInterface.reposition(actorPosition);
			if (t.getDefinition().getMovement()==TileMovement.SLOW)
			{
				actorRPG.addBusy(getMoveCost()*4);		
			}
			else
			{
				actorRPG.addBusy(getMoveCost());			
			}
			collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y).setActorInTile(this);
			return true;
		}
		return false;
	}

	boolean Move7() {
		if (collisionInterface.passable((int) actorPosition.x - 1, (int) actorPosition.y + 1, getFlying())) {
			Tile t=collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y);
			t.setActorInTile(null);
			actorPosition.y += 1;
			actorPosition.x -= 1;
			spriteInterface.reposition(actorPosition);
			if (t.getDefinition().getMovement()==TileMovement.SLOW)
			{
				actorRPG.addBusy(getMoveCost()*4);		
			}
			else
			{
				actorRPG.addBusy(getMoveCost());			
			}
			collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y).setActorInTile(this);
			return true;
		}
		return false;
	}

	protected int getMoveCost() {
		if (actorRPG.getStarving()) {
			return moveCost * 2;
		}
		return moveCost;
	}

	public boolean move(int direction) {
		if (actorRPG.getBindState() > -1) {
			actorRPG.struggle();

			return true;
		}
		switch (direction) {
		case 0:
			return Move0();
		case 1:
			return Move1();
		case 2:
			return Move2();
		case 3:
			return Move3();
		case 4:
			return Move4();
		case 5:
			return Move5();
		case 6:
			return Move6();
		case 7:
			return Move7();

		}

		return false;
	}

	abstract public void Interact(Player player);

	abstract public CombatMove getCombatMove();

	abstract public void Defeat(Actor victor, boolean resolve);

	public Actor_RPG getRPG() {
		return actorRPG;
	}

	public String getName() {

		return actorName;
	}

	abstract public boolean Respawn(long time);

	public void addBusy(int value) {
		actorRPG.addBusy(value);

	}

	public void setBusy(int value) {
		actorRPG.setBusy(value);
	}

	abstract public String getSpriteName();

	abstract public void Save(DataOutputStream dstream) throws IOException;

	abstract public void Load(DataInputStream dstream) throws IOException;

	public Faction getActorFaction() {
		int fstate=actorRPG.getStatusEffectHandler().getFactionState();
		if (fstate!=-1)
		{
			return ((StatusFaction)actorRPG.getStatusEffectHandler().getStatusEffects().get(fstate)).getFaction();
		}
		return actorFaction;
	}

	public void setActorFaction(Faction actorFaction) {
		this.actorFaction = actorFaction;
	}

	public int getUID() {
		return -1;
	}

	abstract public boolean canSave();

	abstract public boolean isBlocking();

	abstract protected void checkSpawnable();

	public boolean isActorVisibility() {
		return actorVisibility;
	}

	public void setActorVisibility(boolean actorVisibility) {
		this.actorVisibility = actorVisibility;
	}

	public ThreatAssessment getThreat()
	{
		return threatAssessment;
	}
}
