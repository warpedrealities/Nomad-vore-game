package gui.lists;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL20;

import shared.Vec2f;

public class ColouredList extends List {

	private float [][] colourList;
	private int [] stringColours;
	
	public ColouredList(Vec2f position, int slots, int texture, int tint, shared.Callback callback,
			float width,float [][]colourList) {
		super(position, slots, texture, tint, callback, width);
		this.colourList=colourList;
	}
	
	public void setStringColour(int [] stringColours)
	{
		this.stringColours=stringColours;
	}
	
	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		// TODO Auto-generated method stub
		if (m_frame != null) {
			m_frame.Draw(buffer, matrixloc);
		}
		for (int i = 0; i < m_fonts.length; i++) {
			if (i + m_offset == m_select) {
				GL20.glUniform4f(m_tint, 0, 1, 0.5F, 1);
				m_fonts[i].Draw(buffer, matrixloc);
			} else {
				if (stringColours.length>i)
				{
					float [] colours=colourList[stringColours[i]];
					GL20.glUniform4f(m_tint, colours[0], colours[1], colours[2], 1);	
				}
				else
				{
					GL20.glUniform4f(m_tint, 1,1,1, 1);			
				}
				m_fonts[i].Draw(buffer, matrixloc);
			}

		}
		slider.Draw(buffer, matrixloc);
	}


}
