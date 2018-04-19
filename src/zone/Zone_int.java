package zone;

import rlforj.los.ILosBoard;
import actor.Actor;
import actor.player.Player;
import artificial_intelligence.pathfinding.Path;
import widgets.Widget;

public interface Zone_int {

	public Actor getActor(int x, int y);

	public Widget getWidget(int x, int y);

	public boolean passable(int x, int y, boolean fly);

	public ILosBoard getBoard();
	
	public float getMovementMultiplier();
	
	public float getVisionMultiplier();
	
	public void updateZoneEnvironment(Player player);

	public int getWidth();

	public int getHeight();

	public Path getPath(int x0, int y0, int x1, int y1, boolean fly, int length);

	public Tile getTile(int x, int y);

	public void removeThreat(int x, int y, Actor npc);

	public void addThreat(int x, int y, Actor npc);

}
