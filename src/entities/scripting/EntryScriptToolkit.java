package entities.scripting;

import actorRPG.Actor_RPG;
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

	public void addResearch(boolean threshold, String parameters) {
		String[] split = parameters.split(" ");
		if (threshold) {
			if (split.length > 2) {
				universe.getPlayer().getEncyclopedia().addThresholdResearch(split[0], Integer.parseInt(split[1]),
						split[2]);
			} else {
				universe.getPlayer().getEncyclopedia().addThresholdResearch(split[0], Integer.parseInt(split[1]), null);
			}

		} else {
			int r = Universe.m_random.nextInt(20) + universe.getPlayer().getRPG().getAttribute(Actor_RPG.SCIENCE);
			universe.getPlayer().getEncyclopedia().addChanceResearch(split[0], Integer.parseInt(split[1]), r, split[2]);
		}
	}

	public Spaceship getShip() {
		if (Spaceship.class.isInstance(universe.getCurrentEntity())) {
			return (Spaceship) universe.getCurrentEntity();
		}
		return null;
	}
}
