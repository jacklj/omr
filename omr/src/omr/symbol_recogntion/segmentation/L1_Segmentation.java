package omr.symbol_recogntion.segmentation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import omr.symbol_recogntion.score_metrics.ScoreMetrics;
import omr.symbol_recogntion.stave_detection.StaveRemovalOld;
import omr.util.Colour;
import omr.util.DisplayImage;
import omr.util.ImageProcessing;
import omr.util.Run_Length_Encoding;


//LOGIC //////
	// take in l0_Segment
//			decide which type it is -> group of beamed notes or not (caution: could be particularly dense group of objects!)
//			if(beamed note group) {
//				do note head detection/ L2_segmentation
//				(if beaming algs fail, probably dense group of objects-> send back up to L1_Segmentation, do connected 
//				component analysis (with morphological operations to attempt to solve problems with overlapping objects?)
//			}
//			else if(already single note / single symbol) {
//				seperate different symbols vertically (eg note and sticcato dot)
//					-> that's the segmentation done-> ready for classification
//			}
	//
	

public class L1_Segmentation {		
	
	// state ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private ScoreMetrics scoreMetrics;
	
	
	// main ////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
		BufferedImage inputImage = ImageProcessing.loadImage("/Users/buster/Stuff/Academia/II/DISSERTATION/test_images/beamedNoteStave.png");

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
//			List<L1_Segment> l1_segment_list = nHd.detect(NOTE_STEM_DETECTION_BASED_ON_RLE_ALG);
			



		}

	}

	
	// constructor /////////////////////////////////////////////////////////////////////////////////////////////////////
	public L1_Segmentation(ScoreMetrics score_metrics) {
		
		scoreMetrics = score_metrics;		
	}
	
	
	// public methods //////////////////////////////////////////////////////////////////////////////////////////////////
	public List<L1_Segment> segment(L0_Segment l0segment) {
		
		List<L1_Segment> l1_segment_list = new ArrayList<L1_Segment>();
		
		
//		decide which type it is -> group of beamed notes or not (caution: could be particularly dense group of objects!)
		int max_simple_symbol_width = (int)2*scoreMetrics.getNoteHead_width();

		
		if(l0segment.get_width() < max_simple_symbol_width) {
			//simple symbol already -> copy entire l0 Segment into a L1 Segment and insert into l1_segment_list

			// remove stem (first check not just barline)

			// detect note stems by searching for long vertical lines using vertical run length encoding

			L1_Segment l1segment = new L1_Segment();

			boolean noteFound = false;
			BufferedImage withoutNoteStems = ImageProcessing.copyImage(l0segment.get_image());

			
			// generate new BufferedImage just of note stems ///////////////////////////////////////////////////////////
			if(l0segment.get_width() > scoreMetrics.getNoteHead_width() / 2) { // not barline
				
//				BufferedImage noteStems = new BufferedImage(l0segment.get_width(), l0segment.get_height(), BufferedImage.TYPE_BYTE_BINARY);
//				ImageProcessing.makeImageWhite(noteStems);


//				System.out.println("noteStems image created - (width, height) (" + width + "," + height + ")" );
				////////////////////////////////////////////////////////////////////////////////////////////////////////////

				// search for long vertical lines in vertical run length encoding -> probably part of note stems ///////////
				int[][] rle_y = Run_Length_Encoding.RLE_2D_along_y_axis(l0segment.get_image());


				for(int x = 0; x < rle_y.length; x++) {
					int y_actual = 0;

					for(int j = 0; j < rle_y[x].length; j++) {
						int run_length = rle_y[x][j];
						Colour run_colour = Run_Length_Encoding.whatColourIsRun(x,j);

						if(run_colour == Colour.BLACK 
								&& (scoreMetrics.getNoteStem_height_min() + 50 <= run_length) 	&& (run_length <= scoreMetrics.getNoteStem_height_max())) {
							// note stem candidate -> add to image of note stems ('noteStems') and remove from withoutNoteStems
							System.out.println("Note stem run found: note stem height = " + run_length);
							noteFound = true;
							for (int y = y_actual; y < y_actual + run_length; y++) { 
//								noteStems.setRGB(x, y, 0); //black
								withoutNoteStems.setRGB(x,y,-1); // white
							}
						} 

						y_actual = y_actual + run_length;
					}
				}

				////////////////////////////////////////////////////////////////////////////////////////////////////////////
			}
			
			
			
			
			
			
			l1segment.set_left_x(l0segment.get_left_x());
			l1segment.set_right_x(l0segment.get_right_x());
			l1segment.set_top_y(l0segment.get_top_y());
			l1segment.set_bottom_y(l0segment.get_bottom_y());
			l1segment.set_image(withoutNoteStems);
			
			l1segment.setNoteDetected(noteFound);
			
			l1_segment_list.add(l1segment);
			
			
			
		} else {
			
			// beamed notes or dense symbol cluster
			
			Beamed_Notes_Segmentation nHd = new Beamed_Notes_Segmentation(l0segment, this.scoreMetrics);
			l1_segment_list = nHd.detect(Beamed_Notes_Segmentation.NOTE_STEM_DETECTION_BASED_ON_RLE_ALG);
			
			
			
		}

		
		return l1_segment_list;
		
	}
	
	//private methods //////////////////////////////////////////////////////////////////////////////////////////////////
	
	
}
