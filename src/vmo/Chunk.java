package vmo;

import shared.Vec2f;
import shared.Vec2i;

public class Chunk implements Chunk_Int{

	public Tile_Int [][] m_tiles;
	public Vec2i m_position;
	public Collision_Interface m_int;
	
	public Chunk(boolean make)
	{

	}
	
	public Chunk Clone()
	{
		Chunk c=new Chunk(true);
		c.m_position=m_position;
		for (int i=0;i<16;i++)
		{
			for (int j=0;j<16;j++)
			{
				for (int k=0;k<16;k++)
				{
					c.m_tiles[j][k]=m_tiles[j][k];
				}
			}
		
		}
		return c;
	}
	
	public void BatchsetImage()
	{

				for (int j=0;j<16;j++)
				{
					for (int k=0;k<16;k++)
					{
						if (m_tiles[j][k]!=null)
						{
							//m_tiles[j][k].CalcImageNum();
						}
						
					}
					
				}
			
	}
	
	@Override
	public Tile_Int[][] getTiles(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vec2f getPos() {
		// TODO Auto-generated method stub
		return null;
	}
}
