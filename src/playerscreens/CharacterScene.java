package playerscreens;

import gui.Button;
import gui.Button2;
import gui.List;
import gui.MultiLineText;
import gui.Text;
import gui.Window;
import input.MouseHook;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import actor.player.Player;
import actorRPG.Actor_RPG;

import nomad.Universe;

import shared.Callback;
import shared.MyListener;
import shared.SceneBase;
import shared.Screen;
import shared.Tools;
import shared.Vec2f;
import view.ViewScene;
import vmo.Game;
import actorRPG.Player_RPG;;

public class CharacterScene extends SceneBase implements Callback, MyListener {

	Matrix4f m_GUImatrix;
	Window window;
	Window[] subWindows;
	Player player;
	Player_RPG playerRPG;
	Text experienceNotes;
	MultiLineText description;
	List perkList;
	Screen m_screen;
	private MouseHook hook;

	public CharacterScene() {
		SetupTextures();
		m_GUImatrix = new Matrix4f();
		m_GUImatrix.m00 = 0.05F;
		m_GUImatrix.m11 = 0.0625F;
		m_GUImatrix.m22 = 1.0F;
		m_GUImatrix.m33 = 1.0F;
		m_GUImatrix.m31 = 0;
		m_GUImatrix.m32 = 0;
		player = Universe.getInstance().getPlayer();
		playerRPG = (Player_RPG) player.getRPG();
		subWindows = new Window[3];

		int values[] = new int[5];
		values[0] = m_textureIds[1];
		values[1] = m_textureIds[0];
		values[2] = m_textureIds[7];
		values[3] = m_textureIds[8];
		values[4] = m_variables[0];
		initialize(values);
	}

	private void SetupTextures() {
		// TODO Auto-generated method stub
		m_textureIds = new int[9];
		// first is square
		// 2nd is font
		m_textureIds[0] = Tools.loadPNGTexture("assets/art/ninepatchblack.png", GL13.GL_TEXTURE0);
		m_textureIds[1] = Tools.loadPNGTexture("assets/art/font2.png", GL13.GL_TEXTURE0);
		// m_textureIds[2]=Tools.loadPNGTexture("assets/art/spritesheet.png",
		// GL13.GL_TEXTURE0);
		// m_textureIds[3]=Tools.loadPNGTexture("assets/art/"+m_world.currentZone.getTileset(),
		// GL13.GL_TEXTURE0);
		m_textureIds[4] = Tools.loadPNGTexture("assets/art/window.png", GL13.GL_TEXTURE0);
		// m_textureIds[5]=Tools.loadPNGTexture("assets/art/widgets.png",
		// GL13.GL_TEXTURE0);
		// m_textureIds[6]=Tools.loadPNGTexture("assets/art/bars.png",
		// GL13.GL_TEXTURE0);
		m_textureIds[7] = Tools.loadPNGTexture("assets/art/button0.png", GL13.GL_TEXTURE0);
		m_textureIds[8] = Tools.loadPNGTexture("assets/art/button1.png", GL13.GL_TEXTURE0);
	}

