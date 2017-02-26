package vmo;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Config {

	float m_scale;
	float m_textscale;
	boolean verboseCombat;
	public static final int VERSION=20;
	
	public Config()
	{
		//open config file
		//read
		FileReader FR;
		BufferedReader BR;
		try {
			FR=new FileReader("assets/config.txt");
			BR=new BufferedReader(FR);
			String str;
			try 
			{
				while (true)
				{
						str = BR.readLine();
		
					if (str!=null)
					{
						if (str.contains("screenscale"))
						{
							String input=BR.readLine();
							m_scale=Float.parseFloat(input);
						}
						if (str.contains("textscale"))
						{
							String input=BR.readLine();
							m_textscale=Float.parseFloat(input);
						}
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
		
		float px=Toolkit.getDefaultToolkit().getScreenSize().height;
		if (px-40<(1024F*m_scale))
		{
			m_scale=((px-40)/1024);
		}
		
		
	}
	
	public float getScale()
	{
		return m_scale;
	}
	
	public float getTextscale()
	{
		return m_textscale;
	}

	public boolean isVerboseCombat() {
		return verboseCombat;
	}

	public void setVerboseCombat(boolean verboseCombat) {
		this.verboseCombat = verboseCombat;
	}
	
}
