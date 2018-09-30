package nomad.universe.actionBar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Action {

	public enum ActionBarType {
		NONE, ABILITY, ITEM
	};

	private ActionBarType type;
	private String actionName;
	private boolean active;

	public Action() {
		type = ActionBarType.NONE;
		active = false;
		actionName = null;
	}

	public Action(DataInputStream dstream) throws IOException {
		type = ActionBarType.values()[dstream.readInt()];
	}

	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(type.ordinal());
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public ActionBarType getType() {
		return type;
	}

	public void setType(ActionBarType type) {
		this.type = type;
	}


}
