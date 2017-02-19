package artificial_intelligence;

import actor.Actor;
import shared.Vec2f;

public interface Sense {

	public boolean CanWalk(int x, int y);

	public Actor getHostile(Actor origin, int maxrange,boolean visibleOnly);
	
	public Actor getPlayer(Actor origin,boolean visibleOnly);
	
	public int getViolationLevel();
	
	public Vec2f getViolationLocation();
	
	public boolean getPreference(String preference);
	
	public void drawText(String text);

}
