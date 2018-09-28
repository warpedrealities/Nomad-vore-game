package interactionscreens.systemScreen;

import gui.Window;
import input.MouseHook;
import shared.MyListener;

public interface SystemDisplay extends MyListener {

	void update(float dT);

	void discard(MouseHook mouse);

	void initialize(int[] textures);

	void reset();
	
	Window getWindow();

}
