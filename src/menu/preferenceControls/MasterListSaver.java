package menu.preferenceControls;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MasterListSaver {

	public void save(List<MasterListPref> allPrefs) throws IOException {
		// TODO Auto-generated method stub
		File file=new File("assets/data/masterPreferencesList.txt");
		file.delete();
		file.createNewFile();
		try (BufferedWriter bwriter=new BufferedWriter(new FileWriter(file)))
		{
			for (int i=0;i<allPrefs.size();i++)
			{
				bwriter.write(allPrefs.get(i).getPreference());
				bwriter.write(",");
				bwriter.write(Integer.toString(allPrefs.get(i).getCount()));
				bwriter.newLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
