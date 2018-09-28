package actor.npc.observerVore.impl;

public interface ScriptStage {

	public int update(boolean visible,boolean noEnemies,int lastDamaged);
	
	public boolean isInterruptible();
	
	public boolean removeTarget();

}
