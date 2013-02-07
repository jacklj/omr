package omr.symbol_recogntion.stave_detection;

//INFO /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//1.
//
//KNOWN BUGS ///////////////////////////////////////////////////////////////////////////////////////////////////////////
//1. the neighbour conditions are NEVER ENCOUNTERED (neither of them!)
// but then ran alg on /Users/buster/Stuff/Academia/II/DISSERTATION/test_images/dont_stop_me_now_1line_binarised.png
// and there were ENCOUNTERs!
// --> maybe do different colours to illustrate whats going on at each stage on a single image.
//
//TO DO ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//1. 
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;

import omr.symbol_recogntion.score_metrics.ScoreMetrics;
import omr.symbol_recogntion.score_metrics.ScoreMetricsCalculator;
import omr.util.Colour;
import omr.util.ImageProcessing;
import omr.util.Run_Length_Encoding;
import omr.util.WriteImageToFile;

public class StaveRemovalOld {

	private static final int DELTA = 2;
	
	/// state //////////////////////////////////////////////////////////////////////////////////////////////////////////
	private BufferedImage original_image = null;
	private int height;
	private int width;
	
	private ScoreMetrics scoreMetrics;
	
	private int staveLine_height;
	private int staveSpace_height;
	
	private BufferedImage staveWithoutStaveLines = null;
	private BufferedImage justStaveLines = null;
	private BufferedImage smoothedStaveLines = null;
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	public static void main(String[] args) {
		
		BufferedImage rawImage = ImageProcessing.loadImage("/Users/buster/Stuff/Academia/II/DISSERTATION/test_images/dont_stop_me_now_1line.png");


//		ImageProcessing.displayImage(rawImage, "Raw Image");
		
		BufferedImage whole_stave = ImageProcessing.preprocess(rawImage);
		
		ScoreMetrics score_metrics = new ScoreMetrics(whole_stave);
		
		
		StaveRemovalOld stave_detection = new StaveRemovalOld(score_metrics, whole_stave);
		
		BufferedImage stave_without_stave_lines = stave_detection.getStaveWithoutStaveLines();
		BufferedImage just_staves = stave_detection.getJustStaveLines();
		
//		ImageProcessing.displayImage(stave_without_stave_lines, "Stave without stave lines");
//		ImageProcessing.displayImage(just_staves, "just_staves");

				
		
//		WriteImageToFile.write(stave_without_stave_lines, "/Users/buster/testOutput/staveWithoutStaveLines.png");
//		WriteImageToFile.write(just_staves, "/Users/buster/testOutput/justStaves.png");

	}

	
	// constructor /////////////////////////////////////////////////////////////////////////////////////////////////////
	public StaveRemovalOld(ScoreMetrics score_metrics, BufferedImage img) {
		original_image = img;
		width = original_image.getWidth();
		height = original_image.getHeight();
		
		staveLine_height = score_metrics.getStaveLine_height();
		staveSpace_height = score_metrics.getStaveSpace_height();
		
		this.scoreMetrics = score_metrics;
		
		locateStaves();
	}

	
	// public methods //////////////////////////////////////////////////////////////////////////////////////////////////	
	public BufferedImage getStaveWithoutStaveLines() {
		return staveWithoutStaveLines;
	}
	
	public BufferedImage getJustStaveLines() {
		return justStaveLines;
	}
	
	public BufferedImage getSmoothedStaveLines() {
		return smoothedStaveLines;
	}
	
	
	
