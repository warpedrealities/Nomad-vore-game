package view.ui;

import actor.player.Inventory;
import actor.player.Player;
import item.Item;
import item.ItemAmmo;
import item.ItemConsumable;
import item.ItemEquip;
import item.ItemWeapon;
import item.handlers.ReloadingHandler;
import item.instances.ItemDepletableInstance;
import item.instances.ItemStack;

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

		if (ItemDepletableInstance.class.isInstance(player.getInventory().getSlot(Inventory.HAND))) {
			ItemDepletableInstance idi = (ItemDepletableInstance) player.getInventory().getSlot(Inventory.HAND);
			ReloadingHandler.reload(idi, item, player.getInventory().getItems());
		}
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
		ItemEquip equip = (ItemEquip) item.getItem();
		Item it = null;
		if (equip.isStackEquip() == false) {
			it = player.getInventory().RemoveItem(item);
		} else {
			it = item;
			player.getInventory().setWeight(player.getInventory().getWeight() - it.getWeight());
			player.getInventory().getItems().remove(item);
		}
		Item hand = player.Equip(Inventory.HAND, item);
		if (hand != null) {
			player.getInventory().AddItem(hand);
		}
		player.addBusy(1);
	}

}
