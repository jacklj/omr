package omr.symbol_recogntion.score_metrics;

// INFO ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//1. this class is necessary because we need to remember the 2 run integers (and their order) which summed to produce a 
//	 run pair sum.
//
//2. we also need to then be able to compare these objects for equality by comparing their state -> we compare 
//	 first_int, second_int and sum.
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


public class Run_Pair_Sum {
	
	private int first_run;
	private int second_run;
	private int run_pair_sum;
	
	public Run_Pair_Sum(int first, int second) {
		first_run = first;
		second_run = second;
		run_pair_sum = first+second;
	}
	
	public int get_first_run() {
		return first_run;
	}
	
	public int get_second_run() {
		return second_run;
	}
	
	public int get_run_pair_sum() {
		return run_pair_sum;
	}
	

	@Override
	public boolean equals(Object o) { ////thats the problem - not overriding equals for some reason....
		
	    if ( this == o ) return true;
	    
	    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	    // from http://www.javapractices.com/topic/TopicAction.do?Id=17
	    // use instanceof instead of getClass here for two reasons
	    //1. if need be, it can match any supertype, and not just one class;
	    //2. it renders an explict check for "that == null" redundant, since
	    //it does the check for null already - "null instanceof [type]" always
	    //returns false. (See Effective Java by Joshua Bloch.)
	    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	    
	    if ( !(o instanceof Run_Pair_Sum) ) return false;
	    //Alternative to the above line :
	    //if ( aThat == null || aThat.getClass() != this.getClass() ) return false;

	    //cast to native object is now safe
	    Run_Pair_Sum other = (Run_Pair_Sum)o;
	    
	    // now do field by field state comparison
		if(
				(other.get_first_run() == this.first_run) && 
				(other.get_second_run() == this.second_run) && 
				(other.get_run_pair_sum() == this.run_pair_sum)
		) {
			return true;
		} else {
			return false;
		}
	}
	
	
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + first_run;
        hash = hash * 31 + second_run;
        hash = hash * 13 + run_pair_sum;
        return hash;
    }
    
    
	public void print() {
		System.out.print(first_run + "\t+ " + second_run + "\t= " + run_pair_sum);
	}

}
