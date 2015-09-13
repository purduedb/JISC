package nonadaptive;

import java.util.HashMap;

public abstract class Operator {
	
	public Operator left; // for join or child of Range 
	public Operator right;
	
	public Operator parent;
		
	public HashMap<Integer, Long> state; // ID, timeStamp
	
	public Operator[] streamTaggers;
	
	public int positionToParent; // 0 = left, 1 = right
	
	public abstract void process(Tuple tuple, int from); // from indicates which of the children is the source of the tuple. Useful only for join
	
	
	// The following is used for adaptivity purposes ONLY
	public Operator nextComplete;
	public int numEntriesToCompState; // a 0 value indicates a complete (valid) state
	
	public long timeStampOfLastChange;
	public int directionCausingIncompleteness; // 0 for left, 1 for right
	
	
	// The following is for eddy purposes only:
	public int myIndex;
	
	
	// The following is used for Parallel Track strategy only
	public Operator oldParent;
	
}
