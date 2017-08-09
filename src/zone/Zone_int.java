package zone;

import rlforj.los.ILosBoard;
import actor.Actor;
import artificial_intelligence.pathfinding.Path;
import widgets.Widget;

public interface Zone_int {

	public Actor getActor(int x, int y);

	public Widget getWidget(int x, int y);

	public boolean passable(int x, int y, boolean fly);

	public ILosBoard getBoard();

	public int getWidth();

	public int getHeight();

	public Path getPath(int x0, int y0, int x1, int y1, boolean fly, int length);

	public Tile getTile(int x, int y);

}
