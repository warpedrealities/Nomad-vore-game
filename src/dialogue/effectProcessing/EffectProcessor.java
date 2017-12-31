package dialogue.effectProcessing;

import mutation.Effect_Mutator;
import nomad.FlagField;
import nomad.Universe;

import javax.xml.soap.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import perks.PerkLibrary;
import shared.Scene_Int;
import shop.ShopList;
import shop.merchant.ShopMerchantScreen;
import solarview.spaceEncounter.SpaceCombatInitializer;
import spaceship.Spaceship;
import spaceship.SpaceshipActionHandler;
import spaceship.boarding.BoardingHelper;
import view.SceneController;
import view.ViewScene;
import vmo.Game;
import widgets.Widget;
import widgets.WidgetCapture;
import widgets.WidgetConversation;
import worldgentools.WidgetPlacer;
import actor.npc.NPC;
import actor.player.CompanionTool;
import actor.player.Player;
import actorRPG.Actor_RPG;
import actorRPG.NPC_RPG;
import actorRPG.player.Player_RPG;
import dialogue.DialogueHelper;
import dialogue.worldscript.WorldScript;
import dialogue.worldscript.WorldScript_Imp;
import faction.Faction;
import faction.FactionLibrary;
import item.Item;
import item.QuestItem;
import item.instances.ItemKeyInstance;
import item.instances.ItemStack;

public class EffectProcessor {

	Widget widget;
	NPC m_npc;
	Spaceship ship;
	FlagField flags;
	Faction faction;
	
	Player m_player;
	
	SceneController controller;
	Scene_Int scene;
	
	public EffectProcessor(Player player, SceneController controller, Scene_Int scene) {
		m_player = player;
		this.controller = controller;
		this.scene=scene;
	}
	
	public void setNPC(NPC npc)
	{
		this.m_npc=npc;
		if (this.m_npc!=null)
		{
			flags=npc.getFlags();
			faction=npc.getActorFaction();			
		}
	}

	public void setWidget(Widget widget) {
		this.widget = widget;
		if (m_npc==null && WidgetConversation.class.isInstance(widget))
		{
			flags=((WidgetConversation)widget).getFlags();
		}
	}

