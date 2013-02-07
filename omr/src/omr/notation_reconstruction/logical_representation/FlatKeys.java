package omr.notation_reconstruction.logical_representation;

public enum FlatKeys  implements KeySignature{
	

		C_MAJOR(0), 
		F_MAJOR(1),
		B_FLAT_MAJOR(2),
		E_FLAT_MAJOR(3),
		A_FLAT_MAJOR(4),
		D_FLAT_MAJOR(5),
		G_FLAT_MAJOR(6),
		C_FLAT_MAJOR(7);

		private int keyNumber;
		
		private FlatKeys(int keyNumber) {
			this.keyNumber = keyNumber;
		}

		
		public int getNumberOfFlatsInKeySig() {
			return this.keyNumber;
		}
		
		
		
		
		public static FlatKeys  getKeySignature(int numberOfFlats) {
			for (FlatKeys key : FlatKeys.values()) {
				if(numberOfFlats == key.getNumberOfFlatsInKeySig()) {
					return key;
				}
			}
			
			return null;
		}
		
		
		public Pitch[] getAccidentalsList(Clef clef) {
			if(this.keyNumber == 0) {
				return null;
			}

			Pitch[] flatsProgression = new Pitch[this.keyNumber];
			if(clef == Clef.TREBLE) {
				for(int i = 0; i < this.keyNumber; i++) {
					flatsProgression[i] = flatsProgressionTrebleClef[i];
				}
			} else if(clef == Clef.BASS) {
				for(int i = 0; i < this.keyNumber; i++) {
					flatsProgression[i] = flatsProgressionBassClef[i];
				}				
			}
			return flatsProgression;
		}
		
		public Pitch[] getAccidentalsList() { // returns default treble clef
			
			if(this.keyNumber == 0) {
				return null;
			}
			
			Pitch[] sharpsProgression = new Pitch[this.keyNumber];
			for(int i = 0; i < this.keyNumber; i++) {
				sharpsProgression[i] = flatsProgressionTrebleClef[i];
			}
			return sharpsProgression;
		}
		
	
		public static Pitch[] getAllAccidentals(Clef clef) {
			if(clef == Clef.TREBLE) {
				return flatsProgressionTrebleClef;
			} else if(clef == Clef.BASS) {
				return flatsProgressionBassClef;
				
			} else {
				return null;
			}
		}
	
	
	private static final Pitch[] flatsProgressionTrebleClef = {
		new Pitch(Pitch.Note.B, Pitch.Octave.MIDDLE_C_to_B_ABOVE_MIDDLE_C, Pitch.Accidental.FLAT),
		new Pitch(Pitch.Note.E, Pitch.Octave.C_ABOVE_MIDDLE_C_to_B_ABOVE_C_ABOVE_MIDDLE_C, Pitch.Accidental.FLAT),
		new Pitch(Pitch.Note.A, Pitch.Octave.MIDDLE_C_to_B_ABOVE_MIDDLE_C, Pitch.Accidental.FLAT),
		new Pitch(Pitch.Note.D, Pitch.Octave.C_ABOVE_MIDDLE_C_to_B_ABOVE_C_ABOVE_MIDDLE_C, Pitch.Accidental.FLAT),
		new Pitch(Pitch.Note.G, Pitch.Octave.MIDDLE_C_to_B_ABOVE_MIDDLE_C, Pitch.Accidental.FLAT),
		new Pitch(Pitch.Note.C, Pitch.Octave.C_ABOVE_MIDDLE_C_to_B_ABOVE_C_ABOVE_MIDDLE_C, Pitch.Accidental.FLAT),
		new Pitch(Pitch.Note.F, Pitch.Octave.MIDDLE_C_to_B_ABOVE_MIDDLE_C, Pitch.Accidental.FLAT)
	};	
	
	private static final Pitch[] flatsProgressionBassClef = {
		new Pitch(Pitch.Note.B, Pitch.Octave.C_BELOW_C_BELOW_MIDDLE_C_to_B_BELOW_C_BELOW_MIDDLE_C, Pitch.Accidental.FLAT),
		new Pitch(Pitch.Note.E, Pitch.Octave.C_BELOW_MIDDLE_C_to_B_BELOW_MIDDLE_C, Pitch.Accidental.FLAT),
		new Pitch(Pitch.Note.A, Pitch.Octave.C_BELOW_C_BELOW_MIDDLE_C_to_B_BELOW_C_BELOW_MIDDLE_C, Pitch.Accidental.FLAT),
		new Pitch(Pitch.Note.D, Pitch.Octave.C_BELOW_MIDDLE_C_to_B_BELOW_MIDDLE_C, Pitch.Accidental.FLAT),
		new Pitch(Pitch.Note.G, Pitch.Octave.C_BELOW_C_BELOW_MIDDLE_C_to_B_BELOW_C_BELOW_MIDDLE_C, Pitch.Accidental.FLAT),
		new Pitch(Pitch.Note.C, Pitch.Octave.C_BELOW_MIDDLE_C_to_B_BELOW_MIDDLE_C, Pitch.Accidental.FLAT),
		new Pitch(Pitch.Note.F, Pitch.Octave.C_BELOW_C_BELOW_MIDDLE_C_to_B_BELOW_C_BELOW_MIDDLE_C, Pitch.Accidental.FLAT)
	};
	
	
	

	
	
	// {"B",	"E",	"A",	"D",	"G",	"C",	"F"}; // i.e. the reverse of sharps
	
	

}