	// private methods /////////////////////////////////////////////////////////////////////////////////////////////////
	private void locateStaves() {
		
		
		ImageProcessing.displayImage(original_image, "Original Image");

		// have a choice of which stave removal algorithm to use?? 
		
		// 1) Y projections based algorithm ////////////////////////////////////////////////////////////////////////////
//		// y projection of image - 5 peaks (each of staveLineHeight width, each staveSpaceHeight apart)
//		
//		System.out.println("Width = " + width);
//		System.out.println("Height = " + height);
//		
//		Projection project = new Projection(image);
//		int yProjection[] = project.yProject(0, width-1, 0, height - 1);
//		
//		// pretty print ////////////////////////////
//		System.out.print("[[");
//		for(int y=0; y < height; y++) {
//			System.out.print(yProjection[y] + ",");
//		}
//		System.out.println("]]");
//		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		
		// 2) USING "AN EFFICIENT STAFF REMOVAL ALGORITHM", DUTTA PAL, FORNES, LLADOS 2010
		
		// 1. horizontal run-length smoothing //////////////////////////////////////////////////////////////////////////
		//		why? to join broken portions of a stave line that often appear due to noise
		//		how?
		//		i) experimentally set the smoothing parameter ('t') as equal to staveline_height
		//		ii) 'add' together any black runs with a white run in between them that's less than t pixels long
		//				->iterate through run length encoding list.
		//					->if a white run is less than t, add its length, the black run behind it and the black run 
		//					  in front of it and combine into one list element.
		//
		
		BufferedImage horizontallySmoothedImage_step1 = ImageProcessing.copyImage(original_image);
		
		int smoothingParameter = scoreMetrics.getStaveLine_height();
		
		
		Run_Length_Encoding rle1 = new Run_Length_Encoding(original_image);
		int[][] rle_x1 = rle1.RLE_2D_along_x_axis();
		
		for(int y = 0; y < rle_x1.length; y++) {
			int x_actual = 0;
			
			for (int j = 0; j < rle_x1[y].length; j++) {
				int run_length = rle_x1[y][j];
				Colour run_colour = Run_Length_Encoding.whatColourIsRun(y,j); // makes code easier to read

				if(run_colour == Colour.WHITE && run_length < smoothingParameter) {
					for (int x = x_actual; x < x_actual + run_length; x++) {
						horizontallySmoothedImage_step1.setRGB(x, y, 0); //black
					}
				}
				x_actual = x_actual + run_length;
			}
		}
					
		
		
		// 2. core algorithm ///////////////////////////////////////////////////////////////////////////////////////////
		
		// run length encoding along y axis
		Run_Length_Encoding rle = new Run_Length_Encoding(horizontallySmoothedImage_step1);
		int[][] rle_y = rle.RLE_2D_along_y_axis();
		
		// generate new BufferedImage just of staves
		BufferedImage just_staves = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		
		//generate new BufferedImage of everything but staves (original image, with staves removed)
		BufferedImage score_minus_staves = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

		for(int x = 0; x < rle_y.length; x++) {
			//System.out.println("x = " + x);
			int y_actual = 0;
			
			for (int i = 0; i < rle_y[x].length; i++) {
				int run_length = rle_y[x][i];
				
				//System.out.println("\t\ti = " + i + "\t\trun_length = "+ run_length + "\t\ty_actual = " + y_actual);
					
				Colour run_colour = Run_Length_Encoding.whatColourIsRun(x,i); // makes code easier to read

				if(run_colour == Colour.BLACK) {

					//check if within parameters
					int lower_bound = Math.abs(staveLine_height - DELTA);
					int upper_bound = staveLine_height + DELTA;

					if((lower_bound <= run_length) && (run_length <= upper_bound))  { // satisfies constraints - 
						// it's a stave line segment
						for (int j = y_actual; j < y_actual + run_length; j++) { // therefore remove black pixels 
							// from 'score_minus_staves' and add them to 'staves'
							score_minus_staves.setRGB(x, j, -1); //white
							just_staves.setRGB(x,j,0); //black
						}
					} else { //doesn't satisfy stave line segment constraints
						for (int j = y_actual; j < y_actual + run_length; j++) { // therefore leave black pixels in 
							// from 'score_minus_staves' and remove them from 'staves'
							score_minus_staves.setRGB(x, j, 0); //black
							just_staves.setRGB(x,j,-1); //white
						}
					}


				} else { //white -> set pixels to white in 'score_minus_staves' and 'staves'
					for (int j = y_actual; j < y_actual + run_length; j++) {
						score_minus_staves.setRGB(x, j, -1); //white
						just_staves.setRGB(x,j,-1); //white
					}
				}

				y_actual = y_actual + run_length;

			}
		}
		

		
		// 3. removal of false segments ////////////////////////////////////////////////////////////////////////////////
		// using 'neighbouring components algorithm'
		//
		BufferedImage just_staves_after_step3 = ImageProcessing.copyImage(just_staves);
		BufferedImage score_minus_staves_after_step3 = ImageProcessing.copyImage(score_minus_staves);
		
		BufferedImage falsley_removed_segments_step3 = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		ImageProcessing.makeImageWhite(falsley_removed_segments_step3);
		
		int T2_step_3_parameter = 2*scoreMetrics.getStaveLine_height();
		
		Run_Length_Encoding rleStaves = new Run_Length_Encoding(just_staves); // run length encoding of (provisional) image of just staves
		int[][] staves_rle_x = rleStaves.RLE_2D_along_x_axis();
				
		
		for(int y = 0; y < staves_rle_x.length; y++) {
//			System.out.println("y: " + y + "\tlength: " + staves_rle_x[y].length);
			int x_actual = 0;
			
			for (int j = 0; j < staves_rle_x[y].length; j++) {
				int run_length = staves_rle_x[y][j];
				Colour run_colour = Run_Length_Encoding.whatColourIsRun(y,j);
				
				if(run_colour == Colour.BLACK && run_length > 0) {
					int x_left = x_actual; // first pixel of run
					int x_right = x_actual + run_length - 1; // final pixel of run
					
					if(run_length < T2_step_3_parameter) { // run width less than T2 -> not a stave line segment

						for (int x = x_left; x <= x_right; x++) {
							falsley_removed_segments_step3.setRGB(x,y,0); // add to step 3 image
							score_minus_staves_after_step3.setRGB(x, y, 0); // add back into symbols image
							just_staves_after_step3.setRGB(x, y, -1); // remove from stave image
							
						}

					} else { // run width more than T2 -> neighbouring components check
						
						if(		( (hasLeftNeighbour(x_left, x_right, y, original_image)
									||hasRightNeighbour(x_left, x_right, y, original_image))
								&& (hasTopNeighbour(x_left, x_right, y, original_image) 
									|| hasBottomNeighbour(x_left, x_right, y, original_image)) )
							) { } else {
							// -> not a stave line segment
							
//							System.out.println("ENCOUNTERED step 3 neighbour check");
							for (int x = x_actual; x < x_actual + run_length; x++) {
								falsley_removed_segments_step3.setRGB(x,y,Color.BLACK.getRGB());
								score_minus_staves_after_step3.setRGB(x, y, 0); // add back to symbols image
								just_staves_after_step3.setRGB(x, y, -1); // remove from stave image
							}
						}
					}
				}
			
				x_actual = x_actual + run_length;
			}
		}
		
		
		// 4. re-addition of wrongly removed staveline segments  ///////////////////////////////////////////////////////
		// re-add segments which  satisfy some criteria to  be a valid staveline portion
		//	
		
		
		
		BufferedImage just_staves_after_step4 = ImageProcessing.copyImage(just_staves_after_step3); // ie set of all valid staveline components
		BufferedImage score_minus_staves_after_step4 = ImageProcessing.copyImage(score_minus_staves_after_step3);
		BufferedImage step_4_wrongly_removed_staveline_segments = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		ImageProcessing.makeImageWhite(step_4_wrongly_removed_staveline_segments);
		
		Run_Length_Encoding rleFalsley_removed_segments = new Run_Length_Encoding(falsley_removed_segments_step3); // run length encoding of (provisional) image of just staves
		int[][] falsley_removed_segments_rle_x = rleFalsley_removed_segments.RLE_2D_along_x_axis();
		
		
		for(int y = 0; y < falsley_removed_segments_rle_x.length; y++) {
			int x_actual = 0;
			
			for (int j = 0; j < falsley_removed_segments_rle_x[y].length; j++) {
				int run_length = falsley_removed_segments_rle_x[y][j];
				Colour run_colour = Run_Length_Encoding.whatColourIsRun(y,j);
				
//				System.out.println("[EXAMINING RUN] run_length: " + run_length + "\trun_colour: " + run_colour.toString());
				
				if(run_colour == Colour.BLACK  && run_length > 0) {
					int x_left = x_actual; // first pixel of run
					int x_right = x_actual + run_length - 1; // final pixel of run
		
					boolean l = hasLeftNeighbour(x_left, x_right, y, just_staves_after_step3);
					boolean r = hasRightNeighbour(x_left, x_right, y, just_staves_after_step3);
					boolean t = hasTopNeighbour(x_left, x_right, y, just_staves_after_step3);
					boolean b = hasBottomNeighbour(x_left, x_right, y, just_staves_after_step3);
//					System.out.println("\t l:" + l + " r:" + r + " t:" + t + " b:" + b);
					
					if( (l || r) && (t || b) ) {
//							( hasLeftNeighbour(x_left, x_right, y, just_staves_after_step3)
//								|| hasRightNeighbour(x_left, x_right, y, just_staves_after_step3) )
//							&& (hasTopNeighbour(x_left, x_right, y, just_staves_after_step3) 
//								|| hasBottomNeighbour(x_left, x_right, y, just_staves_after_step3) )
					
					
						// wrongly removed staveline segment -> readd to valid staveline component image
						for (int x = x_left; x <= x_right; x++) {
							just_staves_after_step4.setRGB(x, y, 0); //add back to staves image
							score_minus_staves_after_step4.setRGB(x, y, -1); // remove from symbols image
							step_4_wrongly_removed_staveline_segments.setRGB(x,y,0); //add to step 4 image
						}
//						System.out.println("ENCOUNTERED step 4 neighbour check");
					} else {
//						System.out.println("Run failed step 4 neighbours check\tx: " + x_left + " - " + x_right + " y: " + y);
					}
				}
			}
		}
		
		
		
		/////////////////////////////////////////
		// my algs... 
		// want to build complete picture of staves
		
		//1) horizontal run length smoothing of stave image with parameter staveSmoothingParameter
		// ensures staves with lots and lots of notes are dealt with (so lots of white gaps in stave lines)
		
		BufferedImage horizontallySmoothedStaves = ImageProcessing.copyImage(just_staves_after_step4);
		
		int staveSmoothingParameter = 3*(scoreMetrics.getNoteHead_width() + scoreMetrics.getNoteStem_width()) ;
		 // FOR NOW HAVE DOUBLED SMOOTHING PARAMETER to get finished stave lines for position calculations...
		
		Run_Length_Encoding rle2 = new Run_Length_Encoding(horizontallySmoothedStaves);
		int[][] staves_horizontal_rle = rle2.RLE_2D_along_x_axis();
		
		for(int y = 0; y < staves_horizontal_rle.length; y++) {
			int x_actual = 0;
			
			for (int j = 0; j < staves_horizontal_rle[y].length; j++) {
				int run_length = staves_horizontal_rle[y][j];
				Colour run_colour = Run_Length_Encoding.whatColourIsRun(y,j); // makes code easier to read

				if(run_colour == Colour.WHITE && run_length <= staveSmoothingParameter) {
					for (int x = x_actual; x < x_actual + run_length; x++) {
						horizontallySmoothedStaves.setRGB(x, y, 0); //black
					}
				}
				x_actual = x_actual + run_length;
			}
		}
				
		//2) remove horizontal runs < 2*noteHead_width
		// this attempts to remove stray bits of symbols etc
		
		//3) another horizontal run length smoothing with big paramater eg 4*noteHeadWidth
		
		//4) anything removed in 2 which was not then added back in 3, add back to symbols image
		// ie iterate through all runs removed in 2
		//	if the equivalently located pixels in the stave image (after step 3) are all white, add back to symbols image
		//  (as probably part of a beam or phrase mark)
		
		
		
		
		
		
		ImageProcessing.displayImage(horizontallySmoothedStaves, "(new) horizontallySmoothedStaves");

		
		
//		ImageProcessing.displayImage(horizontallySmoothedImage_step1, "(Step 1) Horizontal run length smoothing");
//		ImageProcessing.displayImage(score_minus_staves, "(Step 2) Stave without stave lines");
//		ImageProcessing.displayImage(just_staves, "(Step 2) just_staves");
//		ImageProcessing.displayImage(falsley_removed_segments_step3, "(Step 3) Falsley removed Segments (ie these arent stave line segments whearas we previously thought they were");
//		ImageProcessing.displayImage(score_minus_staves_after_step3, "(Step 3) Stave without stave lines after falsely stave line segments readded");
//		ImageProcessing.displayImage(just_staves_after_step3, "(Step 3) just_staves after false stave line segments removed");
//		
//		ImageProcessing.displayImage(step_4_wrongly_removed_staveline_segments, "(Step 4) Wrongly removed staveline segments");
//		ImageProcessing.displayImage(just_staves_after_step4, "(Step 4) just_staves_after_step4");
		ImageProcessing.displayImage(score_minus_staves_after_step4, "(Step 4) Stave minus stave lines");

		ImageProcessing.saveImage(horizontallySmoothedStaves, "Just stave lines");
		
		
		// set state of StaveDetection object
		staveWithoutStaveLines = score_minus_staves_after_step4;
		justStaveLines = just_staves;
		
		smoothedStaveLines = horizontallySmoothedStaves;
		
		
	}
	
	
	
	
	
