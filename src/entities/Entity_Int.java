package entities;

import java.util.ArrayList;

import org.w3c.dom.Element;

import zone.Landing;
import zone.Zone;

public interface Entity_Int {

	public Element LoadZone(Zone zone);

	public ArrayList<Landing> getLandings();

	public void postLoad(Zone zone);

	Zone getLandableZone(int x, int y);

	boolean isVisible();

	void calcVisible();
}
