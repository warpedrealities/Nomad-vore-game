package interactionscreens.systemScreen;

import gui.Button;
import gui.Text;
import gui.Window;
import input.MouseHook;
import nomad.universe.Universe;
import shared.Vec2f;
import shipsystem.ShipDispenser;
import spaceship.Spaceship;
import spaceship.SpaceshipResource;
import spaceship.stats.SpaceshipAnalyzer;
import spaceship.stats.SpaceshipStats;

public class DispenserDisplay implements SystemDisplay {

	private Window window;
	private ShipDispenser dispenser;
	private SystemCallback callback;
	private int index;
	private Text text;
	private int count;
	
	
	public DispenserDisplay(int index, ShipDispenser shipDispenser, SystemCallback callback) {
		this.index=index;
		this.dispenser=shipDispenser;
		this.callback=callback;
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		if (ID==25+index)
		{
			if (count>0)
			{
				Universe.getInstance().getPlayer().getInventory().AddItem(Universe.getInstance().getLibrary().getItem(dispenser.getOutputItem()));
				subtractResource();
				count--;
				reset();
				callback.Callback();
			}

		}
	}

	private void subtractResource() {
		Spaceship ship = (Spaceship) Universe.getInstance().getCurrentZone().getZoneEntity();
		SpaceshipAnalyzer analyzer = new SpaceshipAnalyzer();
		SpaceshipStats stats = analyzer.generateStats(ship);
		SpaceshipResource resource=stats.getResource(dispenser.getInput());
		resource.setResourceAmount(resource.getResourceAmount()-dispenser.getCost());
		analyzer.decomposeResources(stats, ship);	
	}

	@Override
	public void update(float dT) {
		window.update(dT);
	}

	@Override
	public void discard(MouseHook mouse) {
		mouse.Remove(window);
		window.discard();
	}

	@Override
	public void initialize(int[] textures) {
		// TODO Auto-generated method stub
		window = new Window(new Vec2f(-20, -14+(index*3)), new Vec2f(23, 3), textures[1], true);
		
		Text exText=new Text(new Vec2f(0.5F, 1.0F),"Dispenser:"+dispenser.getOutputItem(), 1.0F, textures[4]);
		window.add(exText);		
		text=new Text(new Vec2f(0.5F, 0.5F),"", 1.0F, textures[4]);
		window.add(text);
		Button button = new Button(new Vec2f(18.5F, 0.5F), new Vec2f(4, 2.0F), textures[2], this, "dispense", 25+index, 1);
		window.add(button);
		count=getItemCount();	
		reset();
	}

	@Override
	public Window getWindow() {
		return window;
	}

	@Override
	public void reset() {
		text.setString("items:"+count);
		
	}

	private int getItemCount() {
		Spaceship ship = (Spaceship) Universe.getInstance().getCurrentZone().getZoneEntity();
		SpaceshipAnalyzer analyzer = new SpaceshipAnalyzer();
		SpaceshipStats stats = analyzer.generateStats(ship);
		if (stats.getResource(dispenser.getInput())==null)
		{
			return 0;
		}
		float v=stats.getResource(dispenser.getInput()).getResourceAmount();
		analyzer.decomposeResources(stats, ship);
		return (int) (v/dispenser.getCost());
	}

}
