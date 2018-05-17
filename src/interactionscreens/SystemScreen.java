package interactionscreens;

import gui.Button;
import gui.Text;
import gui.Window;
import gui.lists.List;
import input.MouseHook;
import item.Item;
import item.ItemEnergy;
import item.ItemHasEnergy;
import item.ItemLibrary;
import item.instances.ItemDepletableInstance;
import nomad.universe.Universe;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import actor.player.Player;
import shared.Callback;
import shared.ProportionBar;
import shared.Screen;
import shared.Vec2f;
import shipsystem.ShipAbility.AbilityType;
import shipsystem.ShipConverter;
import shipsystem.ShipDispenser;
import shipsystem.ShipResource;
import shipsystem.WidgetSystem;
import shipsystem.resourceConversion.ResourceConversionHandler;
import widgets.WidgetContainer;

public class SystemScreen extends Screen implements Callback {

	Player player;
	WidgetSystem system;
	ShipResource resourceObj;
	ShipConverter converterObj;
	ShipDispenser dispenserObj;
	Window window;
	List itemList;
	Text weightValue;
	Callback callback;
	Text resource;
	ProportionBar resourceBar;
	Button toggleButton;
	int equipThreshold;
	int equipCount;
	Text dispenserText;

	public SystemScreen(WidgetSystem widgetSystem) {
		// TODO Auto-generated constructor stub
		this.system = widgetSystem;
		this.player = Universe.getInstance().getPlayer();

	}

