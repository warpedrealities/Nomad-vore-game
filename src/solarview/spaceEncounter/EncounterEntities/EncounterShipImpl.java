package solarview.spaceEncounter.EncounterEntities;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import rendering.Square_Rotatable_Int;
import shared.ParserHelper;
import shared.Vec2f;
import shipsystem.weapon.ShipWeapon;
import solarview.spaceEncounter.EncounterEntities.combatControllers.CombatController;
import solarview.spaceEncounter.EncounterEntities.monitoring.Monitor;
import solarview.spaceEncounter.EncounterEntities.monitoring.Ship_Monitor;
import solarview.spaceEncounter.effectHandling.EffectHandler;
import solarview.spaceEncounter.effectHandling.EffectHandler_Interface;
import solarview.spaceEncounter.gui.EncounterLog;
import solarview.spaceEncounter.interfaces.CombatAction;
import solarview.spaceEncounter.interfaces.EncounterShip;
import spaceship.Spaceship;

public class EncounterShipImpl implements EncounterShip {

	private Spaceship ship;
	private CombatController controller;

	private CombatManouver manouver;
	private Square_Rotatable_Int sprite;
	private ShipEmitters emitters;
	private CombatShield shield;
	private List<CombatWeaponImpl> weapons;
	private CombatActionHandler actionHandler;
	private Monitor monitor;
	private EncounterLog log;

	public EncounterShipImpl(Spaceship ship, Vec2f position, int heading) {
		this.ship = ship;
		monitor=new Ship_Monitor();
		if (ship.getShipStats().getShield() != null) {
			shield = new CombatShield(ship, ship.getShipStats().getShield());
		}
		if (this.ship.getShipController() != null) {
			this.controller = this.ship.getShipController().getCombat();
		}
		if (this.ship.getShipStats().getWeapons() != null && this.ship.getShipStats().getWeapons().size() > 0) {
			weapons = new ArrayList<CombatWeaponImpl>();
			for (int i = 0; i < this.getShip().getShipStats().getWeapons().size(); i++) {
				weapons.add(new CombatWeaponImpl(this.getShip().getShipStats().getWeapons().get(i)));
			}
		}
		buildEmitters();
		manouver = new CombatManouver(this, position, heading);

		actionHandler = new CombatActionHandler(this);

	}

	private void buildEmitters() {
		Document doc = ParserHelper.LoadXML("assets/data/ships/" + ship.getshipName() + ".xml");

		// read through the top level nodes
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		NodeList children = n.getElementsByTagName("emitters");
		emitters = new ShipEmitters((Element) children.item(0));

	}

	public void setLog(EncounterLog log) {
		this.actionHandler.setLog(log);
		this.log = log;
	}

	public Spaceship getShip() {
		return ship;
	}

	public Square_Rotatable_Int getSprite() {
		return sprite;
	}

	public void setSprite(Square_Rotatable_Int sprite) {
		this.sprite = sprite;
	}

	@Override
	public Vec2f getPosition() {
		return manouver.getPosition();
	}

	@Override
	public float getHeading() {
		return manouver.getHeading();
	}

	@Override
	public CombatShield getShield() {
		return shield;
	}

	public int getCourse() {
		return manouver.getCourse();
	}

	public void setCourse(byte course) {
		manouver.setCourse(course);
	}

	public List<CombatWeaponImpl> getWeapons() {
		return weapons;
	}

	public void update(float dt,EffectHandler handler) {
		manouver.update(dt);
		actionHandler.update(dt, handler);
		emitters.update(manouver.getPosition(),manouver.getHeading());
	}

	public void runAi(EncounterShipImpl [] allShips,EffectHandler effectHandler)
	{
		if (controller!=null)
		{
			controller.run(this,allShips,effectHandler);
		}
	}

	public void updateResources(EffectHandler handler) {
		ship.getShipStats().battleRrun();
		if (shield != null) {
			shield.update(handler,manouver.getPosition());
		}
		if (weapons != null && weapons.size() > 0) {
			for (int i = 0; i < weapons.size(); i++) {
				weapons.get(i).decrementCooldown();
			}
		}
	}

	public ShipEmitters getEmitters() {
		return emitters;
	}

	@Override
	public Vec2f getEmitter(int i)
	{
		return emitters.getOffsetWeaponEmitters().get(i);
	}

	public List<CombatActionImpl> getActions() {
		return actionHandler.getList();
	}

	@Override
	public Vec2f getLeading(float v) {
		return manouver.lead(v);
	}

	@Override
	public void attack(float distance, CombatAction action, EffectHandler_Interface effectHandler) {
		ShipWeapon weapon=action.getWeapon().getWeapon().getWeapon();
		for (int i = 0; i < weapon.getEffects().size(); i++) {
			weapon.getEffects().get(i).apply(distance, this, effectHandler, log);
		}
	}

	public Monitor getMonitor() {
		return monitor;
	}


	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}

	public int getEvasion() {
		if ((manouver.getCourse() & manouver.full) != 0 || (manouver.getCourse() & manouver.half) != 0) {
			if ((manouver.getCourse() & manouver.left) != 0 || (manouver.getCourse() & manouver.right) != 0) {
				return (int) ship.getShipStats().getManouverability() + 2;
			}
			return (int) ship.getShipStats().getManouverability();
		}
		if (ship.getShipStats().getManouverability() < 0) {
			return (int) ship.getShipStats().getManouverability();
		}
		return 0;
	}

}
