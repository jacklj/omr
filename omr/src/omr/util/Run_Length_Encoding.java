package omr.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;

// INFO ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//1. assumes first pixel is black. if white, first array value is 0 (ie 0 black pixels before first white one)
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// KNOWN BUGS (talked about in reference to rle along y axis, but apply to both algorithms) ////////////////////////////
// 1. if image one pixel wide, the main rle loop from y = 1 will immediately fail so nothing will ever be added to the 
//    rle list
//
// 2. (not really bug but not best implementation possible) So that we can use the simple java type int[][], we must 
//    initialise this with stated array lengths (ie int[][] 2dArray = new int[width][height];). This means the inner 
//	  array can't expand dynamically (preferable, because we don't know how long its going to be - depends on the run
//	  length encoding of that column = eg if all black will be one int long, if alternating single white and black
//	  pixels (and starting with a white pixel) it will be the max, image_height + 1 long.
//    As this is the max, to ensure the inner array is always big enough, we must initialise it to the max possible
//	  value, image_height + 1. However this is a waste of memory, and also messy - each inner run length encoding list
//	  has trailing it '(image_height + 1)- run_length_encoding_list_actual_length' 0s.
//
//	  The alternative solution would be to have an inner array which can dynamically expand. This would also mean we
//	  could use the array.add(x) method rather than having to keep track of the array index as we currently do:
//			rle_2d_array_x_y[x][inner_list_length_accumulator__insert_next_element_here] = run_length_accumulator;
//			inner_list_length_accumulator__insert_next_element_here++;
//	  However then we would have to create a new class for 2D Arrays, increasing the complexity of the code.
//	
//	  SOLVED -> at the end of both r l encoding methods, excess zero's are chopped off
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// TO DO ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 1. implement automatic test harness by storing rle ground truths for each of the test images and checking results 
// 	  against them
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


public class Run_Length_Encoding {
	
	/// state //////////////////////////////////////////////////////////////////////////////////////////////////////////
	private BufferedImage image = null;
	private int height;
	private int width;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public static void main(String[] args) {
		
		//runTestHarness();
		
		// or run on individual image
		String fileName = "rle_test1.png";

		
		BufferedImage img1 = null;
		try {
			img1 = ImageIO.read(new File("//Users//buster//Stuff//Academia//II//DISSERTATION//test_images//" + fileName)); 

		} catch (FileNotFoundException e) {
			System.out.println("[ERROR] File not found exception");
		} catch (IOException e) {
			System.out.println("[ERROR] IO exception");
		}

		
		Run_Length_Encoding rle = new Run_Length_Encoding(img1);


		System.out.println("Along y axis: ");
		int[][] test_along_y = rle.RLE_2D_along_y_axis();
		for(int x = 0; x < test_along_y.length; x++) {
			for(int y = 0; y < test_along_y[x].length; y++) {
				System.out.print(test_along_y[x][y] + " ");
			}
			System.out.println();
		}

		System.out.println();
		
		System.out.println("Along y axis (0s removed): ");
		
		int[][] test_along_y_new = Run_Length_Encoding.removeInnerArrayTrailing0s(test_along_y);
		
		for(int x = 0; x < test_along_y_new.length; x++) {
			for(int y = 0; y < test_along_y_new[x].length; y++) {
				System.out.print(test_along_y_new[x][y] + " ");
			}
			System.out.println();
		}

		System.out.println();
		
		


		System.out.println("Along x axis: ");
		int[][] test_along_x = rle.RLE_2D_along_x_axis();
		for(int y = 0; y < test_along_x.length; y++) {
			for(int x = 0; x < test_along_x[y].length; x++) {
				System.out.print(test_along_x[y][x] + " ");
			}
			System.out.println();
		}

		System.out.println();
		
		System.out.println("Along x axis (0s removed)");
		
		int[][] test_along_x_new = Run_Length_Encoding.removeInnerArrayTrailing0s(test_along_x);
		for(int y = 0; y < test_along_x_new.length; y++) {
			for(int x = 0; x < test_along_x_new[y].length; x++) {
				System.out.print(test_along_x_new[y][x] + " ");
			}
			System.out.println();
		}

		System.out.println();	
		
		
		System.out.println();

	}



	// constructor /////////////////////////////////////////////////////////////////////////////////////////////////////
	public Run_Length_Encoding(BufferedImage img) {
		image = img;
		height = image.getHeight();
		width = image.getWidth();
	}
			

