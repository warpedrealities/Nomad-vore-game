package gui;

import java.nio.FloatBuffer;

import shared.NinePatch;
import shared.Vec2f;

public class UI_Popup extends GUIBase {

	float time;
	float delay;
	MultiLineText textArea;
	Window window;

	public UI_Popup(Vec2f p, Vec2f size, int texture) {
		window = new Window(p, size, texture, true);
		textArea = new MultiLineText(new Vec2f(0.5F, size.y - 0.5F), 64, 100, 0.7F);
		window.add(textArea);

	}

	@Override
	public void update(float DT) {
		if (time > 0) {
			time -= DT;
		}
		if (delay > 0) {
			delay -= DT;
		}

	}

	@Override
	public boolean ClickEvent(Vec2f pos) {

		if (pos.x > window.getPosition().x && pos.x < window.getPosition().x + window.getSize().x) {
			if (pos.y > window.getPosition().y && pos.y < window.getPosition().y + window.getSize().y) {
				if (delay <= 0) {
					time = 0;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {

		if (time > 0) {
			window.Draw(buffer, matrixloc);
		}
	}

	@Override
	public void discard() {

		window.discard();

	}

	@Override
	public void AdjustPos(Vec2f p) {

		window.AdjustPos(p);
	}

	public void setString(String text) {
		textArea.addText(text);
		time += text.length() * 0.1F;
		delay = 1;
	}

}
