package menu;

import gui.Button;
import gui.List;
import gui.Text;
import gui.TextColoured;
import gui.Textwindow;
import gui.Window;
import input.MouseHook;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;

import nomad.Universe;

import shared.Callback;
import shared.FileTools;
import shared.SceneBase;
import shared.Screen;
import shared.Vec2f;
import view.ViewScene;
import vmo.Game;

public class SaveLoad extends Screen implements Callback{

	Callback m_callback;
	boolean m_savemode;
	
	Window m_window;
	List m_list;

	String m_slots[];
	Textwindow m_text;
	Button textButton;
	boolean textEntry;
	float clock;
	TextColoured text;
	
	public SaveLoad(int font, int frame, int button,int tint,boolean save,Callback callback)
	{
		m_callback=callback;
		m_savemode=save;
		m_callback=callback;
		clock=0.5F;
		//generate window
		m_window=new Window(new Vec2f (-6.5F,-12), new Vec2f(13,10), frame,true);
		//generate list of saves
		m_list=new List(new Vec2f(-6.5F,-10.0F), 12, font, frame,tint,this,13);
	//m_window.Add(m_list);
		GenSaveList();
		//if in save mode add a button to make a new save
		if (save==true)
		{		
			Button newsave=new Button(new Vec2f(1.5F,0.25F), new Vec2f(5,1.5F), button,this,"save", 1);
			m_window.add(newsave);		
		}
		else
		{
			Button newsave=new Button(new Vec2f(1.5F,0.25F), new Vec2f(5,1.5F), button,this,"load", 2);
			m_window.add(newsave);			
		}
		//add exit button
		
		Button exit=new Button(new Vec2f(6.5F,0.25F), new Vec2f(5,1.5F), button, this,"back", 0);
		m_window.add(exit);
		m_text=new Textwindow(button, new Vec2f(-6.5F,1), new Vec2f(10,2),tint, "enter save name");
		textButton=new Button(new Vec2f(3.5F,1),new Vec2f(3.0F,2),button,this,"ok",3);
		
		text=new TextColoured(new Vec2f(-6,-0.5F),"",2.0F,tint);
		text.setTint(1, 0, 0);

		m_window.add(text);
	}
	
	void GenSaveList()
	{
		//get save folder
	File file=new File("saves");

		//find the names of all files in the item folder
		File[] files=file.listFiles();
		int count=0;
		String str[]=null;
		if (files!=null)
		{
			//use reader to generate items
			for (int i=0;i<files.length;i++)
			{
				if (files[i].getName().contains(".svn")==false && files[i].isDirectory()==true)
				{
						count++;
				}
			}
			if (count>0)
			{
				m_slots=new String[count];
				
				int index=0;
				for (int i=0;i<files.length;i++)
				{
					if (files[i].getName().contains(".svn")==false && files[i].isDirectory()==true)
					{
							m_slots[index]=files[i].getName();
							index++;
					}
				}			
				
				//draw list of files
				str=new String[count+1];
				for (int i=0;i<count;i++)
				{
					str[i]=m_slots[i];
				}
				str[count]="empty";
			}		
		}

		if (str==null)
		{
			//backup, list is empty
			str=new String[1];
			str[0]="empty";
			
		}
		m_list.GenList(str);
		
	}
	
	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub
		if (clock>0)
		{
			clock-=DT;
		}

		if (textEntry==true)
		{
			m_text.update(DT);
		}
		else
		{
			m_list.update(DT);	
		}
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		// TODO Auto-generated method stub
		m_window.Draw(buffer,matrixloc);	
		m_list.Draw(buffer, matrixloc);
		if (textEntry==true)
		{
			m_text.Draw(buffer, matrixloc);
			textButton.Draw(buffer, matrixloc);
		}
		
		
	}

	@Override
	public void discard(MouseHook mouse) {
		// TODO Auto-generated method stub
		mouse.Remove(m_window);
		mouse.Remove(m_list);
		mouse.Remove(m_text);
		mouse.Remove(textButton);
		m_window.discard();
		m_list.discard();
		m_text.discard();
		textButton.discard();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub
		if (clock<=0)
		{
			switch (ID)
			{
			case 0:
				m_callback.Callback();
			break;
			case 1:
				//save
				if (m_slots!=null)
				{
					if (m_list.getSelect()<m_slots.length)
					{
						Save(m_slots[m_list.getSelect()]);		
					}
					else
					{
						//prompt to enter new save name
						NewSave();
					}
				}
				else
				{
					//prompt to enter new save name
					NewSave();
				}

			break;
			case 2:
				//load
				if (m_slots!=null)
				{
					if (m_list.getSelect()<m_slots.length)
					{
						Load(m_slots[m_list.getSelect()]);		
					}		
				}
				break;
			case 3:
				if (textEntry==true && m_text.m_string.length()>2)
				{
					buildDirectory();
					textEntry=false;
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
	}

	private void buildDirectory()
	{
		String name=m_text.m_string;
		File file=new File("saves/"+name);
		if (file.exists())
		{
			FileTools.deleteFolder(file);
		}
		else
		{
			file.mkdir();	
		}
	
		
		Save(name);
	}
	
	@Override
	public void Callback() {


	
	}

	void Save(String filename)
	{
		try {
			if (Universe.getInstance().save(filename))
			{
				text.setString("SAVE FAILED! re-attempt saving");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
		m_callback.Callback();
	}
	
	void Load(String filename)
	{
		try {
			Universe.getInstance().Load(filename);
			if (Universe.getInstance().getPlaying())
			{
				Game.sceneManager.SwapScene(new ViewScene(SceneBase.getVariables(),Universe.getInstance()));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void NewSave()
	{
		m_text.m_string="";
		textEntry=true;
		
	}
	
}
