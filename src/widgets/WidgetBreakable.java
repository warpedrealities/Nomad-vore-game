package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import nomad.Universe;

import shared.ParserHelper;
import shared.Vec2f;
import view.ViewScene;
import zone.Zone;
import actor.Actor;
import actor.Attackable;
import actorRPG.Actor_RPG;
import actorRPG.RPG_Helper;
import item.Item;
import combat.effect.Effect;
import combat.effect.Effect_Damage;

public class WidgetBreakable extends Widget implements Attackable {

	protected Item[] m_contains;
	protected int hitpoints;
	protected int[] m_resistances;
	protected String m_name;
	protected Vec2f position;
	protected boolean slottedWidget;

	public WidgetBreakable() {

	}

	public int getHitpoints() {
		return hitpoints;
	}

	public boolean isSlottedWidget() {
		return slottedWidget;
	}

	public void setSlottedWidget(boolean slottedWidget) {
		this.slottedWidget = slottedWidget;
	}

	public WidgetBreakable(Element node) {
		isWalkable = false;
		isVisionBlocking = true;
		widgetSpriteNumber = Integer.parseInt(node.getAttribute("sprite"));
		m_name = node.getAttribute("name");
		m_resistances = new int[3];
		NodeList children = node.getChildNodes();
		hitpoints = Integer.parseInt(node.getAttribute("health"));

		for (int i = 0; i < children.getLength(); i++) {
			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;
				// run each step successively
				if (Enode.getTagName() == "description") {
					widgetDescription = Enode.getTextContent();
				}
				if (Enode.getTagName() == "health") {
					hitpoints = Integer.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName() == "resistance") {
					m_resistances[RPG_Helper.AttributefromString(Enode.getAttribute("resists"))] = Integer
							.parseInt(Enode.getAttribute("strength"));
				}
				if (Enode.getTagName() == "contains") {
					int min = Integer.parseInt(Enode.getAttribute("minimum"));
					int max = Integer.parseInt(Enode.getAttribute("maximum"));
					int c = Universe.m_random.nextInt(max - min) + min;
					m_contains = new Item[c];
					for (int j = 0; j < c; j++) {
						m_contains[j] = Universe.getInstance().getLibrary().getItem(Enode.getTextContent());
					}
				}
			}
		}
	}

	public WidgetBreakable(int sprite, String description, String name, Item[] contains, int hp, int[] resistances) {
		m_name = name;
		widgetSpriteNumber = sprite;
		isVisionBlocking = false;
		isWalkable = false;
		widgetDescription = description;
		m_contains = contains;
		m_resistances = resistances;
		hitpoints = hp;

	}

	protected void destroy() {
		if (slottedWidget == false) {
			if (m_contains != null) {
				WidgetItemPile Pile = new WidgetItemPile(2, "a pile of items containing ", m_contains[0]);
				if (m_contains.length > 1) {
					for (int j = 1; j < m_contains.length; j++) {
						Pile.AddItem(m_contains[j]);
					}
				}
				ViewScene.m_interface.ReplaceWidget(this, Pile);
			} else {
				ViewScene.m_interface.RemoveWidget(this);
			}
		}

	}

	public Item[] getContained() {
		return m_contains;
	}

	@Override
	public boolean getAttackable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return m_name;
	}

	@Override
	public Vec2f getPosition() {
		if (position == null) {
			// find this widget
			Zone zone = Universe.getInstance().getCurrentZone();
			for (int i = 0; i < zone.getWidth(); i++) {
				for (int j = 0; j < zone.getHeight(); j++) {
					if (zone.getTile(i, j) != null) {
						if (zone.getTile(i, j).getWidgetObject() != null) {
							if (zone.getTile(i, j).getWidgetObject() == this) {
								position = new Vec2f(i, j);
								break;
							}
							if (zone.getTile(i, j).getWidgetObject().getClass().getName().contains("WidgetSlot")) {
								WidgetSlot ws = (WidgetSlot) zone.getTile(i, j).getWidgetObject();
								if (ws.getWidget() == this) {
									position = new Vec2f(i, j);
									break;
								}

							}
						}
					}

				}
			}
		}
		return position;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {

		dstream.write(5);
		commonSave(dstream);
		saveBreakable(dstream);
	}

	public WidgetBreakable(DataInputStream dstream) throws IOException {
		commonLoad(dstream);
		load(dstream);
	}

	protected void saveBreakable(DataOutputStream dstream) throws IOException {
		if (m_contains != null) {
			dstream.writeInt(m_contains.length);
			for (int i = 0; i < m_contains.length; i++) {
				m_contains[i].save(dstream);
			}
		} else {
			dstream.writeInt(0);
		}

		for (int i = 0; i < m_resistances.length; i++) {
			dstream.writeInt(m_resistances[i]);
		}
		dstream.writeInt(hitpoints);
		ParserHelper.SaveString(dstream, m_name);
		dstream.writeBoolean(slottedWidget);
	}

	protected void load(DataInputStream dstream) throws IOException {

		int count = dstream.readInt();
		if (count > 0) {
			m_contains = new Item[count];
			for (int i = 0; i < count; i++) {
				m_contains[i] = Universe.getInstance().getLibrary().getItem(dstream);
			}
		}

		m_resistances = new int[3];
		for (int i = 0; i < 3; i++) {
			m_resistances[i] = dstream.readInt();
		}
		hitpoints = dstream.readInt();
		m_name = ParserHelper.LoadString(dstream);
		slottedWidget = dstream.readBoolean();
	}

	@Override
	public int applyEffect(Effect effect, Actor origin, boolean critical) {
		// TODO Auto-generated method stub
		if (Effect_Damage.class.isInstance(effect)) {
			Effect_Damage dmg = (Effect_Damage) effect;
			// get bonus from originator
			if (dmg.getDamageType() > Actor_RPG.SHOCK) {
				return 0;
			}
			int bonus = 0;
			if (dmg.getModifierAbility() != -1) {
				bonus = origin.getRPG().getAbilityMod(dmg.getModifierAbility());
			}
			// roll damage
			int damage = Universe.m_random.nextInt(dmg.getMaxValue() - dmg.getMinValue()) + dmg.getMinValue();
			// reduce damage by damage resistance
			damage -= m_resistances[dmg.getDamageType()] + bonus;
			if (damage < 0) {
				damage = 0;
			}
			if (damage>999)
			{
				origin.Defeat(null, false);
				return 0;
			}
			// check if physical harm
			if (dmg.getDamageType() <= Actor_RPG.SHOCK) {
				// if so apply to hp
				hitpoints -= damage;
				if (hitpoints <= 0) {
					destroy();
				}
			}

			return damage;
		}
		return 0;
	}

	@Override
	public int getAttribute(int defAttribute) {
		return -20;
	}

}
