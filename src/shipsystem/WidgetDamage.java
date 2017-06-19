package shipsystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import actor.player.Player;
import widgets.Widget;

public class WidgetDamage extends Widget {

	private int damageValue;
	
	public WidgetDamage(int value)
	{
		damageValue=value;
		isWalkable=true;
		isVisionBlocking=false;
		widgetSpriteNumber=24;
		widgetDescription="A rent in the interior caused by hull damage to the ship. represents "+damageValue+" hull damage";	
		
	}
	
	public WidgetDamage(DataInputStream dstream) throws IOException
	{
		damageValue=dstream.readInt();
		isWalkable=true;
		isVisionBlocking=false;
		widgetSpriteNumber=24;
		
		damageValue=dstream.readInt();
		widgetDescription="A rent in the interior caused by hull damage to the ship. represents"+damageValue+" hull damage";	
	}
	
	@Override
	public void save(DataOutputStream dstream) throws IOException {

		dstream.write(10);
		dstream.writeInt(damageValue);
	}

	public int getDamageValue() {
		return damageValue;
	}

	public void setDamageValue(int damageValue) {
		this.damageValue = damageValue;
		widgetDescription="A rent in the interior caused by hull damage to the ship. represents"+damageValue+" hull damage";	
	}

	public boolean Interact(Player player)
	{
		
		
		return false;
	}
	
}
