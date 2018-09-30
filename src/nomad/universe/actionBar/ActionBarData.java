package nomad.universe.actionBar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ActionBarData {

	private Action[] actions;

	public ActionBarData() {
		actions = new Action[8];
		for (int i = 0; i < 8; i++) {
			actions[i] = new Action();
		}
	}

	public ActionBarData(DataInputStream dstream) throws IOException {
		actions = new Action[8];
		for (int i = 0; i < 8; i++) {
			actions[i] = new Action(dstream);
		}
	}


	public void save(DataOutputStream dstream) throws IOException {
		for (int i = 0; i < 8; i++) {
			actions[i].save(dstream);
		}
	}

	public Action getAction(int i) {
		return actions[i];
	}

}
