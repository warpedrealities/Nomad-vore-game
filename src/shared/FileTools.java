package shared;

import java.io.File;

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
}
