package solarview;

import shared.Vec2f;

public class MouseConverter {

	
	public static Vec2f convertMousePointer(Vec2f pointer, Vec2f player, float scale)
	{
		pointer.x=pointer.x*scaleAdjustment((int)scale); 
		pointer.y=pointer.y*scaleAdjustment((int)scale);
		pointer.x+=player.x+scaledOffsetX(scale);
		pointer.y+=player.y+scaledOffsetY(scale);
		
		
		return pointer;
	}
	
	static private float scaleAdjustment(int scale)
	{
		switch (scale)
		{
		case 1:
			return 2.0F;
			
		case 2:
			return 1.0F;
		
		case 4:
			return 0.5F;
		case 8:
			return 0.25F;
		}
		
		return scale;
	}
	
	static private float scaledOffsetX(float scale)
	{
		switch((int)scale)
		{
		case 1:
			return 13.0F;
			
		case 2:
			return 6.5F;
		case 4:
			return 3.1F;
		case 8:
			return 1.5F;
		}
		return 0;
		
	}
	static private float scaledOffsetY(float scale)
	{
		switch((int)scale)
		{
		case 1:
			return -4.0F;
			
		case 2:
			return -2.0F;
		case 4:
			return -1.0F;
		case 8:
			return -0.5F;
		}
		return 0;
		
	}	
}
