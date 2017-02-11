package shared;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

public class NameGenerator {

	
	Vector <String> m_names;
	Random m_random;
	Vector <String> m_listnames;
	public NameGenerator(String filename,String listfile)
	{
		FileReader FR;
		BufferedReader BR;
		try {
			FR=new FileReader(filename);
			BR=new BufferedReader(FR);
			m_names=new Vector <String>();
			m_random=new Random();

			String str;
			try 
			{
				while (true)
				{
						str = BR.readLine();
		
					if (str!=null)
					{
						m_names.add(str);
					}
					else
					{
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
		try {
			FR=new FileReader(listfile);
			BR=new BufferedReader(FR);
			m_listnames=new Vector<String>();
			m_random=new Random();

			String str;
			try 
			{
				while (true)
				{
						str = BR.readLine();
		
					if (str!=null)
					{
						m_listnames.add(str);
					}
					else
					{
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
	
	public String getName()
	{
		int r=m_random.nextInt(m_names.size());
		return m_names.get(r);
	}
	public String getListname(int i)
	{
		return m_listnames.get(i);
	}
	
}
