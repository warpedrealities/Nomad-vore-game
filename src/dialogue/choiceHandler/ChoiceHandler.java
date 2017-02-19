package dialogue.choiceHandler;

import java.nio.FloatBuffer;

import gui.GUIBase;
import input.MouseHook;
import shared.Callback;
import shared.Vec2f;
import vmo.Game;

public class ChoiceHandler extends GUIBase {
	
	Callback callback;
	Vec2f corner;
	Vec2f size=new Vec2f(16,16);
	int offset;
	int index;
	
	Choice [] choices;
	int numChoices;
	String [] choiceText;
	
	public ChoiceHandler(Vec2f p,int tint,Callback callback) {
		corner=p;
		choices=new Choice[8];
		choiceText=new String[8];
		this.callback=callback;
		
		numChoices=8;
		for (int i=0;i<8;i++)
		{
			choices[i]=new Choice(tint);
		}
		
		resetChoicePos();
	}

	@Override
	public void update(float DT) {


		
		Vec2f p=MouseHook.getInstance().getPosition();
		if (p.x>corner.x && p.y<corner.y && p.x<corner.x+size.x && p.y>corner.y-size.y)
		{
			float dex=0;
			float yInterval=0.8F*Game.sceneManager.getConfig().getTextscale();
			choices[index].setLit(false);
			float y=p.y-corner.y; y=y*-1;
			for (int i=0;i<numChoices;i++)
			{
				if (y>dex && y<dex+(choices[i].getLineCount()*yInterval))
				{
					index=i;
					choices[index].setLit(true);
					break;
				}
				
				dex=dex+(yInterval*choices[i].getLineCount());
			}
		}
		else
		{
			choices[index].setLit(false);
		}
		
		
		
	}

	@Override
	public boolean ClickEvent(Vec2f pos) {
		if (pos.x>corner.x && pos.y<corner.y && pos.x<corner.x+size.x && pos.y>corner.y-size.y)
		{
			if (callback!=null)
			{
				callback.Callback();	
				return true;
			}
		}
		return false;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		for (int i=0;i<numChoices;i++)
		{
			choices[i].draw(buffer, matrixloc);
		}
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		for (int i=0;i<choices.length;i++)
		{
			if (choices[i]!=null)
			{
				choices[i].discard();
			}
		}
	}

	private void resetChoicePos()
	{
		float yInterval=0.8F*Game.sceneManager.getConfig().getTextscale();
		int c=0;
		for (int i=0;i<choices.length;i++)
		{
			if (choices[i]!=null)
			{
				choices[i].setPosition(new Vec2f(corner.x,corner.y-(yInterval*c)));
				c+=choices[i].getLineCount();
			}
		}
				
	}
	
	@Override
	public void AdjustPos(Vec2f p) {

		corner.x=corner.x+p.x;
		corner.y=corner.y+p.y;
		resetChoicePos();
	}

	public void addChoices(String[] choices2, int count) {
		numChoices=count;
		for (int i=0;i<count;i++)
		{
			choiceText[i]=choices2[i];
			choices[i].addText((i+1)+". "+choices2[i]);
		}
		resetChoicePos();
	}

	public int getIndex() {
		return index;
	}

	public String getchoice(int i) {
		return choiceText[i];
	}

}
