package solarview.spaceEncounter;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import actorRPG.Actor_RPG;
import gui.Button;
import gui.Button2;
import gui.ButtonProp;
import gui.MultiLineButton;
import gui.Text;
import gui.Window;
import input.MouseHook;
import nomad.Universe;
import shared.MyListener;
import shared.Tools;
import shared.Vec2f;
import solarview.spaceEncounter.EncounterEntities.EncounterShip;
import spaceship.Spaceship;

public class EncounterGUI implements MyListener {
	private Spaceship playerShip;
	private EncounterShip encounterShip;
	private Matrix4f m_viewMatrix;
	private int []textureIds;
	private Window []windows;
	private MouseHook mouse;
	private Text [] resourceTexts;
	private String [] resourceStrings;
	private Button2 []buttons;
	private Text []shieldTexts;
	private MultiLineButton []weaponButtons;
	
	public EncounterGUI(EncounterShip ship) {
		this.encounterShip=ship;
		this.playerShip=ship.getShip();
		setMatrix();
		setupTextures();
		buildUI();
	}

	private void buildUI()
	{
		windows=new Window[2];
		windows[0]=new Window(new Vec2f (-20,-16), new Vec2f(6,32), textureIds[0],true);
		windows[1]=new Window(new Vec2f (14,-16), new Vec2f(6,32), textureIds[0],true);
		
		if (playerShip.getShipStats().getWeapons().size()>0)
		{
			weaponButtons=new MultiLineButton[playerShip.getShipStats().getWeapons().size()];
			for (int i=0;i<weaponButtons.length;i++)
			{
				weaponButtons[i]=new MultiLineButton((new Vec2f(0.1F,30.0F-(2*i))),new Vec2f(5.8F,2),textureIds[1]
						,this,playerShip.getShipStats().getWeapons().get(i).getWeapon().getName(),20+i,textureIds[16]);
		
				windows[0].add(weaponButtons[i]);
			}
		}
		
		
		resourceStrings=playerShip.getShipStats().getCombatResourceKeys();
		resourceTexts=new Text[resourceStrings.length*2];
		for (int i=0;i<resourceStrings.length;i++)
		{
			resourceTexts[i*2]=new Text(new Vec2f(0.2F,15.5F-i),resourceStrings[i],0.6F,0);
			resourceTexts[(i*2)+1]=new Text(new Vec2f(0.2F,15.1F-i),playerShip.getShipStats().getResource(resourceStrings[i]).getResourceAmount()+
			"/"+playerShip.getShipStats().getResource(resourceStrings[i]).getResourceCap(),0.6F,0);	
			windows[1].add(resourceTexts[i*2]);
			windows[1].add(resourceTexts[(i*2)+1]);
		}
		
		if (encounterShip.getShield()!=null)
		{
			shieldTexts=new Text[2];
			shieldTexts[0]=new Text(new Vec2f(0.2F,1.9F),"text",0.6F,0);
			shieldTexts[1]=new Text(new Vec2f(0.2F,1.3F),"text",0.6F,0);	
			windows[1].add(shieldTexts[0]);
			windows[1].add(shieldTexts[1]);
			writeShieldStatus();
			Button button=new Button(new Vec2f(0.1F,0.1F), new Vec2f(5.9F,2), textureIds[1], this, "toggle", 13);
			windows[1].add(button);
		}
		
		
		
		buildManouver();
	}
	
	private void buildManouver()
	{


		buttons=new Button2[7];
		//halt
		buttons[0]=new Button2(new Vec2f(2.0F,2.0F),new Vec2f(2,2),textureIds[2],this,"",0,textureIds[3],0);
		//left
		buttons[1]=new Button2(new Vec2f(0.0F,2.0F),new Vec2f(2,2),textureIds[4],this,"",1,textureIds[5],0);	
		//right
		buttons[2]=new Button2(new Vec2f(4.0F,2.0F),new Vec2f(2,2),textureIds[6],this,"",2,textureIds[7],0);		
		//forward
		buttons[3]=new Button2(new Vec2f(2.0F,4.0F),new Vec2f(2,2),textureIds[8],this,"",4,textureIds[9],0);
		//forleft
		buttons[4]=new Button2(new Vec2f(0.0F,4.0F),new Vec2f(2,2),textureIds[10],this,"",5,textureIds[11],0);	
		//forright
		buttons[5]=new Button2(new Vec2f(4.0F,4.0F),new Vec2f(2,2),textureIds[12],this,"",6,textureIds[13],0);		
		//full
		buttons[6]=new Button2(new Vec2f(2.0F,6.0F),new Vec2f(2,2),textureIds[14],this,"",8,textureIds[15],0);		
		//create progress time button
		for (int i=0;i<7;i++)
		{
			windows[0].add(buttons[i]);
		}
		Button button=new Button(new Vec2f(0.1F,0.1F), new Vec2f(5.9F,2), textureIds[1], this, "turn", 14);
		windows[0].add(button);
		updateButtons();
	}
	
	public void start(MouseHook mouse)
	{
		this.mouse=mouse;
		if (weaponButtons!=null)
		{
			for (int i=0;i<weaponButtons.length;i++)
			{
				weaponButtons[i].setMouse(mouse);
			}
		}
		mouse.Register(windows[0]);
		mouse.Register(windows[1]);
	}
	
