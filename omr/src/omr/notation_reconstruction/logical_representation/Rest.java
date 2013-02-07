package omr.notation_reconstruction.logical_representation;

public class Rest implements Figure{
	private Duration duration;
	
	public Rest(Duration duration) {
		this.duration = duration;
	}
	
	public Duration getDuration() {
		return this.duration;
	}
	
	public float getDurationValue() {
		return this.duration.getDurationValue();
	}
	
}
