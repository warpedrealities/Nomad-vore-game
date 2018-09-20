package item.handlers;

import java.util.List;

import item.Item;
import item.ItemAmmo;
import item.ItemHasEnergy;
import item.instances.ItemDepletableInstance;
import item.instances.ItemStack;

public class ReloadingHandler {

	
	public static boolean reload(ItemDepletableInstance device, List<Item> inventory)
	{
		ItemHasEnergy item=(ItemHasEnergy)device.getItem();
		for (int i=0;i<inventory.size();i++)
		{
			if (ItemAmmo.class.isInstance(inventory.get(i).getItem()))
			{
				if (item.getEnergy().getRefill().contains(inventory.get(i).getItem().getName()))
				{
					ItemDepletableInstance ammo=null;
					if (ItemStack.class.isInstance(inventory.get(i)))
					{
						ammo=(ItemDepletableInstance) ((ItemStack)inventory.get(i)).takeItem();
						if (((ItemStack)inventory.get(i)).getCount()<1)
						{
							inventory.remove(i);
						}
					}
					else
					{
						ammo=(ItemDepletableInstance)inventory.get(i);
					}
					if (ammo.getEnergy()>0)
					{
						//drain item to try and replenish that which we're recharging
						float Eneed=item.getEnergy().getMaxEnergy()-device.getEnergy();
						float Eavailable=((float)ammo.getEnergy())*item.getEnergy().getrefillrate();
						if (Eneed>=Eavailable)
						{
							//drain ammo entirely
							ammo.setEnergy(0);
							device.setEnergy(device.getEnergy()+Eavailable);
							if (DisposeAmmo((ItemHasEnergy)ammo.getItem()) && inventory.size()>i)
							{	
								if (ammo.equals(inventory.get(i)))
								{
									inventory.remove(i);
								}
								//time tick
							}
							return true;
						}
						else
						{
							float Edrain=Eneed/item.getEnergy().getrefillrate();
							ammo.setEnergy(ammo.getEnergy()-Edrain);
							device.setEnergy(item.getEnergy().getMaxEnergy());
							if (!inventory.get(i).equals(ammo))
							{
								inventory.add(ammo);		
							}
							return true;
						}
					}
				}
			}
		}			
		
		
		return false;
	}
	
	public static boolean DisposeAmmo(ItemHasEnergy def)
	{
		if (def.getEnergy().getRefill()==null)
		{
			return true;
		}
		return false;
	}
	
}
