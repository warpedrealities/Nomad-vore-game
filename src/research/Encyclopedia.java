package research;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nomad.universe.Universe;
import shared.ParserHelper;

public class Encyclopedia {

	Map<String, Data> dataList;
	Map<String, Research> researchList;
	List<Entry> entryList;

	public Encyclopedia() {
		dataList = new HashMap<String, Data>();
		researchList = new HashMap<String, Research>();
		entryList = new ArrayList<Entry>();

	}

	public void save(DataOutputStream dstream) throws IOException {
		Set<String> keySet = dataList.keySet();

		Iterator<String> it = keySet.iterator();

		dstream.writeInt(keySet.size());
		while (it.hasNext()) {
			String str = it.next();
			ParserHelper.SaveString(dstream, str);
			Data d = dataList.get(str);
			d.save(dstream);
		}

		keySet = researchList.keySet();
		it = keySet.iterator();

		dstream.writeInt(keySet.size());
		while (it.hasNext()) {
			String str = it.next();
			ParserHelper.SaveString(dstream, str);
			Research r = researchList.get(str);
			r.save(dstream);
		}

	}

	public void load(DataInputStream dstream) throws IOException {
		int count = dstream.readInt();

		for (int i = 0; i < count; i++) {
			String str = ParserHelper.LoadString(dstream);
			Data d = new Data();
			d.load(dstream);
			dataList.put(str, d);
		}

		count = dstream.readInt();

		for (int i = 0; i < count; i++) {
			String str = ParserHelper.LoadString(dstream);
			Research r = new Research();
			r.load(dstream);
			researchList.put(str, r);
		}

		generateEntries();
	}

	public boolean addData(String name, String group) {
		Data d = dataList.get(name);
		if (d != null) {
			d.incrementCount();
		} else {
			if (group != null) {
				dataList.put(group, new Data(group));
			}
			dataList.put(name, new Data(name));
		}

		int count = 0;
		for (int i = 0; i < entryList.size(); i++) {
			if (entryList.get(i).isUnlocked() == false) {
				if (entryList.get(i).checkUnlock(dataList)) {
					entryList.get(i).runRewards(Universe.getInstance().getPlayer());
				}
				count++;
			}
		}
		if (count > 0) {
			return true;
		}
		return false;
	}

	public void generateEntries() {
		File file = new File("assets/data/encyclopedia");

		// find the names of all files in the item folder
		File[] files = file.listFiles();
		// use reader to generate items

		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().contains(".svn") == false) {
				if (files[i].isDirectory() == true) {
					subLoad(files[i].getName());
				} else {
					Entry entry = new Entry(files[i].getName());
					entryList.add(entry);
					entry.checkUnlock(dataList);
				}
			}
		}

		Collections.sort(entryList);
	}

	private void subLoad(String filename) {
		File file = new File("assets/data/encyclopedia" + "/" + filename);
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().contains(".svn") == false) {
				if (files[i].isDirectory() == true) {
					subLoad(filename + "/" + files[i].getName());
				} else {
					Entry entry = new Entry(filename + "/" + files[i].getName());
					entryList.add(entry);
					entry.checkUnlock(dataList);
				}

			}
		}

	}

	public boolean hasEntry(String find) {

		for (int i = 0; i < entryList.size(); i++) {
			if (entryList.get(i).getFilename().equals(find) && entryList.get(i).isUnlocked()) {
				return true;

			}
		}
		return false;
	}

	public Map<String, Data> getDataList() {
		return dataList;
	}

	public Map<String, Research> getResearchList() {
		return researchList;
	}

	public List<Entry> getEntryList() {
		return entryList;
	}

	public void addResearch(String data, int DC, int roll, String group) {
		if (researchList.get(data) == null) {
			Research d = new Research(DC, roll, data);
			d.setGroup(group);
			researchList.put(data, d);
		}
	}

	public boolean hasResearch(String find) {
		return researchList.get(find) != null;

	}

	public boolean hasData(String find) {

		if (dataList.get(find) != null) {
			return true;
		}

		return false;
	}

}
