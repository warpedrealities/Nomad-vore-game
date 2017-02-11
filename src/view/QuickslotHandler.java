package view;

import actor.Inventory;
import actor.Player;
import item.Item;
import item.ItemConsumable;
import item.ItemStack;
import item.ItemWeapon;
import nomad.Universe;

public class QuickslotHandler {

	Universe gameUniverse;
	Player player;
	
	public QuickslotHandler()
	{
		gameUniverse=Universe.getInstance();
		player=gameUniverse.getPlayer();
	}
		
	public void handle() {

		Item item=player.getInventory().getSlot(Inventory.QUICK);
		if (ItemWeapon.class.isInstance(item.getItem()))
		{
			handleSwap(item);
		}
		if (ItemConsumable.class.isInstance(item.getItem()))
		{
			handleConsumable(item);
		}
	}

	private void handleSwap(Item item) {
		Item hand=player.Equip(Inventory.HAND,item);
		player.getInventory().setSlot(hand, Inventory.QUICK);
		player.addBusy(1);
		ViewScene.m_interface.UpdateInfo();
	}
	
	private void handleConsumable(Item item) {
		// TODO Auto-generated method stub
		if (item.getClass().getName().contains("Consumable"))
		{
			ItemConsumable consumable=(ItemConsumable)item;
			for (int i=0;i<consumable.getNumEffects();i++)
			{
				player.ApplyEffect(consumable.getEffect(i));	
			
			}			
			player.addBusy(2);
			player.getInventory().setWeight(player.getInventory().getWeight()-item.getItem().getWeight());
			if (ItemStack.class.isInstance(item))
			{
				ItemStack stack=(ItemStack)item;
				stack.getItem();
				if (stack.getCount()<=0)
				{
					player.getInventory().setSlot(null, Inventory.QUICK);
				}
			}
			else
			{
				player.getInventory().setSlot(null, Inventory.QUICK);
			}
		}
	}

	

}
