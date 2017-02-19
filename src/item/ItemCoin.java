package item;

import java.io.DataOutputStream;
import java.io.IOException;

import shared.ParserHelper;

public class ItemCoin extends Item {

	int count;
	
	public ItemCoin() {
		super(0);
		// TODO Auto-generated constructor stub
		m_use=ItemUse.COIN;
		m_name="gold";
	}

	public void save(DataOutputStream dstream) throws IOException
	{

		dstream.write(5);
		dstream.writeInt(count);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	
}
