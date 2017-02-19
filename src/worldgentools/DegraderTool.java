package worldgentools;

import vmo.GameManager;
import zone.Tile;
import zone.TileDefLibrary;
import zone.Zone;

public class DegraderTool {
	Tile [][] m_tiles;
	boolean [][]m_grid;
	int m_width,m_height;
	TileDefLibrary m_library;
	int degrade;
	Zone m_zone;
	
	public DegraderTool(Tile [][] tiles,boolean [][]grid,TileDefLibrary library, Zone zone)
	{
		m_library=library;
		m_tiles=tiles;
		m_width=m_tiles.length;
		m_height=m_tiles[0].length;
		m_grid=grid;
		m_zone = zone;
	}
	
	public void Run(int chance, int tiletodegrade, int tiletoreplace)
	{
		for (int i=0;i<m_width;i++)
		{
			for (int j=0;j<m_height;j++)
			{
				if (m_grid[i][j]==true)
				{
					if (m_tiles[i][j]!=null && m_tiles[i][j].getDefinition().getID()==tiletodegrade && m_tiles[i][j].getWidgetObject()==null
							&&
							GameManager.m_random.nextInt(100)<chance)
					{
						m_tiles[i][j]=new Tile(i, j, m_library.getDef(tiletoreplace), m_zone, m_library);
						degrade++;
					}
				}
			
			}
		}	
	}
	
	public Tile [][]getTiles()
	{
		return m_tiles;
	}
}