	public void ProcessSpecial(Element node) {
		String str = node.getAttribute("effect");
		if (str.equals("nonhostile")) {
			m_npc.setPeace(true);
		}
		if (str.equals("makehostile")) {
			m_npc.setPeace(false);
		}
		if (str.equals("opendoor")) {
			DialogueHelper.openDoor(node.getAttribute("lock"));
		}
		if (str.equals("destroywidget")) {
			ViewScene.m_interface.RemoveWidget(widget);
		}
		if (str.equals("replaceWidget")) {
			ViewScene.m_interface.ReplaceWidget(widget, new WidgetPlacer().genWidget(node));
		}
		if (str.equals("makecompanion")) {
			CompanionTool.addCompanion(m_npc, m_player);
		}
		if (str.equals("removecompanion")) {
			CompanionTool.removeCompanion(m_npc, m_player);
		}
		if (str.equals("removenpc")) {
			m_npc.Remove();
		}
		if (str.equals("healnpc")) {
			m_npc.Heal();
		}
		if (str.equals("clearviolation")) {
			controller.getHandler().getFactionListener().setViolation(0, null);
		}
		if (str.equals("shop")) {
			scene.replaceScreen(ShopList.getInstance().getShop(node.getAttribute(("ID"))).getScreen());
		}
		if (str.equals("blueprint")) {
			m_player.getCraftingLibrary().unlockRecipe(node.getAttribute("ID"));
		}
		if (str.equals("giveperk")) {
			Player_RPG rpg = (Player_RPG) m_player.getRPG();
			if (rpg.getPerkInstance(node.getAttribute("perk")) == null) {
				rpg.addPerk(PerkLibrary.getInstance().findPerk(node.getAttribute("perk")));
			}
		}

		if (str.equals("transition")) {
			int x = Integer.parseInt(node.getAttribute("x"));
			int y = Integer.parseInt(node.getAttribute("y"));
			String destination = node.getAttribute("destination");
			ViewScene.m_interface.Transition(destination, x, y);
		}
		if (str.equals("research")) {
			int DC = Integer.parseInt(node.getAttribute("DC"));
			String data = node.getAttribute("data");
			int r = Universe.m_random.nextInt(20) + m_player.getRPG().getAttribute(Actor_RPG.SCIENCE);
			String group = null;
			if (node.getAttribute("group").length() > 0) {
				group = node.getAttribute("group");
			}
			m_player.getEncyclopedia().addResearch(data, DC, r, group);
		}
		if (str.equals("data")) {
			String ID = node.getAttribute("ID");
			m_player.getEncyclopedia().addData(ID, null);
		}
		if (str.equals("mutation")) {
			Effect_Mutator mutator = new Effect_Mutator(node);
			mutator.applyEffect(m_player, m_player, false);
		}
		if (str.equals("marktime")) {
			m_npc.getFlags().setFlag("CLOCK", (int) (Universe.getClock() / 100));
		}
		if (str.equals("restockshop")) {
			String shopID = node.getAttribute("ID");
			ShopList.getInstance().getShop(shopID).refreshStore();
		}
		if (str.equals("worldScript")) {
			WorldScript world = new WorldScript_Imp(Universe.getInstance().getCurrentEntity());
			world.initialize(node.getAttribute("script"));
			world.run();
		}
		if (str.equals("removeCaptive")) {
			if (WidgetCapture.class.isInstance(widget)) {
				WidgetCapture wc = (WidgetCapture) widget;
				for (int i = 0; i < wc.getCapacity(); i++) {
					if (wc.getNPC(i) == m_npc) {
						wc.setNPC(null, i);
						break;
					}
				}
			}
		}
		if (str.equals("captureNPC")) {
			CaptureHandler handler = new CaptureHandler(Universe.getInstance().getCurrentEntity(), m_player);
			if (handler.capture(m_npc)) {
				m_npc.Remove();
			}
		}
		if (str.equals("createNPC")) {
			String filename = node.getAttribute("file");
			ViewScene.m_interface.createNPC(filename, m_player.getPosition(),false);
		}
		if (str.equals("changeFaction")) {
			String faction = node.getAttribute("faction");
			m_npc.setActorFaction(FactionLibrary.getInstance().getFaction(faction));
		}
				
		if (str.equals("spaceCombat"))
		{
			new SpaceCombatInitializer((Spaceship)Universe.getInstance().getCurrentEntity(),ship).run();
		}
		if (str.equals("dock"))
		{
			new SpaceshipActionHandler().join((Spaceship)Universe.getInstance().getCurrentEntity(),ship);
		}
		if (str.equals("toView"))
		{
			Game.sceneManager.SwapScene(new ViewScene());
		}
		if (str.equals("boarders"))
		{
			String []strings=new String[Integer.parseInt(node.getAttribute("count"))];
			NodeList n=node.getChildNodes(); int index=0;
			for (int i=0;i<n.getLength();i++)
			{
				if (n.item(i).getNodeType()==Node.ELEMENT_NODE)
				{
					Element e=(Element)n.item(i);
					if (e.getTagName().equals("filename"))
					{
						strings[index]=e.getTextContent();
						index++;
					}

				}
			}
			new BoardingHelper((Spaceship)Universe.getInstance().getCurrentEntity()).addNPCs(strings);;
		}
	}

	private void handleItems(Item item, Element node)
	{
		if (QuestItem.class.isInstance(item))
		{
			QuestItem qi=(QuestItem)item;
			qi.loadFromFile(node.getAttribute("addendum"));
		}				
		if (ItemKeyInstance.class.isInstance(item))
		{
			ItemKeyInstance iki=(ItemKeyInstance)item;
			iki.setLock(node.getAttribute("addendum"));
		}		
	}
	
