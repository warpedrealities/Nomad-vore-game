package chargen;

import java.util.ArrayList;

import menu.Menu;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.Player;
import gui.Button;
import gui.List;
import gui.MultiLineText;
import gui.Text;
import gui.Textwindow;
import gui.Window;
import input.MouseHook;
import shared.Callback;
import shared.MyListener;
import shared.ParserHelper;
import shared.SceneBase;
import shared.Tools;
import shared.Vec2f;
import start.StartIntro;
import view.ViewScene;
import vmo.Game;

public class PlayerStartScene extends SceneBase implements MyListener, Callback  {

	//data
	private Player player;
	private int phaseIndex;
	private ArrayList<Phase> phaseList;
	private MouseHook mouse;
	
	//display
	private Matrix4f m_GUImatrix;
	private Window window;
	private List choiceList;
	private MultiLineText description;
	private Text phaseNameLabel;
	private Textwindow namePanel;
	
	
	public PlayerStartScene(Player player)
	{
		this.player=player;
		m_GUImatrix=new Matrix4f();
		m_GUImatrix.m00=0.05F;
		m_GUImatrix.m11=0.0625F;
		m_GUImatrix.m22=1.0F;
		m_GUImatrix.m33=1.0F;		
		m_GUImatrix.m31=0; m_GUImatrix.m32=0;	
		loadList();
		setupTextures();
		initialize();
		phaseIndex=0;
		populateList();
	}
	
	private void setupTextures()
	{
		m_textureIds=new int[5];
		//first is square
		//2nd is font
		m_textureIds[0]=Tools.loadPNGTexture("assets/art/ninepatchblack.png", GL13.GL_TEXTURE0);	
		m_textureIds[1]=Tools.loadPNGTexture("assets/art/font2.png", GL13.GL_TEXTURE0);
		m_textureIds[2]=Tools.loadPNGTexture("assets/art/button0.png", GL13.GL_TEXTURE0);
		m_textureIds[3]=Tools.loadPNGTexture("assets/art/button1.png", GL13.GL_TEXTURE0);
	}
	
	private void initialize()
	{
		//build window
		window=new Window(new Vec2f(-14,-9),new Vec2f(28,18),m_textureIds[0],true);
		//build chargen label
		Text label=new Text(new Vec2f(2,10),"character generation",m_variables[0]);
		window.add(label);
		//build phase label
		phaseNameLabel=new Text(new Vec2f(8,10),"phase:"+phaseList.get(phaseIndex).getName(),m_variables[0]);
		window.add(phaseNameLabel);
		//build choicelist
		choiceList=new List(new Vec2f(-14.0F,-9.0F), 17, m_textureIds[1], m_textureIds[0],m_variables[0],this,10);
		//build description
		description=new MultiLineText(new Vec2f (10.5F,17.5F),32,56,0.8F);
		//description.addText(debugtext());
		window.add(description);
		//build buttons
		Button button[]=new Button[2];
		button[0]=new Button(new Vec2f(0.0F,16), new Vec2f(10,2), m_textureIds[2],this,"select", 0);
		button[1]=new Button(new Vec2f(0.0F,14), new Vec2f(10,2), m_textureIds[2],this,"back", 1);
		for (int i=0;i<2;i++)
		{
			window.add(button[i]);
		}
		
		namePanel=new Textwindow(m_textureIds[0],new Vec2f(0,0),new Vec2f(10,2),m_variables[0],"Nomad");
		
		
	}
	
	private String debugtext()
	{
		StringBuilder builder=new StringBuilder();
		
		for (int i=0;i<16;i++)
		{
			builder.append("text text text text text text text text ");
		}
		
		return builder.toString();
	}
	
	private void loadList()
	{
		phaseList=new ArrayList<Phase>();
		Document doc=ParserHelper.LoadXML("assets/data/chargen.xml");
		Element root=doc.getDocumentElement();
	    Element n=(Element)doc.getFirstChild();
		NodeList children=n.getChildNodes();
		
		for (int i=0;i<children.getLength();i++)
		{
			Node node=children.item(i);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)node;
				//run each step successively
				if (Enode.getTagName()=="phaseMutation")
				{
					phaseList.add(new Phase_Mutator(Enode));
				}
				if (Enode.getTagName()=="phasePerk")
				{
					phaseList.add(new Phase_Perk(Enode));
				}
			}
		}
	}
	
	private void populateList()
	{
		choiceList.GenList( phaseList.get(phaseIndex).getChoices());
		choiceList.GenFonts();
		phaseNameLabel.setString("phase:"+phaseList.get(phaseIndex).getName());
		description.addText(phaseList.get(phaseIndex).getChoiceDescription(0));
	}
	
	@Override
	public void Update(float dt) {
	
		window.update(dt);
		choiceList.update(dt);
		if (phaseIndex==phaseList.size())
		{
			namePanel.update(dt);
		}
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
		window.Draw(matrix44Buffer, SceneBase.getVariables()[2]);	
		choiceList.Draw(matrix44Buffer, SceneBase.getVariables()[2]);
		
		if (phaseIndex==phaseList.size())
		{
			namePanel.Draw(matrix44Buffer, SceneBase.getVariables()[2]);
		}
	}

	@Override
	public void start(MouseHook mouse) {
		this.mouse=mouse;
		mouse.Register(window);
		mouse.Register(choiceList);
		mouse.Register(namePanel);
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		CleanTextures();
		mouse.Remove(window);
		mouse.Remove(choiceList);
		mouse.Remove(namePanel);
		namePanel.discard();
		choiceList.discard();
		window.discard();
	}

	@Override
	public void Callback() {
		description.addText(phaseList.get(phaseIndex).getChoiceDescription(choiceList.getSelect()));
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub
		switch (ID)
		{
			case 0:
				//select
				select(choiceList.getSelect());
			break;
		
			case 1:
				//back
				rollback();
			break;
		}
	}
	
	private void select(int index)
	{
		if (phaseIndex<phaseList.size())
		{
			phaseList.get(phaseIndex).performChoice(index, player);		
		}
		choiceList.setSelect(0);
		//move forwards
		phaseIndex++;
		if (phaseIndex>=phaseList.size())
		{
			choiceList.GenList(null);
			description.addText("");
			//finalize
			if (phaseIndex==phaseList.size())
			{
				//show the text
				phaseNameLabel.setString("name your character");
			}
			else
			{
				//start game
				if (namePanel.m_string.length()>0)
				{
					player.setActorName(namePanel.m_string);
				}
				else
				{
					player.setActorName("Nomad");
				}
			
				Game.sceneManager.SwapScene(new StartIntro());
			}
		}
		else
		{
			populateList();
		}
	}
	
	private void rollback()
	{
		if (phaseIndex==0)
		{
			Game.sceneManager.SwapScene(new Menu(SceneBase.getVariables()));
		}
		else
		{
			phaseIndex--;
			phaseList.get(phaseIndex).rollback(player);
			choiceList.setSelect(0);
			populateList();
		}
	}

}
