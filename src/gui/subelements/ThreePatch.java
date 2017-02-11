package gui.subelements;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import gui.SharedGUIResources;
import shared.Vec2f;
import shared.Vertex;

public class ThreePatch {
	int VBO, VAO,VIO,indiceCount, textureID; 
	Matrix4f objMatrix;	
	Vec2f pos;
	
	
	public ThreePatch(Vec2f position)
	{
		textureID=SharedGUIResources.getInstance().getTexture(0);
		
		pos=position;
		//setup the ninepatch 
		generate();
		
		//setup matrix
		objMatrix=new Matrix4f();
		objMatrix.m00=1; objMatrix.m11=1; objMatrix.m22=1; objMatrix.m33=1;
		objMatrix.m30=position.x; objMatrix.m31=position.y;
	}
	
	private void generate()
	{
		indiceCount=24;
		Vertex vertices[]=new Vertex[8];
		VAO=GL30.glGenVertexArrays();
		GL30.glBindVertexArray(VAO);		
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		FloatBuffer params=BufferUtils.createFloatBuffer(4);
		GL11.glGetTexLevelParameterfv(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH, params);
		GL11.glGetTexLevelParameterfv(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT, params);

		VBO=GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);		
		//build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(8 * 6);
		IntBuffer indiceBuffer= BufferUtils.createIntBuffer(24);		
		vertices[0]=new Vertex(0, 0, 0, 0, 0);
		vertices[1]=new Vertex(1, 0, 0, 0.5F, 0);		
		vertices[2]=new Vertex(0, 1, 0, 0, 0.25F);
		vertices[3]=new Vertex(1, 1, 0, 0.5F, 0.25F);	
		vertices[4]=new Vertex(0, 3, 0, 0, 0.75F);
		vertices[5]=new Vertex(1, 3, 0, 0.5F, 0.75F);	
		vertices[6]=new Vertex(0, 4, 0, 0, 1);
		vertices[7]=new Vertex(1, 4, 0, 0.5F, 1);	
	
		for (int i=0;i<8;i++)
		{
			verticesBuffer.put(vertices[i].pos);
			verticesBuffer.put(vertices[i].tex);
		}
		verticesBuffer.flip();
		
		int [] buffer=
			{0,1,2,1,2,3,
			 2,3,4,3,4,5,
			 4,5,6,5,6,7
			};
		indiceBuffer.put(buffer);
		indiceBuffer.flip();
	
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_DYNAMIC_DRAW);
		
		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4*4);
		
		VIO= GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, VIO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);			
				
	}
	
	public void regen(boolean lit)
	{
		Vertex v[]=new Vertex[8];
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(8* 6);	
		float value=0;
		if (lit==true)
		{
			value=0.5F;
		}
		
		v[0]=new Vertex(0, 0, 0, value+0, 0);
		v[1]=new Vertex(1, 0, 0, value+0.5F, 0);		
		v[2]=new Vertex(0, 1, 0, value+0, 0.25F);
		v[3]=new Vertex(1, 1, 0, value+0.5F, 0.25F);	
		v[4]=new Vertex(0, 3, 0, value+0, 0.75F);
		v[5]=new Vertex(1, 3, 0, value+0.5F, 0.75F);	
		v[6]=new Vertex(0, 4, 0, value+0, 1);
		v[7]=new Vertex(1, 4, 0, value+0.5F, 1);	
		
		
		for (int k=0;k<8;k++)
		{
			verticesBuffer.put(v[k].pos);
			verticesBuffer.put(v[k].tex);
		}		
	
		verticesBuffer.flip();	
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0,verticesBuffer);	
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);	
		
		
		
		
	}
	
	public void AdjustPos(Vec2f p)
	{		
		objMatrix.m30=p.x; objMatrix.m31=p.y;		
		pos.x=p.x;pos.y=p.y;
	}
	
	public void Draw(FloatBuffer matrixbuffer, int matrixloc)
	{
		objMatrix.store(matrixbuffer);
		matrixbuffer.flip();
		GL20.glUniformMatrix4fv(matrixloc, false, matrixbuffer);	
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		//bind all the links to chunk elements
		GL30.glBindVertexArray(VAO);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, VIO);
		GL11.glDrawElements(GL11.GL_TRIANGLES, indiceCount, GL11.GL_UNSIGNED_INT, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);	
		GL30.glBindVertexArray(0);					
	}
	
	public void Discard()
	{
		if (VAO!=-1)
		{
			GL30.glBindVertexArray(VAO);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			if (VBO!=-1)
			{
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
				GL15.glDeleteBuffers(VBO);
			}

			if (VIO!=-1)
			{
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
				GL15.glDeleteBuffers(VIO);
			}	
			GL30.glBindVertexArray(0);
			GL30.glDeleteVertexArrays(VAO);			
		}			
				
		
	}
}
