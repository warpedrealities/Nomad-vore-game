package rendering;

import java.util.ArrayList;

import actor.Actor;
import shared.Vec2f;
import zone.Zone;

public class OverlayController {

	private boolean [][] grid;
	private boolean active;
	private Zone zone;
	public OverlayController(Zone zone)
	{
		this.zone=zone;
		grid=new boolean[zone.getWidth()][];
		for (int i=0;i<zone.getWidth();i++)
		{
			grid[i]=new boolean[zone.getHeight()];
		}	
	}
	
	
	public boolean isActive() {
		return active;
	}


	public void setActive(boolean active) {
		this.active = active;
	}


	public void update(ArrayList <Actor> m_actors,Vec2f pos)
	{
		int minx,maxx,miny,maxy;
		minx=(int)pos.x-12;
		miny=(int)pos.y-12;
		maxx=(int)pos.x+12;
		maxy=(int)pos.y+12;
		
		if (minx<0){minx=0;}
		if (miny<0){miny=0;}
	
		if (maxx>=grid.length)
		{
			maxx=grid.length-1;
		}
		if (maxy>=grid[0].length)
		{
			maxy=grid[0].length-1;
		}
		
		
		for (int i=minx;i<maxx;i++)
		{
			for (int j=miny;j<maxy;j++)
			{
				if (zone.getTile(i, j)!=null && 
						zone.getTile(i, j).getExplored() && 
						zone.getTile(i,j).getOverlayImage()>-1 &&
						zone.getTile(i, j).getWidgetObject()==null)
				{
					grid[i][j]=true;
				}
				else
				{
					grid[i][j]=false;
				}

			}
		}
		
		for (int i=0;i<m_actors.size();i++)
		{
			if (m_actors.get(i).getVisible() &&
					m_actors.get(i).getPosition().x>minx &&
					m_actors.get(i).getPosition().x<maxx &&
					m_actors.get(i).getPosition().y>miny &&
					m_actors.get(i).getPosition().y<maxy)
			{
				grid[(int)m_actors.get(i).getPosition().x][(int)m_actors.get(i).getPosition().y]=false;
			}
		}
	}

	public void calcActive(Zone zone) {
		active=false;
		for (int i=0;i<zone.getTiles().length;i++)
		{
			for (int j=0;j<zone.getTiles()[i].length;j++)
			{
				if (zone.getTiles()[i][j]!=null && zone.getTiles()[i][j].getOverlayImage()>-1)
				{
					active=true;
					return;
				}
			}
		}
	}


	public boolean readTile(int i, int j) {

		return grid[i][j];
	
	}
}
