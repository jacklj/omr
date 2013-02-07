package omr.symbol_recogntion.segmentation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import omr.symbol_recogntion.score_metrics.ScoreMetrics;
import omr.util.ConnectedComponentAnalysis;
import omr.util.ImageProcessing;
import omr.util.ConnectedComponentAnalysis.ConnectedComponent;

// segments a whole page of music into it's constituent staves (aka lines of music)

public class Stave_Segmentation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//text removal and stave segmentation
		
		//do connected components analysis.
		// if component small and squareish, then likely a letter
		//if component massive, then probably a stave
		// if component long and thin, probably a slur / phrase
		
		BufferedImage inputImage = ImageProcessing.loadImage("/Users/buster/libraries/GAMERA/dataSets/typeset/testset/modern/original/wagner.png");
		int imageWidth = inputImage.getWidth();
		ScoreMetrics sm = new ScoreMetrics(inputImage);
		int staveLineHeight = sm.getStaveLine_height();
		int staveSpaceHeight = sm.getStaveSpace_height();
		
		
		
		BufferedImage justMusic = ImageProcessing.copyImage(inputImage);
		ImageProcessing.makeImageWhite(justMusic);
		
		BufferedImage justText = ImageProcessing.copyImage(inputImage);
		ImageProcessing.makeImageWhite(justText);
		
		System.out.println("Connected components analysis");
		ConnectedComponentAnalysis cca = new ConnectedComponentAnalysis(inputImage, ConnectedComponentAnalysis.CONNECTIVITY_8);
		
		for(ConnectedComponent cc : cca.getAllConnectedComponents()) {
			System.out.println("Examine cc " + cc.getLabel());
			float heightWidth_ratio = (float)(cc.getHeight()) / (float)(cc.getWidth());
			
			//text conditions:
			if( (cc.getHeight() > staveSpaceHeight && cc.getWidth() > 0.8*staveSpaceHeight) // above minimum size - ignores sticatto / augmentation dots (but also punctuation)
					&& (heightWidth_ratio > 0.5 && heightWidth_ratio < 2.0) ) {
				cc.paintIntoImage(justText, Color.black);
			} else {
				cc.paintIntoImage(justMusic, Color.black);
			}
			
			
			
			
			
			if(cc.getWidth() > (float)0.6*(float)imageWidth) { // a stave
				
				cc.paintIntoImage(justMusic, Color.red);
			}
			
			
			
			
				//seperate grand staves?
				if(cc.getHeight() > 3*(5*staveLineHeight+4*staveSpaceHeight)) {
//					ImageProcessing.displayImage(cc.getImage(), "Stave3");
					// grand stave with three staves
				} else if(cc.getHeight() > 2*(5*staveLineHeight+4*staveSpaceHeight)) {
//					ImageProcessing.displayImage(cc.getImage(), "Stave2");
					// grand stave with two staves
				} else if(cc.getHeight() > (5*staveLineHeight+4*staveSpaceHeight)) {
//					ImageProcessing.displayImage(cc.getImage(), "Stave1");
					// normal stave
				} else {
					//long, thin component -> could be a first time / second time repeat bracket or a really long phrase mark or something?
					// for now, ignore
				}
			
		}
		ImageProcessing.displayImage(justMusic, "just music, text removed");
		ImageProcessing.displayImage(justText, "just text, music removed");
	}
	
	//further connected component analysis on 


	
	public Stave_Segmentation(ScoreMetrics score_metrics) {
		
	}
	
	public List<Stave> segment(BufferedImage score) {
		
		List<Stave> stave_segment_list = new ArrayList<Stave>();
		
		//segment score -> for now, as input to OMR system is a single stave line, just package image up into Stave_Segment
		
		Stave stave = new Stave(0, score.getWidth(), 0, score.getHeight(), score);

		
		stave_segment_list.add(stave);
		
		return stave_segment_list;
		
	}

}
