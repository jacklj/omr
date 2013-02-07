package omr.symbol_recogntion.stave_detection;


// to do ////////
// implement second algorithm (wobbly 8 connectiveness stave line grower)


// info /////////
// top stave line is numbered '1', bottom line '5', like so:

// 1 -----------
// 2 -----------
// 3 -----------
// 4 -----------
// 5 -----------


import java.awt.Color;
import java.awt.image.BufferedImage;

import omr.symbol_recogntion.score_metrics.ScoreMetrics;
import omr.util.Colour;
import omr.util.ImageProcessing;
import omr.util.Run_Length_Encoding;

public class StaveIdentification {
	
	
	public static void main(String[] args) {
		BufferedImage test = ImageProcessing.loadImage("/Users/buster/Stuff/Academia/II/DISSERTATION/test_images/smoothedStaveLines.png");
		ScoreMetrics sm = new ScoreMetrics(test);
		
		
		System.out.println("StaveLine_height_min: " + sm.getStaveLine_height_min());
		System.out.println("StaveLine_height_max: " + sm.getStaveLine_height_max());
		
		System.out.println("StaveSpace_height_min: " + sm.getStaveSpace_height_min());
		System.out.println("StaveSpace_height_max: " + sm.getStaveSpace_height_max());
		
		StaveIdentification sk = new StaveIdentification(test, sm, StaveIdentification.ALGORITHM_TEMPLATE_MATCHING);
		sk.displaySkeleton();
		
		sk.printStaveSkeleton();
		
	}
		
	
	// state ///////////////////////////////////////////////////////////////////////////////////////////////////////////	
	private final int width;
	private int[][] stave;
	
	private int staveStartX = 0;
	
	private BufferedImage inputImage;
	
	ScoreMetrics scoreMetrics;
	
	private static int DELTA = 2;
	
	public static int ALGORITHM_CONNECTED_GROWTH = 1;
	public static int ALGORITHM_TEMPLATE_MATCHING = 2;
	
	// constructor /////////////////////////////////////////////////////////////////////////////////////////////////////
	public StaveIdentification(BufferedImage image_of_staveLines, ScoreMetrics sm, int ALGORITHM) {
		this.width = image_of_staveLines.getWidth();
		stave = new int[5][width];
		
		this.inputImage = image_of_staveLines;
		
		this.scoreMetrics = sm;
		
		process(image_of_staveLines, ALGORITHM);
	}
	
	// get / set ///////////////////////////////////////////////////////////////////////////////////////////////////////
	public void setStaveLinePoint(int staveLineNumber, int x_column, int y_staveLineRunCentre) {
		stave[staveLineNumber - 1][x_column] = y_staveLineRunCentre;
	}
	
	public int getStaveLinePoint(int staveLineNumber, int x_column) {
		return stave[staveLineNumber - 1][x_column];
	}
	
	public void printStaveSkeleton() {
		for(int x = 0; x < stave[1].length; x++) {
			System.out.print("x:" + x + "\t\t");
			for(int whichStave = 1; whichStave <= 5; whichStave++) {
				System.out.print("stave " + whichStave + ": " + this.getStaveLinePoint(whichStave, x) + "\t");
			}
			System.out.println();
		}
	}
	
