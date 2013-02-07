package omr.symbol_recogntion.score_metrics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import omr.util.ImageProcessing;
import omr.util.Run_Length_Encoding;

import org.jfree.chart.JFreeChart;


// INFO ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// logic -> constructor runs all main functions
// then use get methods to extract stavelineheight and stavespaceheight
// good to create an object though so we can run extra functions eg print sorted list, display histogram etc
//
//KNOWN BUGS ///////////////////////////////////////////////////////////////////////////////////////////////////////////
//1.
//
//TO DO ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//1.
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


public class ScoreMetricsCalculatorNaive {
	
	/// state //////////////////////////////////////////////////////////////////////////////////////////////////////////
	private BufferedImage image = null;
	private int height;
	private int width;
	
	private int staveLineHeight;
	private int staveSpaceHeight;
	private int maxFrequency;
	
	private List<Frequency_RunPairSum_Pair> sorted_by_freq_pairList = null;
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(String args[]) {
		
		String imageFilePath = "//Users//buster//Stuff//Academia//II//DISSERTATION//test_images//dont_stop_me_now_1line.png";
		BufferedImage image = ImageProcessing.loadImage(imageFilePath);
		
		ScoreMetricsCalculatorNaive sdc = new ScoreMetricsCalculatorNaive(image);
		int stave_line_height = sdc.getStaveLineHeight();
		int stave_space_height = sdc.getStaveSpaceHeight();
		
		System.out.println("Image: " + imageFilePath);
		System.out.println("\tstaveLineHeight:" + stave_line_height);
		System.out.println("\tstaveSpaceHeight:" + stave_space_height);
		
//		sdc.printHistogram();
	}
	
	
	// constructor /////////////////////////////////////////////////////////////////////////////////////////////////////
	public ScoreMetricsCalculatorNaive(BufferedImage img) {
		image = img;
		height = image.getHeight();
		width = image.getWidth();
		
		Run_Length_Encoding rle = new Run_Length_Encoding(image);
		int[][] rle_2D_list = rle.RLE_2D_along_y_axis();
		
		
		List<Integer> blackRuns = new ArrayList<Integer>();
		List<Integer> whiteRuns = new ArrayList<Integer>();
		
		
		for(int i = 0; i < rle_2D_list.length; i++) {
			for(int j = 0; j < rle_2D_list[i].length; j++) {
				if(j%2 == 0) { // run is black
					blackRuns.add(rle_2D_list[i][j]);
				} else {
					whiteRuns.add(rle_2D_list[i][j]);
				}
			}
		}
		
//		printList(blackRuns);
//		printList(whiteRuns);
		
		int moddalAverageBlackRunLength = getModalValue(blackRuns);
		int moddalAverageWhiteRunLength = getModalValue(whiteRuns);
		
		this.staveLineHeight = moddalAverageBlackRunLength;
		this.staveSpaceHeight = moddalAverageWhiteRunLength;
		
	}
	

	private static Integer getModalValue(List<Integer> intList) {
		Integer largest = getLargestValue(intList);
		int[] histogram = new int[largest + 1];
//		printList(histogram);
//		System.out.println("Histogram initial size = " + histogram.length);
		

				
		for(int i = 0; i < intList.size(); i++) {
			histogram[intList.get(i)]++;
		}
		
//		printList(histogram);
		// now get index of largest value in histogram array
		Integer largestIndex = getLargestHistogramIndex(histogram);
		
		return largestIndex;

	}
	
	
	private static void printList(int[] intList) {
		for(int i = 0; i < intList.length; i++) {
			System.out.print(intList[i] + " ");
		}
		System.out.println();
	}
	
