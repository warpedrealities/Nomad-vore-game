package actor.npc.observerVore.impl;

import org.w3c.dom.Element;

public class FinishStage extends StageBase implements ScriptStage {


	public FinishStage(Element enode) {
		build(enode);
	}

	private void updateStart(boolean visible,boolean noEnemies,int lastDamaged)
	{
		if (lastDamaged > 10 && noEnemies)
		{
			progress(visible);
		}
	}

	private void updateContinue(boolean visible,boolean noEnemies)
	{
		progress(visible);
	}
	@Override
	public int update(boolean visible, boolean noEnemies,int lastDamaged) {
		if (index==0)
		{
			updateStart(visible,noEnemies,lastDamaged);
		}
		else
		{
			updateContinue(visible,noEnemies);
		}

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

	@Override
	public boolean blocksAI() {
		return true;
	}
}
