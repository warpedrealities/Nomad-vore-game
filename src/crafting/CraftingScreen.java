package crafting;

import gui.Button;
import gui.List;
import gui.MultiLineText;
import gui.Text;
import gui.TextColoured;
import gui.Window;

import input.MouseHook;
import item.Item;
import item.ItemAmmo;
import item.ItemDepletableInstance;
import item.ItemStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import nomad.Universe;

import actor.Player;
import actorRPG.Actor_RPG;

import shared.Callback;
import shared.Screen;
import shared.Vec2f;

public class CraftingScreen extends Screen implements Callback {

	private Player player;
	private CraftingLibrary craftingLibrary;

	private Window root;
	private List possibleCrafts;
	private ArrayList <CraftingRecipe> craftables;
	
	
	private MultiLineText description;
	private TextColoured [] requirements;
	private Callback myHost;	
	private int selection;
	private boolean canMake;
	
	private int popupTimer;
	private Window popup;
	private Text popupText;
	
	
	public CraftingScreen()
	{
		player=Universe.getInstance().getPlayer();
		craftingLibrary=player.getCraftingLibrary();
	}
	
	@Override
	public void Callback() {
		// TODO Auto-generated method stub
		selection=possibleCrafts.getSelect();
		if (selection<craftables.size())
		{
			buildRequirements();
		}
		else
		{
			buildEmpty();
		}
	}
	
	private void buildEmpty() {
		// TODO Auto-generated method stub
		description.addText("");
		for (int i=0;i<8;i++)
		{
			requirements[i].setString("");
		}
	}