	private void printList(List<Integer> intList) {
		for(int i = 0; i < intList.size(); i++) {
			System.out.print(intList.get(i) + " ");
		}
		System.out.println();
	}
	
	
	private static Integer getModalValueOLD(List<Integer> intList) {
		Integer largest = getLargestValue(intList);
		List<Integer> histogram = new ArrayList<Integer>(largest + 1);
		
		System.out.println("Histogram initial size = " + histogram.size());
		
		//set all to 0
		for(int i = 0; i < largest + 1; i++) {
			histogram.set(i, 0);
		}
		
		System.out.println("Histogram initial size = " + histogram.size());
		
		for(int i = 0; i < intList.size(); i++) {
			histogram.set(intList.get(i), histogram.get(i) + 1);
		}
		
		// now get index of largest value in histogram array
		Integer largestIndex = getLargestHistogramIndex(histogram);
		
		return largestIndex;

	}
	
	private static Integer getLargestValue(List<Integer> intList) {
		Integer largest = 0;
		for(int i = 0; i < intList.size(); i++) {
			largest = intList.get(i) > largest ? intList.get(i) : largest;
		}
		return largest;
	}
	
	private static Integer getLargestHistogramIndex(List<Integer> intList) { // if multiple largest, gets smallest index
		Integer largestValue = 0;
		Integer largestIndex = 0;
		for(int i = 0; i < intList.size(); i++) {
			if(intList.get(i) > largestValue) {
				largestValue = intList.get(i);
				largestIndex = i;
			}
		}
		return largestIndex;
	}
	
	private static Integer getLargestHistogramIndex(int[] intList) { // if multiple largest, gets smallest index
		Integer largestValue = 0;
		Integer largestIndex = 0;
		for(int i = 0; i < intList.length; i++) {
			if(intList[i] > largestValue) {
				largestValue = intList[i];
				largestIndex = i;
			}
		}
		return largestIndex;
	}
	
	// public methods //////////////////////////////////////////////////////////////////////////////////////////////////
	public void printHistogram() {

		if(sorted_by_freq_pairList == null) {
			System.out.println("[ERROR]  No histogram");
			return;
		}

		int countcheck = 0;
		System.out.print("[counter]\tfreq\t[int1\t+ int2\t= sum\t]");
		//System.out.print("\tobj.toString()"); // for debugging
		System.out.println();

		for(Frequency_RunPairSum_Pair f : sorted_by_freq_pairList) {
			countcheck++;
			System.out.print("[" + countcheck + "\t]\t");
			System.out.print(f.getFrequency() + "\t[");

			Run_Pair_Sum i = (Run_Pair_Sum)f.getRunPairSumObj();

			i.print();
			System.out.print("\t]");
			//System.out.print("\t" + i.toString() ); // for debugging
			System.out.println();
		}

		
		//print max
		System.out.println("\nMax:\t\tFreq(" + maxFrequency + ")\t\t(StaveLineHeight: " + staveLineHeight + ", " +
				"StaveSpaceHeight: " + staveSpaceHeight + ")");

	}
	
	// get methods ///////////////////////////
	public int getStaveLineHeight() {		//
		return staveLineHeight;				//
		}									//
											//
	public int getStaveSpaceHeight() {		//
		return staveSpaceHeight;			//
	}										//
	//////////////////////////////////////////
	
// private methods /////////////////////////////////////////////////////////////////////////////////////////////////
	private List<Run_Pair_Sum> sumListPairs_2D(int[][] rle) {
			// now go through each pair and add, creating a list of Run_Pair_Sum objects (so next we can find the "most 
			// common sum of two consecutive vertical runs" (i.e. find the modal average of the pair sums))

			
			List<Run_Pair_Sum> run_pair_sum_list = new ArrayList<Run_Pair_Sum>();
			
			for(int x = 0; x < rle.length; x++) {
				for (int y = 0; y < rle[x].length - 1; y++) {
					int r1 = rle[x][y];
					int r2 = rle[x][y+1];
					
					if(r2 == 0) {//then the end of the run length encoding list has been found -> exit inner loop to 
								 //skip to next column.
						break;
					}
					
					Run_Pair_Sum pairSum = new Run_Pair_Sum(r1, r2);
					run_pair_sum_list.add(pairSum);
					
				}
			}
						
			return run_pair_sum_list;
		}
	
