package actor.npc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.ParserHelper;

public class ScriptPackage {

	String onDeath;
	String isSpawnable;

	public ScriptPackage(String spawnable, String death) {
		onDeath = death;
		isSpawnable = spawnable;
	}

	public String getOnDeath() {
		return onDeath;
	}

	public String getIsSpawnable() {
		return isSpawnable;
	}

	public ScriptPackage(DataInputStream dstream) throws IOException {
		boolean b = dstream.readBoolean();
		if (b) {
			isSpawnable = ParserHelper.LoadString(dstream);
		}
		b = dstream.readBoolean();
		if (b) {
			onDeath = ParserHelper.LoadString(dstream);
		}

	}

	public void save(DataOutputStream dstream) throws IOException {
		if (isSpawnable != null) {
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, isSpawnable);
		} else {
			dstream.writeBoolean(false);
		}
		if (onDeath != null) {
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, onDeath);
		} else {
			dstream.writeBoolean(false);
		}

	}

}
