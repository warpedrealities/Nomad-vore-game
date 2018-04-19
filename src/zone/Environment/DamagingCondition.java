package zone.Environment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import actor.player.Player;
import actorRPG.Actor_RPG;
import nomad.GameOver;
import shared.ParserHelper;
import view.ViewScene;
import vmo.Game;

public class DamagingCondition implements EnvironmentalCondition {
	private boolean active;
	private String identifier;
	private String message;
	private String gameOver;	
	private int damage;

	public DamagingCondition() {
		
	}

	@Override
	public String getIdentity() {
		return identifier;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(0);
		ParserHelper.SaveString(dstream, identifier);
		ParserHelper.SaveString(dstream, message);
		ParserHelper.SaveString(dstream, gameOver);
		dstream.writeInt(damage);
	}

	@Override
	public void load(DataInputStream dstream) throws IOException {
		identifier=ParserHelper.LoadString(dstream);
		message=ParserHelper.LoadString(dstream);
		gameOver=ParserHelper.LoadString(dstream);
		damage=dstream.readInt();
	}

	@Override
	public void run(Player player) {
		player.getRPG().ReduceStat(Actor_RPG.HEALTH, damage);
		ViewScene.m_interface.DrawText(message.replace("PNAME", player.getName()));
		if (player.getRPG().getStat(Actor_RPG.HEALTH)<=0)
		{
			Game.sceneManager.SwapScene(new GameOver(gameOver));
		}
	}

	@Override
	public void modEnvironment(EnvironmentModifiers modifiers) {
	}

	@Override
	public void setActive(boolean active) {
		this.active=active;
	}

	@Override
	public boolean getActive() {
		return active;
	}

}
