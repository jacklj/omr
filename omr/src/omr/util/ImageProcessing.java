package omr.util;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.LookupOp;
import java.awt.image.ShortLookupTable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageProcessing {

	/**
	 * @param args
	 */
	
	// for testing /////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
		
		BufferedImage img = ImageProcessing.loadImage(
				"/Users/buster/Stuff/Academia/II/DISSERTATION/test_images/beamedNoteStave.png");
		String outputTopLevelDirPath = "/Users/buster/dissertation_Evaluation_output/";
		String outputPathNext = "/Users/buster/libraries/GAMERA/dataSets/";
		
		String outputDirPath = outputTopLevelDirPath + outputPathNext;
		
		ImageProcessing.saveImage(img, outputDirPath, "blah" + "_just_symbols.png");
//		System.out.println("Input image type = " + ImageProcessing.getImageTypeString(img));
//		BufferedImage processed = ImageProcessing.threshold(ImageProcessing.convertToGrayscale(img), 0.9);
//		
//		System.out.println("Processed image type = " + ImageProcessing.getImageTypeString(processed));
//		
//		for(int x = 0; x < processed.getWidth(); x++) {
//			for (int y  = 0; y < processed.getHeight(); y++) {
//				System.out.println(processed.getRGB(x, y));
//			}
//		}
		
//		BufferedImage img = ImageProcessing.loadImage(
//		"/Users/buster/Stuff/Academia/II/DISSERTATION/test_images/beamedNoteStave.png");
//		ImageProcessing.displayImage(img, "ORIGINAL - colourspace: " + ImageProcessing.getImageTypeString(img));
//
//		
//		BufferedImage greyImg = ImageProcessing.convertToGrayscale(img);
//		ImageProcessing.displayImage(greyImg, "GREY- colourspace: " + ImageProcessing.getImageTypeString(greyImg));
//		
//		BufferedImage resizedThresholded = ImageProcessing.threshold(greyImg, 0.9);
//		ImageProcessing.displayImage(resizedThresholded, "BINARISED - colourspace: " + 
//				ImageProcessing.getImageTypeString(resizedThresholded));

	
//		BufferedImage thresholdedImage = ImageProcessing.threshold(greyImg, 0.5);
//		ImageProcessing.displayImage(thresholdedImage, "BINARY 2by2 - colourspace: " + 
//				ImageProcessing.getImageTypeString(thresholdedImage));
//		
//		
//		BufferedImage resized = ImageProcessing.resizeImage(img, 100, 100);
//		ImageProcessing.displayImage(resized, "resized original image " + ImageProcessing.getImageTypeString(resized));
//		//saveImage(resized, "resized3.png");
//		
//		BufferedImage resizedgreyImg = ImageProcessing.resizeImage(greyImg, 100, 100);
//		ImageProcessing.displayImage(resizedgreyImg, "resized GREY  - colourspace: " + 
//				ImageProcessing.getImageTypeString(resizedgreyImg));
//
//		
//		BufferedImage resizedThresholded = ImageProcessing.resizeImage(thresholdedImage, 100, 100);
//		ImageProcessing.displayImage(resizedThresholded, "BINARY 2by2 - colourspace: " + 
//				ImageProcessing.getImageTypeString(resizedThresholded));

//		BufferedImage img = ImageProcessing.loadImage(
//				"/Users/buster/Stuff/Academia/II/DISSERTATION/test_images/beamedNoteStave.png");
//		saveImage(img, "/Users/buster/zzz/one/", "test.png");
//		saveImage(img, "/Users/buster/zzz/one/test.png");
		
		
//		BufferedImage img = ImageProcessing.loadImage(
//				"/Users/buster/Stuff/Academia/II/DISSERTATION/testOut/Stave1/l0_Segment1/l01.png");
//		ImageProcessing.displayImage(img, "input image");
////		displayImage(resizeImage(preprocess(img), 30, 30), "resized");
//		BufferedImage resized = resizeSymbol(img, 30);
//		
//		displayImage(resized, "symbol resized");
//		ImageProcessing.saveImage(img, "original123.png");
//		ImageProcessing.saveImage(resized, "resized123_2.png");
	}

	
	
	
	// file IO /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static BufferedImage loadImage(String filePath) {
		BufferedImage img = null;
		try {img = ImageIO.read(new File(filePath));} 
		catch (FileNotFoundException e) {System.out.println("File not found exception");} 
		catch (IOException e) {System.out.println("IO exception");}
		return img;
	}
	// INPUT: a 'String' containing a file path	// INPUT: a 'String' containing a file path
	// FUNCTIONALITY: tries to open the file pointed to as an image
	// OUTPUT: If it's an image,returns it as a BufferedImage object
	
	
	public static void saveImage(BufferedImage image, String filePath, String fileName) {
		File f = new File(filePath);
		if (f.exists()) {
			// File or directory exists - no need to create
		} else {
			// File or directory does not exist - create!
			System.out.println("Directory doesn't exist -> create");
			
			boolean success = f.mkdirs();
			if (!success) {
				System.out.println("Directory not created.");
			}
		}
		
		saveImage(image, filePath + fileName);
	}
	// INPUT: BufferedImage image, directory path as 'String', file name as 'String'
	// FUNCTIONALITY: saves the image as png at location filePath + fileName
	// OUTPUT: none
	
	
	public static void saveImage(BufferedImage image, String fileName) {
		File outputfile = new File(fileName);
		try {  
			ImageIO.write(image, "png", outputfile);
			System.out.println("Saving image in " + fileName);
//			displayImage(image, "keep printing out??");
		} catch (IOException e) {
			System.out.println("ERROR: Probably not a valid filepath");
		}
	}
	// INPUT: BufferedImage image, file name (with full path) as 'String'
	// FUNCTIONALITY: saves the image as png at location specified in fileName
	// OUTPUT: none
	
	
	
	
	// misc ////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void displayImage (BufferedImage image, String figureName) {
		DisplayImage di = new DisplayImage(image, figureName);
		di.display();
	}
	// INPUT: BufferedImage image, figure name as 'String'
	// FUNCTIONALITY: Displays simple GUI containing image
	// OUTPUT: none

	
	public static String getImageTypeString(BufferedImage image) {
		int type = image.getType();
		String typeName;

		switch (type) {
		case BufferedImage.TYPE_3BYTE_BGR:  typeName = "TYPE_3BYTE_BGR";
		break;
		case BufferedImage.TYPE_4BYTE_ABGR:  typeName = "TYPE_4BYTE_ABGR";
		break;
		case BufferedImage.TYPE_4BYTE_ABGR_PRE:  typeName = "TYPE_4BYTE_ABGR_PRE";
		break;
		case BufferedImage.TYPE_BYTE_BINARY:  typeName = "TYPE_BYTE_BINARY";
		break;
		case BufferedImage.TYPE_BYTE_GRAY:  typeName = "TYPE_BYTE_GRAY";
		break;
		case BufferedImage.TYPE_BYTE_INDEXED:  typeName = "TYPE_BYTE_INDEXED";
		break;
		case BufferedImage.TYPE_CUSTOM:  typeName = "TYPE_CUSTOM";
		break;
		case BufferedImage.TYPE_INT_ARGB:  typeName = "TYPE_INT_ARGB";
		break;
		case BufferedImage.TYPE_INT_ARGB_PRE:  typeName = "TYPE_INT_ARGB_PRE";
		break;
		case BufferedImage.TYPE_INT_BGR:  typeName = "TYPE_INT_BGR";
		break;
		case BufferedImage.TYPE_INT_RGB:  typeName = "TYPE_INT_RGB";
		break;
		case BufferedImage.TYPE_USHORT_555_RGB:  typeName = "TYPE_USHORT_555_RGB";
		break;
		case BufferedImage.TYPE_USHORT_565_RGB:  typeName = "TYPE_USHORT_565_RGB";
		break;
		case BufferedImage.TYPE_USHORT_GRAY:  typeName = "TYPE_USHORT_GRAY";
		break;
		default: typeName = "Invalid colour space";
		break;
		}

		return typeName;
	}
	// INPUT: BufferedImage image
	// FUNCTIONALITY: Figures out what type of BufferedImage image it is
	// OUTPUT: Returns the answer as a String
	
	
	public static void makeImageWhite(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				image.setRGB(x, y, Color.WHITE.getRGB());
			}
		}
	}
	// INPUT: (reference to) BufferedImage image
	// FUNCTIONALITY: paints the image all white
	// OUTPUT: none
	
	
	
	
	// copy image //////////////////////////////////////////////////////////////////////////////////////////////////////
	public static BufferedImage copyImage(BufferedImage source) {
		int width = source.getWidth();
		int height = source.getHeight();
		BufferedImage newImg = new BufferedImage(width, height, source.getType());
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				newImg.setRGB(x,y, source.getRGB(x,y));
			}
		}
		return newImg;
		
	}
	
	
	public static void copyImage(BufferedImage source, BufferedImage destination) {
		int width = source.getWidth();
		int height = source.getHeight();
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				destination.setRGB(x,y, source.getRGB(x,y));
			}
		}
		
	}	
	
	
	
	
	// resizing ////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static BufferedImage resizeImage (BufferedImage originalImage, int scaledWidth, int scaledHeight) {
		
		// don't preserve alpha channel
		//, 	boolean preserveAlpha
