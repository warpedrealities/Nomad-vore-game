package shop.mutator;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import actor.player.Player;
import description.AppearanceParser;
import gui.Button;
import gui.Text;
import gui.TextParagrapher;
import gui.Window;
import gui.lists.List;
import input.MouseHook;
import nomad.universe.Universe;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;

public class MutatorStore extends Screen {

	ShopMutator store;
	Player player;
	Callback callback;
	TextParagrapher description;
	List list;
	Window window;
	Window lowerWindow;
	Text money;

	public MutatorStore(ShopMutator shopMutator) {
		store = shopMutator;
		player = Universe.getInstance().getPlayer();
	}

	@Override
	public void update(float DT) {
		window.update(DT);
		list.update(DT);
		lowerWindow.update(DT);
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		window.Draw(buffer, matrixloc);
		lowerWindow.Draw(buffer, matrixloc);
		list.Draw(buffer, matrixloc);
	}

	@Override
	public void discard(MouseHook mouse) {
		mouse.Remove(list);
		mouse.Remove(window);
		mouse.Remove(lowerWindow);
		list.discard();
		window.discard();
		lowerWindow.discard();

	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub
		switch (ID) {
		case 0:
			callback.Callback();
			break;
		case 1:
			buy();
			break;
		}
	}

	private void buy() {
		if (canBuy()) {
			store.getMutations().get(list.getSelect()).apply(player);
			spendMoney();
			reset();
		}
	}

	private void spendMoney() {
		int amount = store.getMutations().get(list.getSelect()).getCost();
		if (store.isUseCredits()) {
			player.getInventory().setPlayerCredits(player.getInventory().getPlayerCredits() - amount);
		} else {
			player.getInventory().setPlayerGold(player.getInventory().getPlayerGold() - amount);

		}
	}

	private boolean canBuy() {
		if (list.getSelect() < store.getMutations().size()) {
			if (store.isUseCredits() && store.getMutations().get(list.getSelect()).getCost() <= player.getInventory()
					.getPlayerCredits()) {
				return true;
			}
			else if (store.getMutations().get(list.getSelect()).getCost() <= player.getInventory().getPlayerGold())
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void start(MouseHook hook) {
		hook.Register(list);
		hook.Register(window);
		hook.Register(lowerWindow);
	}

	@Override
	public void initialize(int[] textures, Callback callback) {
		// TODO Auto-generated method stub
		// 0 is bar
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint
		this.callback = callback;
		// build window
		window = new Window(new Vec2f(-20, -1.0F), new Vec2f(40, 17), textures[1], true);
		// build button to return

		list = new gui.lists.List(new Vec2f(3, -15.2F), 17, textures[5], textures[4], null);

		// build text paragrapher
		description = new TextParagrapher(new Vec2f(0.5F, 16.5F), 32, 160, 0.7F);
		window.add(description);

		lowerWindow = new Window(new Vec2f(-20, -16), new Vec2f(23, 15), textures[1], true);
		Button button = new Button(new Vec2f(0.5F, 0.5F), new Vec2f(8, 2), textures[2], this, "exit", 0, 0.8F);
		;
		lowerWindow.add(button);
		button = new Button(new Vec2f(0.5F, 2.5F), new Vec2f(8, 2), textures[2], this, "buy mod", 1, 0.8F);
		;
		lowerWindow.add(button);
		money = new Text(new Vec2f(0.2F, 7.2F), "money", 1.0F, textures[4]);
		lowerWindow.add(money);
		reset();
		buildList();
	}

	private void buildList() {

		String strings[] = new String[store.getMutations().size()];
		for (int i = 0; i < store.getMutations().size(); i++) {
			strings[i] = store.getMutations().get(i).getName() + " " + store.getMutations().get(i).getCost();
		}
		list.GenList(strings);
	}

	public void reset() {
		if (store.isUseCredits()) {
			money.setString("credits:"+Universe.getInstance().getPlayer().getInventory().getPlayerCredits());
		} else {
			money.setString("gold:" + Universe.getInstance().getPlayer().getInventory().getPlayerGold());
		}
		description.clean();
		ArrayList<String> strings = AppearanceParser.parseAppearance(Universe.getInstance().getPlayer().getLook());
		for (int i = 0; i < strings.size(); i++) {
			description.addText(strings.get(i));
		}
	}

}
