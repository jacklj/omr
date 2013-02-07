package omr.symbol_recogntion.segmentation;

import java.awt.image.BufferedImage;
import java.util.List;

import omr.symbol_recogntion.score_metrics.ScoreMetrics;
import omr.symbol_recogntion.stave_detection.StaveRemovalOld;
import omr.util.DisplayImage;
import omr.util.ImageProcessing;

public class FujinagaBeamedNoteSegmentation {
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
	
}
