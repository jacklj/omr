package omr.notation_reconstruction.logical_representation;

public class Note implements Figure{

	private Pitch pitch;
	private Duration duration;
	
	// if beamed
	private Note previousNote;
	private Note nextNote;
	
	//if part of chord
	private Note noteAbove;
	private Note noteBelow;
	
	// constructor /////////////////////////////////////////////////////////////////////////////////////////////////////
	public Note(Pitch pitch, Duration duration) {
		this.pitch = pitch;
		this.duration = duration;
		
		this.previousNote = null;
		this.nextNote = null;
		
		this.noteAbove = null;
		this.noteBelow = null;
	}
	
	
	
	// set methods /////////////////////////////////////////////////////////////////////////////////////////////////////
	public void setPreviousBeamedNote(Note previousNote) {
		this.previousNote = previousNote;
	}


	public void setNextBeamedNote(Note nextNote) {
		this.nextNote = nextNote;
	}



	public void setNoteAbove(Note noteAbove) {
		this.noteAbove = noteAbove;
	}



	public void setNoteBelow(Note noteBelow) {
		this.noteBelow = noteBelow;
	}
	
	
	// get methods /////////////////////////////////////////////////////////////////////////////////////////////////////
	public Note getPreviousBeamedNote() {
		return previousNote;
	}

	public Note getNextBeamedNote() {
		return nextNote;
	}
	
	public Note getNoteAbove() {
		return noteAbove;
	}

	public Note getNoteBelow() {
		return noteBelow;
	}
	
	
	
	public Pitch getPitch() {
		return pitch;
	}

	
	public Duration getDuration() {
		return duration;
	}
	
	public float getDurationValue() {
		return duration.getDurationValue();
	}

	

	
	
}
