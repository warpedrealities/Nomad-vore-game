package rendering;

public class SpriteManager_Rectangles extends SpriteManager {

	private void buildSprite(Renderable sprite, SpriteBatch batch) {
		if (Sprite_Rectangle.class.isInstance(sprite)) {
			float x = batch.getTextureWidth() / 64;
			float y = batch.getTextureHeight() / 64;
			float ratio = y / x;
			Sprite_Rectangle spr = (Sprite_Rectangle) sprite;
			spr.setSpriteSize(x);
			spr.setXyRatio(ratio);
			spr.deferredGeneration();
		}

	}

	@Override
	public void addSprite(Renderable sprite, String textureName) {

		if (!addExisting(sprite, textureName)) {
			SpriteBatch batch = new SpriteBatch(textureName);
			batch.genSprite(fileprefix);

			buildSprite(sprite, batch);
			batch.addSprite(sprite);
			spriteBatches.add(batch);
		}
	}

	private boolean addExisting(Renderable sprite, String textureName) {
		for (int i = 0; i < spriteBatches.size(); i++) {
			if (spriteBatches.get(i).getSpriteTexture().equals(textureName)) {
				buildSprite(sprite, spriteBatches.get(i));
				spriteBatches.get(i).addSprite(sprite);
				return true;
			}
		}
		return false;
	}
}
