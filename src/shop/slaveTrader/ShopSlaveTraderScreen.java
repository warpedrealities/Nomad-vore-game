package shop.slaveTrader;

import java.nio.FloatBuffer;

import actor.player.Player;
import gui.Button;
import gui.Text;
import gui.Window;
import gui.lists.List;
import input.MouseHook;
import nomad.universe.Universe;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;

public class ShopSlaveTraderScreen extends Screen implements Callback {
	public static final int DEF_CREDIT_TO_GOLD = 10;
	private Callback callback;
	private ShopSlaveTrader slaveTrader;
	private SlaveTraderPlayerStock playerStock;
	private Player player;
	private Window window;	
	private Text money;
	private List[] slaveLists;
	
	public ShopSlaveTraderScreen(ShopSlaveTrader shopSlaveTrader) {
		this.slaveTrader=shopSlaveTrader;
		SlaveTraderPlayerStockCreator creator=new SlaveTraderPlayerStockCreator(shopSlaveTrader.getBuyList()); 
		player=Universe.getInstance().getPlayer();
		creator.attachShip(player);
		this.playerStock=creator.generateStock();
	}

	@Override
	public void update(float DT) {
		window.update(DT);
		for (int i=0;i<slaveLists.length;i++)
		{
			slaveLists[i].update(DT);
		}
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		window.Draw(buffer, matrixloc);
		for (int i = 0; i < 2; i++) {
			slaveLists[i].Draw(buffer, matrixloc);
		}
	}

	@Override
	public void discard(MouseHook mouse) {
		mouse.Remove(window);
		for (int i = 0; i < slaveLists.length; i++) {
			mouse.Remove(slaveLists[i]);
		}
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {

		switch (ID) {

		case 1:
			sell(slaveLists[0].getSelect());
			break;
		case 2:

			break;
		case 3:

			callback.Callback();
			break;

		}
	}

	private void sell(int index) {
		if (index>=playerStock.getStock().size()){
			return;
		}
		SlaveStockItem item=playerStock.getStock().get(index);
		if (item!=null)
		{
			int value=calcCost(item);
			if (slaveTrader.isUseCredits()) {
				player.getInventory().setPlayerCredits(player.getInventory().getPlayerCredits() + (int) value);
			} else {
				player.getInventory().setPlayerGold(player.getInventory().getPlayerGold() + (int) value);
			}	
			playerStock.removeCaptive(item);
			resetList();
		}
	}

	@Override
	public void start(MouseHook hook) {
		hook.Register(window);
		for (int i = 0; i < 2; i++) {
			hook.Register(slaveLists[i]);
		}	
	
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
		window = new Window(new Vec2f(-3, -16), new Vec2f(6, 15), textures[1], true);
		Button[] buttons = new Button[3];
		buttons[0] = new Button(new Vec2f(0.0F, 0.0F), new Vec2f(6, 1.8F), textures[2], this, "Exit", 3, 1);
		buttons[1] = new Button(new Vec2f(0.0F, 2.0F), new Vec2f(6, 1.8F), textures[2], this, "busy", 2, 1);
		buttons[2] = new Button(new Vec2f(0.0F, 4.0F), new Vec2f(6, 1.8F), textures[2], this, "sell", 1, 1);
		// add buttons to move things to and from the container
		for (int i = 0; i < 3; i++) {
			window.add(buttons[i]);
		}	
		money = new Text(new Vec2f(0.2F, 6.2F), "money", 0.7F, textures[4]);
		slaveLists=new List[2];
		slaveLists[0] = new List(new Vec2f(3, -14.3F), 16, textures[5], textures[4], null);
		slaveLists[1] = new List(new Vec2f(-20, -14.3F), 16, textures[5], textures[4], this);

		String str[] = new String[16];
		for (int i = 0; i < 16; i++) {
			str[i] = "";
		}
		for (int i = 0; i < 2; i++) {
			slaveLists[i].GenList(str);
		}
		window.add(money);
		resetList();
	}
	
	void resetList() {

		if (slaveTrader.isUseCredits()) {
			money.setString("C:" + player.getInventory().getPlayerCredits());
		} else {
			money.setString("G:" + player.getInventory().getPlayerGold());
		}
		String[] str = new String[playerStock.getStock().size()];
		for (int i = 0; i < str.length; i++) {
			str[i] = playerStock.getStock().get(i).getSlaveName()+ " " + "" + calcCost(playerStock.getStock().get(i));

		}
		slaveLists[0].GenList(str);
	}

	private int calcCost(SlaveStockItem slaveStockItem) {
		if (slaveTrader.isUseCredits())
		{
			return slaveStockItem.getValue()*DEF_CREDIT_TO_GOLD;
		}
		else
		{
			return slaveStockItem.getValue();
		}
	}

	@Override
	public void Callback() {
		// TODO Auto-generated method stub
		
	}
}
