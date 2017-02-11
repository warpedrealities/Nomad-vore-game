package vmo;

import shared.Vec2f;

public interface Chunk_Int {

	Tile_Int [][] getTiles(int i);
	
	Vec2f getPos();
}
