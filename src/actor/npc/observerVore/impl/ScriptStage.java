package actor.npc.observerVore.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface ScriptStage {

	public int update(boolean visible,boolean noEnemies,int lastDamaged);

	public boolean isInterruptible();

	public boolean removeTarget();

	public boolean blocksAI();

	public void load(DataInputStream dstream) throws IOException;

	public void save(DataOutputStream dstream) throws IOException;

}
