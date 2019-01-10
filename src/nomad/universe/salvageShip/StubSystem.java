package nomad.universe.salvageShip;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import entities.Entity;
import spaceship.Spaceship;

public class StubSystem {

	private List<Entity> entitiesInSystem;
	
	public StubSystem()
	{
		entitiesInSystem=new ArrayList<Entity>();
	}
	
	public void load(String filename, String systemName) throws IOException {
		// TODO Auto-generated method stub
		File file = new File("saves/" + filename + "/" + systemName + ".sav");
		FileInputStream fstream = new FileInputStream(file);
		DataInputStream dstream = new DataInputStream(fstream);

		int count = dstream.readInt();
		for (int i = 0; i < count; i++) {
			boolean b = dstream.readBoolean();
			if (b == true) {
				int t = dstream.readInt();
				switch (t) {
				case 1:
					Spaceship ship = new Spaceship();
					ship.load(dstream);
					entitiesInSystem.add(ship);
					break;

				}
			}
		}
		dstream.close();
		fstream.close();
	}

	public Spaceship getShip(String shipName) {
		for (int i=0;i<entitiesInSystem.size();i++)
		{
			if (Spaceship.class.isInstance(entitiesInSystem.get(i)) && entitiesInSystem.get(i).getName().equals(shipName))
			{
				return (Spaceship)entitiesInSystem.get(i);
			}
		}
		return null;
	}

}
