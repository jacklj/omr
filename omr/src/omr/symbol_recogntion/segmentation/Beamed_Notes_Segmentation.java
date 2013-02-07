package omr.symbol_recogntion.segmentation;

import java.awt.Color;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import omr.symbol_recogntion.classifier.Classifier;
import omr.symbol_recogntion.score_metrics.ScoreMetrics;
import omr.symbol_recogntion.score_metrics.ScoreMetricsCalculator;
import omr.symbol_recogntion.stave_detection.StaveRemovalOld;
import omr.util.Colour;
import omr.util.DisplayImage;
import omr.util.ImageProcessing;
import omr.util.Projection;
import omr.util.Run_Length_Encoding;
import omr.util.WriteImageToFile;
import omr.util.XYChart;


//INFO /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//1. 
// how to structure??
//
// in-> image of beamed notes cluster -> a large L0_Semgent
//
// out-> list of l1 segments each also having the coordinates of any note heads (both bounding box and centroid)
//
// if L0_Segment not wide enough to be a beamed note, just find centroid (and turn into a L1_Segment for consistency?)
//
//then all these segments are sent to the classifier?

// also have alternative algorithms?
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//KNOWN BUGS ///////////////////////////////////////////////////////////////////////////////////////////////////////////
//1. 
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//TO DO ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//1.
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



public class Beamed_Notes_Segmentation {

	/// state //////////////////////////////////////////////////////////////////////////////////////////////////////////	
	//	private int height;
	//	private int width;
	//	private BufferedImage image;
	private L0_Segment l0segment;
	private boolean anyNotesFound = false;
	private List<L1_Segment> l1SegmentList;
	private int  howManyNotesFound = 0;




	public static final int NOTE_STEM_DETECTION_BASED_ON_RLE_ALG = 1;
	public static final int NOTE_HEAD_DETECTION_BASED_ON_RLE_ALG = 2;



	//temporary constants - eventually get from ScoreConstants

	//private int WHICH_ALGORITHM = NOTE_STEM_DETECTION_BASED_ON_RLE_ALG;

	ScoreMetrics scoreMetrics;
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	public static void main(String[] args) {

		BufferedImage inputImage = ImageProcessing.loadImage("/Users/buster/Stuff/Academia/II/DISSERTATION/test_images/beamedNoteGroup.png");

		BufferedImage stave_image = ImageProcessing.preprocess(inputImage);
		ImageProcessing.saveImage(stave_image, "stave_image.png");
		ScoreMetrics score_metrics = new ScoreMetrics(stave_image);

		// initialise segmenters + classifier
		L0_Segmentation l0Segmentation = new L0_Segmentation(score_metrics);




		StaveRemovalOld stave_removal = new StaveRemovalOld(score_metrics, stave_image);
		BufferedImage staveWithoutStaveLines = stave_removal.getStaveWithoutStaveLines();
		
		
		ImageProcessing.displayImage(staveWithoutStaveLines, "Stave without stave lines");
		//BufferedImage justStaveLines = stave_removal.getJustStaveLines(); // gonna need stats from this for notation reconstruction probably...




		List<L0_Segment> l0_segment_list = l0Segmentation.segment(staveWithoutStaveLines);

		for(L0_Segment l0_segment : l0_segment_list) {

			DisplayImage di = new DisplayImage(l0_segment.get_image(), "L0_Segment");
			di.display();

			Beamed_Notes_Segmentation nHd = new Beamed_Notes_Segmentation(l0_segment, score_metrics);
			List<L1_Segment> l1_segment_list = nHd.detect(NOTE_STEM_DETECTION_BASED_ON_RLE_ALG);
			



		}

	}


	/// constructor ////////////////////////////////////////////////////////////////////////////////////////////////////
	public Beamed_Notes_Segmentation(L0_Segment l0segment, ScoreMetrics sm) {
		this.l0segment = l0segment;
		this.scoreMetrics = sm;
	}

	/// public methods /////////////////////////////////////////////////////////////////////////////////////////////////
	public int getHowManyNotesFound() {
		return howManyNotesFound;
	}


	public void setHowManyNotesFound(int howManyNotesFound) {
		this.howManyNotesFound = howManyNotesFound;
	}

	public boolean getAnyNotesFoundBool() {
		return anyNotesFound;

	}

	public List<L1_Segment> getL1_Segment_list() {
		return l1SegmentList;
	}


