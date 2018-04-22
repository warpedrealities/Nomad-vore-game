package zone.Environment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import actor.player.Player;
import shared.ParserHelper;

public class ModifierCondition implements EnvironmentalCondition {

	private boolean active;
	private float movementModifier=1;
	private float visionModifier=1;
	private String identifier;
	
	public ModifierCondition() {

	}

	public ModifierCondition(Element element)
	{
		identifier=element.getAttribute("ID");
		if (element.getAttribute("vision").length()>0)
		{
			visionModifier=Float.parseFloat(element.getAttribute("vision"));
		}
		if (element.getAttribute("movement").length()>0)
		{
			movementModifier=Float.parseFloat(element.getAttribute("movement"));
		}		
	}
	
	@Override
	public String getIdentity() {
		return identifier;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(1);
		ParserHelper.SaveString(dstream, identifier);
		dstream.writeFloat(movementModifier);
		dstream.writeFloat(visionModifier);
	}

	@Override
	public void load(DataInputStream dstream) throws IOException {
		identifier=ParserHelper.LoadString(dstream);
		movementModifier=dstream.readFloat();
		visionModifier=dstream.readFloat();
	}

	@Override
	public void run(Player player) {

	}

	@Override
	public void modEnvironment(EnvironmentModifiers modifiers) {
		modifiers.movementModifier*=movementModifier;
		modifiers.visionModifier*=visionModifier;
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
		return false;
	}

}
