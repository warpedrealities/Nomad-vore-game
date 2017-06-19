package chargen;

import java.util.ArrayList;

import actor.player.Player;

public interface Phase {
	
	String [] getChoices();
	
	int getChoiceCount();
	
	String getChoiceDescription(int index);
	
	void performChoice(int index, Player player);
	
	void rollback(Player player);
	
	String getName();
}
