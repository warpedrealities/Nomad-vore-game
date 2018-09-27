package start;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import gui.MultiLineText;
import input.Keyboard;
import input.MouseHook;
import shared.ParserHelper;
import shared.SceneBase;
import shared.Vec2f;
import view.ViewScene;
import vmo.Game;

public class StartIntro extends SceneBase {

	MultiLineText text;

	String[] strings;
	int index;
	float clock;

	MouseHook hook;

	public StartIntro() {

		m_GUImatrix = Game.sceneManager.getConfig().getMatrix();


		Document doc = ParserHelper.LoadXML("assets/data/intro.xml");
		Element e = (Element) doc.getFirstChild();
		NodeList nodes = e.getChildNodes();
		strings = new String[Integer.parseInt(e.getAttribute("count"))];
		int dex = 0;
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) nodes.item(i);
				strings[dex] = element.getTextContent();
				dex++;
			}
		}

		text = new MultiLineText(new Vec2f(-19, 15), 64, 100, 1.0F);
		updateText();
	}

	private void updateText() {
		text.addText(strings[index]);
		clock = 0;
	}

	@Override
	public void Update(float dt) {
		// TODO Auto-generated method stub
		clock += dt;
		if (clock > 0.5F) {
			if (hook.buttonDown() || Keyboard.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
				clock = 60;
			}
		}
		if (clock > 60) {
			index++;
			if (index < strings.length) {
				updateText();
			} else {
				Game.sceneManager.SwapScene(new ViewScene());
			}
		}
	}

	@Override
	public void Draw() {
		// TODO Auto-generated method stub
		DrawSetup();
		GL20.glUseProgram(Game.m_pshader);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		m_GUImatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(m_variables[1], false, matrix44Buffer);

		GL20.glUniform4f(m_variables[0], 1.0F, 1.0F, 1.0F, 1);
		text.Draw(matrix44Buffer, m_variables[2]);
	}

	@Override
	public void start(MouseHook mouse) {
		// TODO Auto-generated method stub
		hook = mouse;
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		text.discard();
	}

}
