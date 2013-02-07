package omr.symbol_recogntion.stave_detection;

//INFO /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//1. have commented out smoothed stave line code to make evaluation quicker
//
//KNOWN BUGS ///////////////////////////////////////////////////////////////////////////////////////////////////////////
//1. 

// --> maybe do different colours or red dotted lines or something to illustrate neighbours
//
//TO DO ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//1. 
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;


import omr.symbol_recogntion.score_metrics.ScoreMetrics;
import omr.util.Colour;
import omr.util.ConnectedComponentAnalysis;
import omr.util.Run_Length_Encoding;
import omr.util.ConnectedComponentAnalysis.ConnectedComponent;
import omr.util.ImageProcessing;

public class StaveRemoval {

	
	public static void main(String[] args) {
//		
		BufferedImage rawImage = ImageProcessing.loadImage("/Users/buster/libraries/GAMERA/dataSets/all_justScoreImagess_inverted/ideal/w-05/image/p012.png"); ///Users/buster/Stuff/Academia/II/DISSERTATION/test_images/dont_stop_me_now_1line_binarised.png"); ///Users/buster/Stuff/Academia/II/DISSERTATION/test_images/2notes.png"); ///Users/buster/Stuff/Academia/II/DISSERTATION/test_images/dont_stop_me_now_1line.png");
		
		BufferedImage whole_stave = ImageProcessing.preprocess(rawImage);
		ScoreMetrics score_metrics = new ScoreMetrics(whole_stave);
		System.out.println("Got score metrics");
		
		StaveRemoval stave_removal = new StaveRemoval(score_metrics, whole_stave, 2);
		System.out.println("done stave removal");
		BufferedImage staveWithoutStaveLines = stave_removal.getStaveWithoutStaveLines();
		BufferedImage justStaveLines = stave_removal.getJustStaveLines();
		BufferedImage smoothedStaveLines = stave_removal.getSmoothedStaveLines(); 
				
		
		ImageProcessing.displayImage(whole_stave, "Original Image");
		ImageProcessing.displayImage(staveWithoutStaveLines, "just symbols");
		ImageProcessing.displayImage(justStaveLines, "just staves");

		String folder = "/Users/buster/Stuff/Academia/II/DISSERTATION/evaluation/stave removal eval/test2/";
		ImageProcessing.saveImage(whole_stave, folder, "originalImage.png");
		ImageProcessing.saveImage(staveWithoutStaveLines, folder, "just_symbols.png");
		ImageProcessing.saveImage(justStaveLines, folder, "just_staves.png");

		// debugging
		ImageProcessing.saveImage(stave_removal.getStep2_justStaves(),  folder, "step2_justStaves.png");
		ImageProcessing.saveImage(stave_removal.getStep3_justStaves(),  folder, "step3_justStaves.png");
		ImageProcessing.saveImage(stave_removal.getStep2_justSymbols(),  folder, "step2_justSymbols.png");
		ImageProcessing.saveImage(stave_removal.getStep3_justSymbols(),  folder, "step3_justSymbols.png");
		ImageProcessing.saveImage(stave_removal.getStep3_changes(), folder,  "step3_changes.png");
		ImageProcessing.saveImage(stave_removal.getStep4_changes(),  folder, "step4_changes.png");
		
		//test
//		StaveRemoval  sR = new StaveRemoval(4);
//		sR.rightNeighbourUnitTest();
	}
	
	
	
	/// state //////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static final int DELTA = 2;
	
	private BufferedImage original_image = null;
	private int height;
	private int width;
	
	private ScoreMetrics scoreMetrics;
	
	private int staveLine_height;
	private int staveSpace_height;
	
	private BufferedImage staveWithoutStaveLines = null;
	private BufferedImage justStaveLines = null;
	private BufferedImage smoothedStaveLines = null;
	
	private int debugLevel;
	
	
	// for saving files for debugging purposes /////////////////////////////////////////////////////////////////////////
	private BufferedImage step2_justStaves;
	private BufferedImage step2_justSymbols;
	
	private BufferedImage step3_justStaves;
	private BufferedImage step3_justSymbols;
	private BufferedImage step3_changes;
	
	private BufferedImage step4_justStaves;
	private BufferedImage step4_justSymbols;
	private BufferedImage step4_changes;
	

	public BufferedImage getStep2_justStaves() {return step2_justStaves;}
	public BufferedImage getStep2_justSymbols() {return step2_justSymbols;}

	public BufferedImage getStep3_justStaves() {return step3_justStaves;}
	public BufferedImage getStep3_justSymbols() {return step3_justSymbols;}
	public BufferedImage getStep3_changes() {return step3_changes;}

	public BufferedImage getStep4_justStaves() {return step4_justStaves;}
	public BufferedImage getStep4_justSymbols() {return step4_justSymbols;}
	public BufferedImage getStep4_changes() {return step4_changes;}
	

	// constructor /////////////////////////////////////////////////////////////////////////////////////////////////////
	public StaveRemoval(int debugLevel) {
		this.debugLevel = debugLevel;
	}
	
	public StaveRemoval(ScoreMetrics score_metrics, BufferedImage img, int debugLevel) {
		original_image = img;
		width = original_image.getWidth();
		height = original_image.getHeight();
		
		this.debugLevel = debugLevel;
		
		staveLine_height = score_metrics.getStaveLine_height();
		staveSpace_height = score_metrics.getStaveSpace_height();
		
		this.scoreMetrics = score_metrics;
		
		locateStaves();
	}

	
	// public methods //////////////////////////////////////////////////////////////////////////////////////////////////	
	public BufferedImage getStaveWithoutStaveLines() {
		return staveWithoutStaveLines;
	}
	
	public BufferedImage getJustStaveLines() {
		return justStaveLines;
	}
	
	public BufferedImage getSmoothedStaveLines() {
		return smoothedStaveLines;
	}
	
	
	
