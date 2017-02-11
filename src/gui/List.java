package gui;

import font.NuFont;
import gui.subelements.Slider;
import input.Keyboard;

import java.nio.FloatBuffer;




import org.lwjgl.opengl.GL20;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFW;

import shared.Callback;
import shared.NinePatch;
import shared.Vec2f;

public class List extends GUIBase implements Callback {

	NinePatch m_frame;
	NuFont m_fonts[];
	int m_select;
	String m_strings[];
	float m_delay;
	int m_offset;
	int m_tint;
	Vec2f m_corner;
	Vec2f m_size;
	Callback m_callback;
	Slider slider;
	
	public List(Vec2f position, int slots, int texture,int tint,Callback callback)
	{
		m_callback=callback;
		m_tint=tint;
		m_offset=0;
		m_delay=0.2F;
		m_select=0;
		m_frame=new NinePatch(position,17,(slots*0.8F)+0.5F,texture);
		m_fonts=new NuFont[slots];
		for (int i=0;i<slots;i++)
		{
			m_fonts[i]=new NuFont(new Vec2f (position.x+0.5F,position.y+(slots*0.8F)+0.2F-((float)i*0.8F)),64,0.6F);
			m_fonts[i].setString("penny for a tale");
		}
		m_corner=new Vec2f(position.x,position.y);
		m_size=new Vec2f(17,(slots*0.8F)+0.5F);
		slider=new Slider(new Vec2f(position.x+16,position.y+0.5F+(slots*0.8F)-4),(slots*0.8F)-4,this);
	}

	public List(Vec2f position, int slots, int font, int texture,int tint,Callback callback, float width)
	{
		m_callback=callback;
		m_tint=tint;
		m_offset=0;
		m_delay=0.2F;
		m_select=0;
		m_frame=new NinePatch(position,width,(slots*0.8F)+0.5F,texture);
		m_fonts=new NuFont[slots];
		for (int i=0;i<slots;i++)
		{
			m_fonts[i]=new NuFont(new Vec2f (position.x+0.5F,position.y+(slots*0.8F)+0.2F-((float)i*0.8F)),64,0.6F);
			m_fonts[i].setString("penny for a tale");
		}
		m_corner=new Vec2f(position.x,position.y);
		m_size=new Vec2f(17,(slots*0.8F)+0.5F);
		slider=new Slider(new Vec2f(position.x+width-1,position.y+0.5F+(slots*0.8F)-4),(slots*0.8F)-4,this);
	}	
	public List(Vec2f position, int slots, int font, int texture,int tint,Callback callback, float width, boolean genbackground)
	{
		m_callback=callback;
		m_tint=tint;
		m_offset=0;
		m_delay=0.2F;
		m_select=0;
		if (genbackground==true)
		{
			m_frame=new NinePatch(position,width,(slots*0.8F)+0.5F,texture);	
		}

		m_fonts=new NuFont[slots];
		for (int i=0;i<slots;i++)
		{
			m_fonts[i]=new NuFont(new Vec2f (position.x+0.5F,position.y+(slots*0.8F)+0.2F-((float)i*0.8F)),64,0.6F);
			m_fonts[i].setString("penny for a tale");
		}
		m_corner=new Vec2f(position.x,position.y);
		m_size=new Vec2f(17,(slots*0.8F)+0.5F);
		slider=new Slider(new Vec2f(position.x+width-1,position.y+0.5F+(slots*0.8F)-4),(slots*0.8F)-4,this);
	}	
	
	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub
		if (m_delay<=0 && m_strings!=null)
		{
			if (input.Keyboard.isKeyDown(GLFW.GLFW_KEY_PAGE_DOWN ))
			{
				if (m_offset<m_strings.length-1)
				{
					m_offset++;
					GenFonts();
					m_delay=0.2F;
					slider.setIndex(m_offset);
				}

			}
			if (input.Keyboard.isKeyDown(GLFW.GLFW_KEY_PAGE_UP ))
			{
				if (m_offset>0)
				{
					m_offset--;
					GenFonts();
					m_delay=0.2F;
					slider.setIndex(m_offset);
				}
			}
			
		}
		else
		{
			m_delay-=DT;
		}
		
		slider.update(DT);
	}
	
	public void setSelect(int i)
	{
		m_select=i;
	}
	
	public int getSelect()
	{
		return m_select;
	}

	public int getOffset()
	{
		return m_offset;
	}
	@Override
	public boolean ClickEvent(Vec2f pos) {

		if (m_strings!=null)
		{
			//figure out position
			if (pos.x>m_corner.x && pos.x<m_corner.x+(m_size.x*0.7F))
			{
				if (pos.y>m_corner.y && pos.y<m_corner.y+m_size.y)
				{
					
					//get distance from the top
					float ytop=m_corner.y+m_size.y-0.5F;
					float yd=ytop-pos.y;
					yd=yd/0.8F;
					if ((int)yd+m_offset<m_strings.length)
					{
						m_select=(int)yd+m_offset;	
						if (m_callback!=null)
						{
							m_callback.Callback();	
							
						}
					}					
				}	
			}		
		}

		return false;
	}
	
	public void GenList(String [] strings)
	{
		m_strings=strings;
		
		GenFonts();
		if (strings!=null)
		{
			slider.setNotches(strings.length);		
		}

	}
	
	public void GenFonts()
	{
		if (m_strings!=null)
		{
			for (int i=0;i<m_fonts.length;i++)
			{
				if (i+m_offset<m_strings.length)
				{
					m_fonts[i].setString(m_strings[i+m_offset]);
				}
				else
				{
					m_fonts[i].setString("");
				}
			}		
		}
		else
		{
			for (int i=0;i<m_fonts.length;i++)
			{
				m_fonts[i].setString("");
			}
		}

	}
	
	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		// TODO Auto-generated method stub
		if (m_frame!=null)
		{
			m_frame.Draw(buffer, matrixloc);
		}	
		for (int i=0;i<m_fonts.length;i++)
		{
			if (i+m_offset==m_select)
			{
				GL20.glUniform4f(m_tint,0,1,0,1);
				m_fonts[i].Draw(buffer, matrixloc);
				GL20.glUniform4f(m_tint,1,1,1,1);
			}
			else
			{
				m_fonts[i].Draw(buffer, matrixloc);		
			}

		}
		slider.Draw(buffer, matrixloc);
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		if (m_frame!=null)
		{
			m_frame.Discard();	
		}

		for (int i=0;i<m_fonts.length;i++)
		{
			m_fonts[i].Discard();
		}
		slider.discard();
	}

	@Override
	public void AdjustPos(Vec2f p) {
		
		m_frame.AdjustPos(p);
		for (int i=0;i<m_fonts.length;i++)
		{
			m_fonts[i].AdjustPos(p);
		}
		slider.AdjustPos(p);
	}

	@Override
	public void Callback() {
	
		m_offset=slider.getPositionIndex();
		GenFonts();
	}

}
