package omr.notation_reconstruction.logical_representation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Stave {
	// state ////////////////////////////////////////////////////////
	private Clef startingClef;
	private KeySignature startingKeySignature;
	private TimeSignature startingTimeSignature; // could be null
	
	private final List<TimeSegment> timeSegmentList;
//	private final Iterator iterate;
	/////////////////////////////////////////////////////////////////
	
	public Stave() {
		this.timeSegmentList = new LinkedList<TimeSegment>();
	}
	
	public Stave(List<TimeSegment> timeSegmentList) {
		this.timeSegmentList = timeSegmentList;
//		this.iterate = this.timeSegmentList.iterator();
		
	}
	
	
	public Clef getStartingClef() {
		return startingClef;
	}

	public void setStartingClef(Clef startingClef) {
		this.startingClef = startingClef;
	}

	public TimeSignature getStartingTimeSignature() {
		return startingTimeSignature;
	}

	public void setStartingTimeSignature(TimeSignature startingTimeSignature) {
		this.startingTimeSignature = startingTimeSignature;
	}

	
	public List<TimeSegment> getTimeSegmentList() {
		return timeSegmentList;
	}

	public KeySignature getStartingKeySignature() {
		return startingKeySignature;
	}

	public void setStartingKeySignature(KeySignature startingKeySignature) {
		this.startingKeySignature = startingKeySignature;
	}
	
	
//	public void getNextTimeSegment() {
////		if(this.iterate.hasNext()) {
//	}
	
}