	public List<L1_Segment> detect(int which_algorithm) {

		BufferedImage l0_segment_image = l0segment.get_image();
		int width = l0_segment_image.getWidth();
		int height = l0_segment_image.getHeight();
		
		List<L1_Segment> l1_segment_list = new ArrayList<L1_Segment>();

		// this is for dealing with an L0 segment containing a block of beamed notes (plus possibly accidentals)

		// DIFFERENT ALGORITHMS TO ACHIEVE THIS
		// 2 main ways to detect individual notes in a beamed note section: 
		// 	1) Detect note heads using blob detection
		//
		//	2) Detect note stems by searching for long vertical lines



		if(which_algorithm == NOTE_STEM_DETECTION_BASED_ON_RLE_ALG) {
			// detect note stems by searching for long vertical lines using vertical run length encoding

			// generate new BufferedImage just of note stems ///////////////////////////////////////////////////////////
			BufferedImage noteStems = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
			ImageProcessing.makeImageWhite(noteStems);
			BufferedImage withoutNoteStems = ImageProcessing.copyImage(l0_segment_image);

			
			
			System.out.println("noteStems image created - (width, height) (" + width + "," + height + ")" );
			////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
			// search for long vertical lines in vertical run length encoding -> probably part of note stems ///////////
			Run_Length_Encoding rle = new Run_Length_Encoding(l0_segment_image);
			int[][] rle_y = rle.RLE_2D_along_y_axis();

			
			for(int x = 0; x < rle_y.length; x++) {
				int y_actual = 0;

				for(int j = 0; j < rle_y[x].length; j++) {
					int run_length = rle_y[x][j];
					Colour run_colour = Run_Length_Encoding.whatColourIsRun(x,j);

					if(run_colour == Colour.BLACK 
							&& (scoreMetrics.getNoteStem_height_min() + 10 <= run_length) 	&& (run_length <= scoreMetrics.getNoteStem_height_max())) {
						// note stem candidate -> add to image of note stems ('noteStems') and remove from withoutNoteStems
						System.out.println("Note stem run found: note stem height = " + run_length);
						for (int y = y_actual; y < y_actual + run_length; y++) { 
							noteStems.setRGB(x, y, 0); //black
							withoutNoteStems.setRGB(x,y,-1); // white
						}
					} 

					y_actual = y_actual + run_length;
				}
			}	
			////////////////////////////////////////////////////////////////////////////////////////////////////////////

			DisplayImage di = new DisplayImage(noteStems, "note stems image");
			di.display();
			DisplayImage di2 = new DisplayImage(withoutNoteStems, "without note stems image");
			di2.display();
			
			WriteImageToFile.write(noteStems, "notestemsimage.png");
			
			//now get note stem bounding box coordinates.///////////////////////////////////////////////////////////////
			//1) do x axis bounding 
			// use x projection -> will increase algorithms resiliance to imperfectly vertical stems (due to skew)
			Projection noteStemsProject = new Projection(noteStems);
			int[] xproj_of_noteStems = noteStemsProject.xProject();
			
			
			System.out.print("x projection of noteHead image data: ");
			for(int x = 0; x < xproj_of_noteStems.length; x++) {System.out.print(xproj_of_noteStems[x] + ", ");}
			System.out.println();
			
			XYChart xProjChart = new XYChart(xproj_of_noteStems, xproj_of_noteStems.length, "X projection of note stems");
			xProjChart.display(800, 400);
//			

			
			List<Note_Stem> note_stem_list = new ArrayList<Note_Stem>();
			
			for(int x = 0; x < xproj_of_noteStems.length; x++) {
				if(xproj_of_noteStems[x] != 0) {
					int noteStem_x_left = x;
					for(int i = x; i < xproj_of_noteStems.length; i++) {
						if(xproj_of_noteStems[i]== 0 || i == xproj_of_noteStems.length - 1) {
							x = i;
							int noteStem_x_right = x-1;
							//now have notestem x coords
							note_stem_list.add(new Note_Stem(noteStem_x_left, noteStem_x_right));
							System.out.println("Notestem x coords: " + noteStem_x_left + "," + noteStem_x_right);
							break;
							
						}
					}
					
				}
			}
			
			
//			//2) get bounding box along y axis
//			for(Note_Stem ns: note_stem_list) {
//				
//				
//				int yProjection[] = noteStemsProject.yProject(ns.getLeft_x(), ns.getRight_x(), 0, height - 1);
//
//				int top_y = 0; // just to initialise
//				int bottom_y = 0; // just to initialise
//				
//				for(int y = 0; y < height; y++) {
//					if(yProjection[y] > 0) {
//						//segStarty = (y == 0)? 0 : y-1; // pixel before first symbol pixel (unless y = 0, in which case segStart = 0)
//						top_y = y;
//						break;
//					}
//				}
//
//				for(int y = height - 1; y >= 0; y--) {
//					
//					if(yProjection[y] > 0) {
//						//segEndy = (y == height - 1)? y : y+1; // pixel after last symbol pixel // unless = bottom of image, in which case = y??????
//						bottom_y = y;
//						break;
//					}
//				}
//				ns.setTop_y(top_y);
//				ns.setBottom_y(bottom_y);
//				
//				System.out.println("Note stem coords: x " + ns.getLeft_x() + " - " + ns.getRight_x() + " y " + ns.getTop_y() + " - " + ns.getBottom_y());
//			}
			
			// now must decide what sides noteheads are on./////////////////////////////////////////////////////////////
			boolean noteHeadsOnLeft = false;
			if( 
					(note_stem_list.get(note_stem_list.size() - 1).getRight_x() + (int)(0.33*scoreMetrics.getNoteHead_width())) > (width - 1)
							) { // no note head on the right of the final note head -> note heads must be on left of stems (and beam must be on top)
				noteHeadsOnLeft = true;
				System.out.println("--> note heads on left.");
			} else {
				System.out.println("--> note heads on right.");
			}
			
			//now generate l1_segments
			
			int[] l1_seg_boundaries = new int[100];
			//l1_seg_boundaries[0] = 0;
			
			{int i = 0;
			
			if(noteHeadsOnLeft) {
			for(Note_Stem ns: note_stem_list) {
					int provisional_x_left = ns.getLeft_x() - scoreMetrics.getNoteHead_width();
					int x_left = provisional_x_left > 0 ? provisional_x_left: 0; // if < 0, make 0.
					l1_seg_boundaries[i] = x_left;
					
					i++;
					l1_seg_boundaries[i] = ns.getRight_x();
					i++;
				} 
			} else {
				for(Note_Stem ns: note_stem_list) {
					l1_seg_boundaries[i] = ns.getLeft_x();
					i++;
					
					int provisional_x_right = ns.getRight_x() + scoreMetrics.getNoteHead_width();
					int x_right = provisional_x_right < width ? provisional_x_right: width; // if > width, make width.
					l1_seg_boundaries[i] = x_right;
					
					i++;
				}
			}

			}
			
			

			
			for(int i = 0; i < l1_seg_boundaries.length - 1; i++) {
				if(i>0 && l1_seg_boundaries[i+1] == 0) {
					break;
				}
				
				int xl = l1_seg_boundaries[i];
				int xr = l1_seg_boundaries[i+1];
				int yt = l0segment.get_top_y();
				int yb = l0segment.get_bottom_y();
				
				L1_Segment l1s = new L1_Segment();
				l1s.set_left_x(xl);
				l1s.set_right_x(xr);
				l1s.set_top_y(yt);
				l1s.set_bottom_y(yb);
				System.out.println("L1_Segment details: x" + xl + "," + xr + "  y" + yt + ',' + yb);
				System.out.println("width = " + l1s.get_width() + "  height = " + l1s.get_height());
				BufferedImage l1sImage = withoutNoteStems.getSubimage(xl, 0, xr-xl - 1, withoutNoteStems.getHeight());
				l1s.set_image(l1sImage);
				l1_segment_list.add(l1s);
			}
			
			
			
			display_segmentation_gui(l0_segment_image, l1_segment_list);
			save_all_segments_as_images("/Users/buster/eclipseWorkspace/omr/l1Segs/", l1_segment_list);
			
			
			//			for(Note_Stem ns: note_stem_list) {
//				
//				if(noteHeadsOnLeft) {
//					int provisional_x_left = ns.getLeft_x() - scoreMetrics.getNoteHead_width();
//					int x_left = provisional_x_left > 0 ? provisional_x_left: 0; // if < 0, make 0.
//					int x_right = ns.getRight_x();
//					int y_top = l0segment.get_top_y();
//					int y_bottom = l0segment.get_bottom_y();
//
//					L1_Segment l1s = new L1_Segment();
//					l1s.set_left_x(x_left);
//					l1s.set_right_x(x_right);
//					l1s.set_top_y(y_top);
//					l1s.set_bottom_y(y_bottom);
//				}
//				
//			}
			
				


			
			// or use threshold then scan along and record coordinates of black run lengths (use run length encoding??)
			// once you have stem lines, need to know whether note heads are on their left or right -> use beam location detection (on first and last stem)

			//howManyNotesFound++;

			// if no note stems found,  probably not a beamed group-> just highly clustered primitive symbols-> use minima in projection profile technique



		} else if (which_algorithm == NOTE_HEAD_DETECTION_BASED_ON_RLE_ALG) {


			// algorithm: remove all lines (leaving only blobs -> noteheads!) using run length encoding, then discarding 
			//			   all runs less than 2/3 *note_head.height or note_head.width long (as these will be sections of 
			//			   lines

			// generate new BufferedImage just of noteheads
			BufferedImage noteHeads = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

			//do run length encoding along y axis
			Run_Length_Encoding rle = new Run_Length_Encoding(l0_segment_image);
			int[][] rle_y = rle.RLE_2D_along_y_axis();


			for(int x = 0; x < rle_y.length; x++) {
				int y_actual = 0;

				for(int j = 0; j < rle_y[x].length; j++) {
					// eliminate black runs < 2/3 * note_head.height
					int run_length = rle_y[x][j];
					Colour run_colour = Run_Length_Encoding.whatColourIsRun(x,j);


					y_actual = y_actual + run_length;
				}
			}
			//do horizontal run length encoding
			// eliminate black runs < 2/3 * note_head.width

			//now all that's left should be blobs ie note_heads.


			// then 2 different algorithms to accomplish next bit...

			//1)
			//	do x projection on remaining image (ie project onto x axis)

			// run length encode the result. the black runs indicate the beginning/ends of l1_segments

			// then have to y project each l1_segment (only using the 'note_head image) to detect chords.

			//2) 
			// for each black run length (horizontal run length encoding) remaining, get centre and insert value into a list
			// sort list
			// group values closer than delta (a small constant, perhaps 1/2*note_head.width) together and take average
			//		-> this will give centre of L1_segment

			// do the same for the vertical run lenth encoding, with delta as perhaps 1/2* note_head.height.
			// 	-> this will give the centre of each note head (accounting for chords (multiple note heads on the same x axis
			//		position.


			//howManyNotesFound++;
		}


		//whatever algorithm found, if note head not found, probably cluster of primitive symbols -> use minima of x projection profile method

//		if(howManyNotesFound > 0) {
//			anyNotesFound = true;
//			//........
//		} else {
			//clustered symbol code
//			int[] first_derivative = new int[data.length];
//			int[] second_derivative = new int[data.length];
//			int[] second_deriv_over_0th_deriv = new int[data.length];
//
//			
//			for (int x = 0; x < data.length -2; x++) {
//				int first_deriv = data[x+1] - data[x];
//				first_derivative[x] = first_deriv;
//					
//					
//				int second_deriv = data[x+2] -2*data[x+1] + data[x];
//				second_derivative[x] = second_deriv;
//				
//				int divisor = 1;
//				if(data[x] != 0) {divisor = data[x];}
//				second_deriv_over_0th_deriv[x] = (int)second_deriv/divisor; 
//				
//			}
			
			
			
//			XYChart firstDiffChart = new XYChart(first_derivative, first_derivative.length, "1st derivative");
//			firstDiffChart.display(800, 400);
//			
//			XYChart secondDiffChart = new XYChart(second_derivative, second_derivative.length, "2nd difference");
//			secondDiffChart.display(800, 400);
//			
//			XYChart second_over_yDiffChart = new XYChart(second_deriv_over_0th_deriv, second_deriv_over_0th_deriv.length, "2nd difference (over f(x)) chart");
//			second_over_yDiffChart.display(800, 400);
//		}
		return l1_segment_list;
		
	}

