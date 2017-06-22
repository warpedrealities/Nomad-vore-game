package perks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.ParserHelper;

public class PerkInstance {

	Perk perkDefinition;
	int perkRank;

	public PerkInstance(Perk definition) {
		perkDefinition = definition;
		perkRank = 1;
	}

	public PerkInstance(Perk definition, int rank) {

		perkDefinition = definition;
		perkRank = rank;
	}

	public int getPerkRank() {
		return perkRank;
	}

	public void setPerkRank(int perkRank) {
		this.perkRank = perkRank;
	}

	public Perk getPerk() {
		return perkDefinition;
	}

	public void save(DataOutputStream dstream) throws IOException {

		ParserHelper.SaveString(dstream, perkDefinition.getName());
		dstream.writeInt(perkRank);
	}
}