	private boolean hasLeftNeighbour(int x_left, int x_right, int y_value, BufferedImage image) {
		//check original image for connected component to the left
		int x_value = x_left - 1 > 0 ? x_left - 1 : 0;
		if(image.getRGB(x_value, y_value) == Color.BLACK.getRGB()) {
//			System.out.println("it did happen!l");
			return true;
		} else {
			return false;
		}
	}
	
	private boolean hasRightNeighbour(int x_left, int x_right, int y_value, BufferedImage image) {
		//check original image for connected component to the right
		int x_value = x_right + 1 < image.getWidth() ? x_right + 1 : image.getWidth() - 1;
		if(image.getRGB(x_value, y_value) == Color.BLACK.getRGB()) {
//			System.out.println("it did happen!r");
			return true;
		} else {
			return false;
		}
	}
	
	private boolean hasTopNeighbour(int x_left, int x_right, int y_value, BufferedImage image) {
		//check rectangle above this run for the presence of any black pixels (in image)
		
		int provis_y_top_val = y_value - (scoreMetrics.getStaveLine_height() + scoreMetrics.getStaveSpace_height()); // y value increases downwards
		int y_top_value = provis_y_top_val < 0 ? 0 : provis_y_top_val;
		
		for(int x = x_left; x <= x_right; x++) {
			for(int y = y_top_value; y< y_value; y++ ) {
				if(image.getRGB(x,y) == Color.BLACK.getRGB()) {
//					System.out.println("it did happen!t");
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean hasBottomNeighbour(int x_left, int x_right, int y_value, BufferedImage image) {
		//check rectangle below this run for the presence of any black pixels (in image)
		
		int provis_y_bottom_val = y_value + scoreMetrics.getStaveLine_height() + scoreMetrics.getStaveSpace_height();
		int y_bottom_value = provis_y_bottom_val >= image.getHeight() ? image.getHeight() - 1 : provis_y_bottom_val;
		
		for(int x = x_left; x <= x_right; x++) {
			for(int y = (y_value + 1); y < y_bottom_value; y++) { // + 1?
				if(image.getRGB(x,y) == Color.BLACK.getRGB()) {
//					System.out.println("it did happen!b");
					return true;
				}
			}
		}
		return false;
	}
	
}