	// public methods //////////////////////////////////////////////////////////////////////////////////////////////////
	public int[][] RLE_2D_along_y_axis() {

		int[][] rle_2d_array_x_y = new int[width][height+1]; 
		
		for(int x = 0; x< width; x++) {
			
			Colour previous_pixel_colour;
			int inner_list_length_accumulator__insert_next_element_here = 0;
			int run_length_accumulator = 0;
			
			
			// first pixel treated differently as there's no 'previous_pixel' //////////////////////////////////////////
			
			int colour = image.getRGB(x,0); // -1 == white. else black.
			Colour current_pixel_colour;
			if (colour == -1) {current_pixel_colour = Colour.WHITE;}
			else {current_pixel_colour = Colour.BLACK;}
			
			
			if (current_pixel_colour == Colour.WHITE) {
				rle_2d_array_x_y[x][inner_list_length_accumulator__insert_next_element_here] = 0; // i.e. 0 black pixels
																	// first (convention - first run length is black)
				inner_list_length_accumulator__insert_next_element_here++; // = 1
			}
			else { // BLACK
				// inner_list_length_accumulator__insert_next_element_here = 0
			}
			////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			run_length_accumulator++; // = 1
			previous_pixel_colour = current_pixel_colour;


			for(int y = 1; y < height; y++) {

				colour = image.getRGB(x,y); // -1 == white. else black.
				if (colour == -1) {current_pixel_colour = Colour.WHITE;}
				else {current_pixel_colour = Colour.BLACK;}
				
				
				// to avoid final run length being lost ////////////////////////////////////////////////////////////////
				if(y == height - 1) {

					if (current_pixel_colour == previous_pixel_colour) {
						run_length_accumulator++;
					}
					else { // one run done - add accumulator value to the list
						rle_2d_array_x_y[x][inner_list_length_accumulator__insert_next_element_here] = run_length_accumulator;
						inner_list_length_accumulator__insert_next_element_here++;
						//rle_list.add(run_length_accumulator);
						previous_pixel_colour = current_pixel_colour;
						run_length_accumulator = 1;
					}

					//then add final value
					
					rle_2d_array_x_y[x][inner_list_length_accumulator__insert_next_element_here] = run_length_accumulator;
					inner_list_length_accumulator__insert_next_element_here++;

				}
				////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				else if (current_pixel_colour == previous_pixel_colour) { // same colour - increment current run length counter
					run_length_accumulator++;
				}
				else { // one run done - add accumulator value to the list
					rle_2d_array_x_y[x][inner_list_length_accumulator__insert_next_element_here] = run_length_accumulator;
					inner_list_length_accumulator__insert_next_element_here++;
					//rle_list.add(run_length_accumulator);
					previous_pixel_colour = current_pixel_colour;
					run_length_accumulator = 1;
				}

			}
		}

