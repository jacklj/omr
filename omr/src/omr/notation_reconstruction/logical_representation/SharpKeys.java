package omr.notation_reconstruction.logical_representation;

public enum SharpKeys implements KeySignature{
		C_MAJOR(0), 
		G_MAJOR(1),
		D_MAJOR(2),
		A_MAJOR(3),
		E_MAJOR(4),
		B_MAJOR(5),
		F_SHARP_MAJOR(6),
		C_SHARP_MAJOR(7);
		
		private int keyNumber;
		
		private SharpKeys(int keyNumber) {
			this.keyNumber = keyNumber;
		}
		
		public int getNumberOfSharpsInKeySig() {
			return this.keyNumber;
		}
		
		public Pitch[] getAccidentalsList(Clef clef) {
			if(this.keyNumber == 0) {
				return null;
			}
			
			Pitch[] sharpsProgression = new Pitch[this.keyNumber];
			if(clef == Clef.TREBLE) {
			for(int i = 0; i < this.keyNumber; i++) {
				sharpsProgression[i] = sharpsProgressionTrebleClef[i];
			}
			} else if(clef == Clef.BASS) {
				for(int i = 0; i < this.keyNumber; i++) {
					sharpsProgression[i] = sharpsProgressionBassClef[i];
				}	
			}
			return sharpsProgression;
		}
		
		public Pitch[] getAccidentalsList() { // returns default treble clef ones...
			
			if(this.keyNumber == 0) {
				return null;
			}
			
			Pitch[] sharpsProgression = new Pitch[this.keyNumber];
			for(int i = 0; i < this.keyNumber; i++) {
				sharpsProgression[i] = sharpsProgressionTrebleClef[i];
			}
			return sharpsProgression;
		}
	
	
		public static Pitch[] getAllAccidentals(Clef clef) {
			if(clef == Clef.TREBLE) {
				return sharpsProgressionTrebleClef;
			} else if(clef == Clef.BASS) {
				return sharpsProgressionBassClef;
				
			} else {
				return null;
			}
		}
		
		
	
	public static SharpKeys  getKeySignature(int numberOfSharps) {
		for (SharpKeys key : SharpKeys.values()) {
			if(numberOfSharps == key.getNumberOfSharpsInKeySig()) {
				return key;
			}
		}
		
		return null;
	}
		

	private static final Pitch[] sharpsProgressionTrebleClef = {
		new Pitch(Pitch.Note.F, Pitch.Octave.C_ABOVE_MIDDLE_C_to_B_ABOVE_C_ABOVE_MIDDLE_C, Pitch.Accidental.SHARP),
		new Pitch(Pitch.Note.C, Pitch.Octave.C_ABOVE_MIDDLE_C_to_B_ABOVE_C_ABOVE_MIDDLE_C, Pitch.Accidental.SHARP),
		new Pitch(Pitch.Note.G, Pitch.Octave.C_ABOVE_MIDDLE_C_to_B_ABOVE_C_ABOVE_MIDDLE_C, Pitch.Accidental.SHARP),
		new Pitch(Pitch.Note.D, Pitch.Octave.C_ABOVE_MIDDLE_C_to_B_ABOVE_C_ABOVE_MIDDLE_C, Pitch.Accidental.SHARP),
		new Pitch(Pitch.Note.A, Pitch.Octave.MIDDLE_C_to_B_ABOVE_MIDDLE_C, Pitch.Accidental.SHARP),
		new Pitch(Pitch.Note.E, Pitch.Octave.C_ABOVE_MIDDLE_C_to_B_ABOVE_C_ABOVE_MIDDLE_C, Pitch.Accidental.SHARP),
		new Pitch(Pitch.Note.B, Pitch.Octave.MIDDLE_C_to_B_ABOVE_MIDDLE_C, Pitch.Accidental.SHARP),
	};
	
	
	// "C", "G", "D", "A", "E", "B"};
	
	public static final Pitch[] sharpsProgressionBassClef = {
		new Pitch(Pitch.Note.F, Pitch.Octave.C_BELOW_MIDDLE_C_to_B_BELOW_MIDDLE_C, Pitch.Accidental.SHARP),
		new Pitch(Pitch.Note.C, Pitch.Octave.C_BELOW_MIDDLE_C_to_B_BELOW_MIDDLE_C, Pitch.Accidental.SHARP),
		new Pitch(Pitch.Note.G, Pitch.Octave.C_BELOW_MIDDLE_C_to_B_BELOW_MIDDLE_C, Pitch.Accidental.SHARP),
		new Pitch(Pitch.Note.D, Pitch.Octave.C_BELOW_MIDDLE_C_to_B_BELOW_MIDDLE_C, Pitch.Accidental.SHARP),
		new Pitch(Pitch.Note.A, Pitch.Octave.C_BELOW_C_BELOW_MIDDLE_C_to_B_BELOW_C_BELOW_MIDDLE_C, Pitch.Accidental.SHARP),
		new Pitch(Pitch.Note.E, Pitch.Octave.C_BELOW_MIDDLE_C_to_B_BELOW_MIDDLE_C, Pitch.Accidental.SHARP),
		new Pitch(Pitch.Note.B, Pitch.Octave.C_BELOW_C_BELOW_MIDDLE_C_to_B_BELOW_C_BELOW_MIDDLE_C, Pitch.Accidental.SHARP),
	};	
	
	
	
}
