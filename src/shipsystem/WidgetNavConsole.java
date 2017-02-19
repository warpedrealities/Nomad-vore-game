package shipsystem;

import interactionscreens.NavScreen;
import interactionscreens.SystemScreen;
import item.Item;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import nomad.Universe;

import org.w3c.dom.Element;

import shared.ParserHelper;
import spaceship.Spaceship;

import actor.Player;
import view.ViewScene;
import widgets.WidgetBreakable;

public class WidgetNavConsole extends WidgetBreakable {

	public WidgetNavConsole(Element node)
	{
		super(node);
	}
	
	public WidgetNavConsole(DataInputStream dstream) throws IOException {
		commonLoad(dstream);
		load(dstream);
		
	}

	@Override
	public boolean Interact(Player player)
	{
		ViewScene.m_interface.setScreen(new NavScreen((Spaceship)Universe.getInstance().getCurrentZone().getZoneEntity()));
		return true;
		
	}
	@Override
	public
	void save(DataOutputStream dstream) throws IOException {
		dstream.write(9);
		commonSave(dstream);
		super.saveBreakable(dstream);
		
	}
}