		//now remove trailing 0's from rle_2d_array_x_y
		int[][] rle_0s_removed = Run_Length_Encoding.removeInnerArrayTrailing0s(rle_2d_array_x_y);
		return rle_0s_removed;
	}

	
	public int[][] RLE_2D_along_x_axis() {

		int[][] rle_2d_array_y_x = new int[height][width+1]; 
		
		for(int y = 0; y< height; y++) {
			
			Colour previous_pixel_colour;
			int inner_list_length_accumulator__insert_next_element_here = 0;
			int run_length_accumulator = 0;
			
			
			// first pixel treated differently as there's no 'previous_pixel' //////////////////////////////////////////
			
			int colour = image.getRGB(0,y); // -1 == white. else black.
			Colour current_pixel_colour;
			if (colour == -1) {current_pixel_colour = Colour.WHITE;}
			else {current_pixel_colour = Colour.BLACK;}
			
			
			if (current_pixel_colour == Colour.WHITE) {
				rle_2d_array_y_x[y][inner_list_length_accumulator__insert_next_element_here] = 0; // i.e. 0 black pixels
																	// first (convention - first run length is black)
				inner_list_length_accumulator__insert_next_element_here++; // = 1
			}
			else { // BLACK
				// inner_list_length_accumulator__insert_next_element_here = 0
			}
			////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			run_length_accumulator++; // = 1
			previous_pixel_colour = current_pixel_colour;


			for(int x = 1; x < width; x++) {

				colour = image.getRGB(x,y); // -1 == white. else black.
				if (colour == -1) {current_pixel_colour = Colour.WHITE;}
				else {current_pixel_colour = Colour.BLACK;}
				
				
				// to avoid final run length being lost ////////////////////////////////////////////////////////////////
				if(x == width - 1) {

					if (current_pixel_colour == previous_pixel_colour) {
						run_length_accumulator++;
					}
					else { // one run done - add accumulator value to the list
						rle_2d_array_y_x[y][inner_list_length_accumulator__insert_next_element_here] = run_length_accumulator;
						inner_list_length_accumulator__insert_next_element_here++;
						//rle_list.add(run_length_accumulator);
						previous_pixel_colour = current_pixel_colour;
						run_length_accumulator = 1;
					}

					//then add final value
					
					rle_2d_array_y_x[y][inner_list_length_accumulator__insert_next_element_here] = run_length_accumulator;
					inner_list_length_accumulator__insert_next_element_here++;

				}
				////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				else if (current_pixel_colour == previous_pixel_colour) { // same colour - increment current run length counter
					run_length_accumulator++;
				}
				else { // one run done - add accumulator value to the list
					rle_2d_array_y_x[y][inner_list_length_accumulator__insert_next_element_here] = run_length_accumulator;
					inner_list_length_accumulator__insert_next_element_here++;
					//rle_list.add(run_length_accumulator);
					previous_pixel_colour = current_pixel_colour;
					run_length_accumulator = 1;
				}

			}
		}

		//now remove trailing 0's from rle_2d_array_y_x
		int[][] rle_0s_removed = Run_Length_Encoding.removeInnerArrayTrailing0s(rle_2d_array_y_x);
		return rle_0s_removed;
	}

	
	public static Colour whatColourIsRun(int i, int j) {
		Colour colour;
		
		if(j % 2 == 0) {colour = Colour.BLACK;}
		else {colour = Colour.WHITE;}
		
		return colour;
	}
	
	
	public static BufferedImage convert_rle_along_x_axis_to_image(int[][] rle) {
		
		int height = rle.length; // as outer loop is one element per row
		int width = 0;
		for(int i = 0; i < rle[0].length; i++) { // iterate through the first run length encoded inner array to 
												 // calculate the original image width
			width = width + rle[0][i];
		}
		// now width should be correct
		
		
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		
		for (int y = 0; y < rle.length; y++) {
			int actual_x = 0;
			
//			System.out.println();

			for (int i = 0; i < rle[y].length; i++) {
				int run_length = rle[y][i];
				Colour run_colour = Run_Length_Encoding.whatColourIsRun(y, i);
				
				int rgb; // to play nicely with BufferedImage.setRGB(int x, int y, int colour)
				if(run_colour == Colour.BLACK) {rgb = 0;} // black
				else {rgb = -1;} // WHITE

				for(int x = actual_x; x < actual_x + run_length; x++) { // may be doing 1 too many / few		
					newImage.setRGB(x, y, rgb);
				}

				actual_x = actual_x + run_length; // may be an issue with adding 1 too many

			}
		}
		
		return newImage;
	}
	
	public static BufferedImage convert_rle_along_y_axis_to_image(int[][] rle) {
		
		int width = rle.length; // as outer loop is one element per column
		int height = 0;
		for(int i = 0; i < rle[0].length; i++) { // iterate through the first run length encoded inner array to 
												 // calculate the original image height
			height = height + rle[0][i];
		}
		// now height should be correct
		
		
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		
		for (int x = 0; x < rle.length; x++) {
			int actual_y = 0;
			
//			System.out.println();

			for (int j = 0; j < rle[x].length; j++) {
				int run_length = rle[x][j];
				Colour run_colour = Run_Length_Encoding.whatColourIsRun(x, j);
				
				int rgb; // to play nicely with BufferedImage.setRGB(int x, int y, int colour)
				if(run_colour == Colour.BLACK) {rgb = 0;} // black
				else {rgb = -1;} // WHITE

				for(int y = actual_y; y < actual_y + run_length; y++) { // may be doing 1 too many / few		
					newImage.setRGB(x, y, rgb);
				}

				actual_y = actual_y + run_length; // may be an issue with adding 1 too many

			}
		}
		
		return newImage;
	}
	
	
	// private methods /////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[][] removeInnerArrayTrailing0s (int[][] oldArray) {
		
		int[][] newArray = new int[oldArray.length][];
		
		
		
		for(int i = 0;i < oldArray.length; i++) {
			int zeroesStartAtIndex = 0;
			
			//1) count number of non zeroes in inner array (always ignoring if the first element is a 0)
			for(int j = 1; j < oldArray[i].length; j++) { // ignore element 0
				if(oldArray[i][j] == 0) { // if run length encoding fills inner array (therefore no trailing 0's, wont work........!!!!!!!!)
					zeroesStartAtIndex = j;
					break;
				}
			}
			
			if(zeroesStartAtIndex == 0) { // no trailing 0's - new inner array should be same length as old
				zeroesStartAtIndex = oldArray[i].length;
			}
			
			// so inner array should have length zeroesStartAtIndex (e.g. [3,1,3,2,4,0,0,0,....] -> zeroesStartAtIndex = 5;)
			int lengthOfNewInnerArray = zeroesStartAtIndex;
			//System.out.println("[DEBUG] lengthOfNewInnerArray = " + lengthOfNewInnerArray);
			//now intitialise inner array of the correct length in newArray
			newArray[i] = new int[lengthOfNewInnerArray];
			//copy elements in
			for(int j = 0; j < lengthOfNewInnerArray; j++) {
				newArray[i][j] = oldArray[i][j];
			}
		
		}
		
		return newArray;
	}
	
	
	private static void runTestHarness() {
		
		BufferedImage img1 = null;
		
		String[] test_images = {
				"1by1_black.png",
				"1by1_white.png",
				"2by2.png",
				"3by3_black.png",
				"3by3_white.png",
				"4by2_rectangle.png",
				"4by8_rectangle.png",
				"rle_test1.png",
				"rle_test2.png"
		};

		for(int i = 0; i < test_images.length; i++) {
			
			String fileName = test_images[i];
			
			System.out.println("File = " + fileName);
			System.out.println();
			
			try {
				img1 = ImageIO.read(new File("//Users//buster//Stuff//Academia//II//DISSERTATION//test_images//" + fileName)); 

			} catch (FileNotFoundException e) {
				System.out.println("[ERROR] File not found exception");
			} catch (IOException e) {
				System.out.println("[ERROR] IO exception");
			}

			Run_Length_Encoding rle = new Run_Length_Encoding(img1);

			
			System.out.println("Along y axis: ");
			int[][] test_along_y = rle.RLE_2D_along_y_axis();
			for(int x = 0; x < test_along_y.length; x++) {
				for(int y = 0; y < test_along_y[0].length; y++) {
					System.out.print(test_along_y[x][y] + " ");
				}
				System.out.println();
			}

			System.out.println();

			System.out.println("Along x axis: ");
			int[][] test_along_x = rle.RLE_2D_along_x_axis();
			for(int y = 0; y < test_along_x.length; y++) {
				for(int x = 0; x < test_along_x[0].length; x++) {
					System.out.print(test_along_x[y][x] + " ");
				}
				System.out.println();
			}
			
			System.out.println();
			System.out.println();
			
			
			// have printed out, now check against ground truth for automated test harness
			
			
		}

	}
	
