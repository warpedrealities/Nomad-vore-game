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

public class EncounterShip {

	private Spaceship ship;
	private CombatController controller;
	private Vec2f position;
	private float heading;
	private Square_Rotatable_Int sprite;
	private ShipEmitters emitters;
	private CombatShield shield;
	private List<CombatWeapon> weapons;
	private char course;
	
	public final char stop=0;
	public final char left=1;
	public final char right=2;
	public final char half=4;
	public final char full=8;
	
	public EncounterShip(Spaceship ship, Vec2f position)
	{
		this.ship=ship;
		if (ship.getShipStats().getShield()!=null)
		{
			shield=new CombatShield(ship,ship.getShipStats().getShield());
		}
		if (this.ship.getShipController()!=null)
		{
			this.controller=this.ship.getShipController().getCombat();			
		}
		if (this.ship.getShipStats().getWeapons()!=null &&
			this.ship.getShipStats().getWeapons().size()>0)
		{
			weapons=new ArrayList<CombatWeapon>();
			for (int i=0;i<this.getShip().getShipStats().getWeapons().size();i++)
			{
				weapons.add(new CombatWeapon(this.getShip().getShipStats().getWeapons().get(i)));
			}
		}
		buildEmitters();
		this.position=new Vec2f(position.x,position.y);
	}

	private void buildEmitters()
	{
		Document doc=ParserHelper.LoadXML("assets/data/ships/"+ship.getName()+".xml");
		
		//read through the top level nodes
		Element root=doc.getDocumentElement();
	    Element n=(Element)doc.getFirstChild();
		NodeList children=n.getElementsByTagName("emitters");
		emitters=new ShipEmitters((Element)children.item(0));
		
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
		return position;
	}

	public float getHeading() {
		return heading;
	}

	public CombatShield getShield() {
		return shield;
	}

	public char getCourse() {
		return course;
	}

	public void setCourse(char course) {
		this.course = course;
	}

	public List<CombatWeapon> getWeapons() {
		return weapons;
	}
	
	
	
}
