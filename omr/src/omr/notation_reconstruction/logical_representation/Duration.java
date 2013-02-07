package omr.notation_reconstruction.logical_representation;

public class Duration {
	enum BasicDuration {
		DEMISEMIQUAVER(32),
		SEMIQUAVER(16),
		QUAVER(8),
		CROTCHET(4),
		MINIM(2),
		SEMIBREVE(1),
		BREVE((float)0.5);
		
		private float durationValue;
		private BasicDuration(float durationNumber) {
			this.durationValue = durationNumber;
		}
		public float getDurationValue() {
			return this.durationValue;
		}
	}

	private BasicDuration duration;
	private int augmentationDots;
	
	public Duration(BasicDuration duration) {
		this.duration = duration;
		this.augmentationDots = 0;
	}

	public Duration(BasicDuration duration, int augmentationDots) {
		this.duration = duration;
		this.setAugmentationDots(augmentationDots);
		
	}
	
	public void setAugmentationDots(int augmentationDots) {
		if(augmentationDots < 0) {
			System.err.println("Can't have a negative number of augmentation dots");
			this.augmentationDots = 0;
		} else if (augmentationDots > 4) {
			System.err.println("Unusual to have " + augmentationDots + " augmentation dots");
			this.augmentationDots = augmentationDots;
		} else {
			this.augmentationDots = augmentationDots;	
		}
	}
	
	
	public BasicDuration getBasicDuration() {
		return this.duration;
	}
	
	public int getNumberOfAugmentationDots() {
		return this.augmentationDots;
	}
	
	
	public float getDurationValue() {
		if(this.augmentationDots == 0) {
			return this.duration.getDurationValue();
		} else {

			float durationMultiplier = 1;
			for(int i = this.augmentationDots; i > 0; i--) {
				durationMultiplier = durationMultiplier - (float)(1.0/(float)Math.pow(2.0, i));
			}
			
			return this.duration.getDurationValue() * durationMultiplier;
		}
	}
	
	
	
}
