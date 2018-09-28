package actor.npc.observerVore;

public interface VoreScript {

	
	
	void update(boolean visible,boolean noEnemies);
	
	void attacked();
	
	boolean blocksAI();
	
	boolean isActive();
}