	// private methods /////////////////////////////////////////////////////////////////////////////////////////////////
	private void locateStaves() {
		
		// have a choice of which stave removal algorithm to use?? 
		
		// 1) Y projections based algorithm ////////////////////////////////////////////////////////////////////////////
//		// y projection of image - 5 peaks (each of staveLineHeight width, each staveSpaceHeight apart)
//		
//		System.out.println("Width = " + width);
//		System.out.println("Height = " + height);
//		
//		Projection project = new Projection(image);
//		int yProjection[] = project.yProject(0, width-1, 0, height - 1);
//		
//		// pretty print ////////////////////////////
//		System.out.print("[[");
//		for(int y=0; y < height; y++) {
//			System.out.print(yProjection[y] + ",");
//		}
//		System.out.println("]]");
//		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		
		// 2) USING "AN EFFICIENT STAFF REMOVAL ALGORITHM", DUTTA PAL, FORNES, LLADOS 2010
		
		// 1. horizontal run-length smoothing //////////////////////////////////////////////////////////////////////////
		//		why? to join broken portions of a stave line that often appear due to noise
		//		how?
		//		i) experimentally set the smoothing parameter ('t') as equal to staveline_height
		//		ii) 'add' together any black runs with a white run in between them that's less than t pixels long
		//				->iterate through run length encoding list.
		//					->if a white run is less than t, add its length, the black run behind it and the black run 
		//					  in front of it and combine into one list element.
		//
		
		debug(1, "STEP 1: horizontal run length smoothing");
		
		BufferedImage horizontallySmoothedImage_step1 = ImageProcessing.copyImage(original_image);
		
		int smoothingParameter = scoreMetrics.getStaveLine_height();
		
		
		Run_Length_Encoding rle1 = new Run_Length_Encoding(original_image);
		int[][] rle_x1 = rle1.RLE_2D_along_x_axis();
		
		for(int y = 0; y < rle_x1.length; y++) {
			int x_actual = 0;
			
			for (int j = 0; j < rle_x1[y].length; j++) {
				int run_length = rle_x1[y][j];
				Colour run_colour = Run_Length_Encoding.whatColourIsRun(y,j); // makes code easier to read

				if(run_colour == Colour.WHITE && run_length < smoothingParameter) {
					for (int x = x_actual; x < x_actual + run_length; x++) {
						horizontallySmoothedImage_step1.setRGB(x, y, 0); //black
					}
				}
				x_actual = x_actual + run_length;
			}
		}
					
		
		debug(4, horizontallySmoothedImage_step1, "AFTER STEP 1: Horizontal run length smoothing");

		// 2. core algorithm ///////////////////////////////////////////////////////////////////////////////////////////
		debug(1, "STEP 2: Core Algorithm");
		
		BufferedImage step2_justStaves = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		BufferedImage step2_justSymbols = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);		
		
		// run length encoding along y axis
		Run_Length_Encoding rle = new Run_Length_Encoding(horizontallySmoothedImage_step1);
		int[][] rle_y = rle.RLE_2D_along_y_axis();
		

		for(int x = 0; x < rle_y.length; x++) {
			//System.out.println("x = " + x);
			int y_actual = 0;
			
			for (int i = 0; i < rle_y[x].length; i++) {
				int run_length = rle_y[x][i];
				
				//System.out.println("\t\ti = " + i + "\t\trun_length = "+ run_length + "\t\ty_actual = " + y_actual);
					
				Colour run_colour = Run_Length_Encoding.whatColourIsRun(x,i); // makes code easier to read

				if(run_colour == Colour.BLACK) {

					//check if within parameters
					int lower_bound = Math.abs(staveLine_height - DELTA);
					int upper_bound = staveLine_height + DELTA;

					if((lower_bound <= run_length) && (run_length <= upper_bound))  { // satisfies constraints - 
						// it's a stave line segment
						for (int j = y_actual; j < y_actual + run_length; j++) { // therefore remove black pixels 
							// from 'step2_justSymbols' and add them to 'staves'
							step2_justSymbols.setRGB(x, j, -1); //white
							step2_justStaves.setRGB(x,j,0); //black
						}
					} else { //doesn't satisfy stave line segment constraints
						for (int j = y_actual; j < y_actual + run_length; j++) { // therefore leave black pixels in 
							// from 'step2_justSymbols' and remove them from 'staves'
							step2_justSymbols.setRGB(x, j, 0); //black
							step2_justStaves.setRGB(x,j,-1); //white
						}
					}


				} else { //white -> set pixels to white in 'step2_justSymbols' and 'staves'
					for (int j = y_actual; j < y_actual + run_length; j++) {
						step2_justSymbols.setRGB(x, j, -1); //white
						step2_justStaves.setRGB(x,j,-1); //white
					}
				}

				y_actual = y_actual + run_length;

			}
		}
		
		
		debug(4, step2_justStaves, "AFTER STEP 2: just staves");
		debug(4, step2_justSymbols, "AFTER STEP 2: just symbols");
		

		debug(1, "STEP 2 - done.");
		
		// 3. removal of false segments ////////////////////////////////////////////////////////////////////////////////
		// using 'neighbouring components algorithm'
		debug(1, "STEP 3: removal of false segments using neighbour check");
		
		BufferedImage step3_justStaves = ImageProcessing.copyImage(step2_justStaves);
		BufferedImage step3_justSymbols = ImageProcessing.copyImage(step2_justSymbols);
		BufferedImage components_removed_from_staves_in_step3 = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		ImageProcessing.makeImageWhite(components_removed_from_staves_in_step3);
		
		
		int t2 = 2*scoreMetrics.getStaveLine_height(); // t2: step 3 parameter
		
		Set<ConnectedComponent> componentsNarrowerThanT2 = new HashSet<ConnectedComponent>();
		Set<ConnectedComponent> componentsWiderThanT2 = new HashSet<ConnectedComponent>();
		
		// connected components analysis		
		debug(2, "Connected component analysis of justStaves");
		ConnectedComponentAnalysis ccA = new ConnectedComponentAnalysis(step3_justStaves, ConnectedComponentAnalysis.CONNECTIVITY_8);
		
		debug(2, "If component less than t2 wide, remove.");
		
		for(int i = 1; i <= ccA.getNumberOfComponents(); i++) {
			ConnectedComponent cc = ccA.getConnectedComponent(i);
			int width = cc.getWidth();
			
			if(width < t2) {
				componentsNarrowerThanT2.add(cc);

				ConnectedComponentAnalysis.paintComponentIntoImage(cc, components_removed_from_staves_in_step3, Color.BLACK); // add to this
				ConnectedComponentAnalysis.paintComponentIntoImage(cc, step3_justSymbols, Color.BLACK); // add back to score minus staves
				ConnectedComponentAnalysis.paintComponentIntoImage(cc, step3_justStaves, Color.WHITE); // remove from just_stave image
			} else {
				componentsWiderThanT2.add(cc);
				// all these will remain in step3_justStaves
				
			}
		}
		
		
		debug(4, step3_justStaves, "MID STEP 3: Stave components of width < t2 removed");
		
