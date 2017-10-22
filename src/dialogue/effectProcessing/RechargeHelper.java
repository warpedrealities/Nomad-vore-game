package dialogue.effectProcessing;

import actor.player.Inventory;
import actor.player.Player;
import item.ItemHasEnergy;
import item.instances.ItemDepletableInstance;

public class RechargeHelper {

	private Player player;
	public RechargeHelper(Player player)
	{
		this.player=player;
		
	}
	
	public void run(int amount)
	{
		Inventory inventory=player.getInventory();
		for (int i=0;i<5;i++)
		{
			if (ItemDepletableInstance.class.isInstance(inventory.getSlot(i)))
			{
				amount=recharge((ItemDepletableInstance)inventory.getSlot(i),amount);
				if (amount<=0)
				{
					return;
				}
			}
		}
		
		for (int i=0;i<inventory.getNumItems();i++)
		{
			if (ItemDepletableInstance.class.isInstance(inventory.getItem(i)))
			{
				amount=recharge((ItemDepletableInstance)inventory.getItem(i),amount);
				if (amount<=0)
				{
					return;
				}			
			}			
		}
	}

	private int recharge(ItemDepletableInstance item,int amount) {
		if (ItemHasEnergy.class.isInstance(item.getItem()))
		{
			ItemHasEnergy IHE=(ItemHasEnergy)item.getItem();
			if (IHE.getEnergy().getMaxEnergy()>item.getEnergy() &&
					IHE.getEnergy().getRefill().equals("ENERGY"))
			{
				float Eneed=IHE.getEnergy().getMaxEnergy()-item.getEnergy();
				float Eavailable=((float)amount)*IHE.getEnergy().getrefillrate();
				if (Eneed>=Eavailable)
				{
					item.setEnergy(item.getEnergy()+Eavailable);
					amount=0;
				}
				else
				{
					float Edrain=Eneed/IHE.getEnergy().getrefillrate();
					amount-=Edrain;
					item.setEnergy(IHE.getEnergy().getMaxEnergy());			
				}
			}
		}
		
		return amount;
	}
	
}
