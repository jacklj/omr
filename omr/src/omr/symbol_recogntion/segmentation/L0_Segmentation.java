package omr.symbol_recogntion.segmentation;

import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import omr.symbol_recogntion.score_metrics.ScoreMetrics;
import omr.symbol_recogntion.score_metrics.ScoreMetricsCalculator;
import omr.util.DisplayImage;
import omr.util.Projection;
 

//INFO /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//1. L0 bounding boxes don't overlap with symbols -> their coordinates are:
// 			x_left = 'symbols furthest left pixel's x coord' - 1 (unless = 0, in which case x_left = 0 and does overlap 
//					 with the symbol)
//			x_right = 'symbol's furthest right pixel's x coord' + 1 (unless = width-1, in which case x_right = width - 1
//					  and does overlap with the symbol)
//
// alternatives: have bounding box exactly overlap symbols, but what do you do for eg single pixel width symbols
// 				 (especially when its on the right edge of the image) -> no useful single pixel images...
//
//2. input is an image without staves. change x_min_threshold and y_min_threshold to experiment
//
//3. initial x axis segmentation represents individual moments in time (in Western Standard Notation, x axis (l->r) =  
//	 time. This assumes components representing actions (play a note, rest etc) at the same time are horizontally
// 	 connected. Except sharps, flats etc
//
//4. Then 'low resolution' y axis segmentation -> just restricts bounding box by examining top of top symbol and bottom 
//	 of bottom symbol - may be multiple vertically disconnected symbols between these two points.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//KNOWN BUGS ///////////////////////////////////////////////////////////////////////////////////////////////////////////
//1. issues when segmenting 1 pixel wide (in either dimension) symbols on image edges, due to bounding box definitions
//	 above. to fix, check if on image edge and adjust what you project accordingly....
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//TO DO ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//1.save_all_segments_as_images()
//2. new method -> save picture of whole original image + overlaid bounding boxes (i.e. what's displayed on the gui)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


public class L0_Segmentation {

	
	/// state //////////////////////////////////////////////////////////////////////////////////////////////////////////	
	private int height;
	private int width;
	private BufferedImage image;
	
	private final int X_MIN_THRESHOLD;
	private final int Y_MIN_THRESHOLD;
	
	private List<L0_Segment> l0_Segment_list = new ArrayList<L0_Segment>();
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	

	public static void main(String[] args) {		

		String file_where_to_save_symbol_images = "//Users//buster//testImages//";
		String filePath = "/Users/buster/Stuff/Academia/II/DISSERTATION/evaluation/stave removal eval/test1Line/just_symbols.png";

		BufferedImage img1 = null;
		try {img1 = ImageIO.read(new File(filePath));} 
		catch (FileNotFoundException e) {System.out.println("File not found exception");} 
		catch (IOException e) {System.out.println("IO exception");}


		
		ScoreMetrics score_metrics = new ScoreMetrics(img1);
		
		
		L0_Segmentation l0_s = new L0_Segmentation(score_metrics);
		l0_s.segment(img1);
		
		l0_s.display_segmentation_gui(filePath);
		
//		l0_s.save_all_segments_as_images(file_where_to_save_symbol_images);
		
	}
	
	/// constructor ////////////////////////////////////////////////////////////////////////////////////////////////////
	public L0_Segmentation(ScoreMetrics score_metrics) {

		// these thresholds should ensure that any foreground objects are detected, but noise ignored.
		// sensible values would therefore be:
		//		X_MIN_THRESHOLD = whats the height of the smallest (as in least high projection) symbol?
		//						  then perhaps multiply by eg 2/3 to account for a distorted symbol (better false positive than missed symbol).
		//						  so e.g.... 	= (int)(0.66*slur_tie_phrase_line_height)
		//		Y_MIN_THRSHOLD = e.g. (int)(0.66*note_stem_width)
		X_MIN_THRESHOLD = 1; // will depend on score_metrics
		Y_MIN_THRESHOLD = 1; // 	""			""
		
		// e.g. x_min_threshold = (int)(0.5*score_metrics.getStaveLine_height());
		
		
	}
		
	
	
	/// public methods /////////////////////////////////////////////////////////////////////////////////////////////////
	public List<L0_Segment> getL0_Segment_List() {
		return l0_Segment_list;
	}
	
	public void save_all_segments_as_images(String name_of_folder_to_save_images_to) {
		
		// normalise images first or save as is (and then use eg matlab to normalise)?
		//		how to normalise?  - down/upsize image proportionally so longest edge fits in the desired bounding box.
		//						   - then add white space to the other dimension to make a square.
		
		// iterate through l0_Segment_array, getting the image and saving each one (with incremental file naming)
		
		for(int i = 0; i < l0_Segment_list.size(); i++) {
			
			BufferedImage symbolImage = (l0_Segment_list.get(i)).get_image();
			
			String filename = name_of_folder_to_save_images_to + "symbol_" + i + ".png"; 
			
			File outputfile = new File(filename);
			try {  
				ImageIO.write(symbolImage, "png", outputfile);
				System.out.println("Saving symbol " + i + "\t\t in " + filename);
			} catch (IOException e) {
				System.out.println("ERROR: Probably not a valid filepath to save the symbols' images in");
			}
		}
	}
	
