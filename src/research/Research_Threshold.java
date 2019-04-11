package research;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import actorRPG.Actor_RPG;
import actorRPG.player.Player_RPG;
import playerscreens.Popup;
import shared.ParserHelper;

public class Research_Threshold implements Research {

	private int threshold;
	private String name;
	private String group;

	public Research_Threshold() {


	}

	public Research_Threshold(int threshold, String name, String group) {
		this.threshold = threshold;
		this.name = name;
		this.group = group;
	}

	@Override
	public int getType() {
		return 1;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(threshold);
		ParserHelper.SaveString(dstream, name);
		if (group != null) {
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, group);
		} else {
			dstream.writeBoolean(false);
		}
	}

	@Override
	public void load(DataInputStream dstream) throws IOException {
		threshold = dstream.readInt();
		name = ParserHelper.LoadString(dstream);
		if (dstream.readBoolean()) {
			group = ParserHelper.LoadString(dstream);
		}
	}

	@Override
	public boolean attempt(Player_RPG player_RPG, Popup popup, Encyclopedia e) {
		int skill = player_RPG.getAttribute(Actor_RPG.SCIENCE);

		if (skill >= threshold) {
			popup.setClock(10);
			if (e.addData(name, group)) {
				popup.setText(
						"you have successfully researched " + name + " and have compiled a new entry on your findings");
			} else {
				popup.setText("you have successfully researched " + name);
			}
			return true;

		} else {
			popup.setClock(10);
			popup.setText("you have failed to analyze " + name
					+ " your skills are insufficient, further research without improving your skills would be futile");
			return false;
		}

	}

}
