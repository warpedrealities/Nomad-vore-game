package nomad;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Element;

import rendering.Sprite;
import shared.Vec2f;
import zone.Landing;
import zone.Zone;

abstract public class Entity implements Entity_Int {

	protected String entityName;
	protected Vec2f entityPosition;
	protected boolean entityVisibility;
	protected Sprite spriteObj;

	abstract public void Generate();

	public Vec2f getPosition() {
		return entityPosition;
	}

	public Zone getZone(int index) {
		return null;
	}

	public int getNumZones() {
		return 0;
	}

	public String getName() {
		return entityName;
	}

	public boolean getVisible() {
		return entityVisibility;
	}

	abstract public Zone getZone(String name, int x, int y);

	abstract public Zone getZone(String name);

	abstract public Zone getZone(int x, int y);

	abstract public Element LoadZone(Zone zone);

	abstract public void Save(String filename) throws IOException;

	abstract public boolean isStatic();

	abstract public void save(DataOutputStream dstream) throws IOException;

	abstract public String getSprite();

	abstract public float getSpriteSize();

	public boolean isLoaded() {
		return false;
	}

	@Override
	public ArrayList<Landing> getLandings() {
		// TODO Auto-generated method stub
		return null;
	}

	public Sprite getSpriteObj() {

		return spriteObj;
	}

	public void setSpriteObj(Sprite object) {

		spriteObj = object;
	}

	public void update() {
		// TODO Auto-generated method stub

	}

	abstract public Zone getLandableZone(int x, int y);

	public void systemEntry() {
		// TODO Auto-generated method stub
		
	}
}
