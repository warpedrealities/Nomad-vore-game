package rendering;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import de.matthiasmann.twl.utils.PNGDecoder;

import shared.Tools;

public class SpriteBatch {

	private String spriteTexture;
	private int textureID;
	private ArrayList<Sprite> sprites;
	private String filename;
	private int textureWidth, textureHeight;

	public SpriteBatch(String textureName) {
		sprites = new ArrayList<Sprite>();
		filename = textureName;

		spriteTexture = textureName;
	}

	public void genSprite(String fileprefix) {
		textureID = Tools.loadPNGTexture(fileprefix + filename, GL13.GL_TEXTURE0);
		// Open the PNG file as an InputStream
		InputStream in;
		try {
			in = new FileInputStream(fileprefix + filename);
			PNGDecoder decoder = new PNGDecoder(in);
			textureWidth = decoder.getWidth();
			textureHeight = decoder.getHeight();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Get the width and height of the texture

		// textureWidth=ARBDirectStateAccess.glGetTextureLevelParameteri(textureID,0,
		// GL11.GL_TEXTURE_WIDTH);
		// textureHeight=ARBDirectStateAccess.glGetTextureLevelParameteri(textureID,0,
		// GL11.GL_TEXTURE_HEIGHT);
	}

	public ArrayList<Sprite> getSprites() {
		return sprites;
	}

	public void addSprite(Sprite sprite) {
		sprites.add(sprite);
	}

	public void draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		for (int i = 0; i < sprites.size(); i++) {
			if (sprites.get(i).getVisible()) {
				sprites.get(i).draw(objmatrix, tintvar, matrix44fbuffer);
			}

		}

	}

	public void discard() {
		GL11.glDeleteTextures(textureID);
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
