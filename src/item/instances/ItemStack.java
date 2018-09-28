package item.instances;

import java.io.DataOutputStream;
import java.io.IOException;

import item.Item;
import nomad.universe.Universe;
import shared.ParserHelper;

public class ItemStack extends Item {

	int m_count;
	Item m_item;
	
	public ItemStack(Item item, int count)
	{
		super(item.getUID());
		m_count=count;
		m_item=item;
	}
	
	@Override
	public ItemUse getUse()
	{
		return m_item.getUse();
	}
	
	public int getCount()
	{
		return m_count;
	}
	
	@Override
	public Item getItem()
	{
		return m_item;
	}
	
	public Item takeItem()
	{
		m_count--;
		return Universe.getInstance().getLibrary().getItem(m_item.getName());
	}
	
	public void setCount(int count)
	{
		m_count=count;
	}
	
	@Override
	public String getName()
	{
		return m_item.getName()+"x"+Integer.toString(m_count);
	}
	
	@Override
	public float getWeight()
	{
		return m_item.getWeight()*m_count;
	}
	@Override
	public float getItemValue() {
		return m_item.getItemValue();
	}

	public int getTag() {
		return m_item.getTag();
	}
	@Override
	public String getDescription()
	{
		
		return m_item.getDescription()+" this is a stack of "+Integer.toString(m_count);
	}
	@Override
	public void save(DataOutputStream dstream) throws IOException
	{
		// TODO Auto-generated method stub
		dstream.write(2);
		ParserHelper.SaveString(dstream, m_item.getName());

		dstream.write(m_count);
	}
	
	@Override
	public boolean canStack() {
		return true;
	}
}
