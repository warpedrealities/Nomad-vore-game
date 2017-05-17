package shared;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class FileTools {

	public static void deleteFolder(File folder) {
		System.err.println("deleting folder "+folder.getName());
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	            	System.err.println("deleting file "+f.getName());
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
	
	public static void copyFolder(File origin,File destination)
	{
		try {
			FileUtils.copyDirectory(origin,destination);
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		try {
			errorCheck(origin,destination);
		} catch (IOException e) {

			e.printStackTrace();
		}		
	}
	
	public static void errorCheck(File origin,File destination) throws IOException
	{
		Set <String> originList=new HashSet<String>(Arrays.asList(origin.list()));
		Set <String> destinationList=new HashSet<String>(Arrays.asList(destination.list()));
		originList.remove(destinationList);
		//compare lists
		Iterator <String> iterator=originList.iterator();
		while (iterator.hasNext())
		{
			String missingFile=iterator.next();
			File original=new File("saves/"+origin.getName()+"/"+missingFile);
			File dest=new File("saves/"+destination.getName()+"/"+missingFile);
			FileUtils.copyFile(original, dest);
		}
	}
}

