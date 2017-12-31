package view;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import java.io.IOException;
import java.util.ArrayList;

import graphics.AnimatedFXControl;
import graphics.FX;
import graphics.FX_projectile;
import graphics.Screen_Fade;
import gui.BarFrame;
import gui.Button2;
import gui.DropDown;
import gui.TextView;
import gui.Window;
import help.Help_Scene;
import input.Keyboard;
import input.MouseHook;
import item.Item;
import menu.Menu;
import nomad.GameOver;
import nomad.Universe;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dialogue.DialogueScreen;
import combat.CombatMove;
import playerscreens.AppearanceScreen;
import playerscreens.CharacterScene;
import playerscreens.InventoryScreen;
import rendering.WorldView;
import shared.Callback;
import shared.MyListener;
import shared.ParserHelper;
import shared.SceneBase;
import shared.Scene_Int;
import shared.Screen;
import shared.Tools;
import shared.Vec2f;
import vmo.Game;
import vmo.GameManager;
import widgets.Widget;
import widgets.WidgetConversation;
import widgets.WidgetItemPile;
import zone.TileDef;
import zone.TileDef.TileMovement;
import zone.Zone;
import actor.Actor;
import actor.player.Player;
import actor.ranked.RankedNPC;
import actor.npc.NPC;
import actor.npc.Temp_NPC;
import actor.player.CompanionTool;
import actorRPG.Actor_RPG;
import actorRPG.player.Player_RPG;
import artificial_intelligence.BrainBank;
import artificial_intelligence.detection.Sense;

public class ViewScene extends SceneBase implements ModelController_Int, Scene_Int, MyListener, Callback {

	public static ModelController_Int m_interface;

	/*
	 * booleans determine whether to run the 'handle closure' tool for when the player leaves a selection 
	 * screen without clicking an option by moving the mouse outside the window, do not set special(true)
	 */
	enum ViewMode {
		SELECT(true), FIGHT(true), DOMINATE(true), MOVEMENT(true), OTHER(true), LOOK(false), INTERACT(false), ATTACK(
				false), SPECIAL(false);
		boolean value;

		ViewMode(boolean value) {
			this.value = value;
		}

		boolean getValue() {
			return value;
		}

	};

	ViewMode m_mode;

	SceneController sceneController;

	TextView m_text;
	Matrix4f m_GUImatrix;
	WorldView m_view;

	float m_time;
	MouseHook m_hook;
	Window m_window;
	BarFrame m_bars[];
	Button2 m_buttons[];
	// ZoneInteractionHandler m_handler;
	Screen m_screen;
	AnimatedFXControl FXanimationControl;
	DropDown m_dropdown;
	HandReader m_reader;
	boolean m_disabled;
	IconBox statusDisplay;
	CooldownDisplay cooldownDisplay;
	Targeting targeter;
	Screen_Fade screenFade;

	public ViewScene(int[] variables, Universe world) {
		super(variables);

		sceneController = new SceneController();
		sceneController.setUniverse(world);

		m_interface = this;
		m_mode = ViewMode.LOOK;

		m_GUImatrix = Game.sceneManager.getConfig().getMatrix();
		sceneController.getUniverse().currentZone.ClearVisibleTiles();
		sceneController.initializeHandler(this);
		sceneController.setActiveZone(sceneController.getUniverse().currentZone);

		SetupTextures();
		sceneController.getActiveZone().viewInterface = this;

		sceneController.getActiveZone().AddPlayer(sceneController.getUniverse().player);

		GameManager.m_vision.visitFieldOfView(sceneController.getActiveZone(),
				(int) sceneController.getUniverse().player.getPosition().x,
				(int) sceneController.getUniverse().player.getPosition().y, 10);
		m_view = new WorldView();
		m_view.generate(sceneController.getActiveZone());
		m_time = 0;
		Setup();

		m_text = new TextView(m_textureIds[1], new Vec2f(-20, -16.0F), new Vec2f(40, 15), SceneBase.getVariables()[0]);
		m_window = new Window(new Vec2f(3, -1), new Vec2f(17, 17), m_textureIds[0], true);
		if (sceneController.getActiveZone().zoneDescription != null) {
			m_text.AddText(sceneController.getActiveZone().zoneDescription);
			m_text.BuildStrings();
		}
		m_bars = new BarFrame[4];
		m_buttons = new Button2[5];
		for (int i = 0; i < 4; i++) {

			String str1 = null;
			String str = null;
			switch (i) {
			case 0:
				str = "health";

				str1 = "inventory";
				break;
			case 1:
				str = "resolve";

				str1 = "appearance";
				break;
			case 2:
				str = "satiation";

				str1 = "character";
				break;
			case 3:
				str = "action";

				str1 = "file";
				break;
			}

			m_buttons[i] = new Button2(new Vec2f(8.5F, 6.5F - (i * 2)), new Vec2f(8, 2), m_textureIds[7], this, str1, i,
					m_textureIds[8], 1.2F);
			if (i < 4) {
				m_bars[i] = new BarFrame(new Vec2f(8.5F, 15.7F - (i * 1.0F)), str,
						sceneController.getUniverse().player.getRPG().getStat(i),
						sceneController.getUniverse().player.getRPG().getStatMax(i), 1 + i, m_textureIds[6]);
				m_window.add(m_bars[i]);
			}
			m_window.add(m_buttons[i]);

		}

		m_buttons[4] = new Button2(new Vec2f(0.5F, 0.5F), new Vec2f(8, 2), m_textureIds[7], this, "look", 4,
				m_textureIds[8], 1);
		m_window.add(m_buttons[4]);

		// m_handler=new
		// ZoneInteractionHandler(sceneController.getActiveZone(),this);

		// m_screen=new
		// InventoryScreen(m_textureIds[1],m_textureIds[0],m_textureIds[7],m_textureIds[8],m_world.m_player,m_variables[0],this);

		FXanimationControl = new AnimatedFXControl();

		m_dropdown = new DropDown(m_textureIds[0], new Vec2f(0, 0), new Vec2f(7, 4), this, SceneBase.getVariables()[0]);
		genStandardDropDown();
		m_dropdown.setVisible(false);
		m_reader = new HandReader(new Vec2f(0.0F, 2.5F));
		m_window.add(m_reader);
		m_reader.setPlayer(sceneController.getUniverse().player);
		m_reader.UpdateHand();

		statusDisplay = new IconBox(new Vec2f(1, 9.9F));
		cooldownDisplay = new CooldownDisplay(new Vec2f(1, 11.9F));
		m_window.add(statusDisplay);
		m_window.add(cooldownDisplay);
		redrawBars();
		LinkSenses();
		cooldownDisplay.redraw();
		targeter = new Targeting();
		screenFade = new Screen_Fade(this);
	}

