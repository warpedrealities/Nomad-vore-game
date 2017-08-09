package landingScreen;

import java.nio.FloatBuffer;

import gui.SpriteImage;
import rendering.Sprite;
import rendering.SpriteManager;
import shared.Vec2f;

public class LandingGridRender {

	SpriteImage[][] images;
	Vec2f corner;

	public LandingGridRender() {

	}

	public Vec2f getCorner() {
		return corner;
	}

	public void end() {
		for (int i = 0; i < images.length; i++) {
			for (int j = 0; j < images[i].length; j++) {
				if (images[i][j] != null) {
					images[i][j].discard();
				}
			}
		}

	}

	public void draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer) {
		for (int i = 0; i < images.length; i++) {
			for (int j = 0; j < images.length; j++) {
				if (images[i][j] != null) {
					images[i][j].Draw(matrix44fbuffer, objmatrix);
				}

			}
		}
	}

	public void generate(LandingElement[][] gridElements) {
		// TODO Auto-generated method stub
		images = new SpriteImage[gridElements.length][];
		Vec2f p = new Vec2f(gridElements.length * -2, gridElements[0].length * -2 + 1);
		for (int i = 0; i < images.length; i++) {
			images[i] = new SpriteImage[gridElements[i].length];
			for (int j = 0; j < images[i].length; j++) {
				if (gridElements[i][j] != null) {
					images[i][j] = new SpriteImage(new Vec2f(p.x + (i * 4), p.y + (j * 4)), new Vec2f(4, 4),
							"assets/art/solar/landingmarker.png", 4);
					images[i][j].setFrameIndex(gridElements[i][j].getLandingType().ordinal());
				}
			}
		}
		corner = p;
	}

	public void setImage(int xSelect, int ySelect, int ordinal) {

		images[xSelect][ySelect].setFrameIndex(ordinal);
	}

}