	public void displaySkeleton() {
		BufferedImage display = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		ImageProcessing.copyImage(inputImage, display);
		
		//now draw skeleton on this
		for(int x = 0; x < inputImage.getWidth(); x++ ) {
			for(int staveLine = 1; staveLine <= 5; staveLine++) {
				int y = this.getStaveLinePoint(staveLine, x);
				display.setRGB(x, y, Color.RED.getRGB());
			}
		}
		
		//now diplay image
		
		ImageProcessing.displayImage(display, "Skeleton indicated by red line");
	}
	
	
	// private methods /////////////////////////////////////////////////////////////////////////////////////////////////
	private void process(BufferedImage image_of_staveLines, int ALGORITHM) {
	
		// 2 ALGORITHMS
		// 1) stave line detection algorithm inspired by "DEALING WITH SUPERIMPOSED OBJECTS IN OPTICAL MUSIC RECOGNITION"
		// D Bainbridge and T C Bell, SIXTH INTERNATIONAL CONFERENCE ON IMAGE PROCESSING AND ITS APPLICATIONS, 1997
		
		//(I actually formulated it (the 'wobble' method) independently then found this paper)
		
		// 2) Template matching algorithm
		// 	i) looks at every column of the image one by one
		// 	ii) attempts to match the run length encoded data to an ideal stave template (calculated using staveLine_height
		// 		and staveSpace_height, allowing for small error margins.

		
		
		
		if(ALGORITHM != ALGORITHM_CONNECTED_GROWTH && ALGORITHM != ALGORITHM_TEMPLATE_MATCHING) {
			System.err.println("ERROR - incorrect algorithm specified");
		
		} else if(ALGORITHM == ALGORITHM_CONNECTED_GROWTH) {
			
			//1) get a  column containing a perfect stave
			//
			// a) simple method (estimate): iterate through all columns, l->r
			//					-> if column contains 12 run lengths (1 '0 black', 1 white + 5 black stavelines + 4 
			//					   white staveSpaces + 1 white), it's a perfect stave-> DONE!
			//					-> any more/less-> try another column
			//					-> problem arises if a column contains a non stave artifact and has a hole in a stave 
			//					   line (so the total runs is still 12)
			
			
			//2) for each of the 1 pixel wide stave line segments (1 to 5)
			//		grow left until y = 0 and 
			//		grow right unttil y = width - 1;
			
//			growleft() {
//				int top_threshold = previous_run_topy - 1; //the -1 gives 8 connectivity
//				int bottom_threshold = previous_run_bottomy + 1; // the +1 gives 8 connectivity
//				
//				iterate through this column(y-1)'s black runs
//				foreach(black run) {
//					if(thisRun_bottomy < top_threshold || thisRun_topy > bottom_threshold) {
//						// not part of the same stave line -> ignore
//					
//					} else {
//						//part of the same stave line -> enter midpoint into stave skeleton and repeat growleft
//						// ---> make recursive??
//						// or use break;
//					}
//					
//					if(no relevaant stave segment found in this column) {
//						error-> shouldnt happen if input_image was horizontally smoothed
//					}
//				}
//			}
			
			
		} else if(ALGORITHM == ALGORITHM_TEMPLATE_MATCHING) {
				
		// 2) Template matching algorithm //////////////////////////////////////////////////////////////////////////////
		// 	i) looks at every column of the image one by one
		// 	ii) attempts to match the run length encoded data to an ideal stave template (calculated using staveLine_height
		// 		and staveSpace_height, allowing for small error margins.
		
		int width = image_of_staveLines.getWidth();
		int height = image_of_staveLines.getHeight();
		
		int[][] rleY = Run_Length_Encoding.RLE_2D_along_y_axis(image_of_staveLines);
		int numberOfPerfectStavesFound = 0;
		int numberOfImperfectStavesFound = 0;
		
		for(int x = 0; x < rleY.length; x++) {
			int actual_y = 0;

			
			int numberOfStaveLineRuns = 0;
			int numberOfStaveSpaceRuns = 0;
			
			int z = 0; //numberOfRunsThatHaveFittedThePattern
			int first_stave_run_number;
			boolean patternFound = false;
			
			for(int j = 0; j < rleY[x].length;j++ ) {
				int run_length = rleY[x][j];
				Colour run_colour = Run_Length_Encoding.whatColourIsRun(x, j);
				
//				System.out.println("\trun " + j + "\tlength: " + run_length + "\t\trun_colour: " + run_colour);
//				System.out.println("\t\tz: " + z);
				
				if(     (run_colour == Colour.BLACK) 
						&& (scoreMetrics.getStaveLine_height_min() <= run_length) 
						&& (run_length <= scoreMetrics.getStaveLine_height_max())
					) {
					
//					System.out.println("\t\t\tRun matches stave line constraints");
					if(z == 0 || z == 2 || z == 4 || z == 6 || z == 8) {
//						System.out.println("\t\t\tz = 0, 2, 4, 6 or 8 ");
						numberOfStaveLineRuns++;
						
						this.setStaveLinePoint(z/2+1, x, actual_y + (int)run_length/2);
						
						if(z == 0) {
//							System.out.println("\t\t\t\tz = 0 therefore first stave line!");
							//record coordinates
							first_stave_run_number = j;
						} else if(z == 8) {
//							System.out.println("\t\t\t\tz = 8 therefore last stave line - pattern complete!");
							patternFound = true;
							z++;
							break;
						}
						z++;
					}
					
					
					
				} else if(  (run_colour == Colour.WHITE)
							&& (scoreMetrics.getStaveSpace_height_min() <= run_length) 
							&& (run_length <= scoreMetrics.getStaveSpace_height_max())
						) {
//					System.out.println("\t\t\tRun matches stave space constrainsts");
					if(z == 1 || z == 3 || z == 5 || z == 7) {
//						System.out.println("\t\t\tz = 1,3,5 or 7");
					numberOfStaveSpaceRuns++;
					z++;
					}
					
					
				} else  {// break pattern
					z = 0;
//					System.out.println("Run doesnt match either constraints - z reset to 0");
				}
				
				
				
				
				actual_y = actual_y + run_length;
			}
			
			////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if(patternFound) {
				//entry already in stave skeleton -> grow.
//				System.out.println("PERFECT STAVE FOUND");
				numberOfPerfectStavesFound++;
			} else {
				//imperfect stave -> clear data entered into stave skeleton and try next column
//				System.out.println("IMPERFECT STAVE FOUND (x: " + x + ") -> couldnt find pattern... print run:");
				for (int j = 0; j < rleY[x].length; j++) {
//					System.out.print(rleY[x][j] + "' "); 
				}
//				System.out.println();
				
				numberOfImperfectStavesFound++;
				for(int i = 1; i <= 5; i++)  {
					this.setStaveLinePoint(i, x, 0);
				}
			}
		
		}
		System.out.println("numberOfPerfectStavesFound: " + numberOfPerfectStavesFound + " numberOfImperfectStavesFound: " + numberOfImperfectStavesFound);
		
//		this.printStaveSkeleton();
		
		
		/// most info already in stave skeleton.
		// search for any missing skeleton points
		//	-> fill in data by looking right and left till you get to data points, and take average between the two.
		//			if no data is found when looking left all the way to 1st column of image, the stave hasnt started 
		//			yet, so ignore -> and v.v. for looking right
		
		for(int x = 0; x < image_of_staveLines.getWidth(); x++) {
			if(this.getStaveLinePoint(2, x) == 0) { // data missing
				int[] leftData = new int[5]; 
				int[] rightData = new int[5];
				int leftDataXlocation = 0;
				int rightDataXlocation = 0;
				//look left for nearest data
				
				for(int i = x - 1; i >= 0; i--) {
					if(this.getStaveLinePoint(2, i) != 0) {
						leftDataXlocation = i;
						for(int j = 0; j < 5; j++) {
							leftData[j] = this.getStaveLinePoint(j+1, i);
						}
						break;
					}
					
				}
				
				//look right for nearest data
				for(int i = x + 1; i < image_of_staveLines.getWidth(); i++) {
					if(this.getStaveLinePoint(2, i) != 0) {
						rightDataXlocation = i;
						for(int j = 0; j < 5; j++) {
							rightData[j] = this.getStaveLinePoint(j+1, i);
						}
						break;
					}
				}
				
				// if either wasn't set, probably because the end of stave has been passed -> set all to the values
				// in the other direction
				
				//left and right data obtained -> populate all empty cells
				for(int x_pos = leftDataXlocation + 1; x_pos< rightDataXlocation; x_pos++) {
					for(int j = 0; j < 5; j++) {
						this.setStaveLinePoint(j+1, x_pos, (int)((leftData[j] + rightData[j])/2));
					}
//					System.out.println("Empty cell populated (x: " + x_pos);
				}
				
				
			} else {
				// else data present -> continue for loop

			}
		}
		
//		this.printStaveSkeleton();
		
		
		} 
	}
	
		
//	private boolean isRunLengthColumnAPerfectStave(int[] rlColumn) {
//		
//	}
//	
	
}
