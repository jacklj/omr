package omr.symbol_recogntion.classifier.ann;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.encog.ml.data.basic.BasicMLData;

import omr.util.ImageProcessing;

import libsvm.svm_node;

public class Feature_Extraction_for_Encog {


	public static void main(String[] args) {
		BufferedImage symbol = ImageProcessing.loadImage("/Users/buster/Stuff/Academia/II/DISSERTATION/symbol_sets/sets/basicSymbolSet/20_20/1.treble_clef/1.treble_clef_resized_resized.png");
		BasicMLData dataPoint = extract(symbol);
		
		//print data
		System.out.println();
		for(int i = 0; i < dataPoint.size(); i++) {
			System.out.print(i + ":" + dataPoint.getData(i) + " ");
		}
		
	}
	
	public static BasicMLData extract(BufferedImage image) {
		
		// input - binary image
		int width = image.getWidth();
		int height = image.getHeight();
		
		int noOfPixels = width * height;
		int maxNumberOfFeatures = noOfPixels;
		
		// output note -> training data (generated in matlab) is created
		// by iterating through the image down each column, from left to 
		// right, like so:
		
		// | |
		// | |  . . . > 
		// v v
		
//		double[] doubleData = new double[maxNumberOfFeatures];
		BasicMLData data = new BasicMLData(maxNumberOfFeatures);
		//svm_node[] dataPoint= new svm_node[maxNumberOfFeatures];
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(image.getRGB(x, y) == Color.WHITE.getRGB()) {
					// 0 -> ignore, as doubleData already initialised to all 0's
				} else if (image.getRGB(x, y) == Color.BLACK.getRGB()) {
					
					
					int index = x*height + y; // ranges from 0 - 399
					double value = 1.0; // because pixel is black
					
					data.setData(index, value);
					
				} else {
					System.err.println("ERROR: Image not binary");
					return null;
				}
			}
		}
		
		
//		//print data
//		System.out.println();
//		for(int i = 0; i < data.size(); i++) {
//			System.out.print(i + ":" + data.getData(i) + " ");
//		}
//		
		return data;
		
	}

}