	@Override
	public void update(float DT) {
		possibleCrafts.update(DT);
		
		if (popupTimer>0)
		{
			popupTimer-=DT;
		}
	}
	
	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		// TODO Auto-generated method stub
		root.Draw(buffer, matrixloc);
		possibleCrafts.Draw(buffer, matrixloc);
		if (popupTimer>0)
		{
			popup.Draw(buffer, matrixloc);
		}
	}
	
	@Override
	public void discard(MouseHook mouse) {
		mouse.Remove(root);
		root.discard();
		mouse.Remove(possibleCrafts);
		possibleCrafts.discard();
		popup.discard();
	}
	
	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		switch (ID)
		{
		case 0:
			myHost.Callback();
			break;
		
		case 1:
			buildItem();
			break;
		
		}
	}
	
	private void buildItem() {
	
		//check item can be made
		if (canMake)
		{
			CraftingRecipe recipe=craftables.get(selection);
			//remove required items
			for (int i=0;i<recipe.getIngredients().size();i++)
			{
				player.getInventory().removeItems(
						recipe.getIngredients().get(i).getItemName(), recipe.getIngredients().get(i).getItemQuantity());
			}
			//add new item
				Item item=Universe.getInstance().getLibrary().getItem(recipe.getResult().getName());
				chargeCheck(item);
				player.getInventory().AddItem(item);
				player.calcMove();
			//create prompt to the player that the item has been made
			
				popupTimer=500;
				popupText.setString("you made a "+recipe.getName());
				
			buildRequirements();
		}
	}

	private void chargeCheck(Item item)
	{
		if (ItemDepletableInstance.class.isInstance(item))
		{
			ItemDepletableInstance ie=(ItemDepletableInstance) item;
			if (ItemAmmo.class.isInstance(ie.getItem()))
			{
				ItemAmmo ia=(ItemAmmo)ie.getItem();
				if (ia.getEnergy().getRefill().length()>0)
				{
					ie.setEnergy(0);
				}
			}
			else
			{
				ie.setEnergy(0);
			}
	
		}
	}
	
	@Override
	public void start(MouseHook hook) {

		hook.Register(possibleCrafts);
		hook.Register(root);
	}
	
	@Override
	public void initialize(int[] textures, Callback callback) {

		//0 is font
		//1 is frame 
		//2 button
		//3 is button alt
		//4 tint
		myHost=callback;
		
		root=new Window(new Vec2f(-20,-16),new Vec2f(40,15),textures[1],true);
		
		Button [] buttons=new Button[2];
		buttons[0]=new Button(new Vec2f(34.0F,0.0F),new Vec2f(6,1.8F),textures[2],this,"Exit",0,1);
		buttons[1]=new Button(new Vec2f(28.0F,0.0F),new Vec2f(6,1.8F),textures[2],this,"Make",1,1);
		for (int i=0;i<buttons.length;i++)
		{
			root.add(buttons[i]);
		}
		
		//build info panel
		description=new MultiLineText(new Vec2f (17.5F,14.5F),10,40,0.8F);
		//description.addText("text text text text text text text text text text text text text text text text text");
		root.add(description);
		//build requirements

		requirements=new TextColoured[8];
		
		for (int i=0;i<8;i++)
		{
			requirements[i]=new TextColoured(new Vec2f(15.6F,7-(0.8F*i)),"requirement"+i,0.8F,textures[4]);
			root.add(requirements[i]);
		}
		
		//built list of craftables
		possibleCrafts=new List(new Vec2f(-20,-16.0F),18,textures[1],textures[4],this);
		
		craftables=new ArrayList<CraftingRecipe>();
		
		

		generate();
		
		buildRequirements();
				
		popup=new Window(new Vec2f(-10,0),new Vec2f(20,2),textures[1],true);
		
		popupText=new Text(new Vec2f(0.2F,0.7F),"requirement",0.8F,0);
		popup.add(popupText);
		popupTimer=0;
	}
	
	private void buildRequirements()
	{
		canMake=true;
		if (craftables.size()>selection)
		{
			CraftingRecipe recipe=craftables.get(selection);
			description.addText(recipe.getDescription());
			
			for (int i=0;i<8;i++)
			{
				if (recipe.getIngredients().size()<=i)
				{
					requirements[i].setString("");
				}
				else
				{
					requirements[i].setString(recipe.getIngredients().get(i).getItemName()+
							" x"+recipe.getIngredients().get(i).getItemQuantity());
					
					if (hasIngredient(recipe.getIngredients().get(i)))
					{
						requirements[i].setTint(1, 1, 1);
					}
					else
					{
						canMake=false;
						requirements[i].setTint(1, 0, 0);
					}
				}
				
			}		
		}
		else
		{
			buildEmpty();
		}

	}
	
	private boolean hasIngredient(CraftingIngredient craftingIngredient) {
		int count=0;
		
		ArrayList<Item> list=player.getInventory().getItems();
		for (int i=0;i<list.size();i++)
		{
			if (list.get(i).getItem().getName().contains(craftingIngredient.getItemName()))
			{
				if (ItemStack.class.isInstance(list.get(i)))
				{
					ItemStack stack=(ItemStack)list.get(i);
					count+=stack.getCount();
					
				}
				else
				{
					count++;
				}
				
			}
		}
		
		
		if (count>=craftingIngredient.getItemQuantity())
		{
			return true;
		}
		return false;
	}

	private void generate()
	{
		int skill=player.getRPG().getAttribute(Actor_RPG.CRAFTS);
		//select all craftables that are unlocked and the player has crafting skill for
		Collection <CraftingRecipe> list=craftingLibrary.getCraftables();
		Iterator <CraftingRecipe> it=list.iterator();
		while (it.hasNext())
		{
			CraftingRecipe recipe=it.next();
			if (recipe.getUnlocked()==true && recipe.getRequiredSkill()<=skill)
			{
				craftables.add(recipe);
			}
		}
		
		//generate list
		String []names=new String[craftables.size()];
		for (int i=0;i<names.length;i++)
		{
			names[i]=craftables.get(i).getName();
		}
		possibleCrafts.GenList(names);
	}
	
}
