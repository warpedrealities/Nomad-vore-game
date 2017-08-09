package gui;

import java.nio.FloatBuffer;

import shared.MyListener;
import shared.NinePatch;
import shared.NinePatch_Proportional;
import shared.ProportionBar;
import shared.Vec2f;

public class ButtonProp extends Button {

	private ProportionBar m_prop;

	public ButtonProp(Vec2f pos, Vec2f size, int texture, MyListener listener, String string, int ID, int altTexture,
			int max) {
		super(pos, size, texture, listener, string, ID);
		m_prop = new ProportionBar(pos, size, max, max, 0, texture, 1);
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		if (m_active == true) {
			// m_patch.Draw(buffer, matrixloc);
			m_prop.Draw(buffer, matrixloc);
			// draw text
			// m_font.drawString(m_pos.x, m_pos.y, m_string);
			m_font.Draw(buffer, matrixloc);
		}
	}

	public void setProportion(int prop) {
		m_prop.setValue(prop);
	}

	@Override
	public void discard() {
		m_prop.discard();
		m_patch.Discard();
		m_font.Discard();

	}

}
