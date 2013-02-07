package omr.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class WriteImageToFile {

	public static void write(BufferedImage image, String filepath) {
		
		File file = new File(filepath);
		try {  
			ImageIO.write(image, "png", file);		
		} catch (IOException e) {

		}
	}
}
