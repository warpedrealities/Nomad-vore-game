package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import actor.Player;
import interactionscreens.BedScreen;
import interactionscreens.ComputerScreen;
import view.ViewScene;

public class WidgetComputer extends WidgetBreakable {

	
	
	public WidgetComputer(Element element)
	{
		super(element);
		
	}
	
	@Override
	public
	void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.write(17);
		commonSave(dstream);
		saveBreakable(dstream);
		
	}
	
	public WidgetComputer(DataInputStream dstream) throws IOException {
		// TODO Auto-generated constructor stub
		commonLoad(dstream);
		load(dstream);
		
	}
	
	@Override
	public boolean Interact(Player player)
	{
		ViewScene.m_interface.setScreen(new ComputerScreen());
		
		return true;
	}
}