	@Override
	public SceneController getSceneController() {
		return sceneController;
	}

	private void commonConstruct()
	{
		sceneController = new SceneController();
		sceneController.setUniverse(Universe.getInstance());

		m_interface = this;
		m_mode = ViewMode.LOOK;

		m_GUImatrix = Game.sceneManager.getConfig().getMatrix();
		sceneController.getUniverse().currentZone.ClearVisibleTiles();
		sceneController.initializeHandler(this);
		sceneController.setActiveZone(sceneController.getUniverse().currentZone);

		SetupTextures();
		sceneController.getActiveZone().viewInterface = this;

		sceneController.getActiveZone().AddPlayer(sceneController.getUniverse().player);

		GameManager.m_vision.visitFieldOfView(sceneController.getActiveZone(),
				(int) sceneController.getUniverse().player.getPosition().x,
				(int) sceneController.getUniverse().player.getPosition().y, 10);
		m_view = new WorldView();
		m_view.generate(sceneController.getActiveZone());
		m_time = 0;
		Setup();

		m_text = new TextView(m_textureIds[1], new Vec2f(-20, -16.0F), new Vec2f(40, 15), SceneBase.getVariables()[0]);
		m_window = new Window(new Vec2f(3, -1), new Vec2f(17, 17), m_textureIds[0], true);
		if (sceneController.getActiveZone().zoneDescription != null) {
			m_text.AddText(sceneController.getActiveZone().zoneDescription);
			m_text.BuildStrings();
		}
		m_bars = new BarFrame[4];
		m_buttons = new Button2[5];
		for (int i = 0; i < 4; i++) {

			String str1 = null;
			String str = null;
			switch (i) {
			case 0:
				str = "health";

				str1 = "inventory";
				break;
			case 1:
				str = "resolve";

				str1 = "appearance";
				break;
			case 2:
				str = "satiation";

				str1 = "character";
				break;
			case 3:
				str = "action";

				str1 = "file";
				break;
			}

			m_buttons[i] = new Button2(new Vec2f(8.5F, 6.5F - (i * 2)), new Vec2f(8, 2), m_textureIds[7], this, str1, i,
					m_textureIds[8], 1.2F);
			if (i < 4) {
				m_bars[i] = new BarFrame(new Vec2f(8.5F, 15.7F - (i * 1.0F)), str,
						sceneController.getUniverse().player.getRPG().getStat(i),
						sceneController.getUniverse().player.getRPG().getStatMax(i), 1 + i, m_textureIds[6]);
				m_window.add(m_bars[i]);
			}
			m_window.add(m_buttons[i]);

		}

		m_buttons[4] = new Button2(new Vec2f(0.5F, 0.5F), new Vec2f(8, 2), m_textureIds[7], this, "look", 4,
				m_textureIds[8], 1);
		m_window.add(m_buttons[4]);

		// m_screen=new
		// InventoryScreen(m_textureIds[1],m_textureIds[0],m_textureIds[7],m_textureIds[8],m_world.m_player,m_variables[0],this);

		FXanimationControl = new AnimatedFXControl();

		m_dropdown = new DropDown(m_textureIds[0], new Vec2f(0, 0), new Vec2f(7, 4), this, SceneBase.getVariables()[0]);
		String str0[] = new String[4];
		str0[0] = "look";
		str0[1] = "interact";
		str0[2] = "attack";
		str0[3] = "seduce";
		m_dropdown.BuildFonts(str0);
		m_dropdown.setVisible(false);
		m_reader = new HandReader(new Vec2f(0.0F, 2.5F));
		m_window.add(m_reader);
		m_reader.setPlayer(sceneController.getUniverse().player);
		m_reader.UpdateHand();

		statusDisplay = new IconBox(new Vec2f(1, 9.9F));
		cooldownDisplay = new CooldownDisplay(new Vec2f(1, 11.9F));
		m_window.add(cooldownDisplay);
		m_window.add(statusDisplay);

		redrawBars();
		LinkSenses();
		cooldownDisplay.redraw();
		targeter = new Targeting();
		screenFade = new Screen_Fade(this);		
	}
	
	public ViewScene() {
		 commonConstruct();
	}

	public ViewScene(ArrayList<String> strings) {
		 commonConstruct();
		m_text.AddTexts(strings);
	}

	void Setup() {

		m_viewmatrix.translate(new Vector2f(-1 * sceneController.getUniverse().player.getPosition().x - 9,
				-1 * sceneController.getUniverse().player.getPosition().y + 7));
	}

	public void LinkSenses() {
		for (int i = 0; i < sceneController.getActiveZone().getActors().size(); i++) {
			if (sceneController.getActiveZone().getActors().get(i).getClass().getName().contains("NPC")) {
				NPC npc = (NPC) sceneController.getActiveZone().getActors().get(i);
				npc.setSense(sceneController);
			}
		}

	}

	void SetupTextures() {
		m_textureIds = new int[10];
		// first is square
		// 2nd is font
		m_textureIds[0] = Tools.loadPNGTexture("assets/art/ninepatchblack.png", GL13.GL_TEXTURE0);
		m_textureIds[1] = Tools.loadPNGTexture("assets/art/textWindow.png", GL13.GL_TEXTURE0);
		m_textureIds[2] = Tools.loadPNGTexture("assets/art/spritesheet.png", GL13.GL_TEXTURE0);
		m_textureIds[3] = Tools.loadPNGTexture("assets/art/" + sceneController.getActiveZone().getTileset(),
				GL13.GL_TEXTURE0);
		m_textureIds[4] = Tools.loadPNGTexture("assets/art/window.png", GL13.GL_TEXTURE0);
		m_textureIds[5] = Tools.loadPNGTexture("assets/art/widgets.png", GL13.GL_TEXTURE0);
		m_textureIds[6] = Tools.loadPNGTexture("assets/art/bars.png", GL13.GL_TEXTURE0);
		m_textureIds[7] = Tools.loadPNGTexture("assets/art/button0.png", GL13.GL_TEXTURE0);
		m_textureIds[8] = Tools.loadPNGTexture("assets/art/button1.png", GL13.GL_TEXTURE0);
		m_textureIds[9] = Tools.loadPNGTexture("assets/art/listWindow.png", GL13.GL_TEXTURE0);	
	}

