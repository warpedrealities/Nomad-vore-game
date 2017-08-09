package nomad;

import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import shared.Vec2f;

import zone.Zone;

public class Star extends Entity {
	String filename;
	int intensity;

	public Star(int intensity, int x, int y, String filename) {
		// TODO Auto-generated constructor stub
		this.filename = filename;
		this.intensity = intensity;
		entityPosition = new Vec2f(x, y);
		entityVisibility = true;
		entityName = "star";
	}

	@Override
	public void Generate() {
		// TODO Auto-generated method stub

	}

	@Override
	public Zone getZone(String name, int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Zone getZone(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element LoadZone(Zone zone) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void Save(String filename) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isStatic() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSprite() {
		// TODO Auto-generated method stub
		return filename;
	}

	@Override
	public float getSpriteSize() {
		return 5;
	}

	@Override
	public void postLoad(Zone zone) {
		// TODO Auto-generated method stub

	}

	@Override
	public Zone getLandableZone(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Zone getZone(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getIntensity() {
		return intensity;
	}

}
