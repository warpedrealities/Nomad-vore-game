package item;

import java.io.DataOutputStream;
import java.io.IOException;

import shared.ParserHelper;

public abstract class Item implements Comparable {

	private int UID;
	protected String m_name, shortName, m_description;
	protected float m_weight, itemValue;
	protected int tag;
	
	public Item()
	{
		
	}
	
	public Item(int UID) {
		this.UID = UID;
	}

	public int getUID() {
		return UID;
	}

	public enum ItemUse {
		USE, EQUIP, NONE, COIN,OPEN
	};

	protected ItemUse m_use;

	public ItemUse getUse() {
		return m_use;
	}

	public String getName() {
		return m_name;
	}

	public String getShortName() {
		return shortName;
	}
	
	public String getDescription() {
		return m_description;
	}

	public float getWeight() {
		return m_weight;
	}

	public Item getItem() {
		return this;
	}

	public float getItemValue() {
		return itemValue;
	}

	public void save(DataOutputStream dstream) throws IOException {

		dstream.write(0);
		ParserHelper.SaveString(dstream, m_name);
	}

	@Override
	public int compareTo(Object arg0) {
		Item item = (Item) arg0;
		return getItem().getName().compareTo(item.getItem().getName());
	}

	public int getTag() {
		return tag;
	}

	public abstract boolean canStack();	
}
