package omr.symbol_recogntion.classifier.basicSymbolSet;

import java.awt.image.BufferedImage;

import omr.symbol_recogntion.score_metrics.ScoreMetrics;

public class Symbol {
	private int left_x_coord;
	private int right_x_coord;
	
	private int top_y_coord;
	private int bottom_y_coord;
	
	private int width;
	private int height;
	
	private int semantic_centre_x;
	private int semantic_centre_y;
	
	
	private BufferedImage symbolImage;
	
	
	private SymbolClass symbolClass;
	private SymbolType symbolType;
	
	private ScoreMetrics scoreMetrics;
	

	/// constructors ///////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Symbol(SymbolClass symbolClass, int left, int right, int top, int bottom, ScoreMetrics scoreMetrics) {
		this.scoreMetrics = scoreMetrics;
		
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
	
	
	
	public int getSemantic_centre_x() {
		return semantic_centre_x;
	}


	public int getSemantic_centre_y() {
		return semantic_centre_y;
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
	
	public SymbolClass get_symbol_class() {
		return symbolClass;
	}
	
	
	
	// private methods
	private void calculateSemanticCentre () { // only notes, accidentals and minim/semibreve rest matters
		
		if(this.symbolType == SymbolType.ACCIDENTAL) {
			if(this.symbolClass == SymbolClass.FLAT) {
				semantic_centre_x = this.left_x_coord + (int)(0.5*this.width);
				semantic_centre_y = this.top_y_coord + (int)(0.8*this.height); //this depends on y axis values increasing downwards
			} else {
				// both sharp's and natural's physical centres are their semantic centres
				semantic_centre_x = this.left_x_coord + (int)(0.5*this.width);
				semantic_centre_y = this.top_y_coord + (int)(0.5*this.height); //this depends on y axis values increasing downwards
			}
			

		} else if(this.symbolType == SymbolType.NOTE) {
			
			if(this.symbolClass == SymbolClass.QUAVER // note, right way up
			|| this.symbolClass == SymbolClass.CROTCHET
			|| this.symbolClass == SymbolClass.MINIM) 
			{
				semantic_centre_x = this.left_x_coord + (int)(0.5*this.width);
				semantic_centre_y = this.bottom_y_coord - (int)Math.round(0.5*(float)this.scoreMetrics.getNoteHead_height()); //(int)(0.85*this.height);
				
			} else { // upside down notes
				semantic_centre_x = this.left_x_coord + (int)(0.5*this.width);
				semantic_centre_y = this.top_y_coord + (int)Math.round(0.5*(float)this.scoreMetrics.getNoteHead_height()); //(int)(0.15*this.height);
			}
		
		} else {
			// all other symbols' physical centres are their semantic centres
			semantic_centre_x = this.left_x_coord + (int)(0.5*this.width);
			semantic_centre_y = this.top_y_coord + (int)(0.5*this.height); //this depends on y axis values increasing downwards

		}
	}
	
	
	
	private static SymbolType calculateSymbolType(SymbolClass symbol_class) {
		if(symbol_class == SymbolClass.FLAT 
				|| symbol_class == SymbolClass.NATURAL 
				|| symbol_class == SymbolClass.SHARP) 
		{
			return SymbolType.ACCIDENTAL;
			
		} else if(symbol_class == SymbolClass.REST_QUAVER 
				|| symbol_class == SymbolClass.REST_CROTCHET 
				|| symbol_class == SymbolClass.REST_MINIM_SEMIBREVE) 
		{
			return SymbolType.REST;
			
		} else if(symbol_class == SymbolClass.QUAVER
				|| symbol_class == SymbolClass.QUAVER_UPSIDEDOWN
				|| symbol_class == SymbolClass.CROTCHET
				|| symbol_class == SymbolClass.CROTCHET_UPSIDEDOWN
				|| symbol_class == SymbolClass.MINIM
				|| symbol_class == SymbolClass.MINIM_UPSIDEDOWN
				|| symbol_class == SymbolClass.SEMIBREVE) 
		{
			return SymbolType.NOTE;
		} else if(symbol_class == SymbolClass.TREBLE_CLEF) 
		{
			return SymbolType.CLEF;
		} else if(symbol_class == SymbolClass.DOT) {
			return SymbolType.DOT;
		} else if(symbol_class == SymbolClass.COMMON_TIME) {
			return SymbolType.TIME_SIGNATURE;
		} else if(symbol_class == SymbolClass.BAR_LINE) {
			return SymbolType.BARLINE;
		} else {
		
			// unrecognised symbol
			System.err.println("Symbol class of unrecognised type found... check SymbolClass and SymbolType");
			return null;
		}
		
	}
	
}
