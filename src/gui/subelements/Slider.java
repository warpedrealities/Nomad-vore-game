package gui.subelements;

import java.nio.FloatBuffer;

import gui.GUIBase;
import input.MouseHook;
import shared.Callback;
import shared.Vec2f;

public class Slider extends GUIBase {

	ThreePatch patch;
	int notches;
	float length;
	Vec2f position;
	int positionIndex;
	float currentPosition;
	boolean active;
	Callback callback;
	
	public Slider(Vec2f position, float length,Callback callback)
	{
		this.callback=callback;
		this.position=position;
		patch=new ThreePatch(new Vec2f(position.x,position.y));
		this.length=length;
	}
	
	public void setIndex(int index)
	{
		positionIndex=index;
		recalcPosition();
	}
	
	public void setNotches(int notches) {
		this.notches = notches;
		recalcPosition();
	}
	
	public int getPositionIndex() {
		return positionIndex;
	}

	private void recalcPosition()
	{
		patch.AdjustPos(new Vec2f(position.x,getY()));
		currentPosition=getY();
	}

	private float getNotchHeight()
	{
		return length/(float)notches;
	}
	
	private float getY()
	{
		return position.y-(positionIndex*getNotchHeight());
	}

	private void disable()
	{
		active=false;
		patch.regen(false);
		recalcPosition();
	}
	
	private void enable()
	{
		active=true;
		patch.regen(true);
	}
	
	private boolean inFrame(Vec2f p)
	{
		float y=currentPosition;
		if (p.x>position.x && p.y>y)
		{
			if (p.x<position.x+1 && p.y<y+4)
			{
				return true;
			}
		
			
		}
		return false;
	}
	
	private void slide()
	{
		float ydelta=MouseHook.getInstance().getMouseDelta().y;
		float ynew=currentPosition+ydelta;
		
		if (ydelta!=0 && ynew<position.y && ynew>position.y-length)
		{
			currentPosition=ynew;
			patch.AdjustPos(new Vec2f(position.x,currentPosition));
			float yindex=Math.abs(currentPosition-position.y);
			int index=positionIndex;
			positionIndex=1*((int)(yindex/((float)getNotchHeight())));
			if (index!=positionIndex)
			{
				callback.Callback();
			}
		}
	}
	
	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub
		if (MouseHook.getInstance().buttonDown())
		{
			//find if inside the box
			if (inFrame(MouseHook.getInstance().getPosition()))
			{
				if (!active)
				{
					enable();
				}
				else
				{
					slide();
				}
			}
			else
			{
				if (active)
				{
					disable();
				}
			
			}
		}
		else
		{
			if (active)
			{
				disable();
			}
		}
	}

	@Override
	public boolean ClickEvent(Vec2f pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		patch.Draw(buffer, matrixloc);
	}

	@Override
	public void discard() {		
		patch.Discard();
	}

	@Override
	public void AdjustPos(Vec2f p) {
		patch.AdjustPos(p);
	}

}
