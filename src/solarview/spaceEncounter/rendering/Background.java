package solarview.spaceEncounter.rendering;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import shared.Tools;
import shared.Vec2f;
import shared.Vertex;

public class Background {

	private Vec2f center;
	private int VBO, VIO, VAO, indiceCount, textureID;
	protected Matrix4f matrix;

	public Background() {
		center = new Vec2f(0, 0);
		// build texture
		textureID = Tools.loadPNGTexture("assets/art/encounter/background.png", GL13.GL_TEXTURE0);
		// build geometry
		generate();
		matrix = new Matrix4f();
		matrix.setIdentity();
	}

	public void draw(int viewMatrix, int objmatrix, int tintvar, FloatBuffer matrix44Buffer) {
		GL20.glUniform4f(tintvar, 1, 1, 1, 0.5F);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		matrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(objmatrix, false, matrix44Buffer);
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
		GL11.glDeleteTextures(textureID);
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

	private void generate() {

		VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(VAO);

		VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(9 * 4 * 6);
		// build indice buffer, it's not complex its a repeating pattern of six
		// offset through the sequence
		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(6 * 4 * 9);

		// build the four vertexes for the square
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Vertex v[] = new Vertex[4];

				v[0] = new Vertex(0, 0, 0, 0, 1);
				v[1] = new Vertex(0 + 32, 0, 0, 1, 1);
				v[2] = new Vertex(0 + 32, 0 + 32, 0, 1, 0);
				v[3] = new Vertex(0, 0 + 32, 0, 0, 0);

				for (int k = 0; k < 4; k++) {
					v[k].pos[0] -= 48 - (i * 32);
					v[k].pos[1] -= 48 - (j * 32);
					verticesBuffer.put(v[k].pos);
					verticesBuffer.put(v[k].tex);
				}
			}
		}

		for (int i = 0; i < 9; i++) {
			int buffer[] = new int[6];
			buffer[0] = (i * 4) + 0;
			buffer[1] = (i * 4) + 1;
			buffer[2] = (i * 4) + 3;
			buffer[3] = (i * 4) + 1;
			buffer[4] = (i * 4) + 2;
			buffer[5] = (i * 4) + 3;
			indiceBuffer.put(buffer);
		}
		verticesBuffer.flip();
		indiceBuffer.flip();
		indiceCount = 6 * 9;
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4 * 4);

		VIO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, VIO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	private void reposition() {

		matrix.m30=center.x;
		matrix.m31=center.y;
		/*
		// build the four vertexes for the square
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(9 * 4 * 6);
		// build indice buffer, it's not complex its a repeating pattern of six
		// offset through the sequence

		// build the four vertexes for the square
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Vertex v[] = new Vertex[4];

				v[0] = new Vertex(0, 0, 0, 0, 1);
				v[1] = new Vertex(0 + 32, 0, 0, 1, 1);
				v[2] = new Vertex(0 + 32, 0 + 32, 0, 1, 0);
				v[3] = new Vertex(0, 0 + 32, 0, 0, 0);

				for (int k = 0; k < 4; k++) {
					v[k].pos[0] -= 48 - (i * 32);
					v[k].pos[0] -=center.x;
					v[k].pos[1] -= 48 - (j * 32);
					v[k].pos[1] -=center.y;
					verticesBuffer.put(v[k].pos);
					verticesBuffer.put(v[k].tex);
				}
			}
		}

		verticesBuffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		*/
	}

	public void update(Vec2f location) {
		if (center.x < location.x - 32) {
			center.x += 32;
			reposition();
			return;
		}
		if (center.x > location.x + 32) {
			center.x -= 32;
			reposition();
			return;
		}
		if (center.y < location.y - 32) {
			center.y += 32;
			reposition();
			return;
		}
		if (center.y > location.y + 16) {
			center.y -= 32;
			reposition();
			return;
		}
	}

}
