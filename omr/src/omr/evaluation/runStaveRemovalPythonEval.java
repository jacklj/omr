package omr.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import omr.util.FileListing;

public class runStaveRemovalPythonEval {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String inputTopLevelDirPath = args[0];
		String topLevelGroundTruthDirectory = args[1];
		String pythonScriptLocation = args[2];
		String outputTextfilePath = args[3];
		
//		String inputTopLevelDirPath = "/Users/buster/dissertation_Evaluation_output/Users/buster/libraries/GAMERA/dataSets/small_JustScores/";
//		String topLevelGroundTruthDirectory = "/Users/buster/libraries/GAMERA/dataSets/CvcMuscima-Distortions dataset/";

		
		
		
		// get all files in inputTopLevelDirPath (recursing in all subfolders) /////////////////////////////////////////
		File startingDirectory= new File(inputTopLevelDirPath);
		List<File> files = null;
		try {
			files= FileListing.getFileListing(startingDirectory);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// get only actualResults_justStaves png  files ////////////////////////////////////////////////////////////////
		List<File> imageFiles = new ArrayList<File>();
		for(File f : files) {
			if(f.getPath().endsWith("just_staves.png")) {
				imageFiles.add(f);
			}
		}
		int numberOfInputImages = imageFiles.size();
		System.out.println("Number of image files: " + numberOfInputImages);
		
		
		//open file writer
		FileWriter outFile = null;
		try {
			outFile = new FileWriter(outputTextfilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter out = new PrintWriter(outFile);
		
		int count = 0;

		for(File imageFile : imageFiles) {
			
			String actual_staves = imageFile.getPath(); //"/small_JustScores/curvature/w-30/image/p010.png_just_staves.png";	

			String[] slash_deliniated = actual_staves.split("/");
			String filename = slash_deliniated[slash_deliniated.length - 1];
			String subDir = slash_deliniated[slash_deliniated.length - 4] 
			                                 + "/" + slash_deliniated[slash_deliniated.length - 3] + "/";
			String original_file_name = filename.substring(0, Math.min(filename.length(), 8)); //get first 8 chars of name
			
			String originalImage = topLevelGroundTruthDirectory + subDir + "image/" + original_file_name;//"/CvcMuscima-Distortions dataset/curvature/w-30/image/p010.png";
			String groundTruth_staves = topLevelGroundTruthDirectory + subDir + "gt/" + original_file_name; //"/CvcMuscima-Distortions dataset/curvature/w-30/gt/p010.png";

			//check that these are valid files??
			
			System.out.println("actual staves path: " + actual_staves);
			System.out.println("\t -> original image path: " + originalImage);
			System.out.println("\t -> groundTruth_staves path: " + groundTruth_staves);

			
			//		executePythonEvalScript(originalImage, groundTruth_staves, actual_staves);
		}
		

		System.out.println("done!");
		
		out.close();
		
	}
	
	
	
	private static void executePythonEvalScript(String pythonScriptLocation, String originalImage, 
			String groundTruth_staves, String actual_staves, PrintWriter out) {
		
		String command = pythonScriptLocation + " " + originalImage + " " + groundTruth_staves + " "
		+ " " + actual_staves;

		System.out.println("Shell command: " + command);


		try{
			Runtime rt = Runtime.getRuntime();

			//Process pr = rt.exec("cmd /c dir");
			Process pr = rt.exec(command);

			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

			String line = null;

			System.out.println("--------------- shell output ----------------------");

			while((line=input.readLine()) != null) {
				System.out.println(line);
				out.println(line);

			}

			int exitVal = pr.waitFor();
			System.out.println("Exited with error code "+exitVal);

			System.out.println("-----------------------------------------------------");

		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}

	}	

}
