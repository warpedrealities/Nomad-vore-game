package shop.slaveTrader;

import actor.npc.NPC;

public class SlaveStockItem extends SlaveLineItem{

	private NPC npc;
	public SlaveStockItem(NPC npc, int value)
	{
		this.npc=npc;
		this.value=value;
		this.slaveName=npc.getName();
	}
	
	public NPC getNPC()
	{
		return npc;
	}
}
