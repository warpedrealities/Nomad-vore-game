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

public class SpriteBeam implements Renderable, Square_Rotatable_Int {
	protected int m_VBO;
	protected int m_VAO;
	protected int m_VIO;
	protected int indiceCount;
	protected int numFrames;
	protected float maxLength;	
	protected int maxSegments;
	protected float segmentLength=1;
	protected Vec2f spritePosition;
	protected Matrix4f m_matrix;
	protected float width = 1;
	protected float length;

	protected boolean visible;
	protected SpriteBatch batch;	
	protected float currentFacing;
	protected float spriteDepth = 0.4F;	
	
	public SpriteBeam(Vec2f position,int numFrames, float width, float maxLength)
	{
		spritePosition=position;	
		
		m_matrix = new Matrix4f();
		m_matrix.m00 = width;
		m_matrix.m11 = 1;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		m_matrix.m30 = spritePosition.x;
		m_matrix.m31 = spritePosition.y;	
		

		this.numFrames=numFrames;
		this.width=width;	
		this.maxSegments=(int) (1+(maxLength/segmentLength));
		this.maxLength=(int)maxLength-1;
		length=maxLength;
		generate(maxLength,0);
		this.visible=true;
	}
	
	private void generate(float maxLength, int frame)
	{
		float sqrt = (float) Math.sqrt((float) numFrames);

		int numSegments=(int) ((maxLength/segmentLength)+1);
		
		int U = frame % (int) sqrt;
		int V = frame / (int) sqrt;
		m_VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO);
		m_VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6*numSegments);
		// build indice buffer, it's not complex its a repeating pattern of six
		// offset through the sequence
		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(6*numSegments);
		float Uf = U * (1 / sqrt);
		float Vf = V * (1 / sqrt);
		// build the four vertexes for the square
		for (int i=0;i<numSegments;i++)
		{
			Vertex v[] = new Vertex[4];

			v[0] = new Vertex(-0.5F, i, 0, Uf, Vf + (1 / sqrt));
			v[1] = new Vertex(0.5F , i, 0, Uf + (1 / sqrt), Vf + (1 / sqrt));
			v[2] = new Vertex(0.5F , i + 1, 0, Uf + (1 / sqrt), Vf);
			v[3] = new Vertex(-0.5F, i + 1, 0, Uf, Vf);

			for (int k = 0; k < 4; k++) {
				verticesBuffer.put(v[k].pos);
				verticesBuffer.put(v[k].tex);
			}

			int buffer[] = new int[6];
			buffer[0] = (i*4)+0;
			buffer[1] = (i*4)+1;
			buffer[2] = (i*4)+3;
			buffer[3] = (i*4)+1;
			buffer[4] = (i*4)+2;
			buffer[5] = (i*4)+3;	
			indiceBuffer.put(buffer);		
		}


		verticesBuffer.flip();
		indiceBuffer.flip();
		indiceCount = 6*numSegments;
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4 * 4);

		m_VIO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);		
		
	}

			
	private void regenerate(float length, int frame)
	{
		if (length>maxLength)
		{
			length=maxLength;
		}
		float sqrt = (float) Math.sqrt((float) numFrames);

		int numSegments=(int) ((length/segmentLength)+1);
		
		boolean b=false;

		if (length%1>0)
		{
			b=true;
			numSegments++;
		}
		int U = frame % (int) sqrt;
		int V = frame / (int) sqrt;
		float Uf = U * (1.0F / sqrt);
		float Vf = V * (1.0F / sqrt);
		// build the four vertexes for the square
		int numVertices=4 * 6*numSegments; 
		
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(numVertices);

		for (int i=0;i<numSegments;i++)
		{
			Vertex v[] = new Vertex[4];

			v[0] = new Vertex(-0.5F, i, 0, Uf, Vf + (1 / sqrt));
			v[1] = new Vertex(0.5F , i, 0, Uf + (1 / sqrt), Vf + (1 / sqrt));
			if (i!=numSegments-1)
			{
				v[2] = new Vertex(0.5F , i + 1, 0, Uf + (1 / sqrt), Vf);
				v[3] = new Vertex(-0.5F, i + 1, 0, Uf, Vf);		
			}
			else
			{
				v[2] = new Vertex(0.5F , length+1.5F, 0, Uf + (1 / sqrt), Vf);
				v[3] = new Vertex(-0.5F, length+1.5F, 0, Uf, Vf);		
			}


			for (int k = 0; k < 4; k++) {
				verticesBuffer.put(v[k].pos);
				verticesBuffer.put(v[k].tex);
			}
		}		
		verticesBuffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);		
		indiceCount=numSegments*6;
		if (b)
		{
			indiceCount+=6;
		}
	}
	
	@Override
	public boolean getVisible() {
		return visible;
	}

	@Override
	public void draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer) {
		m_matrix.store(matrix44fbuffer);
		matrix44fbuffer.flip();
		GL20.glUniformMatrix4fv(objmatrix, false, matrix44fbuffer);
		// link mesh
		if (indiceCount > 0) {
			// bind all the links to chunk elements
			GL30.glBindVertexArray(m_VAO);
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
			GL11.glDrawElements(GL11.GL_TRIANGLES, indiceCount, GL11.GL_UNSIGNED_INT, 0);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL30.glBindVertexArray(0);
		}
	}

	@Override
	public void discard() {
		if (m_VAO != -1) {
			GL30.glBindVertexArray(m_VAO);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			if (m_VBO != -1) {
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
				GL15.glDeleteBuffers(m_VBO);
			}

			if (m_VIO != -1) {
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
				GL15.glDeleteBuffers(m_VIO);
			}
			GL30.glBindVertexArray(0);
			GL30.glDeleteVertexArrays(m_VAO);
		}
	}

	@Override
	public void setSpriteBatch(SpriteBatch batch) {
		this.batch=batch;
	}

	@Override
	public SpriteBatch getBatch() {
		return batch;
	}

	@Override
	public void setFacing(float facing) {
		System.out.println(facing);
		
		currentFacing = facing;
		rotate(facing);
			m_matrix.m30 = spritePosition.x;
			m_matrix.m31 = spritePosition.y;		
	}

	private void rotate(float facing)
	{
		Matrix4f.setIdentity(m_matrix);
	//	m_matrix.m00 = (float) (width*Math.cos(fd));
	//	m_matrix.m01 = (float) -Math.sin(fd);
	//	m_matrix.m10 =(float) Math.sin(fd);
	//	m_matrix.m11 = (float) Math.cos(fd);
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		m_matrix.m30 = 0;
		m_matrix.m31 = 0;
		m_matrix.m32 = 0;
		Matrix4f.scale(new Vector3f(width,1,0), m_matrix, m_matrix);

		// rotate things
		double fd=(double)facing*0.785398F;
		Matrix4f.rotate(((float) fd), new Vector3f(0, 0, 1), m_matrix, m_matrix);
				
	}

	public void reposition(Vec2f p) {
		spritePosition.x = (int) p.x;
		spritePosition.y = (int) p.y;
		rotate(currentFacing);

		m_matrix.m30 = spritePosition.x ;
		m_matrix.m31 = spritePosition.y ;
	}

	public void repositionF(Vec2f p) {
		spritePosition.x = p.x;
		spritePosition.y = p.y;
		rotate(currentFacing);

		m_matrix.m30 = spritePosition.x;
		m_matrix.m31 = spritePosition.y;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible=visible;
	}

	@Override
	public void setFlashing(int bool) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setImage(int value) {
		regenerate(length,value);
	}
	
	public void setLength(int value, float length)
	{
		this.length=length;
	}
	
	@Override
	public int getFacing() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void setFacing(int facing) {
		// TODO Auto-generated method stub
		
	}

	public void setWidth(float width) {
		this.width=width;
	}

	@Override
	public void setColour(float r, float g, float b) {
		// TODO Auto-generated method stub
		
	}

	
}
