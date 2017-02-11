package research;

import org.w3c.dom.Element;

import actor.Player;

public class Reward_Recipe implements Entry_Reward {

	String recipeName;
	
	public Reward_Recipe(Element element)
	{
		recipeName=element.getAttribute("recipe");
	}
	
	
	@Override
	public void runReward(Player player) {
		// TODO Auto-generated method stub
		player.getCraftingLibrary().unlockRecipe(recipeName);
	}

}
