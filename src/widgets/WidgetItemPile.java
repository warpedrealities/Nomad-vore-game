package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import actor.player.Player;
import view.ViewScene;

import item.Item;
import item.ItemCoin;
import nomad.universe.Universe;

public class WidgetItemPile extends Widget {

	ArrayList<Item> m_items;

	public WidgetItemPile(int sprite, String description) {
		m_items = new ArrayList<Item>();

		widgetSpriteNumber = sprite;
		isVisionBlocking = false;
		isWalkable = true;
		widgetDescription = description;
	}

	public WidgetItemPile(int sprite, String description, Item item) {
		m_items = new ArrayList<Item>();
		if (item != null) {
			m_items.add(item);
		}

		widgetSpriteNumber = sprite;
		isVisionBlocking = false;
		isWalkable = true;
		widgetDescription = description;
	}

	public int getCount() {
		return m_items.size();
	}

	public Item getItem() {
		return m_items.get(0);
	}

	public void AddItem(Item item) {
		m_items.add(item);
	}

	@Override
	public String getDescription() {
		String str = widgetDescription;
		for (int i = 0; i < m_items.size(); i++) {
			str = str + "," + m_items.get(i).getName();
		}
		return str;
		// rebuild this to list items
	}

	@Override
	public boolean Interact(Player player) {
		// grab item
		Item item = m_items.get(0);
		if (ItemCoin.class.isInstance(item)) {
			ItemCoin coin = (ItemCoin) item;
			player.getInventory().setPlayerGold(player.getInventory().getPlayerGold() + coin.getCount());
		}
		else
		{
			player.getInventory().AddItem(item);			
		}
		player.calcMove();
		m_items.remove(0);

		ViewScene.m_interface.DrawText("picked up " + item.getName());
		// check item count
		if (m_items.size() == 0) {
			ViewScene.m_interface.RemoveWidget(this);
		}
		// if item count 0, remove this widget
		player.TakeAction();
		return false;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.write(2);
		commonSave(dstream);
		dstream.writeInt(m_items.size());
		for (int i = 0; i < m_items.size(); i++) {
			m_items.get(i).save(dstream);
		}
	}

	public WidgetItemPile(DataInputStream dstream) throws IOException {
		// TODO Auto-generated constructor stub
		commonLoad(dstream);
		m_items = new ArrayList<Item>();
		int count = dstream.readInt();
		for (int i = 0; i < count; i++) {
			m_items.add(Universe.getInstance().getLibrary().getItem(dstream));
		}

	}

}
