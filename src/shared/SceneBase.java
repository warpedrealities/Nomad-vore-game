package shared;

import input.MouseHook;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import vmo.Game;
import vmo.GameManager;

abstract public class SceneBase implements Scene {

	protected static int m_variables[];
	protected Matrix4f m_viewmatrix;
	protected FloatBuffer matrix44Buffer;
	protected int m_textureIds[];
	protected Matrix4f m_GUImatrix;

	public static int[] getVariables() {
		return m_variables;
	}

	public SceneBase() {
		m_viewmatrix=Game.sceneManager.getConfig().getMatrix();
		matrix44Buffer = BufferUtils.createFloatBuffer(16);

	}

	public SceneBase(int variables[]) {
		m_variables = variables;
		m_viewmatrix = new Matrix4f();
		m_viewmatrix.m00 = 0.05F;
		m_viewmatrix.m11 = 0.0625F;
		m_viewmatrix.m22 = 1.0F;
		m_viewmatrix.m33 = 1.0F;
		matrix44Buffer = BufferUtils.createFloatBuffer(16);

		m_viewmatrix.m31 = 0;
		m_viewmatrix.m32 = 0;

	}

	@Override
	abstract public void Update(float dt);

	@Override
	abstract public void Draw();

	protected void DrawSetup() {
		GL20.glUseProgram(Game.m_pshader);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		// set standard viewing matrix
		m_viewmatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniform1fv(m_variables[1], matrix44Buffer);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
	}

	@Override
	abstract public void start(MouseHook mouse);

	@Override
	abstract public void end();

	protected void CleanTextures() {
		for (int i = 0; i < m_textureIds.length; i++) {
			GL11.glDeleteTextures(m_textureIds[i]);
		}

	}

	public static void setVariables(int[] var) {
		m_variables = var;
	}

}