	/// private methods ////////////////////////////////////////////////////////////////////////////////////////////////
//	public static void makeImageWhite(BufferedImage image) {
//		int width = image.getWidth();
//		int height = image.getHeight();
//		for(int x = 0; x < width; x++) {
//			for(int y = 0; y < height; y++) {
//				image.setRGB(x, y, Color.WHITE.getRGB());
//			}
//			
//		}
//	}
//	public static BufferedImage copyImage(BufferedImage source) {
//		int width = source.getWidth();
//		int height = source.getHeight();
//		BufferedImage newImg = new BufferedImage(width, height, source.getType());
//		for(int x = 0; x < width; x++) {
//			for(int y = 0; y < height; y++) {
//				newImg.setRGB(x,y, source.getRGB(x,y));
//			}
//		}
//		return newImg;
//		
//	}
	
	public static void display_segmentation_gui(BufferedImage image, List<L1_Segment> l1_segment_list) {
		JFrame frame = new JFrame("Display image (with L1_Segmentation Bounding Boxes)");
		Panel panel = new L1_Segmentation_GUI(image, l1_segment_list);
		frame.getContentPane().add(panel);
		frame.setSize(image.getWidth() + 10, image.getHeight()+26); // leaves room for OS window GUI stuff
		frame.setVisible(true);
	}
	
public static void save_all_segments_as_images(String name_of_folder_to_save_images_to, List<L1_Segment> l1_segment_list) {
		
		for(int i = 0; i < l1_segment_list.size(); i++) {
			
			BufferedImage symbolImage = (l1_segment_list.get(i)).get_image();
			
			String filename = name_of_folder_to_save_images_to + "symbol_" + i + ".png"; 
			
			File outputfile = new File(filename);
			try {  
				ImageIO.write(symbolImage, "png", outputfile);
				System.out.println("Saving symbol " + i + "\t\t in " + filename);
			} catch (IOException e) {
				System.out.println("ERROR: Probably not a valid filepath to save the symbols' images in");
			}
		}
	}
	
}
