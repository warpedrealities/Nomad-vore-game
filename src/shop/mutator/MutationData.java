package shop.mutator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import actor.player.Player;
import mutation.Effect_Mutator;

public class MutationData implements MutationItem {

	Effect_Mutator mutator;
	String name;
	int cost;

	public MutationData(Element enode) {
		cost = Integer.parseInt(enode.getAttribute("cost"));
		name = enode.getAttribute("name");
		mutator = new Effect_Mutator(enode);
	}

	public MutationData(DataInputStream dstream) throws IOException {
		// TODO Auto-generated constructor stub
		cost = dstream.readInt();
	}

	@Override
	public int getCost() {
		// TODO Auto-generated method stub
		return cost;
	}

	@Override
	public void apply(Player player) {
		mutator.applyEffect(player, player, false);
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
	}

	@Override
	public String getName() {
		return name;
	}

}
