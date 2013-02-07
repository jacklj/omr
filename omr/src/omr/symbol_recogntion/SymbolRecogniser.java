package omr.symbol_recogntion;


//INFO /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//1. takes an image of a whole page of music (binarised)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

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
import omr.symbol_recogntion.stave_detection.StaveRemovalOld;
import omr.symbol_recogntion.stave_detection.StaveIdentification;
import omr.util.ImageProcessing;
import omr.util.LoadImage;





public class SymbolRecogniser {

	Score score;

	public static void main(String[] args) {		

		BufferedImage img = LoadImage.load("/Users/buster/Stuff/Academia/II/DISSERTATION/test_images/sibelius_opus_simple_stave.png");
		ImageProcessing.displayImage(img, "Input image");
		
		img = ImageProcessing.preprocess(img);
		ImageProcessing.displayImage(img, "Binarised");
		
		SymbolRecogniser sr = new SymbolRecogniser(img);
		// returns the internal representation of the score, built up from within each segment
	}
	
	
	
	
	/// constructor ////////////////////////////////////////////////////////////////////////////////////////////////////
	public SymbolRecogniser(BufferedImage score_image) { // at the moment takes image of one stave
	
		ScoreMetrics score_metrics = new ScoreMetrics(score_image);
		
		int boxSize = 20; // classifier needs to know this to know which trained classifier to use, and need to resize symbol images to this
		
		// initialise segmenters + classifier
		Stave_Segmentation staveSegmentation = new Stave_Segmentation(score_metrics);
		L0_Segmentation l0Segmentation = new L0_Segmentation(score_metrics);
		L1_Segmentation l1Segmentation = new L1_Segmentation(score_metrics);
		L2_Segmentation l2Segmentation = new L2_Segmentation(score_metrics);
		Classifier classifier = new Classifier(); // arguments will be stuff like type of classifier(SVM, ANN), expected image size(?), trained classifier filepath(?) etc.


		// segment then classify, at each level building up segment object heirarchy
		
		List<Stave> listOfStaves = staveSegmentation.segment(score_image);
		score = new Score(score_image, listOfStaves);
		score.setScoreMetrics(score_metrics);
		
		int staveCount = 0;
		
		for (Stave stave : listOfStaves) {
			staveCount++;
			
			StaveRemovalOld stave_removal = new StaveRemovalOld(score_metrics, stave.get_image()); // remove stave lines
			BufferedImage staveWithoutStaveLines = stave_removal.getStaveWithoutStaveLines();
			stave.setStave_without_stave_lines(staveWithoutStaveLines);

			StaveIdentification sK = new StaveIdentification(stave_removal.getSmoothedStaveLines(), score_metrics, 
					StaveIdentification.ALGORITHM_TEMPLATE_MATCHING); // calculate stave line skeleton
			stave.setStaveSkeleton(sK);
			
			
			ImageProcessing.saveImage(stave.getStave_without_stave_lines(), "/Users/buster/Stuff/Academia/II/DISSERTATION/testOut/Stave" + staveCount + "/", "stave" + staveCount + ".png");
			
			List<L0_Segment> l0_segment_list = l0Segmentation.segment(staveWithoutStaveLines); // l0 segmentation
			stave.set_L0_Segment_list(l0_segment_list);
			
			int l0count = 0;
			
			for(L0_Segment l0_segment : l0_segment_list) {
				l0count++;
				int  l1count = 0;
				
				ImageProcessing.saveImage(l0_segment.get_image(), "/Users/buster/Stuff/Academia/II/DISSERTATION/testOut//Stave" + staveCount + "/l0_Segment" + l0count + "/", "l0" + l0count + ".png");
				
				List<L1_Segment> l1_segment_list = l1Segmentation.segment(l0_segment); // l1 segmentation
				l0_segment.setL1_SegmentList(l1_segment_list);

				for(L1_Segment l1_segment : l1_segment_list) {
					l1count++;
					int l2count = 0;
					
					ImageProcessing.saveImage(l1_segment.get_image(), "/Users/buster/Stuff/Academia/II/DISSERTATION/testOut//Stave" + staveCount + "/l0_Segment" + l0count + "/L1_Segment" + l1count + "/", "l1" + l1count + ".png");

					
					List<L2_Segment> l2_segment_list = l2Segmentation.segment(l1_segment); //l2 segment list
					l1_segment.setL2_SegmentList(l2_segment_list);

					for(L2_Segment l2_segment : l2_segment_list) {
						l2count++;
						
						//normalise image of symbol
						BufferedImage l2_image = l2_segment.get_image();
						BufferedImage normalized = ImageProcessing.resizeSymbol(l2_image , boxSize);
//						ImageProcessing.displayImage(l2_image, "an l2 image");
						
						ImageProcessing.saveImage(normalized, "/Users/buster/Stuff/Academia/II/DISSERTATION/testOut//Stave" + staveCount + "/l0_Segment" + l0count + "/L1_Segment" + l1count + "/", "l2" + l2count + ".png");
								
//						BufferedImage normalisedImage = ImageNormaliser.normalise(l2_image);

//						//then classify
//						SymbolType symbolType = classifier.classify(normalisedImage);
//						Symbol symbol = new Symbol(symbolType, l2_segment.get_left_x(), l2_segment.get_right_x(), l2_segment.get_top_y(), l2_segment.get_bottom_y());
//
//						l2_segment.setSymbol(symbol);
					}


				}

			}
		}
		
		// symbol_recognition done -> have get method to retrieve the internal score representation 'score'
		
	
	}
	
	// get set /////////
	
	public Score getScore() {
		return score;
	}
		
}
