package menu;

import gui.Button;
import gui.MultiLineText;
import gui.Text;
import gui.TextColoured;
import gui.Textwindow;
import gui.Window;
import gui.lists.List;
import input.Keyboard;
import input.MouseHook;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFW;

import nomad.Universe;

import shared.Callback;
import shared.FileTools;
import shared.SceneBase;
import shared.Screen;
import shared.Vec2f;
import view.ViewScene;
import vmo.Game;

public class SaveLoad extends Screen implements Callback {

	private Callback m_callback;
	private Window m_window;
	private Window m_subWindow;
	private List m_list;

	private String m_slots[];
	private Textwindow m_text;
	private Button textButton;
	private boolean textEntry;
	private float clock;
	private TextColoured text;
	
	public SaveLoad(int frame,int list, int button, int tint, boolean save, Callback callback) {
		m_callback = callback;
		clock = 0.5F;
		// generate window
		m_window = new Window(new Vec2f(-6.5F, -12), new Vec2f(13, 10), frame, true);

		// generate list of saves
		m_list = new List(new Vec2f(-6.5F, -10.0F), 12, list, tint, this, 13);
		// m_window.Add(m_list);
		GenSaveList();
		// if in save mode add a button to make a new save
		if (save == true) {
			Button newsave = new Button(new Vec2f(1.5F, 0.25F), new Vec2f(5, 1.5F), button, this, "save", 1);
			m_window.add(newsave);
		} else {
			Button newsave = new Button(new Vec2f(1.5F, 0.25F), new Vec2f(5, 1.5F), button, this, "load", 2);
			m_window.add(newsave);
		}
		// add exit button

		Button exit = new Button(new Vec2f(6.5F, 0.25F), new Vec2f(5, 1.5F), button, this, "back", 0);
		m_window.add(exit);
		m_text = new Textwindow(button, new Vec2f(-6.5F, 1), new Vec2f(10, 2), tint, "enter save name");
		textButton = new Button(new Vec2f(3.5F, 1), new Vec2f(3.0F, 2), button, this, "ok", 3);

		text = new TextColoured(new Vec2f(-6, -0.5F), "", 2.0F, tint);
		text.setTint(1, 0, 0);

		m_window.add(text);
		
		buildTemp();
		
		m_subWindow=new Window(new Vec2f(6.5F, -12), new Vec2f(8, 12), frame, true);
		Button toggle = new Button(new Vec2f(1.5F, 0.25F), new Vec2f(5, 1.5F), button, this, "activate", 4);
		m_subWindow.add(toggle);
		MultiLineText tm=new MultiLineText(new Vec2f(0.5F,12.0F),12,26,0.8F);
		m_subWindow.add(tm);
		tm.addText("save progress reset, "
				+ "this will reset the world and place your character back at "
				+ "the start of the game but retain your perks and inventory"
				+ " only use if you know what you're doing");
	}
	
	void buildTemp()
	{
		File file = new File("saves/temp");
		if (file.exists()) {
			FileTools.deleteFolder(file);
		} else {
			file.mkdir();
		}
	}

	void GenSaveList() {
		// get save folder
		File file = new File("saves");

		// find the names of all files in the item folder
		File[] files = file.listFiles();
		int count = 0;
		String str[] = null;
		if (files != null) {
			// use reader to generate items
			for (int i = 0; i < files.length; i++) {
				if (!files[i].getName().contains("temp") && !files[i].getName().contains(".svn") && files[i].isDirectory() == true) {
					count++;
				}
			}
			if (count > 0) {
				m_slots = new String[count];

				int index = 0;
				for (int i = 0; i < files.length; i++) {
					if (!files[i].getName().contains("temp") && !files[i].getName().contains(".svn") && files[i].isDirectory() == true) {
						m_slots[index] = files[i].getName();
						index++;
					}
				}

				// draw list of files
				str = new String[count + 1];
				for (int i = 0; i < count; i++) {
					str[i] = m_slots[i];
				}
				str[count] = "empty";
			}
		}

		if (str == null) {
			// backup, list is empty
			str = new String[1];
			str[0] = "empty";

		}
		m_list.GenList(str);

	}

	@Override
	public void update(float DT) {
		m_window.update(DT);
		m_subWindow.update(DT);
		textButton.update(DT);
		if (clock > 0) {
			clock -= DT;
		}

		if (textEntry == true) {
			m_text.update(DT);
		} else {
			m_list.update(DT);
		}
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		// TODO Auto-generated method stub
		m_window.Draw(buffer, matrixloc);
		m_list.Draw(buffer, matrixloc);
		if (textEntry == true) {
			m_text.Draw(buffer, matrixloc);
			textButton.Draw(buffer, matrixloc);
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
		{
			m_subWindow.Draw(buffer, matrixloc);
		}
	}

	@Override
	public void discard(MouseHook mouse) {
		mouse.Remove(m_window);
		mouse.Remove(m_list);
		mouse.Remove(m_text);
		mouse.Remove(textButton);
		mouse.Remove(m_subWindow);
		m_window.discard();
		m_list.discard();
		m_text.discard();
		textButton.discard();
		m_subWindow.discard();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub
		if (clock <= 0) {
			switch (ID) {
			case 0:
				m_callback.Callback();
				break;
			case 1:
				// save
				if (m_slots != null) {
					if (m_list.getSelect() < m_slots.length) {
						Save(m_slots[m_list.getSelect()]);
					} else {
						// prompt to enter new save name
						NewSave();
					}
				} else {
					// prompt to enter new save name
					NewSave();
				}

				break;
			case 2:
				// load
				if (m_slots != null) {
					if (m_list.getSelect() < m_slots.length) {
						if (!m_slots[m_list.getSelect()].equals("temp"))
						{
							Load(m_slots[m_list.getSelect()],false);				
						}
					}
				}
				break;
			case 3:
				if (textEntry == true && m_text.m_string.length() > 2) {
					buildDirectory();
					textEntry = false;
				}
				break;
			case 4:
				if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || 
						Keyboard.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT))
				{
					if (m_slots != null) {
						if (m_list.getSelect() < m_slots.length) {
							if (!m_slots[m_list.getSelect()].equals("temp"))
							{
								Load(m_slots[m_list.getSelect()],true);				
							}
						}
					}				
				}
				break;
			}

		}
	}

	@Override
	public void start(MouseHook hook) {
		// TODO Auto-generated method stub
		hook.Register(m_window);
		hook.Register(m_list);
		hook.Register(m_text);
		hook.Register(textButton);
		hook.Register(m_subWindow);
	}

	private void buildDirectory() {
		String name = m_text.m_string;
		File file = new File("saves/" + name);
		if (file.exists()) {
			FileTools.deleteFolder(file);
		} else {
			file.mkdir();
		}

		Save(name);
	}

	@Override
	public void Callback() {

	}

	void Save(String filename) {
		try {
			if (!Universe.getInstance().save(filename)) {
				text.setString("SAVE FAILED! re-attempt saving");
			} else {
				m_callback.Callback();
			}
		} catch (IOException e) {
			text.setString("SAVE FAILED! re-attempt saving");
			e.printStackTrace();
		}

	}

	void Load(String filename,boolean forceReset) {
		try {
			Universe.getInstance().Load(filename,forceReset);
			if (Universe.getInstance().getPlaying()) {
				Game.sceneManager.SwapScene(new ViewScene(SceneBase.getVariables(), Universe.getInstance()));
			}
		} catch (IOException e) {
			text.setString("load failure");
		}
	}

	void NewSave() {
		m_text.m_string = "";
		textEntry = true;

	}

}
