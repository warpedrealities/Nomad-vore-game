package help;

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
import gui.Window;
import input.Keyboard;
import input.MouseHook;
import shared.MyListener;
import shared.ParserHelper;
import shared.Scene;
import shared.SceneBase;
import shared.Vec2f;
import view.ModelController_Int;
import view.ViewScene;
import vmo.Game;

public class Help_Scene extends SceneBase implements MyListener{

	float clock;
	int pageNum;
	MultiLineText text;
	MouseHook hook;
	String page[];
	int pageCount;
		
	public Help_Scene()
	{	
		m_GUImatrix=new Matrix4f();
		m_GUImatrix.m00=0.05F;
		m_GUImatrix.m11=0.0625F;
		m_GUImatrix.m22=1.0F;
		m_GUImatrix.m33=1.0F;		
		m_GUImatrix.m31=0; m_GUImatrix.m32=0;	
	
		clock=2;
		text=new MultiLineText(new Vec2f(-19,15),64,100,0.9F);
		pageNum=0;
		Document doc=ParserHelper.LoadXML("assets/data/help.xml");
		Element e=(Element)doc.getFirstChild();
		NodeList nodes=e.getChildNodes();
		pageCount=Integer.parseInt(e.getAttribute("count"));
		page=new String[pageCount];
		int dex=0;
		for (int i=0;i<nodes.getLength();i++)
		{
			if (nodes.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element element=(Element)nodes.item(i);
				page[dex]=element.getTextContent();
				dex++;
			}
		}
		refreshText();
	}
	
	@Override
	public void Update(float dt) {
		// TODO Auto-generated method stub
		if (clock>0)
		{
			clock-=dt;
		}
		if ((Keyboard.isKeyDown(GLFW.GLFW_KEY_ESCAPE)||Keyboard.isKeyDown(GLFW.GLFW_KEY_F1)) && clock<=0)
		{
			exit();
		
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_A)||Keyboard.isKeyDown(GLFW.GLFW_KEY_KP_4)||Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT) && clock<=0)
		{
			back();
			clock=2;
		}	
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_D)||Keyboard.isKeyDown(GLFW.GLFW_KEY_KP_6)||Keyboard.isKeyDown(GLFW.GLFW_KEY_RIGHT) && clock<=0)
		{
			forward();
			clock=2;
		}				
	}
	
	private void refreshText()
	{
		text.addText(page[pageNum]);
	}
	
	private void back()
	{
		if (pageNum>0)
		{
			pageNum--;
			refreshText();
		}
	}
	
	private void forward()
	{
		if (pageNum<pageCount-1)
		{
			pageNum++;
			refreshText();
		}
	}

	public void exit()
	{
		Game.sceneManager.SwapScene(new ViewScene());
	}
	
	@Override
	public void Draw() {
		DrawSetup();
		GL20.glUseProgram(Game.m_pshader);
		GL11.glDisable(GL11.GL_DEPTH_TEST); 
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		m_GUImatrix.store(matrix44Buffer); matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(m_variables[1], false, matrix44Buffer);

		GL20.glUniform4f(m_variables[0],1.0F,1.0F,1.0F, 1);
		text.Draw(matrix44Buffer, m_variables[2]);
	}

	@Override
	public void start(MouseHook mouse) {
		hook=mouse;
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		text.discard();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub
		
	}

}
