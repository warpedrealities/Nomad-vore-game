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
import shared.Vertex;

public class CircleOverlay {

	private int VBO,VIO,VAO,indiceCount;
	protected Vec2f spritePosition;
	protected Matrix4f matrix;
	
	public CircleOverlay()
	{
		matrix= new Matrix4f();
		matrix.setIdentity();
		matrix.m00=4;
		matrix.m11=4;
		generate();
	}
	
	private void generate()
	{

		VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(VAO);

		VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(5 * 6);
		// build indice buffer, it's not complex its a repeating pattern of six
		// offset through the sequence
		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(12);

		// build the four vertexes for the square
		Vertex v[] = new Vertex[5];

		v[0] = new Vertex(0, 0, 0, 0.5F, 0.5F);
		v[1] = new Vertex(0.5F, 0.5F, 0, 1.0F, 0.0F);
		v[2] = new Vertex(-0.5F, 0.5F, 0, 1.0F, 1.0F);
		v[3] = new Vertex(-0.5F, -0.5F, 0, 1.0F, 0.0F);
		v[4] = new Vertex(0.5F, -0.5F, 0, 0.0F, 0.0F);


		for (int k = 0; k < 5; k++) {
			verticesBuffer.put(v[k].pos);
			verticesBuffer.put(v[k].tex);
		}

		int buffer[] = new int[12];
		buffer[0] = 0;
		buffer[1] = 2;
		buffer[2] = 1;
		buffer[3] = 0;
		buffer[4] = 1;
		buffer[5] = 3;
		buffer[6] = 2;
		buffer[7] = 4;
		buffer[8] = 0;
		buffer[9] = 0;
		buffer[10] = 3;
		buffer[11] = 4;
		indiceBuffer.put(buffer);

		verticesBuffer.flip();
		indiceBuffer.flip();
		indiceCount = 12;
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4 * 4);

		VIO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, VIO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);		
		
		
		
	}
	
	private int getTriangleCount(float width)
	{
		if (width<=2)
		{
			return 1;
		}
		if (width<=6)
		{
			return 3;
		}
		return 4;
		
	}
	
	private Vertex []genFirstTriangle(float width, Vertex[] v)
	{
		if (width==1)
		{
			v[0] = new Vertex(0, 0, 0, 0.5F, 0.5F);
			v[1] = new Vertex(0.25F, 0.5F, 0, 1.0F, 0.25F);
			v[2] = new Vertex(-0.25F, 0.5F, 0, 1.0F, 0.75F);		
		}
		else
		{
			v[0] = new Vertex(0, 0, 0, 0.5F, 0.5F);
			v[1] = new Vertex(0.5F, 0.5F, 0, 1.0F, 0.0F);
			v[2] = new Vertex(-0.5F, 0.5F, 0, 1.0F, 1.0F);		
		}	
		return v;
	}
	
	private Vertex []genSecondThirdTriangle(float width, Vertex[] v)
	{
		if (width==3)
		{
			v[3] = new Vertex(0.5F, 0.2F, 0, 0.7F, 0.0F);
			v[4] = new Vertex(-0.5F, 0.2F, 0, 0.7F, 1.0F);			
		}
		if (width==4)
		{
			v[3] = new Vertex(0.5F, 0.0F, 0, 0.5F, 0.0F);
			v[4] = new Vertex(-0.5F, 0.0F, 0, 0.5F, 1.0F);				
		}
		if (width==5)
		{
			v[3] = new Vertex(0.5F, -0.2F, 0, 0.3F, 0.0F);
			v[4] = new Vertex(-0.5F, -0.2F, 0, 0.3F, 1.0F);			
		}
		if (width>=6)
		{
			v[3] = new Vertex(0.5F, -0.5F, 0, 0.0F, 0.0F);
			v[4] = new Vertex(-0.5F, -0.5F, 0, 0.0F, 1.0F);			
		}
		
		return v;
	}
	
	public void regen(float width)
	{
		int triangleCount=getTriangleCount(width);
		Vertex v[] = new Vertex[2+triangleCount];
		int vCount=2+triangleCount;
		if (vCount>5)
		{
			vCount=5;
		}
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer((vCount) * 6);	
		indiceCount=triangleCount*3;
		v=genFirstTriangle(width,v);
		if (triangleCount>1)
		{
			v=genSecondThirdTriangle(width,v);
		}	
		for (int k = 0; k < vCount; k++) {
			verticesBuffer.put(v[k].pos);
			verticesBuffer.put(v[k].tex);
		}

		verticesBuffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer) {
		matrix.store(matrix44fbuffer);
		matrix44fbuffer.flip();
		GL20.glUniformMatrix4fv(objmatrix, false, matrix44fbuffer);
		// link mesh
		if (indiceCount > 0) {
			// bind all the links to chunk elements
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
	}
	
	public void discard() {
		if (VAO != -1) {
			GL30.glBindVertexArray(VAO);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			if (VBO != -1) {
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
				GL15.glDeleteBuffers(VBO);
			}

			if (VIO != -1) {
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
				GL15.glDeleteBuffers(VIO);
			}
			GL30.glBindVertexArray(0);
			GL30.glDeleteVertexArrays(VAO);
		}
	}

	public void rotate(float angle)
	{
		Matrix4f.setIdentity(matrix);
		matrix.m00 = 4;
		matrix.m11 = 4;
		matrix.m22 = 4;
		matrix.m33 = 1;
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;

		// rotate things
		Matrix4f.rotate(((float) angle) * -0.785398F, new Vector3f(0, 0, 1), matrix, matrix);
		
		
	}
	
	public void reposition(Vec2f position) {
		matrix.m30=position.x;
		matrix.m31=position.y;
		
	}
	
}
