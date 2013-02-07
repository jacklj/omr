package omr.notation_reconstruction.logical_representation;

import omr.notation_reconstruction.logical_representation.Duration.BasicDuration;

public class TimeSignature implements Figure {
	
	public enum Name {
		COMMON_TIME,
		
	}
	
//	enum BOTTOM_NUMBER {
//		1,2,4,8,16,32
//	}
	private int topNumber;
	private int bottomNumber;
	
	private Name name;
	
	
	// constructors ////////////////////////////////////////////////////////////////////////////////////////////////////
	public TimeSignature(int topNumber, int bottomNumber) {
		this.setTopNumber(topNumber);
		this.setBottomNumber(bottomNumber);
		
		this.name = calculateAlternateName(topNumber, bottomNumber);
		
	}
	
	public TimeSignature(Name name) {
		this.name = name;
		calculateNumberForm(name); //sets top and bottom numbers
	}
	
	
	
	// public methods //////////////////////////////////////////////////////////////////////////////////////////////////
	public int getTopNumber() {
		return topNumber;
	}

	public int getBottomNumber() {
		return bottomNumber;
	}

	public Name getName() {
		return name;
	}
	
	
	
	// private methods /////////////////////////////////////////////////////////////////////////////////////////////////
	private static Name calculateAlternateName(int topNumber, int bottomNumber) {
		if(topNumber == 4 && bottomNumber == 4) {
			return Name.COMMON_TIME;
		} else {
			return null;
		}
	}
	
	private void calculateNumberForm(Name name) {
		if(name == Name.COMMON_TIME) {
			this.setTopNumber(4);
			this.setBottomNumber(4);
		}
	}
	
	
	// private set methods /////////////////////////////////////////////////////////////////////////////////////////////
	private void setBottomNumber(int bottomNumber) {
		boolean validBottomNumber = false;

		for(BasicDuration duration : Duration.BasicDuration.values()) {

			if((float)bottomNumber == duration.getDurationValue()) {
				validBottomNumber = true;
			}
		}
		
		if(validBottomNumber) {
			this.bottomNumber = bottomNumber;
		} else {
			System.err.println("Invalid lower number in time signature - set to default: 4");
			this.bottomNumber = 4;
		}
	}
	
	
	private void setTopNumber(int topNumber) {
		if(topNumber <= 0) {
			System.err.println("Top number in time signature can't be <= 0 - set to default(4)") ;
			this.topNumber = 4;
		} else {
			this.topNumber = topNumber;
		}
	}


	
}
