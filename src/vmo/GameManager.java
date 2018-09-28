package vmo;

import java.util.Random;

import rlforj.los.IFovAlgorithm;
import rlforj.los.ILosAlgorithm;
import rlforj.los.PrecisePermissive;

public class GameManager {

	public static Random m_random;
	public static IFovAlgorithm m_vision;
	public static ILosAlgorithm m_los;
	public static long worldClock;

	public GameManager() {
		m_random = new Random();
		m_vision = new PrecisePermissive();
		m_los = new PrecisePermissive();
	}

	public void Newgame() {

	}

	public void Update(float dt) {

	}

	public static long getClock() {
		return worldClock;
	}

	public static void AddClock(int clock) {
		worldClock += clock;
	}

}
