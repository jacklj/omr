package omr.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConnectedComponentAnalysis {
	
	// binary image input represented as 2d int array
	
	
	public static void main(String[] args) {
		
//		 test on int[][] ///////
//		int[][] image = { // 7*8 (rows*columns)
//				{ 1, 1, 1, 1, 1, 1, 0, 1 },
//				{ 1, 1, 1, 1, 1, 1, 0, 1 },
//				{ 1, 1, 1, 1, 1, 0, 0, 1 },
//				{ 0, 0, 1, 1, 1, 0, 0, 1 },
//				{ 1, 1, 1, 1, 0, 1, 0, 1 },
//				{ 0, 0, 0, 1, 0, 1, 0, 1 },
//				{ 1, 1, 0, 1, 0, 0, 0, 1 }
//		};
//		BufferedImage img = ImageProcessing.convert_2dIntArray_toBinaryImage(image);
		
		BufferedImage img = ImageProcessing.loadImage("/Users/buster/Stuff/Academia/II/DISSERTATION/test_images/ccTest.png"); ///Users/buster/Stuff/Academia/II/DISSERTATION/evaluation/stave removal eval/test2/step2_justStaves.png");
		
		BufferedImage newIm = ImageProcessing.copyImage(img);
		ImageProcessing.makeImageWhite(newIm);
		BufferedImage newIm2 = ImageProcessing.copyImage(img);
		ImageProcessing.makeImageWhite(newIm2);
		
		ConnectedComponentAnalysis cca = new ConnectedComponentAnalysis(img, ConnectedComponentAnalysis.CONNECTIVITY_8);
		int [][] labelledIm = cca.getLabelledImage();
//		System.out.println("Labelled Image:");
//		printIm(labelledIm);
		
		System.out.println("Number of components: " + cca.getNumberOfComponents());
//		
		
		
		for(ConnectedComponent cc : cca.getAllConnectedComponents()) {
			System.out.println("CC (label = " + cc.getLabel() + ")");
			System.out.println("Xl,Yt " + cc.getStartXcoord() + "," + cc.getStartYcoord() + "\t Xr,Yb " + cc.getRightXcoord() + "," + cc.getBottomYcoord());
			ImageProcessing.displayImage(cc.getImage(), "connected component " + cc.getLabel());
			if(cc.getLabel() == 1) {
				cc.paintIntoImage(newIm, Color.RED);
			ImageProcessing.displayImage(newIm, "Painted in " + cc.getLabel());
			} else {
				cc.paintIntoImage(newIm2, Color.RED);
				ImageProcessing.displayImage(newIm2, "Painted in " + cc.getLabel());
			}
			
			//			printIm(cc.getIntImage());
		}
		
////		printIm(labelledIm);
//		// describe binary image as 2d int array
//		
//		ConnectedComponent one = cca.getConnectedComponent(6);
//		printIm(one.getImage());
//		System.out.println("startx = " + one.getStartXcoord() + "\tstarty: "  + one.getStartYcoord());
		
		// test on bufferedImage
//		BufferedImage image = ImageProcessing.loadImage("/Users/buster/eclipseWorkspace/omr/just_staves_after_step3.png");///Users/buster/Stuff/Academia/II/DISSERTATION/test_images/minus_staves/ich_grolle_nicht_small.png");
//		
//		int[][] image = {
//				{ 0, 0, 0, 0, 0 },
//				{ 0, 1, 0, 1, 0 },
//				{ 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0 },
//				{ 0, 1, 0, 1, 0 } 
//		};
		
//		int[][] image = {
//				{1, 0, 1}
//		};
		
//		ImageProcessing.displayImage(image, "cc test 1");
//		image = ImageProcessing.preprocess(image);
//		ImageProcessing.displayImage(image, "cc test 1 thresholded");

//		ConnectedComponentAnalysis ccA = new ConnectedComponentAnalysis(image,ConnectedComponentAnalysis.CONNECTIVITY_8 , 4);
//		System.out.println("Number of components: " + ccA.getNumberOfComponents());
		
//		for(ConnectedComponent cc : ccA.getAllConnectedComponents()) {
//			System.out.println("CC (label = " + cc.getLabel() + ")");
//			System.out.println("Xl,Yt " + cc.getStartXcoord() + "," + cc.getStartYcoord() + "\t Xr,Yb " + cc.getRightXcoord() + "," + cc.getBottomYcoord());
//			printIm(cc.getIntImage());
//		}
		
//		for(int i = )
		
//		printIm(ccA.labelledImage);
//		
//		for(int i = 1; i <= ccA.getNumberOfComponents(); i++) {
//			ConnectedComponent cc = ccA.getConnectedComponent(i);
//			System.out.println("COMPONENT " + i + ":");
//			
//			printIm(cc.getIntImage());
//			System.out.println("\twidth: " + cc.getWidth());
//			System.out.println("\theight: " + cc.getHeight());
//			System.out.println("\tleftX: " + cc.getStartXcoord());
//			System.out.println("\trightX: " + cc.getRightXcoord());
//			System.out.println("\ttopY: " + cc.getStartYcoord());
//			System.out.println("\tbottomY: " + cc.getBottomYcoord());
//
//			
//			
//			
//			ImageProcessing.displayImage(cc.getImage(), "cc " + i);
//		}
//		ccA.displayColouredImage();
	}

	
	
	public static class ConnectedComponent{
		private int[][] image;
		private int  width;
		private int  height;
		
		private int  startXcoord;
		private int  startYcoord;
		
		private int rightXcoord;
		private int bottomYcoord;
		
		private int labelInImage;
		
		
		
		// constructor /////////////////////////////////////////////////////////////////////////////////////////////////
		public ConnectedComponent(int[][] image, int xStartCoord, int yStartCoord) {
			this.image = image;
			this.width = image[0].length;
			this.height = image.length;
			
			this.startXcoord = xStartCoord;
			this.startYcoord = yStartCoord;
			
			this.rightXcoord = xStartCoord + this.width - 1;
			this.bottomYcoord = yStartCoord + this.height - 1;
			
			this.labelInImage = 1; //default
			
		}
		
		public ConnectedComponent(int[][] image, int xStartCoord, int yStartCoord, int labelInImage) {
			this.image = image;
			this.width = image[0].length;
			this.height = image.length;
			
			this.startXcoord = xStartCoord;
			this.startYcoord = yStartCoord;
			
			this.rightXcoord = xStartCoord + this.width - 1;
			this.bottomYcoord = yStartCoord + this.height - 1;
			
			this.labelInImage = labelInImage; 
			
		}
		

		// get set /////////////////////////////////////////////////////////////////////////////////////////////////////
		public int[][] getIntImage() {
			return this.image;
		}
		
		public BufferedImage getImage() {
			return ConnectedComponentAnalysis.convert_2dIntArray_toBinaryImage(this.image);
		}
		
		public int getWidth() {
			return this.width;
		}
		
		public int getHeight() {
			return this.height;
		}
		
		public int getStartXcoord() {
			return this.startXcoord;
		}
		
		public int getStartYcoord() {
			return this.startYcoord;
		}	
		
		public int getRightXcoord() {
			return this.rightXcoord;
		}
		
		public int getBottomYcoord() {
			return this.bottomYcoord;
		}
		
		public int getLabel() {
			return this.labelInImage;
		}
		
		public boolean getAbsPixel(int x, int y) {
			// coordinates are absolute
			int localX = x - this.startXcoord;
			int localY = y - this.startYcoord;
			
			if(image[localY][localX] == 1) {
				return true;
			} else {
				return false;
			}
		}
		
		public void paintIntoImage(BufferedImage image, Color colour) {
			paintComponentIntoImage(this, image, colour);
		}
	}
	
	// state ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final int CONNECTIVITY_4 = 1;
	public static final int CONNECTIVITY_8 = 2;
	
	private final int ConnectivityType;
	
	private final int[][] inputImage;
	private int[][] labelledImage;
	
	private final int imageWidth;
	private final int imageHeight;
	
	private int numberOfComponents;
	private List<ConnectedComponent> ccList;
	
	private final int debugLevel;
	
	
	// constructor /////////////////////////////////////////////////////////////////////////////////////////////////////
	public ConnectedComponentAnalysis(int[][] inputImage, int ConnectivityType) {
		this.inputImage = inputImage;
		this.imageHeight = inputImage.length;
		this.imageWidth = inputImage[0].length;
		
//		System.out.println("width: " + imageWidth);
//		System.out.println("height: " + imageHeight);

		this.ConnectivityType = ConnectivityType;
		this.debugLevel = 0; // if none supplied, assume 0
		
		this.labelledImage = perform_analysis();
		
		
		
	}
	
	public ConnectedComponentAnalysis(BufferedImage inputImage, int ConnectivityType) {
		this.inputImage = ConnectedComponentAnalysis.convertBinaryImageTo_2dIntArray(inputImage);
		this.imageHeight = this.inputImage.length;
		this.imageWidth = this.inputImage[0].length;
		
//		System.out.println("width: " + imageWidth);
//		System.out.println("height: " + imageHeight);

		this.ConnectivityType = ConnectivityType;
		this.debugLevel = 0; // if none supplied, assume 0
		
		this.labelledImage = perform_analysis();
		
		
	}
	
	public ConnectedComponentAnalysis(int[][] inputImage, int ConnectivityType, int debugLevel) {
		this.inputImage = inputImage;
		this.imageHeight = inputImage.length;
		this.imageWidth = inputImage[0].length;
		
//		System.out.println("width: " + imageWidth);
//		System.out.println("height: " + imageHeight);

		this.ConnectivityType = ConnectivityType;
		this.debugLevel = debugLevel;
		
		this.labelledImage = perform_analysis();
		
		
		
	}
	
	public ConnectedComponentAnalysis(BufferedImage inputImage, int ConnectivityType, int debugLevel) {
		this.inputImage = ConnectedComponentAnalysis.convertBinaryImageTo_2dIntArray(inputImage);
		this.imageHeight = this.inputImage.length;
		this.imageWidth = this.inputImage[0].length;
		
//		System.out.println("width: " + imageWidth);
//		System.out.println("height: " + imageHeight);

		this.ConnectivityType = ConnectivityType;
		this.debugLevel = debugLevel;
		
		this.labelledImage = perform_analysis();
		
		
	}

	
	// get set /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public int[][] getLabelledImage() {
		return labelledImage;
	}
	
	public int getNumberOfComponents() {
		return this.numberOfComponents;
	}
	
	public ConnectedComponent getConnectedComponent(int label) {
		if(1 <= label && label <= this.numberOfComponents) {
			return this.ccList.get(label - 1);
		} else {
			System.out.println("ERROR: invalid component label");
			return null;
		}
	}
	
	public List<ConnectedComponent> getAllConnectedComponents() {
		return this.ccList;
	}
	
	
	// public methods //////////////////////////////////////////////////////////////////////////////////////////////////
	public static void paintComponentIntoImage(ConnectedComponent cc, BufferedImage image, Color colour) {
		// image must have same dimensions as original image connected component was extracted from
		for(int y = cc.startYcoord; y <= cc.bottomYcoord; y++) {
			for(int x = cc.startXcoord; x <= cc.rightXcoord; x++) {
				if(cc.getAbsPixel(x, y)) {
					image.setRGB(x, y, colour.getRGB());
				}
			}
		}
	}
	
	public static void paintComponentIntoImage(ConnectedComponent cc, BufferedImage image, int colour) {
		// image must have same dimensions as original image connected component was extracted from
		for(int y = cc.startYcoord; y <= cc.bottomYcoord; y++) {
			for(int x = cc.startXcoord; x <= cc.rightXcoord; x++) {
				if(cc.getAbsPixel(x, y)) {
					image.setRGB(x, y, colour);
				}
			}
		}
	}
	
	public void displayColouredImage() {
		BufferedImage thisIm = ConnectedComponentAnalysis.convert_2dIntArray_toBinaryImage(this.inputImage);
		
		BufferedImage display = new BufferedImage(thisIm.getWidth(), thisIm.getHeight(), BufferedImage.TYPE_INT_RGB);
			// must initialise 'display' seperately so we can set the colour space to RGB
				
		ImageProcessing.copyImage(thisIm, display);
		List<Integer> uniqueColours = getUniqueColours(this.getNumberOfComponents());
		
		for(int i = 1; i <= this.getNumberOfComponents(); i++) {
			ConnectedComponent cc = this.getConnectedComponent(i);
			int colour = uniqueColours.get(i-1);
			paintComponentIntoImage(cc,display,colour);
		}
		ImageProcessing.displayImage(display, "Connected Component Labelled");
	}
	
	
	// code from 'http://stackoverflow.com/questions/3403826/how-to-dynamically-compute-a-list-of-colors'
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
	
	// converting images to 2d arrays and v.v. /////////////////////////////////////////////////////////////////////////
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
	
	
	// private methods /////////////////////////////////////////////////////////////////////////////////////////////////
	private int[][] perform_analysis() {
		
		// initialise //////////////////////////
		this.labelledImage = new int[imageHeight][imageWidth]; // elements initialised with 0's
		int labelCount = 1;
		
		int maxPossibleCCs;
		if(ConnectivityType == CONNECTIVITY_8) {
			maxPossibleCCs = (int)(Math.ceil((double)imageHeight/2.0)*(Math.ceil((double)imageWidth / 2.0)));
		} else {
			maxPossibleCCs = imageHeight*((int)Math.ceil((double)imageWidth / 2.0));
		}
		UnionFindSimple unionFind = new UnionFindSimple(maxPossibleCCs);// initialise union-find structure
		debug(3, "Initialising UnionFind structure: maxPossible CCs = " + maxPossibleCCs);
		////////////////////////////////////////
		
		// pass 1
		for(int y = 0; y < imageHeight; y++) {
			
			//process row y
			for(int x = 0; x < imageWidth; x++) {
				if(inputImage[y][x] == 1) {
					Set<Integer> priorNeighbourLabels = priorNeightbourLabels(y,x);
					int label;
					if(priorNeighbourLabels.isEmpty()) {
						label = labelCount;
						labelCount++;
					} else {
						label = min(priorNeighbourLabels);
					}
					labelledImage[y][x] = label;
					
					for(int neighbour : priorNeighbourLabels) {
						if(neighbour != label) {
							unionFind.union(neighbour, label);
						}
					}
				}
			}
		}
		
		
		
		debug(3, labelledImage);
		debug(4, "LabelCount: " + labelCount);
		
		
		// no. of labels = labelCount - 1
		int[] labelList = new int[labelCount]; // dont forget , min label = 1
		
		// pass 2: replaces pass 1 labels with equivalence class labels
		debug(3, "Replacing pass 1 labels with equiaalence class labels");
		
		for(int y = 0; y < imageHeight; y++) {
			for(int x = 0; x < imageWidth; x++) {
				if(inputImage[y][x] == 1) {
					debug(4, "pixel(" + x + "," + y + ")\t label(" + labelledImage[y][x] + ")");
					int newLabel = unionFind.findRoot(labelledImage[y][x]);
					labelledImage[y][x] = newLabel;
					labelList[newLabel] = 1;
				}
			}	
		}


		
//		printIm(labelledImage);

		
		
		// flatten label list
		int newLabelCount = 1;
		for(int i = 1; i < labelList.length; i++) {
			if(labelList[i] != 0) {
				labelList[i] = newLabelCount;
				newLabelCount++;
			}
		}
		
		this.numberOfComponents = newLabelCount - 1;

		// pass 3: relabel so labels are contiguous sequence of numbers from 1 to number_of_components
		for(int y = 0; y < imageHeight; y++) {
			for(int x = 0; x < imageWidth; x++) {
				if(inputImage[y][x] == 1) {
					labelledImage[y][x] = labelList[labelledImage[y][x]];
				}
			}
		}
//		System.out.println("relabelled contiguously:");
		
//		printIm(labelledImage);
		
		
		
//		 print unionFind ////
//		for(UnionFind.Node n : unionFind.nodes) {
//			if(n != null) {
//			System.out.println("value:" + n.value + "\trank: " + n.rank); // + "\tparent: " + n.parent.toString() + "\tchild: " + n.child.toString());
////			unionFind.find(n.value);
//			}
//		}
//		int[] vector = unionFind.getVector();
//		for(int i = 0; i < vector.length; i++) {
//			System.out.print(vector[i] + " ");
//		}
// 		System.out.println();
		
		
		// generate ConnectedComponent list and set state
		this.ccList = this.generateComponentList();
		return labelledImage;
	}
	
	private Set<Integer> priorNeightbourLabels(int y, int x) {
		// different structures for 4/8 connectivity
//		System.out.println("priorNeightbourLabels(" + y + "," + x + ")");
//		System.out.println("LabelledImage dimensions:" + labelledImage.length + "," + labelledImage[0].length);

		
		Set<Integer> labelSet = new HashSet<Integer>();
		
		if(this.ConnectivityType == CONNECTIVITY_4) {
			
		// four connectivity structure
		//	     1
		//	   0 x
		
			if(x > 0) { // not first column
				int zero = labelledImage[y][x - 1];
//				System.out.println("zero:" + zero);
				if(zero != 0) {labelSet.add(zero);};
			}
		
			
			if(y > 0) { // not first row
				int one = labelledImage[y-1][x];
				if(one != 0) labelSet.add(one);
			}
			
		} else { // 8 connectivity
		
		// eight connectivity structure
		//     1 2 3
		//     0 x		
			
			if(x > 0) { // not first column
				int zero = labelledImage[y][x - 1];
				if(zero != 0) {labelSet.add(zero);};
					
			}
			
			if(x > 0 && y > 0) {
				int one = labelledImage[y-1][x - 1];
				if(one != 0) {labelSet.add(one);};		
			}
		
			
			if(y > 0) { // not first row
				int two = labelledImage[y-1][x];
				if(two != 0) {labelSet.add(two);};
				
			}
			
			if(y > 0 && x < imageWidth - 1) {
				int three = labelledImage[y-1][x+1];
				if(three != 0) {labelSet.add(three);};
			}
			
			
		}
		
		
		return labelSet;
	}
	
	private Integer min(Set<Integer> set) {
		// get smallest element
		// do one iteration bubblesort
		Integer min = null;
		for(Integer i : set) {
			if(min == null) {
				min = i;
			} else {
				if(min > i) {
					min = i;
				}
			}
		}
		return min;
	}
	
	private List<ConnectedComponent> generateComponentList() {
		List<ConnectedComponent> ccList = new ArrayList<ConnectedComponent>();

		for(int i = 1; i <= this.numberOfComponents; i++) {
			
			ConnectedComponent cc = generateComponent(i);
			ccList.add(i-1, cc); // therefore does use index 0
		}
		return ccList;
		
	}
	
	private ConnectedComponent generateComponent(int label) {
		int[][] componentImage = new int[imageHeight][imageWidth]; // initialised with 0s
		
		// get min/max x/y for bounding box
		int minY = imageHeight- 1; //max y val
		int maxY = 0;
		
		int minX = imageWidth - 1; //max-x val
		int maxX = 0;
		
		// extract component from labelled image
		for(int y = 0; y < imageHeight; y++) {
			for(int x = 0; x < imageWidth; x++) {
				if(this.labelledImage[y][x] == label) {
					componentImage[y][x] = 1;
					
					minY = minY > y ? y : minY; // inclusive bounding box
					maxY = maxY < y ? y : maxY; // " 	"
					minX = minX > x ? x : minX; // " 	"
					maxX = maxX < x ? x : maxX; // " 	"
				}
			}
		}
		
		// cut padding out of image
		int ccWidth = maxX - minX + 1;
		int ccHeight = maxY - minY + 1;
		int[][] ccImage = new int[ccHeight][ccWidth];
		
		for(int y = 0; y < ccHeight; y++) {
			for(int x = 0; x < ccWidth; x++) {
				ccImage[y][x] = componentImage[minY + y][minX + x];
			}
		}
		
		//new ConnectedComponent
		ConnectedComponent cc = new ConnectedComponent(ccImage, minX, minY, label);
		return cc;
	}
	
	
	// overloaded image pixel get/setters, so can use any representation of an image ///////////////////////////////////
	
	// set foreground pixel
	private void setPixelForeground(int[][] image, int y, int x) {
		
	}
	
	private void setPixelForeground(boolean[][] image, int y, int x) {
		
	}
	private void setPixelForeground(BufferedImage image, int y, int x) {
		
	}
	
	// set background pixel
	private void setPixelBackground(int[][] image, int y, int x) {
		
	}
	
	private void setPixelBackground(boolean[][] image, int y, int x) {
		
	}
	private void setPixelBackground(BufferedImage image, int y, int x) {
		
	}
	
	// get pixel -> need foreground/background type...
	
	
	public static void printIm(int[][] image) {
		System.out.print(" \t| ");
		for(int x = 0; x < image[0].length; x++) {
			System.out.print(x + " ");
		}
		System.out.println();
		System.out.print(" \t| ");
		for(int x = 0; x < image[0].length; x++) {
			System.out.print("__");
		}
		System.out.println();
		
		for(int y = 0; y < image.length; y++) {
			System.out.print(y + "\t| ");
			for(int x = 0; x < image[0].length; x++) {
				System.out.print(image[y][x] + " ");
			}
			
			System.out.println();
		}
	}
	
	private void debug(int debugLevel, String debugMessage) {
		String thisClassName = "ConnectedComponentAnalysis";
		
		if(debugLevel <= this.debugLevel) {
			String indent = "";
			for(int i = debugLevel; i > 1; i--) {
				indent = indent + "\t";
			}
			
			System.out.println(thisClassName + ": DEBUG(" + debugLevel + ")\t" + indent + debugMessage);
		}
	}
	
	private void debug(int debugLevel, int[][] imageInt) {
		if(debugLevel <= this.debugLevel) {
		ConnectedComponentAnalysis.printIm(imageInt);
		}
	}

}
