package gui;

import java.nio.FloatBuffer;


import org.lwjgl.opengl.GL20;

import shared.Vec2f;

import font.NuFont;

public class TextColoured extends GUIBase {

	NuFont m_font;
	Vec2f m_pos;
	float [] textColour;
	int m_tint;
	public TextColoured(Vec2f p, String string, int tint)
	{
		m_tint=tint;
		textColour=new float[4];
		textColour[3]=1;
		m_pos=p;
		m_font=new NuFont(new Vec2f(p.x,p.y+0.5F), 32, 1);
		m_font.setString(string);
	}
	public TextColoured(Vec2f p, String string, float s, int tint)
	{
		m_tint=tint;
		m_pos=p;
		m_font=new NuFont(new Vec2f(p.x,p.y+0.5F), 128, s);
		m_font.setString(string);
		textColour=new float[4];
		textColour[3]=1;
	}	
	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub
		
	}

	public void setTint(float r, float g, float b)
	{
		textColour[0]=r;
		textColour[1]=g;
		textColour[2]=b;
	}
	
	@Override
	public boolean ClickEvent(Vec2f pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		
			GL20.glUniform4f(m_tint,textColour[0],textColour[1],textColour[2],1);
			m_font.Draw(buffer,matrixloc);
			GL20.glUniform4f(m_tint,1,1,1,1);
	}

	@Override
	public void discard() {

		m_font.Discard();
	}

	public void setString(String string)
	{
		m_font.setString(string);
	}
	@Override
	public void AdjustPos(Vec2f p) {
		m_pos.x=m_pos.x+p.x;
		m_pos.y=m_pos.y+p.y;
		m_font.AdjustPos(m_pos);
	}

}
