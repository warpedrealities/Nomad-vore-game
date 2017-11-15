package shop;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import shared.ParserHelper;
import shop.merchant.ShopMerchant;
import shop.services.ShopServices;

public class ShopList {

	private static ShopList shopList;

	public static ShopList getInstance() {
		if (shopList == null) {
			shopList = new ShopList();
		}
		return shopList;
	}

	Map<String, ShopData> shopsRetained;

	public ShopList() {
		shopsRetained = new HashMap<String, ShopData>();
	}

	public ShopData getShop(String shopname) {
		ShopData data = shopsRetained.get(shopname);
		if (data == null) {
			data = genData(shopname);
			shopsRetained.put(data.getName(), data);
		}
		return data;
	}

	private ShopData genData(String shopname)
	{
		Document doc = ParserHelper.LoadXML("assets/data/shops/" + shopname + ".xml");
		Element root = doc.getDocumentElement();		
		String check=root.getTagName();
		
		if ("shop".equals(check))
		{
			return new ShopMerchant(shopname);
		}
		if ("services".equals(check))
		{
			return new ShopServices(shopname);
		}	
		return null;
	}
	
	public void save(DataOutputStream dstream) throws IOException {
		Set keyset = shopsRetained.keySet();
		Iterator<String> it = keyset.iterator();
		int size = keyset.size();
		dstream.writeInt(size);
		while (it.hasNext()) {
			String key = it.next();
			ParserHelper.SaveString(dstream, key);
			dstream.writeInt(shopsRetained.get(key).getType());
			shopsRetained.get(key).save(dstream);
		}

	}
	
	private ShopData loadData(String str, DataInputStream dstream) throws IOException
	{
		ShopData data=null;
		int type=dstream.readInt();
		switch (type)
		{
			case 0:
			data=new ShopMerchant(str,dstream);
		
			case 1:
			data=new ShopServices(str,dstream);
		}
		return data;
	}

	public void load(DataInputStream dstream) throws IOException {
		shopsRetained.clear();
		int s = dstream.readInt();
		for (int i = 0; i < s; i++) {
			String str = ParserHelper.LoadString(dstream);
			shopsRetained.put(str, loadData(str,dstream));
		}
	}

	public void clear() {
		shopsRetained.clear();
	}

}
