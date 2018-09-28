package playerscreens;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import gui.Button;
import gui.ScrollableMultiLineText;
import gui.Text;
import gui.Window;
import gui.lists.List;
import input.MouseHook;
import nomad.universe.Universe;
import perks.Perk;
import perks.PerkLibrary;
import perks.PerkQualifier;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;

public class LevelUpScreen extends Screen implements Callback {

	ArrayList<Perk> availablePerks;
	List perkList;
	Window window;
	ScrollableMultiLineText description;
	int select;
	Callback callback;

	public LevelUpScreen(int[] values, Callback callback) {

		this.callback = callback;
		availablePerks = new ArrayList<Perk>();
		buildPerksList();
		window = new Window(new Vec2f(-20, -16.0F), new Vec2f(20, 32), values[1], true);
		select = -1;
		buildList(values);
		// build select/exit buttons
		Button[] buttons = new Button[2];
		buttons[0] = new Button(new Vec2f(0, 28), new Vec2f(10, 2), values[2], this, "back", 0, 0.8F);
		// add switch button skills
		buttons[1] = new Button(new Vec2f(10, 28), new Vec2f(10, 2), values[2], this, "select", 1, 0.8F);

		for (int i = 0; i < 2; i++) {
			window.add(buttons[i]);
		}

		// build description
		description = new ScrollableMultiLineText(new Vec2f(0.5F, 8.5F), 16, 58, 0.8F,new Vec2f(-1.0F,-10.5F));
		description.addText("text text text");
		window.add(description);
		Window subwindow = new Window(new Vec2f(00, 9.5F), new Vec2f(20, 18.5F), values[1], true);
		window.add(subwindow);
		Text header = new Text(new Vec2f(1.0F, 8.6F), "available perks", 0.8F, values[4]);
		subwindow.add(header);
	}

	private void buildList(int[] values) {
		// 0 is font
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint
		perkList = new List(new Vec2f(-20, -6.3F), 20, values[0], values[4], this, 20, false);

		String[] str = new String[availablePerks.size()];
		for (int i = 0; i < availablePerks.size(); i++) {
			str[i] = availablePerks.get(i).getName();
		}
		perkList.GenList(str);

	}

	private void buildPerksList() {
		PerkQualifier qualifier = new PerkQualifier();
		PerkLibrary instance = PerkLibrary.getInstance();
		for (int i = 0; i < instance.getPerks().size(); i++) {
			if (qualifier.perkAcquireable(PerkLibrary.getInstance().getPerks().get(i))) {
				availablePerks.add(PerkLibrary.getInstance().getPerks().get(i));
			}
		}

	}

	@Override
	public void update(float DT) {

		window.update(DT);
		description.update(DT);
		perkList.update(DT);
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		window.Draw(buffer, matrixloc);
		perkList.Draw(buffer, matrixloc);

	}

	@Override
	public void discard(MouseHook mouse) {

		mouse.Remove(perkList);
		mouse.Remove(window);
		perkList.discard();
		window.discard();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub
		switch (ID) {
		case 0:
			callback.Callback();
			break;
		case 1:
			if (select != -1) {
				Universe.getInstance().getPlayer().levelUp(availablePerks.get(select));
				callback.Callback();
			}

			break;

		}
	}

	@Override
	public void start(MouseHook hook) {

		hook.Register(perkList);
		hook.Register(window);
	}

	@Override
	public void Callback() {

		int d = perkList.getSelect();
		if (d < availablePerks.size()) {
			description.addText(availablePerks.get(d).getDescription());
			select = d;
		}
	}

}
