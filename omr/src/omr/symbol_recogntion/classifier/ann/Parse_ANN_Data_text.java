package omr.symbol_recogntion.classifier.ann;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parse_ANN_Data_text {

	// training data text file format:
	//	[label] [feature number]:[feature value] []:[] ...... []:[] \n
	
	
	public static void main(String[] ars) {
		String training_data_filePath = "/Users/buster/Stuff/Academia/II/DISSERTATION/ANN/data/train.txt";//"/Users/buster/Stuff/Academia/II/DISSERTATION/ANN/allData.txt";
		int numberOfFeatures = 400; //20*20 input image
		int numberOfSymbolClasses = 17;
		Parse_ANN_Data_text parseData = new Parse_ANN_Data_text(training_data_filePath, numberOfFeatures, numberOfSymbolClasses);
		
		double[][] labels = parseData.getLabels();
		double[][] data = parseData.getData();
		
		
		/// now training data should have been successfully imported - print out to test.
		
		System.out.println("PRINT IMPORTED DATA:");
		System.out.println("Number of labels = " + labels.length);
		System.out.println("Number of dataPoints = " + data.length);
		
		for(int i = 0; i < labels.length; i++) {
			String label = "[";
			for(int x = 0; x < numberOfSymbolClasses; x++) {
				label = label + labels[i][x] + " ";
			}
			label = label+ "]\t";
			System.out.print(label);
			for(int j = 0; j < numberOfFeatures; j++) {
				System.out.print(j+1 + "=" +
						data[i][j]     
						
						+ "  ");
			}
			System.out.println();
		}
	}
	
	//state ////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private final String training_data_filePath;
	private final int numberOfFeatures;
	private final int numberOfSymbolClasses;
	
	private List<String> labels;
	private int[][] data;
	
	private double[][] labelsDouble;
	private double[][] dataDouble;
	
	// constructor /////////////////////////////////////////////////////////////////////////////////////////////////////
	Parse_ANN_Data_text(String training_data_filePath, int numberOfFeatures, int numberOfSymbolClasses) {
		this.training_data_filePath = training_data_filePath;
		this.numberOfFeatures = numberOfFeatures;
		this.numberOfSymbolClasses = numberOfSymbolClasses;
		
		
		parse(); // sets labels and data
		
		setDoubles(); // constructs labelsDouble and dataDouble
		
	}
	
	//get set //////////////////////////////////////////////////////////////////////////////////////////////////////////
	List<String> getLabelsString() {
		return labels;
	}
	
	int[][] getDataInts()  {
		return data;
	}
	
	double[][] getLabels() {
		return labelsDouble;
	}
	double[][] getData() {
		return dataDouble;
	}
	
	// private methods ///////
	private void setDoubles() {
		
		
		labelsDouble = new double[labels.size()][numberOfSymbolClasses]; // eg label 3 = [0,0,1,0,0,0,...]
		dataDouble = new double[labels.size()][numberOfFeatures];
		
		for(int i = 0; i < labels.size(); i++) {
			//label
			String labelString = labels.get(i);
		//	String label = (String) labelString.subSequence(1, labelString.length()); // removes the + -> no +'s anymore
			int labelNumber = Integer.parseInt(labelString); // should range from 1 to numberOfSymbolClasses
			labelsDouble[i][labelNumber-1] = 1.0;
			
			//data
			for(int j = 0; j < numberOfFeatures; j++) {
					dataDouble[i][j] = (double)data[i][j];

			}
		}
	}
	
	
	
	private void parse() {
		
		//load file
		BufferedReader r = null;
		try {
			r = new BufferedReader(new FileReader(training_data_filePath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		
		List<String> labels = new ArrayList<String>();
		List<List<String>> dataStrings = new ArrayList<List<String>>();
		
		System.out.println("PARSING:");
		
		String line;
//		int lineCount = 0;
		try {
			while ((line = r.readLine()) != null) {
			  String[] tokens = line.split(" ");
			  
			  //label
			  String label = tokens[0].trim();
			  labels.add(label);
//			  System.out.println("\t label:" + label);
			  
			  //data
			  List<String> oneDataPoint = new ArrayList<String>();
			  for(int i = 1; i < tokens.length; i++) { // -2 to avoid # and fileName
				  String sparseDataPoint = tokens[i].trim();
				  oneDataPoint.add(sparseDataPoint);
			  }
//			  System.out.println();
			  
			  dataStrings.add(oneDataPoint);
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		                                     
		int[][] data = new int[labels.size()][numberOfFeatures]; // initialise 'data' to correct size
		int count = 0;
		
		for(List<String> dataPoint : dataStrings) {
			int[] dataPointDenseFormat = new int[numberOfFeatures];
			for(String point : dataPoint) {
				if(point.length() > 2) { // incase any wierd strings like ' '.
					String[] tokens = point.split(":");
					int featureNumber = Integer.parseInt(tokens[0]);
					int featureValue = Integer.parseInt(tokens[1]);

					dataPointDenseFormat[featureNumber - 1] = (featureValue);
				}
			}
			data[count] = dataPointDenseFormat;
			count++;
		}
		
		System.out.println("parsing done.");
		
		this.labels = labels;
		this.data = data;
		
		
		
//		/// now training data should have been successfully imported - print out to test.
//		
//		System.out.println("PRINT IMPORTED DATA:");
//		System.out.println("Number of labels = " + labels.size());
//		System.out.println("Number of dataPoints = " + data.length);
////		
////		for(int i = 0; i < labels.size(); i++) {
////			String label = labels.get(i);
////			System.out.print("'" + label + "'\t");
////			for(int j = 0; j < numberOfFeatures; j++) {
////				System.out.print(j+1 + "=" +
////						data[i][j]     
////						
////						+ "  ");
////			}
////			System.out.println();
////		}
		
	}

	
}
