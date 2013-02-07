package omr.symbol_recogntion.segmentation;

import java.awt.image.BufferedImage;
import java.util.List;

import omr.symbol_recogntion.stave_detection.StaveIdentification;

public class Stave {
	private int left_x_coord;
	private int right_x_coord;
	
	private int top_y_coord;
	private int bottom_y_coord;
	
	private BufferedImage stave_original_image;
	private BufferedImage stave_without_stave_lines;
	private BufferedImage just_stave_lines;
	
	private List<L0_Segment> l0_Segment_list;
	
	private StaveIdentification staveSkeleton;



	/// constructors ///////////////////////////////////////////////////////////////////////////////////////////////////
	public Stave() {
		
	}
	
	public Stave(int left, int right) {
		left_x_coord = left;
		right_x_coord = right;
		
	}
	
	public Stave(int left, int right, int top, int bottom) {
		left_x_coord = left;
		right_x_coord = right;
		top_y_coord = top;
		bottom_y_coord = bottom;	
	}
	
	public Stave(int left, int right, int top, int bottom, BufferedImage img) {
		left_x_coord = left;
		right_x_coord = right;
		top_y_coord = top;
		bottom_y_coord = bottom;
		stave_original_image = img;
	}	
	
	
	/// get set methods ////////////////////////////////////////////////////////////////////////////////////////////////
		public StaveIdentification getStaveSkeleton() {
		return staveSkeleton;
	}

	public void setStaveSkeleton(StaveIdentification staveSkeleton) {
		this.staveSkeleton = staveSkeleton;
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
		stave_original_image = img;
	}
	public BufferedImage get_image() {
		return stave_original_image;
	}	
	
	
	public void set_L0_Segment_list(List<L0_Segment> l0SegmentList) {
		l0_Segment_list = l0SegmentList;
	}
	
	public List<L0_Segment> get_L0_Segment_list() {
		return l0_Segment_list;
	}

	
	
	
	public BufferedImage getStave_without_stave_lines() {
		return stave_without_stave_lines;
	}

	public void setStave_without_stave_lines(BufferedImage stave_without_stave_lines) {
		this.stave_without_stave_lines = stave_without_stave_lines;
	}

	public BufferedImage getJust_stave_lines() {
		return just_stave_lines;
	}

	public void setJust_stave_lines(BufferedImage just_stave_lines) {
		this.just_stave_lines = just_stave_lines;
	}
	
	
}
