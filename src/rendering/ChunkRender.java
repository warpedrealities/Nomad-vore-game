package rendering;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import vmo.Chunk;
import vmo.Chunk_Int;

public class ChunkRender {

	Chunk_Int p_chunk;
	Layer m_layer;
	public Matrix4f m_matrix;

	public ChunkRender(Chunk_Int chunk) {
		p_chunk = chunk;

		m_matrix = new Matrix4f();
		m_matrix.m00 = 1;
		m_matrix.m11 = 1;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		Vector3f vec = new Vector3f();
		vec.x = p_chunk.getPos().x * 16;
		vec.y = p_chunk.getPos().y * 16;
		Matrix4f.translate(vec, m_matrix, m_matrix);

		m_layer = new Layer();

	}

	public void Discard() {
		m_layer.Discard();

	}

	public void Swap(Chunk chunk) {
		p_chunk = chunk;
		m_matrix.m00 = 1;
		m_matrix.m11 = 1;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		m_matrix.m31 = 0;
		m_matrix.m30 = 0;
		Vector3f vec = new Vector3f();
		vec.x = p_chunk.getPos().x * 16;
		vec.y = p_chunk.getPos().y * 16;
		Matrix4f.translate(vec, m_matrix, m_matrix);
	}

	public void Generate(int level) {
		Discard();
		m_layer.Generate(p_chunk);

	}

	public void Draw(FloatBuffer matrix44Buffer, int matrixloc, int colourloc) {
		// set matrix
		m_matrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(matrixloc, false, matrix44Buffer);
		m_layer.Draw();

		GL20.glUniform4f(colourloc, 1.0F, 1.0F, 1.0F, 1);
	}

	public void Draw2ndPass(FloatBuffer matrix44Buffer, int matrixloc, int colourloc) {
		m_matrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(matrixloc, false, matrix44Buffer);
		m_layer.DrawWidgets();

	}
}
