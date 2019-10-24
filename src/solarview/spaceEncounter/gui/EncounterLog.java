package solarview.spaceEncounter.gui;

import java.nio.FloatBuffer;

import gui.GUIBase;
import gui.Text;
import shared.Vec2f;

public class EncounterLog extends GUIBase {

	private Text[] texts;
	private String[] strings;
	private Vec2f position;

	public EncounterLog(Vec2f p) {
		texts = new Text[10];
		strings = new String[10];
		position = p;
		for (int i = 0; i < 10; i++) {
			texts[i] = new Text(new Vec2f(position.x + 0.2F, position.y + 0.5F - ((i) * 0.8F)), "", 0.6F,
					0);
			strings[i] = "";
		}
	}

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean ClickEvent(Vec2f pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		for (int i = 0; i < texts.length; i++) {
			texts[i].Draw(buffer, matrixloc);
		}
	}

	@Override
	public void discard() {

		for (int i = 0; i < 3; i++) {
			texts[i].discard();
		}
	}

	@Override
	public void AdjustPos(Vec2f p) {
		// TODO Auto-generated method stub

	}

	public void drawText(String text) {

		for (int i = 8; i >= 0; i--) {
			strings[i + 1] = strings[i];
		}
		strings[0] = text;

		for (int i = 0; i < 10; i++) {
			texts[i].setString(strings[i]);
		}
	}

}
