package crafting;

import item.Item;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import shared.ParserHelper;

public class CraftingLibrary {

	Map<String, CraftingRecipe> craftables;

	public CraftingLibrary() {
		craftables = new HashMap<String, CraftingRecipe>();

	}

	public void load() {

		File file = new File("assets/data/recipes");

		// find the names of all files in the item folder
		File[] files = file.listFiles();
		// use reader to generate items
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().contains(".svn") == false) {
				String str = files[i].getName();
				craftables.put(files[i].getName(), reader(files[i].getName()));
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
			if (craftables.get(str) != null) {
				craftables.get(str).setUnlocked(dstream.readBoolean());
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

	public void unlockRecipe(String name) {
		craftables.get(name + ".xml").setUnlocked(true);
	}
}
