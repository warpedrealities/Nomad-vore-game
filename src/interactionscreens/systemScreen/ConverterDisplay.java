package interactionscreens.systemScreen;

import gui.Button;
import gui.Text;
import gui.Window;
import input.MouseHook;
import shared.Callback;
import shared.Vec2f;
import shipsystem.conversionSystem.ShipConverter;

public class ConverterDisplay implements SystemDisplay {
	private Window window;
	private int index;
	private ShipConverter converter;
	private SystemCallback callback;
	private Button button;
	private Text text;
	
	
	public ConverterDisplay(int index, ShipConverter shipConverter , SystemCallback callback) {
		shipConverter.runConversion();
		this.index=index;
		converter=shipConverter;
		this.callback=callback;
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		if (ID==15+index)
		{
			converter.setActive(!converter.isActive());
			reset();
		}
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
		
		Text exText=new Text(new Vec2f(0.5F, 1.0F), buildInputs(), 1.0F, textures[4]);
		window.add(exText);

		text=new Text(new Vec2f(0.5F, 0.5F), "", 1.0F, textures[4]);
		window.add(text);
		
		button = new Button(new Vec2f(19.5F, 0.5F), new Vec2f(3, 2.0F), textures[2], this, "put", 15+index, 1);
		window.add(button);
		reset();
	}

	private String buildInputs() {
		StringBuilder b=new StringBuilder();
		b.append("converting:");
		for (int i=0;i<converter.getProductInfo().getInput().size();i++)
		{
			b.append(converter.getProductInfo().getInput().get(i).getResourceName()+" ");
		}
		b.append("> "+converter.getProductInfo().getOutput());
		
		return b.toString();
	}

	@Override
	public Window getWindow() {
		return window;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		if (converter.isActive())
		{
			text.setString("system online");
			button.setString("off");
		}
		else
		{
			text.setString("system offline");
			button.setString("on");
		}
	}

}
