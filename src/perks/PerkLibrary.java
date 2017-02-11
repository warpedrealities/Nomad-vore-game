package perks;

import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import shared.ParserHelper;

public class PerkLibrary {

	private ArrayList <Perk> perkList;
	static private PerkLibrary instance;
	
	static public PerkLibrary getInstance()
	{
		if (instance==null)
		{
			instance=new PerkLibrary();
		}
		return instance;
	}
	
	private PerkLibrary()
	{
		perkList=new ArrayList<Perk>();
		//load perks
		File file=new File("assets/data/perks");
		
		//find the names of all files in the item folder
		File[] files=file.listFiles();
		//use reader to generate items
		for (int i=0;i<files.length;i++)
		{
			if (files[i].getName().contains(".svn")==false)
			{
				generatePerk(files[i].getName());
			}
		}	
	}
	
	private void generatePerk(String filename)
	{
		Document doc=ParserHelper.LoadXML("assets/data/perks/"+filename);
		Element root=doc.getDocumentElement();
	    Element n=(Element)doc.getFirstChild();
		Element Enode=(Element)n;
	    if (Enode.getTagName()=="perk")
	    {
	    	perkList.add(new Perk(Enode));
	    }
	}
	
	public ArrayList<Perk> getPerks()
	{
		return perkList;
	}
	
	public Perk findPerk(String name)
	{
		for (int i=0;i<perkList.size();i++)
		{
			if (perkList.get(i).getName().equals(name))
			{
				return perkList.get(i);
			}
		}
		return null;
	}
}
