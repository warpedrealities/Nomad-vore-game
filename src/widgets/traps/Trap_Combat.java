package widgets.traps;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import actor.Actor;
import actorRPG.Actor_RPG;
import actorRPG.RPG_Helper;
import shared.ParserHelper;
import view.ViewScene;

public class Trap_Combat implements Trap_Effect {

	int damageAmount;
	int damageType;
	String description;

	public Trap_Combat(Element enode) {
		NodeList n = enode.getElementsByTagName("damage");
		Element e = (Element) n.item(0);
		damageAmount = Integer.parseInt(e.getAttribute("amount"));
		damageType = RPG_Helper.AttributefromString(e.getAttribute("type"));
		description = e.getTextContent();
	}

	@Override
	public void trigger(Actor target) {

		int def = target.getRPG().getAttribute(damageType);
		int v = damageAmount - def;
		String str = description.replace("TARGET", target.getName());
		str = str.replace("VALUE", Integer.toString(v));
		ViewScene.m_interface.DrawText(str);
		ViewScene.m_interface.UpdateInfo();
		if (damageType > Actor_RPG.SHOCK) {
			target.getRPG().ReduceStat(Actor_RPG.RESOLVE, v);
			if (target.getRPG().getStat(Actor_RPG.RESOLVE) < 1) {
				target.getRPG().setStat(Actor_RPG.RESOLVE, 1);
			}
		} else {
			target.getRPG().ReduceStat(Actor_RPG.HEALTH, v);
			if (target.getRPG().getStat(Actor_RPG.HEALTH) < 1) {
				target.getRPG().setStat(Actor_RPG.HEALTH, 1);
			}
		}
	}

	public Trap_Combat(DataInputStream dstream) throws IOException {
		damageAmount = dstream.readInt();
		damageType = dstream.readInt();
		description = ParserHelper.LoadString(dstream);
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.writeInt(0);
		dstream.writeInt(damageAmount);
		dstream.writeInt(damageType);
		ParserHelper.SaveString(dstream, description);
	}

}
