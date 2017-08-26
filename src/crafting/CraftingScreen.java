package crafting;

import gui.Button;
import gui.Button2;
import gui.MultiLineText;
import gui.Text;
import gui.TextColoured;
import gui.Window;
import gui.lists.ColouredList;
import gui.lists.List;
import input.MouseHook;
import item.Item;
import item.ItemAmmo;
import item.instances.ItemDepletableInstance;
import item.instances.ItemStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import actor.player.Player;
import nomad.Universe;
import actorRPG.Actor_RPG;
import actorRPG.player.Player_RPG;
import crafting.helpers.CraftingTokenHandler;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;

public class CraftingScreen extends Screen implements Callback {

	private Player player;
	private CraftingLibrary craftingLibrary;

	private Window root;
	private ColouredList possibleCrafts;
	private ArrayList<CraftingRecipe> craftables;

	private MultiLineText description;
	private TextColoured[] requirements;
	private Callback myHost;
	private int selection;
	private boolean canMake;
	private boolean filterMissingTokens,filterTech;

	private int popupTimer;
	private Window popup;
	private Text popupText;
	private Button2 []filterButtons;
	
	private CraftingTokenHandler tokenHandler;
	
	public CraftingScreen() {
		player = Universe.getInstance().getPlayer();
		craftingLibrary = player.getCraftingLibrary();
		tokenHandler=new CraftingTokenHandler((Player_RPG) player.getRPG());
	}

	@Override
	public void Callback() {
		selection = possibleCrafts.getSelect();
		if (selection < craftables.size()) {
			buildRequirements();
		} else {
			buildEmpty();
		}
	}

	private void buildEmpty() {
		description.addText("");
		for (int i = 0; i < 8; i++) {
			requirements[i].setString("");
		}
	}

