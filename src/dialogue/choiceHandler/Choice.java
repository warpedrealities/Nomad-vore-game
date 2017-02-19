package dialogue.choiceHandler;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.opengl.GL20;

import font.NuFont;
import shared.Vec2f;
import vmo.Game;


public class Choice {

	boolean lit;
	int lineCount;
	int tintID;
	NuFont lines[];
	Vec2f position;
	int width=32;
	
	public Choice(int tint)
	{
		tint=tintID;
		lineCount=2;
		lines=new NuFont[2];
		
		lines[0]=new NuFont(new Vec2f(0,0),64,0.6F*Game.sceneManager.getConfig().getTextscale());
		lines[0].setString("text text text text text text text text text text");
		lines[1]=new NuFont(new Vec2f(0,0),64,0.6F*Game.sceneManager.getConfig().getTextscale());	
		lines[1].setString("text text text text text text text text text text");
	}
	
	public int getLineCount()
	{
		return lineCount;
	}
	
	public Vec2f getPosition()
	{
		return position;
	}
	
	public void setPosition(Vec2f p)
	{
		position=p;
		for (int i=0;i<lineCount;i++)
		{
			lines[i].ResetPos(new Vec2f(p.x,p.y-(0.8F*i*Game.sceneManager.getConfig().getTextscale())));
		}
	}
	
	public void setLit(boolean lit)
	{
		this.lit=lit;
	}
	
	public void addText(String string)
	{
		String [] list=partition(string);
		lineCount=list.length;
		if (list.length>lines.length)
		{
			expand(list.length);
		}
		
		for (int i=0;i<lineCount;i++)
		{
			lines[i].setString(list[i]);
		}
		
	}
	
	private void expand(int newLength)
	{
		NuFont nu[]=new NuFont[newLength];
		for (int i=0;i<newLength;i++)
		{
			if (newLength<lines.length)
			{
				nu[i]=lines[i];
			}
			else
			{
				nu[i]=new NuFont(new Vec2f(0,0),64,0.6F*Game.sceneManager.getConfig().getTextscale());	
			}
			
		}
		lines=nu;
	}
	
	private String [] partition(String string)
	{
		List<String> list=new LinkedList <>();
		int length=0;
		Scanner scanner=new Scanner(string);
		StringBuilder builder=new StringBuilder();
		while (scanner.hasNext())
		{
			String str=scanner.next();
			if (str.length()+length>48)
			{
				length=0;
				if (list.size()>0)
				{
					builder.insert(0, " ");
				}
				list.add(builder.toString());
				builder=new StringBuilder();
				builder.append(str+" ");
			}
			else
			{
				builder.append(str+" ");		
				length=length+str.length()+1;
				
			}
		}
		if (builder.length()>0)
		{
			list.add(builder.toString());
		}
		scanner.close();
		return list.toArray(new String[0]);
	}
	
	public void draw(FloatBuffer buffer, int matrixloc)
	{
		for (int i=0;i<lineCount;i++)
		{
			if (lit)
			{
				GL20.glUniform4f(tintID,0,1,0,1);
			}
			lines[i].Draw(buffer, matrixloc);	
			if (lit)
			{
				GL20.glUniform4f(tintID,1,1,1,1);
			}
		}
	}
	
	public void discard()
	{
		for (int i=0;i<lines.length;i++)
		{
			if (lines[i]!=null)
			{
				lines[i].Discard();
			}
		}
	}
	
}
