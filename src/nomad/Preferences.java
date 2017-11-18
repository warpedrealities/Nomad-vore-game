package nomad;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Preferences {

	ArrayList<String> m_list;

	public Preferences() {
		// load preference list
		m_list = new ArrayList<String>();

		FileReader FR;
		BufferedReader BR;
		try {
			FR = new FileReader("assets/data/preferences.txt");
			BR = new BufferedReader(FR);
			String str;
			try {
				while (true) {
					str = BR.readLine();

					if (str != null) {
						m_list.add(str);
					} else {
						break;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<String> getPrefs()
	{
		return m_list;
	}
	
	public boolean ForbiddenPref(String str) {
		if (m_list.size() > 0) {
			for (int i = 0; i < m_list.size(); i++) {
				if (m_list.get(i).equals(str)) {
					return true;

				}
			}
		}
		return false;
	}

	public void setPreferences(List<String> prefs) {
		m_list.clear();
		m_list.addAll(prefs);
	}

}
