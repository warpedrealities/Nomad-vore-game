package entities.stations;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.xml.soap.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import shared.ParserHelper;
import shared.Vec2f;
import spaceship.Spaceship;
import spaceship.Spaceship.ShipState;

public class DockingPort {
	private boolean startOpen;
	private Spaceship dockedShip;
	private Vec2f displayPosition;
	private String zoneAssociation;

	public DockingPort(Element node) {
		NodeList children = node.getChildNodes();
		if (node.getAttribute("startsOpen").equals("true")) {
			startOpen = true;
		}
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) children.item(i);
				if (e.getTagName().equals("zone")) {
					zoneAssociation = e.getAttribute("value");
				}
				if (e.getTagName().equals("position")) {
					displayPosition=new Vec2f(Float.parseFloat(e.getAttribute("x")),
							Float.parseFloat(e.getAttribute("y")));
				}
				if (e.getTagName().equals("dockedShip")) {
					dockedShip = new Spaceship(e.getAttribute("value"), 0, 0, ShipState.DOCK);
				}

			}
		}
	}

	public DockingPort(DataInputStream dstream) throws IOException {
		startOpen = dstream.readBoolean();
		zoneAssociation = ParserHelper.LoadString(dstream);
		if (dstream.readBoolean()) {
			displayPosition = new Vec2f(dstream.readFloat(), dstream.readFloat());
		}
		if (dstream.readBoolean()) {
			dockedShip = new Spaceship();
			dockedShip.load(dstream);
		}
	}

	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeBoolean(startOpen);
		ParserHelper.SaveString(dstream, zoneAssociation);
		if (displayPosition != null) {
			dstream.writeBoolean(true);
			dstream.writeFloat(displayPosition.x);
			dstream.writeFloat(displayPosition.y);
		} else {
			dstream.writeBoolean(false);
		}
		if (dockedShip != null) {
			dstream.writeBoolean(true);
			dockedShip.save(dstream);
		} else {
			dstream.writeBoolean(false);
		}

	}

	public Spaceship getDockedShip() {
		return dockedShip;
	}

	public void setDockedShip(Spaceship dockedShip) {
		this.dockedShip = dockedShip;
	}

	public boolean isStartOpen() {
		return startOpen;
	}

	public Vec2f getDisplayPosition() {
		return displayPosition;
	}

	public String getZoneAssociation() {
		return zoneAssociation;
	}

}
