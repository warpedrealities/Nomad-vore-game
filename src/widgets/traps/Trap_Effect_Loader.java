package widgets.traps;

import java.io.DataInputStream;
import java.io.IOException;

public class Trap_Effect_Loader {

	public static Trap_Effect loadTrap(DataInputStream dstream) throws IOException {
		int index = dstream.readInt();

		switch (index) {
		case 0:
			return new Trap_Combat(dstream);

		case 1:
			return new Trap_Monstercloset(dstream);
		}
		return null;
	}

}
