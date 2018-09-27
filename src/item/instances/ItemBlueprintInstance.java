package item.instances;

import java.io.DataOutputStream;
import java.io.IOException;

import item.Item;
import item.Item.ItemUse;
import shared.ParserHelper;

public class ItemBlueprintInstance extends Item {

	private Item item;
	String recipe;
	
	public ItemBlueprintInstance(Item item) {
		super(item.getUID());
		this.item=item;
	}
	
	@Override
	public ItemUse getUse()
	{
		return item.getUse();
	}
	
	@Override
	public String getName()
	{
		return  item.getName()+"("+recipe+")";
	}
	@Override
	public float getItemValue() {
		return item.getItemValue();
	}
	public void setRecipe(String recipe) {
		this.recipe=recipe;
	}
	
	@Override
	public String getDescription()
	{
		return item.getDescription();
	}
	
	@Override
	public Item getItem()
	{
		return item;
	}
	
	@Override
	public float getWeight()
	{
		return  item.getWeight();
	}

	public String getRecipe() {
		return recipe;
	}
	

	public void save(DataOutputStream dstream) throws IOException
	{
		dstream.write(6);
		ParserHelper.SaveString(dstream, item.getName());
		ParserHelper.SaveString(dstream, recipe);	
	}
	@Override
	public boolean canStack() {
		return false;
	}
}
