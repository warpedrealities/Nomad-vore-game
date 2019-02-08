package nomad.playerScreens.journal;

import java.nio.FloatBuffer;

import input.MouseHook;
import nomad.universe.Universe;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;

public class JournalScreen extends Screen {

	private JournalSystem journal;

	public JournalScreen() {
		journal = Universe.getInstance().journal;
		journal.sort();

	}

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void discard(MouseHook mouse) {
		// TODO Auto-generated method stub

	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start(MouseHook hook) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize(int[] textures, Callback callback) {

		// 0 is font
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint
	}
}
