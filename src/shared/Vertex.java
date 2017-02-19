package shared;

public class Vertex {
	public float pos[];
	public float tex[];

	public static int size=4*6;
		
	
	public Vertex(float x, float y, float z, float u, float v)
	{

		pos=new float[4];
		tex=new float[2];
		pos[0]=x;
		pos[1]=y;
		pos[2]=z;
		pos[3]=1;
		tex[0]=u;
		tex[1]=v;
	}

}
