package omr.util;

import java.util.*;
import java.io.*;

/**
* Most code taken from:" 
* Recursive file listing under a specified directory.
* 
* @author javapractices.com
* @author Alex Wong
* @author anonymous user
*/
public final class FileListing {

  /**
  * Demonstrate use.
  * 
  * @param aArgs - <tt>aArgs[0]</tt> is the full name of an existing 
  * directory that can be read.
  */
  public static void main(String... aArgs) throws FileNotFoundException {
	  
	 String topLevelDirPath = "/Users/buster/libraries/GAMERA/CVCMUSCIMA_WI/";
    File startingDirectory= new File(topLevelDirPath);
    List<File> files = FileListing.getFileListing(startingDirectory);

    //print out all file names, in the the order of File.compareTo()
    for(File file : files ){
      System.out.println(file);
    }
  }
  
  
  
  static public List<String> getAllImages(String topLevelDirPath) {
	  File startingDirectory= new File(topLevelDirPath);

		List<File> files = null;
		try {
			files= FileListing.getFileListing(startingDirectory);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//get only image files
		List<String> imageFiles = new ArrayList<String>();
		
		for(File f : files) {
			String path = f.getPath();
			if(path.endsWith(".png")) {
				imageFiles.add(path);
			}
		}
		
		return imageFiles;
  }
  
  
  
  /**
  * Recursively walk a directory tree and return a List of all
  * Files found; the List is sorted using File.compareTo().
  *
  * @param aStartingDir is a valid directory, which can be read.
  */
  static public List<File> getFileListing(
    File aStartingDir
  ) throws FileNotFoundException {
    validateDirectory(aStartingDir);
    List<File> result = getFileListingNoSort(aStartingDir);
    Collections.sort(result);
    return result;
  }

  // PRIVATE //
  static private List<File> getFileListingNoSort(
    File aStartingDir
  ) throws FileNotFoundException {
    List<File> result = new ArrayList<File>();
    File[] filesAndDirs = aStartingDir.listFiles();
    List<File> filesDirs = Arrays.asList(filesAndDirs);
    for(File file : filesDirs) {
      result.add(file); //always add, even if directory
      if ( ! file.isFile() ) {
        //must be a directory
        //recursive call!
        List<File> deeperList = getFileListingNoSort(file);
        result.addAll(deeperList);
      }
    }
    return result;
  }

  /**
  * Directory is valid if it exists, does not represent a file, and can be read.
  */
  static private void validateDirectory (
    File aDirectory
  ) throws FileNotFoundException {
    if (aDirectory == null) {
      throw new IllegalArgumentException("Directory should not be null.");
    }
    if (!aDirectory.exists()) {
      throw new FileNotFoundException("Directory does not exist: " + aDirectory);
    }
    if (!aDirectory.isDirectory()) {
      throw new IllegalArgumentException("Is not a directory: " + aDirectory);
    }
    if (!aDirectory.canRead()) {
      throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
    }
  }
} 
