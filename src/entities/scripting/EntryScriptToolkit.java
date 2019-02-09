package entities.scripting;

import nomad.FlagField;
import nomad.playerScreens.journal.JournalEntry;
import nomad.universe.Universe;
import spaceship.Spaceship;

public class EntryScriptToolkit {

	private Universe universe;

	public EntryScriptToolkit(Universe universe) {
		this.universe = universe;
	}

	public FlagField getGlobalFlags() {
		return universe.getPlayer().getFlags();
	}

	public void addJournal(String file, String name) {
		this.universe.getJournal().addEntry(new JournalEntry(file, name));
	}

	public void removeJournal(int id) {
		this.universe.getJournal().removeEntry(id);
	}

	public Spaceship getShip() {
		if (Spaceship.class.isInstance(universe.getCurrentEntity())) {
			return (Spaceship) universe.getCurrentEntity();
		}
		return null;
	}
}