	public void redrawBars() {
		for (int i = 0; i < 4; i++) {
			m_bars[i].setValue(sceneController.getUniverse().player.getRPG().getStat(i));
		}

		statusDisplay.redraw();
		cooldownDisplay.redraw();

	}

	void addTime() {
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || Keyboard.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)
				|| Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || Keyboard.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL)
				|| screenFade.active()) {
			m_time = 0.025F;
		} else {
			m_time = 0.1F;
		}
	}

	void UpdateLogic(float dt) {
		if (m_screen != null) {
			m_screen.update(dt);
			if (sceneController.getUniverse().player.CanAct() == false) {
				if (m_disabled == false) {
					WorldUpdate();
				}
				addTime();
				redrawBars();
			}
		} else {
			m_text.update(dt);
			sceneController.getUniverse().player.controlUpdate(dt);
			if (m_time <= 0) {
				if (sceneController.getUniverse().player.CanAct()) {
					if (targeter.isActive()) {
						if (targeter.update(dt)) {
							addTime();
							m_view.vision(sceneController.getActiveZone(), sceneController.getActiveZone().zoneActors,
									sceneController.getUniverse().getPlayer().getPosition());
							redrawBars();
							GameManager.AddClock(1);

						}
					} else {
						if (sceneController.getUniverse().player.Control() || Click()) {
							if (TransitionCheck() == false) {
								sceneController.getActiveZone().zoneTileGrid[(int) sceneController.getUniverse().player
										.getPosition().x][(int) sceneController.getUniverse().player.getPosition().y]
												.Step();
								m_viewmatrix.m30 = 0;
								m_viewmatrix.m31 = 0;

								m_viewmatrix.translate(
										new Vector2f(-1 * sceneController.getUniverse().player.getPosition().x - 9,
												-1 * sceneController.getUniverse().player.getPosition().y + 7));
								sceneController.getActiveZone().ClearVisibleTiles();
								GameManager.m_vision.visitFieldOfView(sceneController.getActiveZone(),
										(int) sceneController.getUniverse().player.getPosition().x,
										(int) sceneController.getUniverse().player.getPosition().y, 10);
								m_view.vision(sceneController.getActiveZone(),
										sceneController.getActiveZone().zoneActors,
										sceneController.getUniverse().player.getPosition());
								addTime();
								redrawBars();
								cooldownDisplay.update(0);
								GameManager.AddClock(1);
							}
						}
					}
				} else {
					WorldUpdate();
					addTime();
					for (int i = 0; i < 3; i++) {
						m_bars[i].setValue(sceneController.getUniverse().player.getRPG().getStat(i));
					}
					GameManager.AddClock(1);
				}
				keyboardHotkeys();

			}
			if (m_time > 0) {
				m_time -= dt;
			}
		}
	}

	@Override
	public void Update(float dt) {
		m_dropdown.update(dt);
		if (!m_dropdown.getVisible())
		{
			if (!m_buttons[4].getVisible())
			{
				m_buttons[4].setActive(true);						
			}
			m_window.update(dt);			
		}
		else
		{
			m_buttons[4].setActive(false);		
		}
		if (m_dropdown.getVisible() == false && m_mode.getValue()) {
			m_mode = DropdownHandler.handleClosure(m_mode, Universe.getInstance().getPlayer(), m_buttons[4]);
		}
		if (FXanimationControl.getActive() == true) {
			FXanimationControl.Update();
		} else {
			UpdateLogic(dt);
		}

		screenFade.update(dt);
	}

	boolean Click() {
		if (m_hook.buttonDown() && m_hook.IsEnabled() == true && m_screen == null && m_dropdown.getVisible() == false) {
			Vec2f p = m_hook.getPosition();
			if (p.x < 3 && p.y > -1) {
				p = new Vec2f(p.x, p.y);
				p.x += 9 + sceneController.getUniverse().player.getPosition().x;
				p.y += -7 + sceneController.getUniverse().player.getPosition().y;
				if (sceneController.getHandler().ContainsPoint(p)) {
					// look up this tile
					switch (m_mode) {
					case SELECT:
						m_mode = ViewMode.SELECT;
						m_buttons[4].setString("attack");
						break;

					case LOOK:
						sceneController.getHandler().Look(p);
						break;

					case INTERACT:
						sceneController.getHandler().Interact(p, sceneController.getUniverse().player);
						break;

					case ATTACK:

						sceneController.getHandler().attack(p, sceneController.getUniverse().player);
						m_reader.UpdateEnergy();
						redrawBars();
						break;

					case SPECIAL:
						// sceneController.getHandler().Seduce(p,sceneController.getUniverse().player);
						if (!sceneController.getHandler().special(p, sceneController.getUniverse().player)) {
							// m_dropdown.setVisible(true);
							// m_dropdown.AdjustPos(new
							// Vec2f(p.x-1.0F,p.y-1.0F));
						}
						redrawBars();
						m_reader.UpdateEnergy();
						break;
					}
				}

			}

		}
		return false;
	}

	public Vec2f Transition(String destination, int id) {
		BrainBank.getInstance().cleanActives();
		sceneController.getUniverse().getPlayer().getThreat().reset();
		// find zone
		Zone zone = sceneController.getUniverse().getZone(destination);
		zone.LoadZone();
		sceneController.getActiveZone().cleanup();
		sceneController.setActiveZone(zone);
		// initialize zone if uninitialized
		sceneController.getUniverse().setZone(sceneController.getActiveZone());

		// sceneController.getActiveZone().LoadZone();
		if (!sceneController.getActiveZone().isVisited() && sceneController.getActiveZone().zoneDescription != null) {
			m_text.AddText(sceneController.getActiveZone().zoneDescription);
			m_text.BuildStrings();
			sceneController.getActiveZone().setVisited(true);
		}

		sceneController.getActiveZone().viewInterface = this;
		Vec2f v = sceneController.getActiveZone().getPortal(id);
		sceneController.getUniverse().player.setPosition(new Vec2f(v.x, v.y));
		// add player if player isnt already added
		sceneController.getActiveZone().AddPlayer(sceneController.getUniverse().player);
		// place player at coordinates
		// m_handler.setZone(sceneController.getActiveZone());


		// run vision algorithm
		sceneController.getActiveZone().RegenZone();
		CompanionTool.moveCompanions(sceneController.getUniverse().getPlayer(), sceneController.getActiveZone());

		sceneController.getActiveZone().ClearVisibleTiles();

		GameManager.m_vision.visitFieldOfView(sceneController.getActiveZone(),
				(int) sceneController.getUniverse().player.getPosition().x,
				(int) sceneController.getUniverse().player.getPosition().y, 10);
		m_view.End();
		// reset the view

		// texture load
		GL11.glDeleteTextures(m_textureIds[3]);
		m_textureIds[3] = Tools.loadPNGTexture("assets/art/" + sceneController.getActiveZone().getTileset(),
				GL13.GL_TEXTURE0);

		m_view = new WorldView();
		m_view.generate(sceneController.getActiveZone());
		// move camera
		m_viewmatrix.m30 = 0;
		m_viewmatrix.m31 = 0;

		m_viewmatrix.translate(new Vector2f(-1 * sceneController.getUniverse().player.getPosition().x - 9,
				-1 * sceneController.getUniverse().player.getPosition().y + 7));

		LinkSenses();

		try {
			char result = sceneController.getUniverse().autoSave();
			if (result == 1) {
				m_text.AddText("AUTOSAVE INTEGRITY CHECK FAILED");
			}
		} catch (IOException e) {
			m_text.AddText("IOEXCEPTION IN AUTOSAVE");
		}

		return null;
	}

	void Transition(int side, String destination) {
		BrainBank.getInstance().cleanActives();
		sceneController.getUniverse().getPlayer().getThreat().reset();		
		// find zone
		sceneController.getActiveZone().cleanup();
		sceneController.setActiveZone(sceneController.getUniverse().getZone(destination));
		sceneController.getUniverse().currentZone = sceneController.getActiveZone();
		// initialize zone if uninitialized

		sceneController.getActiveZone().LoadZone();
		if (!sceneController.getActiveZone().isVisited() && sceneController.getActiveZone().zoneDescription != null) {
			m_text.AddText(sceneController.getActiveZone().zoneDescription);
			m_text.BuildStrings();
			sceneController.getActiveZone().setVisited(true);
		}
		// m_handler.setZone(sceneController.getActiveZone());

		sceneController.getActiveZone().viewInterface = this;

		// add player if player isnt already added
		sceneController.getActiveZone().AddPlayer(sceneController.getUniverse().player);
		// place player at the edge
		switch (side) {
		case 0:
			// player to south edge
			sceneController.getUniverse().player
					.setPosition(new Vec2f(sceneController.getUniverse().player.getPosition().x, 1));
			break;
		case 1:
			// player to the east edge
			sceneController.getUniverse().player
					.setPosition(new Vec2f(1, sceneController.getUniverse().player.getPosition().y));

			break;
		case 2:
			// player to north edge
			sceneController.getUniverse().player
					.setPosition(new Vec2f(sceneController.getUniverse().player.getPosition().x,
							sceneController.getActiveZone().zoneHeight - 2));
			break;
		case 3:
			// player to west edge
			sceneController.getUniverse().player.setPosition(new Vec2f(sceneController.getActiveZone().zoneWidth - 2,
					sceneController.getUniverse().player.getPosition().y));
			break;
		}
		Safety(side);
		sceneController.getActiveZone().RegenZone();

		CompanionTool.moveCompanions(sceneController.getUniverse().getPlayer(), sceneController.getActiveZone());

		// run vision algorithm
		sceneController.getActiveZone().ClearVisibleTiles();
		GameManager.m_vision.visitFieldOfView(sceneController.getActiveZone(),
				(int) sceneController.getUniverse().player.getPosition().x,
				(int) sceneController.getUniverse().player.getPosition().y, 10);
		m_view.End();
		// reset the view

		// texture load
		GL11.glDeleteTextures(m_textureIds[3]);
		m_textureIds[3] = Tools.loadPNGTexture("assets/art/" + sceneController.getActiveZone().getTileset(),
				GL13.GL_TEXTURE0);

		m_view = new WorldView();
		m_view.generate(sceneController.getActiveZone());
		// move camera
		m_viewmatrix.m30 = 0;
		m_viewmatrix.m31 = 0;

		m_viewmatrix.translate(new Vector2f(-1 * sceneController.getUniverse().player.getPosition().x - 9,
				-1 * sceneController.getUniverse().player.getPosition().y + 7));

		LinkSenses();

		try {
			sceneController.getUniverse().autoSave();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void Safety(int side) {
		Vec2f p = new Vec2f(sceneController.getUniverse().player.getPosition().x,
				sceneController.getUniverse().player.getPosition().y);
		if (sceneController.getActiveZone().passable((int) p.x, (int) p.y, false) == false) {
			if (side == 0 || side == 2) {
				if (p.x > sceneController.getActiveZone().zoneWidth / 2) {
					p.x += -1;
					while (sceneController.getActiveZone().passable((int) p.x, (int) p.y, false) == false) {
						p.x = p.x - 1;
					}
				} else {
					p.x += 1;
					while (sceneController.getActiveZone().passable((int) p.x, (int) p.y, false) == false) {
						p.x = p.x + 1;
					}
				}
			}
			if (side == 1 || side == 3) {
				if (p.y > sceneController.getActiveZone().zoneHeight / 2) {
					p.y += -1;
					while (sceneController.getActiveZone().passable((int) p.x, (int) p.y, false) == false) {
						p.y = p.y - 1;
					}
				} else {

					p.y -= 1;
					while (sceneController.getActiveZone().passable((int) p.x, (int) p.y, false) == false) {
						p.y = p.y + 1;
					}
				}
			}
			sceneController.getUniverse().player.setPosition(p);
		}
	}

	public void Transition(String destination, int x, int y) {
		BrainBank.getInstance().cleanActives();
		sceneController.getUniverse().getPlayer().getThreat().reset();	
		// find zone
		sceneController.getActiveZone().cleanup();
		sceneController.setActiveZone(sceneController.getUniverse().getZone(destination));
		// initialize zone if uninitialized
		sceneController.getUniverse().currentZone = sceneController.getActiveZone();

		sceneController.getActiveZone().LoadZone();
		if (!sceneController.getActiveZone().isVisited() && sceneController.getActiveZone().zoneDescription != null) {
			m_text.AddText(sceneController.getActiveZone().zoneDescription);
			m_text.BuildStrings();
			sceneController.getActiveZone().setVisited(true);
		}

		sceneController.getActiveZone().viewInterface = this;

		// add player if player isnt already added
		sceneController.getActiveZone().AddPlayer(sceneController.getUniverse().player);
		// place player at coordinates
		// m_handler.setZone(sceneController.getActiveZone());

		sceneController.getUniverse().player.setPosition(new Vec2f(x, y));

		sceneController.getActiveZone().RegenZone();
		CompanionTool.moveCompanions(sceneController.getUniverse().getPlayer(), sceneController.getActiveZone());

		// run vision algorithm
		sceneController.getActiveZone().ClearVisibleTiles();
		GameManager.m_vision.visitFieldOfView(sceneController.getActiveZone(),
				(int) sceneController.getUniverse().player.getPosition().x,
				(int) sceneController.getUniverse().player.getPosition().y, 10);
		m_view.End();
		// reset the view

		// texture load
		GL11.glDeleteTextures(m_textureIds[3]);
		m_textureIds[3] = Tools.loadPNGTexture("assets/art/" + sceneController.getActiveZone().getTileset(),
				GL13.GL_TEXTURE0);

		m_view = new WorldView();
		m_view.generate(sceneController.getActiveZone());
		// move camera
		m_viewmatrix.m30 = 0;
		m_viewmatrix.m31 = 0;

		m_viewmatrix.translate(new Vector2f(-1 * sceneController.getUniverse().player.getPosition().x - 9,
				-1 * sceneController.getUniverse().player.getPosition().y + 7));

		LinkSenses();

		try {
			sceneController.getUniverse().autoSave();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	boolean TransitionCheck() {
		if (sceneController.getUniverse().player.getPosition().x == 0) {
			if (sceneController.getActiveZone().adjacentZoneNames[3] != null) {
				Transition(3, sceneController.getActiveZone().adjacentZoneNames[3]);
				return true;
			}
		}
		if (sceneController.getUniverse().player.getPosition().x == sceneController.getActiveZone().zoneWidth - 1) {
			if (sceneController.getActiveZone().adjacentZoneNames[1] != null) {
				Transition(1, sceneController.getActiveZone().adjacentZoneNames[1]);
				return true;
			}
		}
		if (sceneController.getUniverse().player.getPosition().y == 0) {
			if (sceneController.getActiveZone().adjacentZoneNames[2] != null) {
				Transition(2, sceneController.getActiveZone().adjacentZoneNames[2]);
				return true;
			}
		}
		if (sceneController.getUniverse().player.getPosition().y == sceneController.getActiveZone().zoneHeight - 1) {
			if (sceneController.getActiveZone().adjacentZoneNames[0] != null) {
				Transition(0, sceneController.getActiveZone().adjacentZoneNames[0]);
				return true;
			}
		}
		return false;
	}

	public void WorldUpdate() {

		if (m_screen == null) {
			// to only initiate the conversation one time
			sceneController.update();
		}
		BrainBank.getInstance().runMaster();
	}

	@Override
	public void Draw() {
		GL20.glUseProgram(Game.m_pshader);
		DrawSetup();

		GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);

		GL20.glUseProgram(Game.m_pshadowshader);

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		m_viewmatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(m_variables[3], false, matrix44Buffer);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_textureIds[3]);
		m_view.draw(m_variables[4], matrix44Buffer);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_textureIds[5]);
		// m_view.Draw(m_variables[2], matrix44Buffer);
		m_view.drawWidgets(m_variables[4], matrix44Buffer);

		m_view.drawImages(m_variables[4], matrix44Buffer);
		// GL20.glUniform4f(m_variables[0],1.0F,1.0F,1.0F, 1);
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_textureIds[3]);
		// m_view.drawOverlay(m_variables[4], matrix44Buffer);

		GL20.glUseProgram(Game.m_pshader);
		GL20.glUniform4f(m_variables[0], 1.0F, 1.0F, 1.0F, 1);
		m_viewmatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(m_variables[1], false, matrix44Buffer);
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_textureIds[2]);
		m_view.drawSquares(m_variables[2], m_variables[0], matrix44Buffer);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_textureIds[2]);
		FXanimationControl.Draw(m_variables[2], m_variables[0], matrix44Buffer);
		targeter.draw(m_variables[2], m_variables[0], matrix44Buffer);
		// set tint to default
		GL20.glUniform4f(m_variables[0], 1.0F, 1.0F, 1.0F, 1);

		m_GUImatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(m_variables[1], false, matrix44Buffer);
		GL20.glUniform4f(m_variables[0], 1.0F, 1.0F, 1.0F, 1);

		m_window.Draw(matrix44Buffer, SceneBase.getVariables()[2]);

		if (m_screen == null) {
			m_text.Draw(matrix44Buffer, SceneBase.getVariables()[2]);
			GL20.glUniform4f(m_variables[0], 1, 1, 1, 1);
			m_dropdown.Draw(matrix44Buffer, SceneBase.getVariables()[2]);

		} else {
			m_screen.draw(matrix44Buffer, SceneBase.getVariables()[2]);
		}

		screenFade.draw(SceneBase.getVariables()[2], matrix44Buffer);
	}

	@Override
	public void start(MouseHook mouse) {
		// TODO Auto-generated method stub
		m_hook = mouse;
		for (int i = 0; i < 5; i++) {
			mouse.Register(m_buttons[i]);
		}
		if (m_screen != null) {
			m_screen.start(m_hook);
		}
		if (m_dropdown != null) {
			mouse.Register(m_dropdown);
			m_dropdown.setHook(mouse);
		}

	}

	@Override
	public void end() {
		sceneController.shutdown();
		CleanTextures();
		m_view.End();
		m_text.discard();
		m_window.discard();

		if (m_screen != null) {
			m_screen.discard(m_hook);
		}

		FXanimationControl.discard();

		m_hook.Clean();
		targeter.discard();
		m_dropdown.discard();
		m_interface = null;
		screenFade.discard();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {

		if (m_screen == null && m_dropdown.getVisible() == false) {

			switch (ID) {
			case 0:
				m_screen = new InventoryScreen(m_textureIds[0],m_textureIds[1], m_textureIds[7], m_textureIds[8],
						sceneController.getUniverse().player, m_variables[0], this);
				m_screen.start(m_hook);
				break;
			case 1:
				// appearance
				setScreen(new AppearanceScreen());
				break;
			case 2:
				// character
				CharacterScene screen = new CharacterScene();

				Game.sceneManager.SwapScene(screen);
				break;
			case 3:
				Game.sceneManager.SwapScene(new Menu(SceneBase.getVariables()));
				break;

			case 4:
				if (!m_dropdown.getVisible())
				{
					m_dropdown.setVisible(true);				
					DropdownHandler.genStandardDropdown(m_dropdown, sceneController.getUniverse().player);
					m_dropdown.AdjustPos(new Vec2f(p.x - 1.0F, p.y - 1.0F));				
				}
				break;
			}

		}
		if (m_dropdown.getVisible() == true && ID == 12) {
			handleDropdown();
		}

	}

	void genStandardDropDown() {
		String str0[] = new String[4];
		str0[0] = "look";
		str0[1] = "interact";
		str0[2] = "attack";
		str0[3] = "special";
		m_dropdown.BuildFonts(str0);
	}

	void genSelectDropDown() {

		int s = Universe.getInstance().getPlayer().getMoveCount();
		String str0[] = new String[s];
		for (int i = 0; i < s; i++) {
			CombatMove move = Universe.getInstance().getPlayer().getMove(i + 1);
			if (move != null) {
				str0[i] = move.getMoveName();
			}
		}
		m_dropdown.BuildFonts(str0);
	}

	private void keyboardHotkeys() {
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_F1)) {
			Game.sceneManager.SwapScene(new Help_Scene());
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_F2)) {
			m_screen = new QuickActionSelector(m_textureIds[0], m_textureIds[7], m_textureIds[8],
					sceneController.getUniverse().player, m_variables[0], this);
			m_screen.start(m_hook);
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_Q)) {
			sceneController.useQuickslot();
			m_time = 0.2F;
		}

		if (m_mode != ViewMode.SELECT) {

			if (Keyboard.isKeyDown(GLFW.GLFW_KEY_L)) {
				m_mode = ViewMode.LOOK;
				m_buttons[4].setString("look");
				m_time = 0.2F;
			}
			if (Keyboard.isKeyDown(GLFW.GLFW_KEY_I)) {
				m_mode = ViewMode.INTERACT;
				m_buttons[4].setString("interact");
				m_time = 0.2F;
			}
			if (Keyboard.isKeyDown(GLFW.GLFW_KEY_K)) {
				m_mode = ViewMode.ATTACK;
				m_buttons[4].setString("attack");
				m_time = 0.2F;
			}
			if (Keyboard.isKeyDown(GLFW.GLFW_KEY_P)) {
				DropdownHandler.genSpecialDropdown(m_dropdown, Universe.getInstance().getPlayer());
				m_mode = ViewMode.SELECT;
				m_dropdown.setVisible(true);
				m_time = 0.2F;
			}
			if (Keyboard.isKeyDown(GLFW.GLFW_KEY_O)) {
				toggleTargeting();
				m_time = 0.2F;
			}
		}
	}

	void toggleTargeting() {
		if (targeter.isActive()) {
			targeter.setActive(false);

		} else {
			targeter.genList(sceneController.getActiveZone(), sceneController.getUniverse().getPlayer());
			targeter.setActive(true);
		}
	}

	void handleDropdown() {
		if (m_mode.getValue()) {
			m_mode = DropdownHandler.selectMove(Universe.getInstance().getPlayer(), m_mode, m_dropdown.getSelect(),
					m_buttons[4], m_dropdown);

		} else {
			if (m_dropdown.getSelect() == 3) {
				DropdownHandler.genSpecialDropdown(m_dropdown, Universe.getInstance().getPlayer());
				m_mode = ViewMode.SELECT;
			} else {
				// visibility off, set mode
				Player_RPG rpg = (Player_RPG) Universe.getInstance().getPlayer().getRPG();
				switch (m_dropdown.getSelect()) {
				case 0:
					m_mode = ViewMode.LOOK;
					m_buttons[4].setString("look");
					break;
				case 1:
					m_mode = ViewMode.INTERACT;
					m_buttons[4].setString("interact");
					break;
				case 2:
					m_mode = ViewMode.ATTACK;
					m_buttons[4].setString("attack");
					Universe.getInstance().getPlayer().setMove(0);
					break;
				case 4:
					m_mode = ViewMode.SPECIAL;
					if (rpg.useQuickMove()) {
						m_buttons[4].setString(rpg.getQuickAction());
					}

					break;
				}
				m_dropdown.setVisible(false);
				m_buttons[4].setActive(true);
			}
		}

	}

	void setMode(int value) {

		switch (value) {
		case 0:
			m_mode = ViewMode.LOOK;
			m_buttons[4].setString("look");
			break;
		case 1:
			m_mode = ViewMode.INTERACT;
			m_buttons[4].setString("interact");
			break;
		case 2:
			m_mode = ViewMode.ATTACK;
			m_buttons[4].setString("attack");
			break;

		case 3:
			m_mode = ViewMode.SPECIAL;
			m_buttons[4].setString("special");
			break;

		}
	}

	@Override
	public void DrawText(String string) {
		// TODO Auto-generated method stub
		m_text.AddText(string);
		m_text.BuildStrings();
	}

	@Override
	public void Remove() {

		if (m_screen != null) {
			m_disabled = false;
			m_screen.discard(m_hook);
			m_reader.UpdateHand();
			m_screen = null;
		}

	}

	boolean PlaceItemPile(int x, int y, Item item) {
		if (sceneController.getActiveZone().zoneTileGrid[x][y].getWidgetObject() != null) {
			// check its an item pile
			if (sceneController.getActiveZone().zoneTileGrid[x][y].getWidgetObject().getClass().getName()
					.contains("ItemPile")) {
				WidgetItemPile pile = (WidgetItemPile) sceneController.getActiveZone().zoneTileGrid[x][y]
						.getWidgetObject();
				pile.AddItem(item);
				return true;
			}
		} else {
			// build item pile
			WidgetItemPile pile = new WidgetItemPile(2, "a pile of items containing ", item);
			sceneController.getActiveZone().zoneTileGrid[x][y].setWidget(pile);
			m_view.vision(sceneController.getActiveZone(), sceneController.getActiveZone().zoneActors,
					sceneController.getUniverse().player.getPosition());
			return true;

		}

		return false;
	}

	@Override
	public boolean Drop(int x, int y, Item item) {
		// check initial tile
		if (PlaceItemPile(x, y, item)) {
			return true;
		}
		// check adjacent tiles if that fails
		for (int i = x - 1; i < x + 2; i++) {
			for (int j = y - 1; j < y + 2; j++) {
				if (sceneController.getActiveZone().contains(i, j) && i != x && j != y) {
					if (PlaceItemPile(i, j, item)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void RemoveWidget(Widget widget) {

		for (int i = 0; i < sceneController.getActiveZone().zoneWidth; i++) {
			for (int j = 0; j < sceneController.getActiveZone().zoneHeight; j++) {
				if (sceneController.getActiveZone().zoneTileGrid[i][j] != null) {
					if (sceneController.getActiveZone().zoneTileGrid[i][j].getWidgetObject() == widget) {
						sceneController.getActiveZone().zoneTileGrid[i][j].setWidget(null);
						m_view.vision(sceneController.getActiveZone(), sceneController.getActiveZone().zoneActors,
								sceneController.getUniverse().player.getPosition());
						break;
					}
				}
			}
		}
	}

	public void ReplaceWidget(Widget widget, Widget replacewith) {
		for (int i = 0; i < sceneController.getActiveZone().zoneWidth; i++) {
			for (int j = 0; j < sceneController.getActiveZone().zoneHeight; j++) {
				if (sceneController.getActiveZone().zoneTileGrid[i][j] != null) {
					if (sceneController.getActiveZone().zoneTileGrid[i][j].getWidgetObject() == widget) {
						sceneController.getActiveZone().zoneTileGrid[i][j].setWidget(replacewith);
						m_view.vision(sceneController.getActiveZone(), sceneController.getActiveZone().zoneActors,
								sceneController.getUniverse().player.getPosition());
						break;
					}
				}
			}
		}
	}

	@Override
	public void Flash(Vec2f p, int type) {

		switch (type) {
		case 0:
			FXanimationControl.addEffect(new FX(0, p, 1, 0, 0));
			break;

		case 1:
			FXanimationControl.addEffect(new FX(0, p, 1, 0, 1));
			break;
		case 2:
			FXanimationControl.addEffect(new FX(0, p, 0, 1, 0));
			break;
		case 3:
			FXanimationControl.addEffect(new FX(0, p, 1, 0, 0, 5));
			break;

		case 4:
			FXanimationControl.addEffect(new FX(0, p, 1, 0, 1, 5));
			break;
		case 5:
			FXanimationControl.addEffect(new FX(0, p, 0, 1, 0, 10));
			break;	
		}
	}

	@Override
	public void projectile(Vec2f target, Vec2f origin, int type) {
		Vec2f velocity = new Vec2f(target.x - origin.x, target.y - origin.y);
		switch (type) {
		case 0:
			// FXanimationControl.addEffect(new FX(0,p,1,0,0));
			FXanimationControl.addEffect(new FX_projectile(1, origin, velocity, 1, 0, 0));
			break;

		case 1:
			// FXanimationControl.addEffect(new FX(0,p,1,0,1));
			FXanimationControl.addEffect(new FX_projectile(1, origin, velocity, 1, 0, 1));
			break;
		}
	}

	@Override
	public void setScreen(Screen screen) {
		targeter.setActive(false);
		if (m_screen == null) {
			int values[] = new int[6];
			values[0] = m_textureIds[6];
			values[1] = m_textureIds[0];
			values[2] = m_textureIds[7];
			values[3] = m_textureIds[8];
			values[4] = m_variables[0];
			values[5] = m_textureIds[9];	
			screen.initialize(values, this);
			m_screen = screen;
			m_disabled = true;
			m_screen.start(m_hook);
		}
	}

	@Override
	public void replaceScreen(Screen screen) {
		targeter.setActive(false);
		if (m_screen != null) {
			m_disabled = false;
			m_screen.discard(m_hook);
			m_reader.UpdateHand();
			m_screen = null;
		}
		if (screen!=null)
		{
			int values[] = new int[6];
			values[0] = m_textureIds[6];
			values[1] = m_textureIds[0];
			values[2] = m_textureIds[7];
			values[3] = m_textureIds[8];
			values[4] = m_variables[0];
			values[5] = m_textureIds[9];	
			screen.initialize(values, this);
			m_screen = screen;
			m_disabled = true;
			m_screen.start(m_hook);
		}
	}

	@Override
	public void StartConversation(String conversation, NPC npc, boolean badEnd) {
		if (m_screen != null) {
			sceneController.getUniverse().getPlayer().calcMove();
			UpdateInfo();
			m_screen.discard(m_hook);
			m_screen = null;
		}

		DialogueScreen scr = new DialogueScreen(m_textureIds[0], m_textureIds[7], m_textureIds[8],
				SceneBase.getVariables()[0], sceneController.getUniverse().player, m_text, this);
		if (scr.Load(conversation, npc,this) == false && badEnd) {
			if (badEnd) {
				Game.sceneManager
						.SwapScene(new GameOver(SceneBase.getVariables(), "It seems you've met a terrible fate",npc,true));
			}
		} else {
			scr.setTalkingNpc(npc);
			m_disabled = true;
			m_screen = scr;

			m_screen.start(m_hook);
		}
	}

	@Override
	public void StartConversation(String conversation, NPC npc, Widget widget, boolean badEnd) {
		if (m_screen != null) {
			sceneController.getUniverse().getPlayer().calcMove();
			UpdateInfo();
			m_screen.discard(m_hook);
			m_screen = null;
		}

		DialogueScreen scr = new DialogueScreen(m_textureIds[0], m_textureIds[7], m_textureIds[8],
				SceneBase.getVariables()[0], sceneController.getUniverse().player, m_text, this);
		if (scr.Load(conversation, npc,this) == false && badEnd) {
			if (badEnd) {
				Game.sceneManager
						.SwapScene(new GameOver(SceneBase.getVariables(), "It seems you've met a terrible fate",npc,true));
			}
		} else {
			scr.setTalkingNpc(npc);
			scr.setWidget(widget);
			m_disabled = true;
			m_screen = scr;

			m_screen.start(m_hook);
		}
	}

	@Override
	public void StartConversation(String conversation, WidgetConversation widget) {
		if (m_screen != null) {
			sceneController.getUniverse().getPlayer().calcMove();
			UpdateInfo();
			m_screen.discard(m_hook);
			m_screen = null;
		}
		DialogueScreen scr = new DialogueScreen(m_textureIds[0], m_textureIds[7], m_textureIds[8],
				SceneBase.getVariables()[0], sceneController.getUniverse().player, m_text, this);
		if (scr.Load(conversation, null,this) == false) {

		} else {
			scr.setWidget(widget);
			m_disabled = true;
			m_screen = scr;

			m_screen.start(m_hook);
		}
	}

	void MoveToPlayer(Actor actor) {
		if (actor.getPosition().getDistance(sceneController.getUniverse().getPlayer().getPosition()) > 2) {
			for (int i = 0; i < 8; i++) {
				Vec2f v = ZoneInteractionHandler.getPos(i, sceneController.getUniverse().player.getPosition());
				if (sceneController.getActiveZone().contains((int) v.x, (int) v.y)) {
					if (sceneController.getActiveZone().passable((int) v.x, (int) v.y, false)) {
						actor.getSpriteInterface().setVisible(true);
						actor.setPosition(v);
					
						break;
					}
				}
			}
		}
	}

	@Override
	public boolean BeatPlayer(NPC npc) {

		if (m_screen == null) {
			if (sceneController.getUniverse().player.getRPG().getStat(Actor_RPG.HEALTH) <= 0) {
				// move npc adjacent
				MoveToPlayer(npc);
				if (npc.getConversation(NPC.CONVERSATIONVICTORY) != null) {
					StartConversation(npc.getConversation(NPC.CONVERSATIONVICTORY), npc, true);
				}
				return true;
			}
			if (sceneController.getUniverse().player.getRPG().getStat(Actor_RPG.RESOLVE) <= 0) {
				// move npc adjacent
				MoveToPlayer(npc);
				if (npc.getConversation(NPC.CONVERSATIONSEDUCER) != null) {
					StartConversation(npc.getConversation(NPC.CONVERSATIONSEDUCER), npc, true);
				}
				return true;
			}
		}
		return false;
	}

	public void PlayerBeaten(NPC npc, boolean resolve) {
		if (m_screen == null) {
			MoveToPlayer(npc);
			if (resolve == true) {
				if (npc.getConversation(NPC.CONVERSATIONSEDUCER) != null) {
					StartConversation(npc.getConversation(NPC.CONVERSATIONSEDUCER), npc, true);
				}
			} else {
				if (npc.getConversation(NPC.CONVERSATIONVICTORY) != null) {
					StartConversation(npc.getConversation(NPC.CONVERSATIONVICTORY), npc, true);
				} else {
					Game.sceneManager
							.SwapScene(new GameOver(SceneBase.getVariables(), "It seems you've met a terrible fate",npc,true));
				}
			}
		}
	}

	@Override
	public void UpdateInfo() {
		// TODO Auto-generated method stub
		redrawBars();
		m_reader.UpdateHand();
	}

	@Override
	public void Callback() {
		Remove();
	}

	@Override
	public boolean placeWidget(Widget widget, int x, int y, boolean imprecise) {

		if (imprecise) {
			if (!placeWidgetInTile(widget, x, y)) {
				for (int i = 0; i < 8; i++) {
					Vec2f p = ZoneInteractionHandler.getPos(i, new Vec2f(x, y));
					if (placeWidgetInTile(widget, (int) p.x, (int) p.y)) {
						return true;
					}

				}
			} else {
				return true;
			}
		} else {
			return placeWidgetInTile(widget, x, y);
		}
		return false;
	}

	private boolean placeWidgetInTile(Widget widget, int x, int y) {
		if (sceneController.getActiveZone().getTile(x, y) == null) {
			return false;
		}
		if (sceneController.getActiveZone().getTile(x, y).getWidgetObject() != null) {
			return false;
		}
		if (sceneController.getActiveZone().getTile(x, y).getDefinition().getMovement() != TileMovement.WALK) {
			return false;
		}
		sceneController.getActiveZone().getTile(x, y).setWidget(widget);
		m_view.vision(sceneController.getActiveZone(), sceneController.getActiveZone().zoneActors,
				sceneController.getUniverse().player.getPosition());
		return true;
	}

	public String getLastMessage() {
		return m_text.getLastInput();
	}

	@Override
	public void redraw() {
		m_viewmatrix.m30 = 0;
		m_viewmatrix.m31 = 0;

		m_viewmatrix.translate(new Vector2f(-1 * sceneController.getUniverse().player.getPosition().x - 9,
				-1 * sceneController.getUniverse().player.getPosition().y + 7));
		m_view.vision(sceneController.getActiveZone(), sceneController.getActiveZone().zoneActors,
				sceneController.getUniverse().player.getPosition());
	}

	@Override
	public void screenFade(float duration) {

		screenFade.run(duration);
	}

	@Override
	public NPC createNPC(String file, Vec2f position, boolean temp) {
		Document doc = ParserHelper.LoadXML("assets/data/npcs/" + file + ".xml");
		Element n = (Element) doc.getFirstChild();
		Vec2f p = Universe.getInstance().getCurrentZone().getEmptyTileNearP(position);
		NPC npc = null;
		if (temp)
		{
			npc=new Temp_NPC(n, p, file);
		}
		else
		{
			if (n.getTagName().equals("rankedNPC"))
			{
				npc=new RankedNPC(n, p, file);		
			}
			else
			{
				npc=new NPC(n, p, file);		
			}

		}
		npc.setCollisioninterface(Universe.getInstance().getCurrentZone());
		npc.setSense(sceneController);
		Universe.getInstance().getCurrentZone().getActors().add(npc);
		m_view.addActor(npc);
		m_view.vision(sceneController.getActiveZone(), sceneController.getActiveZone().zoneActors,
				sceneController.getUniverse().player.getPosition());
		return npc;
	}

}
