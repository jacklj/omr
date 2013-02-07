package omr.symbol_recogntion.score_metrics;

public final class Frequency_RunPairSum_Pair implements Comparable<Frequency_RunPairSum_Pair> {
	
	private final int frequency;
	private final Run_Pair_Sum runPairSum;
	
	public Frequency_RunPairSum_Pair(int freq, Run_Pair_Sum rps) {
		frequency = freq;
		runPairSum = rps;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public Run_Pair_Sum getRunPairSumObj() {
		return runPairSum;
	}

	@Override
	public int compareTo(Frequency_RunPairSum_Pair f) {
		// return negative int if the current object is less than the argument,
		// zero if the argument is equal,
		// positive int if the current object is greater than the argument
		
		if(frequency < f.frequency) {
			return -1;
		}
		else if (frequency == f.frequency) {
			return 0;
		} else {
			return 1;
		}
	}
	
	// comparators!
}
