package nomad.playerScreens.journal;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import gui.Button;
import gui.Window;
import gui.lists.ColouredList;
import input.MouseHook;
import nomad.universe.Universe;
import shared.ButtonListener;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;
import vmo.Game;

public class JournalScreen extends Screen implements ButtonListener, Callback {

	private JournalSystem journal;
	private Window window;
	private Callback callback;
	private ColouredList list;

	public JournalScreen() {
		journal = Universe.getInstance().journal;
		journal.sort();

	}

	@Override
	public void update(float DT) {
		window.update(DT);
		list.update(DT);
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		window.Draw(buffer, matrixloc);
		list.Draw(buffer, matrixloc);
	}

	@Override
	public void discard(MouseHook mouse) {
		// TODO Auto-generated method stub
		window.discard();
		list.discard();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub
		switch (ID) {
		case 0:
			callback.Callback();
			break;
		}
	}

	@Override
	public void start(MouseHook hook) {
		// TODO Auto-generated method stub
		hook.Register(window);
	}

	@Override
	public void initialize(int[] textures, Callback callback) {

		// 0 is font
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint
		// 5 is list

		window = new Window(new Vec2f(12, -16), new Vec2f(8, 32), textures[1], true);
		Button button = new Button(new Vec2f(0.1F, 0.1F), new Vec2f(7.8F, 2), textures[2], this, "exit", 0);
		this.callback = callback;
		window.add(button);
		float[][] colours = { { 1, 1, 0.8F }, { 1, 1, 0 } };
		list = new ColouredList(new Vec2f(-20, -16), 39, textures[5], textures[4], this, 32, colours);
		resetList();
	}

	private void resetList() {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder();
		ArrayList<String> strings = new ArrayList<String>();
		java.util.List<JournalEntry> entries = journal.getEntryList();
		for (int i = 0; i < entries.size(); i++) {
			JournalEntry entry = entries.get(i);
			strings.add(entry.getTitle());
			try (Scanner scanner = new Scanner(entry.getText())) {
				int lengthlimit = (int) (120.0F / Game.sceneManager.getConfig().getTextWidth());
				while (scanner.hasNext()) {
					String str = scanner.next();
					if (builder.length() + str.length() > lengthlimit) {
						strings.add(builder.toString());
						builder = new StringBuilder();

					}
					if (str.indexOf("LBREAK") != -1) {
						if (str.length() > "LBREAK".length()) {
							str = str.replace("LBREAK", "");
							builder.append(str);
							builder.append(" ");
						}
						strings.add(builder.toString());
						builder = new StringBuilder();
					} else {
						builder.append(str);
						builder.append(" ");
					}
				}
			}
		}
		strings.add(builder.toString());
		list.GenList(strings.stream().toArray(String[]::new));
	}

	@Override
	public void Callback() {
		// TODO Auto-generated method stub

	}
}
