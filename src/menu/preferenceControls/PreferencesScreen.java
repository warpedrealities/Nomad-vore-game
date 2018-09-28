package menu.preferenceControls;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import gui.Button;
import gui.Text;
import gui.Window;
import gui.lists.List;
import input.MouseHook;
import nomad.universe.Universe;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;

public class PreferencesScreen extends Screen implements Callback {

	private Callback callback;
	private List [] lists;
	private Window window;
	private java.util.List<String> activePrefs;
	private java.util.List<MasterListPref> allPrefs;
	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub
		window.update(DT);
		for (int i=0;i<2;i++)
		{
			lists[i].update(DT);
		}
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		// TODO Auto-generated method stub
		window.Draw(buffer, matrixloc);
		for (int i=0;i<2;i++)
		{
			lists[i].Draw(buffer, matrixloc);
		}
	}

	@Override
	public void discard(MouseHook mouse) {
		// TODO Auto-generated method stub
		mouse.Remove(window);
		window.discard();
		for (int i=0;i<2;i++)
		{
			mouse.Remove(lists[i]);
			lists[i].discard();
		}
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub
		switch (ID)
		{
			case 0:
			scan();
			break;
		
			case 1:
			add();
			break;
			
			case 2:
			remove();
			break;
			
			case 3:
			save();
				
			break;
			
			case 4:
			callback.Callback();		
			break;
		}
	}
	
	private void remove() {
		if (lists[1].getSelect()<activePrefs.size())
		{
			activePrefs.remove(lists[1].getSelect());
			resetList();		
		}

	}

	private void add() {
		if (lists[0].getSelect()<allPrefs.size())
		{		
			int index=lists[0].getSelect();
			for (int i=0;i<activePrefs.size();i++)
			{
				if (activePrefs.get(i).equals(allPrefs.get(index).getPreference()))
				{
					return;
				}
			}
			activePrefs.add(allPrefs.get(index).getPreference());
			resetList();		
		}
	}
	
	private void resetList()
	{
		lists[1].GenList(activePrefs.toArray(new String[0]));
	}

	private void save()
	{
		Universe.getInstance().getPrefs().setPreferences(activePrefs);
		File file=new File("assets/data/preferences.txt");
		file.delete();
		try {
			file.createNewFile();					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try (BufferedWriter bwriter=new BufferedWriter(new FileWriter(file)))
		{
			for (int i=0;i<activePrefs.size();i++)
			{
				bwriter.write(activePrefs.get(i));
				bwriter.newLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//now save the active file
		try {
			new MasterListSaver().save(allPrefs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void scan()
	{
		allPrefs=new MasterListGenerator().generate();
		
		String []str=new String[allPrefs.size()];
		for (int i=0;i<str.length;i++)
		{
			str[i]=allPrefs.get(i).getPreference()+" "+allPrefs.get(i).getCount();
		}
		lists[0].GenList(str);
	}
		

	
	@Override
	public void start(MouseHook hook) {
		hook.Register(window);
		for (int i=0;i<2;i++)
		{
			hook.Register(lists[i]);
		}
	}

	public void initialize(int[] textures, Callback callback) {
		this.callback = callback;
		// 0 is frame
		// 1 is list
		// 2 button
		// 3 tint
		// build window
		window=new Window(new Vec2f(-17,-12),new Vec2f(34,5),textures[0],true);
		lists=new List[2];
		lists[0]=new List(new Vec2f(-17, -7.0F), 16, textures[1], textures[3], this);
		lists[1]=new List(new Vec2f(0, -7.0F), 16, textures[1], textures[3], this);		
		
		Button []buttons=new Button[5];
		buttons[0]= new Button(new Vec2f(0.5F, 0.5F), new Vec2f(6, 1.8F), textures[2], this, "scan", 0, 1);
		buttons[1]= new Button(new Vec2f(7.0F, 0.5F), new Vec2f(6, 1.8F), textures[2], this, "add", 1, 1);
		buttons[2]= new Button(new Vec2f(13.5F, 0.5F), new Vec2f(6, 1.8F), textures[2], this, "remove", 2, 1);
		buttons[3]= new Button(new Vec2f(20.0F, 0.5F), new Vec2f(6, 1.8F), textures[2], this, "save", 3, 1);
		buttons[4]= new Button(new Vec2f(26.5F, 0.5F), new Vec2f(6, 1.8F), textures[2], this, "exit", 4, 1);			
		for (int i=0;i<5;i++)
		{
			window.add(buttons[i]);
		}
		loadPreferences();
		
		Text text=new Text(new Vec2f(0.5F, 2.0F), "all fetishes in the right side list will be forbidden from the game", 0.7F, textures[3]);
		window.add(text);
	}
	
	private void loadPreferences()
	{
		activePrefs=new ArrayList<String>(Universe.getInstance().getPrefs().getPrefs());
		lists[1].GenList(activePrefs.toArray(new String[0]));
		
		allPrefs=new MasterListLoader().load();
		
		String []str=new String[allPrefs.size()];
		for (int i=0;i<str.length;i++)
		{
			str[i]=allPrefs.get(i).getPreference()+" "+allPrefs.get(i).getCount();
		}
		lists[0].GenList(str);		
	}

	@Override
	public void Callback() {
		// TODO Auto-generated method stub
		
	}
}
