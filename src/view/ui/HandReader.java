package view;

import java.nio.FloatBuffer;

import actor.player.Inventory;
import actor.player.Player;
import font.NuFont;
import gui.GUIBase;
import item.ItemHasEnergy;
import item.ItemWeapon;
import item.instances.ItemDepletableInstance;
import item.instances.ItemStack;
import shared.Vec2f;

public class HandReader extends GUIBase {

	Player m_player;
	NuFont[] m_fonts;
	int m_energy;
	int m_max;
	boolean m_energized;
	boolean m_equip;
	ItemDepletableInstance m_item;

	public HandReader(Vec2f p) {
		m_fonts = new NuFont[5];
		for (int i = 0; i < 5; i++) {
			m_fonts[i] = new NuFont(new Vec2f(p.x + 0.5F, p.y + 5.6F - (i * 1.1F) + 0.5F), 256, 0.6F);
			m_fonts[i].setString("text text");
		}
		m_fonts[0].setString("In Hand");
	}

	public void setPlayer(Player player) {
		m_player = player;
	}

	public void UpdateHand() {

		// check item in hand
		if (m_player.getInventory().getSlot(Inventory.HAND) != null) {
			m_equip = true;
			if (m_player.getInventory().getSlot(Inventory.HAND).getShortName()!=null)
			{
				setStrings(m_player.getInventory().getSlot(Inventory.HAND).getItem().getShortName());	
			}
			else
			{
				setStrings(m_player.getInventory().getSlot(Inventory.HAND).getItem().getName());	
			}

			// check for power
			if (ItemDepletableInstance.class.isInstance(m_player.getInventory().getSlot(Inventory.HAND))) {
				m_item = (ItemDepletableInstance) m_player.getInventory().getSlot(Inventory.HAND);
				ItemHasEnergy itemdef = (ItemHasEnergy) m_item.getItem();
				if (itemdef.getEnergy() != null) {
					m_energized = true;
					// read max energy
					m_max = itemdef.getEnergy().getMaxEnergy();

					// trigger updateEnergy
					UpdateEnergy();

				} else {
					m_energized = false;
					m_fonts[3].setString("");
				}
			}
			if (ItemWeapon.class.isInstance(m_player.getInventory().getSlot(Inventory.HAND))) {
				m_energized = false;
				m_fonts[3].setString("");
			}
			if (ItemStack.class.isInstance(m_player.getInventory().getSlot(Inventory.HAND))) {
				ItemStack stack = (ItemStack) m_player.getInventory().getSlot(Inventory.HAND);
				m_fonts[3].setString(Integer.toString(stack.getCount()));
			}
		} else {
			m_energized = false;
			m_item = null;
			m_fonts[3].setString("");
			// nothing
			m_equip = false;
		}

		if (m_player.getInventory().getSlot(Inventory.QUICK) != null) {
			if (m_player.getInventory().getSlot(Inventory.QUICK).getShortName()!=null)
			{
				setQuickString(m_player.getInventory().getSlot(Inventory.QUICK).getShortName());		
			}
			else
			{
				setQuickString(m_player.getInventory().getSlot(Inventory.QUICK).getName());		
			}
		} else {
			m_fonts[4].setString("empty");
		}
	}

	void setQuickString(String string) {
		int l = string.length();
		if (l < 20) {
			m_fonts[4].setString(string);
		} else {
			m_fonts[4].setString(string.substring(0, 20));
		}
	}

	void setStrings(String name) {
		int slot = 1;
		// divide text
		int index = 0;
		int s = index;
		int l = name.length();
		if (name.length() < 20) {
			m_fonts[slot].setString(name);
			slot++;
		} else {
			// write new piecemeal assembler
			index = name.indexOf(" ", (name.length()/2)-1);
			if (index==-1)
			{
				index=name.indexOf(" ",0);
			}
			index++;
			m_fonts[slot].setString(name.substring(0, index));
			slot++;
			m_fonts[slot].setString(name.substring(index, name.length()));
			slot++;
		}
		for (int i = slot; i < 3; i++) {
			m_fonts[slot].setString("");
		}

	}

	int getEnergy() {
		if (m_item != null) {
			return m_item.getEnergy();
		}
		return 0;
	}

	public void UpdateEnergy() {
		if (m_energized == true && getEnergy() != m_energy) {
			// redraw last line
			m_energy = m_item.getEnergy();
			String str = Integer.toString(m_energy) + "/" + Integer.toString(m_max);
			m_fonts[3].setString(str);
		}
	}

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean ClickEvent(Vec2f pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		// TODO Auto-generated method stub
		if (m_equip == true) {
			for (int i = 0; i < 5; i++) {
				m_fonts[i].Draw(buffer, matrixloc);
			}
			if (m_energized == true) {
				m_fonts[3].Draw(buffer, matrixloc);
			}
		} else {
			m_fonts[0].Draw(buffer, matrixloc);
			m_fonts[4].Draw(buffer, matrixloc);
		}

	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		for (int i = 0; i < m_fonts.length; i++) {
			m_fonts[i].Discard();
		}
	}

	@Override
	public void AdjustPos(Vec2f p) {
		// TODO Auto-generated method stub
		for (int i = 0; i < m_fonts.length; i++) {
			m_fonts[i].AdjustPos(p);
		}
	}

}
