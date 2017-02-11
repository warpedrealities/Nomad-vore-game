package shop;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import shared.ParserHelper;

public class ShopList {

	private static ShopList shopList;
	
	public static ShopList getInstance()
	{
		if (shopList==null)
		{
			shopList=new ShopList();
		}
		return shopList;
	}
	
	Map<String,ShopData> shopsRetained;
	
	public ShopList()
	{
		shopsRetained=new HashMap<String,ShopData>();
	}
	
	public ShopData getShop(String shopname)
	{
		ShopData data=shopsRetained.get(shopname);
		if (data==null)
		{
			data=new ShopData(shopname);
			shopsRetained.put(data.getName(), data);
		}
		return data;
	}
	
	public void save(DataOutputStream dstream) throws IOException
	{
		Set keyset=shopsRetained.keySet();
		Iterator<String> it=keyset.iterator();
		int size=keyset.size();
		dstream.writeInt(size);
		while (it.hasNext())
		{
			String key=it.next();
			ParserHelper.SaveString(dstream,key);
			shopsRetained.get(key).save(dstream);
		}
		
	}
	
	public void load (DataInputStream dstream) throws IOException
	{
		int s=dstream.readInt();
		for (int i=0;i<s;i++)
		{
			String str=ParserHelper.LoadString(dstream);
			ShopData data=new ShopData(str,dstream);
		}
	}
	
	
}
