package omr.symbol_recogntion.classifier;

import java.awt.Color;
import java.awt.Image;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;


public class ImageNormaliser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String file_where_to_save_symbol_images = "//Users//buster//testImages//";
//		String filePath = "//Users//buster//Stuff//Academia//II//DISSERTATION//symbol_sets//other//sibelius//pngs//without_staves//singleLines//helsinki1.png";
//		String filePath = "//Users//buster//Stuff//Academia//II//DISSERTATION//test_images//ich_grolle_nicht_small.png";
//		String filePath = "//Users//buster//Stuff//Academia//II//DISSERTATION//test_images//ich_grolle_nicht_small.png";
		String filePath = "/Users/buster/libraries/OpenOMR/neuralnetwork/training/bass/bass1.png";
		
		BufferedImage buffImage = null;
		try {
			buffImage = ImageIO.read(new File(filePath));
		} catch (FileNotFoundException e) {
			System.out.println("File not found exception");
		} catch (IOException e) {
			System.out.println("IO exception");
		}
		
		  final int scaleWidth = 8;
		  final int scaleHeight = 16;
		  int totalPixels = scaleWidth * scaleHeight;
		
		Image scaledInstance = buffImage.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_DEFAULT);
		
		JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(scaledInstance)));
		
		
		
		double inputs[] = new double[totalPixels];
		
		
		PixelGrabber pixGrabber = new PixelGrabber(scaledInstance, 0, 0, scaleWidth, scaleHeight, true);
		try
		{
			pixGrabber.grabPixels();
			int pixArray[];
			pixArray = (int[]) pixGrabber.getPixels();
			for (int x = 0; x < totalPixels; x += 1)
			{
				Color col = new Color(pixArray[x]);
				float[] hsb = Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);
				inputs[x] = hsb[2]; //get luminosity
				System.out.print(hsb[2] + ' ');
			}
		} catch (InterruptedException e)
		{
			System.out.println("Error grabbing pixel");
			e.printStackTrace();
		}
		

		
	}
	
	
	public static BufferedImage normalise(BufferedImage image_to_normalise) {
		
		return image_to_normalise;
		
	}

	
	
}
