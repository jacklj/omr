package omr.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


// the purpose of this class is to extract individual symbols from the A4 sized images of symbols produced by Font Book


// grids not of consistent size -> probably have to use projection profiles to segment... classic...

//input - directory string
public class TrainingImageExtractor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		extract("/Users/buster/Stuff/Academia/II/DISSERTATION/test_font_export/from_pdf_2/", "/Users/buster/Stuff/Academia/II/DISSERTATION/test_font_export/singles2/");
	}


	public static void extract(String directory, String outputDirectory) {

		// 1 check valid directory

		//2 get all image (png) files, add to array?

		//3 iterate through these files

		//4 open image as a BufferedImage


		// Directory path here




		File folder = new File(directory);


		//		File outputFolder = new File(outputDirectory);


		File[] listOfFiles = folder.listFiles(); 

		int imgCount = 0;

		for (int i = 0; i < listOfFiles.length; i++) {

			if (listOfFiles[i].isFile()) {
				String file = listOfFiles[i].getName();
				if(!file.contains("png")) {
					System.out.println("******		not an image -  " + file);
				} else {



					imgCount++;
					System.out.println("[IMAGE] " + file);


					BufferedImage fontBookImage = ImageProcessing.loadImage(directory + file);

					// cut top 275 pixels off image to ignore headers
					int header = 275;
					BufferedImage page = fontBookImage.getSubimage(0, header, fontBookImage.getWidth(), fontBookImage.getHeight() - header); //without headers

					//		ImageProcessing.displayImage(page, file);

					int image_width = page.getWidth();
					int image_height = page.getHeight();
					System.out.println("width: " + image_width);
					System.out.println("height: " + image_height);



					page = ImageProcessing.preprocess(page);

					//where to segment
					Projection proj = new Projection(page);



					System.out.println("Determining Cuts");

					List<Integer> xCuts = new ArrayList<Integer>();
					List<Integer> yCuts= new ArrayList<Integer>();

					//find runs of 0.
					// (if longer than say 1/30th of image width / height)
					//		get their centre
					//		add this to x/yCuts

					{
						int[] xProj = proj.xProject();
						for(int x = 0; x < xProj.length; x++) {
							if(xProj[x] == 0) {
								int startx = x;
								while(x< xProj.length && xProj[x] == 0) {
//									System.out.print("0");
									x++;
								}
								int endx = x;
								//								System.out.println();
								//								System.out.println("startx: " + startx + " endx: " + endx);
								int middle0 = (int)(startx + 0.5*(endx - startx + 1));
								//								System.out.println("middle0: " + middle0);
								xCuts.add(middle0);
								System.out.println("\tXcut found: " + middle0);

							}
						}
					}

					{
						int[] yProj = proj.yProject();

						for(int y = 0; y < yProj.length; y++) {
							if(yProj[y] == 0) {
								int starty = y;
								while(y< yProj.length && yProj[y] == 0) {
									y++;
								}
								int endy = y;
								int middle0 = (int)(starty + 0.5*(endy - starty + 1));
								yCuts.add(middle0);

								System.out.println("\tYcut found: " + middle0);


							}
						}
					}

					System.out.println("Extracting Symbols:");

					BufferedImage symbol;




					int symbolCount = 0;

					for(int j = 0; j< xCuts.size() - 1; j++) {
						int previousXcut = xCuts.get(j);
						int thisXcut = xCuts.get(j+1);
						
						int w = thisXcut - previousXcut;
						
						if(w <=0) {
							w = 1;
						}
						if(previousXcut + 1 + w >= page.getWidth()) {
							w = page.getWidth() - previousXcut - 2;
						}

						System.out.println("\tX left: " + previousXcut + " X width: " + w);
						
						
						for(int k = 0; k < yCuts.size() - 1; k++) {
							
							int previousYcut = yCuts.get(k);
							int thisYcut = yCuts.get(k+1);
							
							symbolCount++;

							int h = thisYcut - previousYcut;
							if(h <= 0) {
								h = 1;
							}
							if(previousYcut + 1 + h >= page.getHeight()) {
								h = page.getHeight() - previousYcut - 1;
							}

							System.out.println("\t\tY left: " + previousYcut + " Y width: " + h);

							symbol = page.getSubimage(previousXcut + 1, previousYcut + 1, w, h); //therefore ignore whatevers after the final cut - good (will just be whitespace)

							if(isImageAllWhite(symbol)) {
								System.out.println("\t\t\tblank image - don't save");
							} else {
								String imgName = "page" + imgCount + "_symbol" + symbolCount + ".png";
								ImageProcessing.saveImage(symbol, outputDirectory, imgName);
								System.out.println("\t\t\timage saved - " + imgName);
							}
						}
					}


					//		int w = x + grid_square_width >= image_width ? image_width - x - 1 : grid_square_width;
					//		int h = y + grid_square_height >= image_height ? image_height- y - 1 : grid_square_height;




				}

			}
		}





	}
	
	
	
	private static boolean isImageAllWhite(BufferedImage image) {
		
		int accumulator = 0;
		
		for(int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				if(image.getRGB(x, y) == Color.WHITE.getRGB()) {
					accumulator++;
				}
			}
		}
		
		if(accumulator > 0) {
			return false;
		} else {
			return true;
		}

	}
}
