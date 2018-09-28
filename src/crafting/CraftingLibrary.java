package crafting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import shared.ParserHelper;

public class CraftingLibrary {

	Map<String, CraftingRecipe> craftables;
	List <CraftingRecipe> sortedCraftables;
	
	public CraftingLibrary() {
		craftables = new HashMap<String, CraftingRecipe>();
		sortedCraftables=new ArrayList<CraftingRecipe>();
	}

	public void load() {

		File file = new File("assets/data/recipes");

		// find the names of all files in the item folder
		File[] files = file.listFiles();
		// use reader to generate items
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().contains(".svn") == false) {
				String str = files[i].getName();
				CraftingRecipe recipe=reader(files[i].getName());
				craftables.put(files[i].getName(),recipe );
				if (recipe.getUnlocked())
				{
					sortedCraftables.add(recipe);
				}
			}
		}
		

	}

	private CraftingRecipe reader(String name) {

		return new CraftingRecipe("assets/data/recipes/" + name);
	}

	public void load(DataInputStream dstream) throws IOException {
		load();
		int count = dstream.readInt();
		for (int i = 0; i < count; i++) {
			String str = ParserHelper.LoadString(dstream);
			CraftingRecipe r=craftables.get(str);
			if  (r!= null) {
				boolean b=dstream.readBoolean();
				if (b && !r.getUnlocked())
				{

					sortedCraftables.add(r);
		
				}
				r.setUnlocked(b);
			}
			else
			{
				dstream.readBoolean();
			}
		}

	}

	public void save(DataOutputStream dstream) throws IOException {
		Set<String> keys = craftables.keySet();
		Iterator<String> keyiterator = keys.iterator();
		dstream.writeInt(keys.size());
		while (keyiterator.hasNext()) {
			String name = keyiterator.next();
			ParserHelper.SaveString(dstream, name);
			dstream.writeBoolean(craftables.get(name).getUnlocked());

		}

	}

	public CraftingRecipe getRecipe(String name)
	{
		return craftables.get(name);
	}
	
	public Collection<CraftingRecipe> getCraftables() {
		return craftables.values();
	}
	public List<CraftingRecipe> getSortedCraftables() {
		sortedCraftables.sort(null);
		return sortedCraftables;
	}

	public void unlockRecipe(String name) {
		CraftingRecipe r=craftables.get(name + ".xml");
		if (!r.getUnlocked())
		{
			if (r.getAttachedRecipe()!=null)
			{
				unlockRecipe(r.getAttachedRecipe());
			}
			sortedCraftables.add(r);
		}
		r.setUnlocked(true);
	}
}
