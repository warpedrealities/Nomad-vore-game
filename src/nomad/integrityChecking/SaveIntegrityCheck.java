package nomad.integrityChecking;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nomad.StarSystem;
import nomad.Universe;

public class SaveIntegrityCheck {

	private String filename;
	private Universe universe;
	private List<String> missingFiles;
	public SaveIntegrityCheck(String filename, Universe universe) {
		this.filename=filename;
		this.universe=universe;

		missingFiles=new ArrayList<String>();
		missingFiles.add("player.sav");
		missingFiles.add("verse.sav");
		StarSystem system=universe.getcurrentSystem();
		missingFiles.add(system.getName()+".sav");
		for (int i=0;i<system.getEntities().size();i++)
		{
			if (system.getEntities().get(i).isLoaded())
			{
				missingFiles.add(system.getEntities().get(i).getName()+".sav");
			}
		}
	}

	public boolean isOkay() {
		File folder=new File("saves/"+filename);
		File []files=folder.listFiles();
		
		for (int i=0;i<files.length;i++)
		{
			String str=files[i].getName();
			missingFiles.remove(str);
		}
		if (missingFiles.size()>0)
		{
			return false;
		}
		return true;
	}

}
