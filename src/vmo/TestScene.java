package vmo;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import gui.Text;
import gui.Textwindow;
import gui.Window;
import input.Keyboard;
import input.MouseHook;
import rendering.SquareRenderer;
import shared.SceneBase;
import shared.Tools;
import shared.Vec2f;

public class TestScene extends SceneBase {

	SquareRenderer testsquare;
	Text text;
	Window window;
	MouseHook hook;
	Textwindow textinput;

	public TestScene(int[] var) {
		// TODO Auto-generated constructor stubs
		super(var);
		m_textureIds = new int[2];
		m_textureIds[0] = Tools.loadPNGTexture("assets/art/spritesheet.png", GL13.GL_TEXTURE0);
		m_textureIds[1] = Tools.loadPNGTexture("assets/art/ninepatchblack.png", GL13.GL_TEXTURE0);
		testsquare = new SquareRenderer(0, new Vec2f(0, 0), new Vector4f(1, 1, 1, 1));
		m_GUImatrix = new Matrix4f();
		m_GUImatrix.m00 = 0.05F;
		m_GUImatrix.m11 = 0.0625F;
		m_GUImatrix.m22 = 1.0F;
		m_GUImatrix.m33 = 1.0F;
		m_GUImatrix.m31 = 0;
		m_GUImatrix.m32 = 0;
		window = new Window(new Vec2f(-10, -10), new Vec2f(17, 17), m_textureIds[1], true);
		text = new Text(new Vec2f(0.5F, 5), "isn't it amazing that we have better text now", 0.9F, m_variables[0]);
		textinput = new Textwindow(m_textureIds[1], new Vec2f(1, 1), new Vec2f(10, 2), m_variables[0], "text");
		window.add(text);
		window.add(textinput);
	}

	@Override
	public void Update(float dt) {
		// TODO Auto-generated method stub
		if (hook.buttonDown()) {
			testsquare.reposition(new Vec2f(hook.getPosition().x, hook.getPosition().y));
		}
		textinput.update(dt);
	}

	@Override
	public void Draw() {
		DrawSetup();
		// set tint to default
		GL20.glUniform4f(m_variables[0], 1.0F, 1.0F, 1.0F, 1);
		int tex = GL20.glGetUniformLocation(Game.m_pshader, "texture_diffuse");
		GL20.glUniform1i(tex, 0);
		testsquare.draw(m_variables[2], m_variables[0], matrix44Buffer);
		m_GUImatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(m_variables[1], false, matrix44Buffer);
		GL20.glUniform4f(m_variables[0], 1.0F, 1.0F, 1.0F, 1);
		window.Draw(matrix44Buffer, SceneBase.getVariables()[2]);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_textureIds[0]);

		// text.Draw(matrix44Buffer, m_variables[2]);
	}

	@Override
	public void start(MouseHook mouse) {
		// TODO Auto-generated method stub
		hook = mouse;
		mouse.Register(window);
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		CleanTextures();
		testsquare.discard();
		text.discard();
		window.discard();
		textinput.discard();
	}

}
