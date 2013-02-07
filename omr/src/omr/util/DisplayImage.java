package omr.util;

import java.awt.Graphics;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import omr.symbol_recogntion.score_metrics.ScoreMetrics;
import omr.symbol_recogntion.segmentation.L0_Segmentation;


public class DisplayImage extends Panel {
	
	public static void main(String[] args) {
		
		BufferedImage img1 = ImageProcessing.loadImage("//Users//buster//Stuff//Academia//II//DISSERTATION//test_images//ich_grolle_nicht_small.png");
		
		DisplayImage di = new DisplayImage(img1, "test image");
		di.display();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private BufferedImage  image;
	private int img_width;
	private int img_height;
	private String figureTitle;

	
	public DisplayImage(BufferedImage image, String FigureTitle) {

		this.image = image;
		this.figureTitle = FigureTitle;
		
		img_width = image.getWidth();
		img_height = image.getHeight();

	}

	
	public void paint(Graphics g) {
		
		
		g.drawImage(image,0,0,null);
	}
	
	
	public void display() {
		JFrame frame = new JFrame(figureTitle);
		frame.getContentPane().add(this);
		frame.setSize(img_width + 10, img_height+26); // leaves room for OS window GUI stuff
		frame.setVisible(true);
	}
}