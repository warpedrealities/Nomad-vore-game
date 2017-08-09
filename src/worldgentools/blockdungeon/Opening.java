package worldgentools.blockdungeon;

import shared.Vec2i;

public class Opening {

	public int edgeValue;
	public Vec2i position;

	public Opening() {

	}

	public Opening(int edges, Vec2i pos) {
		edgeValue = edges;
		position = pos;
	}

}
