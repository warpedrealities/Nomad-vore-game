package shop.services;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import entities.stations.Station;
import gui.Button;
import gui.Text;
import gui.Window;
import input.MouseHook;
import nomad.universe.Universe;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;
import spaceship.Spaceship;
import spaceship.stats.SpaceshipAnalyzer;

public class ServicesScreen extends Screen {

	private ShopServices services;
	private Window topWindow;
	private Spaceship ship;
	private Callback callback;
	private Text [] descriptionLines;
	private gui.lists.List uiList;
	private List<ServiceHandler> serviceHandlerList;

	public ServicesScreen(ShopServices services)
	{
		this.services=services;
		serviceHandlerList=new ArrayList<ServiceHandler>();
		Universe universe=Universe.getInstance();
		if (Station.class.isInstance(universe.getCurrentEntity())) {
			ship = ((Station) universe.getCurrentEntity()).getDocked().getDockingPorts()[0].getDockedShip();
		}
		if (Spaceship.class.isInstance(universe.getCurrentEntity())) {
			ship = (Spaceship) universe.getCurrentEntity();
		}
		ship.setShipStats(new SpaceshipAnalyzer().generateStats(ship));
	}

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub
		topWindow.update(DT);
		uiList.update(DT);
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		// TODO Auto-generated method stub
		topWindow.Draw(buffer, matrixloc);
		uiList.Draw(buffer, matrixloc);
	}

	@Override
	public void discard(MouseHook mouse) {
		// TODO Auto-generated method stub
		mouse.Remove(topWindow);
		mouse.Remove(uiList);
		topWindow.discard();
		uiList.discard();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub
		switch (ID) {

		case 2:
			purchase();
			break;

		case 3:
			new SpaceshipAnalyzer().decomposeResources(ship.getShipStats(),ship);
			callback.Callback();
			break;

		}
	}

	private void purchase()
	{
		ServiceHandler handler=serviceHandlerList.get(uiList.getSelect());
		if (handler!=null)
		{
			if (Universe.getInstance().getPlayer().getInventory().getPlayerCredits()<handler.getCost())
			{
				return;
			}
			Universe.getInstance().getPlayer().getInventory().setPlayerCredits(
					Universe.getInstance().getPlayer().getInventory().getPlayerCredits()-
					handler.getCost());

			switch (handler.getService().getType())
			{
			case refuel:
				ship.getShipStats().getResource("FUEL").setResourceAmount(
						ship.getShipStats().getResource("FUEL").getResourceAmount()+handler.getAmount());
				break;
			case repair:
				ship.getShipStats().getResource("HULL").setResourceAmount(
						ship.getShipStats().getResource("HULL").getResourceAmount()+handler.getAmount());
				break;
			}

			resetDescription();
			resetList();
		}
	}

	@Override
	public void start(MouseHook hook) {
		// TODO Auto-generated method stub
		hook.Register(topWindow);
		hook.Register(uiList);
	}

	private void resetDescription()
	{
		int v=(int)ship.getShipStats().getResource("HULL").getResourceAmount();
		descriptionLines[0].setString("hull:"+v+"/"+
				ship.getShipStats().getResource("HULL").getResourceCap());
		v=(int)ship.getShipStats().getResource("FUEL").getResourceAmount();
		descriptionLines[1].setString("fuel:"+v+"/"+
				ship.getShipStats().getResource("FUEL").getResourceCap());
		descriptionLines[2].setString("player credits:"+
				Universe.getInstance().getPlayer().getInventory().getPlayerCredits());
	}

	private void resetList()
	{
		serviceHandlerList.clear();
		for (int i=0;i<services.getCount();i++)
		{
			addServices(services.getService(i));
		}
		String []str=new String[serviceHandlerList.size()];
		for (int i=0;i<serviceHandlerList.size();i++)
		{
			str[i]=serviceHandlerList.get(i).getPrompt();
		}
		uiList.GenList(str);
	}

	private void addServices(ShipService service)
	{
		String stat=null;
		String prompt=null;
		switch (service.getType())
		{
		case refuel:
			stat="FUEL";
			prompt="buy $ fuel for £ credits";
			break;

		case repair:
			stat="HULL";
			prompt="repair $ hull for £ credits";
			break;
		}
		if (ship.getShipStats().getResource(stat).getResourceAmount()
				<
				ship.getShipStats().getResource(stat).getResourceCap())
		{

			int difference=(int) (ship.getShipStats().getResource(stat).getResourceCap()-
					ship.getShipStats().getResource(stat).getResourceAmount());

			serviceHandlerList.add(new ServiceHandler(difference,service,prompt));

			if (ship.getShipStats().getResource(stat).getResourceAmount()
					<
					ship.getShipStats().getResource(stat).getResourceCap()-10)
			{
				serviceHandlerList.add(new ServiceHandler(10,service,prompt));
			}
		}
	}

	@Override
	public void initialize(int[] textures, Callback callback) {
		// TODO Auto-generated method stub
		// 0 is bar
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint
		this.callback = callback;
		topWindow = new Window(new Vec2f(3, -1), new Vec2f(17, 17), textures[1], true);
		Button button = new Button(new Vec2f(0.0F, 0.0F), new Vec2f(6, 1.8F), textures[2], this, "Exit", 3, 1);
		topWindow.add(button);
		button=new Button(new Vec2f(6.0F, 0.0F), new Vec2f(6, 1.8F), textures[2], this, "buy", 2, 1);
		topWindow.add(button);
		descriptionLines=new Text[3];

		for (int i=0;i<3;i++)
		{
			descriptionLines[i]=new Text(new Vec2f(0.2F, 6.2F-(1.8F*i)), "money", 0.7F, textures[4]);
			topWindow.add(descriptionLines[i]);
		}

		uiList= new gui.lists.List(new Vec2f(3, -15.2F), 17, textures[5], textures[4], null);
		resetDescription();
		resetList();
	}
}
