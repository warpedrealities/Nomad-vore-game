package entities.stations;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import entities.Entity;
import nomad.universe.Universe;
import shared.ParserHelper;
import shared.Vec2f;
import spaceship.Spaceship;
import spaceship.Spaceship.ShipState;
import widgets.WidgetPortal;
import zone.Tile;
import zone.Zone;
import zone.Zone.zoneType;

public class Station extends Entity {

	ArrayList<Zone> stationZones;
	DockingModel dockingModel;

	float spriteSize;
	String spriteName;

	public Station(String name, int x, int y) {
		entityName = name;
		entityPosition = new Vec2f(x, y);

	}

	@Override
	public void postLoad(Zone zone) {

		for (int i = 0; i < dockingModel.getDockingPorts().length; i++) {
			if (dockingModel.getDockingPorts()[i].getDockedShip() != null
					&& zone.getName().equals(dockingModel.getDockingPorts()[i].getZoneAssociation())) {
				for (int k = 0; k < zone.getWidth(); k++) {
					for (int j = 0; j < zone.getHeight(); j++) {
						if (zone.getTile(k, j) != null && zone.getTile(k, j).getWidgetObject() != null) {
							if (WidgetPortal.class.isInstance(zone.getTile(k, j).getWidgetObject())) {
								WidgetPortal widget = (WidgetPortal) zone.getTile(k, j).getWidgetObject();
								if (widget.getPortalID() == -101) {
									widget.setDestination(
											dockingModel.getDockingPorts()[i].getDockedShip().getZone(0).getName(),
											-101);
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean FileExists() {
		if (Universe.getInstance().getSaveName() != null) {
			File file = new File("saves/" + Universe.getInstance().getSaveName() + "/" + entityName + ".sav");
			if (file.exists()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void Generate() {
		// TODO Auto-generated method stub
		if (stationZones == null) {
			// check if there exists a world file in the folder
			if (FileExists()) {
				loadGenerate();
				try {
					load();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
				// if not normal load
			{
				firstGenerate();
			}
		}
	}

	void firstGenerate() {
		stationZones = new ArrayList<Zone>();
		Document doc = ParserHelper.LoadXML("assets/data/stations/" + entityName + ".xml");

		Element n = (Element) doc.getFirstChild();
		NodeList children = n.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				if (Enode.getTagName().equals("dockingModel")) {
					dockingModel=new DockingModel(Enode);
				}

				if (Enode.getTagName() == "zone") {

					stationZones.add(new Zone(node.getTextContent(), Integer.parseInt(Enode.getAttribute("x")),
							Integer.parseInt(Enode.getAttribute("y")), zoneType.CLOSED, this));
				}
				if (Enode.getTagName() == "ship") {
					int index = Integer.parseInt(Enode.getAttribute("dock"));
					dockingModel.getDockingPorts()[index].setDockedShip(new Spaceship(Enode.getAttribute("name"), (int) entityPosition.x,
							(int) entityPosition.y, ShipState.DOCK));
					dockingModel.getDockingPorts()[index].getDockedShip().setPosition(new Vec2f(entityPosition.x, entityPosition.y));
				}
			}

		}
	}

	public void loadGenerate() {
		if (stationZones == null) {

			stationZones = new ArrayList<Zone>();
			Document doc = ParserHelper.LoadXML("assets/data/stations/" + entityName + ".xml");

			Element n = (Element) doc.getFirstChild();
			NodeList children = n.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node node = children.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element Enode = (Element) node;
					if (Enode.getTagName() == "zone") {
						// add this world
						boolean surface = false;
						if (Enode.getAttribute("surface") != null) {
							if (Integer.parseInt(Enode.getAttribute("surface")) > 0) {
								surface = true;
							}
						}
						stationZones.add(new Zone(node.getTextContent(), Integer.parseInt(Enode.getAttribute("x")),
								Integer.parseInt(Enode.getAttribute("y")), zoneType.CLOSED, this));
					}
				}

			}
		}
	}

	@Override
	public Zone getZone(int index) {
		return stationZones.get(index);

	}

	@Override
	public Zone getZone(String name, int x, int y) {

		return getZone(name);
	}

	@Override
	public Zone getZone(String name) {
		for (int i = 0; i < stationZones.size(); i++) {
			if (stationZones.get(i).getName().compareTo(name) == 0) {
				return stationZones.get(i);

			}
		}

		return dockingModel.getZone(name);

	}

	@Override
	public int getNumZones() {
		return stationZones.size();
	}

	@Override
	public Element LoadZone(Zone zone) {
		Document doc = ParserHelper.LoadXML("assets/data/stations/" + entityName + "/" + zone.zoneName + ".xml");

		// read through the top level nodes

		Element n = (Element) doc.getFirstChild();

		return n;
	}

	@Override
	public void Save(String filename) throws IOException {
		// TODO Auto-generated method stub
		if (stationZones != null) {
			File file = new File("saves/" + filename + "/" + entityName + ".sav");
			if (file.exists() == false) {
				file.createNewFile();
			}

			FileOutputStream fstream = new FileOutputStream(file);
			DataOutputStream dstream = new DataOutputStream(fstream);

			// save zones
			if (stationZones != null) {
				dstream.writeInt(stationZones.size());
				for (int i = 0; i < stationZones.size(); i++) {
					stationZones.get(i).Save(dstream);
				}

			} else {
				dstream.writeInt(0);
			}
			dockingModel.save(dstream);
		}

	}

	void load() throws IOException {

		File file = new File("saves/" + Universe.getInstance().getSaveName() + "/" + entityName + ".sav");

		FileInputStream fstream = new FileInputStream(file);
		DataInputStream dstream = new DataInputStream(fstream);

		// read number of zone entries
		int count = dstream.readInt();
		for (int i = 0; i < count; i++) {
			String name = ParserHelper.LoadString(dstream);
			// find zone
			Zone zone = getZone(name);
			if (zone != null) {
				zone.load(dstream);
			}
		}

		dockingModel = new DockingModel(dstream);

		dstream.close();
		fstream.close();

	}

	@Override
	public boolean isStatic() {

		return true;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {

	}

	private void loadSprite() {
		Document doc = ParserHelper.LoadXML("assets/data/stations/" + entityName + ".xml");

		Element n = (Element) doc.getFirstChild();

		NodeList nodeList = n.getElementsByTagName("entitysprite");

		Element e = (Element) nodeList.item(0);

		spriteSize = Float.parseFloat(e.getAttribute("size"));
		spriteName = e.getAttribute("filename");

	}

	@Override
	public String getSprite() {
		if (spriteName == null) {
			loadSprite();
		}
		return spriteName;
	}

	@Override
	public float getSpriteSize() {
		if (spriteName == null) {
			loadSprite();
		}
		return spriteSize;
	}

	@Override
	public Zone getLandableZone(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean dock(Spaceship ship, int portIndex) {

		DockingPort port = dockingModel.getDockingPorts()[portIndex];
		if (port.getDockedShip() == null) {
			port.setDockedShip(ship);
			// link up
			WidgetPortal portal = ship.getZone(0).getPortalWidget(-101);
			portal.setDestination(port.getZoneAssociation(), -101);
			getZone(port.getZoneAssociation()).LoadZone();
			portal = getZone(port.getZoneAssociation()).getPortalWidget(-101);
			portal.setDestination(ship.getZone(0).getName(), -101);
			return true;
		}
		return false;
	}

	public DockingModel getDocked() {
		return dockingModel;
	}

	public void unDock(Spaceship ship) {
		// TODO Auto-generated method stub
		for (int i = 0; i < dockingModel.getDockingPorts().length; i++) {
			if (ship.equals(dockingModel.getDockingPorts()[i].getDockedShip())) {
				Zone z = getZone(dockingModel.getDockingPorts()[i].getZoneAssociation());
				for (int j = 0; j < z.getWidth(); j++) {
					for (int k = 0; k < z.getHeight(); k++) {
						if (z.getTile(j, k) != null) {
							Tile t = z.getTile(j, k);
							if (t.getWidgetObject() != null) {
								if (WidgetPortal.class.isInstance(t.getWidgetObject())) {
									WidgetPortal portal = (WidgetPortal) t.getWidgetObject();
									if (portal.getPortalID() == -101) {
										portal.setDestination(null, -101);
										break;
									}

								}
							}
						}

					}
				}
				dockingModel.getDockingPorts()[i].setDockedShip(null);
			}
		}
	}

	@Override
	public Zone getZone(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLoaded() {
		if (stationZones == null) {
			return false;
		}
		return true;
	}
}
