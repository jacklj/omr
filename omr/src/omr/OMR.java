package omr;

import java.awt.image.BufferedImage;


import omr.final_representation_construction.FinalRepresentationConstructor;
import omr.notation_reconstruction.NotationReconstructor_Simple;
import omr.symbol_recogntion.SymbolRecogniser_Simple;
import omr.symbol_recogntion.score_metrics.ScoreMetrics;
import omr.symbol_recogntion.segmentation.Score;
import omr.util.ImageProcessing;



public class OMR {
		
	public static final int OUTPUT_LILYPOND_PDF = 1;
	public static final int OUTPUT_LILYPOND_MIDI = 2;
	public static final int OUTPUT_LILYPOND_CODE = 3;
	
	public static void main(String[] args) {

////////// run from command line ///////////////////////////////////////////////////////////////////////////////////////
//		String path_to_image_file;
//		if (args.length > 0) {
//		    	path_to_image_file = args[0];
//		 }
//		else {
//			System.err.println("[ERROR] Usage: OMR input_image_file_path output_file_path -c classifier_type -t " + 
//					"classifier_trained_file");
//			System.exit(1);
//		}
// 		if (String ouputTypeArg = "lilypond_pdf") {outputType = OMR.OUTPUT_LILYPOND_PDF;}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// run manually here ///////////////////////////////////////////////////////////////////////////////////////////
		String scorePath = 
			"/Users/buster/Stuff/Academia/CompSci/II/DISSERTATION/test_images/sibelius_opus_simple_stave.png";
		String modelFilePath = 
			"/Users/buster/Stuff/Academia/CompSci/II/DISSERTATION/SVM/basicSymbolSet_training/train.model";
		int boxSize = 20;
		int outputType = OMR.OUTPUT_LILYPOND_MIDI;
		String outputDirPath = "/Users/buster/Stuff/Lilypondoutput/";
		String outputFileName = "omr_pdf_out";
		String lilypondExecutableFilePath = "/Applications/LilyPond2.app/Contents/Resources/bin/lilypond";
		int debugLevel = 0;
		
		OMR.process(
				scorePath, 
				modelFilePath, 
				boxSize, 
				outputType, 
				outputDirPath, 
				outputFileName, 
				lilypondExecutableFilePath, 
				debugLevel
				); // at the moment works on one stave (not a whole score page)		
	}
	
	

	// private methods /////////////////////////////////////////////////////////////////////////////////////////////////
	private static void process(String scorePath, String modelFilePath, int boxSize, int outputType, 
			String outputDirPath, String outputFileName, String lilypondExecutableFilePath, int debugLevel) {
		
		// 1.load image
		BufferedImage img = ImageProcessing.loadImage(scorePath);
		ImageProcessing.displayImage(img, "Input image");
		
		// 2.preprocess
		img = ImageProcessing.preprocess(img);
		ImageProcessing.displayImage(img, "Binarised");
		
		// 3.symbol recogniser
		SymbolRecogniser_Simple sr = new SymbolRecogniser_Simple(img, scorePath, modelFilePath, boxSize, 0);
		Score analysedScore = sr.getScore();
		ScoreMetrics scoreMetrics = sr.getScoreMetrics();
		
		//3) notation reconstruction
		NotationReconstructor_Simple nr = new NotationReconstructor_Simple(analysedScore, scoreMetrics, 4); // debug l3
		
		String lilyPondCode = nr.getLilyPondCode();
		String lilyPondMIDIcode = nr.getLilyPondMIDIcode();
		
		System.out.println(lilyPondCode);
		
		//4) final notation reconstruction
		FinalRepresentationConstructor fNr = new FinalRepresentationConstructor(lilyPondCode, lilyPondMIDIcode, 
				outputType, outputDirPath, outputFileName, lilypondExecutableFilePath, 4);

		
		//File pdf_created_by_lilypond?
		//	MIDI file?
		
		
	}
	
	
}
