package shop.mutator;

import java.io.DataOutputStream;
import java.io.IOException;

import actor.player.Player;

public interface MutationItem {

	int getCost();

	void apply(Player player);

	void save(DataOutputStream dstream) throws IOException;

	String getName();
}
