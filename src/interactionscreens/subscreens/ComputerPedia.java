package interactionscreens.subscreens;

import java.nio.FloatBuffer;
import java.util.Iterator;

import gui.Button;
import gui.MultiLineText;
import gui.Window;
import gui.lists.List;
import input.MouseHook;
import nomad.Universe;
import research.Encyclopedia;
import research.Entry;
import shared.Callback;
import shared.MyListener;
import shared.Vec2f;

public class ComputerPedia implements Callback, MyListener {

	private List entryList;
	private Integer[] entryIndexes;
	Window window;
	int index;
	Window view;
	MultiLineText description;

	public ComputerPedia(int[] textures, Callback callback) {
		// TODO Auto-generated method stub
		// 0 is font
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint

		window = new Window(new Vec2f(-15, -16), new Vec2f(4, 5), textures[1], true);
		view = new Window(new Vec2f(-11, -16), new Vec2f(31, 32), textures[1], true);
		description = new MultiLineText(new Vec2f(0.5F, 31.5F), 76, 98, 0.8F);

		view.add(description);
		entryList = new List(new Vec2f(-20, -11), 33, textures[5], textures[4], this, 9, true);

		Button[] buttons = new Button[2];
		buttons[0] = new Button(new Vec2f(0.5F, 0.5F), new Vec2f(3, 1.8F), textures[2], this, "fore", 5, 1);
		buttons[1] = new Button(new Vec2f(0.5F, 2.5F), new Vec2f(3, 1.8F), textures[2], this, "back", 4, 1);
		// add buttons to move things to and from the container
		for (int i = 0; i < 2; i++) {
			window.add(buttons[i]);
		}

		genList();
	}

	public void update(float DT) {
		window.update(DT);
		entryList.update(DT);
	}

	public void genList() {
		Encyclopedia e = Universe.getInstance().getPlayer().getEncyclopedia();

		int count = 0;
		for (int i = 0; i < e.getEntryList().size(); i++) {
			if (e.getEntryList().get(i).isUnlocked()) {
				count++;
			}
		}
		if (count > 0) {
			entryIndexes = new Integer[count];
			String str[] = new String[count];

			int index = 0;
			for (int i = 0; i < e.getEntryList().size(); i++) {
				Entry entry = e.getEntryList().get(i);
				if (entry.isUnlocked()) {
					str[index] = entry.getEntryName();
					entryIndexes[index] = i;
					index++;
				}

			}
			entryList.GenList(str);
		} else {
			entryList.GenList(null);
		}
		if (entryIndexes != null) {
			if (index < entryIndexes.length && entryIndexes.length > 0) {
				setDescription(index);
			}
		}

	}

	private void setDescription(int index) {
		int subdex = entryIndexes[index];
		Entry e = Universe.getInstance().getPlayer().getEncyclopedia().getEntryList().get(subdex);

		description.addText(e.getText());
	}

	@Override
	public void Callback() {
		int s = entryList.getSelect();
		if (s < entryIndexes.length) {
			index = s;
			setDescription(index);
		}

	}

	public void draw(FloatBuffer buffer, int matrixloc) {

		view.Draw(buffer, matrixloc);
		window.Draw(buffer, matrixloc);
		entryList.Draw(buffer, matrixloc);

	}

	public void start(MouseHook hook) {
		hook.Register(window);
		hook.Register(entryList);
	}

	public void discard(MouseHook mouse) {
		mouse.Remove(entryList);
		mouse.Remove(window);
		entryList.discard();
		window.discard();
		view.discard();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub
		switch (ID) {
		case 4:
			// back
			if (index > 0) {
				index--;
				entryList.setSelect(index);
				setDescription(index);
			}
			break;

		case 5:
			// forwards
			if (entryIndexes != null && index < entryIndexes.length - 1) {
				index++;
				entryList.setSelect(index);
				setDescription(index);
			}
			break;

		}
	}

}
