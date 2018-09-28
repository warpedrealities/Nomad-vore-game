package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import actor.player.Player;
import shared.ParserHelper;
import zone.Zone;

abstract public class Widget {

	protected int widgetSpriteNumber;
	protected boolean isWalkable;
	protected boolean isVisionBlocking;
	protected String widgetDescription;

	public boolean Walkable() {
		return isWalkable;
	}

	public String getDescription() {
		return widgetDescription;
	}

	public boolean BlockVision() {
		return isVisionBlocking;
	}

	public int getSprite() {
		return widgetSpriteNumber;
	}

	public boolean Step() {
		return false;
	}

	public boolean Visit() {
		return false;
	}

	public boolean Interact(Player player) {

		return false;
	}

	public void Regen(long l, Zone zone) {

	}

	public abstract void save(DataOutputStream dstream) throws IOException;

	protected void commonSave(DataOutputStream dstream) throws IOException {
		dstream.writeBoolean(isWalkable);
		dstream.writeBoolean(isVisionBlocking);
		dstream.writeInt(widgetSpriteNumber);
		if (widgetDescription != null) {
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, widgetDescription);
		} else {
			dstream.writeBoolean(false);
		}

	}

	public boolean safeOnly() {
		return false;
	}

	protected void commonLoad(DataInputStream dstream) throws IOException {
		isWalkable = dstream.readBoolean();
		isVisionBlocking = dstream.readBoolean();
		widgetSpriteNumber = dstream.readInt();
		if (dstream.readBoolean()) {
			widgetDescription = ParserHelper.LoadString(dstream);
		}

	}

	public void setDescription(String textContent) {
		widgetDescription = textContent;
	}

}