//		int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		int imageType = originalImage.getType(); //BufferedImage.TYPE_INT_RGB;

		BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, imageType);
		
		java.awt.Graphics2D g = scaledImage.createGraphics();
//		if (preserveAlpha) {
//			g.setComposite(AlphaComposite.Src);
//		}
		
		//control quality of scaled copy
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR); //RenderingHints.VALUE_INTERPOLATION_[NEAREST_NEIGHBOR BILINEAR BICUBIC]
		
		
		g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null); 
		g.dispose();
		return scaledImage;
	}
	
	
	public static BufferedImage resizeImage (BufferedImage originalImage, int scaledWidth, int scaledHeight, 
			Object interpolationType) {
		
		// don't preserve alpha channel
		//, 	boolean preserveAlpha
//		int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		int imageType = originalImage.getType(); //BufferedImage.TYPE_INT_RGB;

		BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, imageType);
		
		java.awt.Graphics2D g = scaledImage.createGraphics();
//		if (preserveAlpha) {
//			g.setComposite(AlphaComposite.Src);
//		}
		
		//control quality of scaled copy
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				interpolationType); //RenderingHints.VALUE_INTERPOLATION_[NEAREST_NEIGHBOR BILINEAR BICUBIC]
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null); 
		g.dispose();
		return scaledImage;
	}
	
	
	public static BufferedImage resizeSymbol(BufferedImage inputSymbol, int boxSize) { //, int interpolationMethod) {
		// input - image of just symbol (no padding)
		
		int sourceWidth = inputSymbol.getWidth();
		int sourceHeight = inputSymbol.getHeight();
		
		//System.out.println("input image width: " + sourceWidth + "\theight: " + sourceHeight);
		
		// 1) resize symbol keeping image aspect ratio
		int partiallyResized_width;
		int partiallyResized_height;
		
		// which dimension is bigger
		if(sourceWidth > sourceHeight) {
			partiallyResized_width = boxSize;
			
			// calculate scale factor
			double scaleFactor = (double)((double)boxSize / (double)sourceWidth);
			//System.out.println("Scalefactor: " + scaleFactor);
			
			
			int partiallyResized_height_provisional = (int)((double)sourceHeight * (double)scaleFactor);
			partiallyResized_height = partiallyResized_height_provisional > 0 ? partiallyResized_height_provisional : 1;

			
		} else if (sourceWidth < sourceHeight) {
			partiallyResized_height = boxSize;
			
			// calculate scale factor
			double scaleFactor = (double)((double)boxSize / (double)sourceHeight);
			//System.out.println("Scalefactor: " + scaleFactor);

			int partiallyResized_width_provisional = (int)(sourceWidth * scaleFactor);
			partiallyResized_width = partiallyResized_width_provisional > 0 ? partiallyResized_width_provisional : 1;

			
		} else {
			// they're equal
			partiallyResized_width = boxSize;
			partiallyResized_height = boxSize;
		}
		
		
		
		BufferedImage partiallyResized = ImageProcessing.resizeImage(inputSymbol, partiallyResized_width, partiallyResized_height, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		
		// 2) now add background padding around symbol along shorter dimension to make final image square
		int final_width = boxSize;
		int final_height = boxSize;
		
		int xshift_possible = Math.round((final_width - partiallyResized_width)/2);
        int xshift = xshift_possible > 0 ? xshift_possible : 0;
        int yshift = Math.round((final_height - partiallyResized_height)/2);
		
//        System.out.println("partially resized: width:" + partiallyResized_width + " height:" + partiallyResized_height);
//        System.out.println("xshift: " + xshift);
//        System.out.println("yshift: " + yshift);
        
        
		BufferedImage outputImage = new BufferedImage(boxSize, boxSize, BufferedImage.TYPE_BYTE_GRAY);
		ImageProcessing.makeImageWhite(outputImage);
		
		
		for(int x = 0; x < partiallyResized_width; x++) {
			int xnew = x + xshift;
			for(int y = 0; y < partiallyResized_height; y++) {
				int ynew = y + yshift;
				
				outputImage.setRGB(xnew, ynew, partiallyResized.getRGB(x,y));
			}
		}
//        newImage( (yshift:(yshift + partially_resized_height - 1)), (xshift:(xshift + partially_resized_width - 1)) ) = partially_resized;

		
		
		return outputImage;
	}
	
	
	public static BufferedImage resizeImageMaintainingAspectRatio(BufferedImage source, int box_dimension) {
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();
		
		int outputWidth;
		int outputHeight;
		
		// which dimension is bigger
		if(sourceWidth > sourceHeight) {
			outputWidth = box_dimension;
			// calculate scale factor
			float scaleFactor = box_dimension / outputWidth;
			outputHeight = (int)(sourceHeight * scaleFactor);
			
			// pad with white space
			
		} else if (sourceWidth < sourceHeight) {
			outputHeight = box_dimension;
			// calculate scale factor
			float scaleFactor = box_dimension / outputHeight;
			outputWidth = (int)(sourceWidth * scaleFactor);
			
			//pad with white space
			
		} else {
			// they're equal
			outputWidth = box_dimension;
			outputHeight = box_dimension;
		}
		
		return ImageProcessing.resizeImage(source, outputWidth, outputHeight);
	}
	
	

	
	// covert image to greyscale ///////////////////////////////////////////////////////////////////////////////////////
	public static BufferedImage convertToGrayscale(BufferedImage img) {
		
		if(img.getType() == BufferedImage.TYPE_BYTE_GRAY) {
			System.out.println("Image already greyscale");
			return img;
		}
		
		BufferedImage grey = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

		ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		colorConvert.filter(img, grey);

		return grey;
	}



	
    // threshold ///////////////////////////////////////////////////////////////////////////////////////////////////////
	public static BufferedImage threshold(BufferedImage sourceImage, double d) {
		if((0 > d) || (d > 1) ) {
			System.out.println("[ERROR] Threshold percentage not in the range 0<= thresholdPercentage <= 1");
			return null;
		}
		
		if(sourceImage.getType() != BufferedImage.TYPE_BYTE_GRAY) {
			System.out.println("[ERROR] Input image to threshold is not greyscale... convert first.");
			return null;
		}
		// therefore image is greyscale
		
		int threshold = (int) (256*d);
		short[] thresholdLookup = new short[256];
		
		for (int i = 0; i < thresholdLookup.length; i++) {
			thresholdLookup[i] = (i < threshold) ? (short)Color.BLACK.getRGB() : (short)Color.WHITE.getRGB();
		}
		
		BufferedImageOp thresholdOp = new LookupOp(new ShortLookupTable(0, thresholdLookup), null);
		
		BufferedImage outputImage = new BufferedImage(sourceImage.getWidth(),sourceImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY); 
			
		thresholdOp.filter(sourceImage, outputImage);
		
		return outputImage;
	}




    // preprocess -> greyscale then binarise ///////////////////////////////////////////////////////////////////////////
	public static BufferedImage preprocess(BufferedImage inputImage) {
		// we want image to be binary
		
		//1) greyscale?
		BufferedImage greyScale = ImageProcessing.convertToGrayscale(inputImage);
		
		//2) binary
		BufferedImage binaryImage = ImageProcessing.threshold(greyScale, 0.9);
		
		
		return binaryImage;
	}
	
	
	
	
	// converting between int/logical 2d array image representations and bufferedImages ////////////////////////////////
	private static List<Integer> getUniqueColours(int amount) {
	    final int lowerLimit = 0x10;
	    final int upperLimit = 0xE0;    
	    final int colourStep = (int) ((upperLimit-lowerLimit)/Math.pow(amount,1f/3));

	    final List<Integer> colours = new ArrayList<Integer>(amount);

	    for (int R = lowerLimit;R < upperLimit; R+=colourStep)
	        for (int G = lowerLimit;G < upperLimit; G+=colourStep)
	            for (int B = lowerLimit;B < upperLimit; B+=colourStep) {
	                if (colours.size() >= amount) { //The calculated step is not very precise, so this safeguard is appropriate
	                    return colours;
	                } else {
	                    int colour = (R<<16)+(G<<8)+(B);
	                    colours.add(colour);
	                }               
	            }
	    return colours;
	}
	
	
	public static int[][] convertBinaryImageTo_2dIntArray(BufferedImage image) {
		int[][] arrayIm = new int[image.getHeight()][image.getWidth()];
		for(int y = 0; y < image.getHeight(); y++) {
			for(int x = 0; x < image.getWidth(); x++) {
				if(image.getRGB(x, y) == Color.BLACK.getRGB()) {
					arrayIm[y][x] = 1; // black, foreground pixel
				} // else white. background, = 0
			}
		}
		return arrayIm;
	}
	
	
	public static boolean[][] convertBinaryImageTo_LogicalArray(BufferedImage image) {
		boolean[][] arrayIm = new boolean[image.getHeight()][image.getWidth()];
		for(int y = 0; y < image.getHeight(); y++) {
			for(int x = 0; x < image.getWidth(); x++) {
				if(image.getRGB(x, y) == Color.BLACK.getRGB()) {
					arrayIm[y][x] = true; // black, foreground pixel
				} else  { // white, background, = false
					arrayIm[y][x] = false;
				}
			}
		}
		return arrayIm;
	}
	
	
	public static BufferedImage convert_2dIntArray_toBinaryImage(int[][] intImage) {
		int height = intImage.length;
		int width = intImage[0].length;
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				if(intImage[y][x] == 1) {
					
					image.setRGB(x, y, Color.BLACK.getRGB()); // black, foreground pixel
				} else {
					image.setRGB(x, y, Color.WHITE.getRGB()); // white. background
				}
			}
		}
		return image;
	}
	
	
	public static BufferedImage convert_2dLogicalArray_toBinaryImage(boolean[][] logImage) {
		int height = logImage.length;
		int width = logImage[0].length;
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				if(logImage[y][x] == true) {
					
					image.setRGB(x, y, Color.BLACK.getRGB()); // black, foreground pixel
				} else {
					image.setRGB(x, y, Color.WHITE.getRGB()); // white. background
				}
			}
		}
		return image;
	}
}


