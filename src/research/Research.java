package research;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import actorRPG.player.Player_RPG;
import playerscreens.Popup;

public interface Research {

	int getType();

	void save(DataOutputStream dstream) throws IOException;

	void load(DataInputStream dstream) throws IOException;

	boolean attempt(Player_RPG player_RPG, Popup popup, Encyclopedia e);
}