	@Override
	public void update(float DT) {
		possibleCrafts.update(DT);
		
		if (popupTimer > 0) {
			popupTimer -= DT;
		}
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		// TODO Auto-generated method stub
		root.Draw(buffer, matrixloc);
		possibleCrafts.Draw(buffer, matrixloc);
		if (popupTimer > 0) {
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
		switch (ID) {
		case 0:
			myHost.Callback();
			break;

		case 1:
			buildItem();
			break;

		case 2:
			filterMissingTokens=!filterMissingTokens;
			filterButtons[0].setAlt(filterMissingTokens);
			regen();
			break;
	
		case 3:
			filterTech=!filterTech;
			filterButtons[1].setAlt(filterTech);
			regen();
			break;
		}
	}

	private void regen()
	{
		craftables.clear();
		generate(filterMissingTokens,filterTech);
	}
	
	private void buildItem() {

		// check item can be made
		if (canMake) {
			CraftingRecipe recipe = craftables.get(selection);
			// remove required items
			for (int i = 0; i < recipe.getIngredients().size(); i++) {
				player.getInventory().removeItems(recipe.getIngredients().get(i).getName(),
						recipe.getIngredients().get(i).getQuantity());
			}
			// add new item
			if (recipe.getResultCount() == 1) {
				Item item = Universe.getInstance().getLibrary().getItem(recipe.getResult().getItem().getName());
				chargeCheck(item);
				player.getInventory().AddItem(item);
			} else {

				Item item = Universe.getInstance().getLibrary().getItem(recipe.getResult().getItem().getName());
				chargeCheck(item);
				ItemStack stack = new ItemStack(item, recipe.getResultCount());
				player.getInventory().AddItem(stack);
			}

			player.calcMove();
			// create prompt to the player that the item has been made

			popupTimer = 500;
			popupText.setString("you made a " + recipe.getName());

			buildRequirements();
		}
	}

	private void chargeCheck(Item item) {
		if (ItemDepletableInstance.class.isInstance(item)) {
			ItemDepletableInstance ie = (ItemDepletableInstance) item;
			if (ItemAmmo.class.isInstance(ie.getItem())) {
				ItemAmmo ia = (ItemAmmo) ie.getItem();
				if (ia.getEnergy().getRefill().length() > 0) {
					ie.setEnergy(0);
				}
			} else {
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

		// 0 is font
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint
		myHost = callback;

		root = new Window(new Vec2f(-20, -16), new Vec2f(40, 15), textures[1], true);

		Button[] buttons = new Button[2];
		buttons[0] = new Button(new Vec2f(35.0F, 0.0F), new Vec2f(5, 1.8F), textures[2], this, "Exit", 0, 1);
		buttons[1] = new Button(new Vec2f(30.0F, 0.0F), new Vec2f(5, 1.8F), textures[2], this, "Make", 1, 1);
		for (int i = 0; i < buttons.length; i++) {
			root.add(buttons[i]);
		}
		
		filterButtons=new Button2[2];
		filterButtons[0] = new Button2(new Vec2f(24.0F, 0.0F), new Vec2f(6, 1.8F), textures[2], this, "filter:tokens", 2,textures[3], 1);
		filterButtons[1] = new Button2(new Vec2f(18.0F, 0.0F), new Vec2f(6, 1.8F), textures[2], this, "filter:tech", 3,textures[3], 1);
		for (int i = 0; i < filterButtons.length; i++) {
			root.add(filterButtons[i]);
		}	

		// build info panel
		description = new MultiLineText(new Vec2f(17.5F, 14.5F), 10, 40, 0.8F);
		// description.addText("text text text text text text text text text
		// text text text text text text text text");
		root.add(description);
		// build requirements

		requirements = new TextColoured[8];

		for (int i = 0; i < 8; i++) {
			requirements[i] = new TextColoured(new Vec2f(15.6F, 7 - (0.8F * i)), "requirement" + i, 0.8F, textures[4]);
			root.add(requirements[i]);
		}

		// built list of craftables
		float [][] colours={{1,1,1},{1,0,0}};
		possibleCrafts = new ColouredList(new Vec2f(-20, -16.0F), 18, textures[1], textures[4], this,17,colours);

		craftables = new ArrayList<CraftingRecipe>();

		generate(filterMissingTokens,filterTech);

		buildRequirements();

		popup = new Window(new Vec2f(-10, 0), new Vec2f(20, 2), textures[1], true);

		popupText = new Text(new Vec2f(0.2F, 0.7F), "requirement", 0.8F, 0);
		popup.add(popupText);
		popupTimer = 0;
	}

	private void buildRequirements() {
		canMake = true;
		if (craftables.size() > selection) {
			CraftingRecipe recipe = craftables.get(selection);
			description.addText(recipe.getDescription());

			if (recipe.getRequiredSkill()>player.getRPG().getAttribute(Actor_RPG.TECH))
			{
				canMake = false;
				requirements[0].setTint(1, 0, 0);	
				requirements[0].setString("requires skill of "+recipe.getRequiredSkill());	
				for (int i=1;i<8;i++)
				{
					requirements[i].setString("");		
				}

			}
			else
			{
				java.util.List <CraftingIngredient> l=recipe.getUnmetRequirements(tokenHandler.getMap());
				if (l!=null)
				{
					for (int i=0;i<l.size();i++)
					{
						requirements[0].setTint(1, 0, 0);	
						requirements[0].setString("requires "+l.get(i).getName()+" "+l.get(i).getQuantity());		
					}
					canMake = false;
				}
				else
				{					
					for (int i=0; i < 8; i++) {
						if (recipe.getIngredients().size() <= i) {
							requirements[i].setString("");
						} else {
							requirements[i].setString(recipe.getIngredients().get(i).getName() + " x"
									+ recipe.getIngredients().get(i).getQuantity());
	
							if (hasIngredient(recipe.getIngredients().get(i))) {
								requirements[i].setTint(1, 1, 1);
							} else {
								canMake = false;
								requirements[i].setTint(1, 0, 0);
							}
						}
					}	
				}
			}

		} else {
			buildEmpty();
		}

	}

	private boolean hasIngredient(CraftingIngredient craftingIngredient) {
		int count = 0;

		java.util.List<Item> list = player.getInventory().getItems();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getItem().getName().contains(craftingIngredient.getName())) {
				if (ItemStack.class.isInstance(list.get(i))) {
					ItemStack stack = (ItemStack) list.get(i);
					count += stack.getCount();

				} else {
					count++;
				}

			}
		}

		if (count >= craftingIngredient.getQuantity()) {
			return true;
		}
		return false;
	}

	private boolean canMake(CraftingRecipe recipe,int skill)
	{
		if (recipe.getRequiredSkill()<=skill)
		{
			if (recipe.getTokenRequirements().size()>0)
			{
				for (int i=0;i<recipe.getTokenRequirements().size();i++)
				{
					Integer token=tokenHandler.getToken(recipe.getTokenRequirements().get(i).getName());
					if (token==null)
					{
						token=new Integer(0);
					}
					if (recipe.getTokenRequirements().get(i).getQuantity()>token)
					{
						return false;
					}
				}
			}
			return true;			
		}
		return false;
	}
	
	private void generateOne(boolean filterMissingTokens,CraftingRecipe recipe)
	{
		if (filterMissingTokens)
		{
			if (recipe.getUnmetRequirements(tokenHandler.getMap())==null)
			{
				craftables.add(recipe);	
			}
		}
		else
		{
			craftables.add(recipe);			
		}

	}
	
	private void generate(boolean filterMissingTokens,boolean filterTech) {
		int skill = player.getRPG().getAttribute(Actor_RPG.TECH);
		// select all craftables that are unlocked and the player has crafting
		// skill for
		java.util.List<CraftingRecipe> list=craftingLibrary.getSortedCraftables();
		for (int i=0;i<list.size();i++)
		{
			CraftingRecipe recipe=list.get(i);
			if (filterTech)
			{
				if (recipe.getUnlocked() == true && recipe.getRequiredSkill() <= skill) {
					generateOne(filterMissingTokens,recipe);
				}			
			}
			else
			{
				if (recipe.getUnlocked() == true && recipe.getRequiredSkill() <= skill+2) {
					generateOne(filterMissingTokens,recipe);
				}				
			}
			
		}
		// generate list
		String[] names = new String[craftables.size()];
		int []colours=new int[craftables.size()];
		for (int i = 0; i < names.length; i++) {
			CraftingRecipe r=craftables.get(i);
			if (canMake(r,skill))
			{
				colours[i]=0;
			}
			else
			{
				colours[i]=1;
			}
			names[i] = r.getName();
		}
		possibleCrafts.setStringColour(colours);
		possibleCrafts.GenList(names);
	}

}
