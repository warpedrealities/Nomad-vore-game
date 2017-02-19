package vmo;

import zone.Tile;


public interface Collision_Interface {

	public Tile getTile(float x, float y,byte layer);
	
	public void ForceRebuild();
	
	public boolean OpenTile(int x, int y);
}
