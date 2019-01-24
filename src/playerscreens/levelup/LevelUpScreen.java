package playerscreens.levelup;

import java.nio.FloatBuffer;

import actor.player.Player;
import actorRPG.player.Player_RPG;
import gui.Button;
import gui.Window;
import input.MouseHook;
import nomad.universe.Universe;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;

public class LevelUpScreen extends Screen implements Callback {

	private Callback callback;
	private Player player;
	private Player_RPG rpg;
	private SkillScreen skills;
	private PerkScreen perks;
	private Window window;
	public LevelUpScreen(int[] values, Callback callback) {
		player = Universe.getInstance().getPlayer();
		rpg = (Player_RPG) player.getRPG();
		this.callback = callback;
		skills = new SkillScreen(values, this, rpg);
		if ((rpg.getPlayerLevel() + 1) % 2 == 0) {
			perks = new PerkScreen(values, this, rpg);
		}
		window = new Window(new Vec2f(-20, -16.0F), new Vec2f(18, 3), values[1], true);
		Button[] buttons = new Button[2];
		buttons[0] = new Button(new Vec2f(0.5F, 0.5F), new Vec2f(8, 2), values[2], this, "exit", 0, 0.8F);
		buttons[1] = new Button(new Vec2f(9.5F, 0.5F), new Vec2f(8, 2), values[2], this, "level", 1, 0.8F);
		window.add(buttons[0]);
		window.add(buttons[1]);
	}

	@Override
	public void update(float DT) {
		skills.update(DT);
		if (perks != null) {
			perks.update(DT);
		}
		window.update(DT);
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		skills.draw(buffer, matrixloc);
		if (perks != null) {
			perks.draw(buffer, matrixloc);
		}
		window.Draw(buffer, matrixloc);
	}

	@Override
	public void discard(MouseHook mouse) {
		skills.discard(mouse);
		if (perks != null) {
			perks.discard(mouse);
		}
		mouse.Remove(window);
		window.discard();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		switch (ID) {
		case 0:
			this.callback.Callback();
			break;
		case 1:
			levelUp();

			break;
		}
	}

	private boolean canLevel() {
		if (skills.isReady()) {
			if (perks != null) {
				return perks.isReady();
			} else {
				return true;
			}
		}
		return false;
	}

	private void levelUp() {
		if (canLevel()) {
			rpg.getSkillSelection().improveSkill(skills.getIndex());
			if (perks != null) {
				rpg.levelUp(perks.getPerk());
			} else {
				rpg.levelUp(null);
			}
			this.callback.Callback();
		}
	}

	@Override
	public void start(MouseHook hook) {
		skills.start(hook);
		if (perks != null) {
			perks.start(hook);
		}
		hook.Register(window);
	}

	@Override
	public void Callback() {


	}

}
