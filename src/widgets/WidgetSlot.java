package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import actor.player.Inventory;
import actor.player.Player;
import actorRPG.Actor_RPG;
import combat.CombatMove;
import combat.effect.Effect_Dismantle;
import interactionscreens.SlotScreen;
import item.Item;
import item.instances.ItemDepletableInstance;
import nomad.universe.Universe;
import shared.ParserHelper;
import shared.Vec2f;
import shipsystem.WidgetSystem;
import shipsystem.WidgetSystem.SystemType;
import view.ViewScene;

public class WidgetSlot extends Widget {

	private WidgetBreakable widget;
	private String widgetItem;

	private int facing;
	private WidgetSystem.SystemType systemType;

	public WidgetSlot(Element root) {

		isWalkable = true;
		isVisionBlocking = false;
		systemType = SystemType.NORMAL;
		String typeStr = root.getAttribute("type");
		if (typeStr.equals("HARDPOINT")) {
			systemType = systemType.HARDPOINT;
		}
		if (typeStr.equals("SUPPORT")) {
			systemType = systemType.SUPPORT;
		}
		if (root.getAttribute("facing").length() > 0) {
			facing = Integer.parseInt(root.getAttribute("facing"));
		}
		setDescSprite();

	}

	private void setDescSprite() {
		switch (systemType) {
		case NORMAL:
			widgetSpriteNumber = 5;
			widgetDescription = "a modular slot";
			break;

		case HARDPOINT:
			widgetSpriteNumber = 6;
			widgetDescription = "a hardpoint for weapons and external systems";
			break;

		case SUPPORT:
			widgetSpriteNumber = 14;
			widgetDescription = "a support slot";
			break;
		}

	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.write(12);
		commonSave(dstream);
		dstream.writeInt(systemType.ordinal());
		dstream.writeInt(facing);
		// save widget
		if (widget != null) {
			dstream.writeBoolean(true);
			widget.save(dstream);

		} else {
			dstream.writeBoolean(false);
		}
		if (widgetItem!=null)
		{
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, widgetItem);
		}
		else
		{
			dstream.writeBoolean(false);
		}
	}

	public WidgetSlot(DataInputStream dstream) throws IOException {
		commonLoad(dstream);

		systemType = SystemType.values()[dstream.readInt()];

		facing = dstream.readInt();

		// load widget
		if (dstream.readBoolean()) {
			widget = (WidgetBreakable) WidgetLoader.loadWidget(dstream);

		}
		if (dstream.readBoolean())
		{
			widgetItem=ParserHelper.LoadString(dstream);
		}
		setDescSprite();
	}

	public WidgetBreakable getWidget() {
		return widget;
	}

	public void setWidget(WidgetBreakable widget,String widgetItem) {
		this.widget = widget;
		// trigger global redraw to show changes
		if (ViewScene.m_interface != null) {
			ViewScene.m_interface.redraw();
		}
		this.widgetItem=widgetItem;
	}

	public void setWidgetItem(String widgetItem) {
		this.widgetItem = widgetItem;
	}

	@Override
	public String getDescription() {
		if (widget != null) {
			return widget.getDescription();
		}
		return widgetDescription;
	}

	@Override
	public boolean Walkable() {
		if (widget != null) {
			return widget.isWalkable;
		}
		return isWalkable;
	}

	@Override
	public boolean BlockVision() {
		if (widget != null) {
			return widget.BlockVision();
		}
		return isVisionBlocking;
	}

	@Override
	public int getSprite() {
		if (widget != null) {
			return widget.getSprite();
		}
		return widgetSpriteNumber;
	}

	@Override
	public boolean Interact(Player player) {
		if (widget != null) {
			return widget.Interact(player);
		} else {
			ViewScene.m_interface.setScreen(new SlotScreen(this));
			return true;
		}

	}

	@Override
	public boolean safeOnly() {
		return true;
	}

	public void handleAttack(CombatMove combatMove) {
		if (widget != null) {
			if (widget.getHitpoints() <= 0) {
				Item[] stack = widget.getContained();

				WidgetItemPile Pile = new WidgetItemPile(2, "a pile of items containing ", stack[0]);
				if (stack.length > 1) {
					for (int j = 1; j < stack.length; j++) {
						Pile.AddItem(stack[j]);
					}
				}
				this.widget.handleDismantle(Pile);
				Vec2f p = ViewScene.m_interface.getSceneController().getActiveZone().getWidgetPosition(this);
				ViewScene.m_interface.placeWidget(Pile, (int) p.x, (int) p.y, true);
				widget = null;
				widgetItem=null;
				ViewScene.m_interface.redraw();
			}
		}
	}

	public int getFacing() {
		return facing;
	}

	public void setFacing(int facing) {
		this.facing = facing;
	}

	public SystemType getType() {
		return systemType;
	}

	public boolean checkDismantle(CombatMove combatMove) {
		if (widget != null) {

			if (widgetItem!=null)
			{
				for (int i=0;i<combatMove.getEffects().size();i++)
				{
					if (Effect_Dismantle.class.isInstance(combatMove.getEffects().get(i)))
					{
						WidgetItemPile Pile = new WidgetItemPile(2, "a pile of items containing ", Universe.getInstance().getLibrary().getItem(widgetItem));
						this.widget.handleDismantle(Pile);
						Vec2f p = ViewScene.m_interface.getSceneController().getActiveZone().getWidgetPosition(this);
						ViewScene.m_interface.placeWidget(Pile, (int) p.x, (int) p.y, true);
						ViewScene.m_interface.DrawText(
								"You dismantle the device in this spaceship slot so you can re-use the device later");
						widget = null;
						widgetItem=null;
						ItemDepletableInstance energy = null;
						Actor_RPG actorRPG = Universe.getInstance().getPlayer().getRPG();
						Inventory playerInventory = Universe.getInstance().getPlayer().getInventory();
						if (combatMove.getAmmoCost() > 0) {
							if (combatMove.getEnergySource() == -2) {
								actorRPG.setStat(Actor_RPG.SATIATION,
										actorRPG.getStat(Actor_RPG.SATIATION) - combatMove.getAmmoCost());
							}
							if (combatMove.getEnergySource() >= 0) {
								if (playerInventory.getSlot(combatMove.getEnergySource()) != null
										&& ItemDepletableInstance.class
										.isInstance(playerInventory.getSlot(combatMove.getEnergySource()))) {
									energy = (ItemDepletableInstance) playerInventory
											.getSlot(combatMove.getEnergySource());
									if (energy.getEnergy() < combatMove.getAmmoCost()) {
										ViewScene.m_interface
										.DrawText("insufficent energy to use " + combatMove.getMoveName());
										return false;
									}
								}
							}
						}
						Universe.getInstance().getPlayer().useAmmo(combatMove, null, combatMove.getEnergySource(),
								null);
						ViewScene.m_interface.redraw();
						return true;
					}
				}
			}
		}
		return false;
	}

}
