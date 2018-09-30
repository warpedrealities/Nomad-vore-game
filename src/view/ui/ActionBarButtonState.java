package view.ui;

import actor.player.Inventory;
import actor.player.Player;
import actorRPG.player.Player_RPG;
import combat.CooldownHandler;
import gui.Button4Colour;
import nomad.universe.actionBar.Action;
import nomad.universe.actionBar.Action.ActionBarType;

public class ActionBarButtonState {

	private CooldownHandler cooldownHandler;
	private Inventory inventory;

	public ActionBarButtonState(Player player) {
		Player_RPG rpg = (Player_RPG) player.getRPG();
		cooldownHandler = rpg.getCooldownHandler();
		inventory = player.getInventory();
	}

	public void calculate(Action action, Button4Colour button, boolean shortened) {
		if (action.isActive()) {
			if (action.getType() == ActionBarType.ABILITY) {
				if (!cooldownHandler.moveIsUnusable(action.getActionName())) {
					button.setColour(0);
				} else {
					button.setColour(1);
				}
			}
			if (action.getType() == ActionBarType.ITEM) {
				if (inventory.countItem(action.getActionName()) > 0) {
					button.setColour(2);
				} else {
					button.setColour(3);
				}
			}
			if (shortened) {
				button.setString(action.getActionName().substring(0, 4));
			} else {
				button.setString(action.getActionName());
			}
		} else {
			button.setColour(4);
			button.setString("");
		}
	}

}
