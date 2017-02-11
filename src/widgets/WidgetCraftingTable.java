package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import crafting.CraftingScreen;

import actor.Player;

import nomad.Universe;
import shared.ParserHelper;
import view.ViewScene;
import interactionscreens.ContainerScreen;
import item.Item;

public class WidgetCraftingTable extends WidgetBreakable {

	public WidgetCraftingTable(int sprite, String description, String name,
			Item[] contains, int hp, int[] resistances) {
		super(sprite, description, name, contains, hp, resistances);
		isVisionBlocking=false;
	}
	
	public WidgetCraftingTable(Element n) {
		
		super(n);
	}


	@Override
	public
	void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.write(6);
		commonSave(dstream);
		super.saveBreakable(dstream);
	}
	
	public WidgetCraftingTable(DataInputStream dstream) throws IOException {
		// TODO Auto-generated constructor stub
		super(dstream);
		
	}
	
	
	@Override
	public boolean safeOnly()
	{
		return true;
	}
	
	@Override
	public boolean Interact(Player player)
	{
		ViewScene.m_interface.setScreen(new CraftingScreen());
		
		return true;
	}
}
