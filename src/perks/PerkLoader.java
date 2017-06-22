package perks;

import java.io.DataInputStream;
import java.io.IOException;

import shared.ParserHelper;

public class PerkLoader {

	public static PerkInstance loadPerk(DataInputStream stream) throws IOException {
		String name = ParserHelper.LoadString(stream);
		Perk perk = PerkLibrary.getInstance().findPerk(name);
		if (perk != null) {
			PerkInstance perkInstance = new PerkInstance(perk, stream.readInt());
			return perkInstance;
		}
		return null;
	}
}
