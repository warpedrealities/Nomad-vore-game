package actor.player.reformation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.ParserHelper;

public class MachineEntry {

	private String zoneName;
	private long uid;

	public MachineEntry(String zone, long uid) {
		this.zoneName = zone;
		this.uid = uid;
	}

	public MachineEntry(DataInputStream dstream) throws IOException {
		zoneName = ParserHelper.LoadString(dstream);
		uid = dstream.readLong();
	}

	public void save(DataOutputStream dstream) throws IOException {
		ParserHelper.SaveString(dstream, zoneName);
		dstream.writeLong(uid);
	}

	public String getZoneName() {
		return zoneName;
	}

	public long getUid() {
		return uid;
	}

}
