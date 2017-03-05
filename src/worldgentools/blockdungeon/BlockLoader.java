package worldgentools.blockdungeon;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shared.ParserHelper;

public class BlockLoader {

	public static Collection<? extends StandardBlock> load(String filename) {
		ArrayList <StandardBlock> blocks=new ArrayList<StandardBlock>();
		Document doc=ParserHelper.LoadXML("assets/data/dungeon/blocks/"+filename+".xml");
		Element root=(Element)doc.getFirstChild();
		NodeList children=root.getChildNodes();
			
			for (int i=0;i<children.getLength();i++)
			{
				Node Nnode=children.item(i);
				if (Nnode.getNodeType()==Node.ELEMENT_NODE)
				{
					Element Enode=(Element)Nnode;
					//run each step successively
					if (Enode.getTagName()=="block")
					{
						blocks.add(new StandardBlock(Enode));
					}
				}
			}
		
		return blocks;
	}

}
