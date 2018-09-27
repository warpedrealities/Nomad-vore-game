package actor.npc.observerVore.impl;

import org.w3c.dom.Element;

public class InsertionStage extends StageBase implements ScriptStage {

	public InsertionStage(Element enode) {
		build(enode);
	}

	@Override
	public int update(boolean visible, boolean noEnemies,int lastDamaged) {
		progress(visible);
		if (index>=size)
		{
			return 1;
		}
		return 0;
	}

	@Override
	public boolean isInterruptible() {
		return true;
	}

	@Override
	public boolean removeTarget() {
		return true;
	}

}
