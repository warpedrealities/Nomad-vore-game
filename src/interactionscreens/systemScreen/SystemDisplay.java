package interactionscreens.systemScreen;

import gui.Window;
import input.MouseHook;
import shared.ButtonListener;

public interface SystemDisplay extends ButtonListener {

	void update(float dT);

	void discard(MouseHook mouse);

	void initialize(int[] textures);

	void reset();
	
	Window getWindow();

}
