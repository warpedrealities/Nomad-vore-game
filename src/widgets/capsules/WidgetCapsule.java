package widgets.capsules;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import actor.player.Player;
import shared.ParserHelper;
import widgets.WidgetBreakable;

public class WidgetCapsule extends WidgetBreakable {

	private String spaceship;
	
	public WidgetCapsule(Element element)
	{
		super(element);
	}
	
	public WidgetCapsule(DataInputStream dstream) throws IOException {
		spaceship=ParserHelper.LoadString(dstream);
		commonLoad(dstream);
		load(dstream);
	}
	
	@Override
	public void save(DataOutputStream dstream) throws IOException {

		dstream.write(24);
		ParserHelper.SaveString(dstream, spaceship);
		commonSave(dstream);
		saveBreakable(dstream);
	}

	public String getSpaceship() {
		return spaceship;
	}

	public void setSpaceship(String spaceship) {
		this.spaceship = spaceship;
	}

	public boolean Interact(Player player) {
		new CapsuleRetrievalBehaviour(this).retrieve();
		return false;
	}
}
