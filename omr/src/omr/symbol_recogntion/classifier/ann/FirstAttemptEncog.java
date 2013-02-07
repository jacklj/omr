package omr.symbol_recogntion.classifier.ann;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;


import omr.symbol_recogntion.classifier.basicSymbolSet.SymbolClass;
import omr.util.FileListing;
import omr.util.ImageProcessing;

import org.encog.Encog;
import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.simple.EncogUtility;


public class FirstAttemptEncog {
	
	public static void main(String[] args) {
		
		String trainingdataFile = "/Users/buster/Stuff/Academia/II/DISSERTATION/ANN/data/train.txt";
		String testingDataFile = "/Users/buster/Stuff/Academia/II/DISSERTATION/ANN/data/test.txt";
		String whereToSaveTrainedANN = "/Users/buster/Stuff/Academia/II/DISSERTATION/ANN/encogexample_savedTrainedNetwork.eg";

		int numberOfFeatures = 400;
		int numberOfClasses = 17;
		
		
		
		// train ANN ///////////////////////////////////////////////////////////////////////////////////////////////////
		BasicNetwork network = null;
		try {
			FirstAttemptEncog ann = new FirstAttemptEncog(trainingdataFile, whereToSaveTrainedANN, numberOfFeatures,
					numberOfClasses);
			network = ann.train();
			ann.saveNetwork();
			
//			network = ann.loadAndEvaluate();
//		} catch (Throwable t) {
//			t.printStackTrace();
		} finally {
			Encog.getInstance().shutdown();
		}
		
		
		
		//test ANN /////////////////////////////////////////////////////////////////////////////////////////////////////
		
//		//single image tests
		String testFolder = "/Users/buster/Stuff/Academia/II/DISSERTATION/symbol_sets/sets/testSet_15_5";
		List<String> images = FileListing.getAllImages(testFolder);
		for(String path : images) {

			BufferedImage symbolImage = ImageProcessing.loadImage(path);

			BasicMLData dataPoint = Feature_Extraction_for_Encog.extract(symbolImage);
			double whichClass = network.classify(dataPoint);


			SymbolClass symbol = SymbolClass.whichClass((int)whichClass);

			System.out.println("Symbol: " + symbol.toString() + "\t file: " + path);
		}
		
		// test set
		Parse_ANN_Data_text parseTestData = new Parse_ANN_Data_text(testingDataFile, numberOfFeatures, numberOfClasses);
		double[][] testingLabels = parseTestData.getLabels();
		double[][] testingData = parseTestData.getData();
		MLDataSet testingSet = new BasicMLDataSet(testingData, testingLabels);
		
		double error = network.calculateError(testingSet);
		System.out.println("% error classifying test set = " + error);
		
	}
	
	
	// state ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private final double[][] inputData;
	private final double[][] inputLabels;
	private final String whereToSaveTrainedANN;
	
	BasicNetwork network;
	
	
	// constructor /////////////////////////////////////////////////////////////////////////////////////////////////////
	public FirstAttemptEncog(String training_data_filePath, String whereToSaveTrainedANN, int numberOfFeatures, int numberOfClasses ) {
		this.whereToSaveTrainedANN = whereToSaveTrainedANN;
		
		//load training data into memory
		Parse_ANN_Data_text parseTrainingData = new Parse_ANN_Data_text(training_data_filePath, numberOfFeatures, numberOfClasses);
		
		inputLabels = parseTrainingData.getLabels();
		inputData = parseTrainingData.getData();
	}
	

	// private methods /////////////////////////////////////////////////////////////////////////////////////////////////
	private BasicNetwork train() {
		BasicNetwork network = EncogUtility.simpleFeedForward(400, 100, 0, 17, false);
		
		// randomize consistent so that we get weights we know will converge
		(new ConsistentRandomizer(-1,1,100)).randomize(network);

		

		// train the neural network
		MLDataSet trainingSet = new BasicMLDataSet(inputData, inputLabels);
		final MLTrain train = new ResilientPropagation(network, trainingSet);

		
		int maxIt = 10000;	double maxError = 0.001; //0.049);
		int it = 0;
		do {
			train.iteration();
			it++; System.out.println("  <"+it+"> train.getError() = "+train.getError());
		} while (it<maxIt && train.getError() > maxError);

		double e = network.calculateError(trainingSet);
		System.out.println("Network trained to error: " + e);
		
		
		this.network = network;
		
		return network;
	}
	
	private void saveNetwork() {
		System.out.println("Saving network");
		EncogDirectoryPersistence.saveObject(new File(whereToSaveTrainedANN), network);
	}
	
	
	
	private void trainAndSave() {
		BasicNetwork network = EncogUtility.simpleFeedForward(400, 75, 0, 17, false);
		
		// randomize consistent so that we get weights we know will converge
		(new ConsistentRandomizer(-1,1,100)).randomize(network);

		

		// train the neural network
		MLDataSet trainingSet = new BasicMLDataSet(inputData, inputLabels);
		final MLTrain train = new ResilientPropagation(network, trainingSet);

		do {
			train.iteration();
		} while (train.getError() > 0.049);

		double e = network.calculateError(trainingSet);
		System.out.println("Network trained to error: " + e);

		System.out.println("Saving network");
		EncogDirectoryPersistence.saveObject(new File(whereToSaveTrainedANN), network);
	}

	
	
	
	private BasicNetwork loadAndEvaluate() {
		System.out.println("Loading network");

		BasicNetwork network = (BasicNetwork)EncogDirectoryPersistence.loadObject(new File(whereToSaveTrainedANN));

		MLDataSet trainingSet = new BasicMLDataSet(inputData, inputLabels);
		
		
		double e = network.calculateError(trainingSet);
		System.out
				.println("Loaded network's error is(should be same as above): "
						+ e);
		return network;
	}

	

}
