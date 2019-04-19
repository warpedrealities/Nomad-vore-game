package shop.slaveTrader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import nomad.universe.Universe;
import shared.ParserHelper;
import shared.Screen;
import shop.ShopData;
import shop.shopFinanceFlags.FinanceFlags;

public class ShopSlaveTrader implements ShopData {

	private String shopName;
	private long lastVisited;
	private float profitRatio;
	private boolean useCredits;
	private List<SlaveLineItem> buyList;
	private List<SlaveLineItem> sellList;
	private FinanceFlags financeFlags;

	private void commonConstruct()
	{
		buyList=new ArrayList<SlaveLineItem>();
		sellList=new ArrayList<SlaveLineItem>();
	}

	public ShopSlaveTrader(String str, DataInputStream dstream) throws IOException {
		commonConstruct();
		this.shopName=str;
		this.loadStore(dstream);

	}

	public ShopSlaveTrader(String shopname) {
		commonConstruct();
		this.shopName=shopname;
		lastVisited=Universe.getClock();
		refreshStore();
	}

	@Override
	public int getType() {
		return 2;
	}

	@Override
	public String getName() {
		return shopName;
	}

	@Override
	public void refreshStore() {
		buyList.clear();
		sellList.clear();
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
				if (Enode.getTagName() == "buySlave") {
					buyList.add(new SlaveLineItem(Enode));
				}
				if (Enode.getTagName() == "sellSlave") {
					sellList.add(new SlaveLineItem(Enode));
				}
				if (Enode.getTagName().equals("financeFlags") && financeFlags==null)
				{
					financeFlags=new FinanceFlags(Enode);
				}
			}
		}
	}

	private void loadStore(DataInputStream dstream) throws IOException {

		lastVisited=dstream.readLong();
		profitRatio=dstream.readFloat();
		if (dstream.readBoolean()) {
			financeFlags=new FinanceFlags();
			financeFlags.load(dstream);
		}
		refreshStore();
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {

		dstream.writeLong(lastVisited);
		dstream.writeFloat(profitRatio);
		dstream.writeBoolean(financeFlags!=null);
		if (financeFlags!=null) {
			financeFlags.save(dstream);
		}
	}

	@Override
	public Screen getScreen() {
		return new ShopSlaveTraderScreen(this);
	}

	public boolean isUseCredits() {
		return useCredits;
	}

	public List<SlaveLineItem> getBuyList() {
		return buyList;
	}

	public List<SlaveLineItem> getSellList() {
		return sellList;
	}

	public FinanceFlags getFinanceFlags() {
		return financeFlags;
	}


}
