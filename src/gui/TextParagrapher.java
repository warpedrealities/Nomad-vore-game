package gui;

import java.nio.FloatBuffer;

import font.NuFont;
import shared.Vec2f;
import vmo.Game;

public class TextParagrapher extends GUIBase {

	NuFont m_fonts[];
	int m_length;
	int rowsUsed;

	public TextParagrapher(Vec2f p, int lines, int length, float lwidth) {
		m_length = (int) ((int) length / (Game.sceneManager.getConfig().getTextscale()));
		m_fonts = new NuFont[lines];
		for (int i = 0; i < lines; i++) {
			m_fonts[i] = new NuFont(new Vec2f(p.x, p.y - ((float) i * (lwidth + 0.1F))), length, lwidth);
			m_fonts[i].setString("");
		}

	}

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub

	}

	public void addText(String text) {
		if (rowsUsed == m_fonts.length) {
			return;
		}
		// divide text
		if (text.length() < m_length) {
			m_fonts[rowsUsed].setString(text);
			rowsUsed++;
		} else {
			int index = 0;
			while (true) {
				int split = text.indexOf(" ", index + m_length - 5);
				if (split == -1) {
					break;
				}
				if (split > index + m_length) {
					split = text.indexOf(" ", index + m_length - 12);
				}
				String str = text.substring(index, split);

				m_fonts[rowsUsed].setString(str);
				rowsUsed++;
				index = split + 1;
			}
			m_fonts[rowsUsed].setString(text.substring(index, text.length()));
			rowsUsed++;
		}
	}

	public void clean() {
		for (int i = 0; i < m_fonts.length; i++) {
			m_fonts[i].setString("");
		}
	}

	@Override
	public boolean ClickEvent(Vec2f pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		// TODO Auto-generated method stub
		for (int i = 0; i < m_fonts.length; i++) {
			m_fonts[i].Draw(buffer, matrixloc);
		}
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		for (int i = 0; i < m_fonts.length; i++) {
			m_fonts[i].Discard();
		}
	}

	@Override
	public void AdjustPos(Vec2f p) {
		// TODO Auto-generated method stub
		for (int i = 0; i < m_fonts.length; i++) {
			m_fonts[i].AdjustPos(p);
		}
	}

}
