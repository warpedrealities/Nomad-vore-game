package item;

import item.Item.ItemUse;

import java.io.DataOutputStream;
import java.io.IOException;

import shared.ParserHelper;

public class ItemKeyInstance extends Item {

	private Item hostItem;
	private String lock;
	
	public ItemKeyInstance(ItemKey item) {
		super(item.getUID());

		hostItem=item;
	}

	public void setLock(String string) {

		lock=string;
	}

	@Override
	public ItemUse getUse()
	{
		return hostItem.m_use;
	}
	
	@Override
	public String getName()
	{
		return  lock+" "+hostItem.m_name;
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
	
	public String getLock() {
		return lock;
	}

	public void save(DataOutputStream dstream) throws IOException
	{

		dstream.write(4);
		ParserHelper.SaveString(dstream, hostItem.getName());
		ParserHelper.SaveString(dstream, lock);
		
	}
}
