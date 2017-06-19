package view;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import actor.player.Player;
import actorRPG.Player_RPG;
import combat.CooldownHandler;
import gui.GUIBase;
import nomad.Universe;
import rendering.Sprite;
import shared.Tools;
import shared.Vec2f;

public class CooldownDisplay extends GUIBase {

	int textureId;
	Sprite [] sprites;
	Vec2f position;
	int minInterval;
	int clock;
	public CooldownDisplay(Vec2f p)
	{
		//load texture
		textureId=Tools.loadPNGTexture("assets/art/cooldowns.png", GL13.GL_TEXTURE0);
		//load sprites
		sprites=new Sprite[16];
		for (int i=0;i<8;i++)
		{
			sprites[i]=new Sprite (new Vec2f(0,0), 2,64);
			sprites[i+8]=new Sprite (new Vec2f(0,0), 1.5F,64);
		}
		position=p;
	}

	
	private void updateRenderer()
	{
		Player_RPG player=(Player_RPG)Universe.getInstance().getPlayer().getRPG();
		CooldownHandler handler=player.getCooldownHandler();
		int index=0;
		int interval=60;
		for (int i=0;i<handler.getCount();i++)
		{
			if (handler.getCooldown(i)!=null && handler.getCooldown(i).isVisible())
			{
				if (handler.getCooldown(i).getCooldown()>0 && handler.getCooldown(i).getCoolmax()/16<interval)
				{
					interval=handler.getCooldown(i).getCoolmax()/16;
				}
				if (handler.getCooldown(i).getCooldown()>0)
				{
					int proportion=calcProportion(((float)handler.getCooldown(i).getCooldown())/((float)handler.getCooldown(i).getCoolmax()));
					proportion=8-proportion;
					if (proportion>7)
					{
						proportion=7;
					}
					sprites[index].setVisible(true);
					sprites[index].setImage(proportion);
					if (sprites[index+8].getVisible()==false)
					{
						//calculate icon
						int sprite=handler.getCooldown(i).getIcon()+8;
						sprites[index+8].setImage(sprite);
						sprites[index+8].setVisible(true);
					}				
					index++;
				}			
			}
		}
		
		if (index<8)
		{
			for (int i=index;i<8;i++)
			{
				sprites[i].setVisible(false);
				sprites[i+8].setVisible(false);
			}
		}
		minInterval=interval;
	}
	
	private int calcProportion(float value)
	{
		return (int)(value*8);
	}
	
	@Override
	public void update(float DT) {
		clock++;
		if (clock>minInterval)
		{
			clock=0;
			updateRenderer();
		}
		
	}
	
	public void redraw()
	{
		updateRenderer();
	}

	@Override
	public boolean ClickEvent(Vec2f pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		// TODO Auto-generated method stub
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		for (int i=0;i<16;i++)
		{
			if (sprites[i].getVisible())
			{
				sprites[i].draw(matrixloc,0 , buffer);			
			}

		}
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		GL11.glDeleteTextures(textureId);
		for (int i=0;i<16;i++)
		{
			sprites[i].discard();
		}
	}

	@Override
	public void AdjustPos(Vec2f p) {
		// TODO Auto-generated method stub
		for (int i=0;i<8;i++)
		{
			sprites[i].reposition(new Vec2f(position.x+p.x+(i*2),position.y+p.y));
			sprites[i+8].reposition(new Vec2f(position.x+p.x+(i*2),position.y+p.y));
		}
	}

}
