package view;

import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import actor.player.Player;
import gui.Button4Colour;
import gui.Window;
import gui.lists.List;
import input.Keyboard;
import input.MouseHook;
import nomad.universe.Universe;
import nomad.universe.actionBar.Action;
import nomad.universe.actionBar.Action.ActionBarType;
import nomad.universe.actionBar.ActionBarData;
import shared.Callback;
import shared.Screen;
import shared.Tools;
import shared.Vec2f;
import view.ui.ActionBarButtonState;

public class ActionBarSelector extends Screen implements Callback {

	MouseHook m_hook;
	ModelController_Int callback;
	Player player;
	List moveList;
	List itemList;
	float clock;
	String[] strings;
	Window window;
	private int[] localTextureIds;
	private Button4Colour[] buttons;
	private ActionBarData actionBarData;
	private ActionBarButtonState actionBarButtonState;

	public ActionBarSelector(int frame, int listBack, int button, int buttonalt, Player player, int tint,
			ModelController_Int callback) {
		actionBarData = Universe.getInstance().getActionBarData();
		actionBarButtonState = new ActionBarButtonState(Universe.getInstance().getPlayer());
		this.callback = callback;
		this.player = player;
		this.clock = 1;
		moveList = new List(new Vec2f(-16, 0.0F), 16, listBack, tint, this);
		itemList = new List(new Vec2f(-16, -16.0F), 16, listBack, tint, this);
		strings = new String[this.player.getMoveCount() - 1];
		for (int i = 0; i < strings.length; i++) {
			strings[i] = this.player.getMove(i + 1).getMoveName();
		}
		moveList.GenList(strings);

		strings = new String[this.player.getInventory().getItems().size()];
		for (int i = 0; i < strings.length; i++) {
			strings[i] = this.player.getInventory().getItem(i).getItem().getName();
		}
		itemList.GenList(strings);
		window = new Window(new Vec2f(2, -18), new Vec2f(18, 34), frame, true);
		setupTextures();
		buttons = new Button4Colour[16];
		for (int i = 0; i < 8; i++) {
			buttons[i] = new Button4Colour(new Vec2f(0, 32 - (i * 1.8F)), new Vec2f(12, 1.8F), localTextureIds, this,
					"",
					1000 + i, 1.0F);
			buttons[i + 8] = new Button4Colour(new Vec2f(0, 15 - (i * 1.8F)), new Vec2f(12, 1.8F), localTextureIds,
					this,
					"", 1010 + i, 1.0F);
			window.add(buttons[i]);
			window.add(buttons[i + 8]);
		}
		reset();

	}

	public void reset() {
		for (int i = 0; i < 8; i++) {
			Action action = actionBarData.getAction(i);
			actionBarButtonState.calculate(action, buttons[i], false);
			actionBarButtonState.calculate(action, buttons[i + 8], false);
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

	@Override
	public void update(float DT) {
		moveList.update(DT);
		itemList.update(DT);
		window.update(DT);
		if (clock > 0) {
			clock -= DT;
		} else if (Keyboard.isKeyDown(GLFW.GLFW_KEY_F2)) {
			ViewScene.m_interface.UpdateInfo();
			this.callback.Remove();
		}
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		moveList.Draw(buffer, matrixloc);
		itemList.Draw(buffer, matrixloc);
		window.Draw(buffer, matrixloc);
	}

	@Override
	public void discard(MouseHook mouse) {
		mouse.Remove(itemList);
		mouse.Remove(moveList);
		mouse.Remove(window);
		moveList.discard();
		itemList.discard();
		window.discard();
		for (int i = 0; i < localTextureIds.length; i++) {
			GL11.glDeleteTextures(localTextureIds[i]);
		}
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		if (ID >= 1000 && ID < 1010 && Universe.getInstance().getPlayer().getMoveCount() > moveList.getSelect()) {
			String ability = Universe.getInstance().getPlayer().getMove(moveList.getSelect() + 1).getMoveName();
			Action action = actionBarData.getAction(ID - 1000);
			action.setActive(true);
			action.setType(ActionBarType.ABILITY);
			action.setActionName(ability);
			reset();
		}
		if (ID >= 1010 && Universe.getInstance().getPlayer().getInventory().getNumItems() > itemList.getSelect()) {
			String item = Universe.getInstance().getPlayer().getInventory().getItem(itemList.getSelect()).getItem()
					.getName();
			Action action = actionBarData.getAction(ID - 1010);
			action.setActive(true);
			action.setType(ActionBarType.ITEM);
			action.setActionName(item);
			reset();
		}

	}

	@Override
	public void start(MouseHook hook) {
		m_hook = hook;
		hook.Register(moveList);
		hook.Register(itemList);
		hook.Register(window);
	}

	@Override
	public void Callback() {

	}

}
