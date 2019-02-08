package nomad.playerScreens.journal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JournalSystem {

	private List<JournalEntry> entryList;

	public JournalSystem() {
		entryList = new ArrayList<>();
	}

	public JournalSystem(DataInputStream dstream) throws IOException {
		entryList = new ArrayList<>();
		int c = dstream.readInt();
		for (int i = 0; i < c; i++) {
			entryList.add(new JournalEntry(dstream));
		}
	}

	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(entryList.size());
		for (int i = 0; i < entryList.size(); i++) {
			entryList.get(i).save(dstream);
		}
	}

	public List<JournalEntry> getEntryList() {
		return entryList;
	}

	public void removeEntry(int ID) {
		for (int i = 0; i < entryList.size(); i++) {
			if (entryList.get(i).getID() == ID) {
				entryList.remove(i);
				return;
			}
		}
	}

	public void addEntry(JournalEntry entry) {
		entryList.add(entry);
	}

	public void sort() {
		Collections.sort(entryList);
	}
}
