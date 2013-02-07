package omr.symbol_recogntion.segmentation;

public class Note_Stem {
	private int left_x;
	private int right_x;
	private int top_y;
	private int bottom_y;
	
	public Note_Stem(int left_x, int right_x) {
		this.left_x = left_x;
		this.right_x = right_x;


	}
	
	public Note_Stem(int left_x, int right_x, int top_y, int bottom_y) {
		this.left_x = left_x;
		this.right_x = right_x;
		this.top_y = top_y;
		this.bottom_y = bottom_y;

	}
	
	
	public int getLeft_x() {
		return left_x;
	}
	public void setLeft_x(int left_x) {
		this.left_x = left_x;
	}
	public int getRight_x() {
		return right_x;
	}
	public void setRight_x(int right_x) {
		this.right_x = right_x;
	}
	public int getTop_y() {
		return top_y;
	}
	public void setTop_y(int top_y) {
		this.top_y = top_y;
	}
	public int getBottom_y() {
		return bottom_y;
	}
	public void setBottom_y(int bottom_y) {
		this.bottom_y = bottom_y;
	}
	
}
