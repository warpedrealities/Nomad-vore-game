package spaceship;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import entities.Entity;
import entities.stations.Station;
import nomad.universe.Universe;
import shared.ParserHelper;
import shared.Vec2f;
import spaceship.ShipController.scriptEvents;
import spaceship.npcShips.NpcShipController;
import spaceship.stats.SpaceshipBaseStats;
import spaceship.stats.SpaceshipStats;
import spaceship.stats.WarpHandler;
import widgets.WidgetPortal;
import zone.Landing;
import zone.Zone;
import zone.Zone.zoneType;

public class Spaceship extends Entity {

	private int UID;
	private SpaceshipBaseStats baseStats;

	public enum ShipState {
		SPACE, LAND, DOCK, ADRIFT, SHIPDOCK, SOS
	}

	private Zone interiorZone;
	private String unusableState;
	private String exteriorSprite;
	private Vec2f shipSize;

	private ShipState shipState;
	private SpaceshipStats shipStats;
	private ShipController shipController;
	private Spaceship dockedShip;

	private WarpHandler warpHandler;

	public Spaceship(String name, int x, int y, ShipState state) {
		UID = Universe.getInstance().getUIDGenerator().getShipUID();
		shipState = state;

		entityPosition = new Vec2f(x, y);
		entityName = name;
		interiorZone = new Zone(name + UID, 0, 0, zoneType.CLOSED, this);
		entityVisibility = true;
		Document doc = ParserHelper.LoadXML("assets/data/ships/" + entityName + ".xml");

		// read through the top level nodes
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();

		shipSize = new Vec2f(Integer.parseInt(n.getAttribute("width")),
				Integer.parseInt(n.getAttribute("height")));
		exteriorSprite = n.getAttribute("sprite");
		if (n.getAttribute("unusable").length() > 0) {
			unusableState = n.getAttribute("unusable");

		}
		NodeList children = n.getChildNodes();

	}

	public void setUID(int uID) {

		String s=Integer.toString(uID);
		String o=Integer.toString(this.UID);
		entityName= entityName.replace(o, s);

		interiorZone.zoneName = entityName;
		if (!interiorZone.zoneName.contains(s)) {
			interiorZone.zoneName = entityName + s;
		}
		UID = uID;
	}

	@Override
	public String getName() {
		return super.getName()+UID;
	}

	@Override
	public String getSprite() {
		return exteriorSprite;
	}

