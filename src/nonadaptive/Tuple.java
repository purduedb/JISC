package nonadaptive;

import java.util.BitSet;

public class Tuple {
	
	public enum Sign{
		POSITIVE,
		NEGATIVE,
		UPDATE
	};
	
	public Sign sign;
	
	public Tuple(int schema) {
		
		// For adaptivity purposes
		isCompleting = false; isOld = false;
		
		// For Eddy purposes
		checkedStems = new BitSet(schema);
		
	}
	
	
	
	// As a raw tuple
	public int oID;
	public int xCoord;
	//public double yCoord;
	public long timeStamp;
	public int streamIndex;
	
	// For adaptivity purposes:
	public boolean isCompleting; 
	public boolean isOld;
	
	// For Eddy purposes:
	public BitSet checkedStems;
	
	// For Parallel Track Strategy only
	public boolean toBeProcessedInOldPlan;
}
