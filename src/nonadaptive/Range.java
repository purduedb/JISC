package nonadaptive;

import java.util.HashMap;

public class Range extends Operator{

	public double leftEdge;
	public double rightEdge;
	public double topEdge;
	public double bottomEdge;

	public Range(double leftEdge, double rightEdge, double topEdge, double bottomEdge) {
		this.leftEdge = leftEdge;
		this.rightEdge = rightEdge;
		this.bottomEdge = bottomEdge;
		this.topEdge = topEdge;
		
		state = new HashMap<Integer, Long>();
	}


	@Override
	public void process(Tuple tuple, int from) {
		
		// The following line is for eddy purposes only:
		tuple.checkedStems.set(myIndex);

		boolean qualifies = false;
		if (tuple.xCoord <= rightEdge && tuple.xCoord >= leftEdge)
			qualifies = true;
		
		
		if (state.containsKey(tuple.oID)) {
			
			// The following if statement is for adaptivity purposes only
			if (state.get(tuple.oID) <= timeStampOfLastChange) {
				tuple.isOld = true;
				if (parent.numEntriesToCompState != 0)
					tuple.isCompleting = true;	
			}
			
			
			if (qualifies) {
				tuple.sign = Tuple.Sign.UPDATE;
				state.put(tuple.oID, tuple.timeStamp); 
				parent.process(tuple, positionToParent);
				// Should Check the old and new and so on, but let's assume it changed position for now as there is a very high prob it did
			}
			else {
				tuple.sign = Tuple.Sign.NEGATIVE;
				state.remove(tuple.oID);
				parent.process(tuple, positionToParent);
			}
		}
		else {
			if (qualifies) {
				tuple.sign = Tuple.Sign.POSITIVE;
				state.put(tuple.oID, tuple.timeStamp); 
				parent.process(tuple, positionToParent);
			}
			//else {}// This tuple has no output effect. It should not propagate
		}
	}

}
