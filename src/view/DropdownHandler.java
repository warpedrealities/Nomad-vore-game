package view;

import gui.Button2;
import gui.DropDown;
import actor.player.Player;
import actorRPG.player.Player_RPG;;

public class DropdownHandler {

	static public void genStandardDropdown(DropDown dropDown, Player player) {
		Player_RPG rpg = (Player_RPG) player.getRPG();
		String str = rpg.getQuickAction();
		String str0[];
		if (str == null) {
			str0 = new String[4];
		} else {
			str0 = new String[5];
		}

		str0[0] = "look";
		str0[1] = "interact";
		str0[2] = "attack";
		str0[3] = "special";

		if (str != null) {
			str0[4] = str;
		}
		dropDown.BuildFonts(str0);
	}

	static public void genSpecialDropdown(DropDown dropDown, Player player) {
		String str0[] = new String[4];
		str0[0] = "fight";
		str0[1] = "dominate";
		str0[2] = "movement";
		str0[3] = "other";
		dropDown.BuildFonts(str0);
	}

	static boolean genSpecialFight(DropDown dropdown, Player player) {
		Player_RPG rpg = (Player_RPG) player.getRPG();
		String str0[] = new String[rpg.getMoveCategorySize(0)];
		int offset = rpg.getMoveCategoryOffset(0);
		for (int i = 0; i < rpg.getMoveCategorySize(0); i++) {
			str0[i] = rpg.getCombatMove(i + offset).getMoveName();
		}
		dropdown.BuildFonts(str0);
		return false;
	}

	static public boolean genSpecialDominate(DropDown dropdown, Player player) {
		Player_RPG rpg = (Player_RPG) player.getRPG();
		String str0[] = new String[rpg.getMoveCategorySize(1)];
		int offset = rpg.getMoveCategoryOffset(1);
		for (int i = 0; i < rpg.getMoveCategorySize(1); i++) {
			str0[i] = rpg.getCombatMove(i + offset).getMoveName();
		}

		dropdown.BuildFonts(str0);
		return false;
	}

	static public boolean genSpecialMovement(DropDown dropdown, Player player) {
		Player_RPG rpg = (Player_RPG) player.getRPG();
		String str0[] = new String[rpg.getMoveCategorySize(2)];
		int offset = rpg.getMoveCategoryOffset(2);
		for (int i = 0; i < rpg.getMoveCategorySize(2); i++) {
			str0[i] = rpg.getCombatMove(i + offset).getMoveName();
		}

		dropdown.BuildFonts(str0);
		return false;
	}

	static public boolean genSpecialOther(DropDown dropdown, Player player) {
		Player_RPG rpg = (Player_RPG) player.getRPG();
		String str0[] = new String[rpg.getMoveCategorySize(3)];
		int offset = rpg.getMoveCategoryOffset(3);
		for (int i = 0; i < rpg.getMoveCategorySize(3); i++) {
			str0[i] = rpg.getCombatMove(i + offset).getMoveName();
		}

		dropdown.BuildFonts(str0);
		return false;
	}

	static public ViewScene.ViewMode selectMove(Player player, ViewScene.ViewMode mode, int selection, Button2 button,
			DropDown dropdown) {
		switch (mode) {
		case SELECT:
			// handle selecting between the 4 submenus
			return selectMenu(player, selection, button, dropdown);
		case FIGHT:
			return subMenu(0, player, selection, button, dropdown);
		case DOMINATE:
			return subMenu(1, player, selection, button, dropdown);
		case MOVEMENT:
			return subMenu(2, player, selection, button, dropdown);
		case OTHER:
			return subMenu(3, player, selection, button, dropdown);
		default:
			break;
		}

		return mode;
	}

	static public ViewScene.ViewMode selectMenu(Player player, int selection, Button2 button, DropDown dropdown) {
		Player_RPG rpg = (Player_RPG) player.getRPG();
		if (selection == 0 && rpg.getMoveCategorySize(0) > 0) {
			genSpecialFight(dropdown, player);
			return ViewScene.ViewMode.FIGHT;
		}
		if (selection == 1 && rpg.getMoveCategorySize(1) > 0) {
			genSpecialDominate(dropdown, player);
			return ViewScene.ViewMode.DOMINATE;
		}
		if (selection == 2 && rpg.getMoveCategorySize(2) > 0) {
			genSpecialMovement(dropdown, player);
			return ViewScene.ViewMode.MOVEMENT;
		}
		if (selection == 3 && rpg.getMoveCategorySize(3) > 0) {
			genSpecialOther(dropdown, player);
			return ViewScene.ViewMode.OTHER;
		}
		return ViewScene.ViewMode.SELECT;
	}

	static public ViewScene.ViewMode subMenu(int submenu, Player player, int selection, Button2 button,
			DropDown dropdown) {
		Player_RPG rpg = (Player_RPG) player.getRPG();
		if (selection >= rpg.getMoveCategorySize(submenu)) {
			return ViewScene.ViewMode.SPECIAL;
		}
		int index = selection + rpg.getMoveCategoryOffset(submenu);
		player.setMove(index);
		button.setString(player.getMove(index).getMoveName());
		dropdown.setVisible(false);
		return ViewScene.ViewMode.SPECIAL;
	}

	static public ViewScene.ViewMode handleClosure(ViewScene.ViewMode mode, Player player, Button2 button) {
		Player_RPG rpg = (Player_RPG) player.getRPG();
		int movedex = 0;
		switch (mode) {
		case SELECT:
			player.setMove(0);
			button.setString(player.getMove(0).getMoveName());
			return ViewScene.ViewMode.ATTACK;

		case FIGHT:
			movedex = rpg.getMoveCategoryOffset(0);
			player.setMove(movedex);
			button.setString(player.getMove(movedex).getMoveName());
			return ViewScene.ViewMode.SPECIAL;

		case DOMINATE:
			movedex = rpg.getMoveCategoryOffset(1);
			player.setMove(movedex);
			button.setString(player.getMove(movedex).getMoveName());
			return ViewScene.ViewMode.SPECIAL;

		case MOVEMENT:
			movedex = rpg.getMoveCategoryOffset(2);
			player.setMove(movedex);
			button.setString(player.getMove(movedex).getMoveName());
			return ViewScene.ViewMode.SPECIAL;

		case OTHER:
			movedex = rpg.getMoveCategoryOffset(3);
			player.setMove(movedex);
			button.setString(player.getMove(movedex).getMoveName());
			return ViewScene.ViewMode.SPECIAL;

		default:
			player.setMove(0);
			button.setString(player.getMove(0).getMoveName());
			return ViewScene.ViewMode.ATTACK;
		}

	}
}
