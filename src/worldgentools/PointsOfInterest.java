package worldgentools;

import java.util.ArrayList;

import shared.Vec2i;

public class PointsOfInterest {

	ArrayList<Vec2i> pointList;
	int currentPOI;

	public PointsOfInterest() {
		currentPOI = 0;
		pointList = new ArrayList<Vec2i>();
	}

	public void addPOI(Vec2i position) {
		pointList.add(position);
	}

	public Vec2i getNextPOI() {
		currentPOI++;
		return pointList.get(currentPOI - 1);
	}

	public Vec2i getCurrentPOI() {
		return pointList.get(currentPOI - 1);
	}
}
