package omr.symbol_recogntion.classifier.complexSymbolSet;

public enum SymbolClass {
	TREBLE_CLEF(1),
//	BASS_CLEF,
	
	COMMON_TIME(2),
	
	SHARP(3),
	FLAT(4),
	NATURAL(5),
	
	QUAVER(6),
	QUAVER_UPSIDEDOWN(7),
	CROTCHET(8),
	
	CROTCHET_UPSIDEDOWN(9),
	MINIM(10),
	MINIM_UPSIDEDOWN(11),
	
	SEMIBREVE(12),
	
	REST_QUAVER(13),
	REST_CROTCHET(14),
	REST_MINIM_SEMIBREVE(15),
	
	
//	FILLED_NOTE_HEAD, 
//	UNFILLED_NOTE_HEAD,
//	SEMIBREVE, // different to UNFILLED_NOTE_HEAD?
//	
//	
//	BEAM_SECTION,
//	NOTE_STEM_FLAG_POINTING_DOWN,
//	NOTE_STEM_FLAG_POINTING_UP,
	
//	SEMIQUAVER_REST,
//	QUAVER_REST,
//	CROCHET_REST,
//	MINIM_REST_OR_SEMIBREVE_REST,
//	
//	STACCATO_DOTTED_DOT_BASS_CLEF_COLON_DOT,
	
	BAR_LINE(16),
//	DOUBLE_BAR_LINE_SECOND_BAR,
	
	DOT(17);
	
    private int symbolNumber;

    private SymbolClass(int symbolNumber) {
            this.symbolNumber = symbolNumber;
    }


}
