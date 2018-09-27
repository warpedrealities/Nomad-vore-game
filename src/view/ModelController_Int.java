package view;

import actor.npc.NPC;
import graphics.AnimatedFXControl;
import item.Item;
import shared.Screen;
import shared.Vec2f;
import shop.merchant.ShopMerchantScreen;
import widgets.Widget;
import widgets.WidgetCapture;
import widgets.WidgetConversation;

public interface ModelController_Int {

	public Vec2f Transition(String destination, int id);

	public void Transition(String destination, int x, int y);

	public void DrawText(String string);

	public String getLastMessage();

	public void Remove();

	public boolean Drop(int x, int y, Item item);

	public void RemoveWidget(Widget widget);

	public void ReplaceWidget(Widget widget, Widget replacewith);

	public void Flash(Vec2f p, int type);

	public void projectile(Vec2f target, Vec2f origin, int type);

	public void PlayerBeaten(NPC npc, boolean resolve);

	public void UpdateInfo();

	void StartConversation(String conversation, NPC npc, boolean BadEnd);

	public void StartConversation(String conversation, NPC npc, Widget widget, boolean badEnd);

	void setScreen(Screen screen);

	public boolean placeWidget(Widget widget, int x, int y, boolean imprecise);

	public SceneController getSceneController();

	public void replaceScreen(Screen screen);

	public void redraw();

	public void redrawBars();

	void StartConversation(String conversation, WidgetConversation widget);

	void screenFade(float duration);

	NPC createNPC(String file, Vec2f position, boolean temp);

	AnimatedFXControl getFX();

}
