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
	private boolean exterior;
	
	public WidgetDamage(int value, boolean exterior)
	{
		damageValue=value;
		isWalkable=true;
		isVisionBlocking=false;
		this.exterior=exterior;
		if (exterior)
		{
			widgetSpriteNumber=15;
			widgetDescription="A rent in the interior caused by hull damage to the ship. represents "+damageValue+" hull damage";	

		}
		else
		{
			widgetSpriteNumber=24;
			widgetDescription="A rent in the interior caused by hull damage to the ship. represents "+damageValue+" hull damage";	
					
		}

	}
	
	public WidgetDamage(DataInputStream dstream) throws IOException
	{
		isWalkable=true;
		isVisionBlocking=false;

		
		damageValue=dstream.readInt();
		exterior=dstream.readBoolean();
		if (exterior)
		{
			widgetSpriteNumber=15;
			widgetDescription="A rent in the interior caused by hull damage to the ship. represents "+damageValue+" hull damage";	

		}
		else
		{
			widgetDescription="interior damage to the ship systems. represents "+damageValue+" hull damage";	
			widgetSpriteNumber=24;
		}
	}
	
	@Override
	public void save(DataOutputStream dstream) throws IOException {

		dstream.write(10);
		dstream.writeInt(damageValue);
		dstream.writeBoolean(exterior);
	}

	public int getDamageValue() {
		return damageValue;
	}

	public void setDamageValue(int damageValue) {
		this.damageValue = damageValue;
		if (exterior) {
			widgetDescription="A rent in the interior caused by hull damage to the ship. represents "+damageValue+" hull damage";	
		}
		else 
		{
			widgetDescription="interior damage to the ship systems. represents "+damageValue+" hull damage";	
			 
		}
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
		setDamageValue(damageValue-r);

		if (exterior) {
			ViewScene.m_interface.DrawText("conducting hull repairs, 1 repair kit used to recover "+r+" hull points");	
		}
		else
		{
			ViewScene.m_interface.DrawText("conducting system repairs, 2 scrap used to recover "+r+" hull points");		
		}
	
		if (damageValue<=0)
		{
			ViewScene.m_interface.RemoveWidget(this);
		}	
	}
	
	public boolean Interact(Player player)
	{
		if (exterior) {
			if (player.getInventory().countItem("repair kit")>=1)
			{
				repair(player);
				player.getInventory().removeItems("repair kit", 2);
				player.setBusy(20);
			}
			else
			{
				ViewScene.m_interface.DrawText("need at least 1 repair kit to repair hull damage");
			}		
		}
		else
		{
			if (player.getInventory().countItem("scrap metal")>=2)
			{
				repair(player);
				player.getInventory().removeItems("scrap metal", 2);
				player.setBusy(20);
			}
			else
			{
				ViewScene.m_interface.DrawText("need at least 2 scrap metal to perform system repairs");
			}		
		}

		return false;
	}

	public boolean isExterior() {
		return exterior;
	}
	
}
