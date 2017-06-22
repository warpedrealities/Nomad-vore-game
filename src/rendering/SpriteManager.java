package rendering;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class SpriteManager {

	protected ArrayList<SpriteBatch> spriteBatches;
	protected String fileprefix;

	public SpriteManager() {
		fileprefix = "assets/art/sprites/";
		spriteBatches = new ArrayList<SpriteBatch>();
	}

	public SpriteManager(String fileprefix) {
		this.fileprefix = fileprefix;
		spriteBatches = new ArrayList<SpriteBatch>();
	}

	public void addSprite(Sprite sprite, String textureName) {

		if (!addExisting(sprite, textureName)) {
			SpriteBatch batch = new SpriteBatch(textureName);
			batch.genSprite(fileprefix);
			batch.addSprite(sprite);
			spriteBatches.add(batch);
		}
	}

	private boolean addExisting(Sprite sprite, String textureName) {
		for (int i = 0; i < spriteBatches.size(); i++) {
			if (spriteBatches.get(i).getSpriteTexture().equals(textureName)) {
				spriteBatches.get(i).addSprite(sprite);
				return true;
			}
		}
		return false;
	}

	public void draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer) {
		for (int i = 0; i < spriteBatches.size(); i++) {
			spriteBatches.get(i).draw(objmatrix, tintvar, matrix44fbuffer);
		}
	}

	public void discard() {
		for (int i = 0; i < spriteBatches.size(); i++) {
			spriteBatches.get(i).discard();
		}
	}
}
