package shop.shipyard;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shared.ParserHelper;
import shared.Screen;
import shop.ShopData;

public class ShopShipyard implements ShopData {

	private String shopName;
	private boolean useCredits;
	private List<ShipyardItem> ships;
	public ShopShipyard(String shopname) {
		this.shopName = shopname;
		ships = new ArrayList<>();
		Document doc = ParserHelper.LoadXML("assets/data/shops/" + shopName + ".xml");
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		NodeList children = n.getChildNodes();
		if (n.getAttribute("useCredits").equals("true")) {
			useCredits = true;
		}
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				ships.add(new ShipyardItem(Enode));
			}
		}
	}

	public ShopShipyard(String shopName, DataInputStream dstream) {
		this.shopName = shopName;
		ships = new ArrayList<>();
		Document doc = ParserHelper.LoadXML("assets/data/shops/" + shopName + ".xml");
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		NodeList children = n.getChildNodes();
		if (n.getAttribute("useCredits").equals("true")) {
			useCredits = true;
		}
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				ships.add(new ShipyardItem(Enode));
			}
		}
	}

	@Override
	public int getType() {
		return 3;
	}

	public List<ShipyardItem> getShips() {
		return ships;
	}

	public int getNumShips() {
		return ships.size();
	}

	public boolean isUseCredits() {
		return useCredits;
	}

	@Override
	public String getName() {
		return shopName;
	}

	@Override
	public void refreshStore() {

	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {

	}

	@Override
	public Screen getScreen() {
		return new ShipyardScreen(this);
	}

}