//////// static versions of rle algs //////////////////////////////////
	
	public static int[][] RLE_2D_along_y_axis(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		
		int[][] rle_2d_array_x_y = new int[width][height+1]; 
		
		for(int x = 0; x< width; x++) {
			
			Colour previous_pixel_colour;
			int inner_list_length_accumulator__insert_next_element_here = 0;
			int run_length_accumulator = 0;
			
			
			// first pixel treated differently as there's no 'previous_pixel' //////////////////////////////////////////
			
			int colour = image.getRGB(x,0); // -1 == white. else black.
			Colour current_pixel_colour;
			if (colour == -1) {current_pixel_colour = Colour.WHITE;}
			else {current_pixel_colour = Colour.BLACK;}
			
			
			if (current_pixel_colour == Colour.WHITE) {
				rle_2d_array_x_y[x][inner_list_length_accumulator__insert_next_element_here] = 0; // i.e. 0 black pixels
																	// first (convention - first run length is black)
				inner_list_length_accumulator__insert_next_element_here++; // = 1
			}
			else { // BLACK
				// inner_list_length_accumulator__insert_next_element_here = 0
			}
			////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			run_length_accumulator++; // = 1
			previous_pixel_colour = current_pixel_colour;


			for(int y = 1; y < height; y++) {

				colour = image.getRGB(x,y); // -1 == white. else black.
				if (colour == -1) {current_pixel_colour = Colour.WHITE;}
				else {current_pixel_colour = Colour.BLACK;}
				
				
				// to avoid final run length being lost ////////////////////////////////////////////////////////////////
				if(y == height - 1) {

					if (current_pixel_colour == previous_pixel_colour) {
						run_length_accumulator++;
					}
					else { // one run done - add accumulator value to the list
						rle_2d_array_x_y[x][inner_list_length_accumulator__insert_next_element_here] = run_length_accumulator;
						inner_list_length_accumulator__insert_next_element_here++;
						//rle_list.add(run_length_accumulator);
						previous_pixel_colour = current_pixel_colour;
						run_length_accumulator = 1;
					}

					//then add final value
					
					rle_2d_array_x_y[x][inner_list_length_accumulator__insert_next_element_here] = run_length_accumulator;
					inner_list_length_accumulator__insert_next_element_here++;

				}
				////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				else if (current_pixel_colour == previous_pixel_colour) { // same colour - increment current run length counter
					run_length_accumulator++;
				}
				else { // one run done - add accumulator value to the list
					rle_2d_array_x_y[x][inner_list_length_accumulator__insert_next_element_here] = run_length_accumulator;
					inner_list_length_accumulator__insert_next_element_here++;
					//rle_list.add(run_length_accumulator);
					previous_pixel_colour = current_pixel_colour;
					run_length_accumulator = 1;
				}

			}
		}

		//now remove trailing 0's from rle_2d_array_x_y
		int[][] rle_0s_removed = Run_Length_Encoding.removeInnerArrayTrailing0s(rle_2d_array_x_y);
		return rle_0s_removed;
	}

	
	public static int[][] RLE_2D_along_x_axis(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		
		int[][] rle_2d_array_y_x = new int[height][width+1]; 
		
		for(int y = 0; y< height; y++) {
			
			Colour previous_pixel_colour;
			int inner_list_length_accumulator__insert_next_element_here = 0;
			int run_length_accumulator = 0;
			
			
			// first pixel treated differently as there's no 'previous_pixel' //////////////////////////////////////////
			
			int colour = image.getRGB(0,y); // -1 == white. else black.
			Colour current_pixel_colour;
			if (colour == -1) {current_pixel_colour = Colour.WHITE;}
			else {current_pixel_colour = Colour.BLACK;}
			
			
			if (current_pixel_colour == Colour.WHITE) {
				rle_2d_array_y_x[y][inner_list_length_accumulator__insert_next_element_here] = 0; // i.e. 0 black pixels
																	// first (convention - first run length is black)
				inner_list_length_accumulator__insert_next_element_here++; // = 1
			}
			else { // BLACK
				// inner_list_length_accumulator__insert_next_element_here = 0
			}
			////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			run_length_accumulator++; // = 1
			previous_pixel_colour = current_pixel_colour;


			for(int x = 1; x < width; x++) {

				colour = image.getRGB(x,y); // -1 == white. else black.
				if (colour == -1) {current_pixel_colour = Colour.WHITE;}
				else {current_pixel_colour = Colour.BLACK;}
				
				
				// to avoid final run length being lost ////////////////////////////////////////////////////////////////
				if(x == width - 1) {

					if (current_pixel_colour == previous_pixel_colour) {
						run_length_accumulator++;
					}
					else { // one run done - add accumulator value to the list
						rle_2d_array_y_x[y][inner_list_length_accumulator__insert_next_element_here] = run_length_accumulator;
						inner_list_length_accumulator__insert_next_element_here++;
						//rle_list.add(run_length_accumulator);
						previous_pixel_colour = current_pixel_colour;
						run_length_accumulator = 1;
					}

					//then add final value
					
					rle_2d_array_y_x[y][inner_list_length_accumulator__insert_next_element_here] = run_length_accumulator;
					inner_list_length_accumulator__insert_next_element_here++;

				}
				////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				else if (current_pixel_colour == previous_pixel_colour) { // same colour - increment current run length counter
					run_length_accumulator++;
				}
				else { // one run done - add accumulator value to the list
					rle_2d_array_y_x[y][inner_list_length_accumulator__insert_next_element_here] = run_length_accumulator;
					inner_list_length_accumulator__insert_next_element_here++;
					//rle_list.add(run_length_accumulator);
					previous_pixel_colour = current_pixel_colour;
					run_length_accumulator = 1;
				}

			}
		}

		//now remove trailing 0's from rle_2d_array_y_x
		int[][] rle_0s_removed = Run_Length_Encoding.removeInnerArrayTrailing0s(rle_2d_array_y_x);
		return rle_0s_removed;
	}

	
	

}
