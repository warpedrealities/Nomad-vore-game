package worldgentools;

import shared.Vec2f;
import view.ZoneInteractionHandler;
import vmo.GameManager;
import zone.Tile;
import zone.TileDefLibrary;
import zone.Zone;

public class CaveGenerator {

	Tile[][] m_tiles;
	boolean[][] m_grid;
	int m_width, m_height;
	boolean[][] m_worksheet;
	TileDefLibrary m_library;
	int m_threshold;
	Zone m_zone;

	public CaveGenerator(Tile[][] tiles, boolean[][] grid, TileDefLibrary library, Zone zone) {
		m_library = library;
		m_tiles = tiles;
		m_width = m_tiles.length;
		m_height = m_tiles[0].length;
		m_grid = grid;
		m_zone = zone;
		m_worksheet = new boolean[m_width][];
		for (int i = 0; i < m_height; i++) {
			m_worksheet[i] = new boolean[m_height];
		}
	}

	void Setup(int range, int threshold) {

		m_threshold = 0;
		for (int i = 0; i < m_width; i++) {
			for (int j = 0; j < m_height; j++) {
				if (m_grid[i][j] == true) {
					if (GameManager.m_random.nextInt(range) < threshold || m_tiles[i][j] != null) {
						m_worksheet[i][j] = true;
					} else {
						m_worksheet[i][j] = false;
					}
					m_threshold++;
				}
			}
		}
	}

	void Iterate(int iterations) {
		boolean spawn = true;
		if (iterations == 0) {
			spawn = false;
		}
		boolean[][] ngrid = new boolean[m_width][];
		for (int i = 0; i < m_width; i++) {
			ngrid[i] = new boolean[m_height];
			for (int j = 0; j < m_height; j++) {
				if (m_grid[i][j]) {
					if (TestCell(i, j, spawn)) {
						ngrid[i][j] = true;
					} else {
						ngrid[i][j] = false;
					}
				}
			}

		}
		m_worksheet = ngrid;
		if (iterations > 0) {
			Iterate(iterations - 1);
		}
	}

	Boolean CheckSolid(int x, int y) {
		if (x < 0 || x >= m_width) {
			return true;
		}
		if (y < 0 || y >= m_height) {
			return true;
		}
		if (m_grid[x][y] == false) {
			return true;
		}
		if (m_worksheet[x][y] == true) {
			return true;
		}
		return false;
	}

	boolean TestCell(int x, int y, boolean spawn) {
		int count = 0;
		for (int i = 0; i < 8; i++) {
			Vec2f p = ZoneInteractionHandler.getPos(i, new Vec2f(x, y));
			if (CheckSolid((int) p.x, (int) p.y)) {
				count++;
			}
		}
		if (m_worksheet[x][y] == true && count >= 4) {
			return true;
		}
		if (m_worksheet[x][y] == false && count >= 5) {
			return true;
		}
		if (count == 0 && spawn == true) {
			return true;
		}
		return false;
	}

	void Finalize(int tile) {
		for (int i = 0; i < m_width; i++) {
			for (int j = 0; j < m_height; j++) {
				if (m_worksheet[i][j] == true && m_grid[i][j] == true && m_tiles[i][j] == null) {
					m_tiles[i][j] = new Tile(i, j, m_library.getDef(tile), m_zone, m_library);
					m_grid[i][j] = false;
				}
			}
		}
	}

	boolean TestSize(float adjusted) {
		int count = 0;
		for (int i = 0; i < m_width; i++) {
			for (int j = 0; j < m_height; j++) {
				if (m_grid[i][j] == true && m_worksheet[i][j] == false) {
					count++;
				}
			}
		}
		if (count < adjusted) {
			return true;
		}
		return false;
	}

	public void Run(int bounds, int threshold, int iterations, int tile, float mincast) {

		Setup(bounds, threshold);

		Iterate(iterations);

		while (TestSize(m_threshold * mincast) == true) {
			Setup(bounds, threshold);

			Iterate(iterations);
		}
		Finalize(tile);
	}

	public Tile[][] getTiles() {
		return m_tiles;
	}

	public boolean[][] getGrid() {
		return m_grid;
	}

}
