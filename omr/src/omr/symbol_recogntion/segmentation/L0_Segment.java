package omr.symbol_recogntion.segmentation;

import java.awt.image.BufferedImage;
import java.util.List;

import omr.symbol_recogntion.classifier.basicSymbolSet.Symbol;

public class L0_Segment {
	private int left_x_coord;
	private int right_x_coord;
	
	private int top_y_coord;
	private int bottom_y_coord;
	
	private BufferedImage segment_image;
	
	private List<L1_Segment> l1_segment_list;

	Symbol symbol; // for basic symbol set
	
	/// constructors ///////////////////////////////////////////////////////////////////////////////////////////////////
	public L0_Segment() {
		
	}
	
	public L0_Segment(int left, int right) {
		left_x_coord = left;
		right_x_coord = right;
		
	}
	
	public L0_Segment(int left, int right, int top, int bottom) {
		left_x_coord = left;
		right_x_coord = right;
		top_y_coord = top;
		bottom_y_coord = bottom;	
	}
	
	public L0_Segment(int left, int right, int top, int bottom, BufferedImage img) {
		left_x_coord = left;
		right_x_coord = right;
		top_y_coord = top;
		bottom_y_coord = bottom;
		segment_image = img;
	}	
	
	
	/// get set methods ////////////////////////////////////////////////////////////////////////////////////////////////
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
	
	
	public void setL1_SegmentList(List<L1_Segment> l1SegmentList) {
		l1_segment_list = l1SegmentList;
	}
	
	public List<L1_Segment> getL1_SegmentList() {
		return l1_segment_list;
	}
	
	public Symbol getSymbol() {
		return this.symbol;
	}
	
	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}
	
}
