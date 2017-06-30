package solarview.spaceEncounter;

import gui.Text;
import gui.TextColoured;
import input.MouseHook;
import shared.Vec2f;
import solarview.spaceEncounter.EncounterEntities.EncounterShip;
import solarview.spaceEncounter.rendering.Targeting;

public class TargetingControls {

	private Targeting targeting;
	private TextColoured text;
	private EncounterShip playerShip;
	private EncounterShip []ships;
	private int index;
	public TargetingControls(TextColoured text, EncounterShip player, Targeting targeting,EncounterShip []ships)
	{
		this.text=text;
		this.targeting=targeting;
		playerShip=player;
		this.ships=ships;
		index=-1;
	}
	
	public void Recalc(int i)
	{
		if (i>0)
		{
			targeting.setPosition(ships[i].getPosition());	
			float x=ships[i].getPosition().getDistance(playerShip.getPosition());
			x=x-(x%0.1F);
			text.setString(x+" megameters");		
		}
		else
		{
			text.setString("");
		}

	}
	
	public int getIndex()
	{
		return index;
	}
	
	public void update (float dt)
	{
		if (MouseHook.getInstance().click())
		{
			Vec2f p=MouseHook.getInstance().getPosition();

			//wall off screen edges so you can't click through the ui
			if (p.x>-14 && p.x<14)
			{
				//calculate game space position of click	
				p=p.replicate();
				p.x+=playerShip.getPosition().x;
				p.y+=playerShip.getPosition().y;		
				//move targeter there
				for (int i=1;i<ships.length;i++)
				{
					if (p.getDistance(ships[i].getPosition())<1)
					{
						if (index!=i)
						{
							Recalc(i);
						}
						index=i;
						break;
					}
				}
			}
		}
		if (index!=-1)
		{
			targeting.setVisible(true);

		}
		else
		{
			targeting.setVisible(false);
		}
	}
}
