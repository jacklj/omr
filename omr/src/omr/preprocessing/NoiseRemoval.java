package omr.preprocessing;

import java.awt.Color;
import java.awt.image.BufferedImage;

import omr.util.Colour;
import omr.util.ImageProcessing;
import omr.util.Run_Length_Encoding;

public class NoiseRemoval {

	public static BufferedImage runLengthSmoothing(BufferedImage inputImage) {
		BufferedImage smoothedImage= ImageProcessing.copyImage(inputImage);

		int smoothingParameter = 1;

		//HORIZONTAL rls
		Run_Length_Encoding rle1 = new Run_Length_Encoding(inputImage);
		int[][] rle_x1 = rle1.RLE_2D_along_x_axis();

		for(int y = 0; y < rle_x1.length; y++) {
			int x_actual = 0;

			for (int j = 0; j < rle_x1[y].length; j++) {
				int run_length = rle_x1[y][j];
				Colour run_colour = Run_Length_Encoding.whatColourIsRun(y,j); // makes code easier to read

				if(run_length <= smoothingParameter) {
					if(run_colour == Colour.WHITE) {
						for (int x = x_actual; x < x_actual + run_length; x++) {
							smoothedImage.setRGB(x, y, 0); //black
						}
					} else {
						for (int x = x_actual; x < x_actual + run_length; x++) {
							smoothedImage.setRGB(x, y, Color.WHITE.getRGB()); //black
						}
					}
				}
				x_actual = x_actual + run_length;
			}
		}
		
		//VERTICAL rls
		int[][] rle_y1 = rle1.RLE_2D_along_x_axis();

		for(int y = 0; y < rle_y1.length; y++) {
			int x_actual = 0;

			for (int j = 0; j < rle_y1[y].length; j++) {
				int run_length = rle_y1[y][j];
				Colour run_colour = Run_Length_Encoding.whatColourIsRun(y,j); // makes code easier to read

				if(run_length <= smoothingParameter) {
					if(run_colour == Colour.WHITE) {
						for (int x = x_actual; x < x_actual + run_length; x++) {
							smoothedImage.setRGB(x, y, 0); //black
						}
					} else {
						for (int x = x_actual; x < x_actual + run_length; x++) {
							smoothedImage.setRGB(x, y, Color.WHITE.getRGB()); //black
						}
					}
				}
				x_actual = x_actual + run_length;
			}
		}

		return smoothedImage;
	}

}
