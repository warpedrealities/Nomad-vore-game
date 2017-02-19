package playerscreens;

import gui.GUIBase;
import gui.MultiLineText;
import gui.Window;

import java.nio.FloatBuffer;

import shared.Vec2f;

public class Popup extends GUIBase {
	
	MultiLineText textMessage;
	Window window;
	float clock;
	
	public Popup(int frametexture, Vec2f position)
	{
		clock=0;
		window=new Window(position,new Vec2f(20,8),frametexture,true);
		textMessage=new MultiLineText(new Vec2f(0.5F,7.7F),12,64,0.8F);
		window.add(textMessage);
	
		textMessage.addText("text text text text text text text text text text text text text text text text text text");
	}

	public void setClock(float newvalue)
	{
		clock=newvalue;
	}
	
	public void setText(String text)
	{
		textMessage.addText(text);
	}
	
	@Override
	public void update(float DT) {
		
		if (clock>0)
		{
			clock-=DT;
		}	
	}

	@Override
	public boolean ClickEvent(Vec2f pos) {
	
		return false;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		if (clock>0)
		{
			window.Draw(buffer, matrixloc);
		}	
	}

	@Override
	public void discard() {

		window.discard();
	}

	@Override
	public void AdjustPos(Vec2f p) {
		// TODO Auto-generated method stub
		
	}
}
