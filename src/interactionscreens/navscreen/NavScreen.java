package interactionscreens.navscreen;

import gui.Button;
import gui.Text;
import gui.Window;
import input.MouseHook;

import java.nio.FloatBuffer;
import java.util.Iterator;

import actor.player.CompanionTool;
import nomad.Station;
import nomad.World;
import nomad.universe.Universe;
import shared.Callback;
import shared.Screen;
import shared.Vec2f;
import solarview.SolarScene;
import spaceship.Spaceship;
import spaceship.Spaceship.ShipState;
import spaceship.stats.SpaceshipAnalyzer;
import spaceship.stats.SpaceshipStats;
import spaceship.stats.SpaceshipWeapon;
import spaceship.SpaceshipActionHandler;
import spaceship.SpaceshipResource;
import vmo.Game;
import vmo.GameManager;

public class NavScreen extends Screen implements Callback {

	Spaceship spaceship;
	SpaceshipStats shipStats;
	Callback callback;
	Window window;
	Window statWindow;
	Window crewWindow;
	boolean canLaunch;
	String statustext;
	private int weaponIndex;
	private Text[] weaponTexts;

	public NavScreen(Spaceship ship) {
		spaceship = ship;
		shipStats = new SpaceshipAnalyzer().generateStats(spaceship);
		setCanLaunch();
	}

	public void setCanLaunch() {
		canLaunch = true;
		if (spaceship.isWrecked() == true) {
			canLaunch = false;
			String str = spaceship.getUnusableState();
			statustext = "flight impossible: " + str;
			return;
		}
		if (shipStats.isLooseItems()) {
			canLaunch = false;
			statustext = "unsecured items, thrust unsafe, secure items";
			return;
		}
		if (shipStats.getResource("HULL").getResourceAmount() <= 0) {
			canLaunch = false;
			statustext = "thrust unsafe, hull compromised, repairs required";
			return;
		}
		if (shipStats.getResource("FUEL") == null) {
			statustext = "no fuel tanks connected to drive system, correct fault";
			canLaunch = false;
			return;
		}
		if (shipStats.getResource("FUEL").getResourceAmount() <= spaceship.getBaseStats().getThrustCost()
				&& spaceship.getShipState() == ShipState.LAND) {
			canLaunch = false;
			statustext = "insufficient fuel for launch, more fuel required";
			return;
		}
		if (shipStats.getResource("FUEL").getResourceAmount() <= shipStats.getFuelEfficiency()
				&& spaceship.getShipState() != ShipState.LAND) {
			canLaunch = false;
			statustext = "fuel pressure low, more fuel required";
			return;
		}
		for (int i=0;i<shipStats.getCrewCount();i++)
		{
			if (shipStats.getCrewList().get(i).getActorFaction().getRelationship("player")<=50)
			{
				canLaunch = false;
				statustext = "not safe to take the controls with hostiles aboard";
				break;
			}
		}

		if (canLaunch == true) {
			if (shipStats.getCrewCapacity() < shipStats.getCrewCount()) {
				statustext = "insufficient accomodation for current crew";
			} else {
				statustext = "system okay";
			}

		}
	}

