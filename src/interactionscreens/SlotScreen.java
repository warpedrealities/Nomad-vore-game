package interactionscreens;

import gui.Button;
import gui.Text;
import gui.Window;
import gui.lists.List;
import input.MouseHook;
import item.Item;
import item.ItemEnergy;
import item.ItemHasEnergy;
import item.ItemWidget;
import item.instances.ItemDepletableInstance;
import item.instances.ItemStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import actor.player.Player;
import nomad.Universe;
import shared.Callback;
import shared.ProportionBar;
import shared.Screen;
import shared.Vec2f;
import shipsystem.ShipAbility.AbilityType;
import shipsystem.ShipResource;
import shipsystem.WidgetSystem;
import widgets.Widget;
import widgets.WidgetContainer;
import widgets.WidgetLoader;
import widgets.WidgetSlot;
import widgets.WidgetBreakable;

public class SlotScreen extends Screen implements Callback {

	WidgetSlot slot;
	Player player;
	Window window;
	List itemList;
	Text weightValue;
	Callback callback;
	Text resource;

	public SlotScreen(WidgetSlot widgetSlot) {

		this.slot = widgetSlot;
		this.player = Universe.getInstance().getPlayer();

	}

	@Override
	public void Callback() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(float DT) {

		itemList.update(DT);
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {

		window.Draw(buffer, matrixloc);
		itemList.Draw(buffer, matrixloc);
	}

	@Override
	public void discard(MouseHook mouse) {

		mouse.Remove(itemList);
		itemList.discard();
		mouse.Remove(window);
		window.discard();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub
		switch (ID) {
		case 2:
			callback.Callback();
			break;

		case 1:
			put();
			break;

		}
	}

	private void put() {

		int v = itemList.getSelect();
		if (v >= 0 && v < player.getInventory().getNumItems()) {
			if (trythis(player.getInventory().getItem(v))) {
				if (ItemStack.class.isInstance(player.getInventory().getItem(v)))
				{
					ItemStack is=(ItemStack)player.getInventory().getItem(v);
					is.setCount(is.getCount()-1);
					if (is.getCount()<=0)
					{
						player.getInventory().RemoveItem(player.getInventory().getItem(v));	
					}
				}
				else
				{
					player.getInventory().RemoveItem(player.getInventory().getItem(v));
				}
				callback.Callback();
			}
		}
	}

	private boolean trythis(Item item) {
		// check is module
		ItemWidget iw=null;
		if (ItemWidget.class.isInstance(item.getItem()))
		{
			if (ItemStack.class.isInstance(item))
			{
				iw=(ItemWidget)item.getItem();
			}
			else
			{
				iw=(ItemWidget) item;				
			}
		}
		if (iw!=null) {
			String name = iw.getWidgetName();
			Widget widget = WidgetLoader.genWidget(name);
			if (!slot.isHardpoint() && WidgetSystem.class.isInstance(widget))
			{
				WidgetSystem ws=(WidgetSystem)widget;
				if (ws.isHardPoint())
				{
					return false;
				}
			}
			slot.setWidget((WidgetBreakable) widget);


			return true;
		}
		return false;
	}

	@Override
	public void start(MouseHook hook) {

		hook.Register(window);

		hook.Register(itemList);

	}

	private void resetList() {
		String[] str = new String[player.getInventory().getNumItems()];
		for (int i = 0; i < str.length; i++) {
			str[i] = player.getInventory().getItem(i).getName() + " " + player.getInventory().getItem(i).getWeight();

		}
		itemList.GenList(str);
		weightValue.setString("weight:" + player.getInventory().getWeight());

	}

	private String facingToStr(int i) {
		switch (i) {
		case 0:
			return "forward";

		case 1:
			return "forward right";
		case 2:
			return "right";
		case 3:
			return "backwards right";
		case 4:
			return "back";
		case 5:
			return "backwards left";
		case 6:
			return "left";
		case 7:
			return "forward left";
		}
		return "";
	}

	@Override
	public void initialize(int[] textures, Callback callback) {
		// TODO Auto-generated method stub
		// 0 is bar
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint
		window = new Window(new Vec2f(-20, -16), new Vec2f(23, 15), textures[1], true);
		this.callback = callback;
		// build inventory left
		itemList = new List(new Vec2f(3, -14.3F), 16, textures[1], textures[4], this);

		String str[] = new String[16];
		for (int i = 0; i < 16; i++) {
			str[i] = "strings";
		}

		itemList.GenList(str);

		// build container inventory right
		Button[] buttons = new Button[2];
		buttons[0] = new Button(new Vec2f(17.0F, 0.0F), new Vec2f(6, 1.8F), textures[2], this, "Exit", 2, 1);
		buttons[1] = new Button(new Vec2f(17.0F, 2.0F), new Vec2f(6, 1.8F), textures[2], this, "put", 1, 1);
		// add buttons to move things to and from the container
		for (int i = 0; i < 2; i++) {
			window.add(buttons[i]);
		}
		// add button to exit

		this.callback = callback;

		weightValue = new Text(new Vec2f(14, 0.2F), "encumbrance", 0.7F, textures[4]);
		window.add(weightValue);

		if (slot.isHardpoint()) {
			resource = new Text(new Vec2f(1.0F, 5.5F), "hardpoint facing " + facingToStr(slot.getFacing()), 1.0F,
					textures[4]);
		} else {
			resource = new Text(new Vec2f(1.0F, 5.5F), "system slot ready, please put in a module", 1.0F, textures[4]);
		}

		window.add(resource);
		resetList();

	}

}
