package shop.mutator;

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

public class ShopMutator implements ShopData {

	String shopName;
	List<MutationItem> mutations;
	boolean useCredits;

	public ShopMutator(String shopname) {
		mutations = new ArrayList<>();
		Document doc = ParserHelper.LoadXML("assets/data/shops/" + shopname + ".xml");
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
				mutations.add(new MutationData(Enode));
			}
		}
	}

	public ShopMutator(String str, DataInputStream dstream) throws IOException {
		shopName = str;
		mutations = new ArrayList<>();
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
				mutations.add(new MutationData(Enode));
			}
		}
	}

	@Override
	public int getType() {
		return 3;
	}

	@Override
	public String getName() {
		return shopName;
	}

	@Override
	public void refreshStore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {

	}

	@Override
	public Screen getScreen() {

		return new MutatorStore(this);
	}

	public boolean isUseCredits() {
		return useCredits;
	}

	public List<MutationItem> getMutations() {
		return mutations;
	}

}
