package interactionscreens.subscreens;

import java.nio.FloatBuffer;
import java.util.Iterator;

import actorRPG.Actor_RPG;
import actorRPG.player.Player_RPG;
import graphics.Screen_Fade;
import gui.Button;
import gui.Window;
import gui.lists.List;
import input.MouseHook;
import nomad.universe.Universe;
import playerscreens.Popup;
import research.Encyclopedia;
import research.Research;
import shared.Callback;
import shared.MyListener;
import shared.Vec2f;

public class ComputerResearch implements Callback, MyListener {

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

			int diceroll = Universe.m_random.nextInt(20)
					+ Universe.getInstance().getPlayer().getRPG().getAttribute(Actor_RPG.SCIENCE);

			int finalroll = (diceroll + r.getRoll()) / 2;

			if (finalroll >= r.getDC()) {
				popup.setClock(10);
				if (e.addData(r.getName(), r.getGroup())) {
					popup.setText("you have successfully researched " + research
							+ " and have compiled a new entry on your findings");
				} else {
					popup.setText("you have successfully researched " + research);
				}

			} else {
				popup.setClock(10);
				popup.setText("you have failed to reach meaningful conclusions on the topic of " + research
						+ " you need more data");
			}
			e.getResearchList().remove(research);
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
