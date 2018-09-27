package actor.npc.observerVore.impl;

import org.w3c.dom.Element;

public class DigestionStage extends StageBase implements ScriptStage {

	public DigestionStage(Element enode) {
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
		return false;
	}

	@Override
	public boolean removeTarget() {
		return false;
	}

}
