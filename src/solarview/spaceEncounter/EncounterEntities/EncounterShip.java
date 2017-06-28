package solarview.spaceEncounter.EncounterEntities;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import rendering.Square_Rotatable_Int;
import shared.ParserHelper;
import shared.Vec2f;
import solarview.spaceEncounter.CombatController;
import spaceship.Spaceship;
import particlesystem.ParticleConeEmitter;

public class EncounterShip {

	private Spaceship ship;
	private CombatController controller;

	private CombatManouver manouver;
	private Square_Rotatable_Int sprite;
	private ShipEmitters emitters;
	private CombatShield shield;
	private List<CombatWeapon> weapons;
	private List<CombatAction> actions;
	
	public EncounterShip(Spaceship ship, Vec2f position, int heading) {
		this.ship = ship;
		if (ship.getShipStats().getShield() != null) {
			shield = new CombatShield(ship, ship.getShipStats().getShield());
		}
		if (this.ship.getShipController() != null) {
			this.controller = this.ship.getShipController().getCombat();
		}
		if (this.ship.getShipStats().getWeapons() != null && this.ship.getShipStats().getWeapons().size() > 0) {
			weapons = new ArrayList<CombatWeapon>();
			for (int i = 0; i < this.getShip().getShipStats().getWeapons().size(); i++) {
				weapons.add(new CombatWeapon(this.getShip().getShipStats().getWeapons().get(i)));
			}
		}
		buildEmitters();
		manouver = new CombatManouver(this, position, heading);
		
		actions=new ArrayList<CombatAction>();

	}

	private void buildEmitters() {
		Document doc = ParserHelper.LoadXML("assets/data/ships/" + ship.getName() + ".xml");

		// read through the top level nodes
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		NodeList children = n.getElementsByTagName("emitters");
		emitters = new ShipEmitters((Element) children.item(0));

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

	public Vec2f getPosition() {
		return manouver.getPosition();
	}

	public float getHeading() {
		return manouver.getHeading();
	}

	public CombatShield getShield() {
		return shield;
	}

	public int getCourse() {
		return manouver.getCourse();
	}

	public void setCourse(char course) {
		manouver.setCourse(course);
	}

	public List<CombatWeapon> getWeapons() {
		return weapons;
	}

	public void update(float dt) {

		manouver.update(dt);
	}

	public void updateResources() {
		ship.getShipStats().run();
		if (shield != null) {
			shield.update();
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

	public List<CombatAction> getActions() {
		return actions;
	}


}
