package omr.symbol_recogntion.segmentation;

import java.awt.image.BufferedImage;

import omr.util.Colour;
import omr.util.Projection;
import omr.util.Run_Length_Encoding;

public class Note_Stem_Detection {

	private BufferedImage image;
	private int width;
	private int height;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public Note_Stem_Detection(BufferedImage img) {
		image = img;
		width = image.getWidth();
		height = image.getHeight();
		
	}
	
	public boolean detect() {
		boolean note_stem_present = false;
		
		// generate new BufferedImage just of note stems
		BufferedImage noteStems = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		
		// generate new BufferedImage just everything but note stems
		BufferedImage everything_but_noteStems = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		
		
		//do run length encoding along y axis
		Run_Length_Encoding rle = new Run_Length_Encoding(image);
		int[][] rle_y = rle.RLE_2D_along_y_axis();
		
		for(int x = 0; x < rle_y.length; x++) {
			int y_actual = 0;

			for(int j = 0; j < rle_y[x].length; j++) {
				// eliminate black runs < 2/3 * note_head.height
				int run_length = rle_y[x][j];
				Colour run_colour = Run_Length_Encoding.whatColourIsRun(x,j);
					
				if(run_colour == Colour.BLACK 
						&& (noteStem_height_min <= run_length) 
						&& (run_length <= noteStem_height_max)) {
					// note stem candidate -> add to image of note stems
					for (int y = y_actual; y < y_actual + run_length; y++) { 
						// from 'score_minus_staves' and add them to 'staves'
						noteStems.setRGB(x, j, 0); //black
					}
				}

				y_actual = y_actual + run_length;
			}
		}	
		
		//do x projection of noteStems -> will increase algorithms resiliance to imperfectly vertical stems (due to 
		//skew.
		
		Projection xproj_of_noteStems = new Projection(noteStems);
		
		
		
		return note_stem_present;
		
	}
}
