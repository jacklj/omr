package omr.symbol_recogntion.classifier;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.encog.ml.data.basic.BasicMLData;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import omr.symbol_recogntion.classifier.ann.Feature_Extraction_for_Encog;
import omr.symbol_recogntion.classifier.basicSymbolSet.SymbolClass;
import omr.symbol_recogntion.classifier.complexSymbolSet.SymbolType;
import omr.symbol_recogntion.classifier.svm.Feature_Extraction_for_LIBSVM;
import omr.symbol_recogntion.segmentation.L2_Segment;
import omr.util.ImageProcessing;

public class Classifier {
	
	
	public static void main(String[] args) {
		String SVMmodel_file_name = "/Users/buster/Stuff/Academia/II/DISSERTATION/SVM/basicSymbolSet_training/train.model";
		
		Classifier c = new Classifier(Classifier.SVM, SVMmodel_file_name);
		
		BufferedImage symbolImage = ImageProcessing.loadImage("/Users/buster/Stuff/Academia/II/DISSERTATION/symbol_sets/sets/basicSymbolSet/20_20/11.minim_upsidedown/page141_symbol17_resized_rotated_10_normal_rotated_10_flipped_resized.png");
		SymbolClass symbol = c.classify(symbolImage);
		
		System.out.println("Symbol class = " + symbol.toString());
	}
	
	
	
// state ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final int SVM = 1;
	public static final int ANN = 2;
	
	private final int classifierType;
	String modelFilePath;
	
	svm_model SVMmodel;
	
	// constructor /////////////////////////////////////////////////////////////////////////////////////////////////////
	public Classifier(int classifier_type, String modelFilePath) {
		
		if(classifier_type != SVM && classifier_type != ANN) {
			System.err.println("Invalid classifier type specified - either Classifier.SVM or Classifer.ANN");
		}		
		
		this.classifierType = classifier_type;
		this.modelFilePath = modelFilePath;
		
		// prepare classifer by loading model etc
		prepareClassifier();
		
	}
	
	
	private void prepareClassifier() {
		if(classifierType == SVM) {
			prepareSVM();
		} else if(classifierType == ANN) {
			prepareANN();
		} else {
			System.err.println("ERROR: Invalid classifier type specified.");
			
		}
	}
	
	
	private void prepareSVM() {
		
		svm_model model1 = null;
		try {
			model1 = svm.svm_load_model(modelFilePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("ERROR: Model file couldn't be opened (" + modelFilePath + ")");
			e.printStackTrace();
			
		}
		
		this.SVMmodel = model1;
		
		System.out.println("---------- SVM model info: -----------");
		
		int svmModelType = svm.svm_get_svm_type(model1);
		System.out.println("model type: " + svmModelType);

		
		int numberOfClasses =  svm.svm_get_nr_class(model1);
		System.out.println("number of classes in model: " + numberOfClasses);
		
		
//		int[] labels = new int[17];
//		svm.svm_get_labels(model1, labels);
//		
//		System.out.println("Labels: ");
//		for(int i = 0; i < labels.length; i++) {
//			System.out.print(labels[i] + " ");
//		}
//		System.out.println();
		
//		int checkProbabilityModel = svm.svm_check_probability_model(model1);
//		System.out.println("Probability model: " + checkProbabilityModel);
		/*      This function checks whether the model contains required
    			information to do probability estimates. If so, it returns
    			+1. Otherwise, 0 is returned. This function should be called
    			before calling svm_get_svr_probability and
    			svm_predict_probability.
		 */
		
	}
	
	private void prepareANN() {
		
	}
	
	
	public SymbolClass classify(BufferedImage symbolImage) {
		SymbolClass symbol;
		
		if(classifierType == SVM) {
			symbol = classifySVM(symbolImage);
		} else if(classifierType == ANN) {
			symbol = null;
		} else {
			System.err.println("ERROR: Invalid classifier type specified.");
			symbol = null;
		}
		
		return symbol;
	}
	
	
	
	private SymbolClass classifySVM(BufferedImage symbolImage) {
		
		svm_node[] dataPoint = Feature_Extraction_for_LIBSVM.extract(symbolImage);
		
		 
		double whichClass = svm.svm_predict(this.SVMmodel, dataPoint);
		
		
		SymbolClass symbol = null;
		
		for(SymbolClass sClass : SymbolClass.values()) {
			if(sClass.classNumber() == (int)(whichClass)) {
				symbol = sClass;
			}
		}
		
		System.out.println("Class:" + whichClass + ". " + symbol.toString());
		
		return symbol;
		
	}
	
	
	
	private SymbolClass classifyANN(BufferedImage symbolImage) {
		BasicMLData dataPoint = Feature_Extraction_for_Encog.extract(symbolImage);

		double whichClass = svm.svm_predict(this.SVMmodel, dataPoint);
		
		
		SymbolClass symbol = null;
		
		for(SymbolClass sClass : SymbolClass.values()) {
			if(sClass.classNumber() == (int)(whichClass)) {
				symbol = sClass;
			}
		}
		
		System.out.println("Class:" + whichClass + ". " + symbol.toString());
		
		return symbol;		
	}
}
