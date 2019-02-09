package playerscreens.levelup;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import actorRPG.player.Player_RPG;
import gui.MultiLineText;
import gui.Window;
import gui.lists.List;
import input.MouseHook;
import perks.Perk;
import perks.PerkLibrary;
import perks.PerkQualifier;
import shared.Callback;
import shared.ButtonListener;
import shared.Vec2f;

public class PerkScreen implements Callback {
	private Window window;
	private Player_RPG rpg;
	private List perkList;
	private ArrayList<Perk> availablePerks;
	private MultiLineText description;
	private int index = -1;
	public PerkScreen(int[] variables, ButtonListener listener, Player_RPG rpg) {
		window = new Window(new Vec2f(-2, -16.0F), new Vec2f(22, 7), variables[1], true);
		availablePerks = new ArrayList<Perk>();
		buildPerksList();
		description = new MultiLineText(new Vec2f(0.5F, 6.5F), 16, 72, 0.8F);
		description.addText("TEXT TEXT TEXT");
		buildList(variables);
		window.add(description);
	}

	private void buildList(int[] values) {
		// 0 is font
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint
		perkList = new List(new Vec2f(-2, -9.0F), 30, values[0], values[4], this, 22);

		String[] str = new String[availablePerks.size()];
		for (int i = 0; i < availablePerks.size(); i++) {
			str[i] = availablePerks.get(i).getName();
		}
		perkList.GenList(str);

	}

	private void buildPerksList() {
		PerkQualifier qualifier = new PerkQualifier();
		PerkLibrary instance = PerkLibrary.getInstance();
		for (int i = 0; i < instance.getPerks().size(); i++) {
			if (qualifier.perkAcquireable(PerkLibrary.getInstance().getPerks().get(i))) {
				availablePerks.add(PerkLibrary.getInstance().getPerks().get(i));
			}
		}

	}

	public void update(float dt) {
		window.update(dt);
		perkList.update(dt);
	}

	public void draw(FloatBuffer buffer, int matrixloc) {
		window.Draw(buffer, matrixloc);
		perkList.Draw(buffer, matrixloc);
	}

	public void discard(MouseHook hook) {
		hook.Remove(window);
		hook.Remove(perkList);
		window.discard();
		perkList.discard();
	}

	public void start(MouseHook hook) {
		hook.Register(window);
		hook.Register(perkList);
	}

	public boolean isReady() {
		return index != -1;
	}

	public Perk getPerk() {
		if (index < availablePerks.size()) {
			return availablePerks.get(index);
		}
		return null;
	}

	@Override
	public void Callback() {
		int d = perkList.getSelect();
		if (d < availablePerks.size()) {
			description.addText(availablePerks.get(d).getDescription());
			index = d;
		}

	}
}
