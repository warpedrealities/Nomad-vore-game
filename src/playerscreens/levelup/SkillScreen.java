package playerscreens.levelup;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import actorRPG.Actor_RPG;
import actorRPG.player.Player_RPG;
import gui.Button2;
import gui.Text;
import gui.Window;
import input.MouseHook;
import shared.MyListener;
import shared.Tools;
import shared.Vec2f;

public class SkillScreen implements MyListener {

	private Window window;
	private Text[] texts;
	private Button2[] buttons;
	private Player_RPG rpg;
	private int textures[];
	private int index = -1;

	public SkillScreen(int[] variables, MyListener listener, Player_RPG rpg) {
		this.rpg = rpg;
		window = new Window(new Vec2f(-20, -13.0F), new Vec2f(18, 29), variables[1], true);
		textures = new int[2];
		// first is square
		// 2nd is font
		textures[0] = Tools.loadPNGTexture("assets/art/ui/plus0.png", GL13.GL_TEXTURE0);
		textures[1] = Tools.loadPNGTexture("assets/art/ui/plus1.png", GL13.GL_TEXTURE0);
		buttons = new Button2[14];
		texts = new Text[14];
		for (int i = 0; i < 14; i++) {
			texts[i] = new Text(new Vec2f(0.5F, 13.5F - i), "text" + i, 0.8F, variables[4]);
			texts[i].setTint(true);
			window.add(texts[i]);
			buttons[i] = new Button2(new Vec2f(15.0F, 26.5F - (i * 2)), new Vec2f(2, 2), textures[0], this, "", 50 + i,
					textures[1], 0);
			if (!canImprove(i)) {
				buttons[i].setActive(false);
			}
			window.add(buttons[i]);
		}
		resetList();
	}

	public boolean canImprove(int index) {
		int cap = (rpg.getPlayerLevel() / 2) + 2;
		cap += AbilityCap(index) * 2;
		if (rpg.getAttribute(index + 6) >= cap) {
			return false;
		}
		return true;
	}

	private int AbilityCap(int index) {
		index -= 6;
		switch (index) {
		case Actor_RPG.MELEE:
			return rpg.getAbilityMod(Actor_RPG.STRENGTH);
		case Actor_RPG.RANGED:
			return rpg.getAbilityMod(Actor_RPG.DEXTERITY);
		case Actor_RPG.SEDUCTION:
			return rpg.getAbilityMod(Actor_RPG.CHARM);
		case Actor_RPG.DODGE:
			return rpg.getAbilityMod(Actor_RPG.AGILITY);
		case Actor_RPG.PARRY:
			return rpg.getAbilityMod(Actor_RPG.AGILITY);
		case Actor_RPG.STRUGGLE:
			return rpg.getAbilityMod(Actor_RPG.STRENGTH);
		case Actor_RPG.PLEASURE:
			return rpg.getAbilityMod(Actor_RPG.DEXTERITY);
		case Actor_RPG.PERSUADE:
			return rpg.getAbilityMod(Actor_RPG.INTELLIGENCE);
		case Actor_RPG.WILLPOWER:
			return rpg.getAbilityMod(Actor_RPG.INTELLIGENCE);
		case Actor_RPG.TECH:
			return rpg.getAbilityMod(Actor_RPG.INTELLIGENCE);
		case Actor_RPG.SCIENCE:
			return rpg.getAbilityMod(Actor_RPG.INTELLIGENCE);
		case Actor_RPG.PERCEPTION:
			return rpg.getAbilityMod(Actor_RPG.INTELLIGENCE);
		default:
			return 0;
		}
	}

	public boolean isReady() {
		return index != -1;
	}

	private void resetList() {
		for (int i = 0; i < 14; i++) {
			int skill = rpg.getAttribute(i + 6);
			if (i == index) {
				skill += 1;
				texts[i].setTint(true);
				buttons[i].setAlt(true);
			} else {
				texts[i].setTint(false);
				buttons[i].setAlt(false);
			}
			texts[i].setString(getString(i) + " " + skill);

		}
	}

	private String getString(int i) {
		switch (i) {
		case 0:
			return "parry";
		case 1:
			return "dodge";
		case 2:
			return "melee";
		case 3:
			return "ranged";
		case 4:
			return "willpower";
		case 5:
			return "seduction";
		case 6:
			return "struggle";
		case 7:
			return "pleasure";
		case 8:
			return "persuade";
		case 9:
			return "tech";
		case 10:
			return "science";
		case 11:
			return "navigation";
		case 12:
			return "gunnery";
		case 13:
			return "perception";
		}
		return null;
	}

	public void update(float dt) {
		window.update(dt);
	}

	public void draw(FloatBuffer buffer, int matrixloc) {
		window.Draw(buffer, matrixloc);
	}

	public void discard(MouseHook hook) {
		hook.Remove(window);
		window.discard();

		GL11.glDeleteTextures(textures[0]);
		GL11.glDeleteTextures(textures[1]);
	}

	public void start(MouseHook hook) {
		hook.Register(window);
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {

		if (ID >= 50 && ID <= 64) {
			if (ID - 50 == index) {
				index = -1;
			} else {
				index = ID - 50;
			}
			resetList();
		}
	}

	public int getIndex() {
		return index;
	}
}