	@Override
	public void Update(float DT) {

		if (m_screen == null) {
			perkList.update(DT);
		} else {
			m_screen.update(DT);
		}

	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {

		switch (ID) {
		case 0:
			subWindows[1].setActive(true);
			subWindows[2].setActive(false);
			break;

		case 1:
			subWindows[2].setActive(true);
			subWindows[1].setActive(false);
			break;

		case 2:
			Game.sceneManager.SwapScene(new ViewScene());
			break;

		case 3:
			// start level up process
			if (playerRPG.getPlayerExperience() >= playerRPG.getNextLevel()) {
				levelUp();
			}
			break;
		}
	}

	private void levelUp() {
		int values[] = new int[5];
		values[0] = m_textureIds[1];
		values[1] = m_textureIds[0];
		values[2] = m_textureIds[7];
		values[3] = m_textureIds[8];
		values[4] = m_variables[0];
		m_screen = new LevelUpScreen(values, this);
		m_screen.start(hook);
		hook.Remove(perkList);
	}

	@Override
	public void start(MouseHook hook) {

		hook.Register(window);
		hook.Register(perkList);
		this.hook = hook;
	}

	@Override
	public void Callback() {
		if (m_screen != null) {
			m_screen.discard(hook);
			hook.Register(perkList);
			m_screen = null;
			int values[] = new int[5];
			values[0] = m_textureIds[1];
			values[1] = m_textureIds[0];
			values[2] = m_textureIds[7];
			values[3] = m_textureIds[8];
			values[4] = m_variables[0];
			genExperienceNotes(values[0], values[4]);
			genAttributePage(values[1], values[0], values[4]);
			genCombatPage(values[1], values[0], values[4]);
			genSkillPage(values[1], values[0], values[4]);
			buildList();

		} else {
			int d = perkList.getSelect();
			if (d < playerRPG.getNumPerks()) {
				description.addText(playerRPG.getPerk(d).getPerk().getDescription());

			}
		}
	}

	public void initialize(int[] textures) {
		// TODO Auto-generated method stub
		// 0 is font
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint
		window = new Window(new Vec2f(-20, -16.0F), new Vec2f(40, 32), textures[1], true);

		genExperienceNotes(textures[0], textures[4]);
		genAttributePage(textures[1], textures[0], textures[4]);
		genCombatPage(textures[1], textures[0], textures[4]);
		genSkillPage(textures[1], textures[0], textures[4]);
		genButtons(textures[4], textures[2], textures[3]);
		genPerkSection(textures[1], textures[0], textures[4]);
		subWindows[2].setActive(false);

		Text text = new Text(new Vec2f(0.2F, 15.5F), player.getName(), textures[4]);
		window.add(text);
	}

	private void genButtons(int tint, int button, int buttonalt) {
		Button[] buttons = new Button[4];
		// add switch button combat
		buttons[0] = new Button(new Vec2f(24, 26), new Vec2f(8, 2), button, this, "combat", 0, 0.8F);
		// add switch button skills
		buttons[1] = new Button(new Vec2f(32, 26), new Vec2f(8, 2), button, this, "skills", 1, 0.8F);
		// add back button
		buttons[2] = new Button(new Vec2f(32, 28), new Vec2f(8, 2), button, this, "back", 2, 0.8F);
		// ad level up button if necessary
		// buttons[3]=new Button(Vec2f pos, Vec2f size, int texture, int font,
		// MyListener listener,String string, int ID, float s);
		buttons[3] = new Button(new Vec2f(24, 28), new Vec2f(8, 2), button, this, "levelup", 3, 0.8F);
		if (playerRPG.getPlayerExperience() < playerRPG.getNextLevel()) {
			buttons[3].setActive(false);
		}

		for (int i = 0; i < 4; i++) {
			window.add(buttons[i]);
		}
	}

	private void genExperienceNotes(int font, int tint) {
		if (experienceNotes == null) {
			Text text = new Text(new Vec2f(16.0F, 15.2F), "Karma:" + playerRPG.getKarmaMeter(), 0.6F, tint);
			experienceNotes = new Text(new Vec2f(10, 15.2F), "text", 0.6F, tint);
			window.add(text);
			window.add(experienceNotes);
		}

		experienceNotes.setString("level " + playerRPG.getPlayerLevel() + " exp " + playerRPG.getPlayerExperience()
				+ "/" + playerRPG.getNextLevel());
	}

	private void genAttributePage(int frame, int font, int tint) {
		if (subWindows[0] != null) {
			window.Remove(subWindows[0]);
		}
		subWindows[0] = new Window(new Vec2f(0, 24), new Vec2f(24, 6), frame, true);
		window.add(subWindows[0]);
		Text[] text = new Text[6];
		text[0] = new Text(new Vec2f(0.5F, 0.4F), "strength:" + playerRPG.getAbility(Actor_RPG.STRENGTH), 0.8F, tint);
		text[1] = new Text(new Vec2f(0.5F, 1.0F), "endurance:" + playerRPG.getAbility(Actor_RPG.ENDURANCE), 0.8F, tint);
		text[2] = new Text(new Vec2f(0.5F, 1.6F), "dexterity:" + playerRPG.getAbility(Actor_RPG.DEXTERITY), 0.8F, tint);
		text[3] = new Text(new Vec2f(5.5F, 0.6F), "agility:" + playerRPG.getAbility(Actor_RPG.AGILITY), 0.8F, tint);
		text[4] = new Text(new Vec2f(5.5F, 1.0F), "intelligence:" + playerRPG.getAbility(Actor_RPG.INTELLIGENCE), 0.8F,
				tint);
		text[5] = new Text(new Vec2f(5.5F, 1.6F), "charm:" + playerRPG.getAbility(Actor_RPG.CHARM), 0.8F, tint);
		for (int i = 0; i < 6; i++) {
			subWindows[0].add(text[i]);
		}
		Text header = new Text(new Vec2f(3.5F, 2.3F), "abilities", 0.8F, tint);
		subWindows[0].add(header);

	}

	private void genCombatPage(int frame, int font, int tint) {
		if (subWindows[1] != null) {
			window.Remove(subWindows[1]);
		}
		subWindows[1] = new Window(new Vec2f(0, 0), new Vec2f(20, 24), frame, true);
		window.add(subWindows[1]);

		Text[] text = new Text[11];
		// generate texts
		for (int i = 0; i < 11; i++) {
			text[i] = new Text(new Vec2f(0.5F, 10.5F - (0.9F * (float) i)), "", 0.8F, tint);

			subWindows[1].add(text[i]);
		}
		// set contents

		text[0].setString("kineticdef:" + playerRPG.getAttribute(Actor_RPG.KINETIC));
		text[1].setString("thermaldef:" + playerRPG.getAttribute(Actor_RPG.THERMAL));
		text[2].setString("shockdef:" + playerRPG.getAttribute(Actor_RPG.SHOCK));
		text[3].setString("teasedef:" + playerRPG.getAttribute(Actor_RPG.TEASE));
		text[4].setString("pheremonedef:" + playerRPG.getAttribute(Actor_RPG.PHEREMONE));
		text[5].setString("psychicdef:" + playerRPG.getAttribute(Actor_RPG.PSYCHIC));
		text[6].setString("parry:" + playerRPG.getAttribute(Actor_RPG.PARRY));
		text[7].setString("dodge:" + playerRPG.getAttribute(Actor_RPG.DODGE));
		text[8].setString("melee:" + playerRPG.getAttribute(Actor_RPG.MELEE));
		text[9].setString("ranged:" + playerRPG.getAttribute(Actor_RPG.RANGED));
		text[10].setString("seduction:" + playerRPG.getAttribute(Actor_RPG.SEDUCTION));

		Text header = new Text(new Vec2f(0.5F, 11.3F), "combat attributes", 0.8F, tint);
		subWindows[1].add(header);
	}

	private void genSkillPage(int frame, int font, int tint) {
		if (subWindows[2] != null) {
			window.Remove(subWindows[2]);
		}
		subWindows[2] = new Window(new Vec2f(0, 0), new Vec2f(20, 24), frame, true);
		window.add(subWindows[2]);

		Text[] text = new Text[9];
		// generate texts
		for (int i = 0; i < 9; i++) {
			text[i] = new Text(new Vec2f(0.5F, 10.5F - (0.9F * (float) i)), "", 0.8F, tint);

			subWindows[2].add(text[i]);
		}

		// set contents
		text[0].setString("struggle:" + playerRPG.getAttribute(Actor_RPG.STRUGGLE));
		text[1].setString("pleasure:" + playerRPG.getAttribute(Actor_RPG.PLEASURE));
		text[2].setString("persuade:" + playerRPG.getAttribute(Actor_RPG.PERSUADE));
		text[3].setString("willpower:" + playerRPG.getAttribute(Actor_RPG.WILLPOWER));
		text[4].setString("tech:" + playerRPG.getAttribute(Actor_RPG.TECH));
		text[5].setString("science:" + playerRPG.getAttribute(Actor_RPG.SCIENCE));
		text[6].setString("navigation:" + playerRPG.getAttribute(Actor_RPG.NAVIGATION));
		text[7].setString("gunnery:" + playerRPG.getAttribute(Actor_RPG.GUNNERY));
		text[8].setString("perception:" + playerRPG.getAttribute(Actor_RPG.PERCEPTION));
		Text header = new Text(new Vec2f(3.2F, 11.3F), "skills", 0.8F, tint);
		subWindows[2].add(header);
	}

	private void genPerkSection(int frame, int font, int tint) {

		perkList = new List(new Vec2f(0, -6.75F), 16, font, frame, tint, this, 20, false);
		description = new MultiLineText(new Vec2f(20.5F, 6.2F), 16, 64, 0.8F);
		window.add(description);
		Text header = new Text(new Vec2f(2.2F, 7.0F), "perks", 0.8F, tint);

		buildList();

		Window window = new Window(new Vec2f(20, 8.6F), new Vec2f(20, 15.4F), frame, true);
		this.window.add(window);
		window.add(header);

		header = new Text(new Vec2f(12.0F, 3.6F), "description", 0.8F, tint);
		this.window.add(header);
	}

	private void buildList() {

		String[] str = new String[playerRPG.getNumPerks()];
		for (int i = 0; i < playerRPG.getNumPerks(); i++) {
			str[i] = playerRPG.getPerk(i).getPerk().getName();
			if (playerRPG.getPerk(i).getPerkRank() > 1) {
				str[i] = str[i] + " " + playerRPG.getPerk(i).getPerkRank();
			}
		}
		perkList.GenList(str);

	}

	@Override
	public void Draw() {
		GL20.glUseProgram(Game.m_pshader);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		DrawSetup();
		// set tint to default
		GL20.glUniform4f(m_variables[0], 1.0F, 1.0F, 1.0F, 1);

		m_GUImatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniform1fv(m_variables[1], matrix44Buffer);
		GL20.glUniform4f(m_variables[0], 1.0F, 1.0F, 1.0F, 1);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		window.Draw(matrix44Buffer, SceneBase.getVariables()[2]);
		perkList.Draw(matrix44Buffer, SceneBase.getVariables()[2]);

		if (m_screen != null) {
			m_screen.draw(matrix44Buffer, SceneBase.getVariables()[2]);
		}
	}

	@Override
	public void end() {
		CleanTextures();
		window.discard();
		perkList.discard();
	}
}
