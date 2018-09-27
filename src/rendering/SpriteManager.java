package rendering;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpriteManager {

	protected ArrayList<SpriteBatch> spriteBatches;
	protected Map<String,SpriteBatch> batchMap;
	protected String fileprefix;

	public SpriteManager() {
		fileprefix = "assets/art/sprites/";
		spriteBatches = new ArrayList<SpriteBatch>();
		batchMap=new HashMap<String,SpriteBatch>();
	}

	public SpriteManager(String fileprefix) {
		this.fileprefix = fileprefix;
		spriteBatches = new ArrayList<SpriteBatch>();
		batchMap=new HashMap<String,SpriteBatch>();	
	}

	public void addSprite(Renderable sprite, String textureName) {

		if (!addExisting(sprite, textureName)) {
			SpriteBatch batch = new SpriteBatch(textureName);
			batch.genSprite(fileprefix);
			batch.addSprite(sprite);
			spriteBatches.add(batch);
			batchMap.put(textureName, batch);
		}
	}
	
	public void removeSprite(Renderable sprite, String textureName)
	{
		if (batchMap.get(textureName)!=null)
		{
			batchMap.get(textureName).removeSprite(sprite);		
		}
	
	}

	private boolean addExisting(Renderable sprite, String textureName) {
		SpriteBatch batch=batchMap.get(textureName);
		if (batch!=null)
		{
			batch.addSprite(sprite);	
			return true;
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
