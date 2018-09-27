package worldgentools.islandgenerator;

public class Island {

	private int size;
	private char[][] contents;

	public Island(int width, int height) {
		contents = new char[width][];
		for (int i = 0; i < contents.length; i++) {
			contents[i] = new char[height];
		}
		size = 0;
	}

	public void addTile(int x, int y) {
		if (contents[x][y] < 2) {
			size++;
		}
		contents[x][y] = 2;

		addAdjacent(x, y);
	}

	public boolean isNeighbour(Island island) {
		for (int i = 0; i < contents.length; i++) {
			for (int j = 0; j < contents[0].length; j++) {
				if (contents[i][j] == 2 && island.isAdjacent(i, j)) {
					absorbIsland(island);
					return true;
				}
			}
		}

		return false;
	}

	public void absorbIsland(Island island) {
		for (int i = 0; i < contents.length; i++) {
			for (int j = 0; j < contents[0].length; j++) {
				if (island.contents[i][j] == 2 && contents[i][j] != 2) {
					island.contents[i][j] = 2;
				}
			}
		}
		size += island.size;
	}

	private void addAdjacent(int x, int y) {
		if (x > 0) {
			if (contents[x - 1][y] == 0) {
				contents[x - 1][y] = 1;
			}
		}
		if (x < contents.length - 2) {
			if (contents[x + 1][y] == 0) {
				contents[x + 1][y] = 1;
			}
		}

		if (y > 0) {
			if (contents[x][y - 1] == 0) {
				contents[x][y - 1] = 1;
			}
		}
		if (y < contents[0].length - 2) {
			if (contents[x][y + 1] == 0) {
				contents[x][y + 1] = 1;
			}
		}

	}

	public boolean isAdjacent(int x, int y) {
		if (contents[x][y] == 1) {
			return true;
		}
		return false;
	}

	public boolean isInside(int x, int y) {
		if (contents[x][y] == 2) {
			return true;
		}
		return false;
	}

	public int getSize() {
		return size;
	}
}
