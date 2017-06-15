package crafting;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import nomad.Universe;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shared.ParserHelper;

import item.Item;

public class CraftingRecipe {

	private String name;
	
	private boolean unlocked;
	
	private int requiredSkill;
	
	private ArrayList<CraftingIngredient> ingredients;
	
	private Item result;
	
	private int resultCount=1;
	
	private String description;
	
	public CraftingRecipe(String name) {
		Document doc=ParserHelper.LoadXML(name);
		Element root=doc.getDocumentElement();
		Element n=(Element)doc.getFirstChild();
		Element Enode=(Element)n;
		loadFromFile(Enode);
	}

	private void loadFromFile(Element enode) {

		ingredients=new ArrayList<CraftingIngredient>();
		name=enode.getAttribute("name");
		unlocked=Boolean.parseBoolean(enode.getAttribute("startunlocked"));
		requiredSkill=Integer.parseInt(enode.getAttribute("requiredskill"));
		NodeList children=enode.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
			Node node=children.item(i);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)node;
				if (Enode.getTagName()=="description")
				{	
					description=Enode.getTextContent();
				}
				if (Enode.getTagName()=="result")
				{	
					result=Universe.getInstance().getLibrary().getItem(Enode.getAttribute("item"));
					if (Enode.getAttribute("count").length()>0)
					{
						resultCount=Integer.parseInt(Enode.getAttribute("count"));
					}
				}
				if (Enode.getTagName()=="ingredient")
				{	
					ingredients.add(new CraftingIngredient(Enode));
				}
			}
		}
	}

	public boolean getUnlocked() {
		// TODO Auto-generated method stub
		return unlocked;
	}

	public void setUnlocked(boolean unlocked)
	{
		this.unlocked=unlocked;
	}

	public String getName() {
		return name;
	}

	public int getRequiredSkill() {
		return requiredSkill;
	}

	public ArrayList<CraftingIngredient> getIngredients() {
		return ingredients;
	}

	public Item getResult() {
		return result;
	}

	public String getDescription() {
		return description;
	}

	public int getResultCount() {
		return resultCount;
	}
	
	
	
}
