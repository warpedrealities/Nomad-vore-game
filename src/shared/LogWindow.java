package shared;

import java.awt.Font;
import java.io.InputStream;
import java.nio.FloatBuffer;

import font.NuFont;
import gui.GUIBase;



public class LogWindow extends GUIBase {

	NuFont []m_fonts;
	Vec2f m_pos;
	int m_maxcount;
	int m_length;
	int m_texID;
	//TrueTypeFont m_font;
	
	public LogWindow(Vec2f pos, int texID, int count, int length )
	{
		m_pos=pos;
		m_fonts=new NuFont[count];
		m_maxcount=count;
		m_length=length;
		m_texID=texID;
	}
	
	public void Draw(FloatBuffer buffer, int matrixloc)
	{
		for (int i=0;i<m_maxcount;i++)
		{
			if (m_fonts[i]!=null)
			{
				m_fonts[i].Draw(buffer,matrixloc);
				//draw the string to the screen, offset from m_pos
				//	m_font.drawString(m_pos.x, m_pos.y+(20*i), m_strings[i],Color.white);
			}			
		}	
	}
	
	int SplitString(String string)
	{
		if (string.length()<=m_length)
		{
			return m_length;
		}
		else
		{
			for (int i=m_length;i>0;i--)
			{
				byte []b=string.getBytes();
				if (b[i]==' ')
				{
					return i;
				}
				
			}
		}
		return 0;
	}
	
	public void AddString(String string)
	{
		int d=SplitString(string);
		if (d==m_length)
		{
			pushString(string);	
		}
		else
		{
			String substr0=string.substring(0, d);
			String substr1=string.substring(d, string.length());
			pushString(substr0);
			pushString(substr1);
		}
	}

	void pushString(String string)
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
		m_fonts[0]=new NuFont(m_pos, 64, 0.5F);
		m_fonts[0].setString(string);			
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
	public void discard() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
