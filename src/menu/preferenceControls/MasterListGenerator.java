package menu.preferenceControls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


public class MasterListGenerator {

	private XMLInputFactory xmlInputFactory;
	public List <MasterListPref> generate()
	{
		xmlInputFactory = XMLInputFactory.newInstance();
		Map <String,MasterListPref> map=new HashMap<String,MasterListPref>();
		try {
			scanDirectory("assets/data/conversations",map);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ArrayList(map.values());
	}
	
	private void scanDirectory(String filename, Map<String,MasterListPref> map) throws FileNotFoundException
	{
		File file=new File(filename);
		File []files= file.listFiles();	
		
		for (int i=0;i<files.length;i++)
		{
			if (files[i].isDirectory())
			{
				scanDirectory(filename+"/"+files[i].getName(),map);
			}
			else if (files[i].getName().contains(".xml"))
			{
				try {
					scanFile(files[i],map);
				} catch (XMLStreamException e) {
					System.err.println("file:"+filename+files[i].getName());
					e.printStackTrace();
				}
			}
		}
	}

	private void scanFile(File file, Map<String, MasterListPref> map) throws FileNotFoundException, XMLStreamException {
		XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(new FileInputStream(file.getPath()));
		System.out.println(file.getName());
	    while(reader.hasNext())
	    	{
	    		int next= reader.next();
	    		if (next==reader.START_ELEMENT)
	    		{
	    			String qname=reader.getName().getLocalPart();
	    			if (qname.equals("preference"))
	    			{				
	    				String str=reader.getAttributeValue(0);
	    				checkPref(str,map);
	    			}
	    	}
            
	    }
	}
	
	private void checkPref(String str,Map<String, MasterListPref> map)
	{
		MasterListPref pref=map.get(str);
		if (pref!=null)
		{
			pref.incrementCount();
		}
		else
		{
			pref=new MasterListPref(str);
			map.put(str, pref);
		}
	}
	
}
