package item;

import item.Item.ItemUse;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import combat.CombatMove;
import combat.effect.Effect_Recover;
import actor.Modifier;
import actor.player.Inventory;

public class ItemEquip extends Item implements ItemHasEnergy {

	ArrayList<CombatMove> moveList;

	int m_slot;
	boolean stackEquip;
	Modifier m_modifier;
	ItemEnergy m_energy;

	public ItemEquip(int uid) {
		super(uid);
	}

	public ItemEquip(Element Inode, int uid) {
		super(uid);
		NodeList children = Inode.getChildNodes();

		m_use = ItemUse.EQUIP;
		m_name = Inode.getAttribute("name");
		m_weight = Float.parseFloat(Inode.getAttribute("weight"));
		itemValue = Float.parseFloat(Inode.getAttribute("value"));

		String slot = Inode.getAttribute("slot");
		if (slot.contains("BODY")) {
			m_slot = Inventory.BODY;
		}
		if (slot.contains("HEAD")) {
			m_slot = Inventory.HEAD;
		}
		if (slot.contains("HAND")) {
			m_slot = Inventory.HAND;
		}
		if (slot.contains("ACCESSORY")) {
			m_slot = Inventory.ACCESSORY;
		}

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				if (Enode.getTagName() == "description") {
					m_description = Enode.getTextContent().replace("\n", "");
				}
				if (Enode.getTagName() == "modifier") {
					m_modifier = new Modifier(Enode);
				}
				if (Enode.getTagName() == "combatMove") {
					if (moveList == null) {
						moveList = new ArrayList<CombatMove>();
					}
					moveList.add(new CombatMove(Enode));
				}
				if (Enode.getTagName() == "energy") {
					m_energy = new ItemEnergy(Enode);
				}
			}
		}

	}

	public int getSlot() {
		return m_slot;
	}

	public Modifier getModifier() {
		return m_modifier;
	}

	@Override
	public ItemEnergy getEnergy() {
		// TODO Auto-generated method stub
		return m_energy;
	}

	public CombatMove getMove(int index) {
		return moveList.get(index);
	}

	public int getMoveCount() {
		if (moveList != null) {
			return moveList.size();
		}
		return 0;
	}

	public boolean isStackEquip() {
		return stackEquip;
	}
	@Override
	public boolean canStack() {
		return false;
	}
}
