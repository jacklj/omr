package omr.symbol_recogntion.segmentation;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import javax.imageio.ImageIO;



public class L2_Segmentation_GUI extends Panel {
	
	private BufferedImage  image;
	private List<L2_Segment> l2_Segment_array = null;
	private int img_width;
	private int img_height;

	public L2_Segmentation_GUI(BufferedImage image, List<L2_Segment> l2_s_a) {
		this.image = image;
		img_width = image.getWidth();
		img_height = image.getHeight();
		
		l2_Segment_array = l2_s_a;
	}

	public void paint(Graphics g) {
		
		g.drawImage(image,0,0,null);
		
		//g.drawRect(3, 3, 16, 69);
		
		for(L2_Segment s : l2_Segment_array) { // iterator (shorthand syntax)
			
			int l2_start_x = s.get_left_x();
			int l2_end_x = s.get_right_x();
			int l2_start_y = s.get_top_y();
			int l2_end_y = s.get_bottom_y();
			
			
			int rectDstx = l2_start_x-1;
			int rectDsty = l2_start_y-1;
			int rectWidth = (l2_end_x) - (l2_start_x) +1; /// work out why some symbols' bounding boxes have 1 px white border on right side and some don't -> possibly grayscale so curved shapes have antialiased 
			int rectHeight = l2_end_y - l2_start_y +1;
			g.setColor(Color.magenta);
			g.drawRect(rectDstx, rectDsty, rectWidth, rectHeight);
		}
	}

//	static public void main(String args[]) throws
//	Exception {
//		JFrame frame = new JFrame("Display image");
//		Panel panel = new TestGUI2();
//		frame.getContentPane().add(panel);
//		frame.setSize(500, 500);
//		frame.setVisible(true);
//		
//	}
}