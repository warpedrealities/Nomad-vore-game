package solarview;

import input.MouseHook;
import nomad.universe.Universe;

import java.nio.FloatBuffer;
import java.util.Queue;

import gui.Button;
import gui.Text;
import gui.Window;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import actorRPG.Actor_RPG;

import shared.MyListener;
import shared.Screen;
import shared.Tools;
import shared.Vec2f;
import spaceship.Spaceship;
import vmo.Game;

public class SolarGUI {

	Spaceship playerShip;
	int[] textureIds;
	Matrix4f m_GUImatrix;
	Window window;
	Text[] resourceTexts;
	String[] resourceStrings;

	Text globalPosition;
	Text messages;
	Queue<String> messageQueue;
	
	public SolarGUI(Queue<String> messageQueue) {
		m_GUImatrix = Game.sceneManager.getConfig().getMatrix();
		this.messageQueue=messageQueue;

		setupTextures();

	}

	public int[] getTextures() {
		return textureIds;
	}

	private void setupTextures() {
		textureIds = new int[2];
		textureIds[0] = Tools.loadPNGTexture("assets/art/ninepatchgreen.png", GL13.GL_TEXTURE0);
		textureIds[1] = Tools.loadPNGTexture("assets/art/button0.png", GL13.GL_TEXTURE0);
		// textureIds[1]=Tools.loadPNGTexture("assets/art/font2.png",
		// GL13.GL_TEXTURE0);
	}

	public void generate(Spaceship ship, MyListener listener) {
		window = new Window(new Vec2f(11, -10), new Vec2f(9, 26), textureIds[0], true);
		playerShip = ship;

		resourceStrings = playerShip.getShipStats().getResourceKeys();
		resourceTexts = new Text[resourceStrings.length];
		for (int i = 0; i < resourceStrings.length; i++) {
			if (resourceStrings[i].equals("FOOD")) {
				resourceTexts[i] = new Text(new Vec2f(0.2F, 12.5F - i),
						"FOOD:" + Universe.getInstance().getPlayer().getRPG().getStat(Actor_RPG.SATIATION) + "/"
								+ +Universe.getInstance().getPlayer().getRPG().getStatMax(Actor_RPG.SATIATION),
						0.8F, 0);
			} else {
				resourceTexts[i] = new Text(new Vec2f(0.2F, 12.5F - i),
						resourceStrings[i] + ":"
								+ (int)playerShip.getShipStats().getResource(resourceStrings[i]).getResourceAmount() + "/"
								+ playerShip.getShipStats().getResource(resourceStrings[i]).getResourceCap(),
						0.8F, 0);
			}

			window.add(resourceTexts[i]);
		}

		globalPosition = new Text(new Vec2f(0.2F, 3.5F),
				"X:" + playerShip.getPosition().x + " Y:" + playerShip.getPosition().y, 0.8F, 0);
		window.add(globalPosition);
		messages = new Text(new Vec2f(-15.2F, 0.2F),"", 0.6F, 0);

		window.add(messages);
		
		// add buttons
		Button[] buttons = new Button[4];

		buttons[0] = new Button(new Vec2f(0.1F, 0.1F), new Vec2f(8.8F, 2), textureIds[1], listener, "exit", 0);
		buttons[1] = new Button(new Vec2f(4.6F, 2.1F), new Vec2f(4.4F, 2), textureIds[1], listener, "+", 1);
		buttons[2] = new Button(new Vec2f(0.1F, 2.1F), new Vec2f(4.4F, 2), textureIds[1], listener, "-", 2);
		buttons[3] = new Button(new Vec2f(0.1F, 4.1F), new Vec2f(8.8F, 2), textureIds[1], listener, "system", 3);

		for (int i = 0; i < 4; i++) {
			window.add(buttons[i]);
		}
	}

	public void draw(int viewMatrix, int objmatrix, int tintvar, FloatBuffer matrix44Buffer) {
		m_GUImatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(viewMatrix, false, matrix44Buffer);

		window.Draw(matrix44Buffer, objmatrix);
		
	}

	public void update() {

		for (int i = 0; i < resourceStrings.length; i++) {
			if (resourceStrings[i].equals("FOOD")) {
				resourceTexts[i]
						.setString("FOOD:" + Universe.getInstance().getPlayer().getRPG().getStat(Actor_RPG.SATIATION)
								+ "/" + +Universe.getInstance().getPlayer().getRPG().getStatMax(Actor_RPG.SATIATION));
			} else {
				resourceTexts[i].setString(resourceStrings[i] + ":"
						+ (int) playerShip.getShipStats().getResource(resourceStrings[i]).getResourceAmount() + "/"
						+ playerShip.getShipStats().getResource(resourceStrings[i]).getResourceCap());
			}

		}
		globalPosition.setString("X:" + playerShip.getPosition().x + " Y:" + playerShip.getPosition().y);
		handleMessages();
	}
	
	public void handleMessages()
	{
		if (playerShip.getWarpHandler()!=null)
		{
			messages.setString("establishing warp bubble, do not move, press space");
		}		
		else
		{
			if (messageQueue.isEmpty())
			{
				messages.setString("");		
			}
			else
			{
				messages.setString(messageQueue.poll());	
			}

		}
	}

	public void start(MouseHook mouse) {
		mouse.Register(window);
	}

	public void discard(MouseHook mouse) {
		mouse.Remove(window);
		window.discard();
		for (int i = 0; i < textureIds.length; i++) {
			GL11.glDeleteTextures(textureIds[i]);
		}
	}

	public void update(float dt) {
		window.update(dt);
	}
}
