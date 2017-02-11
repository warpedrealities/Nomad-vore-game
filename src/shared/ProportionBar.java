package shared;

import gui.GUIBase;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class ProportionBar extends GUIBase{

	int m_VAO,m_VBO,m_VIO;
	Matrix4f m_matrix;	
	Vec2f m_size;
	int m_texture;
	Vec2f m_position;
	boolean visibility;
	
	int m_max,m_value,m_offset;
	
	public ProportionBar(Vec2f position,Vec2f size, int value, int max, int Toffset, int texture)
	{
		m_position=position;
		m_texture=texture;
		m_matrix=new Matrix4f();
		m_matrix.scale(new Vector3f(size.x,size.y,1));
		m_matrix.m30=position.x;
		m_matrix.m31=position.y;
		m_max=max;
		m_value=value;
		m_size=size;
		m_offset=Toffset;
		//build meshes
		m_VAO=GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO);
		
		m_VBO=GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);		
		visibility=true;
		
		//load from actor

		//set VAO,VBO,VIO
	//	int d=m_actor.getSprite(i);
		//create arrays
		//build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(8 * 6);
		//build indice buffer, it's not complex its a repeating pattern of six offset through the sequence
		IntBuffer indiceBuffer= BufferUtils.createIntBuffer(12);

		//precalc UV based on value
//		int V=d/8;
//		int U=d%8;
		float Vf=0.125F*m_offset;
		float Uf=0.0F;
		float h=value;
		float proportion=h/m_max;
		
		Vertex v[]=new Vertex[8];
	
		
		v[0]=new Vertex(-0.5F, 0, 0, 0, Vf+0.125f);
		v[1]=new Vertex(-0.5F+(proportion), 0, 0, proportion, Vf+0.125f);
		v[2]=new Vertex(-0.5F+(proportion), 1, 0, proportion, Vf);
		v[3]=new Vertex(-0.5F, 1, 0, Uf, Vf);		

		v[4]=new Vertex(-0.5F+proportion, 0, 0, proportion,0+0.125f);
		v[5]=new Vertex(0.5F, -0, 0, 1, 0+0.125f);
		v[6]=new Vertex(0.5F, 1, 0,1, 0);
		v[7]=new Vertex(-0.5F+proportion, 1, 0, proportion, 0);	

		for (int j=0;j<8;j++)
		{
			verticesBuffer.put(v[j].pos);
			verticesBuffer.put(v[j].tex);				
		}
		int buffer[]=new int[12];
		buffer[0]=0;
		buffer[1]=1;
		buffer[2]=3;
		buffer[3]=1;
		buffer[4]=2;
		buffer[5]=3;
		buffer[6]=4+0;
		buffer[7]=4+1;
		buffer[8]=4+3;
		buffer[9]=4+1;
		buffer[10]=4+2;
		buffer[11]=4+3;	
		indiceBuffer.put(buffer);
		
		verticesBuffer.flip();	
		indiceBuffer.flip();

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
	public void update(float DT) {
		// TODO Auto-generated method stub
		
	}

	void Regen()
	{
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(8 * 6);	
		//precalc UV based on value
//		int V=d/8;
//		int U=d%8;
		float Vf=0.125F*m_offset;
		float Uf=0.0F;
		float h=m_value;
		float proportion=h/m_max;
		
		Vertex v[]=new Vertex[8];
	
		
		v[0]=new Vertex(-0.5F, 0, 0, 0, Vf+0.125f);
		v[1]=new Vertex(-0.5F+(proportion), 0, 0, proportion, Vf+0.125f);
		v[2]=new Vertex(-0.5F+(proportion), 1, 0, proportion, Vf);
		v[3]=new Vertex(-0.5F, 1, 0, Uf, Vf);		

		v[4]=new Vertex(-0.5F+proportion, 0, 0, proportion,0+0.125f);
		v[5]=new Vertex(0.5F, -0, 0, 1, 0+0.125f);
		v[6]=new Vertex(0.5F, 1, 0,1, 0);
		v[7]=new Vertex(-0.5F+proportion, 1, 0, proportion, 0);	
		
		for (int j=0;j<8;j++)
		{
			verticesBuffer.put(v[j].pos);
			verticesBuffer.put(v[j].tex);				
		}
		
		verticesBuffer.flip();	
		GL30.glBindVertexArray(m_VAO);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
	    GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, 
	    		verticesBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);		
		GL30.glBindVertexArray(0);
	}
	
	public void setValue(int value)
	{
		m_value=value;
		Regen();
	}
	
	public void setMax(int max)
	{
		m_max=max;
		Regen();
	}
	
	@Override
	public boolean ClickEvent(Vec2f pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		// TODO Auto-generated method stub
		if (visibility==true)
		{
			m_matrix.store(buffer);
			buffer.flip();
			GL20.glUniformMatrix4fv(matrixloc, false, buffer);	
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_texture);
			
			//bind all the links to chunk elements
			GL30.glBindVertexArray(m_VAO);
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
			GL11.glDrawElements(GL11.GL_TRIANGLES, 12, GL11.GL_UNSIGNED_INT, 0);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);	
			GL30.glBindVertexArray(0);			
		}
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
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

	@Override
	public void AdjustPos(Vec2f p) {
		// TODO Auto-generated method stub
		m_matrix.m30=p.x+m_position.x;
		m_matrix.m31=p.y+m_position.y;
	}

	public void setVisible(boolean b) {
	
		visibility=b;
	}

	
	
}