	@Override
	public void Generate() {
		Document doc = ParserHelper.LoadXML("assets/data/ships/" + entityName + ".xml");

		// read through the top level nodes
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		NodeList children = n.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				if (Enode.getTagName().contains("layout")) {
					interiorZone.LoadZone(Enode);
				}
				if (baseStats == null && Enode.getTagName().contains("shipstats")) {
					baseStats = new SpaceshipBaseStats(Enode);
				}
			}

		}

	}

	public void generateStats() {
		Document doc = ParserHelper.LoadXML("assets/data/ships/" + entityName + ".xml");

		// read through the top level nodes
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		NodeList children = n.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				if (Enode.getTagName().contains("shipstats")) {
					loadStats(Enode);
				}
			}

		}

	}

	private void loadStats(Element n) {

	}

	@Override
	public Zone getZone(int index) {
		switch (index)
		{
		case 0:
			return interiorZone;

		case 1:
			return dockedShip.interiorZone;
		}
		return interiorZone;

	}

	@Override
	public int getNumZones() {
		if (dockedShip!=null)
		{
			return 2;
		}
		return 1;
	}

	@Override
	public Zone getZone(String name) {
		if (interiorZone != null) {
			if (interiorZone.getName().contains(name)) {
				return interiorZone;
			}
		}

		if (dockedShip!=null && dockedShip.getZone(0)!=null)
		{
			if (dockedShip.getZone(0).getName().contains(name))
			{
				return dockedShip.getZone(0);
			}
		}
		return null;
	}

	public Vec2f getSize() {
		return shipSize;
	}

	@Override
	public Element LoadZone(Zone zone) {
		Document doc = ParserHelper.LoadXML("assets/data/ships/" + entityName + ".xml");

		// read through the top level nodes
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		NodeList children = n.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				if (Enode.getTagName().contains("layout")) {
					return Enode;
				}
				if (baseStats == null && Enode.getTagName().contains("shipstats")) {
					baseStats = new SpaceshipBaseStats(Enode);
				}
			}
		}
		return null;
	}

	public int getUID() {
		return UID;
	}

	public Element getExterior() {
		Document doc = ParserHelper.LoadXML("assets/data/ships/" + entityName + ".xml");

		// read through the top level nodes
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		NodeList children = n.getElementsByTagName("exterior");

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				if (Enode.getTagName().contains("exterior")) {
					return Enode;
				}
			}
		}

		return null;
	}

	public ShipState getState() {
		return shipState;
	}

	public String getUnusableState() {
		return unusableState;
	}

	public void setUnusableState(String unusableState) {
		this.unusableState = unusableState;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {

		// save type
		dstream.writeInt(1);
		dstream.writeInt(UID);

		ParserHelper.SaveString(dstream, shipState.toString());
		// save name
		ParserHelper.SaveString(dstream, entityName);
		// save position
		entityPosition.Save(dstream);
		// save visibility
		dstream.writeBoolean(visibility);
		if (visibilityScript != null) {
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, visibilityScript);
		} else {
			dstream.writeBoolean(false);
		}
		// save wrecked
		if (unusableState != null) {
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, unusableState);
		} else {
			dstream.writeBoolean(false);
		}

		// save sprite
		ParserHelper.SaveString(dstream, exteriorSprite);
		// save size
		shipSize.Save(dstream);
		// save zones
		interiorZone.Save(dstream);

		if (baseStats != null) {
			dstream.writeBoolean(true);
			baseStats.save(dstream);
		} else {
			dstream.writeBoolean(false);
		}

		if (dockedShip != null) {
			dstream.writeBoolean(true);
			dockedShip.save(dstream);
		} else {
			dstream.writeBoolean(false);
		}
		if (shipController!=null)
		{
			dstream.writeBoolean(true);
			shipController.save(dstream);
		}
		else
		{
			dstream.writeBoolean(false);
		}
		if (warpHandler!=null)
		{
			dstream.writeBoolean(true);
			warpHandler.save(dstream);
		}
		else
		{
			dstream.writeBoolean(false);
		}
	}

	public void load(DataInputStream dstream) throws IOException {

		UID = dstream.readInt();
		String str = ParserHelper.LoadString(dstream);
		shipState = ShipState.valueOf(str);
		entityName = ParserHelper.LoadString(dstream);
		entityPosition = new Vec2f(dstream);
		visibility = dstream.readBoolean();
		if (dstream.readBoolean()) {
			visibilityScript = ParserHelper.LoadString(dstream);
		}
		boolean b = dstream.readBoolean();
		if (b) {
			unusableState = ParserHelper.LoadString(dstream);
		}
		exteriorSprite = ParserHelper.LoadString(dstream);
		shipSize = new Vec2f(dstream);
		interiorZone = new Zone(entityName + UID, 0, 0, zoneType.CLOSED, this);
		ParserHelper.LoadString(dstream);
		interiorZone.load(dstream);

		if (dstream.readBoolean()) {
			baseStats = new SpaceshipBaseStats(dstream);
		}
		if (dstream.readBoolean()) {
			dockedShip = new Spaceship();
			int v=dstream.readInt();
			dockedShip.load(dstream);
		}
		if (dstream.readBoolean())
		{
			shipController=new NpcShipController();
			shipController.load(dstream);
			shipController.setShip(this);
			this.visibility = true;
		}
		if (dstream.readBoolean())
		{
			warpHandler=new WarpHandler();
			warpHandler.load(dstream);
		}


	}

	public Spaceship() {

	}

	@Override
	public void Save(String filename) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isStatic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Zone getZone(String name, int x, int y) {
		if (interiorZone != null) {
			if (interiorZone.getName().contains(name)) {
				return interiorZone;
			}
		}
		if (dockedShip!=null)
		{
			if (dockedShip.interiorZone!=null)
			{
				if (dockedShip.interiorZone.getName().contains(name))
				{
					return dockedShip.interiorZone;
				}
			}
		}
		return null;
	}

	public SpaceshipBaseStats getBaseStats() {
		return baseStats;
	}

	public boolean isWrecked() {
		if (unusableState != null) {
			return true;
		}
		return false;
	}

	public ShipState getShipState() {
		return shipState;
	}

	public SpaceshipStats getShipStats() {
		return shipStats;
	}

	public void setShipStats(SpaceshipStats shipStats) {
		this.shipStats = shipStats;
	}

	public void setShipState(ShipState shipState) {
		this.shipState = shipState;
	}

	public void setPosition(Vec2f pos) {
		this.entityPosition = pos;
		if (spriteObj != null) {
			spriteObj.reposition(pos);
		}
	}

	@Override
	public float getSpriteSize() {
		if (shipSize.x > shipSize.y) {
			return shipSize.x / 16;
		} else {
			return shipSize.y / 16;
		}
	}

	@Override
	public void update() {

		if (warpHandler!=null)
		{
			if (!warpHandler.update(this))
			{
				warpHandler=null;
			}
		}

		if (shipStats != null) {
			shipStats.run();
		}
		if (shipController != null) {
			shipController.update(this);
		}

	}

	public ShipController getShipController() {
		return shipController;
	}

	public void setShipController(ShipController shipController) {
		this.visibility = true;
		this.shipController = shipController;
		if (shipController!=null)
		{
			shipController.setShip(this);
		}
	}

	public boolean canThrust() {

		if (shipStats != null && shipStats.getResource("FUEL") != null
				&& shipStats.getResource("FUEL").getResourceAmount() >= shipStats.getFuelEfficiency()) {
			return true;
		}
		return false;
	}

	@Override
	public void postLoad(Zone zone) {
		if (shipState == ShipState.LAND) {
			ArrayList<WidgetPortal> portals = zone.getPortalWidgets();
			// find destination zone
			Landing landing = null;
			Entity world = Universe.getInstance().getCurrentWorld(this);

			for (int i = 0; i < world.getLandings().size(); i++) {
				if (world.getLandings().get(i).getShip() == this) {
					landing = world.getLandings().get(i);
					break;
				}
			}

			Zone destination = world.getZone(landing.getX(), landing.getY());
			for (int i = 0; i < portals.size(); i++) {
				portals.get(i).setDestination(destination.getName(), portals.get(i).getID());
			}

		}
		if (shipState == ShipState.DOCK) {
			ArrayList<WidgetPortal> portals = zone.getPortalWidgets();

			Station station = (Station) Universe.getInstance().getCurrentWorld(this);
			for (int i = 0; i < station.getDocked().getDockingPorts().length; i++) {
				if (this.equals(station.getDocked().getDockingPorts()[i].getDockedShip())) {
					portals.get(0).setDestination(station.getZone(i).getName(), portals.get(0).getID());
				}
			}
		}
	}

	@Override
	public Zone getLandableZone(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Zone getZone(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	public Spaceship getDockedShip() {
		return dockedShip;
	}

	public void setDockedShip(Spaceship dockedShip) {
		this.dockedShip = dockedShip;
	}

	@Override
	public void systemEntry() {
		super.systemEntry();
		if (shipController!=null)
		{
			shipController.event(scriptEvents.systemEntry);
		}
	}


	public WarpHandler getWarpHandler() {
		return warpHandler;
	}

	public void setWarpHandler(WarpHandler warpHandler) {
		this.warpHandler=warpHandler;
	}

	public String getshipName() {
		return entityName;
	}

}
