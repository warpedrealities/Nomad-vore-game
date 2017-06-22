package combat.statusEffects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class AttribMod {

	public int attribute;
	public int modifier;

	public void save(DataOutputStream dstream) throws IOException {

		dstream.writeInt(attribute);
		dstream.writeInt(modifier);
	}

	public void load(DataInputStream dstream) throws IOException {
		attribute = dstream.readInt();
		modifier = dstream.readInt();
	}
}
