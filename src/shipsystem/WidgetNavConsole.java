package shipsystem;

import interactionscreens.NavScreen;
import interactionscreens.WarpScreen;
import interactionscreens.systemScreen.SystemScreen;
import item.Item;
import nomad.universe.Universe;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import actor.player.Player;
import shared.ParserHelper;
import spaceship.Spaceship;
import view.ViewScene;
import widgets.WidgetBreakable;

public class WidgetNavConsole extends WidgetBreakable {

	public WidgetNavConsole(Element node) {
		super(node);
	}

	public WidgetNavConsole(DataInputStream dstream) throws IOException {
		commonLoad(dstream);
		load(dstream);

	}

	@Override
	public boolean Interact(Player player) {
		Spaceship ship=(Spaceship) Universe.getInstance().getCurrentZone().getZoneEntity();
		if (ship.getWarpHandler()!=null && ship.getWarpHandler().getCharge()>=100)
		{
			if (ship.getWarpHandler().flightElapsed())
			{
				ViewScene.m_interface
				.setScreen(new NavScreen(ship));			
			}
			else
			{
				ViewScene.m_interface
				.setScreen(new WarpScreen(ship));			
			}

		}
		else
		{
			ViewScene.m_interface
			.setScreen(new NavScreen(ship));		
		}

		
		return true;

	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.write(9);
		commonSave(dstream);
		super.saveBreakable(dstream);

	}
}
