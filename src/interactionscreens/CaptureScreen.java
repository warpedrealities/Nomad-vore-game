package interactionscreens;

import java.nio.FloatBuffer;

import actor.npc.NPC;
import actor.player.Inventory;
import gui.Button;
import gui.List;
import gui.MultiLineText;
import gui.Window;
import input.MouseHook;
import item.ItemCaptureInstance;
import nomad.Entity;
import nomad.Universe;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;
import spaceship.Spaceship;
import view.ViewScene;
import widgets.WidgetCapture;

public class CaptureScreen extends Screen implements Callback {

	private Callback callback;
	private Window window;
	private WidgetCapture widget;
	private List specimenList;
	private MultiLineText description;

	public CaptureScreen(WidgetCapture widgetCapture) {
		this.widget = widgetCapture;

	}

	@Override
	public void update(float DT) {
		specimenList.update(DT);
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		window.Draw(buffer, matrixloc);
		specimenList.Draw(buffer, matrixloc);
	}

	@Override
	public void discard(MouseHook mouse) {
		mouse.Remove(window);
		mouse.Remove(specimenList);
		window.discard();
		specimenList.discard();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		switch (ID) {
		case 0:
			callback.Callback();
			break;

		case 1:
			interact();
			break;
		case 2:
			flush();
			break;
		case 3:
			synch();
			break;
		}
	}

	private void interact() {
		int index = specimenList.getSelect();
		if (index < widget.getCapacity() && widget.getNPC(index) != null) {
			NPC npc = widget.getNPC(index);
			ViewScene.m_interface.StartConversation(npc.getConversation(NPC.CONVERSATIONCAPTIVE), npc, widget, false);

		}
	}

	private void flush() {
		int index = specimenList.getSelect();
		if (index < widget.getCapacity() && widget.getNPC(index) != null) {
			description.addText("containment chamber:" + index + " has been purged.");
			widget.setNPC(null, index);

			genList();
		}
	}

	private void synch() {
		// synchronize all devices
		String s = Universe.getInstance().getCurrentZone().getName();
		Inventory inventory = Universe.getInstance().getPlayer().getInventory();
		for (int i = 0; i < inventory.getNumItems(); i++) {
			if (ItemCaptureInstance.class.isInstance(inventory.getItem(i))) {
				ItemCaptureInstance ici = (ItemCaptureInstance) inventory.getItem(i);

				if (s.equals(ici.getShip())) {
					description.addText("device already synchronized");
				} else {
					description.addText("new device synchronized to specimen containment");
					ici.setShip(s);
				}
				break;
			}
		}

	}

	@Override
	public void start(MouseHook hook) {
		hook.Register(window);
		hook.Register(specimenList);
	}

	private boolean getCanFlush() {
		Entity e = Universe.getInstance().getCurrentEntity();
		if (e != null && Spaceship.class.isInstance(e)) {
			Spaceship s = (Spaceship) e;
			if (s.getState() != Spaceship.ShipState.LAND) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void initialize(int[] textures, Callback callback) {
		this.callback = callback;
		// 0 is bar
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint

		// build window
		window = new Window(new Vec2f(-20, -16), new Vec2f(40, 15), textures[1], true);
		Button[] buttons = new Button[4];

		buttons[0] = new Button(new Vec2f(34.0F, 0.0F), new Vec2f(6, 1.8F), textures[2], this, "Exit", 0, 1);

		buttons[2] = new Button(new Vec2f(34.0F, 4.0F), new Vec2f(6, 1.8F), textures[2], this, "interact", 1, 1);
		if (getCanFlush()) {
			buttons[1] = new Button(new Vec2f(34.0F, 2.0F), new Vec2f(6, 1.8F), textures[2], this, "Flush", 2, 1);
		}
		buttons[3] = new Button(new Vec2f(34.0F, 6.0F), new Vec2f(6, 1.8F), textures[2], this, "synch", 3, 1);

		for (int i = 0; i < 4; i++) {
			window.add(buttons[i]);
		}

		specimenList = new List(new Vec2f(-20, -14.3F), 16, textures[1], textures[4], this);
		genList();

		description = new MultiLineText(new Vec2f(17.5F, 14.5F), 76, 68, 0.8F);
		// description.addText("evil evil evil evil evil evil evil evil evil
		// evil");
		window.add(description);
	}

	private void genList() {
		String[] specimens = new String[widget.getCapacity()];
		for (int i = 0; i < widget.getCapacity(); i++) {
			if (widget.getNPC(i) != null) {
				specimens[i] = widget.getNPC(i).getName();
			} else {
				specimens[i] = "unoccupied";
			}
		}

		specimenList.GenList(specimens);
	}

	@Override
	public void Callback() {

	}
}
