package item;

import java.io.DataOutputStream;
import java.io.IOException;

import shared.ParserHelper;

public class ItemCoin extends Item {

	int count;
	boolean isCredits;

	public ItemCoin(boolean credits) {
		super(0);
		// TODO Auto-generated constructor stub
		m_use = ItemUse.COIN;

		this.isCredits=credits;
		if (this.isCredits)
		{
			m_name = "credits";		
		}
		else
		{
			m_name = "gold";		
		}
	}

	public ItemCoin() {
		super(0);
		m_use = ItemUse.COIN;
		m_name = "gold";
	}

	public void save(DataOutputStream dstream) throws IOException {

		dstream.write(5);
		dstream.writeInt(count);
		dstream.writeBoolean(isCredits);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	@Override
	public boolean canStack() {
		return false;
	}

	public void setIsCredits(boolean readBoolean) {
		this.isCredits=readBoolean;
		if (this.isCredits)
		{
			m_name="credits";
		}
	}

	public boolean isCredits() {
		return isCredits;
	}
}
