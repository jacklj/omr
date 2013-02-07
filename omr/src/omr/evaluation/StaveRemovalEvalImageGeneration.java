package omr.evaluation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import omr.symbol_recogntion.score_metrics.ScoreMetrics;
import omr.symbol_recogntion.score_metrics.ScoreMetricsCalculator;
import omr.symbol_recogntion.score_metrics.ScoreMetricsCalculatorNaive;
import omr.symbol_recogntion.stave_detection.StaveRemoval;
import omr.util.FileListing;
import omr.util.ImageProcessing;

public class StaveRemovalEvalImageGeneration {
	//iterate through images and for each one produce just_symbol and just_stave output images
	// 		-> to then run eval algorithms on at a later date 
	

	public static void main(String[] args) {

		String inputTopLevelDirPath = "/Users/buster/libraries/GAMERA/dataSets/typeset/typeset_distorted/originalImages/";
		String outputTopLevelDirPath = "/Users/buster/libraries/GAMERA/dataSets/typeset/typeset_distorted_stavesRemoved/";
		
		
		// get all files in inputTopLevelDirPath (recursing in all subfolders) /////////////////////////////////////////
		File startingDirectory= new File(inputTopLevelDirPath);
		List<File> files = null;
		try {
			files= FileListing.getFileListing(startingDirectory);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// get only image files ////////////////////////////////////////////////////////////////////////////////////////
		List<File> imageFiles = new ArrayList<File>();
		for(File f : files) {
			if(f.getPath().endsWith(".png")) {
				imageFiles.add(f);
			}
		}
		int numberOfInputImages = imageFiles.size();
		System.out.println("Number of image files: " + numberOfInputImages);
		
		
		// time execution //////////////////////////////////////////////////////////////////////////////////////////////
		final long startTime = System.nanoTime();
		System.out.println("Test started at: " + startTime);
		int count = 0;
		try {
			
			
			
			for(File f : imageFiles) {
				count++;
				System.out.println(count);
				
				runTestOnImage(f, outputTopLevelDirPath);
				
				
				
			}
			
			
			
			
		} 
		finally { // use of try/finally block ensures the following code is ALWAYS executed, 
					// even if an exception is thrown etc.
		  final long endTime = System.nanoTime();
		  System.out.println("Test finished at: " + endTime);
		  
		  final long duration = endTime - startTime;
		  System.out.println("Test duration: " + endTime + " nanoSeconds");
		  
		  final long nsecsPerImage = duration / numberOfInputImages;
		  System.out.println("nanoSeconds per Image (ie average algorithm run time): " + nsecsPerImage);
		  
		  final long nsecsPerImageDone = duration / count;
		  System.out.println("nanoSeconds per Image done so far  (incase test fails before all images done): " + nsecsPerImageDone);

		}
		
		
		System.out.println("done!");
		
	}


	
	
	public static void runTestOnImage(File f, String topLevelOutputfolder) {

		
		String inputImagePath = f.getPath();
		System.out.println("Input image: " + inputImagePath);
		String inputImageName = f.getName();
		String ouputDirPath = topLevelOutputfolder + f.getParent() + "/";
		
		BufferedImage image = ImageProcessing.loadImage(inputImagePath);
		
		// need to preprocess image?
		
		ScoreMetrics score_metrics = new ScoreMetrics(image);
		
		StaveRemoval stave_removal = new StaveRemoval(score_metrics, image, 2);

		BufferedImage staveWithoutStaveLines = stave_removal.getStaveWithoutStaveLines();
		BufferedImage justStaveLines = stave_removal.getJustStaveLines();
				
		
		ImageProcessing.saveImage(staveWithoutStaveLines, ouputDirPath, inputImageName + "_just_symbols.png");
		ImageProcessing.saveImage(justStaveLines, ouputDirPath, inputImageName +  "_just_staves.png");
	}
	




}
