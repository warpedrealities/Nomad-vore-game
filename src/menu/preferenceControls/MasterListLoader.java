package menu.preferenceControls;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MasterListLoader {

	public List<MasterListPref> load() {
		List<MasterListPref> list=new ArrayList<MasterListPref>();
		
		File file=new File("assets/data/masterPreferencesList.txt");
		if (file.exists())
		{
			try (BufferedReader BR = new BufferedReader(new FileReader(file));) {
				while (true) 
				{
					String str = BR.readLine();
					if (str != null) {
						String []substr=str.split(",");
						MasterListPref pref=new MasterListPref(substr[0],Integer.parseInt(substr[1]));
						list.add(pref);
					} else {
						break;
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				return list;
			}

		}
		return list;
	}

}
