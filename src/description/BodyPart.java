package description;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shared.ParserHelper;

public class BodyPart {

	Map<String,Integer> partValues;
	String partName;
	
	public BodyPart(String filename)
	{
		
		partValues=new HashMap<String,Integer>();
		Document doc=ParserHelper.LoadXML("assets/data/description/bodyparts/"+filename+".xml");
		Element root=doc.getDocumentElement();
	    Element node=(Element)doc.getFirstChild();
	    partName=node.getAttribute("name");
		NodeList children=node.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
		
			Node N=children.item(i);
			if (N.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)N;
				//run each step successively
				if (Enode.getTagName()=="variable")
				{
					partValues.put(Enode.getAttribute("name"), Integer.parseInt(Enode.getAttribute("value")));
				}
			}
		}
	}

	public BodyPart(DataInputStream dstream) throws IOException
	{
		partValues=new HashMap<String,Integer>();
		partName=ParserHelper.LoadString(dstream);
		int count=dstream.readInt();
		for (int i=0;i<count;i++)
		{
			String str=ParserHelper.LoadString(dstream);
			int v=dstream.readInt();
			partValues.put(str, v);	
		}
		
	}
	
	public void save(DataOutputStream dstream) throws IOException
	{
		ParserHelper.SaveString(dstream, partName);
		Set<String> set=partValues.keySet();
		dstream.writeInt(set.size());
		Iterator<String>it=set.iterator();
		
		while (it.hasNext())
		{
			String str=it.next();
			int v=partValues.get(str);
			ParserHelper.SaveString(dstream, str);
			dstream.writeInt(v);
		}
		
	}

	public String getPartName() {
		return partName;
	}
	
	public int getValue(String value)
	{
		Integer v=partValues.get(value);
		if (v==null)
		{
			return 0;
		}
		return v;
	}
	
	public void modValue(String id, int mod)
	{
		Integer v=partValues.get(id);
		if (v!=null)
		{
			partValues.put(id, v+mod);
		}
	}
	public void setValue(String id, int n)
	{
		Integer v=partValues.get(id);
		if (v!=null)
		{
			partValues.put(id, n);
		}
	}
}
