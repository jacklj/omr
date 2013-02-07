package omr.final_representation_construction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import omr.OMR;

public class FinalRepresentationConstructor {
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	// state ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private final String lilyPondPDFcode;
	private final String lilyPondMIDIcode;
	private final String lilypondExecutableFilePath;
	
	private File finalRepresentation;
	private final int debugLevel;


	// constructor /////////////////////////////////////////////////////////////////////////////////////////////////////
	public FinalRepresentationConstructor(String lilyPondPDFcode,String lilyPondMIDIcode, int outputType, String outputDirPath, String outputFileName,  String lilypondExecutableFilePath, int debugLevel) {
		this.lilyPondPDFcode = lilyPondPDFcode;
		this.lilyPondMIDIcode = lilyPondMIDIcode;
		this.lilypondExecutableFilePath = lilypondExecutableFilePath;
		this.debugLevel = debugLevel;

		
		//saveImage(image, outputDirpath + fileName); 
		
		
		
		if(outputType == OMR.OUTPUT_LILYPOND_CODE) {
			debug(1, "Ouput Type: Lilyypond Code");
			
			generateCodeFile(outputDirPath, outputFileName, lilyPondPDFcode);
		} else if (outputType == OMR.OUTPUT_LILYPOND_PDF) {
			debug(1, "Ouput Type: Lilyypond PDF");
			
			generatePDFfile(outputDirPath, outputFileName);
		} else if (outputType == OMR.OUTPUT_LILYPOND_MIDI) {
			debug(1, "Ouput Type: Lilyypond MIDI");
			
			generateMIDIfile(outputDirPath, outputFileName);
		} else {
			// Unrecognised output file type - set to default?
			System.err.println("Unrecognised file type - set to default file type (LilyPond generated PDF");
			generatePDFfile(outputDirPath, outputFileName);
		}
		
	}
	
	
	// get set /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public File getFinalRepresentation() {
		return finalRepresentation;
	}
	
	// private methods /////////////////////////////////////////////////////////////////////////////////////////////////
	private void generateCodeFile(String outputDirPath, String outputFileName, String lilypond_code) {
		
		checkOutputDir(outputDirPath);
		
		String outputPath = outputDirPath + outputFileName;
		
		File f = new File(outputPath);
		if(!f.exists()){
			System.out.println("Output file \"" + outputPath + "\" doesn't exist - create new file");
			
			try {f.createNewFile();} 
			catch (IOException e) {
				System.err.println("File \"" + outputPath + "\" could not be created");
				e.printStackTrace();
			}
			
		} else {
			System.out.println("Output file \"" + outputPath + "\" already exists - overwrite");
		}
		
		
		// write to file
		FileWriter fW = null;
		try {
			fW = new FileWriter(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Couldnt open file writer for \"" + outputPath + "\"");
		}
		
		try {
			fW.write(lilypond_code);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Couldnt write to file \"" + outputPath + "\"");

		}
		
		try {
			fW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Couldnt close file writer for \"" + outputPath + "\"");

			e.printStackTrace();
		}

		System.out.println("Lilypond code text file saved at \"" + outputPath + "\"");

	}
	
	
	
	private void generatePDFfile(String outputDirPath, String outputFileName) {
		// --init=FILE 
		// --output=FILE // suffix will be added

		//create temporary lilypond code file so it can then be compiled by Lilypond
		checkOutputDir(outputDirPath);

		String tempFileName = "temporaryLilpondCodeFile.ly";
		generateCodeFile(outputDirPath, tempFileName, lilyPondPDFcode);
	
		compileLilyPondFile(outputDirPath, outputFileName, tempFileName);
	}
	
	
	
	private void generateMIDIfile(String outputDirPath, String outputFileName) {
		// --init=FILE 
		// --output=FILE // suffix will be added

		//create temporary lilypond code file so it can then be compiled by Lilypond
		checkOutputDir(outputDirPath);

		String tempFileName = "temporaryLilpondCodeFile.ly";
		generateCodeFile(outputDirPath, tempFileName, lilyPondMIDIcode);
	
		compileLilyPondFile(outputDirPath, outputFileName, tempFileName);
		
	}
	
	
		// run lilypond from shell
	private void compileLilyPondFile(String outputDirPath, String outputFileName, String tempFileName) {
		
		String lilypondCommand = this.lilypondExecutableFilePath + " --output=" + outputDirPath + outputFileName
		+ " " + outputDirPath + tempFileName;

		debug(1, "Shell command: " + lilypondCommand);


		try{
			Runtime rt = Runtime.getRuntime();

			//Process pr = rt.exec("cmd /c dir");
			Process pr = rt.exec(lilypondCommand);

			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

			String line = null;

			System.out.println("--------------- shell output ----------------------");

			while((line=input.readLine()) != null) {
				System.out.println(line);
			}

			int exitVal = pr.waitFor();
			System.out.println("Exited with error code "+exitVal);

			System.out.println("-----------------------------------------------------");

		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}

	}	
	
	
	

	
	
	private void compileLilyPondcode() {
		
	}
	
	
	private static void  checkOutputDir(String outputDirpath) {
		File f = new File(outputDirpath);
		if (f.exists()) {
			// File or directory exists
			System.out.println("Directory " + outputDirpath + " already exists");
			
		} else {
			// File or directory does not exist - create!
			System.out.println("Directory doesn't exist -> create");

			boolean success = f.mkdirs();
			if (success) {
				System.out.println("Directory: " + outputDirpath + " created");
			} else {
				System.out.println("Directory: not created.");

			}
		}

		
	}
	
	
	
	// debug ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void debug(int debugLevel, String debugMessage) {
		String thisClassName = "FinalRepresentationReconstructor";
		
		if(debugLevel <= this.debugLevel) {
			String indent = "";
			for(int i = debugLevel; i > 1; i--) {
				indent = indent + "\t";
			}
			
			System.out.println(thisClassName + ": DEBUG(" + debugLevel + ")\t" + indent + debugMessage);
		}
	}

}
