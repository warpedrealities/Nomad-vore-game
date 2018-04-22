package zone.Environment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.xml.soap.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
		active=false;
	}

	public DamagingCondition(Element e) {
		active=false;
		identifier=e.getAttribute("ID");
		damage=Integer.parseInt(e.getAttribute("damage"));
		NodeList children=e.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
			if (children.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element element=(Element)children.item(i);
				if (element.getTagName().equals("message"))
				{
					message=element.getTextContent();
				}
				if (element.getTagName().equals("gameOver"))
				{
					gameOver=element.getTextContent();
				}
			}
		}
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

	@Override
	public boolean getDangerous() {
		return true;
	}

}
