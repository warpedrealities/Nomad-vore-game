package vmo;


import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import shared.Vertex;

public class Quad {

	int m_VBO, m_VAO,m_VIO,m_indicecount; 
	Matrix4f m_matrix;
	
	public Quad()
	{
		m_indicecount=6;
		//generate quad
		Vertex vertices[]=new Vertex[4];
		m_VAO=GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO);
		
		vertices[0]=new Vertex(-0.5F,-0.5F, 0, 1.0F, 0.0F);
		vertices[1]=new Vertex(0.5F, -0.5F, 0, 1.0F, 1.0F);
		vertices[2]=new Vertex(0.5F, 0.5F, 0, 0.0F, 1.0F);
		vertices[3]=new Vertex(-0.5F, 0.5F, 0, 0.0F, 0.0F);	
		
		m_VBO=GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);		
		//build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6);
		//build indice buffer, it's not complex its a repeating pattern of six offset through the sequence
		IntBuffer indiceBuffer= BufferUtils.createIntBuffer(6);		
		
		
		for (int i=0;i<4;i++)
		{
			verticesBuffer.put(vertices[i].pos);
			verticesBuffer.put(vertices[i].tex);
		}
		verticesBuffer.flip();
		int buffer[]={2, 3, 0,0, 1, 2};
				
		indiceBuffer.put(buffer);
		indiceBuffer.flip();
	
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
		
		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size,
		4*4);
		
		m_VIO= GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);			
		
		m_matrix=new Matrix4f();
		m_matrix.m00=1;
		m_matrix.m11=1;
		m_matrix.m22=1;
		m_matrix.m33=1;
		
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
	
	public void Draw(int matrixid)
	{
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
}
