package omr.symbol_recogntion;


//INFO /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//1. takes an image of a whole page of music (binarised)

// simple - just does l0 segmentation, then classifies -> basic scores only (ie no composite symbols and only one symbol 
// per unit of time, therefore one symbol per L0 Segment)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import java.awt.image.BufferedImage;
import java.util.List;


import omr.symbol_recogntion.classifier.Classifier;
import omr.symbol_recogntion.classifier.basicSymbolSet.Symbol;
import omr.symbol_recogntion.classifier.basicSymbolSet.SymbolClass;
import omr.symbol_recogntion.score_metrics.ScoreMetrics;
import omr.symbol_recogntion.segmentation.L0_Segment;
import omr.symbol_recogntion.segmentation.L0_Segmentation;
import omr.symbol_recogntion.segmentation.L1_Segmentation;
import omr.symbol_recogntion.segmentation.L2_Segmentation;
import omr.symbol_recogntion.segmentation.Score;
import omr.symbol_recogntion.segmentation.Stave;
import omr.symbol_recogntion.segmentation.Stave_Segmentation;
import omr.symbol_recogntion.stave_detection.StaveRemovalOld;
import omr.symbol_recogntion.stave_detection.StaveIdentification;
import omr.util.ImageProcessing;





public class SymbolRecogniser_Simple {

	// state ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private Score score;
	private ScoreMetrics scoreMetrics;

