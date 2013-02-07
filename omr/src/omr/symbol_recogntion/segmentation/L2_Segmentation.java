package omr.symbol_recogntion.segmentation;

import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import omr.symbol_recogntion.score_metrics.ScoreMetrics;
import omr.util.ImageProcessing;
import omr.util.Projection;


	//LOGIC //////
	// take in l1_Segment

public class L2_Segmentation {
	private static final int X_MIN_THRESHOLD = 1;
	private static final int Y_MIN_THRESHOLD = 2;
	
	// state ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private ScoreMetrics scoreMetrics;
	
	
	// main ////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// create artificial l1 segment
		L1_Segment l1seg = new L1_Segment();
		
		BufferedImage l1_segment_image = ImageProcessing.loadImage("/Users/buster/eclipseWorkspace/omr/l1Segs/symbol_2.png");
		l1seg.set_image(l1_segment_image);
		l1seg.set_left_x(0);
		l1seg.set_right_x(l1_segment_image.getWidth() - 1);
		l1seg.set_top_y(0);
		l1seg.set_bottom_y(l1_segment_image.getHeight() - 1);
		
	
		ScoreMetrics sm = new ScoreMetrics(l1_segment_image);
		
		
		L2_Segmentation l2Seg = new L2_Segmentation(sm);
		List<L2_Segment> l2_seg_list = l2Seg.segment(l1seg);
		
		
		for(L2_Segment l2 : l2_seg_list) {
			ImageProcessing.displayImage(l2.get_image(), "l2seg");
		}
		
		l2Seg.display_segmentation_gui(l1_segment_image, l2_seg_list);
	}

	
	// constructor /////////////////////////////////////////////////////////////////////////////////////////////////////
	public L2_Segmentation(ScoreMetrics score_metrics) {
		
		scoreMetrics = score_metrics;		
	}
	
	
	// public methods //////////////////////////////////////////////////////////////////////////////////////////////////
	public List<L2_Segment> segment(L1_Segment l1Segment) {
		
		List<L2_Segment> l2_segment_list = new ArrayList<L2_Segment>();

		BufferedImage l1_segment_image = l1Segment.get_image();
		int width = l1_segment_image.getWidth();
		int height = l1_segment_image.getHeight();
		
		
		System.out.println("L1 Segment top_y = " + l1Segment.get_top_y());
		System.out.println("L1 Segment left_x = " + l1Segment.get_left_x());
		
		System.out.println("L1 Segment height = " + l1Segment.get_height());
		System.out.println("L1 Segment width = " + l1Segment.get_width());
		
		
		// does the l1_segment contain a note? (using note stem detection?)
		// if((contains vertical run > min_note_stem_height) && (l1_segment.width > min_note_head_width)) { // the latter condition prevents us from thinking bar lines are note_stems
		//		remove stem;
		//		vertical segmentation;
		//			-> giving list of l2_Segments (hopefully images of individual symbols)
		
		//		// probable problem -> multiple angled beams wont be segmented using vertical segmentation...
		//		//solutions: 1) rotate beams
		//		//		     2) connected component labelling
		//		//			 3) have symbol for eg double beam, triple beam
		
		//} else {
		//		vertical segmentation;
		//			-> giving list of l2_Segments
		//}
		
		
		//vertical segmentation based on y projection
		{ 
			Projection project = new Projection(l1_segment_image);
		
		int yProj[] = project.yProject(); //project whole image
		
		 // to contain int y below
			int y = 0;
			while(y < height) {
				//			System.out.println(x + ": " + xProjection[x]);

				if(yProj[y] > Y_MIN_THRESHOLD) { // beginning of l2 segment

					//int segStartx = (x == 0)? 0 : x-1; // pixel before first symbol pixel (unless x = 0, in which case 
					int segStarty = y;

					//need to find end of segment -> keep going until x projection no longer exceeds x_min_size				
					while( (y < height) && (yProj[y] > (Y_MIN_THRESHOLD)) ) {
						y++;
					}

					//int segEndx = (x == width)? x-1 : x; // pixel after last symbol pixel (unless x = width, in which 
														   //case segEndx = x-1)
					int segEndy = y-1;

					L2_Segment l2segment = new L2_Segment();
					
					l2segment.set_top_y(segStarty + l1Segment.get_top_y()); ///// absolute!!!!!
					l2segment.set_bottom_y(segEndy + l1Segment.get_top_y()); ///// absolute!!!!!
					l2segment.set_left_x(l1Segment.get_left_x());
					l2segment.set_right_x(l1Segment.get_right_x());
//					l2segment.calc_and_set_image(l1_segment_image);
					
					System.out.println("\tL2 Segment top_y = " + l2segment.get_top_y() + "\tsegStarty = " + segStarty);
					System.out.println("\tL2 Segment left_x = " + l2segment.get_left_x());
					
					System.out.println("\tL2 Segment height = " + l2segment.get_height());
					System.out.println("\tL2 Segment width = " + l2segment.get_width());
					System.out.println();



					BufferedImage l2sImage = l1_segment_image.getSubimage(0, segStarty , l2segment.get_width() - 1, l2segment.get_height());
					l2segment.set_image(l2sImage);
					
					l2_segment_list.add(l2segment);
					
				} else {
					y++;
				}
			}
		}
		
		//restrict bounding box along x axis
		for(L2_Segment l2s : l2_segment_list) {
			
			// do x proj on image
			Projection project = new Projection(l2s.get_image());
			int xProjection[] = project.xProject(); //project whole image
			
			{ // to contain int x below
				int x = 0;
				while(x < l2s.get_width()) {
					//			System.out.println(x + ": " + xProjection[x]);

					if(xProjection[x] > (X_MIN_THRESHOLD)) { // beginning of l0 segment

						//int segStartx = (x == 0)? 0 : x-1; // pixel before first symbol pixel (unless x = 0, in which case 
						int segStartx = x;

						//need to find end of segment -> keep going until x projection no longer exceeds x_min_size				
						while( (x < l2s.get_width()) && (xProjection[x] > (X_MIN_THRESHOLD)) ) {
							x++;
						}

						//int segEndx = (x == width)? x-1 : x; // pixel after last symbol pixel (unless x = width, in which 
															   //case segEndx = x-1)
						int segEndx = x-1;
						l2s.set_left_x(l2s.get_left_x() + segStartx);
						l2s.set_right_x(l2s.get_left_x() + segEndx);

					} else {
						x++;
					}
				}
			}
			//find component
			
			//change bounding box x values
			
		}
		
		
		return l2_segment_list;
		
	}
	
	//private methods //////////////////////////////////////////////////////////////////////////////////////////////////
	public static void display_segmentation_gui(BufferedImage image, List<L2_Segment> l2_segment_list) {
		JFrame frame = new JFrame("Display image (with L1_Segmentation Bounding Boxes)");
		Panel panel = new L2_Segmentation_GUI(image, l2_segment_list);
		frame.getContentPane().add(panel);
		frame.setSize(image.getWidth() + 10, image.getHeight()+26); // leaves room for OS window GUI stuff
		frame.setVisible(true);
	}
	
}
