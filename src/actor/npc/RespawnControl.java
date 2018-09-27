package actor.npc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import shared.ParserHelper;
import shared.Vec2f;
import vmo.GameManager;

public class RespawnControl {

	Vec2f startPosition;
	long timeGone;
	long respawnTime;
	boolean isGone;

	public RespawnControl(Element node, Vec2f position) {
		startPosition = new Vec2f(position.x, position.y);
		respawnTime = Integer.parseInt(node.getAttribute("delay"));
	}

	public RespawnControl(int time, Vec2f position)
	{
		respawnTime=time;
		startPosition=position.replicate();
	}
	
	public RespawnControl(RespawnControl respawn, Vec2f position) {

		startPosition = new Vec2f(position.x, position.y);
		respawnTime = respawn.respawnTime;
	}

	public void setGone() {
		isGone = true;
		timeGone = GameManager.getClock();
	}

	public Vec2f getStartPosition() {
		return startPosition;
	}

	public void Reset() {
		isGone = false;
	}

	public boolean Canrespawn() {
		if (isGone == false) {
			return false;
		}
		if (respawnTime <= 0) {
			return false;
		}
		if (timeGone + respawnTime < GameManager.getClock()) {
			return true;
		}
		return false;
	}

	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		startPosition.Save(dstream);
		dstream.writeBoolean(isGone);
		dstream.writeLong(respawnTime);
		dstream.writeLong(timeGone);
	}

	public RespawnControl(DataInputStream dstream) throws IOException {
		// TODO Auto-generated constructor stub
		startPosition = new Vec2f(dstream);
		isGone = dstream.readBoolean();
		respawnTime = dstream.readLong();
		timeGone = dstream.readLong();
	}
}
