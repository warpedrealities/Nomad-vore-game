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
import shipsystem.ShipAbility.AbilityType;

public class ShipWeapon extends ShipAbility {

	private String description, name, effectScript,effectSheet;
	private int minDamage,maxDamage,tracking,disruption,penetration,cooldown,volley=1;
	private List <WeaponCost> weaponCosts;
	private float firingArc,falloff,rangePenalty,maxRange;
	
	
	public ShipWeapon(Element enode, String m_name) {
		abilityType=AbilityType.SA_WEAPON;
		weaponCosts=new ArrayList<WeaponCost>();
		NodeList children=enode.getChildNodes();
		name=m_name;
		for (int i=0;i<children.getLength();i++)
		{
			if (children.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element e=(Element)children.item(i);
				if (e.getTagName().equals("damage"))
				{
					minDamage=Integer.parseInt(e.getAttribute("min"));
					maxDamage=Integer.parseInt(e.getAttribute("max"));
					if (e.getAttribute("falloff").length()>0)
					{
						falloff=Float.parseFloat(e.getAttribute("falloff"));
					}
					if (e.getAttribute("volley").length()>0)
					{
						volley=Integer.parseInt(e.getAttribute("volley"));
					}
				}
				if (e.getTagName().equals("cooldown"))
				{
					cooldown=Integer.parseInt(e.getAttribute("value"));
				}
				if (e.getTagName().equals("effects"))
				{
					effectScript=e.getAttribute("script");
					effectSheet=e.getAttribute("sheet");
				}
				if (e.getTagName().equals("description"))
				{
					description=e.getTextContent();
				}
				if (e.getTagName().equals("special"))
				{
					if (e.getAttribute("tracking").length()>0)
					{
						tracking=Integer.parseInt(e.getAttribute("tracking"));
					}
					if (e.getAttribute("penetration").length()>0)
					{
						penetration=Integer.parseInt(e.getAttribute("penetration"));
					}
					if (e.getAttribute("disruption").length()>0)
					{
						disruption=Integer.parseInt(e.getAttribute("disruption"));
					}
				}
				if (e.getTagName().equals("range"))
				{
					firingArc=Float.parseFloat(e.getAttribute("arc"));
					maxRange=Float.parseFloat(e.getAttribute("range"));
					if (e.getAttribute("rangePenalty").length()>0)
					{
						rangePenalty=Float.parseFloat(e.getAttribute("rangePenalty"));
					}
				}
				if (e.getTagName().equals("weaponCost"))
				{
					weaponCosts.add(new WeaponCost(e));
				}	
			}
		}
	}

	public ShipWeapon(DataInputStream dstream, String m_name) throws IOException {
		abilityType=AbilityType.SA_WEAPON;
		name=ParserHelper.LoadString(dstream);
		description=ParserHelper.LoadString(dstream);
		effectScript=ParserHelper.LoadString(dstream);
		effectSheet=ParserHelper.LoadString(dstream);
		
		minDamage=dstream.readInt();
		maxDamage=dstream.readInt();
		tracking=dstream.readInt();
		disruption=dstream.readInt();
		penetration=dstream.readInt();
		cooldown=dstream.readInt();
		volley=dstream.readInt();
		
		weaponCosts=new ArrayList<WeaponCost>();
		int c=dstream.readInt();
		for (int i=0;i<c;i++)
		{
			weaponCosts.add(new WeaponCost(dstream));
		}
		
		firingArc=dstream.readFloat();
		falloff=dstream.readFloat();
		rangePenalty=dstream.readFloat();
		maxRange=dstream.readFloat();
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		ParserHelper.SaveString(dstream, name);
		ParserHelper.SaveString(dstream, description);
		ParserHelper.SaveString(dstream, effectScript);
		ParserHelper.SaveString(dstream, effectSheet);
		
		dstream.writeInt(minDamage);
		dstream.writeInt(maxDamage);
		dstream.writeInt(tracking);
		dstream.writeInt(disruption);
		dstream.writeInt(penetration);
		dstream.writeInt(cooldown);
		dstream.writeInt(volley);
		
		dstream.writeInt(weaponCosts.size());
		for (int i=0;i<weaponCosts.size();i++)
		{
			weaponCosts.get(i).save(dstream);
		}
		
		dstream.writeFloat(firingArc);
		dstream.writeFloat(falloff);
		dstream.writeFloat(rangePenalty);
		dstream.writeFloat(maxRange);
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

	public int getMinDamage() {
		return minDamage;
	}

	public int getMaxDamage() {
		return maxDamage;
	}

	public int getTracking() {
		return tracking;
	}

	public int getDisruption() {
		return disruption;
	}

	public int getPenetration() {
		return penetration;
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

	public float getFalloff() {
		return falloff;
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

	
	
}
