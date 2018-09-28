package solarview.spaceEncounter.gui;

import java.nio.FloatBuffer;

import actorRPG.player.Player_RPG;
import gui.Button;
import gui.Text;
import gui.Window;
import input.MouseHook;
import nomad.universe.Universe;
import shared.Callback;
import shared.MyListener;
import shared.Screen;
import shared.Vec2f;
import solarview.spaceEncounter.EncounterLogic.GameState;
import solarview.spaceEncounter.EncounterEntities.EncounterShip;
import solarview.spaceEncounter.EncounterEntities.monitoring.Monitor;
import solarview.spaceEncounter.retreatHandler.RetreatHandler;
import spaceship.ShipController.scriptEvents;

public class Screen_Recap extends Screen implements MyListener {

	private EncounterShip []ships;
	private GameState state;
	private Monitor playerMonitor;
	private int playerHull,expReward;
	private Monitor enemyMonitor;
	private Window window;
	
	public Screen_Recap(GameState state, EncounterShip playerShip, EncounterShip enemyShip) {
		this.state=state;
		
		ships=new EncounterShip[2];
		ships[0]=playerShip;
		ships[1]=enemyShip;
		
		this.playerHull=(int) playerShip.getShip().getShipStats().getResource("HULL").getResourceAmount();
		playerMonitor=playerShip.getMonitor();
		enemyMonitor=enemyShip.getMonitor();
		
		if (state==GameState.victory)
		{
			this.expReward=enemyShip.getShip().getShipController().getExperience();
			//handle player getting the exp reward
			((Player_RPG)Universe.getInstance().getPlayer().getRPG()).addEXP(enemyShip.getShip().getShipController().getExperience());		
		}
	}

	@Override
	public void update(float DT) {
		window.update(DT);
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		window.Draw(buffer, matrixloc);	
	}

	@Override
	public void discard(MouseHook mouse) {
		window.discard();
		mouse.Remove(window);
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		if (ID==0)
		{
			Universe.getInstance().getPlayer().setBusy(0);
			switch(state)
			{
				case victory:
					ships[1].getShip().getShipController().event(scriptEvents.loss);
				break;
				case defeat:
					ships[1].getShip().getShipController().event(scriptEvents.victory);
				break;
				case retreat:
					new RetreatHandler().run(ships[0]);
				break;			
			}
		}
	}

	private float calcHitRate(int numHits, int numMisses)
	{
		float total=numHits+numMisses;
		
		float v=numHits/total;
		if (Float.isNaN(v))
		{
			v=1;
		}
		return v*100;
	}
	
	@Override
	public void initialize(int[] textures, Callback callback) {
		// TODO Auto-generated method stub
		// 0 is font
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint
		window=new Window(new Vec2f(-7, -10), new Vec2f(16,20), textures[1], true);
		
		Text []text=new Text[9];
		switch (state)
		{
		case victory:
			text[0]=new Text(new Vec2f(0.2F,9.0F),"VICTORY",1.0F,0);
			break;
		case defeat:
			text[0]=new Text(new Vec2f(0.2F,9.0F),"DEFEAT",1.0F,0);
			break;
		case retreat:
			text[0]=new Text(new Vec2f(0.2F,9.0F),"RETREATED",1.0F,0);
			break;
		}
		text[1]=new Text(new Vec2f(0.2F,8.0F),"hull points left:"+playerHull,0.8F,0);
		text[2]=new Text(new Vec2f(0.2F,7.0F),"damage suffered:"+playerMonitor.getDamage(),0.8F,0);
		text[3]=new Text(new Vec2f(0.2F,6.0F),"damage absorbed:"+playerMonitor.getShieldDamage(),0.8F,0);
		float hitRatio=calcHitRate(playerMonitor.getHits(),playerMonitor.getMisses());
		text[4]=new Text(new Vec2f(0.2F,5.0F),"hit ratio:"+hitRatio+"%",0.8F,0);
		text[5]=new Text(new Vec2f(0.2F,4.0F),"damage inflicted:"+enemyMonitor.getDamage(),0.8F,0);
		hitRatio=calcHitRate(enemyMonitor.getMisses(),enemyMonitor.getHits());
		text[6]=new Text(new Vec2f(0.2F,3.0F),"shots evaded:"+hitRatio+"%",0.8F,0);
		text[8]=new Text(new Vec2f(0.2F,2.0F),"exp awarded:"+expReward,0.8F,0);
		for (int i=0;i<text.length;i++)
		{
			window.add(text[i]);
		}
		Button button=new Button(new Vec2f(3.5F,0.2F),new Vec2f(6,1.8F),textures[2],this,"conclude",0,1);
				
		window.add(button);
	}
	@Override
	public void start(MouseHook hook) {
		hook.Register(window);
	}

}