		debug(2, "Neighbours check the rest");
		// now neighbours check
		ccA = new ConnectedComponentAnalysis(step3_justStaves, ConnectedComponentAnalysis.CONNECTIVITY_8);
		
		for(int i = 1; i <= ccA.getNumberOfComponents(); i++) {
			ConnectedComponent cc = ccA.getConnectedComponent(i);
			
			boolean hasLeftNeighbour = hasLeftNeighbour(cc, ccA, horizontallySmoothedImage_step1);
			boolean hasRightNeighbour = hasRightNeighbour(cc, ccA, horizontallySmoothedImage_step1);
			boolean hasTopNeighbour =  hasTopNeighbour(cc, ccA);
			boolean hasBottomNeighbour = hasBottomNeighbour(cc, ccA);
			
			if(	(hasLeftNeighbour || hasRightNeighbour) && (hasTopNeighbour || hasBottomNeighbour) ) {
				// then it's a valid stave line segment
				
			} else {
				// not a valid stave line segment
					ConnectedComponentAnalysis.paintComponentIntoImage(cc, components_removed_from_staves_in_step3, Color.BLACK); // add to this
					ConnectedComponentAnalysis.paintComponentIntoImage(cc, step3_justSymbols, Color.BLACK); // add back to score minus staves
					ConnectedComponentAnalysis.paintComponentIntoImage(cc, step3_justStaves, Color.WHITE); // remove from just_stave image
			
			}
			
		}
		
		
		debug(4, step3_justStaves, "AFTER STEP 3: just staves");
		debug(4, step3_justSymbols, "AFTER STEP 3: just symbols");
		debug(4, components_removed_from_staves_in_step3, "AFTER STEP 3: components removed from staves in step 3");
		

		debug(2, "STEP 3 - done.");
		// 4. re-addition of wrongly removed staveline segments  ///////////////////////////////////////////////////////
		//	
		
		// "Re-add some wrongly eliminated portions of staffline which will satisfy some criteria to be a part of
		// "valid staffline portion. Let S = {s_1, s_2, s_3, ...} be the set of all valid staff component and 
		// E = {e_1, e_2, e_3, ...} be the set of all components we have eliminated yet. We will add an eliminated 
		// component e_n in E to S if there exists s_k in S such that s_k is the left or right neighbour of e_n and also 
		// there exist s_l in S such that s_l is the top or bottom neighbour of e_n.Thus we have got the new set 
		// S' = (S union {x}),for all x in E that are satisfying the above condition. "
		debug(2, "STEP 4 - readd wrongly removed staveline segments");
		
		BufferedImage step4_justStaves = ImageProcessing.copyImage(step3_justStaves); // = S (set of all valid staffline components)
		// components_removed_from_staves_in_step3 = E (the set of all components we have eliminated yet)
		BufferedImage step4_justSymbols = ImageProcessing.copyImage(step3_justSymbols);
		
		// for debugging
		BufferedImage step_4_wrongly_removed_staveline_segments_ie_components_affected_by_step_4 = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);   
		ImageProcessing.makeImageWhite(step_4_wrongly_removed_staveline_segments_ie_components_affected_by_step_4);
		
		
		ConnectedComponentAnalysis ccA_setS = new ConnectedComponentAnalysis(step3_justStaves, ConnectedComponentAnalysis.CONNECTIVITY_8);
		ConnectedComponentAnalysis ccA_setE = new ConnectedComponentAnalysis(components_removed_from_staves_in_step3, ConnectedComponentAnalysis.CONNECTIVITY_8);
		
		for(int i = 1; i <= ccA_setE.getNumberOfComponents(); i++) {
			ConnectedComponent cc = ccA_setE.getConnectedComponent(i);
			
			boolean hasLeftNeighbour_inS = hasLeftNeighbour(cc, ccA_setS, horizontallySmoothedImage_step1); // correct image to check connectedness?
			boolean hasRightNeighbour_inS = hasRightNeighbour(cc, ccA_setS, horizontallySmoothedImage_step1);// " 		" 		"		"
			boolean hasTopNeighbour_inS =  hasTopNeighbour(cc, ccA_setS);
			boolean hasBottomNeighbour_inS = hasBottomNeighbour(cc, ccA_setS);
			
			if(	(hasLeftNeighbour_inS || hasRightNeighbour_inS) && (hasTopNeighbour_inS || hasBottomNeighbour_inS) ) {
				// then it's a valid stave line segment - add back to S
				ConnectedComponentAnalysis.paintComponentIntoImage(cc, step4_justStaves, Color.BLACK); // add to S
				ConnectedComponentAnalysis.paintComponentIntoImage(cc, step_4_wrongly_removed_staveline_segments_ie_components_affected_by_step_4, Color.BLACK); // for debugging
				
				ConnectedComponentAnalysis.paintComponentIntoImage(cc, step4_justSymbols, Color.WHITE); // remove from symbols image
				
				
			} else {
				// not a valid stave line segment - don't add back to S, leave in symbols image
				
			}
		}

		
		debug(2, "STEP 4 - done.");
		
		debug(4, step4_justStaves, "AFTER STEP 4: just staves");
		debug(4, step4_justSymbols, "AFTER STEP 4: just symbols");
		debug(4, step_4_wrongly_removed_staveline_segments_ie_components_affected_by_step_4,
				"AFTER STEP 4: wrongly removed stave line segments that have now been readded to 'just staves'");
		
		
		/// save state for debugging purposes
		this.step2_justStaves = step2_justStaves;
		this.step2_justSymbols = step2_justSymbols;
		
		this.step3_justStaves = step3_justStaves;
		this.step3_justSymbols = step3_justSymbols;
		this.step3_changes = components_removed_from_staves_in_step3;
				
		this.step4_justStaves = step4_justStaves;
		this.step4_justSymbols = step4_justSymbols;
		this.step4_changes = step_4_wrongly_removed_staveline_segments_ie_components_affected_by_step_4;

		// end of Dutta 2010 alg ///////////////////////////////////////////////////////////////////////////////////////
		
		
		
		
