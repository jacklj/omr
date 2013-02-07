package omr.symbol_recogntion.segmentation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import omr.symbol_recogntion.classifier.Classifier;
import omr.symbol_recogntion.classifier.complexSymbolSet.Symbol;
import omr.symbol_recogntion.score_metrics.ScoreMetrics;
import omr.symbol_recogntion.segmentation.L0_Segment;
import omr.symbol_recogntion.segmentation.L0_Segmentation;
import omr.symbol_recogntion.segmentation.L1_Segment;
import omr.symbol_recogntion.segmentation.L1_Segmentation;
import omr.symbol_recogntion.segmentation.L2_Segment;
import omr.symbol_recogntion.segmentation.L2_Segmentation;
import omr.symbol_recogntion.stave_detection.StaveRemovalOld;

public class SegmenterOBSOLETE { // possibly call this omr.run or something and have as top level program (the one you call)
		
	//takes in a whole stave minus stave lines
	// and a ScoreMetrics object
	
	// run L0_segmentation -> list of l0_Segments
	
	// iterate through these, doing L1_Segmentation
	
	public static void main(String[] args) {
		
		String filename = "ich_grolle_nicht_small";
		
		BufferedImage imageOfOneStave = null;
		try {
			imageOfOneStave = ImageIO.read(new File("//Users//buster//Stuff//Academia//II//DISSERTATION//test_images//" + filename 
					+ ".png")); 
		} catch (FileNotFoundException e) {
			System.out.println("[ERROR] File not found exception");
		} catch (IOException e) {
			System.out.println("[ERROR] IO exception");
		}
		
		
		ScoreMetrics score_metrics = new ScoreMetrics(imageOfOneStave);

		StaveRemovalOld stave_removal = new StaveRemovalOld(score_metrics, imageOfOneStave);
		BufferedImage staveWithoutStaveLines = stave_removal.getStaveWithoutStaveLines();
		BufferedImage justStaves = stave_removal.getJustStaveLines();

		SegmenterOBSOLETE segmenter = new SegmenterOBSOLETE(score_metrics, staveWithoutStaveLines);
	}
	
	
	public SegmenterOBSOLETE(ScoreMetrics score_metrics, BufferedImage imageOfOneStave) {
		
		segment(score_metrics, imageOfOneStave);

	}

	// private methods ///////////////////////////////////////////////////////////////////////////////////////////////
	private void segment(ScoreMetrics score_metrics, BufferedImage imageofOneStave) {
		
		
		L0_Segmentation l0Segmentation = new L0_Segmentation(score_metrics);
		L1_Segmentation l1Segmentation = new L1_Segmentation(score_metrics);
		L2_Segmentation l2Segmentation = new L2_Segmentation(score_metrics);
		
		Classifier classifier = new Classifier();
		
		
		List<L0_Segment> l0_segment_list = l0Segmentation.segment(imageofOneStave);
		
		
		for(L0_Segment l0_segment : l0_segment_list) {
			List<L1_Segment> l1_segment_list= l1Segmentation.segment(l0_segment);
			
			
			for(L1_Segment l1_segment : l1_segment_list) {
				List<L2_Segment> l2_segment_list= l2Segmentation.segment(l1_segment);
				
				List<Symbol> symbols_present_in_l1_segment = new ArrayList<Symbol>();
				
				for(L2_Segment l2_segment : l2_segment_list) {
					Symbol symbol= classifier.classify(l2_segment);
					
					symbols_present_in_l1_segment.add(symbol);
					
				}
				// symbols_present_in_l1_segment; complete
				// now construct internal representation??
				//what order are the symbols in in symbols_present_in_l1_segment - top down? bottom up?

			}
			
		}

		
		
	}
	
	
}

