package zone.Environment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import actor.player.Player;

public interface EnvironmentalCondition {

	String getIdentity();
	
	void save(DataOutputStream dstream) throws IOException;
	
	void load(DataInputStream dstream) throws IOException;
	
	void run(Player player);

	void modEnvironment(EnvironmentModifiers modifiers);
	
	void setActive(boolean active);
	
	boolean getActive();
}
