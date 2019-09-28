package artificial_intelligence.detection;

import actor.Actor;
import artificial_intelligence.senseCriteria.Sense_Criteria;
import nomad.FlagField;
import shared.Vec2f;

public interface Sense {

	public boolean CanWalk(int x, int y);

	public Actor getHostile(Actor origin, int maxrange, boolean visibleOnly);
	public Actor getHostile(Actor origin, int maxrange, boolean visibleOnly,String exclude, int exValue);
	public Actor getVictim(Actor origin, int maxrange, boolean visibleOnly, String name, boolean seduced);
	public Actor getNamedActor(Actor origin, int maxRange, boolean visibleOnly, String name);
	public Actor getPlayer(Actor origin, boolean visibleOnly);

	public Actor getActorInTile(int x, int y, boolean activeOnly);

	public Actor getActorInTile(int x, int y);
	public boolean getTileVisible(int x, int y);
	public Actor getActor(Actor origin, int maxrange, boolean visibleOnly,Sense_Criteria criteria);
	public Sense_Criteria getCriteria(String properties);
	public int getViolationLevel();

	public Vec2f getViolationLocation();

	public boolean getPreference(String preference);

	public void drawText(String text);

	public FlagField getGlobalFlags();

}
