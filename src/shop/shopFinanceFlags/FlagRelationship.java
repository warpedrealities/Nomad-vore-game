package shop.shopFinanceFlags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import faction.FactionLibrary;
import nomad.universe.Universe;
import shared.ParserHelper;

public class FlagRelationship {


	public enum RelationshipType{BUY,SELL};
	
	private int level, levelThresholds[];
	private boolean isFaction;
	private RelationshipType type;
	private String flagName;
	
	public FlagRelationship(Element e) {
		level=0;
		flagName=e.getAttribute("flag");
		type=RelationshipType.valueOf(e.getAttribute("type"));
		levelThresholds=new int[3];
		if ("true".equals(e.getAttribute("isFaction")))
		{
			isFaction=true;
		}
		if (e.getAttribute("l1").length()>1)
		{
			levelThresholds[0]=Integer.parseInt(e.getAttribute("l1"));		
		}
		if (e.getAttribute("l2").length()>1)
		{
			levelThresholds[1]=Integer.parseInt(e.getAttribute("l2"));		
		}
		if (e.getAttribute("l3").length()>1)
		{
			levelThresholds[2]=Integer.parseInt(e.getAttribute("l3"));		
		}
	}

	public FlagRelationship(DataInputStream dstream) throws IOException {
		type=RelationshipType.valueOf(ParserHelper.LoadString(dstream));
		flagName=ParserHelper.LoadString(dstream);
		isFaction=dstream.readBoolean();
		level=dstream.readInt();
		levelThresholds=new int[3];
		for (int i=0;i<3;i++)
		{
			levelThresholds[i]=dstream.readInt();
		}
	}

	public void save(DataOutputStream dstream) throws IOException {
		ParserHelper.SaveString(dstream, type.toString());
		ParserHelper.SaveString(dstream, flagName);
		dstream.writeBoolean(isFaction);
		dstream.writeInt(level);
		for (int i=0;i<3;i++) {
			dstream.writeInt(levelThresholds[i]);
		}
	}

	public RelationshipType getType() {
		return type;
	}

	public void check(String factionName, int money) {
		if (level<3)
		{
			if (levelThresholds[level]==0)
			{
				return;
			}
			if (money>levelThresholds[level])
			{
				incrementFlag(factionName);
				level++;
			}
		}
	}

	private void incrementFlag(String factionName) {
		if (isFaction)
		{
			FactionLibrary.getInstance().getFaction(factionName).getFactionFlags().incrementFlag(flagName);
		}
		else
		{
			Universe.getInstance().getPlayer().getFlags().incrementFlag(flagName);
		}
	}	
	
}
