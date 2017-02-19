package gui;

import java.nio.FloatBuffer;
import java.util.Vector;

import shared.NinePatch;
import shared.Vec2f;

public class Window extends GUIBase{
	
	NinePatch m_patch;
	Vec2f m_pos,m_size;
	Vector <GUIBase> m_contents;
	boolean m_active;
	public Window(Vec2f pos, Vec2f size, int textureID,boolean active)
	{
		m_active=active;
		m_pos=pos;
		m_size=size;
		m_contents=new Vector<GUIBase>();
		m_patch=new NinePatch(pos,size.x,size.y,textureID);
	}
	
	public void add(GUIBase element)
	{
		m_contents.add(element);
		element.AdjustPos(m_pos);
	}
	
	public void Remove(GUIBase element)
	{
		m_contents.remove(element);
	}
	
	public void update(float DT)
	{
		if (m_active==true)
		{
			if (m_contents.size()>0)
			{
				for (int i=0;i<m_contents.size();i++)
				{
					m_contents.get(i).update(DT);					
				}
			}
			
		}
	}
	
	public void setActive(boolean active)
	{
		m_active=active;
	}
	
	public boolean getActive()
	{
		return m_active;
	}
	//pass clicks down, use hierarchies to make sure they get where they need to go
	public boolean ClickEvent(Vec2f pos)
	{
		if (m_active==true)
		{
			if (pos.x>m_pos.x && pos.x<m_pos.x+m_size.x)
			{
				if (pos.y>m_pos.y && pos.y<m_pos.y+m_size.y)
				{
					if (m_contents.size()>0)
					{
						for (int i=0;i<m_contents.size();i++)
						{
							if (m_contents.get(i).ClickEvent(pos))
							{
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public void Draw(FloatBuffer buffer, int matrixloc)
	{
		if (m_active==true)
		{
			m_patch.Draw(buffer, matrixloc);
			if (m_contents.size()>0)
			{
				for (int i=0;i<m_contents.size();i++)
				{
					m_contents.get(i).Draw(buffer, matrixloc);
				}
			}
		}
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		m_patch.Discard();
		if (m_contents.size()>0)
		{
			for (int i=0;i<m_contents.size();i++)
			{
				m_contents.get(i).discard();
			}
		}
	}

	@Override
	public void AdjustPos(Vec2f p) {
		// TODO Auto-generated method stub
		m_pos.x+=p.x;
		m_pos.y+=p.y;
		m_patch.AdjustPos(m_pos);
		for (int i=0;i<m_contents.size();i++)
		{
			m_contents.get(i).AdjustPos(p);
		}
	}	
	
	public Vec2f getPosition()
	{
		return m_pos;
	}
	
	public Vec2f getSize()
	{
		return m_size;
	}

}
