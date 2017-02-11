package gui;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import shared.Tools;
import shared.Vec2f;
import shared.Vertex;

public class Image extends GUIBase {

	int m_VBO, m_VAO,m_VIO,m_indicecount, m_textureID; 
	Matrix4f m_matrix;	
	Vec2f m_pos;
	boolean isVisible;
	
	public Image()
	{
		
	}
	
	public Image(Vec2f p, Vec2f size, String texture)
	{
		m_textureID = Tools.loadPNGTexture(texture, GL13.GL_TEXTURE0);
		
		m_pos=p;
		//setup the ninepatch 
		Generate(size);
		
		//setup matrix
		m_matrix=new Matrix4f();
		m_matrix.m00=1; m_matrix.m11=1; m_matrix.m22=1; m_matrix.m33=1;
		m_matrix.m30=p.x; m_matrix.m31=p.y;		
		isVisible=true;
		
	}
	
	public void setTexture(String texture)
	{
		GL11.glDeleteTextures(m_textureID);		
		m_textureID = Tools.loadPNGTexture(texture, GL13.GL_TEXTURE0);
	}
	
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}


	protected void Generate(Vec2f size) {
	
		m_VAO=GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO);
		
		m_VBO=GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		//build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6);
		//build indice buffer, it's not complex its a repeating pattern of six offset through the sequence
		IntBuffer indiceBuffer= BufferUtils.createIntBuffer(6);

		//build the four vertexes for the square
		Vertex v[]=new Vertex[4];
		
		v[0]=new Vertex(0, 0, 0, 0, 1);
		v[1]=new Vertex(0+size.x, 0, 0, 1, 1);
		v[2]=new Vertex(0+size.x, 0+size.y, 0, 1,0);
		v[3]=new Vertex(0, 0+size.y, 0, 0, 0);	

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
		m_indicecount=6;
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
		
		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size,
		4 * 4);
		
		m_VIO= GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);			
	}


	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean ClickEvent(Vec2f pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		if (isVisible==true)
		{
			
			m_matrix.store(buffer);
			buffer.flip();
			GL20.glUniformMatrix4fv(matrixloc, false, buffer);	
			
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
			
	}

	@Override
	public void discard() {

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
		GL11.glDeleteTextures(m_textureID);	
	}

	@Override
	public void AdjustPos(Vec2f p) {

		m_matrix.m30=p.x; m_matrix.m31=p.y;	
	}

}
