package solarview.spaceEncounter;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import input.MouseHook;
import shared.SceneBase;
import solarview.spaceEncounter.EncounterEntities.EncounterShip;
import solarview.spaceEncounter.animation.Animator;
import spaceship.Spaceship;
import vmo.Game;

public class SpaceEncounter extends SceneBase {

	private EncounterLogic logic;
	private EncounterRenderer renderer;
	private EncounterGUI gui;
	
	private EncounterShip[] buildShips(Spaceship playerShip,Spaceship [] alienShips)
	{
		int c=1;
		if (alienShips!=null)
		{
			c+=alienShips.length;
		}
		
		EncounterShip []list=new EncounterShip[c];
		list[0]=new EncounterShip(playerShip);
		for (int i=1;i<c;i++)
		{
			list[i]=new EncounterShip(alienShips[i-1]);
		}
		return list;
		
	}
	
	public SpaceEncounter(Spaceship playerShip,Spaceship [] alienShips)
	{		
		logic=new EncounterLogic(buildShips(playerShip,alienShips));
		renderer=new EncounterRenderer(logic.getShipList());
		gui=new EncounterGUI(playerShip);
	}
	
	@Override
	public void Update(float dt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void Draw() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL20.glUniformMatrix4fv(m_variables[3], false, matrix44Buffer);
		GL20.glUseProgram(Game.m_pshader);
		GL20.glUniform4f(m_variables[0], 1.0F, 1.0F, 1.0F, 1);
		m_viewmatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(m_variables[1], false, matrix44Buffer);
		renderer.draw(m_variables[1],m_variables[2],m_variables[0], matrix44Buffer);
	}

	@Override
	public void start(MouseHook mouse) {
		// TODO Auto-generated method stub

	}

	@Override
	public void end() {
		// TODO Auto-generated method stub

	}

}
