package crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actorRPG.player.Player_RPG;
import item.Item;
import nomad.universe.Universe;
import shared.ParserHelper;

public class CraftingRecipe implements Comparable<Object> {

	private String name;

	private boolean unlocked;
	private String attachedRecipe;
	private int requiredSkill;
	private int requiredScience=-1;
	
	private ArrayList<CraftingIngredient> ingredients;
	private ArrayList<CraftingIngredient> tokenRequirements;
	private Item result;

	private int resultCount = 1;

	private String description;

	public CraftingRecipe(String name) {
		Document doc = ParserHelper.LoadXML(name);
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		Element Enode = (Element) n;
		loadFromFile(Enode);
	}

	private void loadFromFile(Element enode) {

		ingredients = new ArrayList<CraftingIngredient>();
		tokenRequirements=new ArrayList<CraftingIngredient>();
		name = enode.getAttribute("name");
		unlocked = Boolean.parseBoolean(enode.getAttribute("startunlocked"));
		requiredSkill = Integer.parseInt(enode.getAttribute("requiredskill"));
		NodeList children = enode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				if (Enode.getTagName() == "description") {
					description = Enode.getTextContent();
				}
				if (Enode.getTagName() == "result") {
					result = Universe.getInstance().getLibrary().getItem(Enode.getAttribute("item"));
					if (Enode.getAttribute("count").length() > 0) {
						resultCount = Integer.parseInt(Enode.getAttribute("count"));
					}
				}
				if (Enode.getTagName() == "ingredient") {
					ingredients.add(new CraftingIngredient(Enode));
				}
				if (Enode.getTagName() == "tokenRequirement") {
					tokenRequirements.add(new CraftingIngredient(Enode));
				}
				if (Enode.getTagName() == "scienceRequirement") {
					requiredScience=Integer.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName().equals("attachedRecipe"))
				{
					attachedRecipe=Enode.getAttribute("value");
				}
			}
		}
	}

	public boolean getUnlocked() {
		return unlocked;
	}

	public void setUnlocked(boolean unlocked) {
		this.unlocked = unlocked;
	}

	public String getName() {
		return name;
	}

	public int getRequiredSkill() {
		return requiredSkill;
	}

	public int getRequiredScience() {
		return requiredScience;
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

	@Override
	public int compareTo(Object o) {
		CraftingRecipe r=(CraftingRecipe)o;
		return requiredSkill-r.getRequiredSkill();
	}

	
	
	public String getAttachedRecipe() {
		return attachedRecipe;
	}

	public ArrayList<CraftingIngredient> getTokenRequirements() {
		return tokenRequirements;
	}

	public List<CraftingIngredient> getUnmetRequirements(Player_RPG rpg)
	{
		if (tokenRequirements.size()==0)
		{
			return null;
		}
		List <CraftingIngredient> l=new ArrayList<CraftingIngredient>();
		for (int i=0;i<tokenRequirements.size();i++)
		{
			int token=rpg.getCraftingTokenCount(tokenRequirements.get(i).getName());
			if (token<tokenRequirements.get(i).getQuantity())
			{
				l.add(tokenRequirements.get(i));
			}
			
		}
		
		if (l.size()>0)
		{
			return l;
		}
		return null;
	}

	public List<CraftingIngredient> getUnmetRequirements(Map<String, Integer> map) {
		if (tokenRequirements.size()==0)
		{
			return null;
		}
		List <CraftingIngredient> l=new ArrayList<CraftingIngredient>();
		for (int i=0;i<tokenRequirements.size();i++)
		{
			
			Integer token=map.get(tokenRequirements.get(i).getName());
			if (token==null)
			{
				token=0;
			}
			if (token<tokenRequirements.get(i).getQuantity())
			{
				l.add(tokenRequirements.get(i));
			}
			
		}		
		if (l.size()>0)
		{
			return l;
		}
		return null;
	}
}
