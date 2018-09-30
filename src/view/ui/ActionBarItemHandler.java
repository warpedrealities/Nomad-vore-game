package view.ui;

import actor.player.Inventory;
import actor.player.Player;
import item.Item;
import item.ItemAmmo;
import item.ItemConsumable;
import item.ItemHasEnergy;
import item.ItemWeapon;
import item.instances.ItemDepletableInstance;
import item.instances.ItemStack;
import view.ViewScene;

public class ActionBarItemHandler {
	private Player player;

	public ActionBarItemHandler(Player player) {
		this.player = player;

	}

	public void handleItem(Item item) {
		if (ItemWeapon.class.isInstance(item.getItem())) {
			handleSwap(item);
		}
		if (ItemConsumable.class.isInstance(item.getItem())) {
			handleConsumable(item);
		}
		if (ItemAmmo.class.isInstance(item.getItem())) {
			handleAmmo(item);
		}
	}

	private void handleAmmo(Item item) {

		if (!ItemDepletableInstance.class.isInstance(item) || !ItemAmmo.class.isInstance(item.getItem())) {
			return;
		}
		if (ItemDepletableInstance.class.isInstance(player.getInventory().getSlot(Inventory.HAND))) {
			ItemDepletableInstance idi = (ItemDepletableInstance) player.getInventory().getSlot(Inventory.HAND);

			ItemHasEnergy ihe = (ItemHasEnergy) idi.getItem();
			if (ihe.getEnergy() != null && ihe.getEnergy().getMaxEnergy() > idi.getEnergy()) {
				if (ihe.getEnergy().getRefill() != null
						&& ihe.getEnergy().getRefill().contains(item.getItem().getName())) {
					ItemDepletableInstance ammo = (ItemDepletableInstance) item;
					if (ammo.getEnergy() > 0) {
						player.addBusy(1);
						// drain item to try and replenish that which we're recharging
						float Eneed = ihe.getEnergy().getMaxEnergy() - idi.getEnergy();
						float Eavailable = (ammo.getEnergy()) * ihe.getEnergy().getrefillrate();
						if (Eneed >= Eavailable) {
							// drain ammo entirely
							ammo.setEnergy(0);
							idi.setEnergy(idi.getEnergy() + Eavailable);
							if (DisposeAmmo((ItemHasEnergy) ammo.getItem())) {
								player.getInventory().RemoveItem(item);
								player.addBusy(1);
								ViewScene.m_interface.UpdateInfo();
							}
						} else {
							float Edrain = Eneed / ihe.getEnergy().getrefillrate();
							ammo.setEnergy(ammo.getEnergy() - Edrain);
							idi.setEnergy(ihe.getEnergy().getMaxEnergy());
						}
					}
				}
			}
		}
	}

	private boolean DisposeAmmo(ItemHasEnergy def) {
		if (def.getEnergy().getRefill() == null) {
			return true;
		}
		return false;
	}

	private void handleConsumable(Item item) {
		if (item.getItem().getClass().getName().contains("Consumable")) {
			ItemConsumable consumable = (ItemConsumable) item.getItem();
			for (int i = 0; i < consumable.getNumEffects(); i++) {
				player.ApplyEffect(consumable.getEffect(i));

			}
			player.addBusy(2);
			player.getInventory().setWeight(player.getInventory().getWeight() - item.getItem().getWeight());
			if (ItemStack.class.isInstance(item)) {
				ItemStack stack = (ItemStack) item;
				stack.takeItem();
				if (stack.getCount() <= 0) {
					player.getInventory().RemoveItem(item);
				}
			} else {
				player.getInventory().RemoveItem(item);
			}

		}
	}

	private void handleSwap(Item item) {
		Item hand = player.Equip(Inventory.HAND, item);
		if (hand != null) {
			player.getInventory().AddItem(hand);
		}
		player.addBusy(1);
	}

}
