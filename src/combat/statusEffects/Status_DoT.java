package combat.statusEffects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.Actor;
import actorRPG.Actor_RPG;
import actorRPG.RPG_Helper;
import nomad.Universe;
import shared.ParserHelper;
import view.ViewScene;
import vmo.GameManager;
import zone.Zone;

public class Status_DoT implements StatusEffect {

	int clock;
	int uid;
	int spriteIcon;
	int duration;
	int strength;
	String tag;
	String affectText;
	int abilityTest;
	
	String removeText;
	AttribMod []modifiers;
	Actor origin;
	
	public Status_DoT()
	{
		
	}
	
	public Status_DoT(Element e)
	{
		uid=Integer.parseInt(e.getAttribute("uid"));
		spriteIcon=Integer.parseInt(e.getAttribute("icon"));
		
		if (e.getAttribute("duration").length()>0)
		{
			duration=Integer.parseInt(e.getAttribute("duration"));	
		}
		int count=Integer.parseInt(e.getAttribute("numModifiers"));
		modifiers=new AttribMod[count];
		NodeList children=e.getChildNodes();
		int index=0;
		for (int i=0;i<children.getLength();i++)
		{
			Node node=children.item(i);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)node;
				if (Enode.getTagName()=="removeText")
				{
					removeText=Enode.getTextContent();
				}
				if (Enode.getTagName()=="affectText")
				{
					affectText=Enode.getTextContent();
				}
				if (Enode.getTagName()=="effect")
				{
					modifiers[index]=new AttribMod();
					modifiers[index].attribute=RPG_Helper.statFromString(Enode.getAttribute("stat"));
					modifiers[index].modifier=Integer.parseInt(Enode.getAttribute("modifier"));
					index++;
				}
				if (Enode.getTagName()=="resist")
				{
					strength=Integer.parseInt(Enode.getAttribute("strength"));
					abilityTest=RPG_Helper.abilityFromString(Enode.getAttribute("test"));
					tag=Enode.getAttribute("tag");
				}
			}
		}		
	}
	
	@Override
	public void load(DataInputStream dstream) throws IOException {
		uid=dstream.readInt();
		clock=dstream.readInt();
		spriteIcon=dstream.readInt();
		duration=dstream.readInt();
		strength=dstream.readInt();
		abilityTest=dstream.readInt();
		int c=dstream.readInt();
		modifiers=new AttribMod[c];
		for (int i=0;i<c;i++)
		{
			modifiers[i]=new AttribMod();
			modifiers[i].load(dstream);
		}
		removeText=ParserHelper.LoadString(dstream);
		tag=ParserHelper.LoadString(dstream);
		affectText=ParserHelper.LoadString(dstream);
		int uid=dstream.readInt();
		if (uid!=-1)
		{
			recoverOrigin(uid);
		}
		
	}

	private void recoverOrigin(int uid)
	{
		List<Actor> actors=Universe.getInstance().getCurrentZone().getActors();
		for (int i=0;i<actors.size();i++)
		{
			if (actors.get(i)==origin)
			{
				origin=actors.get(i);
				break;
			}
		}
	}
	
	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(6);
		dstream.writeInt(uid);
		dstream.writeInt(clock);
		dstream.writeInt(spriteIcon);
		dstream.writeInt(duration);
		dstream.writeInt(strength);
		dstream.writeInt(abilityTest);
		dstream.writeInt(modifiers.length);
		for (int i=0;i<modifiers.length;i++)
		{
			modifiers[i].save(dstream);
		}
		ParserHelper.SaveString(dstream,removeText);
		ParserHelper.SaveString(dstream, tag);
		ParserHelper.SaveString(dstream, affectText);
		if (origin==null)
		{
			dstream.writeInt(-1);
		}
		else
		{
			dstream.writeInt(origin.getUID());	
		}

	}

	@Override
	public void apply(Actor_RPG subject) {
		

	}
	
	private boolean zoneCheck()
	{
		
		Zone z=Universe.getInstance().getCurrentZone();
		for (int i=0;i<z.getActors().size();i++)
		{
			if (z.getActors().get(i)==origin)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void update(Actor_RPG subject) {
		
		clock++;
		if (clock>5)
		{
			clock=0;
			
			subject.ReduceStat(modifiers[0].attribute, modifiers[0].modifier);
			
			if (subject.getActor().getVisible()==true && ViewScene.m_interface!=null)
			{
				String str=affectText.replace("VALUE", Integer.toString(modifiers[0].modifier));
				ViewScene.m_interface.DrawText(str.replace("TARGET", subject.getName()));
			}
			
			if (origin!=null)
			{
				if (subject.getStat(Actor_RPG.HEALTH)<=0 && zoneCheck())
				{
					subject.getActor().Defeat(origin, false);
					return;
				}
				if (subject.getStat(Actor_RPG.RESOLVE)<=0 && zoneCheck())
				{
					subject.getActor().Defeat(origin, true);
					return;
				}		
			}
			int m=subject.getAbilityMod(abilityTest);
			int r=GameManager.m_random.nextInt(20)+m;
			if (r>strength)
			{
				duration=0;
			}
			
		}
	
	}

	@Override
	public void remove(Actor_RPG subject) {
		
		if (ViewScene.m_interface!=null)
		{
			ViewScene.m_interface.DrawText(removeText.replace("TARGET",subject.getName()));
		}
	}

	@Override
	public boolean maintain() {
		duration--;
		if (duration<1)
		{
			return true;
		}
		return false;
	}

	@Override
	public StatusEffect cloneEffect() {
		Status_DoT status=new Status_DoT();
		status.duration=this.duration;
		status.removeText=this.removeText;
		status.spriteIcon=this.spriteIcon;
		status.uid=this.uid;
		status.strength=strength;
		status.tag=tag;
		status.abilityTest=abilityTest;
		status.modifiers=new AttribMod[modifiers.length];
		status.affectText=affectText;
		for (int i=0;i<modifiers.length;i++)
		{
			status.modifiers[i]=modifiers[i];
		}
		return status;
	}

	@Override
	public int getStatusIcon() {
		
		return spriteIcon;
	}

	@Override
	public int getUID() {

		return uid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 2;
		result = prime * result + uid;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Status_DoT other = (Status_DoT) obj;
		if (uid != other.uid)
			return false;
		return true;
	}

	@Override
	public void setOrigin(Actor actor)
	{
		origin=actor;
	}
}
