package omr.symbol_recogntion.classifier.complexSymbolSet;

import java.awt.image.BufferedImage;

public class Symbol {
	private int left_x_coord;
	private int right_x_coord;
	
	private int top_y_coord;
	private int bottom_y_coord;
	
	private int width;
	private int height;
	
	private int semantic_centre_x;
	private int semantic_centre_y;
	
	
	private SymbolClass symbolClass;
	private SymbolType symbolType;
	

	/// constructors ///////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Symbol(SymbolClass symbolClass, int left, int right, int top, int bottom) {
		this.symbolClass = symbolClass;
		this.symbolType = calculateSymbolType(symbolClass);
		
		left_x_coord = left;
		right_x_coord = right;
		top_y_coord = top;
		bottom_y_coord = bottom;
		
		width = Math.abs(right_x_coord - left_x_coord + 1);
		height = Math.abs(bottom_y_coord - top_y_coord + 1);
		
		this.calculateSemanticCentre(); // sets semantic_centre_x and semantic_centre_y
		
	}	
	
	
	/// get methods ////////////////////////////////////////////////////////////////////////////////////////////////
	public void set_left_x(int left) {
		left_x_coord = left;
	}
	public int get_left_x() {
		return left_x_coord;
	}
	
	
	public void set_right_x(int right) {
		right_x_coord = right;
	}
	public int get_right_x() {
		return right_x_coord;
	}
	
	
	public void set_top_y(int top) {
		top_y_coord = top;
	}
	public int get_top_y() {
		return top_y_coord;
	}
	
	
	public void set_bottom_y(int bottom) {
		bottom_y_coord = bottom;
	}
	public int get_bottom_y() {
		return bottom_y_coord;
	}	
	
	
	public SymbolClass get_symbol_class() {
		return symbolClass;
	}	
	
	
//	public void set_symbol_type(SymbolType symbol_type) {
//		symbol_type = symbol_type;
//		
//		//then must recalculate Symbol Class
//		symbol_class = calculateSymbolClass(symbol_type);
//		
//	}
	
	public SymbolType get_symbol_type() {
		return symbolType;
	}
	

	
	
	
	// private methods
	private void calculateSemanticCentre () {
		//e.g.
		if(this.symbolType == SymbolType.ACCIDENTAL) {
			if(this.symbolClass == SymbolClass.FLAT) {
				semantic_centre_x = this.left_x_coord + (int)0.5*(this.width);
				semantic_centre_y = this.top_y_coord + (int)0.8*(this.height); //this depends on y axis values increasing downwards
			} else {
				// both sharp's and natural's physical centres are their semantic centres
				semantic_centre_x = this.left_x_coord + (int)0.5*(this.width);
				semantic_centre_y = this.top_y_coord + (int)0.5*(this.height); //this depends on y axis values increasing downwards
			}
			

		} else { // for Basic Symbol set, notes are primitives, so need to calculate semantic centre for these
			// all other symbols' physical centres are their semantic centres
			semantic_centre_x = this.left_x_coord + (int)0.5*(this.width);
			semantic_centre_y = this.top_y_coord + (int)0.5*(this.height); //this depends on y axis values increasing downwards

		}
	}
	
	
	
	private static SymbolType calculateSymbolType(SymbolClass symbol_class) {
		if(symbol_class == SymbolClass.FLAT 
				|| symbol_class == SymbolClass.NATURAL 
				|| symbol_class == SymbolClass.SHARP) {
			return SymbolType.ACCIDENTAL;
			
		} else if(symbol_class == SymbolClass.SEMIQUAVER_REST 
				|| symbol_class == SymbolClass.QUAVER_REST 
				|| symbol_class == SymbolClass.CROCHET_REST 
				|| symbol_class == SymbolClass.MINIM_REST_OR_SEMIBREVE_REST) {
			return SymbolType.REST;
			
		} else if(symbol_class == SymbolClass.FILLED_NOTE_HEAD
				|| symbol_class == SymbolClass.UNFILLED_NOTE_HEAD) {
			return SymbolType.NOTEHEAD;
		} else if(symbol_class == SymbolClass.TREBLE_CLEF
				|| symbol_class == SymbolClass.BASS_CLEF) {
			return SymbolType.CLEF;
		} else if(symbol_class == SymbolClass.STACCATO_DOTTED_DOT_BASS_CLEF_COLON_DOT) {
			return SymbolType.DOT;
		} else {
			return SymbolType.OTHER;
		}
		
	}
	
}
