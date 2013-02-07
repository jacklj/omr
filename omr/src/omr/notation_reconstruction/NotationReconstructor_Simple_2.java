package omr.notation_reconstruction;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import omr.OMR;
import omr.notation_reconstruction.logical_representation.Clef;
import omr.notation_reconstruction.logical_representation.KeySignature;
import omr.notation_reconstruction.logical_representation.Pitch;
import omr.notation_reconstruction.logical_representation.Pitch.Note;
import omr.notation_reconstruction.logical_representation.SharpKeys;
import omr.notation_reconstruction.logical_representation.FlatKeys;
import omr.notation_reconstruction.logical_representation.TimeSignature;
import omr.symbol_recogntion.SymbolRecogniser_Simple;
import omr.symbol_recogntion.classifier.basicSymbolSet.Symbol;
import omr.symbol_recogntion.classifier.basicSymbolSet.SymbolClass;
import omr.symbol_recogntion.classifier.basicSymbolSet.SymbolType;
import omr.symbol_recogntion.score_metrics.ScoreMetrics;
import omr.symbol_recogntion.segmentation.L0_Segment;
import omr.symbol_recogntion.segmentation.Score;
import omr.symbol_recogntion.segmentation.Stave;
import omr.symbol_recogntion.stave_detection.StaveSkeleton;
import omr.util.ImageProcessing;

public class NotationReconstructor_Simple_2 {

	
	// public static void main /////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) {
		String scorePath = "/Users/buster/Stuff/Academia/II/DISSERTATION/test_images/1bar.png"; //"/Users/buster/Stuff/Academia/II/DISSERTATION/Lilypond/c _scale.png";
		String modelFilePath = "/Users/buster/Stuff/Academia/II/DISSERTATION/SVM/basicSymbolSet_training/train.model";
		int boxSize = 20;
		
		int outputType = OMR.OUTPUT_LILYPOND_CODE;
		String outputPath = "";
		int debugLevel = 4;
		
		
		// 1.load image
		BufferedImage img = ImageProcessing.loadImage(scorePath);
		ImageProcessing.displayImage(img, "Input image");
		
		// 2.preprocess
		img = ImageProcessing.preprocess(img);
		ImageProcessing.displayImage(img, "Binarised");
		
		// 3.symbol recogniser
		SymbolRecogniser_Simple sr = new SymbolRecogniser_Simple(img, scorePath, modelFilePath, boxSize, debugLevel);
		Score analysedScore = sr.getScore();
		ScoreMetrics scoreMetrics = sr.getScoreMetrics();
		
		//3) notation reconstruction
		NotationReconstructor_Simple nr = new NotationReconstructor_Simple(analysedScore, scoreMetrics, debugLevel); // debug level = 3
		
		String internalRepresentation = nr.getInternalRepresentation(); // how long can a Java string be? long enough?
		
