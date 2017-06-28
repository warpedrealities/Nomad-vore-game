package combat.effect;

import nomad.Universe;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import view.ViewScene;

import combat.statusEffects.StatusEffect;
import combat.statusEffects.Status_AttribMod;
import combat.statusEffects.Status_Bind;
import combat.statusEffects.Status_Defence;
import combat.statusEffects.Status_DoT;
import combat.statusEffects.Status_Stealth;
import combat.statusEffects.Status_Stun;
import combat.statusEffects.Status_SubAbilityMod;
import actor.Actor;
import actorRPG.RPG_Helper;

public class Effect_Status extends Effect {

	public class ProportionalEffect {
		int stat;
		float proportion;

		public ProportionalEffect(Element element) {
			stat = RPG_Helper.statFromString(element.getAttribute("stat"));
			proportion = Float.parseFloat(element.getAttribute("proportion"));
		}

		public int getStat() {
			return stat;
		}

		public float getProportion() {
			return proportion;
		}

	}

	ProportionalEffect proportionalEffect;

	int probability;
	int probabilityModifier = -1;
	float rangedDecay = 0;
	StatusEffect effect;
	String applyText;
	String statusTag;
	boolean inverseTag;
	boolean replaceStatus;

	public Effect_Status(Element element) {
		probability = Integer.parseInt(element.getAttribute("probability"));

		if (element.getAttribute("rangedDecay").length() > 0) {
			rangedDecay = Float.parseFloat(element.getAttribute("rangedDecay"));
		}
		if (element.getAttribute("replacestatus").equals("true")) {
			replaceStatus = true;
		}

		NodeList list = element.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) list.item(i);
				String str = e.getTagName();
				if (e.getTagName().equals("statusTag")) {
					statusTag = e.getAttribute("tag");
					if (e.getAttribute("inverse").length() > 0) {
						inverseTag = true;
					}
				}
				if (e.getTagName().equals("proportionalEffect")) {
					proportionalEffect = new ProportionalEffect(e);
				}
				if (e.getTagName().equals("probabilityModifier")) {
					probabilityModifier = RPG_Helper.abilityFromString(e.getAttribute("value"));
				}
				if (e.getTagName().equals("applyText")) {
					applyText = e.getTextContent();
				}
				if (e.getTagName().equals("statusAttribMod")) {
					effect = new Status_AttribMod(e);
				}
				if (e.getTagName().equals("statusStun")) {
					effect = new Status_Stun(e);
				}
				if (e.getTagName().equals("statusBind")) {
					effect = new Status_Bind(e);
				}
				if (e.getTagName().equals("statusSubAbilityMod")) {
					effect = new Status_SubAbilityMod(e);
				}
				if (e.getTagName().equals("statusStealth")) {
					effect = new Status_Stealth(e);
				}
				if (e.getTagName().equals("statusDefence")) {
					effect = new Status_Defence(e);
				}
				if (e.getTagName().equals("statusDoT")) {
					effect = new Status_DoT(e);
				}
			}
		}
	}

	public Effect_Status() {

	}

	@Override
	public int applyEffect(Actor origin, Actor target, boolean critical) {
		// check tag
		if (statusTag != null) {
			if (target.getRPG().getTagged(statusTag) && inverseTag == false) {
				return 0;
			}
			if (!target.getRPG().getTagged(statusTag) && inverseTag == true) {
				return 0;
			}
		}

		int modprob = probability;
		if (probabilityModifier != -1) {
			int mod = (target.getRPG().getAbility(probabilityModifier) - 5) * 15;
		}
		// probability
		if (modprob >= 100) {
			// apply
			StatusEffect clone = effect.cloneEffect();

			float d = origin.getPosition().getDistance(target.getPosition());
			if (d > 2 && rangedDecay > 0) {
				float decay = d * rangedDecay;
				clone.modifyStrength(decay, false);

				if (!clone.isEffective()) {
					return 0;
				}

			}

			if (proportionalEffect != null) {

				float m = target.getRPG().getStatMax(proportionalEffect.stat);
				float v = target.getRPG().getStat(proportionalEffect.stat);
				float p = v / m;
				float fr = 1 - proportionalEffect.proportion;
				clone.modifyStrength((p * proportionalEffect.proportion) + fr, true);

			}

			if (Status_Bind.class.isInstance(clone)) {
				Status_Bind bind = (Status_Bind) clone;
				bind.setOrigin(origin);
			}
			boolean b = false;
			if (target.getRPG().hasStatus(clone.getUID())) {
				b = true;
			}
			if (target.getRPG().applyStatus(clone, replaceStatus)) {
				clone.setOrigin(origin);
				if (applyText.length() > 0 && !b) {
					// write apply text
					if (applyText.contains("TARGET")) {
						ViewScene.m_interface.DrawText(applyText.replace("TARGET", target.getName()));
					} else {
						ViewScene.m_interface.DrawText(applyText);
					}
				}
			}

			return 0;
		} else {
			int r = Universe.m_random.nextInt(100);
			if (r < modprob) {
				StatusEffect clone = effect.cloneEffect();
				clone.setOrigin(origin);
				if (Status_Bind.class.isInstance(clone)) {
					Status_Bind bind = (Status_Bind) clone;

				}
				// apply
				if (target.getRPG().applyStatus(clone, replaceStatus)) {
					ViewScene.m_interface.DrawText(applyText.replace("TARGET", target.getName()));
				}
				return 0;
			}

		}
		return 0;
	}

	public StatusEffect getEffect() {
		return effect;
	}

	@Override
	public Effect clone() {
		Effect_Status statusEffect = new Effect_Status();
		statusEffect.proportionalEffect = proportionalEffect;
		statusEffect.probability = probability;
		statusEffect.probabilityModifier = probabilityModifier;
		statusEffect.rangedDecay = rangedDecay;
		statusEffect.replaceStatus = replaceStatus;
		statusEffect.effect = effect.cloneEffect();

		statusEffect.applyText = applyText;
		statusEffect.statusTag = statusTag;
		statusEffect.inverseTag = inverseTag;

		return statusEffect;
	}

	@Override
	public void applyChange(Effect effect,int rank) {
		// TODO Auto-generated method stub

	}

}
