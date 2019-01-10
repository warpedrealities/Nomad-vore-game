package landingScreen;

import java.nio.FloatBuffer;

import entities.World;
import gui.GUIBase;
import landingScreen.LandingElement.LandingClass;
import shared.SceneBase;
import shared.Vec2f;

public class LandingGrid extends GUIBase {

	LandingGridRender gridRender;
	LandingElement[][] gridElements;

	Vec2f corner;
	Vec2f farCorner;
	int xSelect;
	int ySelect;

	public LandingGrid(World world) {
		gridRender = new LandingGridRender();
		gridElements = LandingAnalyzer.generateGrid(world);
		gridRender.generate(gridElements);
		corner = gridRender.getCorner();
		farCorner = new Vec2f(corner.x + (4 * gridElements.length), corner.y + (4 * gridElements[0].length));
		updateSelect();

	}

	private void updateSelect() {
		for (int i = 0; i < gridElements.length; i++) {
			for (int j = 0; j < gridElements[0].length; j++) {
				if (gridElements[i][j] != null) {
					if (gridElements[i][j].getLandingType() == LandingClass.LC_SELECTED) {
						xSelect = i;
						ySelect = j;
						break;
					}
				}
			}
		}
	}

	public int getxSelect() {
		return xSelect;
	}

	public int getySelect() {
		return ySelect;
	}

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean ClickEvent(Vec2f pos) {
		if (pos.x > corner.x && pos.x < farCorner.x) {
			if (pos.y > corner.y && pos.y < farCorner.y) {
				int x = (int) (pos.x - corner.x);
				int y = (int) (pos.y - corner.y);
				x = x / 4;
				y = y / 4;

				if (gridElements[x][y] != null && gridElements[x][y].getLandingType() == LandingClass.LC_OPEN) {
					gridElements[x][y].setLandingType(LandingClass.LC_SELECTED);
					if (xSelect>-1 && ySelect>-1)
					{
						gridElements[xSelect][ySelect].setLandingType(LandingClass.LC_OPEN);	
						gridRender.setImage(xSelect, ySelect, LandingClass.LC_OPEN.ordinal());	
					}
					xSelect = x;
					ySelect = y;
					gridRender.setImage(xSelect, ySelect, LandingClass.LC_SELECTED.ordinal());
				}

				return true;
			}
		}
		return false;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {

		gridRender.draw(matrixloc, SceneBase.getVariables()[0], buffer);

	}

	@Override
	public void discard() {

		gridRender.end();
	}

	@Override
	public void AdjustPos(Vec2f p) {
		// TODO Auto-generated method stub

	}

	public void failedLanding() {
		// TODO Auto-generated method stub
		gridElements[xSelect][ySelect].setLandingType(LandingClass.LC_SELECTED);
		gridElements[xSelect][ySelect].setLandingType(LandingClass.LC_INVALID);
		gridRender.setImage(xSelect, ySelect, LandingClass.LC_INVALID.ordinal());
		xSelect = -1;
		ySelect = -1;
	}

}