	public void ProcessEffect(Element node) {
		String str = node.getAttribute("type");
		str = str.toLowerCase();
		float value = Float.parseFloat(node.getAttribute("value"));
		if (str.equals("heal")) {
			m_player.healTo(value);
		}
		if (str.equals("givegold")) {
			m_player.getInventory().setPlayerGold(m_player.getInventory().getPlayerGold() + (int) value);
		}
		if (str.equals("removestatus")) {
			((Player_RPG) m_player.getRPG()).removeStatus(Integer.parseInt(node.getAttribute("value")));
		}
		if (str.equals("passtime")) {
			Universe.AddClock((int) value);
		}
		if (str.equals("feed")) {
			((Player_RPG) m_player.getRPG()).feed((int) value, true);
		}
		if (str.equals("givefood")) {
			((Player_RPG) m_player.getRPG()).feed((int) value, false);
		}
		if (str.equals("stunnpc")) {
			m_npc.addBusy((int) value);
		}
		if (str.equals("experience")) {
			Player_RPG rpg = (Player_RPG) m_player.getRPG();
			rpg.addEXP((int) value);
		}
		if (str.equals("giveitem")) {
			if (value > 1) {
				for (int i = 0; i < value; i++) {
					m_player.getInventory()
							.AddItem(Universe.getInstance().getLibrary().getItem(node.getAttribute("item")));
				}
			} else {
				Item item=Universe.getInstance().getLibrary().getItem(node.getAttribute("item"));
				handleItems(item,node);
				m_player.getInventory().AddItem(item);
			}
		}
		if (str.equals("setlocalflag")) {

			flags.setFlag(node.getAttribute("flag"), (int) value);

		}
		if (str.equals("setglobalflag")) {
			m_player.getFlags().setFlag(node.getAttribute("flag"), (int) value);
		}
		if (str.equals("setfactionflag")) {
			faction.getFactionFlags().setFlag(node.getAttribute("flag"), (int) value);
		}
		if (str.equals("manipulatefactionflag")) {
			FactionLibrary.getInstance().getFaction(node.getAttribute("faction")).getFactionFlags()
					.setFlag(node.getAttribute("flag"), Integer.parseInt(node.getAttribute("value")));
		}
		if (str.equals("incrementlocalflag")) {

			flags.setFlag(node.getAttribute("flag"),
			m_npc.getFlags().readFlag(node.getAttribute("flag")) + (int) value);

		}
		if (str.equals("incrementglobalflag")) {
			m_player.getFlags().setFlag(node.getAttribute("flag"),
					m_player.getFlags().readFlag(node.getAttribute("flag")) + (int) value);
		}
		if (str.equals("incrementfactionflag")) {
			faction.getFactionFlags().setFlag(node.getAttribute("flag"),
					m_player.getFlags().readFlag(node.getAttribute("flag")) + (int) value);
		}
		if (str.equals("setviolation")) {
			controller.getHandler().getFactionListener().setViolation(Integer.parseInt(node.getAttribute("value")),
					m_player.getPosition());
		}
		if (str.equals("modfactiondisposition")) {
			Integer existing = faction.getRelationship("player");
			faction.modDisposition("player", existing + Integer.parseInt(node.getAttribute("value")));
		}
		if (str.equals("manipulatedisposition")) {
			FactionLibrary.getInstance().getFaction(node.getAttribute("faction")).modDisposition("player",
					Integer.parseInt(node.getAttribute("value")));
		}

		if (str.equals("removeitem")) {
			String item = node.getAttribute("item");
			m_player.getInventory().removeItems(item, (int) value);
		}
		if (str.equals("imprison")) {
			for (int i = 0; i < value; i++) {
				m_player.Update();
			}
			Universe.AddClock((int) value);
		}
		if (str.equals("giveEnergy")) {
			new RechargeHelper(m_player).run((int) value);
		}
	}

	public void setSpaceship(Spaceship ship) {
		this.ship=ship;
		this.flags=ship.getShipController().getflags();
		this.faction=ship.getShipController().getFaction();
	}

	public void endConversation() {
		scene.replaceScreen(null);
	}

}
