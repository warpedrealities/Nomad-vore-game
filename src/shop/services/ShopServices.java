package shop.services;

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

public class ShopServices implements ShopData {
	
	private String shopName;
	private List<ShipService> services;
	
	public ShopServices(String shopname) {
		this.shopName=shopname;
		services=new ArrayList<ShipService>();
		Document doc = ParserHelper.LoadXML("assets/data/shops/" + shopName + ".xml");
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		NodeList children = n.getChildNodes();
		
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				services.add(new ShipService(Enode));
			}
		}
	}


	public ShopServices(String str, DataInputStream dstream) throws IOException {
		shopName=str;
		services=new ArrayList<ShipService>();		
		int c=dstream.readInt();
		for (int i=0;i<c;i++)
		{
			services.add(new ShipService(dstream));
		}
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(services.size());
		for (int i=0;i<services.size();i++)
		{
			services.get(i).save(dstream);
		}
	}


	@Override
	public int getType() {
		return 1;
	}


	@Override
	public void refreshStore() {
		
	}

	@Override
	public Screen getScreen() {
		return new ServicesScreen(this);
	}
	
	@Override
	public String getName() {

		return shopName;
	}

	public ShipService getService(int index)
	{
		return services.get(index);
	}

	public int getCount() {
		return services.size();
	}	
}
