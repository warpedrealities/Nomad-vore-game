package interactionscreens.systemScreen;

import gui.Button;
import gui.Text;
import gui.Window;
import input.MouseHook;
import item.Item;
import item.ItemEnergy;
import item.ItemHasEnergy;
import item.instances.ItemDepletableInstance;
import nomad.universe.Universe;
import shared.Callback;
import shared.ProportionBar;
import shared.Vec2f;
import shipsystem.ShipResource;
import shipsystem.resourceConversion.ResourceConversionHandler;

public class ResourceDisplay implements SystemDisplay {
	private Window window;
	private ShipResource resource;
	private SystemCallback callback;
	private int index;
	private ProportionBar resourceBar;
	private Text resourceText;
	public ResourceDisplay(int index, ShipResource shipResource, SystemCallback callback) {
		this.resource=shipResource;
		this.callback=callback;
		this.index=index;
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		if (ID==(index+5))
		{
			if (tryThis(callback.getItem()))
			{
				reset();
				callback.Callback();
			}
		}
	}
	
	private boolean tryThis(Item item) {
	// check if item is a conversion
	ResourceConversionHandler handler=ResourceConversionHandler.getInstance();
	if (handler.canConvert(resource.getContainsWhat(), item.getItem().getName()))
	{
		if (resource.getAmountContained()>=resource.getContainedCapacity())
		{
			return false;
		}
		if (ItemDepletableInstance.class.isInstance(item)) {
			ItemDepletableInstance idi = (ItemDepletableInstance) item;
			ItemHasEnergy ihe = (ItemHasEnergy) idi.getItem();

			float ratio = (float) idi.getEnergy() / ihe.getEnergy().getMaxEnergy();
			resource.addResource(((float)handler.conversionValue(resource.getContainsWhat(), item.getItem().getName())) * ratio);
		} else {
			resource.addResource(handler.conversionValue(resource.getContainsWhat(), item.getItem().getName()));
		}	
		Universe.getInstance().getPlayer().getInventory().RemoveItem(item);
		return true;
	}

	// check if resource is energy and the item is a rechargeable
	if (resource.getContainsWhat().equals("ENERGY") && ItemDepletableInstance.class.isInstance(item)) {
		ItemDepletableInstance it = (ItemDepletableInstance) item;
		int energy = it.getEnergy();
		ItemEnergy ie = ((ItemHasEnergy) it.getItem()).getEnergy();
		if (ie != null && ie.getRefill() != null && ie.getRefill().contains("ENERGY")
				&& energy < ie.getMaxEnergy()) {
			int missing = ie.getMaxEnergy() - energy;
			// get amount of energy required to completely recharge item
			float required = ((float) missing) / ie.getrefillrate();
			// if resource energy equal or greater, fully recharge and
			// subtract
			if (resource.getAmountContained() >= required) {
				it.setEnergy(ie.getMaxEnergy());
				resource.setAmountContained(resource.getAmountContained() - required);
				return true;
			}
			// if not, recharge partially
			else if (resource.getAmountContained() > 0) {
				float amount = resource.getAmountContained() * ie.getrefillrate();
				it.setEnergy(it.getEnergy() + amount);

				resource.setAmountContained(0);
				return true;
			}
		}
	}

	return false;
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
		window = new Window(new Vec2f(-20, -14+(index*3)), new Vec2f(23, 3), textures[1], true);
		
		resourceText=new Text(new Vec2f(0.5F, 0.5F), resource.getContainsWhat(), 1.0F, textures[4]);
		window.add(resourceText);
		resourceBar= new ProportionBar(new Vec2f(8.5F, 1.5F), new Vec2f(16, 1), 40, 40, 5, textures[0]);
		reset();
		window.add(resourceBar);
		
		Button button = new Button(new Vec2f(19.5F, 0.5F), new Vec2f(3, 2.0F), textures[2], this, "put", 5+index, 1);
		window.add(button);
	}

	@Override
	public Window getWindow() {
		return window;
	}

	@Override
	public void reset() {
		resourceBar.setMax(this.resource.getContainedCapacity());
		resourceBar.setValue((int) this.resource.getAmountContained());
		resourceText.setString(resource.getContainsWhat()+" "+((int)resource.getAmountContained())+"/"+resource.getContainedCapacity());
	}

}