//		/////////////////////////////////////////
//		// my algs... 
//		// want to build complete picture of staves
//		
//		//1) horizontal run length smoothing of stave image with parameter staveSmoothingParameter
//		// ensures staves with lots and lots of notes are dealt with (so lots of white gaps in stave lines)
//		
//		BufferedImage horizontallySmoothedStaves = ImageProcessing.copyImage(step4_justStaves);
//		
//		int staveSmoothingParameter = 3*(scoreMetrics.getNoteHead_width() + scoreMetrics.getNoteStem_width()) ;
//		 // FOR NOW HAVE DOUBLED SMOOTHING PARAMETER to get finished stave lines for position calculations...
//		
//		Run_Length_Encoding rle2 = new Run_Length_Encoding(horizontallySmoothedStaves);
//		int[][] staves_horizontal_rle = rle2.RLE_2D_along_x_axis();
//		
//		for(int y = 0; y < staves_horizontal_rle.length; y++) {
//			int x_actual = 0;
//			
//			for (int j = 0; j < staves_horizontal_rle[y].length; j++) {
//				int run_length = staves_horizontal_rle[y][j];
//				Colour run_colour = Run_Length_Encoding.whatColourIsRun(y,j); // makes code easier to read
//
//				if(run_colour == Colour.WHITE && run_length <= staveSmoothingParameter) {
//					for (int x = x_actual; x < x_actual + run_length; x++) {
//						horizontallySmoothedStaves.setRGB(x, y, 0); //black
//					}
//				}
//				x_actual = x_actual + run_length;
//			}
//		}
				
		//2) remove horizontal runs < 2*noteHead_width
		// this attempts to remove stray bits of symbols etc
		
		//3) another horizontal run length smoothing with big paramater eg 4*noteHeadWidth
		
		//4) anything removed in 2 which was not then added back in 3, add back to symbols image
		// ie iterate through all runs removed in 2
		//	if the equivalently located pixels in the stave image (after step 3) are all white, add back to symbols image
		//  (as probably part of a beam or phrase mark)
		
				
		
		// set state of StaveDetection object
		this.staveWithoutStaveLines = step4_justSymbols;
		this.justStaveLines = step4_justStaves;
		
