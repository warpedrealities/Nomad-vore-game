package shop.merchant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dialogue.evaluation.OutEvaluator;
import item.Item;
import nomad.universe.Universe;
import shared.ParserHelper;
import shared.Screen;
import shop.ShopData;
import vmo.GameManager;

public class ShopMerchant implements ShopData {

	private String shopName;
	private long lastVisited;
	private boolean useCredits;
	private Map<String, ShopLineItem> sellInventory;
	private Map<String, ShopLineItem> buyModifiers;
	private float profitRatio; // for buying back unknowns and etcs

	private void commonConstruct() {
		sellInventory = new HashMap<String, ShopLineItem>();
		buyModifiers = new HashMap<String, ShopLineItem>();
	}

	public ShopMerchant(String shopname) {
		commonConstruct();
		lastVisited = GameManager.getClock();
		this.shopName = shopname;
		refreshStore();
	}

	public void refreshStore() {
		sellInventory.clear();
		buyModifiers.clear();
		Document doc = ParserHelper.LoadXML("assets/data/shops/" + shopName + ".xml");
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		NodeList children = n.getChildNodes();
		profitRatio = Float.parseFloat(n.getAttribute("profitratio"));
		if (n.getAttribute("useCredits").contains("true")) {
			useCredits = true;
		}
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				// run each step successively
				if (Enode.getTagName() == "inventoryitem") {
					addItem(Enode);
				}
				if (Enode.getTagName() == "conditionalitem") {
					conditionalItem(Enode);
				}
				if (Enode.getTagName() == "buymodifier") {
					addModifier(Enode);
				}
			}
		}
	}

	private void addModifier(Element enode) {
		// TODO Auto-generated method stub
		String itemName = enode.getAttribute("name");
		float cost = 0;
		if (enode.getAttribute("cost").length() > 0) {
			cost = Float.parseFloat(enode.getAttribute("cost"));
		}
		buyModifiers.put(itemName, new ShopLineItem(itemName, 0, cost));

	}

	private void conditionalItem(Element enode) {
		NodeList n = enode.getElementsByTagName("conditional");
		Element e = (Element) n.item(0);
		String flag = e.getAttribute("flag");
		int value = Integer.parseInt(e.getAttribute("value"));
		String operator = e.getAttribute("operator");
		int c = Universe.getInstance().getPlayer().getFlags().readFlag(flag);
		if (OutEvaluator.ConditionCheck(value, operator, c)) {
			addItem(enode);
		}
	}

	private void addItem(Element enode) {
		String itemName = enode.getAttribute("name");
		int quantity = Integer.parseInt(enode.getAttribute("quantity"));
		float cost = 0;
		String tag = null;
		if (enode.getAttribute("cost").length() > 0) {
			cost = Float.parseFloat(enode.getAttribute("cost"));
		} else {
			Item it=Universe.getInstance().getLibrary().getItem(itemName);
			cost =it.getItemValue() * profitRatio;
		}
		if (enode.getAttribute("tag").length() > 0) {
			tag = enode.getAttribute("tag");
		}

		int chance = Integer.parseInt(enode.getAttribute("probability"));
		int roll = Universe.m_random.nextInt(100);
		if (roll < chance) {
			ShopLineItem item = sellInventory.get(itemName);
			if (item != null) {
				item.setQuantity(item.getQuantity() + quantity);
			} else {
				item = new ShopLineItem(itemName, quantity, cost);
				item.setTag(tag);
				sellInventory.put(itemName, item);
			}

		}
	}


	public void save(DataOutputStream dstream) throws IOException {
		ParserHelper.SaveString(dstream, shopName);
		Set<String> keyset = sellInventory.keySet();
		Iterator<String> it = keyset.iterator();
		dstream.writeInt(keyset.size());
		while (it.hasNext()) {
			String key = it.next();
			ParserHelper.SaveString(dstream, key);
			sellInventory.get(key).save(dstream);
		}
		keyset = buyModifiers.keySet();
		it = keyset.iterator();
		dstream.writeInt(keyset.size());
		while (it.hasNext()) {
			String key = it.next();
			ParserHelper.SaveString(dstream, key);
			buyModifiers.get(key).save(dstream);
		}
		dstream.writeBoolean(useCredits);
	}

	public ShopMerchant(String str, DataInputStream dstream) throws IOException {
		commonConstruct();

		shopName = ParserHelper.LoadString(dstream);
		int s = dstream.readInt();
		for (int i = 0; i < s; i++) {
			String name = ParserHelper.LoadString(dstream);
			ShopLineItem item = new ShopLineItem(name, dstream);
			sellInventory.put(name, item);
		}
		s = dstream.readInt();
		for (int i = 0; i < s; i++) {
			String name = ParserHelper.LoadString(dstream);
			ShopLineItem item = new ShopLineItem(name, dstream);
			buyModifiers.put(name, item);
		}
		useCredits = dstream.readBoolean();
	}

	public String getName() {

		return shopName;
	}

	public Map<String, ShopLineItem> getBuyModifiers() {
		return buyModifiers;
	}

	public void visit(long timestamp) {
		if (timestamp > lastVisited + 1000) {
			refreshStore();
		}
		lastVisited = timestamp;
	}

	public boolean isUseCredits() {
		return useCredits;
	}

	public ArrayList<ShopLineItem> getItems() {
		// TODO Auto-generated method stub
		ArrayList<ShopLineItem> items = new ArrayList<ShopLineItem>();
		Iterator<String> it = sellInventory.keySet().iterator();
		while (it.hasNext()) {
			items.add(sellInventory.get(it.next()));
		}
		return items;
	}

	public float getProfitRatio() {

		return profitRatio;
	}

	public void merge(ArrayList<ShopLineItem> shopList) {

		sellInventory.clear();
		for (int i = 0; i < shopList.size(); i++) {
			sellInventory.put(shopList.get(i).getName(), shopList.get(i));
		}
	}

	@Override
	public int getType() {
		return 0;
	}

	@Override
	public Screen getScreen() {
		return new ShopMerchantScreen(this);
	}
}
