package gui;

import shared.ButtonListener;
import shared.Vec2f;

public class Button4Colour extends Button {

	int textures[];
	int colourIndex;

	public Button4Colour(Vec2f pos, Vec2f size, int[] textures, ButtonListener listener, String string, int ID,
			float s) {
		super(pos, size, textures[0], listener, string, ID, s);
		this.textures = textures;
		m_patch.setTexture(this.textures[4]);
		colourIndex = 4;
	}

	public int getColour() {
		return colourIndex;
	}

	public void setColour(int colour) {
		if (colour != colourIndex) {
			m_patch.setTexture(this.textures[colour]);
		}
		colourIndex = colour;
	}

	public boolean getVisible() {
		return m_active;
	}
}
