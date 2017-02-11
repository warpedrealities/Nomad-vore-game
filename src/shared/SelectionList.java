package shared;

import java.nio.FloatBuffer;


import org.lwjgl.opengl.GL20;

import font.NuFont;
import gui.GUIBase;

public class SelectionList extends GUIBase {

	int m_tintloc;
	int m_texid;
	NuFont []m_fonts;
	Vec2f m_pos;
	int m_maxcount;
	int m_length;
	int m_selection;
	//TrueTypeFont m_font;
	
	public SelectionList(Vec2f pos, int texID, int count, int length,int tintloc )
	{
		m_selection=-1;
		m_pos=pos;
		m_fonts=new NuFont[count];
		m_maxcount=count;
		m_length=length;
		m_tintloc=tintloc;
		m_texid=texID;
	}
	
	public void Generate(String choices[])
	{
		discard();
		for (int i=0;i<choices.length;i++)
		{
			m_fonts[i]=new NuFont(new Vec2f(m_pos.x,m_pos.y+((float)i*1.0F)),m_length,1.0F);
			m_fonts[i].setString(choices[i]);
		}
		
	}
	/*
	void pushString(String string,int texid)
	{
		for (int i=m_maxcount-1;i>0;i--)
		{
			if (m_fonts[i-1]!=null)
			{	
				m_fonts[i]=m_fonts[i-1];
				//reposition all fonts
				m_fonts[i].AdjustPos(new Vec2f(m_pos.x,m_pos.y+((float)i*0.5F)));
			}
		}
		m_fonts[0]=new NuFont(m_pos, texid, 64, 0.5F);
		m_fonts[0].setString(string);			
	}
	 */
	public int getSelection()
	{
		return m_selection;
	}
	
	@Override
	public void update(float DT) {

		
	}

	@Override
	public boolean ClickEvent(Vec2f pos) {

		if (pos.x>m_pos.x && pos.x<m_pos.x+m_length)
		{
			if (pos.y>m_pos.y && pos.y<m_pos.y+m_maxcount/2)
			{
				//m_listener.ButtonCallback(m_ID, pos);
				int i=(int) (pos.y-m_pos.y);
				m_selection=i;
			}
		}		
		
		return false;	

	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {

		for (int i=0;i<m_maxcount;i++)
		{
			if (m_fonts[i]!=null)
			{
				if (i==m_selection)
				{
					//set tint to default
					GL20.glUniform4f(m_tintloc,1.0F,1.0F,0.0F, 1);				
				}

				m_fonts[i].Draw(buffer,matrixloc);
				//draw the string to the screen, offset from m_pos
				//	m_font.drawString(m_pos.x, m_pos.y+(20*i), m_strings[i],Color.white);
				if (i==m_selection)
				{
					//set tint to default
					GL20.glUniform4f(m_tintloc,1.0F,1.0F,1.0F, 1);				
				}

			}			
		}			
	}

	@Override
	public void discard() {

		for (int i=0;i<m_maxcount;i++)
		{
			if (m_fonts[i]!=null)
			{
				m_fonts[i].Discard();
			}
		}	
	}

	@Override
	public void AdjustPos(Vec2f p) {

		m_pos.x+=p.x;
		m_pos.y+=p.y;
		for (int i=0;i<m_maxcount;i++)
		{
			if (m_fonts[i]!=null)
			{
				m_fonts[i].AdjustPos(new Vec2f(m_pos.x,m_pos.y+i));
			}		
		}		
	}

}
