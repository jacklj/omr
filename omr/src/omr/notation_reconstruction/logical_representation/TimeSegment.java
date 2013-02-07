package omr.notation_reconstruction.logical_representation;

import java.util.ArrayList;
import java.util.List;

public class TimeSegment {
	private List<Figure> symbols;
	
	
//	public static void main(String[] args) {
//		List<Symbol> sList = new ArrayList<Symbol>();
//		sList.add(Clef.TREBLE);
//		
//		TimeSegment ts = new TimeSegment(sList);
//		
//	}
	
	
	
	public TimeSegment(List<Figure> symbols) {
		this.symbols = symbols;
	}
	
	public List<Figure> getSymbolList() {
		return this.symbols;
	}
	
}
