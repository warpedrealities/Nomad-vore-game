package shared;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import gui.GUIBase;
import rendering.ColourMap;

public class Portrait extends GUIBase {

	byte m_gender, m_hair, m_skin, m_haircolour;
	int[] m_VBO, m_VAO, m_VIO;
	int m_textureID, m_colourloc;
	// Matrix4f m_matrix;
	NinePatch m_patch;
	Vec2f m_pos;

	public Portrait(Vec2f p, int colourloc, int textureid, int textureback) {
		m_pos = p;
		m_colourloc = colourloc;
		m_gender = 0;
		m_hair = 0;
		m_skin = 1;
		m_haircolour = 6;
		m_VBO = new int[3];
		m_VAO = new int[3];
		m_VIO = new int[3];
		m_patch = new NinePatch(p, 4, 8, textureback);
		// setup matrix
		// m_matrix=new Matrix4f();
		// m_matrix.m00=1; m_matrix.m11=1; m_matrix.m22=1; m_matrix.m33=1;
		// m_matrix.m30=p.x; m_matrix.m31=p.y;
		m_textureID = textureid;
		Generate();
	}

	public void AdjustPos(Vec2f p) {
		m_pos.x += p.x;
		m_pos.y += p.y;
		m_patch.AdjustPos(m_pos);
	}

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub

	}

	public static int getBodyImage(byte g) {
		switch (g) {
		case 0:
			return 0;

		case 1:
			return 12;
		case 2:
			return 24;

		}
		return 0;
	}

	int getHairImage() {
		return 40 + (m_hair * 4);
	}

	public void IncrementGender() {
		m_gender++;
		if (m_gender > 2) {
			m_gender = 0;
		}
		ReGenerate();
	}

	public void DecrementGender() {
		m_gender--;
		if (m_gender < 0) {
			m_gender = 2;
		}
		ReGenerate();
	}

	public void IncrementHair() {
		m_hair++;
		if (m_hair > 6) {
			m_hair = 0;
		}
		ReGenerate();
	}

	public void DecrementHair() {
		m_hair--;
		if (m_hair < 0) {
			m_hair = 6;
		}
		ReGenerate();
	}

	public void IncrementSkinTone() {
		m_skin++;
		if (m_skin > 5) {
			m_skin = 0;
		}

	}

	public void DecrementSkinTone() {
		m_skin--;
		if (m_skin < 0) {
			m_skin = 5;
		}

	}

	public void IncrementHairColour() {
		m_haircolour++;
		if (m_haircolour > 10) {
			m_haircolour = 6;
		}
	}

	public void DecrementHairColour() {
		m_haircolour--;
		if (m_haircolour < 6) {
			m_haircolour = 10;
		}

	}

	void Generate() {
		for (int i = 0; i < 3; i++) {
			m_VAO[i] = GL30.glGenVertexArrays();
			GL30.glBindVertexArray(m_VAO[i]);

			m_VBO[i] = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO[i]);

			// load from actor

			// set VAO,VBO,VIO
			int d = 0;
			switch (i) {
			case 0:
				d = getBodyImage(m_gender);
				break;
			case 1:
				d = getHairImage();
				break;
			case 2:
				d = 188;

			}
			// create arrays
			// build the vertex buffer here
			FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6);
			// build indice buffer, it's not complex its a repeating pattern of
			// six offset through the sequence
			IntBuffer indiceBuffer = BufferUtils.createIntBuffer(6);

			// precalc UV based on value
			int V = d / 32;
			int U = d % 32;
			float Uf = U * 0.03125f;
			float Vf = V * 0.0625f;

			Vertex v[] = new Vertex[4];
			v[0] = new Vertex(0, 0 + 8, 0, Uf, Vf);
			v[1] = new Vertex(0 + 4, 0 + 8, 0, Uf + 0.03125F, Vf);
			v[2] = new Vertex(0 + 4, 0, 0, Uf + 0.03125F, Vf + 0.0625f);
			v[3] = new Vertex(0, 0, 0, Uf, Vf + 0.0625f);

			for (int j = 0; j < 4; j++) {
				verticesBuffer.put(v[j].pos);
				verticesBuffer.put(v[j].tex);
			}
			int buffer[] = new int[6];
			buffer[0] = 0;
			buffer[1] = 1;
			buffer[2] = 3;
			buffer[3] = 1;
			buffer[4] = 2;
			buffer[5] = 3;
			indiceBuffer.put(buffer);

			verticesBuffer.flip();
			indiceBuffer.flip();

			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

			GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
			GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4 * 4);

			m_VIO[i] = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO[i]);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		}

	}

	public void ReGenerate() {
		for (int i = 0; i < 3; i++) {
			// load from actor
			int d = 0;
			switch (i) {
			case 0:
				d = getBodyImage(m_gender);
				break;
			case 1:
				d = getHairImage();
				break;
			case 2:
				d = 188;
			}
			// create arrays
			// build the vertex buffer here
			FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6);
			// build indice buffer, it's not complex its a repeating pattern of
			// six offset through the sequence

			// precalc UV based on value
			int V = d / 32;
			int U = d % 32;
			float Uf = U * 0.03125f;
			float Vf = V * 0.0625f;

			Vertex v[] = new Vertex[4];
			v[0] = new Vertex(0, 0 + 8, 0, Uf, Vf);
			v[1] = new Vertex(0 + 4, 0 + 8, 0, Uf + 0.03125F, Vf);
			v[2] = new Vertex(0 + 4, 0, 0, Uf + 0.03125F, Vf + 0.0625f);
			v[3] = new Vertex(0, 0, 0, Uf, Vf + 0.0625f);
			for (int j = 0; j < 4; j++) {
				verticesBuffer.put(v[j].pos);
				verticesBuffer.put(v[j].tex);
			}
			verticesBuffer.flip();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO[i]);
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}
	}

	@Override
	public boolean ClickEvent(Vec2f pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		// TODO Auto-generated method stub

		m_patch.Draw(buffer, matrixloc);

		// m_matrix.store(buffer);
		// buffer.flip();
		// GL20.glUniformMatrix4(matrixloc, false, buffer);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_textureID);

		for (int i = 0; i < 3; i++) {
			byte[] b = null;
			switch (i) {
			case 0:
				b = ColourMap.getInstance().getColour(m_skin);
				break;
			case 1:
				b = ColourMap.getInstance().getColour(m_haircolour);
				break;
			case 2:
				b = new byte[] { 100, 100, 100 };
				break;
			}

			GL20.glUniform4f(m_colourloc, (float) b[0] / 100, (float) b[1] / 100, (float) b[2] / 100, 1);
			// bind all the links to chunk elements
			GL30.glBindVertexArray(m_VAO[i]);
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO[i]);
			GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL30.glBindVertexArray(0);
		}
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 3; i++) {
			if (m_VAO[i] != -1) {
				GL30.glBindVertexArray(m_VAO[i]);
				GL20.glDisableVertexAttribArray(0);
				GL20.glDisableVertexAttribArray(1);
				if (m_VBO[i] != -1) {
					GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
					GL15.glDeleteBuffers(m_VBO[i]);
				}

				if (m_VIO[i] != -1) {
					GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
					GL15.glDeleteBuffers(m_VIO[i]);
				}
				GL30.glBindVertexArray(0);
				GL30.glDeleteVertexArrays(m_VAO[i]);
			}

		}
		GL11.glDeleteTextures(m_textureID);
	}
}
