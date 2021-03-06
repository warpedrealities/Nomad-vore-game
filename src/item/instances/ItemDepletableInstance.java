package item.instances;

import java.io.DataOutputStream;
import java.io.IOException;

import item.Item;
import item.ItemAmmo;
import item.ItemHasEnergy;
import shared.ParserHelper;

public class ItemDepletableInstance extends Item {

	Item m_item;
	ItemHasEnergy m_itemenergy;
	float m_energy;

	public ItemDepletableInstance(Item item) {
		super(item.getUID());
		m_item = item;
		m_itemenergy = (ItemHasEnergy) m_item;
		if (m_itemenergy.getEnergy() != null) {
			m_energy = m_itemenergy.getEnergy().getMaxEnergy();
		}
	}

	@Override
	public ItemUse getUse() {
		return m_item.getUse();
	}

	@Override
	public String getName() {
		return m_item.getName();
	}

	@Override
	public String getDescription() {
		if (m_itemenergy.getEnergy() != null) {
			return m_item.getDescription() + " " + Integer.toString((int) m_energy) + "/"
					+ m_itemenergy.getEnergy().getMaxEnergy();
		}
		return m_item.getDescription();
	}

	@Override
	public float getWeight() {
		return m_item.getWeight();
	}

	public int getEnergy() {
		return (int) m_energy;
	}

	public void setEnergy(float energy) {
		m_energy = energy;
	}

	@Override
	public Item getItem() {
		return m_item;
	}

	public void UseEnergy(int amount) {
		m_energy -= amount;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.write(1);
		ParserHelper.SaveString(dstream, getName());

		dstream.writeFloat(m_energy);
	}

	@Override
	public float getItemValue() {
		return m_item.getItemValue();
	}
	
	public boolean canStack()
	{
		if (m_energy==m_itemenergy.getEnergy().getMaxEnergy())
		{
			if (m_itemenergy.getEnergy().getMaxEnergy()==1||ItemAmmo.class.isInstance(m_item))
			{
				return true;
			}
		}
		return false;
	}
}