	@Override
	public void Callback() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(float DT) {
		window.update(DT);
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

		case 3:
			toggleConverter();
			break;
		case 4:
			dispenseItem();
			break;
		}
	}

	private void dispenseItem() {

		if (getNumDispensable() > 0) {

			player.getInventory().AddItem(Universe.getInstance().getLibrary().getItem(dispenserObj.getOutputItem()));
			resourceObj.setAmountContained(resourceObj.getAmountContained() - dispenserObj.getCost());
			resetList();
		}
	}

	private void toggleConverter() {
		if (converterObj != null) {
			if (converterObj.isActive()) {
				toggleButton.setString("turn on");
				converterObj.setActive(false);
			} else {
				toggleButton.setString("turn off");
				converterObj.setActive(true);
			}

		}
	}

	private Item findEquip(int index) {

		for (int i = 0; i < 4; i++) {
			if (player.getInventory().getSlot(i) != null) {
				if (index == 0) {
					return player.getInventory().getSlot(i);
				}
				index--;

			}
		}
		return null;
	}

	private void put() {
		if (resourceObj != null) {
			int v = itemList.getSelect();
			if (v >= 0 && v < player.getInventory().getNumItems() + equipCount) {
				if (v < equipThreshold) {
					if (trythis(player.getInventory().getItem(v))) {
						resetList();
					}
				} else {
					if (trythis(findEquip(v - equipThreshold))) {
						resetList();
					}
				}
			}
		}
	}

	private boolean trythis(Item item) {
		// check if item is a conversion
		ResourceConversionHandler handler=ResourceConversionHandler.getInstance();
		if (handler.canConvert(resourceObj.getContainsWhat(), item.getItem().getName()))
		{
			if (resourceObj.getAmountContained()>=resourceObj.getContainedCapacity())
			{
				return false;
			}
			if (ItemDepletableInstance.class.isInstance(item)) {
				ItemDepletableInstance idi = (ItemDepletableInstance) item;
				ItemHasEnergy ihe = (ItemHasEnergy) idi.getItem();

				float ratio = (float) idi.getEnergy() / ihe.getEnergy().getMaxEnergy();
				resourceObj.addResource(((float)handler.conversionValue(resourceObj.getContainsWhat(), item.getItem().getName())) * ratio);
			} else {
				resourceObj.addResource(handler.conversionValue(resourceObj.getContainsWhat(), item.getItem().getName()));
			}	
			player.getInventory().RemoveItem(item);
			return true;
		}
	
		// check if resource is energy and the item is a rechargeable
		if (resourceObj.getContainsWhat().equals("ENERGY") && ItemDepletableInstance.class.isInstance(item)) {
			ItemDepletableInstance it = (ItemDepletableInstance) item;
			int energy = it.getEnergy();
			ItemEnergy ie = ((ItemHasEnergy) it.getItem()).getEnergy();
			if (ie != null && ie.getRefill() != null && ie.getRefill().contains("ENERGY")
					&& energy < ie.getMaxEnergy()) {
				int missing = ie.getMaxEnergy() - energy;
				// get amount of energy required to completely recharge item
				float required = ((float) missing) / ie.getrefillrate();
				// if resource energy equal or greater, fully recharge and
				// subtract
				if (resourceObj.getAmountContained() >= required) {
					it.setEnergy(ie.getMaxEnergy());
					resourceObj.setAmountContained(resourceObj.getAmountContained() - required);
					return true;
				}
				// if not, recharge partially
				else if (resourceObj.getAmountContained() > 0) {
					float amount = resourceObj.getAmountContained() * ie.getrefillrate();
					it.setEnergy(it.getEnergy() + amount);

					resourceObj.setAmountContained(0);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void start(MouseHook hook) {

		hook.Register(window);

		hook.Register(itemList);

	}

	private Item[] getEquipped() {
		int c = 0;
		Item[] item = new Item[4];
		for (int i = 0; i < 4; i++) {
			if (player.getInventory().getSlot(i) != null) {

				item[c] = player.getInventory().getSlot(i);
				c++;
			}

		}
		if (c == 0) {
			equipCount = 0;
			return null;
		}
		Item[] items = new Item[c];
		for (int i = 0; i < c; i++) {
			items[i] = item[i];
		}

		equipCount = c;
		return items;
	}

	private String buildString(Item item) {
		StringBuilder strb = new StringBuilder();
		strb.append(item.getName());
		if (ItemDepletableInstance.class.isInstance(item)) {
			ItemDepletableInstance idi = (ItemDepletableInstance) item;
			ItemHasEnergy ihe = (ItemHasEnergy) idi.getItem();
			strb.append(" " + idi.getEnergy());
			if (ihe.getEnergy() != null) {
				strb.append("/" + ihe.getEnergy().getMaxEnergy());
			}
		}

		return strb.toString();
	}

	private void resetList() {
		Item[] equipped = getEquipped();
		equipThreshold = player.getInventory().getNumItems();
		int e = 0;
		if (equipped != null) {
			e = equipped.length;
		}
		String[] str = new String[player.getInventory().getNumItems() + e];
		for (int i = 0; i < str.length; i++) {
			if (i < equipThreshold) {
				str[i] = buildString(player.getInventory().getItem(i));
			} else {
				str[i] = buildString(equipped[i - equipThreshold]);
			}
		}
		itemList.GenList(str);
		weightValue.setString("weight:" + player.getInventory().getWeight());
		if (resourceObj != null) {
			resourceBar.setValue((int) resourceObj.getAmountContained());
			resource.setString(resourceObj.getContainsWhat() + " " + resourceObj.getAmountContained() + "/"
					+ resourceObj.getContainedCapacity());

		}
		if (dispenserObj != null) {
			dispenserText.setString(dispenserObj.getOutputItem() + ":" + Integer.toString(getNumDispensable()));			
		}
	}

	@Override
	public void initialize(int[] textures, Callback callback) {
		// 0 is bar
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint
		window = new Window(new Vec2f(-20, -16), new Vec2f(23, 15), textures[1], true);
		this.callback = callback;
		// build inventory left
		itemList = new List(new Vec2f(3, -14.3F), 16, textures[5], textures[4], this);

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

		toggleButton = new Button(new Vec2f(5.0F, 6.5F), new Vec2f(6, 1.8F), textures[2], this, "on/off", 3, 1);
		window.add(toggleButton);

		resource = new Text(new Vec2f(2.0F, 5.5F), "resource text", 1.0F, textures[4]);

		window.add(resource);
		resourceBar = new ProportionBar(new Vec2f(8.5F, 12), new Vec2f(16, 2), 40, 40, 5, textures[0]);
		window.add(resourceBar);

		Text conversionLabel = new Text(new Vec2f(2.0F, 4.5F), "converter text", 1.0F, textures[4]);
		window.add(conversionLabel);
		resetList();
		buildResource();
		buildConverter(conversionLabel);
		buildDispenser(textures);
	}

	private void buildDispenser(int textures[]) {
		if (dispenserObj != null) {
			dispenserText = new Text(new Vec2f(2.0F, 1.5F),
					dispenserObj.getOutputItem() + ":" + Integer.toString(getNumDispensable()), 1.0F, textures[4]);
			window.add(dispenserText);

			Button button = new Button(new Vec2f(4.0F, 0.5F), new Vec2f(8, 1.8F), textures[2], this, "dispense item", 4,
					0.8F);
			window.add(button);
		}
	}

	private int getNumDispensable() {
		if (resourceObj != null && resourceObj.getContainsWhat().equals(dispenserObj.getInput())) {
			int count=(int)resourceObj.getAmountContained() / dispenserObj.getCost();
			return count;
		}
		return 0;
	}

	private void buildConverter(Text label) {
		if (converterObj != null) {
			label.setString(converterObj.getConvertFrom() + "->" + converterObj.getConvertTo());
			if (converterObj.isActive()) {
				toggleButton.setString("turn off");
			} else {
				toggleButton.setString("turn on");
			}

		} else {
			label.setString("");
			toggleButton.setActive(false);

		}
	}

	private void buildResource() {
		// find resource
		for (int i = 0; i < system.getShipAbilities().size(); i++) {
			if (system.getShipAbilities().get(i).getAbilityType() == AbilityType.SA_RESOURCE) {
				resourceObj = (ShipResource) system.getShipAbilities().get(i);

			}
			if (system.getShipAbilities().get(i).getAbilityType() == AbilityType.SA_CONVERTER) {
				converterObj = (ShipConverter) system.getShipAbilities().get(i);
				converterObj.runConversion();
			}
			if (system.getShipAbilities().get(i).getAbilityType() == AbilityType.SA_DISPENSER) {
				dispenserObj = (ShipDispenser) system.getShipAbilities().get(i);
			}
		}
		if (resourceObj != null) {
			resourceBar.setMax(resourceObj.getContainedCapacity());
			resourceBar.setValue((int) resourceObj.getAmountContained());
			resource.setString(resourceObj.getContainsWhat() + " " + resourceObj.getAmountContained() + "/"
					+ resourceObj.getContainedCapacity());

		} else {
			resource.setString("");
			resourceBar.setVisible(false);
		}
	}
}
