package omr.notation_reconstruction.logical_representation;

import java.util.List;

import omr.symbol_recogntion.classifier.basicSymbolSet.SymbolClass;

// relative pitch info: (plus each relative pitch's clef dependent pitch ///////////////////////////////////
//							treble_clef		bass_clef
//10   ---					a''					c'
// 9						g''					b
// 8 -----------			f''					a
// 7						e''					g
// 6 -----------			d''					f
// 5						c''					e
// 4 -----------			b'					d
// 3						a'					c
// 2 -----------			g'					b,
// 1						f'					a,
// 0 -----------			e'					g,
//-1    					b'					f,
//-2    ---					c'					e,
//////////////////////////////////////////////////////////////////////////////

// absolute octaves:
//  ...
// -1 : c below c below middle c - b below c below middle c
//  0 : c below middle c - b below middle c
//  1 : middle c - b above middle c
//  2 : c above middle c - b above c above middle c
//  ...



public class Pitch {
	// has note and absolute octave 
	public enum Note {
		C, D, E, F, G, A, B
	};
	
	public enum Octave {
		FOUR_Cs_BELOW_MIDDLE_C_to_B_BELOW_THREE_Cs_BELOW_MIDDLE_C(-3),
		THREE_Cs_BELOW_MIDDLE_C_to_B_BELOW_TWO_Cs_BELOW_MIDDLE_C(-2),
		C_BELOW_C_BELOW_MIDDLE_C_to_B_BELOW_C_BELOW_MIDDLE_C(-1),
		C_BELOW_MIDDLE_C_to_B_BELOW_MIDDLE_C(0),
		MIDDLE_C_to_B_ABOVE_MIDDLE_C(1),
		C_ABOVE_MIDDLE_C_to_B_ABOVE_C_ABOVE_MIDDLE_C(2),
		C_ABOVE_C_ABOVE_MIDDLE_C_to_B_ABOVE_C_ABOVE_C_ABOVE_MIDDLE_C(3),
		THREE_Cs_ABOVE_MIDDLE_C_to_B_ABOVE_THREE_Cs_ABOVE_MIDDLE_C(4);
		
		private int octaveNumber;
		
		private Octave(int octaveNumber) {
			this.octaveNumber = octaveNumber;
		}
		

		public int getOctaveNumber() {
			return this.octaveNumber;
		}
	};
	
	public enum Accidental {
		DOUBLE_FLAT, FLAT, NATURAL, SHARP, DOUBLE_SHARP
	}
	
	
	
	
	private final Note note;
	private final int octaveNumber;
	private final Octave octaveName;
	private Accidental accidental; // absolute - not dependent on key
	
	
	
	// constructors /////////////////////////////////////////
	public Pitch(Note note, int octaveNumber) {
		this.note = note;
		
		if(octaveNumber > 4 || octaveNumber < -3) {
			System.err.println("Sanity check: is octave \"" + "\" correct?");
		}
		this.octaveNumber = octaveNumber;
		
		this.octaveName = calculateOctaveName(octaveNumber);
		this.accidental = Accidental.NATURAL; //default
		
	}
	
	public Pitch(Note note, int octaveNumber, Accidental accidental) {
		this.note = note;
		
		if(octaveNumber > 4 || octaveNumber < -3) {
			System.err.println("Sanity check: is octave \"" + "\" correct?");
		}
		this.octaveNumber = octaveNumber;
		
		this.octaveName = calculateOctaveName(octaveNumber);
		this.accidental = accidental;
	}
	
	
	public Pitch(Note note, Octave octave) {
		this.note = note;
		this.octaveName = octave;
		this.octaveNumber = octave.getOctaveNumber();
		this.accidental = Accidental.NATURAL; // default
	}

	public Pitch(Note note, Octave octave, Accidental accidental) {
		this.note = note;
		this.octaveName = octave;
		this.octaveNumber = octave.getOctaveNumber();
		this.accidental = accidental;
	}
	/////////////////////////////////////////////////////////////
	
	
	public Note getNote() {
		return this.note;
	}


	public int getOctaveNum() {
		return this.octaveNumber;
	}


	public Octave getOctave() {
		return this.octaveName;
	}

	public Accidental getAccidental() {
		return this.accidental;
	}
	public void setAccidental(Accidental accidental) {
		this.accidental = accidental;
	}
	
	public void setAccidental (SymbolClass accidental) {
		if(accidental == SymbolClass.SHARP) {
			this.accidental = Accidental.SHARP;
		} else if (accidental == SymbolClass.FLAT) {
			this.accidental = Accidental.FLAT;
		} else if (accidental == SymbolClass.NATURAL) {
			this.accidental = Accidental.NATURAL;
		} else {
		
			System.err.println("Invalid accidental");
		}
	}
	
	public static Accidental calculateDefaultAccidental(KeySignature currentKey, Note note) {
		Pitch[] accidentalsList = currentKey.getAccidentalsList();
		Pitch.Accidental accidental = Accidental.NATURAL;
		for(Pitch p : accidentalsList) {
			if(p.getNote() == note) {
				accidental = p.getAccidental();
			}
		}
		
		return accidental;
		
	}
	
	public static Octave calculateOctaveName(int octaveNumber) {
		Octave octaveName = null;
		for(Octave octave : Octave.values()) {
			if(octaveNumber == octave.getOctaveNumber()) {
				octaveName = octave;
			}
		}
		return octaveName;
	}

	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj.getClass() != this.getClass()) {
			return false;
		} else {
			if (this.getNote() == ((Pitch)obj).getNote()
					&& this.getOctave() == ((Pitch)obj).getOctave()
					&& this.getAccidental() == ((Pitch)obj).getAccidental()
			) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	
	
}
