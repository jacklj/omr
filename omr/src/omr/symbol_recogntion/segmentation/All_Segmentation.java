package omr.symbol_recogntion.segmentation;


//INFO /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//1. takes an image of a whole page of music (binarised)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import omr.OMR;
import omr.symbol_recogntion.classifier.Classifier;
import omr.symbol_recogntion.classifier.ImageNormaliser;
import omr.symbol_recogntion.classifier.complexSymbolSet.Symbol;
import omr.symbol_recogntion.classifier.complexSymbolSet.SymbolType;
import omr.symbol_recogntion.score_metrics.ScoreMetrics;
import omr.symbol_recogntion.segmentation.L0_Segment;
import omr.symbol_recogntion.segmentation.L0_Segmentation;
import omr.symbol_recogntion.segmentation.L1_Segment;
import omr.symbol_recogntion.segmentation.L1_Segmentation;
import omr.symbol_recogntion.segmentation.L2_Segment;
import omr.symbol_recogntion.segmentation.L2_Segmentation;
import omr.symbol_recogntion.segmentation.Score;
import omr.symbol_recogntion.segmentation.Stave;
import omr.symbol_recogntion.segmentation.Stave_Segmentation;
import omr.symbol_recogntion.stave_detection.StaveRemoval;
import omr.symbol_recogntion.stave_detection.StaveRemovalOld;
import omr.symbol_recogntion.stave_detection.StaveIdentification;
import omr.util.ImageProcessing;





public class All_Segmentation {

	Score score;

	public static void main(String[] args) {		

		BufferedImage img = ImageProcessing.loadImage("/Users/buster/Stuff/Academia/II/DISSERTATION/test_images/sibelius_opus_simple_stave.png");
		ImageProcessing.displayImage(img, "Input image");
		
		img = ImageProcessing.preprocess(img);
		ImageProcessing.displayImage(img, "Binarised");
		
		All_Segmentation sr = new All_Segmentation(img);
		// returns the internal representation of the score, built up from within each segment
	}
	
	
	
	
	/// constructor ////////////////////////////////////////////////////////////////////////////////////////////////////
	public All_Segmentation(BufferedImage stave_image) { // at the moment takes image of one stave
	
		ScoreMetrics score_metrics = new ScoreMetrics(stave_image);
				
		// initialise segmenters
		L0_Segmentation l0Segmentation = new L0_Segmentation(score_metrics);
		L1_Segmentation l1Segmentation = new L1_Segmentation(score_metrics);
		L2_Segmentation l2Segmentation = new L2_Segmentation(score_metrics);


		// segment - at each level building up segment object heirarchy
		
		// stave removal
			
		StaveRemoval stave_removal = new StaveRemoval(score_metrics, stave_image, 0); // remove stave lines
			BufferedImage staveWithoutStaveLines = stave_removal.getStaveWithoutStaveLines();

						
			List<L0_Segment> l0_segment_list = l0Segmentation.segment(staveWithoutStaveLines); // l0 segmentation
			
			int l0count = 0;
			
			for(L0_Segment l0_segment : l0_segment_list) {
				l0count++;
				int  l1count = 0;
								
				List<L1_Segment> l1_segment_list = l1Segmentation.segment(l0_segment); // l1 segmentation
				l0_segment.setL1_SegmentList(l1_segment_list);

				for(L1_Segment l1_segment : l1_segment_list) {
					l1count++;
					int l2count = 0;
					
					
					List<L2_Segment> l2_segment_list = l2Segmentation.segment(l1_segment); //l2 segment list
					l1_segment.setL2_SegmentList(l2_segment_list);

//					for(L2_Segment l2_segment : l2_segment_list) {
//						l2count++;
//						
////						//normalise image of symbol
////						BufferedImage l2_image = l2_segment.get_image();
////						BufferedImage normalized = ImageProcessing.resizeSymbol(l2_image , boxSize);
//////						ImageProcessing.displayImage(l2_image, "an l2 image");
////						
////						ImageProcessing.saveImage(normalized, "/Users/buster/Stuff/Academia/II/DISSERTATION/testOut//Stave" + staveCount + "/l0_Segment" + l0count + "/L1_Segment" + l1count + "/", "l2" + l2count + ".png");
////								
//////						BufferedImage normalisedImage = ImageNormaliser.normalise(l2_image);
////
//////						//then classify
//////						SymbolType symbolType = classifier.classify(normalisedImage);
//////						Symbol symbol = new Symbol(symbolType, l2_segment.get_left_x(), l2_segment.get_right_x(), l2_segment.get_top_y(), l2_segment.get_bottom_y());
//////
//////						l2_segment.setSymbol(symbol);
//					}


				}

			}
			//segmentation done - display gui
		
				
	
	}
	
	// get set /////////
	
	public Score getScore() {
		return score;
	}
	
	public void display_segmentation_gui(BufferedImage wholeImage, List<L0_Segment> l0_seg_list) {
		JFrame frame = new JFrame("Display All_Segmentation Image");
		Panel panel = new All_Segmentation_GUI(wholeImage, l0_seg_list);
		frame.getContentPane().add(panel);
		frame.setSize(wholeImage.getWidth() + 10, wholeImage.getHeight()+26); // leaves room for OS window GUI stuff
		frame.setVisible(true);
	}
		
}
