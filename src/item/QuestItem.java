package item;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import shared.ParserHelper;

public class QuestItem extends Item {
	
	public QuestItem() {
		m_use = ItemUse.NONE;	
	}
	

	public QuestItem(String filename) {
		loadFromFile(filename);
	}

	public void loadFromFile(String filename) {
		
		Document doc = ParserHelper.LoadXML("assets/data/questItems/" + filename +".xml");
		Element n = (Element) doc.getFirstChild();
		Element Enode = (Element) n;
		m_use = ItemUse.NONE;
		m_name = Enode.getAttribute("name");
		m_weight = Float.parseFloat(Enode.getAttribute("weight"));
		itemValue = Float.parseFloat(Enode.getAttribute("value"));		
		m_description = Enode.getTextContent().replace("\n", "");	
		if (Enode.getAttribute("tag").length()>0)
		{
			tag=Integer.parseInt(Enode.getAttribute("tag"));
		}		
		
	}
	
	public void save(DataOutputStream dstream) throws IOException {

		dstream.write(9);
		ParserHelper.SaveString(dstream, m_name);
		ParserHelper.SaveString(dstream, m_description);
		dstream.writeFloat(m_weight);
		dstream.writeFloat(itemValue);
	}	
	
	public void load(DataInputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		m_name=ParserHelper.LoadString(dstream);
		m_description=ParserHelper.LoadString(dstream);
		m_weight=dstream.readFloat();
		itemValue=dstream.readFloat();
	}
	@Override
	public boolean canStack() {
		return false;
	}
}
