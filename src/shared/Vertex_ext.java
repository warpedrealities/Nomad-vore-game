package shared;

public class Vertex_ext{
	public static int size=4*7;
	public float pos[];
	public float tex[];
	public float m_light;
	public Vertex_ext(float x, float y, float z, float u, float v,float l) {

		pos=new float[4];
		tex=new float[2];
		pos[0]=x;
		pos[1]=y;
		pos[2]=z;
		pos[3]=1;
		tex[0]=u;
		tex[1]=v;
		m_light=l;
		// TODO Auto-generated constructor stub
	}

	
}
