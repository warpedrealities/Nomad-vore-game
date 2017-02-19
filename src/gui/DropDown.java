package gui;

import font.NuFont;
import input.Keyboard;
import input.MouseHook;

import java.nio.FloatBuffer;


import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL20;

import shared.MyListener;
import shared.NinePatch;
import shared.Vec2f;
import vmo.Game;

public class DropDown extends GUIBase{

	NinePatch m_ninepatch;
	Vec2f m_pos;
	Vec2f m_size;
	NuFont m_font[];
	String m_strings[];
	boolean m_visible;
	boolean m_mouseIn;
	int m_select;
	MouseHook m_hook;
	int m_tintID;
	float m_initialize;

	public DropDown (int background, Vec2f pos, Vec2f size,MyListener listener,int colour)
	{	
		m_tintID=colour;
		m_pos=pos;
		m_size=size;
		//build ninepatch
		m_ninepatch=new NinePatch(pos, size.x*Game.sceneManager.getConfig().getTextscale(), size.y, background);
		//m_ninepatch.reGen(size.x*Game.sceneManager.getConfig().getTextscale(), size.y*2);
		m_listener=listener;
		m_font=new NuFont[9];
		for (int i=0;i<9;i++)
		{
			m_font[i]=new NuFont(new Vec2f(pos.x+0.5F,pos.y+(i*0.8F)+1.5F), 256, 0.8F);
			m_font[i].setString("text");
		}
		//build fonts
		m_visible=true;
	}

	public void setVisible(boolean visible)
	{
		m_visible=visible;
		m_mouseIn=false;
		m_initialize+=0.3F;
	}
	
	public void BuildFonts(String str[])
	{
		int size=str.length;
		if (size<4)
		{
			size=4;
		}
		m_size.y=size*1.0F;
		m_ninepatch.reGen(m_size.x, m_size.y);
		m_strings=str;
		for (int i=0;i<9;i++)
		{
			if (i<m_strings.length)
			{
				if (m_strings[i]!=null)
				{
					m_font[i].setString(str[i]);			
				}
				else
				{
					m_font[i].setString("");	
				}			
			}
			else
			{
				m_font[i].setString("");	
			}

		}
	}

	public boolean getVisible()
	{
		return m_visible;
	}
	
	public void CleanFonts()
	{
		
	}

	public void setHook(MouseHook hook)
	{
		m_hook=hook;
	}
	public int getSelect()
	{
		return m_select;
	}
	
	@Override
	public void update(float DT) 
	{		
		if (m_visible==true)
		{
			if (m_initialize>0)
			{
				m_initialize-=DT;
			}
			Vec2f pos=m_hook.getPosition();
			//make sure the mouse pointer is inside the dropdown
			if (pos.x>m_pos.x && pos.x<m_pos.x+m_size.x &&
				pos.y>m_pos.y && pos.y<m_pos.y+m_size.y)
			{
				m_mouseIn=true;
				//check position and highlight that font
				float y=pos.y-m_pos.y-0.5F;
				y=y/0.8F;
				if (y<m_strings.length)
				{
					m_select=(int)y;
				}
			}
			else
			{
				keyboardControl();
				if (m_mouseIn==true)
				{
					m_visible=false;
					m_mouseIn=false;
				}
			}

		}

	}
	
	private void keyboardControl()
	{
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_1))
		{
			m_select=0;
			m_listener.ButtonCallback(12, null);
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_2))
		{
			m_select=1;
			m_listener.ButtonCallback(12, null);
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_3))
		{
			m_select=2;
			m_listener.ButtonCallback(12, null);
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_4))
		{
			m_select=3;
			m_listener.ButtonCallback(12, null);
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_5))
		{
			m_select=4;
			m_listener.ButtonCallback(12, null);
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_6))
		{
			m_select=5;
			m_listener.ButtonCallback(12, null);
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_7))
		{
			m_select=6;
			m_listener.ButtonCallback(12, null);
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_8))
		{
			m_select=7;
			m_listener.ButtonCallback(12, null);
		}		
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_9))
		{
			m_select=8;
			m_listener.ButtonCallback(12, null);
		}		
	}

	
	@Override
	public boolean ClickEvent(Vec2f pos) 
	{
		if (m_visible==true && m_initialize<=0)
		{
			if (m_select<m_font.length && m_strings[m_select]!=null)
			{
				m_listener.ButtonCallback(12, pos);
				return true;
			}
	
		}
		return false;
	}



	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) 
	{
		if (m_visible==true)
		{
			m_ninepatch.Draw(buffer, matrixloc);
			
			for (int i=0;i<m_font.length;i++)
			{
				
				if (i==m_select)
				{
					GL20.glUniform4f(m_tintID,1,1,0.5F,1);
					m_font[i].Draw(buffer, matrixloc);	
					GL20.glUniform4f(m_tintID,1,1,1,1);
				}
				else
				{
					m_font[i].Draw(buffer, matrixloc);		
				}

			}
			
		}

	}




	@Override
	public void discard() 
	{

		for (int i=0;i<m_font.length;i++)
		{
			m_font[i].Discard();
		}
		m_ninepatch.Discard();	
	}




	@Override
	public void AdjustPos(Vec2f p) 
	{
		m_pos.x=p.x;
		m_pos.y=p.y;

		for (int i=0;i<m_font.length;i++)
		{
			m_font[i].AdjustPos(p);			
		}

		m_ninepatch.AdjustPos(m_pos);	
	}
	
	
	
}
