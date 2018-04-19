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
import nomad.universe.Universe;
import actorRPG.Actor_RPG;
import actorRPG.RPGActionHandler;

import rendering.Square_Int;
import shared.Geometry;
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

	protected int getMoveCost() {
		if (actorRPG.getStarving()) {
			return moveCost * 2;
		}
		return (int) (moveCost*collisionInterface.getMovementMultiplier());
	}

	private boolean swapPlaces(int x0, int y0, int x1, int y1)
	{
		Actor actor=collisionInterface.getActor(x1, y1);
		if (actor!=null && !actor.getAttackable())
		{
			actor.setPosition(new Vec2f(x0,y0));
			actorPosition.x=x1;
			actorPosition.y=y1;
			if (spriteInterface!=null)
			{
				spriteInterface.reposition(actorPosition);
			}	
			Tile t=collisionInterface.getTile((int)actorPosition.x, (int)actorPosition.y);
			if (t!=null)
			{
				t.setActorInTile(this);
			}
			
			actorRPG.addBusy(4);
			return true;
		}
		return false;
	}
	
	public boolean move(int direction) {
		if (actorRPG.getBindState() > -1) {
			actorRPG.struggle();

			return true;
		}
		Vec2f p=Geometry.getPos(direction, actorPosition);
		if (collisionInterface.passable((int) p.x, (int) p.y, getFlying())) {
			Tile t=collisionInterface.getTile((int) actorPosition.x, (int) actorPosition.y);	
			if (!swapPlaces((int)actorPosition.x,(int)actorPosition.y,(int)p.x,(int)p.y))
			{
				t.setActorInTile(null);		
				actorPosition=p;
				
				spriteInterface.reposition(actorPosition);
				if (t.getDefinition().getMovement()==TileMovement.SLOW && !getFlying())
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
			else
			{
				spriteInterface.reposition(actorPosition);
				if (t.getDefinition().getMovement()==TileMovement.SLOW)
				{
					actorRPG.addBusy(getMoveCost()*4);		
				}
				else
				{
					actorRPG.addBusy(getMoveCost());			
				}
				return true;
			}
			
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
