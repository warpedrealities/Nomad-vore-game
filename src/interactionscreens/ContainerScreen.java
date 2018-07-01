package interactionscreens;

import gui.Button;
import gui.Text;
import gui.Window;
import gui.lists.List;
import input.Keyboard;
import input.MouseHook;
import item.Item;
import item.ItemCoin;
import item.instances.ItemStack;
import nomad.universe.Universe;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import actor.player.Player;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;
import widgets.WidgetContainer;

public class ContainerScreen extends Screen implements Callback {

	Player player;
	WidgetContainer container;
	Window window;
	List[] itemLists;
	Text[] weightValues;
	Callback callback;

	public ContainerScreen(WidgetContainer container) {
		this.container = container;
		this.player = Universe.getInstance().getPlayer();

	}

	@Override
	public void update(float DT) {
		window.update(DT);
		for (int i = 0; i < 2; i++) {
			itemLists[i].update(DT);
		}
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		window.Draw(buffer, matrixloc);
		for (int i = 0; i < 2; i++) {
			itemLists[i].Draw(buffer, matrixloc);
		}
	}

	@Override
	public void discard(MouseHook mouse) {
		player.calcMove();
		for (int i = 0; i < 2; i++) {
			mouse.Remove(itemLists[i]);
			itemLists[i].discard();
		}
		mouse.Remove(window);
		window.discard();
	}

	private void put() {
		int index = itemLists[0].getSelect();
		if (index < player.getInventory().getNumItems()) {
			Item item = player.getInventory().RemoveItem(player.getInventory().getItem(index));
			if (!container.addItem(item)) {
				player.getInventory().AddItem(item);
			}
			player.setBusy(1);
		}
		resetList();
	}

	private void take() {
		int index = itemLists[1].getSelect();

		if (index < container.getItems().size()) {
			Item item = container.takeItem(index);
			if (ItemCoin.class.isInstance(item)) {
				ItemCoin coin = (ItemCoin) item;
				if (coin.isCredits())
				{
					player.getInventory().setPlayerCredits(player.getInventory().getPlayerCredits() + coin.getCount());
									
				}
				else
				{
					player.getInventory().setPlayerGold(player.getInventory().getPlayerGold() + coin.getCount());
			
				}
			} else {
				player.getInventory().AddItem(item);
				player.setBusy(1);
			}

		}
		resetList();
	}


	private void takeStack() {
		int index = itemLists[1].getSelect();

		if (index < container.getItems().size()) {
			if (ItemStack.class.isInstance(container.getItems().get(index)))
			{
				ItemStack stack=(ItemStack)container.getItems().get(index);
				container.setContainedWeight(container.getContainedWeight()-stack.getWeight());
				container.getItems().remove(index);
				//add to player inventory
				player.getInventory().AddItem(stack);
			}
			else
			{
				take();
			}
			
		}
		
		resetList();
	}

	private void putStack() {
		int index = itemLists[0].getSelect();
		if (index < player.getInventory().getNumItems()) {
			if (ItemStack.class.isInstance(player.getInventory().getItem(index)))
			{
				ItemStack stack=(ItemStack)player.getInventory().getItems().get(index);
				player.getInventory().setWeight(player.getInventory().getWeight()-stack.getWeight());
				player.getInventory().getItems().remove(index);
				//add to container inventory
				container.addStack(stack);
			}
			else
			{
				put();
			}

		}
		
		resetList();
	}
	
	
	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		switch (ID) {

		case 1:
			// put
			if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)||Keyboard.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT))
			{
				putStack();
			}
			else
			{
				put();		
			}

			break;
		case 2:
			// take
			if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)||Keyboard.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT))
			{
				takeStack();
			}
			else
			{
				take();			
			}

			break;
		case 3:
			container.sort();
			player.calcMove();
			callback.Callback();
			break;

		}
	}

	@Override
	public void start(MouseHook hook) {

		hook.Register(window);
		for (int i = 0; i < 2; i++) {
			hook.Register(itemLists[i]);
		}
	}

	private void resetList() {
		String[] str = new String[player.getInventory().getNumItems()];
		for (int i = 0; i < str.length; i++) {
			str[i] = player.getInventory().getItem(i).getName() + " " + player.getInventory().getItem(i).getWeight();

		}
		itemLists[0].GenList(str);

		if (container.getItems() == null) {
			container.setItems(new ArrayList<Item>());
		}
		if (container.getItems().size() > 0) {
			str = new String[container.getItems().size()];
			for (int i = 0; i < str.length; i++) {
				if (ItemCoin.class.isInstance(container.getItems().get(i))) {
					ItemCoin ic = (ItemCoin) container.getItems().get(i);
					str[i] = container.getItems().get(i).getName() + " " + ic.getCount();
				} else {
					str[i] = container.getItems().get(i).getName() + " " + container.getItems().get(i).getWeight();
				}

			}
			itemLists[1].GenList(str);
		} else {
			itemLists[1].GenList(null);
		}
		weightValues[0].setString("weight:" + container.getContainedWeight()+"/"+container.getMaxWeight());
		weightValues[1].setString("weight:" + player.getInventory().getWeight());
	}

	@Override
	public void initialize(int[] textures, Callback callback) {
		// TODO Auto-generated method stub
		// 0 is font
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint
		window = new Window(new Vec2f(-3, -14.4F), new Vec2f(6, 13.4F), textures[1], true);

		itemLists = new List[2];
		// build inventory left
		itemLists[0] = new List(new Vec2f(3, -14.3F), 16, textures[5], textures[4], this);
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
		buttons[1] = new Button(new Vec2f(0.0F, 2.0F), new Vec2f(6, 1.8F), textures[2], this, "take", 2, 1);
		buttons[2] = new Button(new Vec2f(0.0F, 4.0F), new Vec2f(6, 1.8F), textures[2], this, "put", 1, 1);
		// add buttons to move things to and from the container
		for (int i = 0; i < 3; i++) {
			window.add(buttons[i]);
		}
		// add button to exit

		this.callback = callback;

		weightValues = new Text[2];

		weightValues[0] = new Text(new Vec2f(-8.4F, -0.5F), "encumbrance", 0.7F, textures[4]);
		weightValues[1] = new Text(new Vec2f(8, -0.5F), "encumbrance", 0.7F, textures[4]);

		for (int i = 0; i < 2; i++) {
			window.add(weightValues[i]);
		}
		
		Text text=new Text(new Vec2f(-6.0F, -0.5F), "hold shift to move stacks", 0.7F, textures[4]);
		window.add(text);
		resetList();
	}

	@Override
	public void Callback() {
		// TODO Auto-generated method stub

	}

}