	@Override
	public void Callback() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(float DT) {
		window.update(DT);
		if (statWindow!=null)
		{
			statWindow.update(DT);
		}
		if (crewWindow!=null)
		{
			crewWindow.update(DT);
		}		
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {

		window.Draw(buffer, matrixloc);
		if (statWindow!=null)
		{
			statWindow.Draw(buffer, matrixloc);	
		}
		if (crewWindow!=null)
		{
			crewWindow.Draw(buffer, matrixloc);	
		}

	}

	@Override
	public void discard(MouseHook mouse) {

		mouse.Remove(window);
		window.discard();
		if (statWindow!=null)
		{
			mouse.Remove(statWindow);	
			statWindow.discard();
		}
		if (crewWindow!=null)
		{
			mouse.Remove(crewWindow);
			crewWindow.discard();			
		}

	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		switch (ID) {
		case 0:
			callback.Callback();
			break;

		case 1:
			launch();
			break;

		case 2:
			nextWeapon();
			break;

		case 3:
			previousWeapon();
			break;
		}
	}

	private void nextWeapon() {
		if (weaponIndex < shipStats.getWeapons().size() - 1) {
			weaponIndex++;
			showWeapon();
		}

	}

	private void previousWeapon() {
		if (weaponIndex > 0) {
			weaponIndex--;
			showWeapon();
		}

	}

	private String facingToStr(int i) {
		switch (i) {
		case 0:
			return "forward";

		case 1:
			return "forward right";
		case 2:
			return "right";
		case 3:
			return "backwards right";
		case 4:
			return "back";
		case 5:
			return "backwards left";
		case 6:
			return "left";
		case 7:
			return "forward left";
		}
		return "";
	}

	private void showWeapon() {
		SpaceshipWeapon weapon = shipStats.getWeapons().get(weaponIndex);
		weaponTexts[0].setString(weapon.getWeapon().getName());

		String str = "mindmg:" + weapon.getWeapon().getMinDamage() + " maxdmg:" + weapon.getWeapon().getMaxDamage()
				+ " pen:" + weapon.getWeapon().getPenetration() + " dis:" + weapon.getWeapon().getDisruption()
				+ " trac:" + weapon.getWeapon().getTracking();
		weaponTexts[1].setString(str);

		str = "facing:" + facingToStr(weapon.getFacing()) + " rof:" + weapon.getWeapon().getVolley() + " arc:"
				+ weapon.getWeapon().getFiringArc() + " cooldown:" + weapon.getWeapon().getCooldown() + " range:"
				+ weapon.getWeapon().getMaxRange();
		weaponTexts[2].setString(str);

		weaponTexts[3].setString(weapon.getWeapon().getDescription());

		StringBuilder builder = new StringBuilder();
		builder.append("firecost:");
		for (int i = 0; i < weapon.getWeapon().getWeaponCosts().size(); i++) {
			builder.append(weapon.getWeapon().getWeaponCosts().get(i).toString());
		}
		weaponTexts[4].setString(builder.toString());

		weaponTexts[5].setString((weaponIndex + 1) + "/" + shipStats.getWeapons().size());
	}

	private void launch() {
		if (canLaunch == true) {
			if (spaceship.getState() == ShipState.LAND) {
				if (shipStats.getCrewCapacity() < shipStats.getCrewCount()) {
					offLoadCrew();
				}
				spaceship.setShipStats(shipStats);
				spaceship.setShipState(ShipState.SPACE);
				shipStats.getResource("FUEL").subtractResourceAmount(shipStats.getFuelEfficiency());
				int r = new SpaceshipActionHandler().Launch(spaceship,
						(World) Universe.getInstance().getCurrentWorld(spaceship));

				Game.sceneManager.SwapScene(new SolarScene(r, spaceship));
				return;
			}
			if (spaceship.getState() == ShipState.SPACE) {
				spaceship.setShipStats(shipStats);
				// spaceship.setShipState(ShipState.SPACE);
				Game.sceneManager.SwapScene(new SolarScene(0, spaceship));
				return;
			}
			if (spaceship.getState() == ShipState.DOCK) {
				if (shipStats.getCrewCapacity() < shipStats.getCrewCount()) {
					offLoadCrew();
				}
				spaceship.setShipStats(shipStats);
				spaceship.setShipState(ShipState.SPACE);
				int r = new SpaceshipActionHandler().undock(spaceship,
						(Station) Universe.getInstance().getCurrentWorld(spaceship));

				Game.sceneManager.SwapScene(new SolarScene(r, spaceship));
				return;
			}
			if (spaceship.getState() == ShipState.SHIPDOCK) {
				int r = new SpaceshipActionHandler().separate(spaceship,
						(Spaceship) Universe.getInstance().getCurrentWorld(spaceship));
				spaceship.setShipStats(shipStats);
				spaceship.setShipState(ShipState.SPACE);
				Game.sceneManager.SwapScene(new SolarScene(r, spaceship));
				return;
			}
		}
	}

	private void offLoadCrew() {
		// figure out how many crew we need to offload
		int offload = shipStats.getCrewCount() - shipStats.getCrewCapacity();
		for (int i = 0; i < offload; i++) {
			CompanionTool.removePassenger(shipStats.getCrewList().get(i), spaceship);
		}
	}

	@Override
	public void start(MouseHook hook) {

		hook.Register(window);
		if (statWindow!=null)
		{
			hook.Register(statWindow);
		}
	}

	private void initNav(int[] textures)
	{
		window = new Window(new Vec2f(-20, -16), new Vec2f(40, 15), textures[1], true);

		// build buttons
		Button[] buttons = new Button[2];

		buttons[0] = new Button(new Vec2f(34.0F, 0.0F), new Vec2f(6, 1.8F), textures[2], this, "Exit", 0, 1);

		switch (spaceship.getShipState()) {
		case LAND:
			buttons[1] = new Button(new Vec2f(34.0F, 2.0F), new Vec2f(6, 1.8F), textures[2], this, "launch", 1, 1);
			break;

		case DOCK:
			buttons[1] = new Button(new Vec2f(34.0F, 2.0F), new Vec2f(6, 1.8F), textures[2], this, "undock", 1, 1);
			break;
		case SHIPDOCK:
			buttons[1] = new Button(new Vec2f(34.0F, 2.0F), new Vec2f(6, 1.8F), textures[2], this, "undock", 1, 1);
			break;
		case SPACE:
			buttons[1] = new Button(new Vec2f(34.0F, 2.0F), new Vec2f(6, 1.8F), textures[2], this, "control", 1, 1);
			break;
		case ADRIFT:
			buttons[1] = new Button(new Vec2f(34.0F, 2.0F), new Vec2f(6, 1.8F), textures[2], this, "control", 1, 1);
			break;	
		}

		for (int i = 0; i < 2; i++) {
			window.add(buttons[i]);
		}
		// build info texts
		Text[] texts = new Text[10];
		int count = 0;
		Iterator<SpaceshipResource> iterator = shipStats.getIterator();

		for (int i = 0; i < 10; i++) {
			texts[i] = new Text(new Vec2f(0.5F, 7.0F - (0.7F * i)), "texts", 0.7F, textures[4]);
			if (iterator.hasNext()) {
				SpaceshipResource res = iterator.next();
				texts[i].setString(res.getResourceName() + ":" + res.getResourceAmount() + "/" + res.getResourceCap());
				count++;
			} else {
				texts[i].setString("");
			}
			window.add(texts[i]);
		}

		if (count < 10 && shipStats.isLooseItems()) {
			texts[count].setString("unsecured objects detected, acceleration hazard");
		}

		Text status = new Text(new Vec2f(10.5F, 7.0F), statustext, 0.7F, textures[4]);
		window.add(status);
		buildStatWindow(textures);
		buildCrewWindow(textures);		
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

		initNav(textures);	
	}

	private void buildCrewWindow(int[] textures) {
		crewWindow=new Window(new Vec2f(-1, -1), new Vec2f(4, 6), textures[1], true);
		Text[] texts = new Text[4];
		for (int i = 0; i < 4; i++) {
			texts[i] = new Text(new Vec2f(0.5F, 2.5F - (0.7F * i)), "texts", 0.7F, textures[4]);
			switch (i) {
			case 0:	
				texts[i].setString("nav:" + shipStats.getCrewStats().getNavigation());		
			break;
			case 1:	
				texts[i].setString("gun:" + shipStats.getCrewStats().getGunnery());		
			break;
			case 2:	
				texts[i].setString("eng:" + shipStats.getCrewStats().getEngineer());		
			break;
			case 3:	
				texts[i].setString("tec:" + shipStats.getCrewStats().getTechnician());		
			break;	
			}
			crewWindow.add(texts[i]);
		}
	
	}
	
	private void buildStatWindow(int[] textures) {

		statWindow = new Window(new Vec2f(3, -1), new Vec2f(17, 17), textures[1], true);
		Text[] texts = new Text[5];
		for (int i = 0; i < 5; i++) {
			texts[i] = new Text(new Vec2f(0.5F, 8.0F - (0.7F * i)), "texts", 0.7F, textures[4]);
			switch (i) {
			case 0:
				texts[i].setString("fuel efficiency:" + shipStats.getFuelEfficiency());
				break;
			case 1:
				float speed = ((10.0F / ((float) shipStats.getMoveCost())) * 100);
				speed -= (speed) % 1;
				texts[i].setString("speed:" + speed);
				break;
			case 2:
				texts[i].setString("manouver:" + shipStats.getManouverability());
				break;
			case 3:
				texts[i].setString("armour:" + shipStats.getArmour());
				break;
			case 4:
				texts[i].setString("FTL:" + shipStats.getFTL());
				break;	
			}
			statWindow.add(texts[i]);
		}

		if (shipStats.getShield() != null) {
			texts = new Text[5];
			for (int i = 0; i < 5; i++) {
				texts[i] = new Text(new Vec2f(5.5F, 8.0F - (0.7F * i)), "texts", 0.7F, textures[4]);
				switch (i) {
				case 0:
					texts[i].setString("hitpoints:" + shipStats.getShield().getHitpoints());
					break;
				case 1:
					texts[i].setString("absorption:" + shipStats.getShield().getAbsorption());
					break;
				case 2:
					texts[i].setString("regeneration:" + shipStats.getShield().getRegeneration());
					break;
				case 3:
					texts[i].setString("restartTime:" + shipStats.getShield().getRestartTime());
					break;
				case 4:
					texts[i].setString("energyCost:" + shipStats.getShield().getEnergyCost());
					break;
				}
				statWindow.add(texts[i]);
			}
		}

		if (!shipStats.getWeapons().isEmpty()) {
			weaponTexts = new Text[6];
			for (int i = 0; i < 5; i++) {
				weaponTexts[i] = new Text(new Vec2f(0.2F, 4.0F - (0.7F * i)), "texts", 0.6F, textures[4]);
				statWindow.add(weaponTexts[i]);
			}
			weaponTexts[5] = new Text(new Vec2f(3.3F, 0.5F), "texts", 0.6F, textures[4]);
			statWindow.add(weaponTexts[5]);
			Button[] buttons = new Button[2];

			buttons[0] = new Button(new Vec2f(0.1F, 0.1F), new Vec2f(6, 1.8F), textures[2], this, "back", 3, 1);
			buttons[1] = new Button(new Vec2f(11.1F, 0.1F), new Vec2f(6, 1.8F), textures[2], this, "forward", 2, 1);
			for (int i = 0; i < 2; i++) {
				statWindow.add(buttons[i]);
			}
			showWeapon();
		}
	}

}
