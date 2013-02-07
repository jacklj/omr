package omr.symbol_recogntion.score_metrics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;


// this class could replace StaveDetectionConstants

// in it we (calculate then) store not only stave_line_height and stave_space_height but all the other metrics we will use throughout 
// the system

// e.g. note_head.height = stave_space_height (or have range/interval to account for errors 
//					eg [stave_spave_height - stave_line_height, stave_spave_height + stave_line_height])


//UNITS: all measurements in pixels


// i want an object of this class to be declared at the beginning of the symbol_recognition, then all the metrics can be 
// accessed, preferably by object.var not get/set methods (for neatness), but doesnt matter too much


public class ScoreMetrics {
	
	/// state //////////////////////////////////////////////////////////////////////////////////////////////////////////
	//image
	private BufferedImage image = null;

	
	//ideal metrics/////////////////////////
	private int staveLine_height;
	private int staveSpace_height;
	
	private int wholeStave_height;
	
	private int noteHead_height;
	private int noteHead_width;
	
	private int noteStem_height;
	private int noteStem_width;
	
	private int noteBeam_height;
	
	//error margins/////////////////////////
	private int staveLine_height_DELTA;
	private int staveSpace_height_DELTA;
	
	private int noteHead_height_DELTA;
	private int noteHead_width_DELTA;
	
	private int noteStem_height_DELTA;
	private int noteStem_width_DELTA;
	
	private int noteBeam_height_DELTA;
	
	//have general error margin?
	private static int DELTA = 2;
	
	
	//error ranges///////////////////////////
	private int staveLine_height_min;
	private int staveLine_height_max;
	
	private int staveSpace_height_min;
	private int staveSpace_height_max;

	
	private int noteHead_height_min;
	private int noteHead_height_max;
	
	private int noteHead_width_min;
	private int noteHead_width_max;
	
	
	private int noteStem_height_min;
	private int noteStem_height_max;
	
	private int noteStem_width_min;
	private int noteStem_width_max;
	
	
	private int noteBeam_height_min;
	private int noteBeam_height_max;

	
// constructor /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public ScoreMetrics(BufferedImage img) { // image of whole page of music
		image = img;
		
		ScoreMetricsCalculator sMc = new ScoreMetricsCalculator(image);
		staveLine_height = sMc.getStaveLineHeight();
		staveSpace_height = sMc.getStaveSpaceHeight();
		
		
		// set ideal metrics //////////////////////////
		
		wholeStave_height = 5*staveLine_height + 4* staveSpace_height;
			
		noteHead_height = staveSpace_height;
		noteHead_width = staveSpace_height*2;
		
		//noteStem_height = 
		noteStem_width = staveLine_height;
		noteStem_height = (int) (3.5*staveSpace_height);
		
		noteBeam_height = staveLine_height*2;
		
		
		// set error margins //////////////////////////
		
//		staveLine_height_DELTA;
//		staveSpace_height_DELTA;
//		
//		noteHead_height_DELTA;
//		noteHead_width_DELTA;
//		
//		noteStem_height_DELTA;
//		noteStem_width_DELTA;
//		
//		noteBeam_height_DELTA;
		
		
		
		// now calculate and set error ranges ///////////////////////////////
		
		staveLine_height_min = staveLine_height - DELTA;
		staveLine_height_max = staveLine_height + DELTA;
//		
		staveSpace_height_min = staveSpace_height - DELTA;
		staveSpace_height_max = staveSpace_height + DELTA;
//
		
		noteHead_height_min = noteHead_height - staveLine_height;
		noteHead_height_max = noteHead_height + 2 * staveLine_height;
		
		noteHead_width_min = (int) (noteHead_width *0.6);
		noteHead_width_max = (int) (noteHead_width * 1.3);
		
		
		noteStem_height_min = (int)(noteStem_height * 0.6);
		noteStem_height_max = 2*noteStem_height;
		
		noteStem_width_min = noteStem_width - DELTA;
		noteStem_width_max = noteStem_width + DELTA;
//		
//		
		noteBeam_height_min = (int)(noteBeam_height*0.5);
		noteBeam_height_max = (int)(noteBeam_height*1.5);

		
	}
	

// get methods /////////////////////////////////////////////////////////////////////////////////////////////////////
	public int getStaveLine_height() {
		return staveLine_height;
	}

	public int getStaveSpace_height() {
		return staveSpace_height;
	}
	
	public int getWholeStave_height() {
		return wholeStave_height;
	}
	

	public int getNoteHead_height() {
		return noteHead_height;
	}

	public int getNoteHead_width() {
		return noteHead_width;
	}

	public int getNoteStem_height() {
		return noteStem_height;
	}

	public int getNoteStem_width() {
		return noteStem_width;
	}

	public int getNoteBeam_height() {
		return noteBeam_height;
	}

	
	// error margins /////////////////////////////////
	public static double getDelta() {
		return DELTA;
	}

	
	
	// error ranges ////////////////////////////
	public int getStaveLine_height_min() {
		return staveLine_height_min;
	}

	public int getStaveLine_height_max() {
		return staveLine_height_max;
	}

	public int getStaveSpace_height_min() {
		return staveSpace_height_min;
	}

	public int getStaveSpace_height_max() {
		return staveSpace_height_max;
	}

	public int getNoteHead_height_min() {
		return noteHead_height_min;
	}

	public int getNoteHead_height_max() {
		return noteHead_height_max;
	}

	public int getNoteHead_width_min() {
		return noteHead_width_min;
	}

	public int getNoteHead_width_max() {
		return noteHead_width_max;
	}

	public int getNoteStem_height_min() {
		return noteStem_height_min;
	}

	public int getNoteStem_height_max() {
		return noteStem_height_max;
	}

	public int getNoteStem_width_min() {
		return noteStem_width_min;
	}

	public int getNoteStem_width_max() {
		return noteStem_width_max;
	}

	public int getNoteBeam_height_min() {
		return noteBeam_height_min;
	}

	public int getNoteBeam_height_max() {
		return noteBeam_height_max;
	}
}

