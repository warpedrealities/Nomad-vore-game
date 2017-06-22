package effect;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Scanner;

import org.w3c.dom.Element;

import actor.player.Player_LOOK;
import description.BodyPart;
import description.MacroLibrary;

public class Mutation {

	String bodyPart;
	String partVariable;
	String description;
	int modifierValue;
	boolean setVariable;

	enum BodyMod {
		BM_NONE, BM_ADD, BM_REMOVE
	};

	BodyMod bodyMod;

	public Mutation(Element element) {
		bodyMod = BodyMod.BM_NONE;
		bodyPart = element.getAttribute("part");
		partVariable = element.getAttribute("variable");
		description = element.getTextContent();
		if (element.getAttribute("modifier").length() > 0) {
			modifierValue = Integer.parseInt(element.getAttribute("modifier"));
		}
		if (element.getAttribute("set").length() > 0) {
			setVariable = true;
		}

		if (element.getAttribute("bodychange").length() > 0) {
			String str = element.getAttribute("bodychange");
			if (str.equals("add")) {
				bodyMod = BodyMod.BM_ADD;
			}
			if (str.equals("remove")) {
				bodyMod = BodyMod.BM_REMOVE;
			}
		}
	}

	public void processMutation(Player_LOOK look, StringBuilder builder) {
		// do the mutation
		// handle body part addition
		if (!bodyMod.equals(bodyMod.BM_NONE)) {
			if (bodyMod.equals(bodyMod.BM_REMOVE)) {
				look.removeBodyPart(bodyPart);
			} else {
				look.addPart(new BodyPart(bodyPart));
			}
		}
		// handle any variable mods
		if (!partVariable.equals("") && modifierValue != 0) {
			if (setVariable == true) {
				look.getPart(bodyPart).setValue(partVariable, modifierValue);
			} else {
				int v = look.getPart(bodyPart).getValue(partVariable);
				v = v + modifierValue;
				look.getPart(bodyPart).setValue(partVariable, v);
			}
		}

		// build strings
		Scanner input = new Scanner(description);

		while (input.hasNext()) {
			String str = input.next();
			if (!str.contains("MACRO") && !str.contains("VALUE") && !str.contains("CHANGE")) {
				builder.append(str + " ");
			}
			if (str.contains("MACRO")) {
				String macroname = str.replace("MACRO", "");
				builder.append(MacroLibrary.getInstance().lookupMacro(look, macroname) + " ");
			}
			if (str.contains("VALUE")) {
				builder.append(look.getPart(bodyPart).getValue(partVariable) + " ");
			}
			if (str.contains("CHANGE")) {
				builder.append(Math.abs(modifierValue) + " ");
			}
		}
		input.close();
	}

}
