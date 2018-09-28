package view;

import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFW;

import actor.player.Player;
import actorRPG.player.Player_RPG;
import gui.lists.List;
import input.Keyboard;
import input.MouseHook;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;

public class QuickActionSelector extends Screen implements Callback {

	MouseHook m_hook;
	ModelController_Int callback;
	Player player;
	List list;
	float clock;
	String[] strings;

	public QuickActionSelector(int frame, int button, int buttonalt, Player player, int tint,
			ModelController_Int callback) {
		this.callback = callback;
		this.player = player;
		this.clock = 1;
		list = new List(new Vec2f(-8, -8.0F), 16, frame, tint, this);
		strings = new String[this.player.getMoveCount() - 1];
		for (int i = 0; i < strings.length; i++) {
			strings[i] = this.player.getMove(i + 1).getMoveName();
		}
		list.GenList(strings);
	}

	@Override
	public void update(float DT) {
		list.update(DT);
		if (clock > 0) {
			clock -= DT;
		} else if (Keyboard.isKeyDown(GLFW.GLFW_KEY_F2)) {
			this.callback.Remove();
		}
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		list.Draw(buffer, matrixloc);
	}

	@Override
	public void discard(MouseHook mouse) {

		mouse.Remove(list);
		list.discard();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {

	}

	@Override
	public void start(MouseHook hook) {
		m_hook = hook;
		hook.Register(list);
	}

	@Override
	public void Callback() {
		Player_RPG rpg = (Player_RPG) this.player.getRPG();
		rpg.setQuickAction(strings[list.getSelect()]);
		this.callback.Remove();
	}

}
