package elke;

import java.util.HashMap;

import nonadaptive.Operator;
import nonadaptive.Tuple;

public class PRange extends Operator{

	public double leftEdge;
	public double rightEdge;
	public double topEdge;
	public double bottomEdge;

	public PRange(double leftEdge, double rightEdge, double topEdge, double bottomEdge) {
		this.leftEdge = leftEdge;
		this.rightEdge = rightEdge;
		this.bottomEdge = bottomEdge;
		this.topEdge = topEdge;
		
		state = new HashMap<Integer, Long>();
		oldPlanDiscarded = true;
	}
	
	public boolean oldPlanDiscarded;
	public int positionToOldParent;


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
				if (!oldPlanDiscarded) {
					oldParent.process(tuple, positionToOldParent);
				}
				// Should Check the old and new and so on, but let's assume it changed position for now as there is a very high prob it did
			}
			else {
				tuple.sign = Tuple.Sign.NEGATIVE;
				state.remove(tuple.oID);
				parent.process(tuple, positionToParent);
				if (!oldPlanDiscarded) {
					oldParent.process(tuple, positionToOldParent);
				}
			}
		}
		else {
			if (qualifies) {
				tuple.sign = Tuple.Sign.POSITIVE;
				state.put(tuple.oID, tuple.timeStamp); 
				parent.process(tuple, positionToParent);
				if (!oldPlanDiscarded) {
					oldParent.process(tuple, positionToOldParent);
				}
			}
			//else: This tuple has no output effect. It should not propagate
		}
	}

}
