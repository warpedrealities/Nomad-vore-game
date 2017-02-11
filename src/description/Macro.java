package description;

import actor.Player_LOOK;

public abstract class Macro {

	String macroName,partName,variableName;
	
	public String getMacroName() {
		return macroName;
	}

	public String getPartName() {
		return partName;
	}

	public String getVariableName() {
		return variableName;
	}

	public abstract String readMacro(Player_LOOK look);
	
}
