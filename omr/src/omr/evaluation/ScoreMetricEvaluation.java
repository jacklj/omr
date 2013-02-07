package omr.evaluation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import omr.symbol_recogntion.score_metrics.ScoreMetricsCalculator;
import omr.symbol_recogntion.score_metrics.ScoreMetricsCalculatorNaive;
import omr.util.FileListing;
import omr.util.ImageProcessing;

public class ScoreMetricEvaluation {
	public static void main(String[] args) {

		String outputTextfilePath = "score_metric_evalutation_results_saltNpepper.txt";
		
		String topLevelDirPath = "/Users/buster/libraries/GAMERA/Dataset_plus_saltNpepper/salt_and_pepper/";
		
		File startingDirectory= new File(topLevelDirPath);

		List<File> files = null;
		try {
			files= FileListing.getFileListing(startingDirectory);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//get only image files
		List<File> imageFiles = new ArrayList<File>();
		
		for(File f : files) {
			if(f.getPath().endsWith(".png")) {
				imageFiles.add(f);
			}
		}
		
		
		// progress bar?
//		int progressBarWidth = 50;
		System.out.println("Number of image files: " + imageFiles.size());
//		int progressBarOneUnitLengthPotench = (int) imageFiles.size() / progressBarWidth;
//		int progressBarOneUnitLength = progressBarOneUnitLengthPotench == 0? 1 : progressBarOneUnitLengthPotench;
//		
//		System.out.println(progressBarOneUnitLength);
//		displayProgBar(0, progressBarWidth);
//		int previousValue = 0;
		
		

		FileWriter outFile = null;
		try {
			outFile = new FileWriter(outputTextfilePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintWriter out = new PrintWriter(outFile);
		
		int count = 0;
		
		for(File f : imageFiles) {
			count++;
			String outputString = runTestOnImage(f.getPath());
			out.println(count + " " + outputString);
			System.out.println(count + " " + outputString);
//			if(count / progressBarOneUnitLength > previousValue) {
//				previousValue = count / progressBarOneUnitLength;
//				displayProgBar(count/progressBarOneUnitLength, progressBarWidth);
//			}
		}
		
		System.out.println("done!");
		
		out.close();
	}

	public static void displayProgBar(int progressSoFar, int maxVal) {
		String progBar = "[";
		
		for(int i = 0; i < maxVal; i++) {
			if(i < progressSoFar) {
				
			
			progBar = progBar + "-";
			} else {
				progBar = progBar + " ";
			}
		}
		progBar = progBar + "]\n";
		System.out.print(progBar);
	}

	public static String runTestOnImage(String imagePath) {

		BufferedImage image = ImageProcessing.loadImage(imagePath);
		
		
		ScoreMetricsCalculatorNaive sdcN = new ScoreMetricsCalculatorNaive(image);
		int stave_line_height_naive = sdcN.getStaveLineHeight();
		int stave_space_height_naive = sdcN.getStaveSpaceHeight();
		
		ScoreMetricsCalculator sdc = new ScoreMetricsCalculator(image);
		int stave_line_height_new = sdc.getStaveLineHeight();
		int stave_space_height_new = sdc.getStaveSpaceHeight();	

		String output = imagePath + "\t\t(" + stave_line_height_naive + "," + stave_space_height_naive + 
				")\t\t(" + stave_line_height_new + "," + stave_space_height_new + ")";
		return output;
		
//		System.out.println("\tstaveLineHeight:" + stave_line_height);
//		System.out.println("\tstaveSpaceHeight:" + stave_space_height);
	}
}
