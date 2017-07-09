package widgets;

import item.ItemKeyInstance;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import shared.ParserHelper;
import view.ViewScene;
import vmo.GameManager;
import actor.player.Inventory;
import actor.player.Player;
import actorRPG.Actor_RPG;

public class WidgetDoor extends WidgetBreakable {

	int lockStrength;
	String lockKey;
	boolean jammedLock;

	public WidgetDoor(Element enode) {
		super(enode);

	}

	public void setLockStrength(int strength) {
		lockStrength = strength;
	}

	public void setLockKey(String key) {
		lockKey = key;
	}

	public String getLockKey() {
		return lockKey;
	}

	private boolean canUnlock(Inventory inventory) {
		for (int i = 0; i < inventory.getItems().size(); i++) {
			if (ItemKeyInstance.class.isInstance(inventory.getItems().get(i))) {
				ItemKeyInstance iki = (ItemKeyInstance) inventory.getItems().get(i);
				if (iki.getLock().equals(lockKey)) {
					ViewScene.m_interface.DrawText("you unlock the door with the " + iki.getName());
					inventory.RemoveItem(iki);
					ViewScene.m_interface.RemoveWidget(this);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean Interact(Player player) {
		if (lockStrength > 0) {
			if (canUnlock(player.getInventory())) {
				return true;
			}

			if (jammedLock == true) {
				ViewScene.m_interface.DrawText("The lock is jammed, you'll need to find the key");
				return true;
			}

			// attempt to pick the lock
			int roll = GameManager.m_random.nextInt(20) + player.getRPG().getAttribute(Actor_RPG.TECH);
			player.addBusy(10);
			ViewScene.m_interface.DrawText("you attempt to pick the lock");
			if (roll >= lockStrength) {
				ViewScene.m_interface.DrawText("success");
				ViewScene.m_interface.RemoveWidget(this);
				return true;
			} else {
				ViewScene.m_interface.DrawText("you have jammed the lock trying to pick it");
				jammedLock = true;
				return true;
			}
		} else if (lockStrength == 0) {
			ViewScene.m_interface.DrawText("you open the door");
			ViewScene.m_interface.RemoveWidget(this);
			return true;
		}
		return false;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.write(11);
		commonSave(dstream);
		super.saveBreakable(dstream);

		dstream.writeInt(lockStrength);
		dstream.writeBoolean(jammedLock);

		if (lockKey != null) {
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, lockKey);
		} else {
			dstream.writeBoolean(false);
		}

	}

	public WidgetDoor(DataInputStream dstream) throws IOException {

		commonLoad(dstream);
		load(dstream);

		lockStrength = dstream.readInt();
		jammedLock = dstream.readBoolean();

		boolean b = dstream.readBoolean();
		if (b == true) {
			lockKey = ParserHelper.LoadString(dstream);
		}
	}
}
