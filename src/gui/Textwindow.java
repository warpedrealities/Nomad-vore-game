package gui;

import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL20;

import shared.NinePatch;
import shared.Vec2f;

import font.NuFont;
import input.Keyboard;

public class Textwindow extends GUIBase 
{
	NinePatch m_patch;
	NuFont m_font;
	int m_colourID;
	Vec2f m_pos,m_size;
	boolean m_active,m_uppercase;
	public String m_string;
	public String m_emptystring;
	private double keyClock;
	public Textwindow (int back, Vec2f pos, Vec2f size,int colour, String emptystring)
	{
		m_uppercase=false;
		m_pos=pos;
		m_size=size;
		m_active=false;
	
		m_colourID=colour;
		m_string="";
		m_emptystring=emptystring;
		m_font=new NuFont(new Vec2f(pos.x+0.25F,pos.y+(size.y*0.75F)),32,1.0F);
		m_patch=new NinePatch(pos, size.x, size.y, back);
		m_font.setString(m_emptystring);
		keyClock=0;
	}
	public Textwindow (int []texid, Vec2f pos, Vec2f size,int colour, String emptystring)
	{
		m_uppercase=false;
		m_pos=pos;
		m_size=size;
		m_active=false;
		
		m_colourID=colour;
		m_string="";
		m_emptystring=emptystring;
		m_font=new NuFont(pos,20,1.0F);
		m_font.AdjustPos(new Vec2f(pos.x+(2),pos.y+(2)));
		m_patch=new NinePatch(pos, size.x, size.y, texid[1]);
		m_font.setString(m_emptystring);
	}
	public Textwindow (int []texid,  Vec2f pos, Vec2f size,int colour, String emptystring, String Fullstring)
	{
		m_uppercase=false;
		m_pos=pos;
		m_size=size;
		m_active=false;
	
		m_colourID=colour;
		m_string=Fullstring;
		m_emptystring=emptystring;
		m_font=new NuFont(new Vec2f(pos.x+(0.25F),pos.y+(0.1F)),20,0.5F);
		m_patch=new NinePatch(pos, size.x, size.y, texid[1]);
		m_font.setString(m_emptystring);
	}	
	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub
		if (m_active)
		{
			//if active, poll for character messages from the keyboard
			if (Keyboard.getInstance().next())
			{
				char cha=(char)Keyboard.getInstance().readChar();
				m_string=m_string+cha;
				
				amend();
			}
			
			if (keyClock<=0 && Keyboard.isKeyDown(GLFW.GLFW_KEY_BACKSPACE) && m_string.length()>0)
			{
				m_string=m_string.substring(0, m_string.length()-1);
				amend();
				keyClock=0.25F;
			}
		}
		if (keyClock>0)
		{
			keyClock-=DT;
		}
	}
	
	void amend()
	{
		if (m_string.length()>0)
		{
			m_font.setString(m_string);
		}
		else
		{
			m_font.setString(m_emptystring);
		}
	}
	@Override
	public boolean ClickEvent(Vec2f pos) {
		// TODO Auto-generated method stub
		//check 
		if (pos.x>m_pos.x && pos.x<m_pos.x+m_size.x)
		{
			if (pos.y>m_pos.y && pos.y<m_pos.y+m_size.y)
			{
				if (m_active==true)
				{
					m_active=false;
					Keyboard.getInstance().disableCharInput();
				}
				else
				{
					m_active=true;
					Keyboard.getInstance().enableCharInput();

				}
				return true;
			}
		}		
		return false;
	}
	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {

		if (m_active==true)
		{
			GL20.glUniform4f(m_colourID,1.0F,1.0F,0.5F, 1);
			m_patch.Draw(buffer, matrixloc);	
			m_font.Draw(buffer, matrixloc);	
			GL20.glUniform4f(m_colourID,1.0F,1.0F,1.0F, 1);		
		}
		else
		{
			m_patch.Draw(buffer, matrixloc);
			m_font.Draw(buffer, matrixloc);			
		}

		
	}
	@Override
	public void discard() {
		// TODO Auto-generated method stub
		m_font.Discard();
		m_patch.Discard();
	}
	@Override
	public void AdjustPos(Vec2f p) {
		// TODO Auto-generated method stub

		m_pos.x+=p.x;
		m_pos.y+=p.y;
		Vec2f pp=new Vec2f(m_pos.x+0.25F, m_pos.y+(m_size.y/2)-0.25F);	
		m_font.AdjustPos(pp);	
		m_patch.AdjustPos(m_pos);	
	}

	
	
	
}
