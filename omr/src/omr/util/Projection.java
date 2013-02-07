package omr.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;


//INFO /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//1. the x and yProject functions will project inclusive of the bounding box pixels (i.e. projects from x_left to 
//   x_right inclusive and y_top to y_bottom inclusive)
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//KNOWN BUGS ///////////////////////////////////////////////////////////////////////////////////////////////////////////
//1.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//TO DO ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//1.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////





public class Projection {


	/// state //////////////////////////////////////////////////////////////////////////////////////////////////////////
	private BufferedImage image;
	private int width;
	private int height;
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/// public static void main - for testing //////////////////////////////////////////////////////////////////////////
	public static void main(String args[]) {

		BufferedImage img1 = null;	
		String filePath = "//Users//buster//Stuff//Academia//II//DISSERTATION//symbol_sets//sibelius//pngs//without_staves//singleLines//helsinki1.png";
//		String filePath = "//Users//buster//Stuff//Academia//II//DISSERTATION//test_images//ich_grolle_nicht_small.png";
		try {
			img1 = ImageIO.read(new File(filePath)); 

		} catch (FileNotFoundException e) {
			System.out.println("File not found exception");
		} catch (IOException e) {
			System.out.println("IO exception");
		}
		
		
		System.out.println("height: " + img1.getHeight());
		System.out.println("width: " + img1.getWidth());
		Projection project = new Projection(img1);
		
		int xProjection[] = project.xProject(); //project whole image
		Projection.print(xProjection);
		
		int yProjection[] = project.yProject(); //project whole image
		Projection.print(yProjection);		

	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	
	// constructor /////////////////////////////////////////////////////////////////////////////////////////////////////	
	public Projection(BufferedImage img) {
		image = img;
		width = image.getWidth();
		height = image.getHeight();
	}

	
	// public methods //////////////////////////////////////////////////////////////////////////////////////////////////
	public int[] xProject() { // x projects whole image
		int left_x = 0;
		int right_x = width - 1; // as pixel array in BufferedImage is addressed from 0 to (width - 1)
		int top_y = 0;
		int bottom_y = height - 1; // " "   " "
		
		int xProjection[] = this.xProject(left_x, right_x, top_y, bottom_y);
		return xProjection;
	}
	
	public int[] xProject(int left_x, int right_x, int top_y, int bottom_y) { // x projects bounding box part of image 
																			  // (sides of bounding box inclusive)

		int xProjection[] = new int[right_x - left_x + 1];

		for(int x=left_x; x <= right_x; x++) {
			xProjection[x] = 0; // to make sure initialised properly
			for (int y = top_y; y <= bottom_y; y++) {
				int colour = image.getRGB(x,y); // -1 == white. else black.
				Colour current_pixel_colour;
				if (colour == -1) {current_pixel_colour = Colour.WHITE;}
				else {current_pixel_colour = Colour.BLACK;}

				if (current_pixel_colour == Colour.BLACK) {
					xProjection[x] = xProjection[x] + 1;
				}
			}
		}

		return xProjection;
	}

	
	public int[] yProject() { // y projects whole image
		
		int left_x = 0;
		int right_x = width - 1; // as pixel array in BufferedImage is addressed from 0 to (width - 1)
		int top_y = 0;
		int bottom_y = height - 1; // " "   " "
		
		int yProjection[] = this.yProject(left_x, right_x, top_y, bottom_y);
		return yProjection;
	}
	
	public int[] yProject(int left_x, int right_x, int top_y, int bottom_y) { // y projects bounding box part of image

		int yProjection[] = new int[bottom_y - top_y + 1];

		for(int y=top_y; y <= bottom_y; y++) {
			for (int x = left_x; x <= right_x; x++) {
				int colour = image.getRGB(x,y); // -1 == white. else black.
				Colour current_pixel_colour;
				if (colour == -1) {current_pixel_colour = Colour.WHITE;}
				else {current_pixel_colour = Colour.BLACK;}

				if (current_pixel_colour == Colour.BLACK) {
					yProjection[y] = yProjection[y] + 1;
				}
			}
		}


		return yProjection;
	}
	
	
	public static void print(int projection[]) {
		
		String toPrint = "[";
		for(int i=0; i < projection.length; i++) {
			toPrint = toPrint.concat(Integer.toString(projection[i]));
			if(!(i == (projection.length - 1))) {
				toPrint = toPrint.concat(",");
			}
		}
		toPrint = toPrint.concat("]");
		System.out.println(toPrint);
	}
	
}
