package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import actor.player.Player;
import interactionscreens.ComputerScreen;
import interactionscreens.ReformerScreen;
import view.ViewScene;

public class WidgetReformer extends WidgetBreakable {

	private boolean active, suppressed;
	private long uid;

	public WidgetReformer(Element element) {
		super(element);

	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.write(23);
		commonSave(dstream);
		saveBreakable(dstream);
		dstream.writeBoolean(active);
		dstream.writeBoolean(suppressed);
		dstream.writeLong(uid);
	}

	public WidgetReformer(DataInputStream dstream) throws IOException {
		// TODO Auto-generated constructor stub
		commonLoad(dstream);
		load(dstream);
		active = dstream.readBoolean();
		suppressed = dstream.readBoolean();
		uid=dstream.readLong();
	}

	@Override
	public boolean Interact(Player player) {
		ViewScene.m_interface.setScreen(new ReformerScreen(this));
		return true;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isSuppressed() {
		return suppressed;
	}

	public void setSuppressed(boolean suppressed) {
		this.suppressed = suppressed;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

}