		System.out.println(internalRepresentation);
		
	}
	
	
	
	
	// key theory //////////////////////////////////////////////////////////////////////////////////////////////////////
	private static final String[] sharpKeySignatureProgression = {"F", "C", "G", "D", "A", "E", "B"};
	private static final String[] sharpKeySignatures =  {"\\key c \\major",
																  "\\key g \\major",
																       "\\key d \\major",
																            "\\key a \\major",
																                 "\\key e \\major",
																                     "\\key b \\major",
																                           "\\key fis \\major",
																                                 "\\key cis major"};
	
	
	private static final String[] flatKeySignatureProgression = {"B",	"E",	"A",	"D",	"G",	"C",	"F"}; 
		// i.e. the reverse of above
	private static final String[] flatKeySignatures = {"\\key c \\major",
																 "\\key f \\major",
																		"\\key bes \\major",
																	  			"\\key ees \\major",
																	  					"\\key aes \\major",
																	  							"\\key des \\major",
																	  									"\\key ges \\major",
	                                 																			"\\key ces major"};
	
	// state ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private final omr.symbol_recogntion.segmentation.Score scoreGraphicalRepresentation;
	private final ScoreMetrics scoreMetrics;
	private final int debugLevel; 		// 0 = no debugging print outs
								      	// 1 = basic debugging print outs
										// 2 = medium debugging print outs
										// 3 = most debugging print outs
										// 4 = all debugging print outs
	
	private String lilyPondCode;
	private String lilyPondMIDIcode;
	
	private final omr.notation_reconstruction.logical_representation.Score scoreLogicalRepresentation;
	
	// constructor /////////////////////////////////////////////////////////////////////////////////////////////////////
	public NotationReconstructor_Simple_2(omr.symbol_recogntion.segmentation.Score score_internal_graphical_representation, ScoreMetrics scoreMetrics, int debugLevel) {
		this.scoreGraphicalRepresentation = score_internal_graphical_representation;
		this.scoreMetrics = scoreMetrics;
		this.debugLevel = debugLevel;
		
		this.scoreLogicalRepresentation = new omr.notation_reconstruction.logical_representation.Score();
		this.reconstruct();
	}
	

	// get set methods /////////////////////////////////////////////////////////////////////////////////////////////////
	public String getLilyPondCode() {
		return this.lilyPondCode;
		
	}
	
	public String getLilyPondMIDIcode() {
		return this.lilyPondMIDIcode;
	}
	
	public String getInternalRepresentation() {
		return this.lilyPondCode; // at the moment, internal representation IS lilypond code
	}
	
	

	// main functionality //////////////////////////////////////////////////////////////////////////////////////////////
	private void reconstruct() {
		
		
		this.scoreLogicalRepresentation.setTitle(this.scoreGraphicalRepresentation.getScoreSourceFilePath());
		
		List<Stave> staveGraphical_list = this.scoreGraphicalRepresentation.getStave_list();
		
		SymbolClass previousStavesTimeSignature = null; // as the time signature is usually just included in the first line of music
		
		for (Stave stave_graphical : staveGraphical_list) {
			
			omr.notation_reconstruction.logical_representation.Stave staveLogical = this.scoreLogicalRepresentation.newStave(); // new logical stave
			
			
			// internal representation: each stave has a list of L0_Segments and a stave skeleton
			
			// music syntax rules:
			// 		- each stave has a clef and key signature before any other symbols
			//		- the first stave of a page will also have a time signature
			
			debug(1, "New stave");
			
			StaveSkeleton sk = stave_graphical.getStaveSkeleton();
			List<L0_Segment> l0_segment_list = stave_graphical.get_L0_Segment_list();
						
			// deal with clef and key signature seperately...
			// adapted version of algorithm  in "OMR using projections", Fujinaga, 1997
			
			
			// clef ////////////////////////////////////////////////////////////////////////////////////////////////////
			debug(2, "Calculating stave clef");

			int timeSegmentIndex = 0; // in simpleOMR a time segment = an L0_Segment (and each time segment contains only one symbol)
			L0_Segment clefSegment = l0_segment_list.get(timeSegmentIndex);
			Symbol clefSymbol = clefSegment.getSymbol();

			
			if(clefSymbol.get_symbol_type() != SymbolType.CLEF) {
				System.err.println("ERROR: first symbol not a clef - stave breaks WMN rules... (set to default: treble clef)");
				staveLogical.setStartingClef(Clef.TREBLE);
				// discard this symbol - probably error
				timeSegmentIndex++;
				
				debug(3, "ERROR: first symbol not a clef - stave breaks WMN rules... (set to default: treble clef)");
			} else {
				if(clefSymbol.get_symbol_class() == SymbolClass.TREBLE_CLEF) {					
					staveLogical.setStartingClef(Clef.TREBLE);
					timeSegmentIndex++;
					
					debug(3, "1st symbol = stave clef = TREBLE_CLEF");
				} else { // no others in SimpleOMR
					// if bass clef, need to get next time segment too to get the colon... (and currentTimeSegment + 2)
					System.out.println("ERROR: Clef class not recognised (SimpleOMR) - set to default (treble)");
					staveLogical.setStartingClef(Clef.TREBLE);  // default
					
					// discard this symbol - probably error
					timeSegmentIndex++;
					
					debug(3, "1st symbol a clef, but of an unrecognised class - set to default( treble)");
				}
			}
			
						
			
			// key signature ///////////////////////////////////////////////////////////////////////////////////////////
			debug(2, "Calculating key signature");
			KeySignature keySignature;

			// WMN: - if it's the first line of the page, there will always be a time signature present at the beginning of the stave
			//		- then the key signature (if any) lies between the clef and the key signature
			//	    - so look for a run of all sharps or all flats (making a key signature) (also must be precise pitches)
			//		  then a time signature
			
			// 		- if not the first line of music, not necessarily a time signature present
			//		- use Fujinaga approximation: 
			//			"
			//			 To find the type (sharp or flat) and the number of accidentaIs in the key signature, the staff 
			//			 projection is scanned from the right side of the clef until an empty space larger than a 
			//			 staffspace is found. Thc assumptions here arc that the space between the accidentaIs within a
			//			 key signature is less than a staffspace, and that the space between the last accidentaI in the 
			//			key signature and the following symbol is at least a staffspace. "
			//
			//		- if (symbol 2 is not a sharp or flat -> key = c major (/a minor))
			//		- else get all subsequent consecutive accidentals of the same class (ie until a symbol not of the
			//		  same kind of accidental is found
			//		 - if last time unit's (L2_Segment, here l0_segment) has multiple vertically consecutive sharps - not part of key signnature (accidentals for a chord) 
			//			|| if one sharp, but it's futher than stave_space_height away (or closer to next symbol than to previous one might be a better metric),
			//		   then probably accidentals not in the key signature -> ignore.
			
			// 		- compare the accidentals' pitches to the key signature patterns (circle of fifths)
			
			// will currently fail if either  g major (just f# in key sig) or f maj (just Bb in key sig) && next symbol is a note of the same pitch (f# or Bb) 

			List<Symbol> keySignatureSymbolList = new ArrayList<Symbol>();
			
			L0_Segment provisionalFirstKeySignSegment = l0_segment_list.get(timeSegmentIndex); // timeSegmentIndex should = 1;
			Symbol provisionalFirstKeySigSymbol = provisionalFirstKeySignSegment.getSymbol();
			SymbolClass provisionalClass = provisionalFirstKeySigSymbol.get_symbol_class();

			Pitch provisionalPitch = this.calculatePitch(provisionalFirstKeySigSymbol, sk, staveLogical.getStartingClef());
	
				
			provisionalPitch.setAccidental(provisionalClass);
			debug(3, "Got symbol after clef - it's a " + provisionalFirstKeySigSymbol.get_symbol_class().toString() + ", pitch: " + provisionalPitch.getNote().toString());
			
			if(provisionalPitch.equals( (Pitch)(SharpKeys.getAllAccidentals(staveLogical.getStartingClef())[0])) ) { // ie an f# -  part of a sharp key signature.
				timeSegmentIndex++;
				debug(3, "f# found -> probably part of a # key signature");

				//get all subsequent consecutive segments containing one sharp whose pitch matches the circle or fifths pattern
				// (last one could still be an accidental - check the final sharps position as described above)

				int keySigCount = 1;
				
				for (int i = timeSegmentIndex; i < timeSegmentIndex + 6; i++) { // 7 - 1 as first accidental in key signature has already been parsed
					L0_Segment thisSegment = l0_segment_list.get(i);
					
					Symbol thisSymbol = thisSegment.getSymbol();
					SymbolClass sClass = thisSymbol.get_symbol_class();
					Pitch pitch = this.calculatePitch(thisSymbol, sk, staveLogical.getStartingClef());
					pitch.setAccidental(sClass);
					
					if( keySigCount < 8 
							&& (pitch.equals( (Pitch)(SharpKeys.getAllAccidentals(staveLogical.getStartingClef())[keySigCount])) )
					   ) { // max sharps in a key signature = 7 (c# major)
						
						//in key signature - continue!
						debug(3, pitch + "# found: part of key signature");
						keySigCount++;

					} else {
						// not in key signature -> key signature is whatever's already been.
						debug(3, sClass.toString() + " found: NOT part of key signature");
						break; 
						// either a NOT sharp symbol (or wrong pitch)
						// or keySigCount >= 7  (in which case it's C#m major)
					}

				}

				keySignature = SharpKeys.getKeySignature(keySigCount); // keySigCount = number of sharps in key signature
				timeSegmentIndex = timeSegmentIndex + keySigCount; //increase timeSegmentIndex appropriately

				//done parsing sharp key signature


			} else if (provisionalPitch.equals( (Pitch)(FlatKeys.getAllAccidentals(staveLogical.getStartingClef())[0])) 			) {  // ie a Bb - probably part of a flat key signature
				timeSegmentIndex++;
				debug(3, "Bb found -> probably part of a b key signature");

				// get all subsequent consecutive segments containing one flat whose pitch matches the circle or fifths pattern
				// (last one could still be an accidental - check the final flat's position as described above)

				int keySigCount = 1;

				for (int i = timeSegmentIndex; i < timeSegmentIndex + 6; i++) { // 7 - 1 as first accidental in key signature has already been parsed
					L0_Segment thisSegment = l0_segment_list.get(i);
					
					Symbol thisSymbol = thisSegment.getSymbol();
					SymbolClass sClass = thisSymbol.get_symbol_class();
					Pitch pitch = this.calculatePitch(thisSymbol, sk, staveLogical.getStartingClef());
					pitch.setAccidental(sClass);
					
					if( keySigCount < 8 && (pitch.equals( (Pitch)(FlatKeys.getAllAccidentals(staveLogical.getStartingClef())[keySigCount])) ) 
							) {
						//in key signature - continue!
						debug(3, pitch + "b found: part of key signature");
						keySigCount++;

					} else {
						// not in key signature -> key signature is whatever's already been.
						debug(3, sClass.toString() + " found: NOT part of key signature");
						break; // don't increment currentTimeSeg so this symbol will be reparsed
						 // either a NOT flat symbol (or wrong pitch)
						// or for loop has finished running (in which case it's Cb major)
					}

				}

				keySignature = FlatKeys.getKeySignature(keySigCount); // keySigCount = number of sharps in key signature
				timeSegmentIndex = timeSegmentIndex + keySigCount; //increase timeSegmentIndex appropriately

				//done parsing sharp key signature


			} else {//not part of key signature -> c major
				//set key sign to C major and don't increment timeSegmentIndex so symbol will be reparsed
				keySignature = SharpKeys.getKeySignature(0); // c major

			}
			
			
			debug(1, "Stave Key Signature = \"" + keySignature.toString() + "\"");
			staveLogical.setStartingKeySignature(keySignature);
			
			
			
			
			// time signature //////////////////////////////////////////////////////////////////////////////////////////
			debug(2, "Calculating Time Signature:");
						
			L0_Segment provisionalTimeSignatureSegment = l0_segment_list.get(timeSegmentIndex);
			Symbol provisionalTimeSigSymbol = clefSegment.getSymbol();
			
			if(provisionalTimeSigSymbol.get_symbol_type() == SymbolType.TIME_SIGNATURE) {
				if(provisionalTimeSigSymbol.get_symbol_class() == SymbolClass.COMMON_TIME) {
					staveLogical.setStartingTimeSignature(new TimeSignature(TimeSignature.Name.COMMON_TIME));
				} else { // no others in basic symbol set - set to commontime
					staveLogical.setStartingTimeSignature(new TimeSignature(TimeSignature.Name.COMMON_TIME));
				}
				timeSegmentIndex++;
				
			} else { 
				//either not first line (in which case set stave time signature to previousStavesTimeSignature,
				// or if that's null (therefore it's the first line of music) then score representation violates WMN - 
				// set to default - common time
				
				//either way, don't increment timeSegmentIndex so the symbol is reparsed.
				
//				if(previousStavesTimeSignature != null) {
//					staveTimeSignature = previousStavesTimeSignature;
//				} else {
//					staveTimeSignature = SymbolClass.COMMON_TIME;					
//					System.err.println("Time signature not found in score... Invalid WMN. Set to default = common time");
//				}
				
			}
			

			debug(1, "Stave Time Signature = \"" + staveLogical.getStartingTimeSignature().toString() + "\"");
			
			
			
			Clef currentClef = staveLogical.getStartingClef();
			TimeSignature currentTimeSignature = staveLogical.getStartingTimeSignature();
			
			// subsequent symbols //////////////////////////////////////////////////////////////////////////////////////
			debug(2, "Examining other symbols:");
			
			staveLogical.getTimeSegmentList()
			
			
			for (int i = timeSegmentIndex; i < l0_segment_list.size(); i++) {
				
				// current symbol //////////////
				L0_Segment l0_Segment = l0_segment_list.get(i);
				Symbol currentSymbol = l0_Segment.getSymbol();
				
				SymbolClass symClass = currentSymbol.get_symbol_class();
				SymbolType symType = currentSymbol.get_symbol_type();
				
				// previous symbol /////////////
				l0_Segment = l0_segment_list.get(i - 1); // if first symbol, will freak
				Symbol prevSymbol = l0_Segment.getSymbol();
				
				SymbolClass prevSymClass = prevSymbol.get_symbol_class();
				SymbolType prevSymType = prevSymbol.get_symbol_type();
				
				// next symbol ////////////////
				l0_Segment = l0_segment_list.get(i + 1); // if last symbol, will freak
				Symbol nextSymbol = l0_Segment.getSymbol();
				
				SymbolClass nextSymClass = nextSymbol.get_symbol_class();
				SymbolType nextSymType = nextSymbol.get_symbol_type();
				
				
				
				
				switch(symClass) {
				case TREBLE_CLEF:
					currentClef = Clef.TREBLE;
					staveLogical
					break;
					
				case COMMON_TIME:
					currentTimeSignature = SymbolClass.COMMON_TIME;
					lilypondSymbolCode = "\n\\time 4/4\n";
					break;
					
				case SHARP: 
					// either key signature or attatched to a note...
					// if attatched to a note, remember so can modify following note (will be the next symbol)
					
					break;
					
				case FLAT:
					// either key signature or attatched to a note...
					break;
					
				case NATURAL:
					// attatched to a note...
					break;
				
				case QUAVER:
					 // as same code as upside down quaver, no break; statement -> continues down to code
					
					
				case QUAVER_UPSIDEDOWN: // same as quaver
					lilypondSymbolCode = this.calculatePitch(currentSymbol, sk, currentClef) + "8 ";
					break;
					
				case CROTCHET:
					// same code below
					
				case CROTCHET_UPSIDEDOWN:
					lilypondSymbolCode = this.calculatePitch(currentSymbol, sk, currentClef) + "4 ";
					break;
					
				case MINIM:
					// same code below
					
					
				case MINIM_UPSIDEDOWN:
					lilypondSymbolCode = this.calculatePitch(currentSymbol, sk, currentClef) + "2 ";
					break;
					
				
				case SEMIBREVE:
					lilypondSymbolCode = this.calculatePitch(currentSymbol, sk, currentClef) + "1 ";
					break;
					
				
				case REST_QUAVER:
					lilypondSymbolCode = "r8 ";
					break;
					
				case REST_CROTCHET:
					lilypondSymbolCode = "r4 ";
					break;
					
				case REST_MINIM_SEMIBREVE:
					String pitchCode = this.calculatePitch(currentSymbol, sk, currentClef);
					if(pitchCode == "c'" )
					break;
					
				case BAR_LINE:

					break;
					
				case DOT:

					break;
					
				default:
					System.err.println("ERROR Unrecognised symbol");
					
					break;
				}
				
				
				
				lilypondInputString = lilypondInputString + lilypondSymbolCode;
				
			} // all time segments and symbols should have now been processed.
			
			
			lilypondInputString = lilypondInputString + "\n}\n"; // end \new Staff {
		}
		

		lilypondInputString = lilypondInputString + "\n\t} \n\t% end of whole compound music expression\n";
		
		
		String lilypondMIDIinputString = lilypondInputString + midiCode + "\n}\n";
		this.lilyPondMIDIcode = lilypondMIDIinputString;
		
		lilypondInputString = lilypondInputString + "\n}\n"; // end \new Score {
		this.lilyPondCode = lilypondInputString;
		
//		return lilypondInputString;
		
	}	
	
	
	
	
	// other private methods ///////////////////////////////////////////////////////////////////////////////////////////
	private Pitch calculatePitch(Symbol symbol, StaveSkeleton sk, Clef currentClef) {
		// which symbols have a pitch (ie vertical position has semantic meaning)? 
		//		-> accidentals, notes (and their accompanying augmenting dots) and minim/semibreve rest
		
		// simple symbol set - only treble clef so absolute pitch values easy to calculate
		
		debug(3,"Calculating pitch of symbol: " + symbol.get_symbol_class().toString());
				
		
		//staveSkeleton info: ////////////////////////////////////////////
		// lowest stave line is numbered '5', top line '1', like so:   	//
		//															   	//
		// 1 -----------											   	//
		// 2 -----------												//
		// 3 -----------												//
		// 4 -----------												//
		// 5 -----------												//
		//////////////////////////////////////////////////////////////////
		
		//Different algorithms...
		
		//Simple OpenOMR Method
		
		// 1) get lowest stave line y coordinate at this x value
		int lowestStaveLineYcoord = sk.getStaveLinePoint(5, symbol.getSemantic_centre_x());
		
		// 2) calculate how many notes away from the lowest stave line the note is
		
		// relative pitch info: (+ lilypond codes ///////////////////////////////////
		// lowest stave line numbered 1, notes upwards increase, downwards decrease
		//							treble	clef		bass clef
		//10   ---					a''
		// 9						g''
		// 8 -----------			f''
		// 7						e''
		// 6 -----------			d''
		// 5						c''
		// 4 -----------			b'
		// 3						a'
		// 2 -----------			g'
		// 1						f'
		// 0 -----------			e'
		//-1    					b'
		//-2    ---					c'
		//////////////////////////////////////////////////////////////////////////////
		// lilypond octaves - middle c -> b above middle c = 1 (')
		// 					- c below middle c -> b below middle c = 0 (no modifier)
		//					- c below c below middle c -> b below c below middle c = -1 (,)
		
		debug(4, "Lowest stave line y coord: " + lowestStaveLineYcoord);
		debug(4, "Symbol semantic centre y coord: " + symbol.getSemantic_centre_y());
		debug(4, "Stave space height + stave line height/2: " +  (float)(scoreMetrics.getStaveSpace_height() + scoreMetrics.getStaveLine_height() )/2.0);
		
		float relativePitchPreRound = (float)	( 
						(float)(lowestStaveLineYcoord - symbol.getSemantic_centre_y()) // y increases downwards
						/ 
						(float)( (float)(scoreMetrics.getStaveSpace_height() + scoreMetrics.getStaveLine_height())/2.0) 
						);
		
		int relativePitch = Math.round(relativePitchPreRound);
		int pixelError = (int)Math.round( (relativePitchPreRound - (float)relativePitch) *
				(float)(scoreMetrics.getStaveSpace_height() + scoreMetrics.getStaveLine_height())/2.0 );
				
		debug(4, "relative pitch calculation pixel error margin: " + pixelError);
		debug(4, "Relative pitch: " + relativePitch);
		
		if(currentClef != Clef.TREBLE) { // just for now
			System.out.println("Unrecognised clef - set to default (treble)");
			currentClef = Clef.TREBLE;
		}
		
		
		Pitch.Octave octave = null;
		Pitch.Note note = null;
//		Pitch.Accidental defaultAccidental = null;
		
		if(currentClef == Clef.TREBLE) {
			debug(4, "Current clef = Treble");
			
			// calculate octave ////////////////////////////////////////////////////////////////////
			int octaveNumber;
			// the following code is necessary due to using 0 (rather than relativePitch numberings going ...2,1,-1,-2...
			if(relativePitch >= -2) {
				octaveNumber = (int)Math.floor((float) ( (float)(relativePitch + 2) / (float)7.0 )) + 1;
			} else { // less than -2
				octaveNumber = (int)Math.floor((float) ( (float)(relativePitch + 3) / (float)7.0 ));
			}
			
			octave = Pitch.calculateOctaveName(octaveNumber);
			debug(4, "Octave name = " + octave.toString());
			
						
			// calculate note /////////////////////////////////////////////////////////////////////
			int noteNumber = Math.abs( (relativePitch) % 7 ); // +2 because then c = 0 (just simpler)
			note = this.calculateNote(noteNumber, currentClef);
			debug(4, "note number (relative): " + noteNumber);
			
			
//			// calculate default accidental ////////////////////////////////////////////////////////
//			defaultAccidental = Pitch.calculateDefaultAccidental(currentKey, note);
//			debug(4, "noteName: " + note);
			
		} 
		
		return new Pitch(note, octave);
		
	}

	
	
	
	private static Note calculateNote(int noteNumber, Clef clef) {
		Note noteName = null;
		
		if(clef == Clef.TREBLE) {

			switch(noteNumber) {
			case 0:
				noteName = Note.E;
				break;
			case 1:
				noteName = Note.F;
				break;
			case 2:
				noteName = Note.G;
				break;
			case 3:
				noteName = Note.A;
				break;
			case 4:
				noteName = Note.B;
				break;
			case 5:
				noteName = Note.C;
				break;
			case 6:
				noteName = Note.D;
				break;
			default:
				System.err.println("Error - couldnt calculate note pitch (specifically, which note) - input is number between 0 & 7");
				noteName = null;
				break;
			}
			
		} else if (clef == Clef.BASS){
			
			switch(noteNumber) {
			case 0:
				noteName = Note.G;
				break;
			case 1:
				noteName = Note.A;
				break;
			case 2:
				noteName = Note.B;
				break;
			case 3:
				noteName = Note.C;
				break;
			case 4:
				noteName = Note.D;
				break;
			case 5:
				noteName = Note.E;
				break;
			case 6:
				noteName = Note.F;
				break;
			default:
				System.err.println("Error - couldnt calculate note pitch (specifically, which note) - input is number between 0 & 7");
				noteName = null;
				break;
			}
		}
		
		return noteName;
	}
	
	
	
	
	private void debug(int debugLevel, String debugMessage) {
		String thisClassName = "NotationReconstructor_Simple";
		
		if(debugLevel <= this.debugLevel) {
			String indent = "";
			for(int i = debugLevel; i > 1; i--) {
				indent = indent + "\t";
			}
			
			System.out.println(thisClassName + ": DEBUG(" + debugLevel + ")\t" + indent + debugMessage);
		}
	}
	
	
}
