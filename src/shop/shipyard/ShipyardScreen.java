package shop.shipyard;

import java.nio.FloatBuffer;

import actor.player.Player;
import gui.Button;
import gui.Image;
import gui.ScrollableMultiLineText;
import gui.Text;
import gui.TextColoured;
import gui.Window;
import input.MouseHook;
import nomad.universe.Universe;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;
import view.ViewScene;

public class ShipyardScreen extends Screen {

	private ShopShipyard shipyard;
	private Window window;
	private Callback callback;
	private Image image;
	private int index;
	private TextColoured price;
	private Text shipName, money;
	private ScrollableMultiLineText description;
	private Player player;
	private boolean canBuy;

	public ShipyardScreen(ShopShipyard shopShipyard) {
		this.shipyard = shopShipyard;
		index = 0;
		player = Universe.getInstance().getPlayer();
	}

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub
		window.update(DT);
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		// TODO Auto-generated method stub
		window.Draw(buffer, matrixloc);
		image.Draw(buffer, matrixloc);
	}

	@Override
	public void discard(MouseHook mouse) {
		// TODO Auto-generated method stub
		mouse.Remove(window);
		window.discard();
		image.discard();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		switch (ID) {
		case 0:
			this.callback.Callback();
			break;
		case 1:
			if (canBuy) {
				buyShip();
			}
			break;
		case 2:

			break;
		case 3:

			break;
		}
	}

	private void buyShip() {
		SpaceshipPurchaseHelper handler = new SpaceshipPurchaseHelper(shipyard.getShips().get(index).getFile());
		if (handler.run()) {
			if (shipyard.isUseCredits()) {
				player.getInventory().setPlayerCredits(
						player.getInventory().getPlayerCredits() - (shipyard.getShips().get(index).getCost() * 10));
			} else {
				player.getInventory().setPlayerGold(
						player.getInventory().getPlayerGold() - (shipyard.getShips().get(index).getCost()));
			}
			ViewScene.m_interface.DrawText("You have purchased a new spacecraft, welcome aboard");
			handler.warp();
			callback.Callback();
		}
		else {
			ViewScene.m_interface.DrawText("Nowhere to dock new spaceship captain");
			callback.Callback();
		}

	}

	@Override
	public void start(MouseHook hook) {
		// TODO Auto-generated method stub
		hook.Register(window);
	}

	@Override
	public void initialize(int[] textures, Callback callback) {
		// 0 is font
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint

		// TODO Auto-generated method stub
		window = new Window(new Vec2f(-20, -16), new Vec2f(40, 15), textures[1], true);
		Button[] buttons = new Button[4];
		buttons[0] = new Button(new Vec2f(0.5F, 6.5F), new Vec2f(4, 1.8F), textures[2], this, "prev", 2, 1);
		buttons[1] = new Button(new Vec2f(0.5F, 4.5F), new Vec2f(4, 1.8F), textures[2], this, "next", 3, 1);
		buttons[2] = new Button(new Vec2f(0.5F, 2.5F), new Vec2f(4, 1.8F), textures[2], this, "buy", 1, 1);
		buttons[3] = new Button(new Vec2f(0.5F, 0.5F), new Vec2f(4, 1.8F), textures[2], this, "exit", 0, 1);

		if (shipyard.getNumShips() == 1) {
			buttons[0].setActive(false);
			buttons[1].setActive(false);
		}
		// add buttons to move things to and from the container
		for (int i = 0; i < 4; i++) {
			window.add(buttons[i]);
		}

		price=new TextColoured(new Vec2f(6.5F, 0.5F),"",textures[4]);
		money = new Text(new Vec2f(12.0F, 0.5F), "", textures[4]);
		window.add(money);
		window.add(price);
		shipName = new Text(new Vec2f(9.5F, 7.0F), "", textures[4]);
		window.add(shipName);
		this.callback = callback;
		image = new Image(new Vec2f(-8.5F, 7.5F), new Vec2f(1, 1), "assets/art/portraits/test.png");
		description = new ScrollableMultiLineText(new Vec2f(6.5F, 12.5F), 8, 96, 0.8F, new Vec2f(18, -6.5F));
		this.window.add(description);
		reset();
	}

	private void calcCanBuy() {
		int cost, money;
		if (shipyard.isUseCredits()) {
			cost = shipyard.getShips().get(index).getCost() * 10;
			money = player.getInventory().getPlayerCredits();
		} else {
			cost = shipyard.getShips().get(index).getCost();
			money = player.getInventory().getPlayerGold();
		}
		if (cost > money) {
			canBuy = false;
			this.price.setTint(1, 0, 0);
		} else {
			canBuy = true;
			this.price.setTint(0, 1, 0);
		}
	}

	public void reset() {

		if (shipyard.isUseCredits()) {
			money.setString("PlayerCredits:" + player.getInventory().getPlayerCredits());
			price.setString("shipCost:" + shipyard.getShips().get(index).getCost() * 10);
		} else {
			money.setString("PlayerGolds:" + player.getInventory().getPlayerGold());
			price.setString("shipCost:" + shipyard.getShips().get(index).getCost());
		}
		calcCanBuy();
		shipName.setString(shipyard.getShips().get(index).getFile());
		description.addText(shipyard.getShips().get(index).getDescription());

		image.setSize(shipyard.getShips().get(index).getImageWidth(), shipyard.getShips().get(index).getImageHeight());
		image.setTexture("assets/art/bigsprites/" + shipyard.getShips().get(index).getImage() + ".png");
	}
}
