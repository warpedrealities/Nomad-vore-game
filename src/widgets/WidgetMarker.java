package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class WidgetMarker extends Widget {

	private int UID;

	public WidgetMarker(int UID) {
		isWalkable = true;
		isVisionBlocking = false;
		this.UID = UID;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(28);
		dstream.writeInt(UID);
		commonSave(dstream);
	}

	public WidgetMarker(DataInputStream dstream) throws IOException {
		this.UID = dstream.readInt();
		commonLoad(dstream);
	}

	public int getUID() {
		return UID;
	}

}
