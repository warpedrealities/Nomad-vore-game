package worldgentools.blockdungeon.advanced;

import shared.Vec2i;

public class AdvancedOpening {
	public int edgeValue;
	public Vec2i position;
	public short[] type;

	public AdvancedOpening() {
		
	}

	public AdvancedOpening(int edges, Vec2i pos) {
		type=new short[4];
		edgeValue = edges;
		this.type=type;
		position = pos;
	}
}
