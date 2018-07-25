package shop.shopFinanceFlags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import shared.ParserHelper;
import shop.shopFinanceFlags.FlagRelationship.RelationshipType;

public class FinanceFlags {

	private int moneyBought, moneySold;
	private List<FlagRelationship> flagRelationships;
	private String factionName;
	
	public FinanceFlags() {
		flagRelationships=new ArrayList<FlagRelationship>();		
	}
	
	public FinanceFlags(Element element) {
		factionName=element.getAttribute("faction");
		flagRelationships=new ArrayList<FlagRelationship>();
		
		NodeList children=element.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
			if (children.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element e=(Element)children.item(i);
				if (e.getTagName().equals("flagRelationship")) {
					flagRelationships.add(new FlagRelationship(e));
				}
			}
		}		
	}
	

	
	public void save(DataOutputStream dstream) throws IOException
	{
		ParserHelper.SaveString(dstream, factionName);
		dstream.writeInt(moneyBought);
		dstream.writeInt(moneySold);
		dstream.writeInt(flagRelationships.size());
		for (int i=0;i<flagRelationships.size();i++)
		{
			flagRelationships.get(i).save(dstream);
		}
		
	}
	public void load(DataInputStream dstream) throws IOException
	{
		factionName=ParserHelper.LoadString(dstream);
		moneyBought=dstream.readInt();
		moneySold=dstream.readInt();
		int c=dstream.readInt();
		for (int i=0;i<c;i++)
		{
			flagRelationships.add(new FlagRelationship(dstream));
		}
		
	}

	private void updateFlags(FlagRelationship.RelationshipType type)
	{
		for (int i=0;i<flagRelationships.size();i++)
		{
			if (flagRelationships.get(i).getType()==type)
			{
				if (type== RelationshipType.SELL)
				{
					flagRelationships.get(i).check(factionName,moneySold);
				}
				else
				{
					flagRelationships.get(i).check(factionName,moneyBought);			
				}
			
			}
		}
	}
	
	public void addSellMoney(int value) {
		moneySold+=value;
		updateFlags(RelationshipType.SELL);
	}

	public void addBuyMoney(int value) {
		moneyBought+=value;
		updateFlags(RelationshipType.BUY);
	}
}