	private final BufferedImage scoreImage;
	private final String scoreFilePath;
	private final String modelFilePath;
	private final int classifierInputImageBoxSize;
	private final int debugLevel;
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	public static void main(String[] args) {		

		String scorePath = "/Users/buster/Stuff/Academia/II/DISSERTATION/test_images/1bar.png";
		String modelFilePath = "/Users/buster/Stuff/Academia/II/DISSERTATION/SVM/basicSymbolSet_training/train.model";
		int boxSize = 20;
		int debugLevel = 4;
		
		// 1.load image
		BufferedImage img = ImageProcessing.loadImage(scorePath);
		ImageProcessing.displayImage(img, "Input image");
		
		// 2.preprocess
		img = ImageProcessing.preprocess(img);
		ImageProcessing.displayImage(img, "Binarised");
		
		// 3.symbol recogniser
		SymbolRecogniser_Simple sr = new SymbolRecogniser_Simple(img, scorePath, modelFilePath, boxSize, debugLevel);
		
		Score score = sr.getScore();
	}
	
	
	
	
	/// constructor ////////////////////////////////////////////////////////////////////////////////////////////////////
	public SymbolRecogniser_Simple(BufferedImage scoreImage, String scoreFilePath, String modelFilePath, int classifierInputImageBoxSize, int debugLevel) { 
					// can symbolClassifyingImageBoxSize be inferred from the modelFilePath?
	
		this.scoreImage = scoreImage;
		this.scoreFilePath = scoreFilePath;
		this.modelFilePath = modelFilePath;
		this.classifierInputImageBoxSize = classifierInputImageBoxSize; // classifier needs to know this to know which trained classifier to use, and need to resize symbol images to this
		
		this.debugLevel = debugLevel;
		
		
		this.recognise();
	}
	
	
	/// symbol recogniser //////////////////////////////////////////////////////////////////////////////////////////////
	private void recognise() {
		
		debug(1, "Calculate score metrics.");
		
		ScoreMetrics score_metrics = new ScoreMetrics(this.scoreImage);
		this.scoreMetrics = score_metrics;
		
		// initialise segmenters + classifier
		Stave_Segmentation staveSegmentation = new Stave_Segmentation(score_metrics);
		L0_Segmentation l0Segmentation = new L0_Segmentation(score_metrics);
		L1_Segmentation l1Segmentation = new L1_Segmentation(score_metrics);
		L2_Segmentation l2Segmentation = new L2_Segmentation(score_metrics);
		Classifier classifier = new Classifier(Classifier.SVM, modelFilePath); // arguments will be stuff like type of classifier(SVM, ANN), expected image size(?), trained classifier filepath(?) etc.
		
		debug(1, "segmenters and classifier initialised.");

		// segment then classify, at each level building up segment object hierarchy
		
		List<Stave> listOfStaves = staveSegmentation.segment(this.scoreImage);
		
		this.score = new Score(this.scoreImage, listOfStaves);
		this.score.setScoreSourceFilePath(scoreFilePath);
		this.score.setScoreMetrics(score_metrics);
		
		debug(1, "Segment score");
		
		int staveCount = 0;
		
		for (Stave stave : listOfStaves) {
			staveCount++;
			debug(2, "new Stave (numnber " + staveCount + ")");
			
			debug(2, "stave removal.");
			StaveRemovalOld stave_removal = new StaveRemovalOld(score_metrics, stave.get_image()); // remove stave lines
			BufferedImage staveWithoutStaveLines = stave_removal.getStaveWithoutStaveLines();
			stave.setStave_without_stave_lines(staveWithoutStaveLines);

			debug(2, "Stave skeleton calculation.");
			StaveIdentification sK = new StaveIdentification(stave_removal.getSmoothedStaveLines(), score_metrics, 
					StaveIdentification.ALGORITHM_TEMPLATE_MATCHING); // calculate stave line skeleton
			sK.displaySkeleton();
			stave.setStaveSkeleton(sK);
			
			
			ImageProcessing.saveImage(stave.getStave_without_stave_lines(), "/Users/buster/Stuff/Academia/II/DISSERTATION/testOut/Stave" + staveCount + "/", "stave" + staveCount + ".png");
			
			List<L0_Segment> l0_segment_list = l0Segmentation.segment(staveWithoutStaveLines); // l0 segmentation
			stave.set_L0_Segment_list(l0_segment_list);
			
			debug(2, "L0 Segmentation:");
			
			int l0count = 0;
			
			for(L0_Segment l0_segment : l0_segment_list) {
				l0count++;
				debug(3, "new l0_segment (" + l0count + ")");
				
				ImageProcessing.saveImage(l0_segment.get_image(), "/Users/buster/Stuff/Academia/II/DISSERTATION/testOut//Stave" + staveCount + "/l0_Segment" + l0count + "/", "l0" + l0count + ".png");
				
				
				//normalise image of symbol
				BufferedImage l0_image = l0_segment.get_image();
				BufferedImage symbolImage = ImageProcessing.resizeSymbol(l0_image , this.classifierInputImageBoxSize);

				
				//then classify
				SymbolClass symClass = classifier.classify(symbolImage);
				
				Symbol symbol = new Symbol(symClass, l0_segment.get_left_x(), l0_segment.get_right_x(), l0_segment.get_top_y(), l0_segment.get_bottom_y(), score_metrics);
				debug(3, "new symbol: " + symClass.toString());
				debug(4, "bb left x: " + l0_segment.get_left_x());
				debug(4, "bb right x: " + l0_segment.get_right_x());
				debug(4, "bb top y: " + l0_segment.get_top_y());
				debug(4, "bb bottom y: " + l0_segment.get_bottom_y());
				debug(4, "Semantic centre: (" + symbol.getSemantic_centre_x() + ", " + symbol.getSemantic_centre_y() + ")");
				
				l0_segment.setSymbol(symbol);

				ImageProcessing.saveImage(symbolImage, "/Users/buster/Stuff/Academia/II/DISSERTATION/testOut//Stave" + staveCount + "/l0_Segment" + l0count + "/" + symClass.toString() + ".png");


			}
		}
		
		// symbol_recognition done -> have get method to retrieve the internal score representation 'score'
		
	
	}
	
	
	
	// get set /////////
	public Score getScore() {
		return score;
	}
	
	
	public ScoreMetrics getScoreMetrics() {
		return scoreMetrics;
	}
		
	
	private void debug(int debugLevel, String debugMessage) {
		String thisClassName = "SymbolRecogniser_Simple";
		
		if(debugLevel <= this.debugLevel) {
			String indent = "";
			for(int i = debugLevel; i > 1; i--) {
				indent = indent + "\t";
			}
			
			System.out.println(thisClassName + ": DEBUG(" + debugLevel + ")\t" + indent + debugMessage);
		}
	}
}
