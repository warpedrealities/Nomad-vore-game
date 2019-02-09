package shared;

import java.util.EventListener;

public interface ButtonListener extends EventListener {

	public void ButtonCallback(int ID, Vec2f p);

}
