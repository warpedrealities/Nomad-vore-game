package shop.merchant;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import actor.player.Player;
import gui.Button;
import gui.ScrollableMultiLineText;
import gui.Text;
import gui.Window;
import gui.lists.List;
import input.MouseHook;
import item.Item;
import item.ItemAmmo;
import item.QuestItem;
import item.instances.ItemBlueprintInstance;
import item.instances.ItemDepletableInstance;
import item.instances.ItemExpositionInstance;
import item.instances.ItemKeyInstance;
import item.instances.ItemStack;
import nomad.universe.Universe;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;

public class ShopMerchantScreen extends Screen implements Callback {
	public static final int DEF_CREDIT_TO_GOLD = 10;
	private ShopMerchant shopData;
	private Player player;
	private ArrayList<ShopLineItem> shopList;
	Window window;
	Window subwindow;
	List[] itemLists;
	Text weightValue;
	Text money;
	ScrollableMultiLineText description;

	Callback callback;

	public ShopMerchantScreen(ShopMerchant merchant) {

		shopData = merchant;
		shopData.visit(Universe.getClock());
		player = Universe.getInstance().getPlayer();
		shopList = shopData.getItems();
	}

	@Override
	public void update(float DT) {
		window.update(DT);
		subwindow.update(DT);
		for (int i=0;i<itemLists.length;i++)
		{
			itemLists[i].update(DT);
		}
		description.update(DT);
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		// TODO Auto-generated method stub
		window.Draw(buffer, matrixloc);
		subwindow.Draw(buffer, matrixloc);
		for (int i = 0; i < 2; i++) {
			itemLists[i].Draw(buffer, matrixloc);
		}
	}

	@Override
	public void discard(MouseHook mouse) {
		player.calcMove();
		for (int i = 0; i < itemLists.length; i++) {
			mouse.Remove(itemLists[i]);
		}
		mouse.Remove(window);
		shopData.merge(shopList);
	}

	private void buy(int slotNum) {
		if (slotNum>=shopList.size())
		{
			return;
		}
		ShopLineItem item = shopList.get(slotNum);
		// check player currency
		int c = player.getInventory().getPlayerGold();
		if (shopData.isUseCredits()) {
			c = player.getInventory().getPlayerCredits();
			if (item.getCost()* DEF_CREDIT_TO_GOLD > c) {
				return;
			}		
		}
		else
		{
			if (item.getCost() > c) {
				return;
			}			
		}

		// add item to player inventory
		Item itemBought = Universe.getInstance().getLibrary().getItem(item.getName());
		if (ItemKeyInstance.class.isInstance(itemBought)) {
			ItemKeyInstance iki = (ItemKeyInstance) itemBought;
			iki.setLock(item.getTag());
		}
		if (ItemExpositionInstance.class.isInstance(itemBought)) {
			ItemExpositionInstance iei = (ItemExpositionInstance) itemBought;
			iei.setExposition(item.getTag());
		}
		if (ItemBlueprintInstance.class.isInstance(itemBought))
		{
			ItemBlueprintInstance ibi=(ItemBlueprintInstance) itemBought;
			ibi.setRecipe(item.getTag());
		}
		player.getInventory().AddItem(itemBought);
		// deduct currency
		if (shopData.isUseCredits()) {
			player.getInventory().setPlayerCredits(
					(int) (player.getInventory().getPlayerCredits() - (item.getCost() * DEF_CREDIT_TO_GOLD)));
			if (shopData.getFinanceFlags()!=null) 
			{
				shopData.getFinanceFlags().addBuyMoney((int)(item.getCost() * DEF_CREDIT_TO_GOLD));
			}
		} else {
			player.getInventory().setPlayerGold((int) (player.getInventory().getPlayerGold() - item.getCost()));
			if (shopData.getFinanceFlags()!=null) 
			{
				shopData.getFinanceFlags().addBuyMoney((int)(item.getCost()));
			}
		}
		// reduce count of items available in store
		item.setQuantity(item.getQuantity() - 1);
		// remove item from store if count is now 0
		if (item.getQuantity() <= 0) {
			shopList.remove(item);
		}
		resetList();
	}

	private float valueMod(String itemName) {
		ShopLineItem sli = shopData.getBuyModifiers().get(itemName);
		if (sli != null) {
			return sli.getCost();
		}
		return 0;
	}

	private void sell(int slotNum) {

		// figure out how much money to give the player
		if (player.getInventory().getItem(slotNum) == null) {
			return;
		}
		float v = player.getInventory().getItem(slotNum).getItemValue();
		float valt = valueMod(player.getInventory().getItem(slotNum).getItem().getName());
		if (valt > 0) {
			v = valt;
		}
		if (shopData.isUseCredits()) {
			v = v * DEF_CREDIT_TO_GOLD;
			if (shopData.getFinanceFlags()!=null) 
			{
				shopData.getFinanceFlags().addSellMoney((int)v);
			}
			player.getInventory().setPlayerCredits(player.getInventory().getPlayerCredits() + (int) v);
		} else {
			player.getInventory().setPlayerGold(player.getInventory().getPlayerGold() + (int) v);
			if (shopData.getFinanceFlags()!=null) 
			{
				shopData.getFinanceFlags().addSellMoney((int)v);
			}
		}
		// remove the item, unless its a stack then reduce it by 1
		Item it = player.getInventory().getItem(itemLists[0].getSelect());
		if (ItemStack.class.isInstance(it)) {
			player.getInventory().RemoveItemStack(it);

		} else {
			player.getInventory().RemoveItem(it);
		}
		
		// add the item to the shop list, because it matters
		if (!QuestItem.class.isInstance(it))
		{
			addToShop(it);			
		}
		resetList();
	}

