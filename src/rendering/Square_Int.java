package rendering;

import shared.Vec2f;

public interface Square_Int {
	public void reposition(Vec2f p);

	public void repositionF(Vec2f p);

	public void setVisible(boolean visible);

	public void setFlashing(int bool);

	public void setImage(int value);
}
