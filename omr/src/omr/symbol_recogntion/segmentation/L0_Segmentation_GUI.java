package omr.symbol_recogntion.segmentation;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import javax.imageio.ImageIO;



public class L0_Segmentation_GUI extends Panel {
	
	private BufferedImage  image;
	private List<L0_Segment> l0_Segment_array = null;
	private int img_width;
	private int img_height;

	public L0_Segmentation_GUI(String inputfile, List<L0_Segment> l0_s_a) {
		try {
//			System.out.println("Enter image name\n");
//			BufferedReader bf=new BufferedReader(new 
//					InputStreamReader(System.in));
//			String imageName=bf.readLine();
			File input = new File(inputfile);
			image = ImageIO.read(input);
		} catch (IOException ie) {
			System.out.println("Error:"+ie.getMessage());
		}
		
		img_width = image.getWidth();
		img_height = image.getHeight();
		
		l0_Segment_array = l0_s_a;
	}

	public void paint(Graphics g) {
		int dstx1 = 0;
		int dsty1 = 0;
		int dstx2 = (19-4)*3;
		int dsty2 = 69*3;
		int srcx1 = 4;
		int srcy1 = 0;
		int srcx2 = 19;
		int srcy2 = 69;
				
		//g.drawImage( image, 
		//	       dstx1, dsty1, dstx2, dsty2,
		//	       srcx1, srcy1, srcx2, srcy2,
		//	       null);
		
		g.drawImage(image,0,0,null);
		
		//g.drawRect(3, 3, 16, 69);
		
		for(L0_Segment s : l0_Segment_array) { // iterator (shorthand syntax)
			
			int l0_start_x = s.get_left_x();
			int l0_end_x = s.get_right_x();
			int l0_start_y = s.get_top_y();
			int l0_end_y = s.get_bottom_y();
			
			
			int rectDstx = l0_start_x-1;
			int rectDsty = l0_start_y-1;
			int rectWidth = (l0_end_x) - (l0_start_x) +1; /// work out why some symbols' bounding boxes have 1 px white border on right side and some don't -> possibly grayscale so curved shapes have antialiased 
			int rectHeight = l0_end_y - l0_start_y +1;
			
			 g.setColor(Color.green);
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