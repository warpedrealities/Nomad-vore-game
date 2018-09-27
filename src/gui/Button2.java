package gui;

import shared.MyListener;
import shared.Vec2f;

public class Button2 extends Button {

	int m_textures[];
	boolean m_alternate;

	public Button2(Vec2f pos, Vec2f size, int texture, MyListener listener, String string, int ID, int altexture,
			float s) {
		super(pos, size, texture, listener, string, ID, s);
		m_textures = new int[2];
		m_textures[0] = texture;
		m_textures[1] = altexture;
	}

	public boolean getAlt() {
		return m_alternate;
	}

	public void setAlt(boolean alt) {
		m_alternate = alt;
		if (m_alternate == true) {
			m_patch.setTexture(m_textures[1]);
		} else {
			m_patch.setTexture(m_textures[0]);
		}
	}

	public boolean getVisible() {
		return m_active;
	}
}