	private void setupTextures()
	{
		textureIds=new int[17];
		textureIds[0]=Tools.loadPNGTexture("assets/art/ninepatchblack.png", GL13.GL_TEXTURE0);	
		textureIds[1]=Tools.loadPNGTexture("assets/art/button0.png", GL13.GL_TEXTURE0);
		textureIds[2]=Tools.loadPNGTexture("assets/art/ui/halt0.png", GL13.GL_TEXTURE0);
		textureIds[3]=Tools.loadPNGTexture("assets/art/ui/halt1.png", GL13.GL_TEXTURE0);
		textureIds[4]=Tools.loadPNGTexture("assets/art/ui/left0.png", GL13.GL_TEXTURE0);
		textureIds[5]=Tools.loadPNGTexture("assets/art/ui/left1.png", GL13.GL_TEXTURE0);
		textureIds[6]=Tools.loadPNGTexture("assets/art/ui/right0.png", GL13.GL_TEXTURE0);
		textureIds[7]=Tools.loadPNGTexture("assets/art/ui/right1.png", GL13.GL_TEXTURE0);
		textureIds[8]=Tools.loadPNGTexture("assets/art/ui/half0.png", GL13.GL_TEXTURE0);
		textureIds[9]=Tools.loadPNGTexture("assets/art/ui/half1.png", GL13.GL_TEXTURE0);
		textureIds[14]=Tools.loadPNGTexture("assets/art/ui/full0.png", GL13.GL_TEXTURE0);
		textureIds[15]=Tools.loadPNGTexture("assets/art/ui/full1.png", GL13.GL_TEXTURE0);
		textureIds[10]=Tools.loadPNGTexture("assets/art/ui/forleft0.png", GL13.GL_TEXTURE0);
		textureIds[11]=Tools.loadPNGTexture("assets/art/ui/forleft1.png", GL13.GL_TEXTURE0);
		textureIds[12]=Tools.loadPNGTexture("assets/art/ui/forright0.png", GL13.GL_TEXTURE0);
		textureIds[13]=Tools.loadPNGTexture("assets/art/ui/forright1.png", GL13.GL_TEXTURE0);
		textureIds[16]=Tools.loadPNGTexture("assets/art/button1.png", GL13.GL_TEXTURE0);
	}
	
	private void setMatrix() {
		m_viewMatrix = new Matrix4f();
		m_viewMatrix.m00 = 0.05F;
		m_viewMatrix.m11 = 0.0625F;
		m_viewMatrix.m22 = 1.0F;
		m_viewMatrix.m33 = 1.0F;
	}	
	
	public void draw(int viewMatrix, int objmatrix, int tintvar, FloatBuffer matrix44Buffer) {
		m_viewMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(viewMatrix, false, matrix44Buffer);		
		GL20.glUniform4f(tintvar,1,1,1,1);
		for (int i=0;i<2;i++)
		{
			windows[i].Draw(matrix44Buffer, objmatrix);
		}
	}
	
	public void update(float dt)
	{
		if (weaponButtons!=null)
		{
			for (int i=0;i<weaponButtons.length;i++)
			{
				weaponButtons[i].update(dt);
			}
		}
	}
	
	public void updateUI()
	{
		for (int i=0;i<resourceStrings.length;i++)
		{
			if (resourceStrings[i].equals("FOOD"))
			{
				resourceTexts[i].setString("FOOD:"+Universe.getInstance().getPlayer().getRPG().getStat(Actor_RPG.SATIATION)+"/"+
								+Universe.getInstance().getPlayer().getRPG().getStatMax(Actor_RPG.SATIATION));
			}
			else
			{
				resourceTexts[i].setString(resourceStrings[i]+":"+(int)playerShip.getShipStats().getResource(resourceStrings[i]).getResourceAmount()+
						"/"+playerShip.getShipStats().getResource(resourceStrings[i]).getResourceCap());			
			}
		
		}
		if (encounterShip.getShield()!=null)
		{
			writeShieldStatus();
		}
	}	
	
	private void writeShieldStatus()
	{
		switch(encounterShip.getShield().getStatus())
		{
		case OFF:
			shieldTexts[0].setString("offline");	
			shieldTexts[1].setString("");
			break;
		case STARTUP:
			shieldTexts[0].setString("starting up");	
			shieldTexts[1].setString("");
			break;
		case ON:
			shieldTexts[0].setString("online");
			writeHitpoints();
			break;
		case FASTCHARGE:
			shieldTexts[0].setString("charging");
			writeHitpoints();
			break;
		case COOLDOWN:
			shieldTexts[0].setString("restarting");
			writeCooldown();
			break;
		}
	}
	private void writeHitpoints()
	{
		shieldTexts[1].setString(encounterShip.getShield().getHitpoints()+"/"+playerShip.getShipStats().getShield().getHitpoints());
	}
	
	private void writeCooldown()
	{
		shieldTexts[1].setString(encounterShip.getShield().getCooldown()+"/"+playerShip.getShipStats().getShield().getRestartTime());
	
	}
	
	public void discard()
	{
		for (int i=0;i<windows.length;i++)
		{
			mouse.Remove(windows[i]);
			windows[i].discard();
		}
		for (int i=0;i<textureIds.length;i++)
		{
			GL11.glDeleteTextures(textureIds[i]);		
		}	
		

	}

	private void updateButtons()
	{
		for (int i=0;i<7;i++)
		{
			buttons[i].setAlt(false);
		}
		char course=encounterShip.getCourse();
		switch(course)
		{
		case 0:
			buttons[0].setAlt(true);
			break;
		case 1:
			buttons[1].setAlt(true);
			break;
		case 2:
			buttons[2].setAlt(true);
			break;
		case 4:
			buttons[3].setAlt(true);
			break;
		case 5:
			buttons[4].setAlt(true);
			break;
		case 6:
			buttons[5].setAlt(true);
			break;
		case 8:
			buttons[6].setAlt(true);
			break;
		}
		
	}
	
	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		if (ID<9)
		{
			encounterShip.setCourse((char) ID);
			updateButtons();
		}
		switch (ID)
		{
		case 13:
			encounterShip.getShield().toggleStatus();
			writeShieldStatus();
			break;
		}
	}
}