	private List<Frequency_RunPairSum_Pair> sortListPairs(List<Run_Pair_Sum> rle_consecutive_pair_sum_objects_list) {
		/////////////////// FIND MODAL AVERAGE /////////////////////////////////////////////////////////////////////////

		// use Set to eliminate duplicate int_pair_sum objects (i.e. can then iterate through a list of each type of 
		// int_pair_sum object, then use Collections.frequency to count the number of each int_pair_sum type appearing
		// in rle_consecutive_pair_sum_objects_list
	
		
		Set<Run_Pair_Sum> pair_sum_object_set = new HashSet<Run_Pair_Sum>(rle_consecutive_pair_sum_objects_list);
		// -> to eliminate duplicates, creating a set of all the different int_pair_sum objects appearing in 
		// rle_consecutive_pair_sum_objects_list.
		
		
		List<Frequency_RunPairSum_Pair> freq_pairSum_list = new ArrayList<Frequency_RunPairSum_Pair>();
		
		for(Run_Pair_Sum s : pair_sum_object_set) { // iterator (shorthand syntax)
			
			int occurrences = Collections.frequency(rle_consecutive_pair_sum_objects_list, s);
			
			Frequency_RunPairSum_Pair f_ips_pair = new Frequency_RunPairSum_Pair(occurrences, s); 
			freq_pairSum_list.add(f_ips_pair);
			
			// debug /////////////////////////////////////////////
//			s.print();
//			System.out.print("\t->\t" + occurrences);
//			System.out.print("\t" + s.toString());
//			System.out.print("\t\t" + f_ips_pair.toString());
//			//get intpairsum obj back out again to test.
//			Int_Pair_Sum i = f_ips_pair.getIntPairObj();
//			System.out.print("\t\t" + i.toString());
//			System.out.println();
			//////////////////////////////////////////////////////
			
		}
		
		Collections.sort(freq_pairSum_list);
		
		return freq_pairSum_list;
	}
	
	private void extractMax(List<Frequency_RunPairSum_Pair> freq_pairSum_list) {
		
		//set object state
		sorted_by_freq_pairList = freq_pairSum_list;
		
		//get max
		Frequency_RunPairSum_Pair f = freq_pairSum_list.get(freq_pairSum_list.size()-1);
		Run_Pair_Sum i = f.getRunPairSumObj();
		maxFrequency = f.getFrequency();
		int i_first = i.get_first_run();
		int i_second = i.get_second_run();
		
		//checks?? - if they're equal or not larger is not roughly 3x larger (for example) throw out error message?
		
		if(i_first == i_second) {
			System.err.println("[ERROR]  StaveSpaceHeight and StaveLineHeight are equal - not expected in WMN");
			staveLineHeight = i_first; // set anyway
			staveSpaceHeight = i_second;
			
		} else if (i_first < i_second) {
			staveLineHeight = i_first;
			staveSpaceHeight = i_second;
		} else {
			staveLineHeight = i_second;
			staveSpaceHeight = i_first;
		}
		
	}

//	private void printList(List<Frequency_RunPairSum_Pair> freq_pairSum_list) {
//		
//		int countcheck = 0;
//		System.out.println("[counter]\tfreq\t[int1\t+ int2\t= sum\t]\tobj.toString()");
//		
//		for(Frequency_RunPairSum_Pair f : sorted_by_freq_pairList) {
//			countcheck++;
//			System.out.print("[" + countcheck + "\t]\t");
//			System.out.print(f.getFrequency() + "\t[");
//			
//			Run_Pair_Sum i = (Run_Pair_Sum)f.getRunPairSumObj();
//			
//			i.print();
//			System.out.print("\t]\t" + i.toString() );
//			System.out.println();
//		}
//		
//		//get max
//		Frequency_RunPairSum_Pair f = freq_pairSum_list.get(freq_pairSum_list.size()-1);
//		Run_Pair_Sum i = f.getRunPairSumObj();
//		int maxFreq = f.getFrequency();
//		System.out.print("\nMax:\t\tFreq(" + maxFreq + ")\t\t[");
//		i.print();
//		System.out.println("]");
//	}

	

}