package spacetest;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import input.Keyboard;
import input.MouseHook;
import rendering.SpriteManager;
import rendering.SpriteRotatable;
import shared.SceneBase;
import shared.Vec2f;
import solarview.spaceEncounter.EncounterEntities.CombatWeapon;
import solarview.spaceEncounter.EncounterEntities.WeaponCheck;
import solarview.spaceEncounter.effectHandling.EffectHandler;
import solarview.spaceEncounter.interfaces.CombatAction;
import solarview.spaceEncounter.rendering.CircleHandler;
import vmo.Game;

public class SpaceTest extends SceneBase {
	private Matrix4f m_viewMatrix;
	private SpriteManager spriteManager;
	private SpriteRotatable target;
	private MouseHook mouse;
	private EffectHandler effectHandler;
	private CombatAction action;
	private EncounterShipTest origin, tship;
	private Vec2f offset;
	private CircleHandler circle;
	private CombatWeapon weapon;
	private boolean buttonToggle;

	public SpaceTest() {
		spriteManager = new SpriteManager("assets/art/solar/");
		setMatrix();
		buildSprites();
		effectHandler = new EffectHandler();
		origin = new EncounterShipTest();
		tship = new EncounterShipTest();
		tship.setPosition(new Vec2f(2, 2));
		action = new CombatActionTest(tship);
		offset = new Vec2f(0.5F, 0.5F);
		circle = new CircleHandler();
		// circle.setWidth(1);
		circle.setPosition(new Vec2f(0, 0));
		circle.setWidth(2.0F);
		circle.setVisible(true);
		weapon = new CombatWeaponTest(2.0F);

	}

	private void buildSprites() {
		// TODO Auto-generated method stub
		SpriteRotatable sprite = new SpriteRotatable(new Vec2f(0, 0), 1);
		sprite.setCentered(true);
		spriteManager.addSprite(sprite, "avenger.png");
		sprite.setVisible(true);
		target = new SpriteRotatable(new Vec2f(2, 2), 1);
		target.setCentered(true);
		spriteManager.addSprite(target, "fighter.png");
		target.setVisible(true);

	}

	@Override
	public void Update(float dt) {
		// TODO Auto-generated method stub
		if (mouse.buttonDown()) {
			target.reposition(mouse.getPosition());
			tship.setPosition(mouse.getPosition().replicate().add(offset));
		}
		effectHandler.update(dt);
		if (!effectHandler.effectsRunning() && Keyboard.isKeyDown(GLFW.GLFW_KEY_A)) {
			effectHandler.addScript(origin, action, false);
		}
		if (!effectHandler.effectsRunning() && Keyboard.isKeyDown(GLFW.GLFW_KEY_D)) {
			effectHandler.addScript(origin, action, true);
		}
		if (buttonToggle && !Keyboard.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
			buttonToggle = false;
		}
		if (!buttonToggle && Keyboard.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
			buttonToggle = true;
			if (WeaponCheck.checkArc(origin, tship, weapon)) {
				System.out.println("in arc");
			} else {
				System.out.println("out of arc");
			}
		}
	}

	private void setMatrix() {
		m_viewMatrix = new Matrix4f();
		m_viewMatrix.m00 = 0.05F;
		m_viewMatrix.m11 = 0.0625F;
		m_viewMatrix.m22 = 1.0F;
		m_viewMatrix.m33 = 1.0F;
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
		spriteManager.draw(m_variables[2], m_variables[0], matrix44Buffer);
		effectHandler.draw(matrix44Buffer, m_variables[2], m_variables[1]);
		circle.draw(m_variables[2], m_variables[0], matrix44Buffer);
	}

	@Override
	public void start(MouseHook mouse) {
		this.mouse = mouse;
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		spriteManager.discard();
		effectHandler.discard();
		circle.discard();
	}

}