	private void addToShop(Item item) {
		for (int i = 0; i < shopList.size(); i++) {
			if (shopList.get(i).getName().contains(item.getItem().getName())) {
				shopList.get(i).setQuantity(shopList.get(i).getQuantity() + 1);
				return;
			}
		}

		float v = item.getItemValue();
		float valt = valueMod(item.getItem().getName());
		if (valt > 0) {
			v = valt;
		}
		ShopLineItem line = new ShopLineItem(item.getItem().getName(), 1, v * shopData.getProfitRatio());
		if (ItemKeyInstance.class.isInstance(item)) {
			ItemKeyInstance iki = (ItemKeyInstance) item;
			line.setTag(iki.getLock());
		}
		if (ItemExpositionInstance.class.isInstance(item)) {
			ItemExpositionInstance iei = (ItemExpositionInstance) item;
			line.setTag(iei.getExposition());
		}
		shopList.add(line);
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {

		switch (ID) {

		case 1:
			sell(itemLists[0].getSelect());
			break;
		case 2:
			buy(itemLists[1].getSelect());
			break;
		case 3:
			player.calcMove();
			callback.Callback();
			break;

		}
	}

	@Override
	public void start(MouseHook hook) {
		// TODO Auto-generated method stub
		hook.Register(window);
		for (int i = 0; i < 2; i++) {
			hook.Register(itemLists[i]);
		}
	}

	@Override
	public void Callback() {

		description.addText(
				Universe.getInstance().getLibrary().readItemDesc(shopList.get(itemLists[1].getSelect()).getName()));
	}

	@Override
	public void initialize(int[] textures, Callback callback) {
		// TODO Auto-generated method stub
		// 0 is bar
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint
		this.callback = callback;
		window = new Window(new Vec2f(-3, -16), new Vec2f(6, 15), textures[1], true);
		subwindow = new Window(new Vec2f(3, -1), new Vec2f(17, 17), textures[1], true);
		itemLists = new List[2];
		// build inventory left
		itemLists[0] = new List(new Vec2f(3, -14.3F), 16, textures[5], textures[4], null);
		itemLists[1] = new List(new Vec2f(-20, -14.3F), 16, textures[5], textures[4], this);

		String str[] = new String[16];
		for (int i = 0; i < 16; i++) {
			str[i] = "strings";
		}
		for (int i = 0; i < 2; i++) {
			itemLists[i].GenList(str);
		}

		// build container inventory right
		Button[] buttons = new Button[3];
		buttons[0] = new Button(new Vec2f(0.0F, 0.0F), new Vec2f(6, 1.8F), textures[2], this, "Exit", 3, 1);
		buttons[1] = new Button(new Vec2f(0.0F, 2.0F), new Vec2f(6, 1.8F), textures[2], this, "buy", 2, 1);
		buttons[2] = new Button(new Vec2f(0.0F, 4.0F), new Vec2f(6, 1.8F), textures[2], this, "sell", 1, 1);
		// add buttons to move things to and from the container
		for (int i = 0; i < 3; i++) {
			window.add(buttons[i]);
		}
		// add button to exit

		this.callback = callback;

		weightValue = new Text(new Vec2f(6, 0.2F), "encumbrance", 0.7F, textures[4]);
		window.add(weightValue);
		money = new Text(new Vec2f(0.2F, 6.2F), "money", 0.7F, textures[4]);

		window.add(money);

		description = new ScrollableMultiLineText(new Vec2f(0.5F, 16), 10, 48, 0.8F,new Vec2f(19,12));
		description.addText("" + "");
		subwindow.add(description);
		resetList();
	}

	int calcCost(Item item) {
		int value = (int) item.getItemValue();
		int altvalue = (int) valueMod(item.getItem().getName());
		if (altvalue > 0) {
			value = altvalue;
		}
		if (ItemDepletableInstance.class.isInstance(item)) {
			Item it = item.getItem();
			if (ItemAmmo.class.isInstance(it)) {
				float e = ((ItemDepletableInstance) item).getEnergy();
				float m = ((ItemAmmo) it).getEnergy().getMaxEnergy();
				float v = value;
				v = v * (e / m);
				value = (int) v;
			}
		}
		if (shopData.isUseCredits()) {
			value = value * DEF_CREDIT_TO_GOLD;

		}
		return value;
	}

	void resetList() {
		String[] str = new String[player.getInventory().getNumItems()];
		for (int i = 0; i < str.length; i++) {
			str[i] = player.getInventory().getItem(i).getName() + " " + "" + calcCost(player.getInventory().getItem(i));

		}
		itemLists[0].GenList(str);
		weightValue.setString("weight:" + player.getInventory().getWeight());
		// write list for sellable items
		str = new String[shopList.size()];
		for (int i = 0; i < shopList.size(); i++) {
			str[i] = shopList.get(i).getName();
			if (shopList.get(i).getTag() != null) {
				str[i] = str[i] + " " + shopList.get(i).getTag();
			}
			str[i] = str[i] + " x" + shopList.get(i).getQuantity();
			if (shopData.isUseCredits()) {
				str[i] = str[i] + " C:" + shopList.get(i).getCost() * DEF_CREDIT_TO_GOLD;
			} else {
				str[i] = str[i] + " G:" + shopList.get(i).getCost();
			}
		}
		itemLists[1].GenList(str);

		if (shopData.isUseCredits()) {
			money.setString("C:" + player.getInventory().getPlayerCredits());
		} else {
			money.setString("G:" + player.getInventory().getPlayerGold());
		}
	}

}
