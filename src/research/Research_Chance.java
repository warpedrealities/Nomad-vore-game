package research;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import actorRPG.Actor_RPG;
import actorRPG.player.Player_RPG;
import nomad.universe.Universe;
import playerscreens.Popup;
import shared.ParserHelper;

public class Research_Chance implements Research {

	private int roll;
	private int DC;
	private String name;
	private String group;

	public Research_Chance() {

	}

	public Research_Chance(int DC, int roll, String name) {
		this.DC = DC;
		this.roll = roll;
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public int getRoll() {
		return roll;
	}

	public int getDC() {
		return DC;
	}

	public String getName() {
		return name;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(DC);
		dstream.writeInt(roll);
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
		DC = dstream.readInt();
		roll = dstream.readInt();
		name = ParserHelper.LoadString(dstream);
		if (dstream.readBoolean()) {
			group = ParserHelper.LoadString(dstream);
		}
	}

	@Override
	public int getType() {
		return 0;
	}

	@Override
	public boolean attempt(Player_RPG player_RPG, Popup popup, Encyclopedia e) {
		int diceroll = Universe.m_random.nextInt(20) + player_RPG.getAttribute(Actor_RPG.SCIENCE);

		int finalroll = (diceroll + roll) / 2;

		if (finalroll >= DC) {
			popup.setClock(10);
			if (e.addData(name, group)) {
				popup.setText("you have successfully researched " + name
						+ " and have compiled a new entry on your findings");
			} else {
				popup.setText("you have successfully researched " + name);
			}
			return true;

		} else {
			popup.setClock(10);
			popup.setText(
					"you have failed to reach meaningful conclusions on the topic of " + name
					+ " you need more data");
			return true;
		}


	}
}