	public void display_segmentation_gui(String inputfile) {
		JFrame frame = new JFrame("Display L0_Segmentation Image");
		Panel panel = new L0_Segmentation_GUI(inputfile, l0_Segment_list);
		frame.getContentPane().add(panel);
		frame.setSize(width + 10, height+26); // leaves room for OS window GUI stuff
		frame.setVisible(true);
	}
	
	
	public  List<L0_Segment> segment(BufferedImage imageOfOneStave) {	
		image = imageOfOneStave;
		height = image.getHeight();
		width = image.getWidth();
		
		// segmentation along x axis
		Projection project = new Projection(image);
		int xProjection[] = project.xProject(); //project whole image
		
		//Projection.print(xProjection);
		
		{ // to contain int x below
			int x = 0;
			while(x < width) {
				//			System.out.println(x + ": " + xProjection[x]);

				if(xProjection[x] > (X_MIN_THRESHOLD)) { // beginning of l0 segment

					//int segStartx = (x == 0)? 0 : x-1; // pixel before first symbol pixel (unless x = 0, in which case 
					int segStartx = x;

					//need to find end of segment -> keep going until x projection no longer exceeds x_min_size				
					while( (x < width) && (xProjection[x] > (X_MIN_THRESHOLD)) ) {
						x++;
					}

					//int segEndx = (x == width)? x-1 : x; // pixel after last symbol pixel (unless x = width, in which 
														   //case segEndx = x-1)
					int segEndx = x-1;

					L0_Segment segment = new L0_Segment(segStartx, segEndx);
					l0_Segment_list.add(segment);
				} else {
					x++;
				}
			}
		}
		
		// pretty print segment info calculated so far /////////////////////////////////////////////////////////////////
//		for(int i = 0; i < l0_Segment_array.size(); i++) {
//			System.out.println("Segment " + i + ": " + ((L0_Segment)l0_Segment_array.get(i)).get_left_x() + " - " + ((L0_Segment)l0_Segment_array.get(i)).get_right_x());
//		}
//		System.out.println();
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		// segmentation along y axis

		// iterate through calculated segments, restricting the bounding box along the y axis -> do y projection
		// then check for pixels first downwards from top then upwards from bottom
		for(L0_Segment s : l0_Segment_list) {
			
			// use previously calculated x projections, y -> whole image height
			//int yProjection[] = project.yProject(s.get_left_x() + 1, s.get_right_x()-1, 0, height - 1); // + 1 and - 0 as the bounding boxes are around the symbol (unless its on an image edge -> then doesn't work -->  check for this?????????)
			int yProjection[] = project.yProject(s.get_left_x(), s.get_right_x(), 0, height - 1); // + 1 and - 0 as the bounding boxes are around the symbol (unless its on an image edge -> then doesn't work -->  check for this?????????)
			//Projection.print(yProjection);

			int segStarty = 0; // just to initialise
			int segEndy = 0; // just to initialise
			
			for(int y = 0; y < height; y++) {
				if(yProjection[y] > Y_MIN_THRESHOLD) {
					//segStarty = (y == 0)? 0 : y-1; // pixel before first symbol pixel (unless y = 0, in which case segStart = 0)
					segStarty = y;
					break;
				}
			}

			for(int y = height - 1; y >= 0; y--) {
				
				if(yProjection[y] > Y_MIN_THRESHOLD) {
					//segEndy = (y == height - 1)? y : y+1; // pixel after last symbol pixel // unless = bottom of image, in which case = y??????
					segEndy = y;
					break;
				}
			}
				
			
			s.set_top_y(segStarty);
			s.set_bottom_y(segEndy);
			
			//now have full bounding box, extract image of symbol from whole image of score
			int segStartx = s.get_left_x();
			int segEndx = s.get_right_x();
			
			//BufferedImage(width, height, imageType)
			int segment_image_width = segEndx - segStartx + 1;
			int segment_image_height = segEndy - segStarty + 1;
			
			BufferedImage symbolImage = new BufferedImage(segment_image_width, segment_image_height, BufferedImage.TYPE_BYTE_BINARY); // could experiment with rgb TYPE to see why anomalies with sybol bounding boxes are happening... (ie to expose light (but not white) anti aliased edge pixels)

			//image
			
			for(int x = 0; x < segment_image_width; x++) {
				int original_x_coord = segStartx + x;
				
				for(int y = 0; y < segment_image_height; y++) {
					int original_y_coord = segStarty + y;
					symbolImage.setRGB(x, y, image.getRGB(original_x_coord, original_y_coord));
				}
			}
			
			s.set_image(symbolImage);
			
		}
		
		// L0_segmentation done.
		return l0_Segment_list;
		
		// pretty print segment info calculated so far /////////////////////////////////////////////////////////////////
//		for(int i = 0; i < l0_Segment_array.size(); i++) {
//			System.out.print("Segment " + i + ": x " + ((L0_Segment)l0_Segment_array.get(i)).get_left_x() + " - " + ((L0_Segment)l0_Segment_array.get(i)).get_right_x());
//			System.out.println("\ty " + ((L0_Segment)l0_Segment_array.get(i)).get_top_y() + " - " + ((L0_Segment)l0_Segment_array.get(i)).get_bottom_y());
//		}
//		System.out.println();
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
	}
	
	
	/// private methods ////////////////////////////////////////////////////////////////////////////////////////////////

}
