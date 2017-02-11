package vmo;

import rendering.Widget;
import shared.Vec2f;

public interface Actor_RUI {

	public void Moved(Vec2f p);
	public void Moved(float x, float y);
	public void Redraw();
	public void RePosition();
	public void setWidgetValue(int value);
	public void setWidget(Widget widget);
}
