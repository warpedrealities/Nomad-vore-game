package rendering;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import actor.Actor;
import actorRPG.Actor_RPG;
import shared.Vec2f;
import vmo.Tile_Int;
import widgets.WidgetSprite;
import zone.Zone;

public class WorldView {

	int m_width, m_height;
	RenderLayer m_layers[][];

	ArrayList<SegSprite> imageOverlays;

	SpriteManager spriteManager;

	OverlayController overlayController;

	public WorldView() {
		spriteManager = new SpriteManager_Rectangles();

	}

	public void draw(int objmatrix, FloatBuffer matrix44Buffer) {
		for (int i = 0; i < m_width; i++) {
			for (int j = 0; j < m_height; j++) {
				m_layers[i][j].Draw(objmatrix, matrix44Buffer);
			}
		}

	}

	public void drawWidgets(int objmatrix, FloatBuffer matrix44Buffer) {
		for (int i = 0; i < m_width; i++) {
			for (int j = 0; j < m_height; j++) {
				m_layers[i][j].DrawWidgets(objmatrix, matrix44Buffer);
			}
		}

	}

	public void drawOverlay(int objmatrix, FloatBuffer matrix44Buffer) {
		for (int i = 0; i < m_width; i++) {
			for (int j = 0; j < m_height; j++) {
				// m_layers[i][j].drawOverlay(objmatrix, matrix44Buffer);
			}
		}

	}

	public void vision(Zone zone, ArrayList<Actor> m_actors, Vec2f p) {

		overlayController.update(m_actors, p);
		for (int i = 0; i < m_width; i++) {

			for (int j = 0; j < m_height; j++) {
				m_layers[i][j].Generate(zone.getTiles(), 16 * i, 16 * j);
				m_layers[i][j].GenerateWidgets(zone.getTiles(), 16 * i, 16 * j);
				if (overlayController.isActive()) {
					// m_layers[i][j].generateOverlay(zone.getTiles(),16*i,
					// 16*j,overlayController);
				}

			}
		}

		if (imageOverlays.size() > 0) {
			for (int i = 0; i < imageOverlays.size(); i++) {
				imageOverlays.get(i).Generate();
			}
		}
	}

	public void drawSquares(int objmatrix, int tintvar, FloatBuffer matrix44Buffer) {
		spriteManager.draw(objmatrix, tintvar, matrix44Buffer);
	}

	public void drawImages(int objmatrix, FloatBuffer matrix44Buffer) {
		if (imageOverlays.size() > 0) {
			for (int i = 0; i < imageOverlays.size(); i++) {
				imageOverlays.get(i).Draw(objmatrix, matrix44Buffer);
			}

		}
	}

	public void generate(Zone zone) {
		m_width = zone.getWidth() / 16;
		m_height = zone.getHeight() / 16;

		overlayController = new OverlayController(zone);
		overlayController.calcActive(zone);
		m_layers = new RenderLayer[m_width][];
		imageOverlays = new ArrayList<SegSprite>();
		for (int i = 0; i < m_width; i++) {
			m_layers[i] = new RenderLayer[m_height];
			for (int j = 0; j < m_height; j++) {
				m_layers[i][j] = new RenderLayer();
				m_layers[i][j].Generate(zone.getTiles(), 16 * i, 16 * j);
				m_layers[i][j].GenerateWidgets(zone.getTiles(), 16 * i, 16 * j);

				m_layers[i][j].setPosition(new Vec2f(16 * i, 16 * j));
			}
		}
		// add squares
		ArrayList<Actor> actors = zone.getActors();
		for (int i = 0; i < actors.size(); i++) {
			if (actors.get(i).getSpriteInterface() != null) {
				Sprite sprite = (Sprite) actors.get(i).getSpriteInterface();
				spriteManager.removeSprite(sprite, actors.get(i).getSpriteName());
			}
			Sprite_Rectangle sprite = new Sprite_Rectangle(actors.get(i).getPosition(), 4);
			sprite.setVisible(actors.get(i).getVisible());
			actors.get(i).setSpriteInterface(sprite);
			spriteManager.addSprite(sprite, actors.get(i).getSpriteName());
		}

		for (int i = (m_width * 16) - 1; i >= 0; i--) {
			for (int j = (m_height * 16) - 1; j >= 0; j--) {
				// detect sprites
				if (zone.getTiles()[i][j] != null) {
					if (zone.getTiles()[i][j].getWidgetObject() != null) {
						String str = zone.getTiles()[i][j].getWidgetObject().getClass().getName();
						if (str.contains("Sprite")) {
							WidgetSprite widget = (WidgetSprite) zone.getTiles()[i][j].getWidgetObject();

							int width = widget.getWidth();
							int height = widget.getheight();
							float h = height;
							Tile_Int tiles[] = new Tile_Int[4];
							tiles[0] = zone.getTiles()[i + 1][j + 1];
							tiles[2] = zone.getTiles()[i + 1][j + height - 1];
							tiles[1] = zone.getTiles()[i + width - 1][j + 1];
							tiles[3] = zone.getTiles()[i + width - 1][j + height - 1];

							SegSprite sprite = new SegSprite(widget.getImage(), tiles,
									new Vec2f(i, j - (h * 0.5F) - 1.0F), width, height);
							imageOverlays.add(sprite);
							sprite.Generate();
						}
					}
				}
			}
		}
	}

	public void addActor(Actor actor) {
		if (actor.getSpriteInterface() != null) {
			Sprite sprite = (Sprite) actor.getSpriteInterface();
			spriteManager.removeSprite(sprite, actor.getSpriteName());
		}
		Sprite_Rectangle sprite = new Sprite_Rectangle(actor.getPosition(), 4);
		sprite.setVisible(actor.getVisible());
		actor.setSpriteInterface(sprite);
		spriteManager.addSprite(sprite, actor.getSpriteName());
	}

	public void End() {
		for (int i = 0; i < m_width; i++) {
			for (int j = 0; j < m_height; j++) {
				m_layers[i][j].Discard();
			}
		}

		spriteManager.discard();

		if (imageOverlays.size() > 0) {
			for (int i = 0; i < imageOverlays.size(); i++) {
				imageOverlays.get(i).CleanTexture();
				imageOverlays.get(i).Discard();
			}
		}
	}

}
