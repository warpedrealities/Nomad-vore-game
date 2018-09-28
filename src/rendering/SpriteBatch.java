package rendering;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import de.matthiasmann.twl.utils.PNGDecoder;
import shared.Tools;

public class SpriteBatch {

	private String spriteTexture;
	private int textureID;
	private ArrayList<Renderable> sprites;
	private String filename;
	private int textureWidth, textureHeight;

	public SpriteBatch(String textureName) {
		sprites = new ArrayList<Renderable>();
		filename = textureName;
		textureID=-1;
		spriteTexture = textureName;
	}

	public void genSprite(String fileprefix) {
		if (filename!=null)
		{
			textureID = Tools.loadPNGTexture(fileprefix + filename, GL13.GL_TEXTURE0);
			// Open the PNG file as an InputStream
			InputStream in;
			try {
				in = new FileInputStream(fileprefix + filename);
				PNGDecoder decoder = new PNGDecoder(in);
				textureWidth = decoder.getWidth();
				textureHeight = decoder.getHeight();

			} catch (FileNotFoundException e) {
			
				e.printStackTrace();
			}

			catch (IOException e) {

				e.printStackTrace();
			}		
		}
	}

	public ArrayList<Renderable> getSprites() {
		return sprites;
	}

	public void addSprite(Renderable sprite) {
		sprites.add(sprite);
		sprite.setSpriteBatch(this);
	}
	
	public void removeSprite(Renderable sprite) {
		sprite.discard();
		sprites.remove(sprite);
	}	

	public void draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer) {
		if (textureID!=-1)
		{
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);			
		}
		for (int i = 0; i < sprites.size(); i++) {
			if (sprites.get(i).getVisible()) {
				sprites.get(i).draw(objmatrix, tintvar, matrix44fbuffer);
			}

		}

	}

	public void discard() {
		if (textureID!=-1)
		{
			GL11.glDeleteTextures(textureID);		
		}

		
		for (int i=0;i<sprites.size();i++)
		{
			sprites.get(i).discard();
		}
		
	}

	public int getTextureID() {
		return textureID;
	}

	public String getSpriteTexture() {
		return spriteTexture;
	}

	public int getTextureWidth() {
		return textureWidth;
	}

	public int getTextureHeight() {
		return textureHeight;
	}



}
