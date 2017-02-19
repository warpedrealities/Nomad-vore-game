package gui;

import java.nio.FloatBuffer;

import shared.Clickable;
import shared.MyListener;
import shared.Vec2f;



public abstract class GUIBase implements Clickable
{
	protected MyListener m_listener;

	
	public abstract void update(float DT);
	
	//pass clicks down, use hierarchies to make sure they get where they need to go
	public abstract boolean ClickEvent(Vec2f pos);
	
	public abstract void Draw(FloatBuffer buffer, int matrixloc);
	
	public abstract void discard();
	
	public abstract void AdjustPos(Vec2f p);
}
