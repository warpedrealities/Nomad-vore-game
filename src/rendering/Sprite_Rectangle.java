package rendering;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import shared.Vec2f;
import shared.Vec2i;
import shared.Vertex;

public class Sprite_Rectangle extends Sprite {

	float xyRatio;
	int spriteNum;
	
	public Sprite_Rectangle (Vec2f vec2f, int numFrames)
	{		
		spritePosition=new Vec2f((int)vec2f.x,(int)vec2f.y);
		m_matrix=new Matrix4f();
		m_matrix.m00=1;	m_matrix.m11=1; m_matrix.m22=1; m_matrix.m33=1;
		this.numFrames=numFrames;
		reposition(vec2f);
	}
	
	public void deferredGeneration()
	{
		generate(numFrames);	
	}
	
	public void setXyRatio(float xyRatio) {
		this.xyRatio = xyRatio;
	}



	protected void generate(int numFrames)
	{
		float sqrt=(float)Math.sqrt((float)numFrames);

		int U=spriteNum%(int)sqrt;
		int V=spriteNum/(int)sqrt;	
		m_VAO=GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO);
		
		m_VBO=GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		//build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6);
		//build indice buffer, it's not complex its a repeating pattern of six offset through the sequence
		IntBuffer indiceBuffer= BufferUtils.createIntBuffer(6);
		float Uf=U*(1/sqrt);
		float Vf=V*(1/sqrt);
		//build the four vertexes for the square
		Vertex v[]=new Vertex[4];
		
		float height=spriteSize*xyRatio;
		
		v[0]=new Vertex(spriteSize*-0.5F, 0, 0, Uf, Vf+(1/sqrt));
		v[1]=new Vertex(spriteSize*0.5F, 0, 0, Uf+(1/sqrt), Vf+(1/sqrt));
		v[2]=new Vertex(spriteSize*0.5F, 0+height, 0, Uf+(1/sqrt), Vf);
		v[3]=new Vertex(spriteSize*-0.5F, 0+height, 0, Uf, Vf);	

		for (int k=0;k<4;k++)
		{

			verticesBuffer.put(v[k].pos);
			verticesBuffer.put(v[k].tex);
		}		
	
		int buffer[]=new int[6];
		buffer[0]=0;
		buffer[1]=1;
		buffer[2]=3;
		buffer[3]=1;
		buffer[4]=2;
		buffer[5]=3;				
		indiceBuffer.put(buffer);	
		
		verticesBuffer.flip();	
		indiceBuffer.flip();
		indicecount=6;
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_DYNAMIC_DRAW);
		
		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size,
		4 * 4);
		
		m_VIO= GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);			
	}
	
	@Override
	public void setImage(int value)
	{
		spriteNum=value;
		if ( m_VBO!=0)
		{
			regen(value);
		}		
	}
	
	@Override
	protected void regen(int select)
	{
		float sqrt=(float)Math.sqrt((float)numFrames);

		int U=select%(int)sqrt;
		int V=select/(int)sqrt;	
		float Uf=U*(1.0F/sqrt);
		float Vf=V*(1.0F/sqrt);
		//build the four vertexes for the square
		Vertex v[]=new Vertex[4];
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6);	
		float height=spriteSize*xyRatio;
		
		v[0]=new Vertex(spriteSize*-0.5F, 0, 0, Uf, Vf+(1/sqrt));
		v[1]=new Vertex(spriteSize*0.5F, 0, 0, Uf+(1/sqrt), Vf+(1/sqrt));
		v[2]=new Vertex(spriteSize*0.5F, 0+height, 0, Uf+(1/sqrt), Vf);
		v[3]=new Vertex(spriteSize*-0.5F, 0+height, 0, Uf, Vf);	
		
		for (int k=0;k<4;k++)
		{
			verticesBuffer.put(v[k].pos);
			verticesBuffer.put(v[k].tex);
		}		
	
		verticesBuffer.flip();	
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0,verticesBuffer);	
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);	
	}
	
	@Override
	public void reposition(Vec2f p)
	{
		spritePosition.x=(int) p.x; spritePosition.y=(int) p.y;
		m_matrix.m00=1;	m_matrix.m11=1; m_matrix.m22=1; m_matrix.m33=1;
		m_matrix.m30=1; m_matrix.m31=1; m_matrix.m32=0;
	
		m_matrix.m30=spritePosition.x+0.5F;
		m_matrix.m31=spritePosition.y+0.0F;		
	}
	@Override
	public void setSpriteSize(float spriteSize) 
	{
		this.spriteSize = spriteSize;
	}
}
