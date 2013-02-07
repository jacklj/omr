package omr.symbol_recogntion.classifier.svm;

import java.awt.Color;
import java.awt.image.BufferedImage;

import omr.util.ImageProcessing;

import libsvm.svm_node;

public class Feature_Extraction_for_LIBSVM {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BufferedImage symbol = ImageProcessing.loadImage("/Users/buster/Stuff/Academia/II/DISSERTATION/symbol_sets/sets/basicSymbolSet/20_20/1.treble_clef/1.treble_clef_resized_resized.png");
		svm_node[] dataPoint = extract(symbol);
		
	}
	
	public static svm_node[] extract(BufferedImage image) {
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
		
		// the first feature (top left pixel) is numbered 1 in libSVM training text file
		svm_node[] dataPoint= new svm_node[maxNumberOfFeatures];
		
		int nodeCount = 0;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(image.getRGB(x, y) == Color.WHITE.getRGB()) {
					// 0 -> ignore, as LibSVm uses a sparse data format 
				} else if (image.getRGB(x, y) == Color.BLACK.getRGB()) {
					svm_node node = new svm_node();
					
					node.index = x*height + y+1; // why +1? because in libSVM datapoint format, the first feature is numbered 1 (not 0)
					node.value = 1; // because pixel is white
					
					dataPoint[nodeCount] = node;
					nodeCount++;
					
				} else {
					System.err.println("ERROR: Image not binary");
					return null;
				}
			}
		}
		
		// cut svm_node[] array down to correct length
		svm_node[] cutDown = new svm_node[nodeCount]; // not nodeCount + 1 because nodeCount is incremented after the 
													  // final dataPoint[] assignment
		for(int i = 0; i < nodeCount; i ++) {
			cutDown[i] = dataPoint[i];
		}
		dataPoint = cutDown;
		
		
		//print data
//		System.out.println();
//		for(int i = 0; i < dataPoint.length; i++) {
//			System.out.print(dataPoint[i].index + ":" + dataPoint[i].value + " ");
//		}
		
		return dataPoint;
		
	}

}
