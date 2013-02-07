package omr.util;

import java.awt.image.BufferedImage;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int width = 300;
		int height = 100;
		
		BufferedImage noteStems = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		System.out.println("noteStems image created - (width, height) (" + width + "," + height + ")" );
		
		for(int x = 0; x < noteStems.getWidth(); x++) {
			for(int y = 0; y < noteStems.getHeight(); y++) {
				noteStems.setRGB(x, y, -1); //white
			}
		}
		
		
		DisplayImage di1 = new DisplayImage(noteStems, "should be white");
		di1.display();
	}

}
