package dialogue.worldscript;

public interface WorldManipulator {

	void setZone(String zoneName);

	void removeWidget(int x, int y);

	void RestoreShip(String shipName, int x, int y);

}
