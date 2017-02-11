package view;

import item.Item;
import shared.Screen;
import shared.Vec2f;
import widgets.Widget;
import widgets.WidgetConversation;
import actor.NPC;

public class ModelController implements ModelController_Int {

	@Override
	public Vec2f Transition(String destination, int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void Transition(String destination, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void DrawText(String string) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Remove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean Drop(int x, int y, Item item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void RemoveWidget(Widget widget) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ReplaceWidget(Widget widget, Widget replacewith) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Flash(Vec2f p, int type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean BeatPlayer(NPC npc) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void PlayerBeaten(NPC npc, boolean resolve) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void UpdateInfo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void StartConversation(String conversation, NPC npc, boolean BadEnd) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setScreen(Screen screen) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void projectile(Vec2f target, Vec2f origin, int type) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean placeWidget(Widget widget, int x, int y, boolean imprecise) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getLastMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SceneController getSceneController() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void replaceScreen(Screen screen) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void redraw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void StartConversation(String conversation, WidgetConversation widget) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void redrawBars() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void screenFade(float duration) {
		// TODO Auto-generated method stub
		
	}



	
	
	
	
}
