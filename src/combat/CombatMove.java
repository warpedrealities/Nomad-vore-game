package combat;

import java.util.ArrayList;

import nomad.Universe;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import combat.effect.Effect;
import combat.effect.Effect_Damage;
import combat.effect.Effect_Dismantle;
import combat.effect.Effect_Movement;
import combat.effect.Effect_Status;
import combat.effect.Effect_Submit;
import combat.effect.analyze.Effect_Analyze;
import combat.effect.map.Effect_Map;
import combat.statusEffects.StatusEffect;
import combat.effect.Effect_Recover;
import combat.effect.Effect_Reinforce;
import combat.effect.Effect_Scan;
import combat.effect.Effect_Spawn;
import shared.Vec2f;
import view.ViewScene;
import view.ZoneInteractionHandler;
import vmo.Game;
import vmo.GameManager;
import zone.Tile;
import actor.Actor;
import actor.Attackable;
import actor.player.Player;
import actorRPG.RPG_Helper;

public class CombatMove {

	public enum AttackPattern {
		P_MELEE(0), P_SWEEP(1), P_CIRCLE(2), P_SHORT(3), P_RANGED(4), P_CONE(5), P_BOMB(6), P_REACH(7), P_SELF(-1);

		int value;

		AttackPattern(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

	}

	public enum MoveType {
		FIGHT(0), DOMINATE(1), MOVEMENT(2), OTHER(3);
		int value;

		MoveType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	};

	public enum ItemThrow {
		T_KEEP, T_THROW, T_ONCE
	};

	private String moveName,overrideCooldown;
	private int ammoCost, timeCost, moveCooldown, attackBonus, bonusAttribute, rangedBias, icon;
	private AttackPattern attackPattern;
	private MoveType moveType;
	private ArrayList<Effect> effects;
	private String[] hitText;
	private String[] missText;
	private boolean nonViolent, toggleAbility;
	private boolean throwWeapon;
	private int energySource = 0;
	private int actionCost;
	private boolean basicAction;
	private boolean threatening;
	
	public CombatMove(Effect effect, AttackPattern pattern, String moveName, String hitText, String missText,
			int attackBonus, int bonusAttribute) {
		effects = new ArrayList<Effect>();
		effects.add(effect);
		this.moveName = moveName;
		this.hitText = new String[1];
		this.hitText[0] = hitText;
		this.missText = new String[1];
		this.missText[0] = missText;
		this.attackBonus = attackBonus;
		this.bonusAttribute = bonusAttribute;
		this.attackPattern = pattern;
	}

