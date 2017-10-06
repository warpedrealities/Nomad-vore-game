package gui;

import java.nio.FloatBuffer;
import java.util.Scanner;

import font.FontSupport;
import font.NuFont;

import shared.Vec2f;
import vmo.Game;

public class MultiLineText extends GUIBase {

	NuFont m_fonts[];
	int m_length;

	public MultiLineText(Vec2f p, int lines, int length, float lwidth) {
		m_length = (int) ((int) length / (Game.sceneManager.getConfig().getTextWidth()));
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
		dirtSimpleAddText(text);
	}

	private void dirtSimpleAddText(String text) {
		clean();
		Scanner scanner = new Scanner(text);
		int index = 0;
		StringBuilder builder = new StringBuilder();
		int l = 0;
		while (scanner.hasNext() && index < m_fonts.length) {
			String str = scanner.next();
			if (str.equals("LBREAK")) {
				m_fonts[index].setString(builder.toString());
				builder = new StringBuilder();
				index++;
				l = 0;
			} else {
				if (str.length() + l > ((float)m_length) * 0.8F) {
					l = 0;
					m_fonts[index].setString(builder.toString());
					index++;
					builder = new StringBuilder();
					builder.append(str);
					builder.append(" ");
					l += str.length();
				} else {
					builder.append(str);
					builder.append(" ");
					l += str.length();
				}

			}
		}
		if (index < m_fonts.length) {
			m_fonts[index].setString(builder.toString());
		}
		scanner.close();
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
