package entities.stations;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shared.ParserHelper;
import zone.Zone;

public class DockingModel {

	private DockingPort[] dockingPorts;
	private String dockingImage;

	public DockingModel(Element element) {
		dockingPorts = new DockingPort[Integer.parseInt(element.getAttribute("count"))];
		int index = 0;
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) children.item(i);
				if (e.getTagName().equals("dockingPort")) {
					dockingPorts[index] = new DockingPort(e);
					index++;
				}
				if (e.getTagName().equals("image")) {
					dockingImage = e.getAttribute("value");
				}
			}
		}
	}

	public DockingModel(DataInputStream dstream) throws IOException {
		int count = dstream.readInt();
		if (dstream.readBoolean()) {
			dockingImage = ParserHelper.LoadString(dstream);
		}
		dockingPorts = new DockingPort[count];

		for (int i = 0; i < count; i++) {
			dockingPorts[i] = new DockingPort(dstream);
		}
	}

	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(dockingPorts.length);
		if (dockingImage != null) {
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, dockingImage);
		} else {
			dstream.writeBoolean(false);
		}

		for (int i = 0; i < dockingPorts.length; i++) {
			dockingPorts[i].save(dstream);
		}
	}

	public DockingPort[] getDockingPorts() {
		return dockingPorts;
	}

	public String getDockingImage() {
		return dockingImage;
	}

	public Zone getZone(String name) {
		for (int i = 0; i < dockingPorts.length; i++) {
			if (dockingPorts[i].getDockedShip() != null) {
				if (dockingPorts[i].getDockedShip().getZone(0).getName().equals(name)) {
					return dockingPorts[i].getDockedShip().getZone(0);
				}
			}
		}
		return null;
	}

}
