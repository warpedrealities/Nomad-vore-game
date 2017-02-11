package worldgentools;

import shared.Vec2f;

public class PerlinTool {

	Vec2f [][] preludeGrid;
	float [][] productGrid;
	
	Vec2f [][][] subGrid;
	
	int width;
	int height;
	public PerlinTool(int width, int height)
	{
		this.width=width/4;
		this.height=height/4;
		preludeGrid=new Vec2f[this.width+1][];
		for (int i=0;i<this.width+1;i++)
		{
			preludeGrid[i]=new Vec2f[this.height+1];
		}
		generateGrid();
		
		productGrid=new float[width][];
		for (int i=0;i<width;i++)
		{
			productGrid[i]=new float[height];
		}
		generateSubGrid();
		generateProduct();
	}
	
	public boolean [][] getGrid(boolean [][] extGrid,float low, float high)
	{
		boolean [][] grid=new boolean[this.width*4][];
		for (int i=0;i<this.width*4;i++)
		{
			grid[i]=new boolean[this.height*4];
			for (int j=0;j<this.height*4;j++)
			{
				if (extGrid[i][j]==true && productGrid[i][j]>=low && productGrid[i][j]<high)
				{
					grid[i][j]=true;
				}
				else
				{
					grid[i][j]=false;
				}
			}
		}
		
		return grid;
	}
	
	private void generateSubGrid()
	{
		subGrid=new Vec2f[4][][];
		for (int i=0;i<4;i++)
		{
			subGrid[i]=new Vec2f[4][];
			for (int j=0;j<4;j++)
			{
				subGrid[i][j]=new Vec2f[4];
				//calc topright
				subGrid[i][j][0]=new Vec2f(i-4.5F,j-0.5F);
				//calc bottomright
				subGrid[i][j][1]=new Vec2f(i-4.5F,j-4.5F);
				//calc bottomleft
				subGrid[i][j][2]=new Vec2f(i-0.5F,j-4.5F);
				//calc topleft
				subGrid[i][j][3]=new Vec2f(i-0.5F,j-0.5F);
				for (int k=0;k<4;k++)
				{
					subGrid[i][j][k].normalize();
				}
			}
		}
	}
	
	
	private Vec2f genVector()
	{
		double r=Math.random()*6.2831D;
		return new Vec2f((float)(1*Math.cos(r)),(float)(1*-Math.sin(r)));
	}
	
	private void generateGrid()
	{
		for (int i=0;i<preludeGrid.length;i++)
		{
			for (int j=0;j<preludeGrid[i].length;j++)
			{
				preludeGrid[i][j]=genVector();
			}
		}
	}
	
	private void generateProduct()
	{
		for (int i=0;i<width;i++)
		{
			for (int j=0;j<height;j++)
			{
				genProductSquare(i,j);
			}
		}
	}
	
	private float dotProduct(Vec2f a, Vec2f b)
	{
		float x=a.x*b.x;
		float y=a.y*b.y;
		return x+y;
	}
	
	private void genProductSquare(int x, int y)
	{
		Vec2f ne=preludeGrid[x+1][y+1];
		Vec2f se=preludeGrid[x+1][y];
		Vec2f sw=preludeGrid[x][y];
		Vec2f nw=preludeGrid[x][y+1];
		
		float products[]=new float[4];
		
		//calculate 4 vectors
		for (int i=0;i<4;i++)
		{
			for (int j=0;j<4;j++)
			{

				products[0]=dotProduct(subGrid[i][j][0],ne);
				products[1]=dotProduct(subGrid[i][j][1],se);
				products[2]=dotProduct(subGrid[i][j][2],sw);
				products[3]=dotProduct(subGrid[i][j][3],nw);
				productGrid[(x*4)+i][(y*4)+j]=(products[0]+products[1]+products[2]+products[3])/4;		
			}
			
		}
	}
}
