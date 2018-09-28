package view;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import actor.npc.NPC;
import actor.player.Player;
import input.Keyboard;
import nomad.universe.Universe;
import rendering.Sprite;
import shared.Tools;
import shared.Vec2f;
import zone.Zone;

public class Targeting {

	float clock;
	ArrayList<NPC> targetList;
	boolean isActive;
	int textureID;
	int targetIndex;
	Sprite reticle;

	public Targeting() {
		targetList = new ArrayList<NPC>();
		clock = 0;
		isActive = false;
		textureID = Tools.loadPNGTexture("assets/art/reticle.png", GL13.GL_TEXTURE0);
		reticle = new Sprite(new Vec2f(0, 0), 2, 1);
	}

	private void genSortedList(ArrayList<NPC> targets, Vec2f playerPos) {
		float distance = 0;
		while (targets.size() > 0 && distance < 9) {
			int dex = -1;
			float z = 99;
			for (int i = targets.size() - 1; i >= 0; i--) {
				float d = playerPos.getDistance(targets.get(i).getPosition());
				if (d < z) {
					z = d;
					dex = i;

				}

			}
			targetList.add(targets.get(dex));
			targets.remove(dex);
			distance = z;
		}
	}

	public void genList(Zone zone, Player player) {
		targetIndex = 0;
		targetList.clear();

		ArrayList<NPC> candidateList = new ArrayList<NPC>();
		for (int i = 0; i < zone.getActors().size(); i++) {
			if (NPC.class.isInstance(zone.getActors().get(i)) && zone.getActors().get(i).getVisible()
					&& zone.getActors().get(i).getAttackable()) {
				candidateList.add((NPC) zone.getActors().get(i));
			}
		}
		// sort by distance order

		genSortedList(candidateList, player.getPosition());

		if (targetList.size() > 0) {
			reticle.reposition(targetList.get(0).getPosition());
		}

	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean value) {
		isActive = value;
	}

	public boolean update(float DT) {
		if (clock > 0) {
			clock -= DT;
		}
		if (clock <= 0) {
			if (targetList.size() > 0) {
				reticle.reposition(targetList.get(targetIndex).getPosition());
				return runLogic();

			} else {
				isActive = false;
			}
		}
		return false;
	}

	private boolean runLogic() {
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_ENTER) || Keyboard.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
			if (targetList.get(targetIndex).getAttackable() == false) {
				genList(Universe.getInstance().getCurrentZone(), Universe.getInstance().getPlayer());
			} else {
				Player player = Universe.getInstance().getPlayer();
				player.useMove(player.getSpecialMove(), targetList.get(targetIndex));
				clock = 0.1F;
				return true;
			}

		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_A) || Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT)
				|| Keyboard.isKeyDown(GLFW.GLFW_KEY_KP_4)) {
			decrementTarget();
			clock = 0.15F;
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_D) || Keyboard.isKeyDown(GLFW.GLFW_KEY_RIGHT)
				|| Keyboard.isKeyDown(GLFW.GLFW_KEY_KP_6)) {
			IncrementTarget();
			clock = 0.15F;
		}
		return false;
	}

	private void IncrementTarget() {
		// TODO Auto-generated method stub
		targetIndex++;
		if (targetIndex >= targetList.size()) {
			targetIndex = 0;
		}

	}

	private void decrementTarget() {
		// TODO Auto-generated method stub
		targetIndex--;
		if (targetIndex < 0) {
			targetIndex = targetList.size() - 1;
		}

	}

	public void discard() {
		GL11.glDeleteTextures(textureID);
		reticle.discard();
	}

	public void draw(int objmatrix, int tintvar, FloatBuffer matrix44Buffer) {

		if (isActive && targetList.size() > 0) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

			reticle.draw(objmatrix, tintvar, matrix44Buffer);
		}
	}
}
