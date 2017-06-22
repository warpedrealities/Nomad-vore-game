package zonePreload;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.Vec2i;

public class ZonePreloadVector extends ZonePreload {

	private Vec2i position;

	public ZonePreloadVector() {
		preloadType = PRELOADVECTOR;
	}

	public ZonePreloadVector(Vec2i p, int id) {
		preloadType = PRELOADVECTOR;
		position = p;
		preloadID = id;
	}

	public Vec2i getPosition() {
		return position;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(preloadType);
		dstream.writeInt(preloadID);
		dstream.writeInt(position.x);
		dstream.writeInt(position.y);
	}

	@Override
	public void load(DataInputStream dstream) throws IOException {

		preloadID = dstream.readInt();
		position = new Vec2i(dstream.readInt(), dstream.readInt());

	}

}
