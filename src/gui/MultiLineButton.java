package gui;

import java.nio.FloatBuffer;

import font.NuFont;
import input.MouseHook;
import shared.MyListener;
import shared.NinePatch;
import shared.NinePatch_Alternate;
import shared.Vec2f;

public class MultiLineButton extends GUIBase {

	private NinePatch_Alternate m_patch;
	private String[] m_strings;
	private NuFont[] m_fonts;
	private int m_textures[];
	private boolean m_alternate;

	// encapsulation of patch, means gui elements don't inherit boxes
	private Vec2f m_pos;
	private Vec2f m_size;
	private int m_ID;
	private boolean m_active;
	private MouseHook mouse;
	private boolean mouseOver;
	protected float clock;
	
	public MultiLineButton(Vec2f pos, Vec2f size, int texture, MyListener listener, String string, int ID,
			int altTexture) {

		m_textures = new int[2];
		m_textures[0] = texture;
		m_textures[1] = altTexture;
		m_active = true;
		m_pos = pos;
		m_size = size;
		m_listener = listener;
		m_strings = new String[2];
		// build ninepatch
		m_patch = new NinePatch_Alternate(pos, size.x, size.y, texture);
		m_ID = ID;
		Vec2f p = new Vec2f(pos.x, pos.y);
		float sy = (size.y) * 0.65F;
		m_fonts = new NuFont[2];
		m_fonts[0] = new NuFont(new Vec2f(pos.x + (0.4F * size.y), pos.y + sy + 0.5F), 64, 0.6F);
		m_fonts[1] = new NuFont(new Vec2f(pos.x + (0.4F * size.y), pos.y + sy - 0.2F), 64, 0.6F);
		buildStrings(string);

	}

	public void setMouse(MouseHook mouse) {
		this.mouse = mouse;
	}

	public void setAlt(boolean alt) {
		m_alternate = alt;
		if (m_alternate == true) {
			m_patch.setTexture(m_textures[1]);
		} else {
			m_patch.setTexture(m_textures[0]);
		}
	}

	public void buildStrings(String str) {
		int index = str.indexOf(' ', str.length() / 2);
		if (index!=-1)
		{
			m_fonts[0].setString(str.substring(0, index));
			m_fonts[1].setString(str.substring(index, str.length()));			
		}
		else
		{
			m_fonts[0].setString(str);
			m_fonts[1].setString("");
		}

	}

	public void setString(String string) {
		buildStrings(string);
	}

	public void setActive(boolean a) {
		m_active = a;
	}

	@Override
	public void update(float DT) {
		Vec2f pos = mouse.getPosition();
		if (pos.x > m_pos.x && pos.x < m_pos.x + m_size.x) {
			if (pos.y > m_pos.y && pos.y < m_pos.y + m_size.y) {
				mouseOver = true;
			} else {
				mouseOver = false;
			}
		} else {
			mouseOver = false;
		}
		if (clock>0)
		{
			clock-=DT;
			if (clock<=0)
			{
				clock=0;
				m_patch.toggle();
				m_listener.ButtonCallback(m_ID, m_pos);		
			}
		}
		if (clock<0)
		{
			clock+=DT;
			if (clock>=0)
			{
				clock=0;
			}
		}
	}

	public boolean isMouseOver() {
		return mouseOver;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		if (m_active == true) {
			m_patch.Draw(buffer, matrixloc);
			// draw text
			m_fonts[0].Draw(buffer, matrixloc);
			m_fonts[1].Draw(buffer, matrixloc);
		}

	}

	@Override
	public boolean ClickEvent(Vec2f pos) {
		// check inside
		if (m_active == false || clock<0) {
			return false;
		}
		if (pos.x > m_pos.x && pos.x < m_pos.x + m_size.x) {
			if (pos.y > m_pos.y && pos.y < m_pos.y + m_size.y) {
				clock=0.02F;
				m_patch.toggle();
				// return true;
			}
		}

		return false;
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		m_patch.Discard();
		m_fonts[0].Discard();
		m_fonts[1].Discard();
	}

	public void AdjustPos(Vec2f p) {
		m_pos.x += p.x;
		m_pos.y += p.y;
		/*
		 * m_pos.x+=p.x; m_pos.y+=p.y; Vec2f pp=new Vec2f(m_pos.x+0.25F,
		 * m_pos.y+(m_size.y/2)-0.25F);
		 */
		m_fonts[0].AdjustPos(p);
		m_fonts[1].AdjustPos(p);
		m_patch.AdjustPos(m_pos);
	}
}
