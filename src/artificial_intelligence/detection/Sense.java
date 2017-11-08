package artificial_intelligence.detection;

import actor.Actor;
import shared.Vec2f;

public interface Sense {

	public boolean CanWalk(int x, int y);

	public Actor getHostile(Actor origin, int maxrange, boolean visibleOnly);
	public Actor getHostile(Actor origin, int maxrange, boolean visibleOnly,String exclude, int exValue);
	public Actor getVictim(Actor origin, int maxrange, boolean visibleOnly, String name, boolean seduced);
	public Actor getNamedActor(Actor origin, int maxRange, boolean visibleOnly, String name);
	public Actor getPlayer(Actor origin, boolean visibleOnly);

	public int getViolationLevel();

	public Vec2f getViolationLocation();

	public boolean getPreference(String preference);

	public void drawText(String text);

}