	public CombatMove(Element Enode) {

		// name
		moveName = Enode.getAttribute("name");
		if (Enode.getAttribute("cooldownName").length()>0)
		{
			overrideCooldown=Enode.getAttribute("cooldownName");
		}
		// bonus			
		if (Enode.getAttribute("bonusToHit").length() > 0) {
			attackBonus = Integer.parseInt(Enode.getAttribute("bonusToHit"));
		}
		if (Enode.getAttribute("moveType").length() > 0) {
			moveType = strToType(Enode.getAttribute("moveType"));
		}
		if (Enode.getAttribute("basicAction").equals("true")) {
			basicAction = true;
		}
		// action cost
		if (Enode.getAttribute("actionCost").length() > 0) {
			actionCost = Integer.parseInt(Enode.getAttribute("actionCost"));
		}
		// ammocost
		if (Enode.getAttribute("ammoCost").length() > 0) {
			ammoCost = Integer.parseInt(Enode.getAttribute("ammoCost"));
		}
		// timecost
		if (Enode.getAttribute("timeCost").length() > 0) {
			timeCost = Integer.parseInt(Enode.getAttribute("timeCost"));
		}
		// movecooldown
		if (Enode.getAttribute("cooldown").length() > 0) {
			moveCooldown = Integer.parseInt(Enode.getAttribute("cooldown"));
		}
		if (Enode.getAttribute("icon").length() > 0) {
			icon = Integer.parseInt(Enode.getAttribute("icon"));
		}
		if (Enode.getAttribute("rangedbias").length() > 0) {
			rangedBias = Integer.parseInt(Enode.getAttribute("rangedbias"));
		}
		if (Enode.getAttribute("toggledability").length() > 0) {
			toggleAbility = true;
		}
		if (Enode.getAttribute("throw").equals("true")) {
			throwWeapon = true;
		}
		// attack pattern
		if (Enode.getAttribute("pattern").length() > 0) {
			attackPattern = strToPattern(Enode.getAttribute("pattern"));
		}
		if (Enode.getAttribute("bonusAttribute").length() > 0) {
			bonusAttribute = RPG_Helper.AttributefromString(Enode.getAttribute("bonusAttribute"));
		}
		
		if ("true".equals(Enode.getAttribute("threatening")))
		{
			threatening=true;
		}

		// load effects
		effects = new ArrayList<Effect>();
		NodeList list = Enode.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) 
		{
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) 
			{
				Element e = (Element) list.item(i);
				if (e.getTagName().equals("effectDamage")) {
					Effect_Damage ed = new Effect_Damage(e);
					effects.add(ed);
				}
				if (e.getTagName().equals("effectrecover")) {
					Effect_Recover er = new Effect_Recover(e);
					effects.add(er);

				}
				if (e.getTagName().equals("effectStatus")) {
					effects.add(new Effect_Status(e));
				}

				if (e.getTagName().equals("effectMovement")) {
					effects.add(new Effect_Movement(e));
				}
				if (e.getTagName().equals("effectSpawn")) {
					effects.add(new Effect_Spawn(e));
				}
				if (e.getTagName().equals("effectReinforce")) {
					effects.add(new Effect_Reinforce(e));
				}
				if (e.getTagName().equals("effectDismantle")) {
					effects.add(new Effect_Dismantle());
				}
				if (e.getTagName().equals("effectScan")) {
					effects.add(new Effect_Scan(e));
				}
				if (e.getTagName().equals("effectSubmit")) {
					effects.add(new Effect_Submit(e));
				}
				if (e.getTagName().equals("effectAnalyze")) {
					effects.add(new Effect_Analyze(e));
				}
				if (e.getTagName().equals("effectMap")) {
					effects.add(new Effect_Map(e));
				}
				if (e.getTagName().equals("missText")) {
					genMiss(e);
				}
				if (e.getTagName().equals("hitText")) {
					genHit(e);
				}
				if (e.getTagName().equals("energySource")) {
					String value = e.getAttribute("value");
					if (value.equals("hand")) {
						energySource = 0;
					}
					if (value.equals("accessory")) {
						energySource = 1;
					}
					if (value.equals("body")) {
						energySource = 2;
					}
					if (value.equals("head")) {
						energySource = 3;
					}
					if (value.equals("stack")) {
						energySource = -1;
					}
					if (value.equals("satiation")) {
						energySource = -2;
					}
				}
			}
			
		}
		for (int i=0;i<effects.size();i++)
		{
			if (effects.get(i).harmless())
			{
				nonViolent=true;
			}
		}
	}

	public int getActionCost() {
		return actionCost;
	}

	public void setActionCost(int actionCost) {
		this.actionCost = actionCost;
	}

	public boolean isBasicAction() {
		return basicAction;
	}

	public void setBasicAction(boolean basicAction) {
		this.basicAction = basicAction;
	}

	public CombatMove() {

		effects = new ArrayList<Effect>();
	}

	public CombatMove clone() {
		CombatMove move = new CombatMove();
		move.moveName = moveName;
		move.ammoCost = ammoCost;
		move.timeCost = timeCost;
		move.actionCost = actionCost;
		move.moveCooldown = moveCooldown;
		move.attackBonus = attackBonus;
		move.bonusAttribute = bonusAttribute;
		move.rangedBias = rangedBias;
		move.icon = icon;
		move.attackPattern = attackPattern;
		move.moveType = moveType;

		for (int i = 0; i < effects.size(); i++) {
			move.effects.add(effects.get(i).clone());
		}
		move.hitText = hitText;
		move.missText = missText;
		move.nonViolent = nonViolent;
		move.toggleAbility = toggleAbility;
		move.throwWeapon = throwWeapon;
		move.energySource = energySource;
		move.basicAction = basicAction;
		return move;
	}

	private void genHit(Element element) {
		hitText = new String[Integer.parseInt(element.getAttribute("count"))];
		NodeList list = element.getChildNodes();
		int index = 0;
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element enode = (Element) list.item(i);
				hitText[index] = enode.getTextContent();
				index++;
			}
		}
	}

	private void genMiss(Element element) {
		missText = new String[Integer.parseInt(element.getAttribute("count"))];
		int index = 0;
		NodeList list = element.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element enode = (Element) list.item(i);
				missText[index] = enode.getTextContent();
				index++;
			}
		}
	}

	static public MoveType strToType(String string) {
		if (string.equals("DOMINATE")) {
			return MoveType.DOMINATE;
		}
		if (string.equals("MOVEMENT")) {
			return MoveType.MOVEMENT;
		}
		if (string.equals("OTHER")) {
			return MoveType.OTHER;
		}
		return MoveType.FIGHT;
	}

	static public AttackPattern strToPattern(String string) {
		if (string.equals("MELEE")) {
			return AttackPattern.P_MELEE;
		}
		if (string.equals("SWEEP")) {
			return AttackPattern.P_SWEEP;
		}
		if (string.equals("CIRCLE")) {
			return AttackPattern.P_CIRCLE;
		}
		if (string.equals("CONE")) {
			return AttackPattern.P_CONE;
		}
		if (string.equals("RANGED")) {
			return AttackPattern.P_RANGED;
		}
		if (string.equals("SHORT")) {
			return AttackPattern.P_SHORT;
		}
		if (string.equals("SELF")) {
			return AttackPattern.P_SELF;
		}
		if (string.equals("BOMB")) {
			return AttackPattern.P_BOMB;			
		}
		if (string.equals("REACH")) {
			return AttackPattern.P_REACH;
		}
		return AttackPattern.P_MELEE;
	}

	public String getMoveName() {
		return moveName;
	}

	public int getAmmoCost() {
		return ammoCost;
	}

	public int getTimeCost() {
		return timeCost;
	}

	public int getMoveCooldown() {
		return moveCooldown;
	}

	public int getAttackBonus() {
		return attackBonus;
	}

	public AttackPattern getAttackPattern() {
		return attackPattern;
	}

	public int getEnergySource() {
		return energySource;
	}

	private boolean getVisible(Vec2f p) {
		Tile t = Universe.getInstance().getCurrentZone().getTile((int) p.x, (int) p.y);

		if (t != null && t.getVisible()) {
			return true;
		}
		return false;
	}

	public boolean useNormalMove(Actor origin, Attackable target) {
		// check los
		if (GameManager.m_los.existsLineOfSight(Universe.getInstance().getCurrentZone(), (int) origin.getPosition().x,
				(int) origin.getPosition().y, (int) target.getPosition().x, (int) target.getPosition().y, false)) {
			// check range
			float distance = origin.getPosition().getDistance(target.getPosition());
			if (distance > 9) {
				return false;
			}
			if (attackPattern == attackPattern.P_SHORT && distance >= 4) {
				return false;
			}
			if (attackPattern == attackPattern.P_REACH && distance >= 3) {
				return false;
			}
			if ((attackPattern == attackPattern.P_MELEE || attackPattern == attackPattern.P_CIRCLE
					|| attackPattern == attackPattern.P_SWEEP) && distance >= 2) {
				return false;
			}
			int defAttribute = CombatLookup.getDefenceForAttack(bonusAttribute);
			// get defence
			int def = CombatLookup.getBaseDefence(distance, defAttribute) + target.getAttribute(defAttribute);
			// get attack bonus
			int bonus = attackBonus + origin.getRPG().getAttribute(bonusAttribute);
			if (distance >= 2 && attackPattern != AttackPattern.P_CONE && moveType!=MoveType.MOVEMENT) {
				ViewScene.m_interface.projectile(new Vec2f(target.getPosition().x, target.getPosition().y),
						new Vec2f(origin.getPosition().x, origin.getPosition().y), 0);
				def -= rangedBias;
			} else {
				def += rangedBias;
			}
			// roll attack
			int r = GameManager.m_random.nextInt(20) + bonus;
			if (attackPattern == AttackPattern.P_SELF) {
				r = 999;
			}
			if (attackPattern == AttackPattern.P_SWEEP) {
				return CombatAura.doSweep(this, origin, target);
			}
			if (attackPattern == AttackPattern.P_CIRCLE) {
				return CombatAura.doCircle(this, origin, target);
			}
			if (attackPattern == AttackPattern.P_BOMB) {
				return CombatAura.doExplosion(this, origin, target, true);
			}
			if (attackPattern == AttackPattern.P_CONE) {
				return CombatAura.doCone(this, origin, target);
			}

			if (def <= r) {
				if (Game.sceneManager.getConfig().isVerboseCombat() && r < 100 && origin.getVisible()) {
					ViewScene.m_interface.DrawText(origin.getName() + " attacks " + target.getName() + " " + (r - bonus)
							+ "+" + bonus + " DC:" + def + "=hit");
				}
				int value = 0;
				// apply effect to target
				boolean critical = false;
				boolean visible = getVisible(target.getPosition());
				if (r > def + 10 && r < 100 && Player.class.isInstance(origin) && Actor.class.isInstance(target)) {
					critical = true;
				}
				for (int i = 0; i < effects.size(); i++) {
					value += target.applyEffect(effects.get(i), origin, critical);
				}
				// show hit text
				origin.getThreat().addThreat(value);
				if (target.getAttackable() && visible) {
					String text = hitText[GameManager.m_random.nextInt(hitText.length)];
					if (text.length() > 0) {
						String str = text.replace("TARGET", target.getName()).replace("VALUE", Integer.toString(value));
						if (critical) {
							ViewScene.m_interface.DrawText(str + " (CRITICAL!)");
						} else {
							if (ViewScene.m_interface!=null)
							{
								ViewScene.m_interface.DrawText(str);
							}
				
						}
					}
				}

				// show impact sigil
				if (visible) {

					if (attackPattern == AttackPattern.P_MELEE || attackPattern == AttackPattern.P_RANGED
							|| attackPattern == AttackPattern.P_SHORT||attackPattern == AttackPattern.P_REACH) {
						if (isNonViolent()) {
							ViewScene.m_interface.Flash(target.getPosition(), 1);
						} else {
							if (ViewScene.m_interface!=null)
							{
								ViewScene.m_interface.Flash(target.getPosition(), 0);			
							}
				
						}

					}
				}

				return true;
			} else {
				if (Game.sceneManager.getConfig().isVerboseCombat() && r < 100 && origin.getVisible()) {
					ViewScene.m_interface.DrawText(origin.getName() + " attacks " + target.getName() + " " + (r - bonus)
							+ "+" + bonus + " DC:" + def + "=miss");
				}
				// show miss text
				String text = missText[GameManager.m_random.nextInt(missText.length)];
				String str = text.replace("TARGET", target.getName());
				ViewScene.m_interface.DrawText(str);
				return true;
			}
		}
		return false;
	}

	private boolean checkToggleOff(Actor target) {
		for (int i = 0; i < effects.size(); i++) {
			if (Effect_Status.class.isInstance(effects.get(i))) {
				Effect_Status effect = (Effect_Status) effects.get(i);
				if (target.getRPG().hasStatus(effect.getEffects().get(0).getUID())) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean useToggledMove(Actor origin, Attackable target) {
		if (checkToggleOff(origin)) {
			for (int i = 0; i < effects.size(); i++) {
				if (Effect_Status.class.isInstance(effects.get(i))) {
					Effect_Status effect = (Effect_Status) effects.get(i);
					if (origin.getRPG().hasStatus(effect.getEffects().get(0).getUID())) {
						origin.getRPG().removeStatus(effect.getEffects().get(0).getUID());
					}
				}

				if (Player.class.isInstance(origin)) {
					Player player = (Player) origin;
					player.calcMove();
				}
				return true;
			}
		} else {
			for (int i = 0; i < effects.size(); i++) {
				if (Effect_Status.class.isInstance(effects.get(i))) {
					Effect_Status effect = (Effect_Status) effects.get(i);
					effect.applyEffect(origin, origin, false);
				}
				if (Player.class.isInstance(origin)) {
					Player player = (Player) origin;
					player.calcMove();
				}

				return true;
			}
		}
		return false;
	}

	public boolean useMove(Actor origin, Attackable target) {
		if (toggleAbility == true) {
			return useToggledMove(origin, target);
		} else {
			return useNormalMove(origin, target);
		}

	}

	public boolean isNonViolent() {

		return nonViolent;

	}

	public boolean isToggleAbility() {
		return toggleAbility;
	}

	public int getIcon() {
		return icon;
	}

	public boolean isThrowWeapon() {
		return throwWeapon;
	}

	public int getBonusAttribute() {
		return bonusAttribute;
	}

	public ArrayList<Effect> getEffects() {
		return effects;
	}

	public String[] getHitText() {
		return hitText;
	}

	public String[] getMissText() {
		return missText;
	}

	public void setAttackBonus(int attackBonus) {
		this.attackBonus = attackBonus;
	}

	public void setAttackPattern(AttackPattern attackPattern) {
		if (attackPattern == null) {
			return;
		}
		if (this.attackPattern.getValue() < attackPattern.getValue()) {
			this.attackPattern = attackPattern;
		}

	}

	public void setTimeCost(int timeCost) {
		this.timeCost = timeCost;
	}

	public void setName(String moveName2) {
		this.moveName = moveName2;

	}

	public MoveType getMoveType() {
		return moveType;
	}

	public boolean isThreatening() {
		return threatening;
	}

	public String getOverrideCooldown() {
		// TODO Auto-generated method stub
		return null;
	}

}
