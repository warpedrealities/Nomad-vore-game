package shipsystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import actor.player.Player;
import actorRPG.Actor_RPG;
import view.ViewScene;
import vmo.GameManager;
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

	private void repair(Player player)
	{
		int max=player.getAttribute(Actor_RPG.TECH);
		int r=1;
		if (max>0)
		{
			r+=GameManager.m_random.nextInt(max);
		}
		if (r>damageValue)
		{
			r=damageValue;
		}
		damageValue-=r;

		ViewScene.m_interface.DrawText("conducting hull repairs, 2 scrap used to recover"+r+" hull points");	
		if (damageValue<=0)
		{
			ViewScene.m_interface.RemoveWidget(this);
		}	
	}
	
	public boolean Interact(Player player)
	{
		if (player.getInventory().countItem("scrap metal")>=2)
		{
			repair(player);
			player.getInventory().removeItems("scrap metal", 2);
			player.setBusy(20);
		}
		else
		{
			ViewScene.m_interface.DrawText("need at least 2 scrap metal to perform hull repairs");
		}
		return false;
	}
	
}
