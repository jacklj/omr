package omr.symbol_recogntion.segmentation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import omr.symbol_recogntion.score_metrics.ScoreMetricsCalculator;
import omr.util.Projection;
 
// returns a list of the vertical segments to look at?
// or slightly better - do y projection on each of those too to get a proper bounding box...

public class L0_Segmentation_old_input_image_with_staves {

	/**
	 * @param args
	 */
	
	
	
	/// state //////////////////////////////////////////////////////////////////////////////////////////////////////////
	private int height;
	private int width;
	private BufferedImage image;
	
	private List<L0_Segment> l0_Segment_array = new ArrayList<L0_Segment>();
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	

	public static void main(String[] args) {		

		BufferedImage img1 = null;
		try {
			img1 = ImageIO.read(new File("//Users//buster//Stuff//Academia//II//DISSERTATION//test_images//2notes.png")); // binarized image?? - assume done for now - use lilypond scores.
		} catch (FileNotFoundException e) {
			System.out.println("File not found exception");
		} catch (IOException e) {
			System.out.println("IO exception");
		}


		ScoreMetricsCalculator sd = new ScoreMetricsCalculator(img1);
		
		
		int sLH = sd.getStaveLineHeight();
		int sSH = sd.getStaveSpaceHeight();
		
		L0_Segmentation_old_input_image_with_staves l0_s = new L0_Segmentation_old_input_image_with_staves(img1, sLH, sSH);
		
		
	}
	
	/// constructor ////////////////////////////////////////////////////////////////////////////////////////////////////
	public L0_Segmentation_old_input_image_with_staves(BufferedImage img, int staveLineHeight, int StaveSpaceHeight) {
		image = img;
		height = image.getHeight();
		width = image.getWidth();

		Projection project = new Projection(image);
		int xProjection[] = project.xProject(0, width - 1, 0, height - 1); //project whole image
		
		
		
		// pretty print ////////////////////////////
		System.out.print("[");
		for(int x=0; x < width; x++) {
			System.out.print(xProjection[x] + ",");
		}
		System.out.println("]");
		///////////////////////////////
			
		
		//if x projection is more than 5*2 (total of all stave line pixels), there is something else there - symbols to recognise
		
		//graph x projection??
		
		//now make a list of l0_Segments - each one has a start and stop x coordinate
		
		//initialise array of l0_Segment objects
		//List<L0_Segment> l0_Segment_array = new ArrayList<L0_Segment>();
		
		for(int x = 0; x < width; x++) {
			if(xProjection[x] > (staveLineHeight * 5)) {
				// beginning of l0 segment
				int segStart = x;
				
				//need to find end -> keep going until x projection no longer exceeds 10
				
				//first test whether modifying the loop variable inside a for loop affects its use in the loop logic - answer = YES!
				
				while(xProjection[x] > (staveLineHeight*5)) {
					x++;
				}
				
				int segEnd = x-1;
				
				L0_Segment segment = new L0_Segment(segStart, segEnd);
				l0_Segment_array.add(segment);
				
			}
		}
		
		//now should have complete list of l0 Segments: print coords
		int arraySize = l0_Segment_array.size();
		
		//pretty print
		for(int i = 0; i < arraySize; i++) {
			System.out.println("Segment " + i + ": " + ((L0_Segment)l0_Segment_array.get(i)).get_left_x() + " - " + ((L0_Segment)l0_Segment_array.get(i)).get_right_x());
		}
		
		System.out.println();
		
		// iterate through calculated segments, restricting the bounding box along the y axis -> do y projection
		for(L0_Segment s : l0_Segment_array) {
			// use previously calculated x projections, y -> whole image height
			int yProjection[] = project.yProject(s.get_left_x(), s.get_right_x(), 0, height - 1);
			
			// pretty print ////////////////////////////
			System.out.print("[");
			for(int y=0; y < height; y++) {
				System.out.print(yProjection[y] + ",");
			}
			System.out.println("]");
			///////////////////////////////////////////
			
			
			//remove stave lines. when on either side of the stave line there's stuff, fill in the average width
			// of the two.
			
			
			
		}
		
		
	}
	
	
	/// public methods /////////////////////////////////////////////////////////////////////////////////////////////////
	public List<L0_Segment> getL0_Segment_List() {
		return l0_Segment_array;
	}
	

	
	
}
