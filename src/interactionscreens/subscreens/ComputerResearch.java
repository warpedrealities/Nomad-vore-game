package interactionscreens.subscreens;

import java.nio.FloatBuffer;
import java.util.Iterator;

import actorRPG.player.Player_RPG;
import gui.Button;
import gui.Window;
import gui.lists.List;
import input.MouseHook;
import nomad.universe.Universe;
import playerscreens.Popup;
import research.Encyclopedia;
import research.Research;
import shared.ButtonListener;
import shared.Callback;
import shared.Vec2f;

public class ComputerResearch implements Callback, ButtonListener {

	private Callback callback;
	private Window window;
	private List dataList;
	private List researchList;
	private String[] researchStrings;
	private Popup popup;
	private float clock;

	public ComputerResearch(int[] textures, Callback callback) {
		// TODO Auto-generated method stub
		// 0 is font
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint

		window = new Window(new Vec2f(-20, -11), new Vec2f(5, 10), textures[1], true);
		Button button = new Button(new Vec2f(0.5F, 0.5F), new Vec2f(4, 1.8F), textures[2], this, "research", 3, 1);
		window.add(button);

		researchList = new List(new Vec2f(-15, -16), 18, textures[5], textures[4], this, 15, true);
		genList();
		this.callback = callback;

		popup = new Popup(textures[1], new Vec2f(-18.5F, 4));

	}

	private void genList() {
		Encyclopedia e = Universe.getInstance().getPlayer().getEncyclopedia();
		int count = e.getResearchList().size();
		if (count > 0) {
			String str[] = new String[count];

			Iterator<String> it = e.getResearchList().keySet().iterator();

			int index = 0;
			while (it.hasNext()) {
				str[index] = it.next();
				index++;
			}
			researchList.GenList(str);
			researchStrings = str;
		} else {
			researchList.GenList(null);
		}

	}

	public void update(float DT) {
		if (clock > 0) {
			clock -= DT;
		}
		window.update(DT);
		popup.update(DT);
		researchList.update(DT);
	}

	@Override
	public void Callback() {
		// TODO Auto-generated method stub

	}

	public void draw(FloatBuffer buffer, int matrixloc) {

		window.Draw(buffer, matrixloc);
		researchList.Draw(buffer, matrixloc);

		popup.Draw(buffer, matrixloc);
	}

	public void start(MouseHook hook) {
		hook.Register(window);
		hook.Register(researchList);
	}

	public void discard(MouseHook mouse) {
		mouse.Remove(window);
		mouse.Remove(researchList);
		mouse.Remove(popup);
		window.discard();
		researchList.discard();
		mouse.Remove(popup);
		popup.discard();
	}

	private void research(String research) {

		Encyclopedia e = Universe.getInstance().getPlayer().getEncyclopedia();
		Research r = e.getResearchList().get(research);
		if (r != null) {
			for (int i = 0; i < 500; i++) {
				((Player_RPG) Universe.getInstance().getPlayer().getRPG()).update();
			}
			Universe.AddClock(500);
			callback.Callback();
			if (r.attempt((Player_RPG) Universe.getInstance().getPlayer().getRPG(), popup, e)) {
				e.getResearchList().remove(research);
			}

			genList();
		}

	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		if (ID == 3) {
			if (researchStrings != null && researchStrings.length > researchList.getSelect() && clock <= 0) {
				research(researchStrings[researchList.getSelect()]);
				clock = 0.5F;
			}
		}
	}
}