//		this.smoothedStaveLines = horizontallySmoothedStaves;
		
		
	}
	
	
	
	
	// neighbour algorithms ////////////////////////////////////////////////////////////////////////////////////////////
	private  boolean hasLeftNeighbour(ConnectedComponent cc_This, ConnectedComponentAnalysis cc_Set_to_find_neighbours_from, BufferedImage originalImage) {
		int[][] C_labelledImage = cc_Set_to_find_neighbours_from.getLabelledImage();
		
		//1 get 'this' bounding box (absolute) coordinates
		int this_leftX = cc_This.getStartXcoord();
		int this_rightX = cc_This.getRightXcoord();
		int this_topY = cc_This.getStartYcoord();
		int this_bottomY = cc_This.getBottomYcoord();
		
		debug(3, "LeftNeighbour check:");
		debug(4, cc_This.getIntImage());
		debug(4, "\tThis component's boundingBox: (" + this_leftX + "," + this_topY + ")\t(" + this_rightX + "," + this_bottomY + ")");
		
		
		debug(4, "Look for candidate left neighbour...");
		//2 in labelled C image, look left until another component is found (candidate left neighbour)
		int neighbour_label = 0;
		
		label_outerForLoop: for(int x = this_leftX-1; x >= 0; x--) {
			for(int y = this_topY; y <= this_bottomY; y++) {
				// what if multiple different components feature in this column - will only get the first (highest), may 
				// not be connected to A whereas one of the others might be... -> to remedy, get list of equally nearest
				// connected components and check each of them for being connected to A in the original image.
				
				if(C_labelledImage[y][x] != 0) {//its a component pixel
					neighbour_label = C_labelledImage[y][x];
					debug(4, "Candidate left neighbour found! label: " + neighbour_label);
					break label_outerForLoop;
				}
			}
		}
		
		if(neighbour_label == 0)  { // no candidate left neighbour, therefore definitely no neighbour
			debug(4, "Candidate left neighbour not found! No left neighbour.");

			return false;
		}
		
		
		
		//3 do cc analysis of subsection of original image (subsection defined by bounding box of cc_this && cc_LeftNeighbour
		ConnectedComponent cc_LeftNeighbour = cc_Set_to_find_neighbours_from.getConnectedComponent(neighbour_label);
		int neighbour_leftX = cc_LeftNeighbour.getStartXcoord();
		int neighbour_rightX = cc_LeftNeighbour.getRightXcoord();
		int neighbour_topY = cc_LeftNeighbour.getStartYcoord();
		int neighbour_bottomY = cc_LeftNeighbour.getBottomYcoord();
		
		debug(4, "\tLneighbour boundingBox: (" + neighbour_leftX + "," + neighbour_topY + ")\t(" + neighbour_rightX + "," + neighbour_bottomY + ")");
		
		// get subimage of original image - subimage should include both this component and its potential left neighbour
		debug(4, "CC analysis of subsection of image");
		int subIm_leftx = Math.min(this_leftX, neighbour_leftX);
		int subIm_rightx = Math.max(this_rightX, neighbour_rightX);
		
		int subIm_topy = Math.min(this_topY, neighbour_topY);
		int subIm_bottomy = Math.max(this_bottomY, neighbour_bottomY);
		
		debug(4, "Sub image bounding box: (" + subIm_leftx + "," + subIm_topy + ")\t(" + subIm_rightx + "," + subIm_bottomy + ")");
		
		BufferedImage subImage = originalImage.getSubimage(subIm_leftx, subIm_topy, 
				subIm_rightx - subIm_leftx + 1, subIm_bottomy - subIm_topy + 1);
		
		ConnectedComponentAnalysis cca_subIm = new ConnectedComponentAnalysis(subImage, ConnectedComponentAnalysis.CONNECTIVITY_8);
		int[][] subIm_labelledImage = cca_subIm.getLabelledImage();
		debug(4, "Subimage cc'd:");
		debug(4, subIm_labelledImage);
		
		
		
		//4 see if cc_This and cc_Neighbour are in the same component in original image by checking that an arbitrary 
		//  pixel in each has the same label in subIm_labelledImage
		debug(4, "See if this and neighbour are part of same connected component in subsection of original image:");
		
		int[] arbitraryPixelLocation = getArbitraryPixelFromAConnectedComponent(cc_This);
		int thisX = arbitraryPixelLocation[0];
		int thisY = arbitraryPixelLocation[1];
		debug(4, "Arbitrary pixel in this: (" + thisX + "," + thisY + ")");
		
		arbitraryPixelLocation = getArbitraryPixelFromAConnectedComponent(cc_LeftNeighbour);
		int neighbourX = arbitraryPixelLocation[0];
		int neighbourY = arbitraryPixelLocation[1];
		debug(4, "Arbitrary pixel in neighbour: (" + neighbourX + "," + neighbourY + ")");

		
		if(subIm_labelledImage[thisY - subIm_topy][thisX - subIm_leftx] == subIm_labelledImage[neighbourY - subIm_topy][neighbourX - subIm_leftx]) { // +1 s ?
			//this and neighbour connected component in original image
			debug(4, "->pixels in same connected component!");
			debug(4, "It has a left neighbour - return true");
			return true;
		} else {
			debug(4, "->pixels NOT in same connected component!");
			debug(4, "It doesnt have a left neighbour - return false");
			return false;
		}
		
	}
	
	
	private  boolean hasRightNeighbour(ConnectedComponent cc_This, ConnectedComponentAnalysis cc_Set_to_find_neighbours_from, BufferedImage originalImage) {
		int[][] C_labelledImage = cc_Set_to_find_neighbours_from.getLabelledImage();
		
		int width = originalImage.getWidth();
		
		
		//1 get this bounding box (absolute)
		int this_leftX = cc_This.getStartXcoord();
		int this_rightX = cc_This.getRightXcoord();
		int this_topY = cc_This.getStartYcoord();
		int this_bottomY = cc_This.getBottomYcoord();
		
		debug(3, "RightNeighbour check:");
		debug(4, cc_This.getIntImage());
		debug(4, "\tThis component's boundingBox: (" + this_leftX + "," + this_topY + ")\t(" + this_rightX + "," + this_bottomY + ")");
		
		
		debug(4, "Look for candidate right neighbour...");
		//2 in labelled C image, look left until another component is found (candidate right neighbour)
		int neighbour_label = 0;
		
		label_outerForLoop: for(int x = this_rightX+1; x < width; x++) {
			for(int y = this_topY; y <= this_bottomY; y++) {
				// what if multiple different components feature in this column - will only get the first (highest), may 
				// not be connected to A whereas one of the others might be... -> to remedy, get list of equally nearest
				// connected components and check each of them for being connected to A in the original image.
				
				if(C_labelledImage[y][x] != 0) {//its a component pixel
					neighbour_label = C_labelledImage[y][x];
					debug(4, "Candidate right neighbour found! label: " + neighbour_label);
					break label_outerForLoop; // breaks out of both for loops
				}
			}
		}
		
		if(neighbour_label == 0)  { // no candidate left neighbour, therefore definitely no neighbour
			debug(4, "Candidate right neighbour not found! No right neighbour.");

			return false;
		}
		
		
		
		//3 do cc analysis of subsection of original image (subsection defined by bounding box of cc_this && cc_RightNeighbour
		ConnectedComponent cc_RightNeighbour = cc_Set_to_find_neighbours_from.getConnectedComponent(neighbour_label);
		int neighbour_leftX = cc_RightNeighbour.getStartXcoord();
		int neighbour_rightX = cc_RightNeighbour.getRightXcoord();
		int neighbour_topY = cc_RightNeighbour.getStartYcoord();
		int neighbour_bottomY = cc_RightNeighbour.getBottomYcoord();
		
		debug(4, "\tRneighbour boundingBox: (" + neighbour_leftX + "," + neighbour_topY + ")\t(" + neighbour_rightX + "," + neighbour_bottomY + ")");
		
		// get subimage of original image - subimage should include both this component and its potential left neighbour
		debug(4, "CC analysis of subsection of image");
		int subIm_leftx = Math.min(this_leftX, neighbour_leftX);
		int subIm_rightx = Math.max(this_rightX, neighbour_rightX);
		
		int subIm_topy = Math.min(this_topY, neighbour_topY);
		int subIm_bottomy = Math.max(this_bottomY, neighbour_bottomY);
		
		debug(4, "Sub image bounding box: (" + subIm_leftx + "," + subIm_topy + ")\t(" + subIm_rightx + "," + subIm_bottomy + ")");
		
		BufferedImage subImage = originalImage.getSubimage(subIm_leftx, subIm_topy, 
				subIm_rightx - subIm_leftx + 1, subIm_bottomy - subIm_topy + 1);
//		ImageProcessing.saveImage(subImage, "subImage.png");
		
		ConnectedComponentAnalysis cca_subIm = new ConnectedComponentAnalysis(subImage, ConnectedComponentAnalysis.CONNECTIVITY_8);
		int[][] subIm_labelledImage = cca_subIm.getLabelledImage();
		debug(4, "Subimage cc'd:");
		debug(4, subIm_labelledImage);
		
		
		
		//4 see if cc_This and cc_Neighbour are in the same component in original image by checking that an arbitrary 
		//  pixel in each has the same label in subIm_labelledImage
		debug(4, "See if this and neighbour are part of same connected component in subsection of original image:");

		int[] arbitraryPixelLocation = getArbitraryPixelFromAConnectedComponent(cc_This);
		int thisX = arbitraryPixelLocation[0];
		int thisY = arbitraryPixelLocation[1];
		debug(4, "Arbitrary pixel in this: (" + thisX + "," + thisY + ")");
		
		//get a pixel in neighbour (arbitrary - first foreground pixel in top row will do)
		
		arbitraryPixelLocation = getArbitraryPixelFromAConnectedComponent(cc_RightNeighbour);
		int neighbourX = arbitraryPixelLocation[0];
		int neighbourY = arbitraryPixelLocation[1];
		debug(4, "Arbitrary pixel in R neighbour: (" + neighbourX + "," + neighbourY + ")");

		
		if(subIm_labelledImage[thisY - subIm_topy][thisX - subIm_leftx] == subIm_labelledImage[neighbourY - subIm_topy][neighbourX - subIm_leftx]) { // +1 s ?
			//this and neighbour connected component in original image
			debug(4, "->pixels in same connected component!");
			debug(4, "It has a right neighbour - return true");
			return true;
		} else {
			debug(4, "->pixels NOT in same connected component!");
			debug(4, "It doesnt have a right neighbour - return false");
			return false;
		}
		
	}
	
	
	private boolean hasTopNeighbour(ConnectedComponent cc_This, 
			ConnectedComponentAnalysis cc_Set_to_find_neighbours_from)	{
		
		int[][] C_labelledImage = cc_Set_to_find_neighbours_from.getLabelledImage();
		debug(3, "topNeighbour check:");
		
		//1 get this bounding box (absolute)
		int this_leftX = cc_This.getStartXcoord();
		int this_rightX = cc_This.getRightXcoord();
		int this_topY = cc_This.getStartYcoord();
		
		
		debug(4, cc_This.getIntImage());
		debug(4, "\tThis component's boundingBox: (" + this_leftX + "," + this_topY + ")\t(" + this_rightX + ")");
		
		// see if any components above this, less than staveSpave.height+staveLine.height away.
		debug(4, "see if any component above this closer than staveLine_height (" + staveLine_height 
				+ ") + staveSpace_height (" + staveSpace_height + ") (= " + (staveLine_height+staveSpace_height) + ")");
		
		int minYcandidate = this_topY - 1 - (staveSpace_height + staveLine_height);
		int minY = minYcandidate > 0 ? minYcandidate : 0;
		
		for(int x = this_leftX; x <= this_rightX; x++) {
			for(int y = this_topY-1; y > minY; y-- ) {
				if(C_labelledImage[y][x] != 0) {
					debug(4, "Top neighbour found! component with label: " + C_labelledImage[y][x]);
					debug(4, "distance of cc " + C_labelledImage[y][x] + " from this = " + (y - this_topY) + "(inclusive)");
					
					return true;
				}
			}
		}
		
		debug(4, "No top neighbout found - return false");
		return false;
	}
	
	
	private boolean hasBottomNeighbour(ConnectedComponent cc_This, 
			ConnectedComponentAnalysis cc_Set_to_find_neighbours_from)	{
		int[][] C_labelledImage = cc_Set_to_find_neighbours_from.getLabelledImage();
		int imageHeight = C_labelledImage.length;
		
		//1 get this bounding box (absolute)
		int this_leftX = cc_This.getStartXcoord();
		int this_rightX = cc_This.getRightXcoord();
		int this_bottomY = cc_This.getBottomYcoord();
		
		
		debug(4, cc_This.getIntImage());
		debug(4, "\tThis component's boundingBox: (" + this_leftX + "," + ")\t(" + this_rightX + "," + this_bottomY + ")");
		
		
		// see if any components below this, less than staveSpave.height+staveLine.height away.
		debug(4, "see if any component below THIS closer than staveLine_height (" + staveLine_height 
				+ ") + staveSpace_height (" + staveSpace_height + ") (= " + (staveLine_height+staveSpace_height) + ")");
		
		int maxYcandidate = this_bottomY + (staveSpace_height + staveLine_height);
		int maxY = maxYcandidate < imageHeight ? maxYcandidate : imageHeight - 1;
		
		
		for(int x = this_leftX; x <= this_rightX; x++) {
			for(int y = this_bottomY+1; y <= maxY; y++ ) {
				if(C_labelledImage[y][x] != 0) {
					debug(4, "Bottom neighbour found! component with label: " + C_labelledImage[y][x]);
					debug(4, "distance of cc " + C_labelledImage[y][x] + " from this = " + (y -this_bottomY) + "(inclusive)");
					return true;
				}
			}
		}
		
		debug(4, "No bottom neighbout found - return false");

		return false;
	}
	

	
	
	
	private static int[] getArbitraryPixelFromAConnectedComponent(ConnectedComponent cc) {
		int cc_leftX = cc.getStartXcoord();
		int cc_rightX = cc.getRightXcoord();
		int cc_topY = cc.getStartYcoord();
		int cc_bottomY = cc.getBottomYcoord();		
		
		int neighbourX = 0;
		int neighbourY = 0;
//		System.out.println("get arb pixel from " + cc.getLabel() + ":");
		outerFor: for(int y = cc_topY; y <= cc_bottomY; y++) {
//			System.out.println("Checking row " + y);
			for(int x = cc_leftX; x <= cc_rightX; x++) {
//				System.out.println("\tChecking column " + x);
				if(cc.getAbsPixel(x, y) == true) {
					neighbourX = x;
					neighbourY = y;
					break outerFor;
				}
			}
		}
		
		int[] pixelLocationPair = {neighbourX, neighbourY};
		return pixelLocationPair;
	}
	
	
	
	// debugging ///////////////////////////////////////////////////////////////////////////////////////////////////////
	private void debug(int debugLevel, String debugMessage) {
		String thisClassName = "StaveRemoval";
		
		if(debugLevel <= this.debugLevel) {
			String indent = "";
			for(int i = debugLevel; i > 1; i--) {
				indent = indent + "\t";
			}
			
			System.out.println(thisClassName + ": DEBUG(" + debugLevel + ")\t" + indent + debugMessage);
		}
	}
	
	private void debug(int debugLevel, int[][] imageInt) {
		if(debugLevel <= this.debugLevel) {
			ConnectedComponentAnalysis.printIm(imageInt);
		}

	}
	
	private void debug(int debugLevel, BufferedImage image, String imageDisplayTitle) {
		if(debugLevel <= this.debugLevel) {
			ImageProcessing.displayImage(image, imageDisplayTitle);
		}
	}
	
	
	
	// unit tests //////////////////////////////////////////////////////////////////////////////////////////////////////
	private void rightNeighbourUnitTest() {
		
//		int[][] originalIntImage = {
//				{ 0, 0, 1, 0, 0 },
//				{ 0, 1, 1, 1, 0 },
//				{ 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0 },
//				{ 0, 1, 0, 1, 0 } 
//		};
//		
//		int[][] afterStep2IntImage = {
//				{ 0, 0, 0, 0, 0 },
//				{ 0, 1, 0, 1, 0 },
//				{ 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0 },
//				{ 0, 1, 0, 1, 0 } 
//		};
//		
		// bounding boxes encompassing and object's top row is cut off (left component bigger)
//		int[][] originalIntImage = {
//				{ 0, 0, 0, 0, 1 },
//				{ 0, 0, 1, 1, 1 },
//				{ 1, 1, 1, 0, 0 },
//				{ 1, 0, 0, 0, 0 },
//				{ 1, 0, 0, 1, 0 },
//				{ 1, 0, 1, 1, 0 }
//				};
		
		// bounding boxes encompassing and object's top row is cut off (right component bigger)
//		int[][] originalIntImage = {
//				{ 1, 0, 0, 0, 0 },
//				{ 1, 1, 0, 0, 0 },
//				{ 0, 1, 1, 0, 0 },
//				{ 0, 0, 0, 1, 0 },
//				{ 0, 0, 0, 0, 1 },
//				{ 0, 0, 1, 0, 1 }
//				};
		
		int[][] originalIntImage = {
				{ 1, 0, 0, 0, 0 },
				{ 0, 1, 0, 0, 0 },
				{ 0, 0, 1, 0, 0 },
				{ 0, 0, 0, 1, 0 },
				{ 0, 1, 0, 1, 0 }
				};
		
		int[][] afterStep2IntImage = originalIntImage;
		
		BufferedImage originalImage = ImageProcessing.convert_2dIntArray_toBinaryImage(originalIntImage);
		BufferedImage just_staves_after_step2 = ImageProcessing.convert_2dIntArray_toBinaryImage(afterStep2IntImage);
		
		
		// now neighbours check
		ConnectedComponentAnalysis ccA = new ConnectedComponentAnalysis(just_staves_after_step2, ConnectedComponentAnalysis.CONNECTIVITY_8);
		int[][] labelledImage = ccA.getLabelledImage();
		
		for(int i = 1; i <= ccA.getNumberOfComponents(); i++) {
			ConnectedComponent cc = ccA.getConnectedComponent(i);
			int width2 = cc.getWidth();
			int height2 = cc.getHeight();
			
		//	boolean hasLeftNeighbour = hasLeftNeighbour(cc, ccA, horizontallySmoothedImage_step1);
			boolean hasRightNeighbour = hasRightNeighbour(cc, ccA, originalImage);
		}
		
		
		
		
	}
	
	
	private void leftNeighbourUnitTest() {

		// normal functionality test
//		int[][] originalIntImage = {
//				{ 0, 0, 1, 0, 0 },
//				{ 0, 1, 1, 1, 0 },
//				{ 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0 },
//				{ 0, 1, 0, 1, 0 } 
//		};
//		
//		int[][] afterStep2IntImage = {
//				{ 0, 0, 0, 0, 0 },
//				{ 0, 1, 0, 1, 0 },
//				{ 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0 },
//				{ 0, 1, 0, 1, 0 } 
//		};
		
		// image boundaries test
//		int[][] originalIntImage = {
//				{ 0, 1, 0 },
//				{ 1, 1, 1 },
//				{ 0, 0, 0 },
//				{ 0, 0, 0 },
//				{ 1, 0, 1,} 
//		};
//		
//		int[][] afterStep2IntImage = {
//				{ 0, 0, 0 },
//				{ 1, 0, 1 },
//				{ 0, 0, 0 },
//				{ 0, 0, 0 },
//				{ 1, 0, 1 } 
//		};
		
		//all
//		int[][] originalIntImage = {
//				{ 1, 1, 1 },
//				{ 1, 1, 1 },
//				{ 1, 1, 1 }
//		};
//		
//		int[][] afterStep2IntImage = {
//				{ 1, 1, 1 },
//				{ 1, 1, 1 },
//				{ 1, 1, 1 }
//		};
		
		//none
//		int[][] originalIntImage = {
//				{ 0, 0, 0 },
//				{ 0, 0, 0 },
//				{ 0, 0, 0 }
//		};
//		
//		int[][] afterStep2IntImage = {
//				{ 0, 0, 0 },
//				{ 0, 0, 0 },
//				{ 0, 0, 0 }
//		};
		
		//overlapping connected components
//		int[][] originalIntImage = {
//			{ 1, 0, 0, 0 },
//			{ 0, 1, 0, 0 },
//			{ 0, 1, 1, 0 },
//			{ 1, 0, 1, 0 }
//		};
//
//		int[][] afterStep2IntImage = {
//			{ 1, 0, 0, 0 },
//			{ 0, 0, 0, 0 },
//			{ 0, 0, 1, 0 },
//			{ 1, 0, 1, 0 }
//		};
		
		
		// overlapping bboxes no left neighbour
//		int[][] originalIntImage = {
//				{ 1, 1, 0, 1 },
//				{ 0, 0, 0, 1 },
//				{ 0, 1, 1, 1 }
//			};
//
//			int[][] afterStep2IntImage = {
//				{ 1, 1, 0, 1 },
//				{ 0, 0, 0, 1 },
//				{ 0, 1, 1, 1 }
//			};
//			
			// overlapping bboxes yes left neighbour
//			int[][] originalIntImage = {
//				{ 1, 1, 1, 1 },
//				{ 0, 0, 0, 1 },
//				{ 0, 1, 1, 1 }
//			};
//
//			int[][] afterStep2IntImage = {
//				{ 1, 1, 0, 1 },
//				{ 0, 0, 0, 1 },
//				{ 0, 1, 1, 1 }
//			};
				
		int[][] originalIntImage = 
		{
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
				};
			
		// one bounding box totally encompassing another
//		int[][] originalIntImage = {
//				{ 0, 0, 1, 1 },
//				{ 1, 1, 1, 0 },
//				{ 1, 0, 0, 0 },
//				{ 1, 0, 1, 0 },
//				{ 1, 0, 0, 0 }
//				};
		
		// bounding boxes encompassing and object's top row is cut off (left component bigger)
//		int[][] originalIntImage = {
//				{ 0, 0, 0, 0, 1 },
//				{ 0, 0, 1, 1, 1 },
//				{ 1, 1, 1, 0, 0 },
//				{ 1, 0, 0, 0, 0 },
//				{ 1, 0, 0, 1, 0 },
//				{ 1, 0, 1, 1, 0 }
//				};
		
		// bounding boxes encompassing and object's top row is cut off (right component bigger)
//		int[][] originalIntImage = {
//				{ 1, 0, 0, 0, 0 },
//				{ 1, 1, 1, 0, 0 },
//				{ 0, 1, 1, 0, 0 },
//				{ 0, 0, 0, 1, 0 },
//				{ 0, 0, 0, 1, 1 },
//				{ 0, 1, 0, 0, 1 }
//				};
		
		int[][] afterStep2IntImage = originalIntImage;
		
		BufferedImage originalImage = ImageProcessing.convert_2dIntArray_toBinaryImage(originalIntImage);
		BufferedImage just_staves_after_step2 = ImageProcessing.convert_2dIntArray_toBinaryImage(afterStep2IntImage);
		ImageProcessing.saveImage(originalImage, "unit_test_image_thats_breaking_leftNeighbour.png");
		
		
		// now neighbours check
		ConnectedComponentAnalysis ccA = new ConnectedComponentAnalysis(just_staves_after_step2, ConnectedComponentAnalysis.CONNECTIVITY_8);
		int[][] labelledImage = ccA.getLabelledImage();
		
		for(int i = 1; i <= ccA.getNumberOfComponents(); i++) {
			ConnectedComponent cc = ccA.getConnectedComponent(i);
			int width2 = cc.getWidth();
			int height2 = cc.getHeight();
			
		//	boolean hasLeftNeighbour = hasLeftNeighbour(cc, ccA, horizontallySmoothedImage_step1);
			boolean hasLeftNeighbour = hasLeftNeighbour(cc, ccA, originalImage);
		}
		
		debug(2, "Done.");
	}
	
	
	private void topNeighbourUnitTest() {
		
		this.staveLine_height = 1;
		this.staveSpace_height = 1;
		
//		// test none
//		int[][] justStavesImage = {
//				{ 0, 0, 0 },
//				{ 0, 0, 0 },
//				{ 0, 0, 0 }
//		};
//		
		//test all
//		int[][] justStavesImage = {
//				{ 1, 1, 1 },
//				{ 1, 1, 1 },
//				{ 1, 1, 1 }
//		};
//		
		// test yes
//		int[][] justStavesImage = {
//				{ 0, 0, 0 },
//				{ 1, 0, 0 },
//				{ 0, 0, 0 },
//				{ 1, 1, 0 }
//		};
//		
		//test no
		int[][] justStavesImage = {
				{ 1, 0, 0 },
				{ 0, 0, 1 },
				{ 0, 0, 0 },
				{ 1, 1, 0 }
		};
		
		
		
		BufferedImage just_staves_after_step2 = ImageProcessing.convert_2dIntArray_toBinaryImage(justStavesImage);
		
		
		// now neighbours check
		System.out.println("CC analysis");
		ConnectedComponentAnalysis ccA = new ConnectedComponentAnalysis(just_staves_after_step2, ConnectedComponentAnalysis.CONNECTIVITY_8);
		
		for(int i = 1; i <= ccA.getNumberOfComponents(); i++) {
			System.out.println("\tneighbour check on component " + i);

			ConnectedComponent cc = ccA.getConnectedComponent(i);
			
			boolean hasTopNeighbour = hasTopNeighbour(cc, ccA);
		}
		
		System.out.println("Done");
		
		
	}
	
	
	private void bottomNeighbourUnitTest() {
		
		this.staveLine_height = 1;
		this.staveSpace_height = 1;
		
		// test none
//		int[][] justStavesImage = {
//				{ 0, 0, 0 },
//				{ 0, 0, 0 },
//				{ 0, 0, 0 }
//		};
		
		//test all
//		int[][] justStavesImage = {
//				{ 1, 1, 1 },
//				{ 1, 1, 1 },
//				{ 1, 1, 1 }
//		};
		
		// test yes
//		int[][] justStavesImage = {
//				{ 0, 0, 0 },
//				{ 1, 0, 0 },
//				{ 0, 0, 0 },
//				{ 1, 1, 0 }
//		};
		
		//test no
		int[][] justStavesImage = {
				{ 1, 0, 0 },
				{ 0, 0, 1 },
				{ 0, 0, 0 },
				{ 1, 1, 0 }
		};
		
		
		
		BufferedImage just_staves_after_step2 = ImageProcessing.convert_2dIntArray_toBinaryImage(justStavesImage);
		
		
		// now neighbours check
		System.out.println("CC analysis");
		ConnectedComponentAnalysis ccA = new ConnectedComponentAnalysis(just_staves_after_step2, ConnectedComponentAnalysis.CONNECTIVITY_8);
		
		for(int i = 1; i <= ccA.getNumberOfComponents(); i++) {
			System.out.println("\tneighbour check on component " + i);

			ConnectedComponent cc = ccA.getConnectedComponent(i);
			
			boolean hasBottomNeighbour = hasBottomNeighbour(cc, ccA);
		}
		
		System.out.println("Done");
			
	}
	
	

	
	private void getArbitraryPixelFromAConnectedComponent_unit_test() {
		int[][] originalIntImage = {
				{ 1, 0, 0, 0, 0 },
				{ 0, 1, 0, 0, 0 },
				{ 0, 0, 1, 0, 0 },
				{ 0, 0, 0, 1, 0 },
				{ 0, 1, 0, 1, 0 }
				};
		
		
		BufferedImage testImage = ImageProcessing.convert_2dIntArray_toBinaryImage(originalIntImage);
		
		
		// now neighbours check
		ConnectedComponentAnalysis ccA = new ConnectedComponentAnalysis(testImage, ConnectedComponentAnalysis.CONNECTIVITY_8);
		ConnectedComponentAnalysis.printIm(ccA.getLabelledImage());
		for(ConnectedComponent cc : ccA.getAllConnectedComponents()) {

			int[] arbs = getArbitraryPixelFromAConnectedComponent(cc);
			int arbX = arbs[0];
			int arbY = arbs[1];
			
			System.out.println("Component " + cc.getLabel() + "'s  arbitrary pixel : (" + arbX + "," + arbY + ")" );
		}
			
	}
}
