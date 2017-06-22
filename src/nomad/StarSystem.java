package nomad;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rendering.SpriteBatch;
import shared.ParserHelper;
import shared.Vec2f;
import spaceship.Spaceship;
import spaceship.Spaceship.ShipState;

public class StarSystem {

	ArrayList<Entity> entitiesInSystem;
	String systemName;
	Vec2f systemPosition;

	public StarSystem(String name, int x, int y) {
		systemName = name;
		systemPosition = new Vec2f(x, y);
	}

	public void GenerateSystem(boolean firstload) {
		if (entitiesInSystem == null) {
			// generate the system
			entitiesInSystem = new ArrayList<Entity>();
			Document doc = ParserHelper.LoadXML("assets/data/systems/" + systemName + ".xml");
			Element root = doc.getDocumentElement();
			Element n = (Element) doc.getFirstChild();
			NodeList children = n.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node node = children.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element Enode = (Element) node;
					if (Enode.getTagName() == "World") {
						// add this world
						entitiesInSystem.add(new World(node.getTextContent(), Integer.parseInt(Enode.getAttribute("x")),
								Integer.parseInt(Enode.getAttribute("y"))));
					}
					if (Enode.getTagName() == "Station") {
						// add this world
						entitiesInSystem.add(new Station(node.getTextContent(),
								Integer.parseInt(Enode.getAttribute("x")), Integer.parseInt(Enode.getAttribute("y"))));
					}
					if (Enode.getTagName() == "Star") {
						entitiesInSystem.add(new Star(Integer.parseInt(Enode.getAttribute("intensity")),
								Integer.parseInt(Enode.getAttribute("x")), Integer.parseInt(Enode.getAttribute("y")),
								Enode.getAttribute("sprite")));
					}
					if (Enode.getTagName() == "Spaceship" && firstload == true) {
						Spaceship ship = new Spaceship(Enode.getAttribute("file"),
								Integer.parseInt(Enode.getAttribute("x")), Integer.parseInt(Enode.getAttribute("y")),
								ShipState.SPACE);

						entitiesInSystem.add(ship);
					}
				}

			}

		}
	}

	public String getName() {
		return systemName;
	}

	public World getWorld(int x, int y) {
		for (int i = 0; i < entitiesInSystem.size(); i++) {
			Vec2f p = entitiesInSystem.get(i).getPosition();
			int xcomp = (int) p.x;
			int ycomp = (int) p.y;
			if (xcomp == x && ycomp == y) {
				if (entitiesInSystem.get(i).getClass().getName().contains("World")) {
					return (World) entitiesInSystem.get(i);
				}
			}

		}
		return null;
	}

	public void Save(String filename) throws IOException {
		// save file as filename
		File file = new File("saves/" + filename + "/" + systemName + ".sav");
		if (file.exists() == false) {
			file.createNewFile();
		}

		FileOutputStream fstream = new FileOutputStream(file);
		DataOutputStream dstream = new DataOutputStream(fstream);

		// save entities
		if (entitiesInSystem != null) {
			dstream.writeInt(entitiesInSystem.size());
			for (int i = 0; i < entitiesInSystem.size(); i++) {
				if (entitiesInSystem.get(i).isStatic()) {
					dstream.writeBoolean(false);
					entitiesInSystem.get(i).Save(filename);
				} else {
					dstream.writeBoolean(true);
					entitiesInSystem.get(i).save(dstream);
				}

			}
		} else {
			dstream.writeInt(0);
		}
		dstream.close();

	}

	public ArrayList<Entity> getEntities() {
		return entitiesInSystem;
	}

	public void load(String filename) throws IOException {
		// TODO Auto-generated method stub
		File file = new File("saves/" + filename + "/" + systemName + ".sav");
		FileInputStream fstream = new FileInputStream(file);
		DataInputStream dstream = new DataInputStream(fstream);

		int count = dstream.readInt();
		for (int i = 0; i < count; i++) {
			boolean b = dstream.readBoolean();
			if (b == true) {
				int t = dstream.readInt();
				switch (t) {
				case 1:
					Spaceship ship = new Spaceship();
					ship.load(dstream);
					entitiesInSystem.add(ship);
					break;

				}
			}
		}
	}
}
