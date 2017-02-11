package playerscreens;

import gui.Button;
import gui.TextParagrapher;
import gui.Textwindow;
import gui.Window;
import input.MouseHook;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import nomad.Universe;

import description.AppearanceParser;

import shared.Callback;
import shared.Screen;
import shared.Vec2f;

public class AppearanceScreen extends Screen implements Callback{

	Callback callback;
	TextParagrapher description;
	Window window;
	Textwindow text;
	
	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {

		window.Draw(buffer, matrixloc);
	}

	@Override
	public void discard(MouseHook mouse) {
	
		window.discard();
		mouse.Remove(window);
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		switch (ID)
		{
			case 0:
				callback.Callback();
				break;
		}
	}

	@Override
	public void start(MouseHook hook) {
	
		hook.Register(window);
	}

	@Override
	public void initialize(int[] textures, Callback callback) {

		//0 is font
		//1 is frame 
		//2 button
		//3 is button alt
		//4 tint
		
		this.callback=callback;
		
		//build window
		window=new Window(new Vec2f(-20,-16.0F), new Vec2f(40,32),textures[1],true);
		//build button to return
		Button button=new Button(new Vec2f(31.5F,0.5F), new Vec2f(8,2), textures[2],  this,"exit", 0, 0.8F);	;
		
		window.add(button);
		//build text paragrapher
		description=new TextParagrapher(new Vec2f(0.5F,31.5F), 32, 140, 0.8F);
		window.add(description);
		//dummyText();
		buildText();
	}
	
	private void buildText()
	{
		ArrayList<String> strings=AppearanceParser.parseAppearance(Universe.getInstance().getPlayer().getLook());
		for (int i=0;i<strings.size();i++)
		{
			description.addText(strings.get(i));
		}
	}

	private void dummyText()
	{
		for (int i=0;i<64;i++)
		{
			StringBuilder text=new StringBuilder();
			for (int j=0;j<32;j++)
			{
				text.append("text ");
			}
			description.addText(text.toString());
		}
	}
	
	@Override
	public void Callback() {
		// TODO Auto-generated method stub
		
	}
}
