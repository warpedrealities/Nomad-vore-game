package view.ui;

import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import actor.player.Inventory;
import actor.player.Player;
import gui.Button4Colour;
import gui.Window;
import input.Keyboard;
import input.MouseHook;
import item.Item;
import nomad.universe.Universe;
import nomad.universe.actionBar.Action;
import nomad.universe.actionBar.ActionBarData;
import shared.MyListener;
import shared.Tools;
import shared.Vec2f;

public class ActionBar implements MyListener {

	private Window window;
	private int[] localTextureIds;
	private Button4Colour[] buttons;
	private ActionBarData actionBarData;
	private ActionBarButtonState actionBarButtonState;
	private ActionBarItemHandler itemHandler;
	private int updateAction;
	private float clock = 0;

	public ActionBar(Vec2f position, int[] textureIds) {
		actionBarData = Universe.getInstance().getActionBarData();
		actionBarButtonState = new ActionBarButtonState(Universe.getInstance().getPlayer());
		itemHandler = new ActionBarItemHandler(Universe.getInstance().getPlayer());
		window = new Window(position, new Vec2f(2.5F, 17), textureIds[0], true);

		setupTextures();
		buttons = new Button4Colour[8];
		for (int i=0;i<buttons.length;i++)
		{
			buttons[i] = new Button4Colour(new Vec2f(0, 15 - (i * 2.1F)), new Vec2f(2.5F, 2), localTextureIds, this,
					"",
					1000 + i, 1.0F);
			window.add(buttons[i]);
			if (actionBarData.getAction(i).isActive())
			{
				buttons[i].setString(actionBarData.getAction(i).getActionName().substring(0, 4));
			}
		}
		updateAction = 0;
		reset();
	}

	public void reset() {
		for (int i = 0; i < 8; i++) {
			Action action = actionBarData.getAction(i);
			actionBarButtonState.calculate(action, buttons[i], true);
		}
	}

	private void setupTextures() {
		localTextureIds = new int[5];
		localTextureIds[0] = Tools.loadPNGTexture("assets/art/buttonBrightGreen.png", GL13.GL_TEXTURE0);
		localTextureIds[1] = Tools.loadPNGTexture("assets/art/buttonDarkGreen.png", GL13.GL_TEXTURE0);
		localTextureIds[2] = Tools.loadPNGTexture("assets/art/buttonWhite.png", GL13.GL_TEXTURE0);
		localTextureIds[3] = Tools.loadPNGTexture("assets/art/buttonGrey.png", GL13.GL_TEXTURE0);
		localTextureIds[4] = Tools.loadPNGTexture("assets/art/buttonDarkGrey.png", GL13.GL_TEXTURE0);
	}


	public int update(float DT) {
		clock -= DT;
		if (clock <= 0) {
			handleKeys();
		}
		window.update(DT);
		return updateAction;
	}

	public void handleKeys() {
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_1)) {
			processAction(0);
			clock = 1;
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_2)) {
			processAction(1);
			clock = 1;
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_3)) {
			processAction(2);
			clock = 1;
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_4)) {
			processAction(3);
			clock = 1;
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_5)) {
			processAction(4);
			clock = 1;
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_6)) {
			processAction(5);
			clock = 1;
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_7)) {
			processAction(6);
			clock = 1;
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_8)) {
			processAction(7);
			clock = 1;
		}
	}

	public void setUpdateAction(int value) {
		updateAction = value;
	}


	public void Draw(FloatBuffer buffer, int matrixloc) {
		window.Draw(buffer, matrixloc);
	}


	public void discard() {

		for (int i = 0; i < localTextureIds.length; i++) {
			GL11.glDeleteTextures(localTextureIds[i]);
		}
		window.discard();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		if (ID >= 1000) {
			processAction(ID - 1000);
		}
	}

	public void processAction(int index) {
		Action action = actionBarData.getAction(index);
		switch (action.getType()) {
		case ABILITY:
			setMove(action.getActionName());
			break;
		case ITEM:
			useItem(action.getActionName());
			break;
		default:
			break;
		}
	}

	private void useItem(String actionName) {
		Player player = Universe.getInstance().getPlayer();
		Item item = getItem(actionName, player.getInventory());
		if (item != null) {
			itemHandler.handleItem(item);
			updateAction = 1;
		}
	}

	private Item getItem(String actionName, Inventory inventory) {
		for (int i = 0; i < inventory.getItems().size(); i++) {
			if (inventory.getItems().get(i).getItem().getName().equals(actionName)) {

				return inventory.getItems().get(i);

			}
		}
		return null;
	}

	private void setMove(String actionName) {
		Player player = Universe.getInstance().getPlayer();
		for (int i = 0; i < player.getMoveCount(); i++) {
			if (player.getMove(i).getMoveName().equals(actionName)) {
				player.setMove(i);
				updateAction = 2;
				break;
			}
		}

	}

	public void start(MouseHook mouse) {
		mouse.Register(window);
	}

}
