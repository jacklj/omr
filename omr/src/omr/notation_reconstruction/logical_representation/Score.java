package omr.notation_reconstruction.logical_representation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Score {
	private String title;
	private TimeSignature currentTimeSignature;
	private final Iterator iterate;
	
	private final List<Stave> staveList;

	public Score() {
		this.staveList = new LinkedList<Stave>();
		this.iterate = this.staveList.iterator();
	}
	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public TimeSignature getCurrentTimeSignature() {
		return currentTimeSignature;
	}

	public void setCurrentTimeSignature(TimeSignature currentTimeSignature) {
		this.currentTimeSignature = currentTimeSignature;
	}

	public List<Stave> getStaveList() {
		return staveList;
	}

	public void addStave(Stave stave) {
		this.staveList.add(stave);
	}
	
	public Stave getStave(int staveNumber) {
		if(staveNumber <= 0 || staveNumber > this.staveList.size()) {
			System.err.println("getStave called with non existant stave number");
			return null;
		}
		return this.staveList.get(staveNumber-1);
	}
	
	public void setStave(int staveNumber, Stave stave) {
		if(staveNumber <= 0 || staveNumber > this.staveList.size() + 1) {
			System.err.println("getStave called with non existant stave number (ie 0 or " +
					"less, or more than current number of staves + 1");
		}
		this.staveList.add(staveNumber, stave);
	}
	
	public Stave newStave() {
		Stave stave = new Stave();
		this.staveList.add(stave);
		return stave;
	}
	
}
