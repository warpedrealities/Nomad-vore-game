package shipsystem.weapon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import shared.ParserHelper;
import shipsystem.ShipAbility;

public class ShipWeapon extends ShipAbility {

	private String description, name, effectScript, effectSheet;
	private int tracking, cooldown, volley = 1;
	private List<ShipWeaponEffect> effects;
	private List<WeaponCost> weaponCosts;
	private float firingArc, rangePenalty, maxRange, falloff;

	public ShipWeapon(Element enode, String m_name) {
		abilityType = AbilityType.SA_WEAPON;
		weaponCosts = new ArrayList<WeaponCost>();
		NodeList children = enode.getChildNodes();
		effects = new ArrayList<>();
		name = m_name;
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) children.item(i);
				if (e.getTagName().contains("Effect")) {
					effects.add(WeaponEffectLoader.readEffect(e));
				}
				if (e.getAttribute("volley").length() > 0) {
					volley = Integer.parseInt(e.getAttribute("volley"));
				}
				if (e.getTagName().equals("cooldown")) {
					cooldown = Integer.parseInt(e.getAttribute("value"));
				}
				if (e.getTagName().equals("effects")) {
					effectScript = e.getAttribute("script");
					effectSheet = e.getAttribute("sheet");
				}
				if (e.getTagName().equals("description")) {
					description = e.getTextContent();
				}
				if (e.getTagName().equals("special")) {
					if (e.getAttribute("tracking").length() > 0) {
						tracking = Integer.parseInt(e.getAttribute("tracking"));
					}

				}
				if (e.getTagName().equals("range")) {
					firingArc = Float.parseFloat(e.getAttribute("arc"));
					maxRange = Float.parseFloat(e.getAttribute("range"));
					if (e.getAttribute("rangePenalty").length() > 0) {
						rangePenalty = Float.parseFloat(e.getAttribute("rangePenalty"));
					}
				}
				if (e.getTagName().equals("weaponCost")) {
					weaponCosts.add(new WeaponCost(e));
				}
			}
		}
	}

	public ShipWeapon(DataInputStream dstream, String m_name) throws IOException {
		abilityType = AbilityType.SA_WEAPON;
		name = ParserHelper.LoadString(dstream);
		description = ParserHelper.LoadString(dstream);
		effectScript = ParserHelper.LoadString(dstream);
		effectSheet = ParserHelper.LoadString(dstream);

		tracking = dstream.readInt();

		cooldown = dstream.readInt();
		volley = dstream.readInt();

		weaponCosts = new ArrayList<WeaponCost>();
		int c = dstream.readInt();
		for (int i = 0; i < c; i++) {
			weaponCosts.add(new WeaponCost(dstream));
		}

		tracking = dstream.readInt();
		rangePenalty = dstream.readFloat();
		maxRange = dstream.readFloat();

		c = dstream.readInt();
		effects = new ArrayList<>(c);
		for (int i = 0; i < c; i++) {
			effects.add(WeaponEffectLoader.loadEffect(dstream));
		}
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		ParserHelper.SaveString(dstream, name);
		ParserHelper.SaveString(dstream, description);
		ParserHelper.SaveString(dstream, effectScript);
		ParserHelper.SaveString(dstream, effectSheet);

		dstream.writeInt(tracking);

		dstream.writeInt(cooldown);
		dstream.writeInt(volley);

		dstream.writeInt(weaponCosts.size());
		for (int i = 0; i < weaponCosts.size(); i++) {
			weaponCosts.get(i).save(dstream);
		}

		dstream.writeFloat(firingArc);
		dstream.writeFloat(rangePenalty);
		dstream.writeFloat(maxRange);

		dstream.writeInt(effects.size());
		for (int i = 0; i < effects.size(); i++) {
			dstream.writeInt(effects.get(i).getType());
			effects.get(i).save(dstream);
		}
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String getEffectScript() {
		return effectScript;
	}

	public String getEffectSheet() {
		return effectSheet;
	}

	public int getTracking() {
		return tracking;
	}

	public int getCooldown() {
		return cooldown;
	}

	public List<WeaponCost> getWeaponCosts() {
		return weaponCosts;
	}

	public float getFiringArc() {
		return firingArc;
	}

	public float getRangePenalty() {
		return rangePenalty;
	}

	public float getMaxRange() {
		return maxRange;
	}

	public int getVolley() {
		return volley;
	}

	public List<ShipWeaponEffect> getEffects() {
		return effects;
	}

}
