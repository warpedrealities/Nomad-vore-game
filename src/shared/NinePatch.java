package shared;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

public class NinePatch {

	int m_VBO, m_VAO,m_VIO,m_indicecount, m_textureID; 
	Matrix4f m_matrix;	
	Vec2f m_pos;
	public NinePatch(Vec2f p, float width, float height, int textureID)
	{
		m_textureID=textureID;
		
		m_pos=p;
		//setup the ninepatch 
		Generate(width,height);
		
		//setup matrix
		m_matrix=new Matrix4f();
		m_matrix.m00=1; m_matrix.m11=1; m_matrix.m22=1; m_matrix.m33=1;
		m_matrix.m30=p.x; m_matrix.m31=p.y;
		
	}
	
	public void AdjustPos(Vec2f p)
	{		
		m_matrix.m30=p.x; m_matrix.m31=p.y;		
	}
	
	public void setTexture(int tex)
	{
		m_textureID=tex;
	}
	
	public void reGen(float width, float height)
	{
		FloatBuffer params=BufferUtils.createFloatBuffer(4);
		GL11.glGetTexLevelParameterfv(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH, params);
		float twidth=params.get(0);
		GL11.glGetTexLevelParameterfv(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT, params);
		float theight=params.get(0);
		
		float borderx=8/twidth;
		float bordery=8/theight;
		Vertex vertices[]=new Vertex[16];
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(16 * 6);
		//build using a lawn mower style, left to right, top to bottom row by row
		vertices[0]=new Vertex(0, 0, 0, 0, 0);
		vertices[1]=new Vertex(0.4F,0,0,borderx,0);
		vertices[2]=new Vertex(width-0.4F,0,0,1-borderx,0);
		vertices[3]=new Vertex(width,0,0,1,0);
		//row 2
		vertices[4]=new Vertex(0, 0.4F, 0, 0, bordery);
		vertices[5]=new Vertex(0.4F,0.4F,0,borderx,bordery);
		vertices[6]=new Vertex(width-0.4F,0.4F,0,1-borderx,bordery);
		vertices[7]=new Vertex(width,0.4F,0,1,bordery);		
		//row 3
		vertices[8]=new Vertex(0, height-0.4F, 0, 0, 1-bordery);
		vertices[9]=new Vertex(0.4F,height-0.4F,0,borderx,1-bordery);
		vertices[10]=new Vertex(width-0.4F,height-0.4F,0,1-borderx,1-bordery);
		vertices[11]=new Vertex(width,height-0.4F,0,1,1-bordery);
		//row 4
		vertices[12]=new Vertex(0, height, 0, 0, 1);
		vertices[13]=new Vertex(0.4F,height,0, borderx,1);
		vertices[14]=new Vertex(width-0.4F, height ,0,1-borderx,1);
		vertices[15]=new Vertex(width, height ,0,1,1);
		for (int i=0;i<16;i++)
		{
			verticesBuffer.put(vertices[i].pos);
			verticesBuffer.put(vertices[i].tex);
		}
		verticesBuffer.flip();
		
		GL30.glBindVertexArray(m_VAO);	
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);		
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0,verticesBuffer);	
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);			
		GL30.glBindVertexArray(0);
	}
	
	void Generate(float width, float height)
	{
		m_indicecount=54;
		Vertex vertices[]=new Vertex[16];
		m_VAO=GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO);		
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_textureID);
		
		FloatBuffer params=BufferUtils.createFloatBuffer(4);
		GL11.glGetTexLevelParameterfv(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH, params);
		float twidth=params.get(0);
		GL11.glGetTexLevelParameterfv(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT, params);
		float theight=params.get(0);
		
		float borderx=8/twidth;
		float bordery=8/theight;
		m_VBO=GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);		
		//build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(16 * 6);
		IntBuffer indiceBuffer= BufferUtils.createIntBuffer(54);		
		
		//build using a lawn mower style, left to right, top to bottom row by row
		vertices[0]=new Vertex(0, 0, 0, 0, 0);
		vertices[1]=new Vertex(0.4F,0,0,borderx,0);
		vertices[2]=new Vertex(width-0.4F,0,0,1-borderx,0);
		vertices[3]=new Vertex(width,0,0,1,0);
		//row 2
		vertices[4]=new Vertex(0, 0.4F, 0, 0, bordery);
		vertices[5]=new Vertex(0.4F,0.4F,0,borderx,bordery);
		vertices[6]=new Vertex(width-0.4F,0.4F,0,1-borderx,bordery);
		vertices[7]=new Vertex(width,0.4F,0,1,bordery);		
		//row 3
		vertices[8]=new Vertex(0, height-0.4F, 0, 0, 1-bordery);
		vertices[9]=new Vertex(0.4F,height-0.4F,0,borderx,1-bordery);
		vertices[10]=new Vertex(width-0.4F,height-0.4F,0,1-borderx,1-bordery);
		vertices[11]=new Vertex(width,height-0.4F,0,1,1-bordery);
		//row 4
		vertices[12]=new Vertex(0, height, 0, 0, 1);
		vertices[13]=new Vertex(0.4F,height,0, borderx,1);
		vertices[14]=new Vertex(width-0.4F, height ,0,1-borderx,1);
		vertices[15]=new Vertex(width, height ,0,1,1);
		for (int i=0;i<16;i++)
		{
			verticesBuffer.put(vertices[i].pos);
			verticesBuffer.put(vertices[i].tex);
		}
		verticesBuffer.flip();
		
		int [] buffer=
			{
			0,1,4,1,5,4,
			1,2,5,2,6,5,
			2,3,6,3,7,6,
			4,5,8,5,8,9,
			5,6,9,6,9,10,
			6,7,10,7,10,11,
			8,9,12,9,12,13,
			9,13,14,14,9,10,
			15,11,10,15,14,10
			};
	
		indiceBuffer.put(buffer);
		indiceBuffer.flip();
	
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_DYNAMIC_DRAW);
		
		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4*4);
		
		m_VIO= GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);			
				
		
	}
	
	public void Draw(FloatBuffer matrixbuffer, int matrixloc)
	{
		
		m_matrix.store(matrixbuffer);
		matrixbuffer.flip();
		GL20.glUniformMatrix4fv(matrixloc, false, matrixbuffer);	
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_textureID);
		
		//bind all the links to chunk elements
		GL30.glBindVertexArray(m_VAO);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
		GL11.glDrawElements(GL11.GL_TRIANGLES, m_indicecount, GL11.GL_UNSIGNED_INT, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);	
		GL30.glBindVertexArray(0);					
	}
	
	public void Discard()
	{
		if (m_VAO!=-1)
		{
			GL30.glBindVertexArray(m_VAO);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			if (m_VBO!=-1)
			{
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
				GL15.glDeleteBuffers(m_VBO);
			}

			if (m_VIO!=-1)
			{
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
				GL15.glDeleteBuffers(m_VIO);
			}		
			GL30.glBindVertexArray(0);
			GL30.glDeleteVertexArrays(m_VAO);			
		}			
				
		
	}
}
