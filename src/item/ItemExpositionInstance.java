package item;

import item.Item.ItemUse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.ParserHelper;

public class ItemExpositionInstance extends Item {

	String exposition;
	ItemExposition hostItem;
	
	public ItemExpositionInstance(int UID) {
		super(UID);
		// TODO Auto-generated constructor stub
	}
	public ItemExpositionInstance(ItemExposition item) {
		super(item.getUID());
		// TODO Auto-generated constructor stub
		hostItem=item;
	}

	@Override
	public ItemUse getUse()
	{
		return hostItem.m_use;
	}
	
	@Override
	public String getName()
	{
		return  hostItem.m_name+":"+exposition;
	}
	
	@Override
	public String getDescription()
	{

		return  hostItem.m_description;
	}
	
	
	@Override
	public float getWeight()
	{
		return  hostItem.m_weight;
	}
	@Override
	public Item getItem()
	{
		return hostItem;
	}
	
	public String getExposition() {
		return exposition;
	}

	public void setExposition(String exposition) {
		this.exposition = exposition;
	}

	public void save(DataOutputStream dstream) throws IOException
	{

		dstream.write(3);
		ParserHelper.SaveString(dstream, hostItem.getName());
		ParserHelper.SaveString(dstream, exposition);
		
	}
}
