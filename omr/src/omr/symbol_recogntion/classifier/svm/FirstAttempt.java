package omr.symbol_recogntion.classifier.svm;

import java.awt.image.BufferedImage;
import java.io.IOException;

import omr.util.ImageProcessing;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

public class FirstAttempt {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		BufferedImage symbol = ImageProcessing.loadImage("/Users/buster/Stuff/Academia/II/DISSERTATION/symbol_sets/sets/basicSymbolSet/20_20/1.treble_clef/1.treble_clef_resized_resized.png");
		svm_node[] dataPoint = Feature_Extraction_for_LIBSVM.extract(symbol);
		
		
		String model_file_name = "/Users/buster/Stuff/Academia/II/DISSERTATION/SVM/basicSymbolSet_training/train.model";
		
		svm_model model1 = null;
		
		
		try {
			model1 = svm.svm_load_model(model_file_name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		int svmModelType = svm.svm_get_svm_type(model1);
		System.out.println("model type: " + svmModelType);

		
		
		int numberOfClasses =  svm.svm_get_nr_class(model1);
		System.out.println("number of Classes in model: " + numberOfClasses);
		
		
		int[] labels = new int[17];
		svm.svm_get_labels(model1, labels);
		
		System.out.println("Labels: ");
		for(int i = 0; i < labels.length; i++) {
			System.out.print(labels[i] + " ");
		}
		System.out.println();
		
		
		
		
		int checkProbabilityModel = svm.svm_check_probability_model(model1);
		System.out.println("Probability model: " + checkProbabilityModel);
		/*      This function checks whether the model contains required
    			information to do probability estimates. If so, it returns
    			+1. Otherwise, 0 is returned. This function should be called
    			before calling svm_get_svr_probability and
    			svm_predict_probability.
		 */
		
		
//		public static double svm_predict_values(svm_model model, svm_node[] x, double[] dec_values);
		
		svm_node node = new svm_node();
		node.index = 3;
		node.value = 0.6346;
		
		 
		
		double whichClass = svm.svm_predict(model1, dataPoint);
		
		System.out.println("Class:" + whichClass);
		
	}

}
