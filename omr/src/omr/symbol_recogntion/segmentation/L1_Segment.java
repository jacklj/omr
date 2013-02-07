package omr.symbol_recogntion.segmentation;

import java.awt.image.BufferedImage;
import java.util.List;

public class L1_Segment {
	private int left_x_coord;
	private int right_x_coord;
	
	private int top_y_coord;
	private int bottom_y_coord;
		
	private BufferedImage segment_image;
	
	private List<L2_Segment> l2_segment_list;
	
	private boolean noteDetected;

	/// constructors ///////////////////////////////////////////////////////////////////////////////////////////////////
	public L1_Segment() {
		
	}
	
	public L1_Segment(int left, int right) {
		left_x_coord = left;
		right_x_coord = right;
		
	}
	
	public L1_Segment(int left, int right, int top, int bottom) {
		left_x_coord = left;
		right_x_coord = right;
		top_y_coord = top;
		bottom_y_coord = bottom;
	}
	
	public L1_Segment(int left, int right, int top, int bottom, BufferedImage img) {
		left_x_coord = left;
		right_x_coord = right;
		top_y_coord = top;
		bottom_y_coord = bottom;
		segment_image = img;
	}	
	
	


	/// get set methods ////////////////////////////////////////////////////////////////////////////////////////////////
		public boolean isNoteDetected() {
		return noteDetected;
	}

	public void setNoteDetected(boolean noteDetected) {
		this.noteDetected = noteDetected;
	}
	
	public void set_left_x(int left) {
		left_x_coord = left;
	}
	public int get_left_x() {
		return left_x_coord;
	}
	
	
	public void set_right_x(int right) {
		right_x_coord = right;
	}
	public int get_right_x() {
		return right_x_coord;
	}
	
	
	public void set_top_y(int top) {
		top_y_coord = top;
	}
	public int get_top_y() {
		return top_y_coord;
	}
	
	
	public void set_bottom_y(int bottom) {
		bottom_y_coord = bottom;
	}
	public int get_bottom_y() {
		return bottom_y_coord;
	}	
	
	
	public void set_image(BufferedImage img) {
		segment_image = img;
	}
	public BufferedImage get_image() {
		return segment_image;
	}
	
	
	public int get_height() {
		int height = Math.abs(bottom_y_coord-top_y_coord) + 1; // absolute() ensures always correct whatever the coordinate 
															   // system direction conventions (ie positively increases downwards or upwards)
		return height;
	}
	
	public int get_width() {
		int width = Math.abs(right_x_coord - left_x_coord) + 1; //	" " 			" "
		return width;
	}
	
	
	public void setL2_SegmentList(List<L2_Segment> l2SegmentList) {
		l2_segment_list = l2SegmentList;
	}
	
	public List<L2_Segment> getL2_SegmentList() {
		return l2_segment_list;
	}
	
	
}
