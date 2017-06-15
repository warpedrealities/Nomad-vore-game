package solarview.spaceEncounter.EncounterEntities;

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
	
	public EncounterShip(Spaceship ship, Vec2f position)
	{
		this.ship=ship;
		if (this.ship.getShipController()!=null)
		{
			this.controller=this.ship.getShipController().getCombat();			
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
	
	
	
}
