package gui;

import java.nio.FloatBuffer;

import shared.ProportionBar;
import shared.Vec2f;

import font.NuFont;

public class BarFrame extends GUIBase {

	NuFont m_fonts[];
	ProportionBar m_bar;
	boolean m_visible;

	public BarFrame(Vec2f position, String tag, int value, int max, int offset, int texture) {
		m_visible = true;
		m_fonts = new NuFont[2];
		m_fonts[0] = new NuFont(new Vec2f(position.x - 6, position.y + 1), 16, 0.8F);
		m_fonts[1] = new NuFont(new Vec2f(position.x + 4, position.y + 1), 16, 0.8F);
		m_fonts[0].setString(tag);
		m_fonts[1].setString(Integer.toString(value));
		m_bar = new ProportionBar(position, new Vec2f(16, 0.9F), value, max, offset, texture);
	}

	public void setVisible(boolean value) {
		m_visible = value;
	}

	public void setValue(int value) {
		m_fonts[1].setString(Integer.toString(value));
		m_bar.setValue(value);
	}

	public void setMax(int value) {

		m_bar.setMax(value);
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
		// TODO Auto-generated method stub
		if (m_visible == true) {
			m_bar.Draw(buffer, matrixloc);

			for (int i = 0; i < 2; i++) {
				m_fonts[i].Draw(buffer, matrixloc);
			}

		}

	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		m_bar.discard();
		for (int i = 0; i < 2; i++) {
			m_fonts[i].Discard();
		}
	}

	@Override
	public void AdjustPos(Vec2f p) {

		m_fonts[0].AdjustPos(p);
		m_fonts[1].AdjustPos(p);
		m_bar.AdjustPos(p);

	}

}
