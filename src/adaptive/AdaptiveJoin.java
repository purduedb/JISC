package adaptive;

import java.util.HashMap;

import nonadaptive.*;
import nonadaptive.Tuple.Sign;

public class AdaptiveJoin extends Operator{


	public AdaptiveJoin() {

		this.state = new HashMap<Integer, Long>();
	}

	private void complete(Tuple tuple, Operator stopOp) {

		Operator op = this;
		while (true) {
			if (op.left.state.containsKey(tuple.oID) && op.right.state.containsKey(tuple.oID)) {
				op.state.put(tuple.oID, tuple.timeStamp);
//				if (op.directionCausingIncompleteness == 0) {
//					if (op.left.state.containsKey(tuple.oID)) {
//						if (op.left.state.get(tuple.oID) <= timeStampOfLastChange) {
//							op.numEntriesToCompState --;
//							op.left.state.put(tuple.oID, tuple.timeStamp);
//						} 
//					}
//				}
//				else {
//					if (op.right.state.containsKey(tuple.oID)) {
//						if (op.right.state.get(tuple.oID) <= timeStampOfLastChange) {
//							op.numEntriesToCompState --;
//							op.right.state.put(tuple.oID, tuple.timeStamp);
//						} 
//					}
//				}
			}
			else
				return;
						
			if (op == stopOp)
				return;
			op = op.parent;
		}
	}

	public void process(Tuple tuple, int from) {
		Operator oppositeOp = null;
		if (from == 0) // from left
			oppositeOp = right;
		else if (from == 1) // from right
			oppositeOp = left;

		if (tuple.sign == Tuple.Sign.POSITIVE) {
			if (oppositeOp.state.containsKey(tuple.oID)) {
				state.put(tuple.oID, tuple.timeStamp);
				parent.process(tuple, positionToParent);
			}
			else if (oppositeOp.numEntriesToCompState != 0) { // it is an incomplete state
				((AdaptiveJoin)nextComplete).complete(tuple, oppositeOp); //-1 is a signal to do double join
				if (oppositeOp.state.containsKey(tuple.oID)) {
					state.put(tuple.oID, tuple.timeStamp);
					parent.process(tuple, positionToParent);
				}
			}
		}
		else {
			if (state.containsKey(tuple.oID)) {
				if (tuple.sign == Sign.NEGATIVE) 
					state.remove(tuple.oID);
				parent.process(tuple, positionToParent);
			}
			else {
				if (tuple.isOld && numEntriesToCompState != 0) {
					if (from == directionCausingIncompleteness)
						numEntriesToCompState--;
					if (oppositeOp.state.containsKey(tuple.oID)) {
						if (tuple.sign == Sign.UPDATE) 
							state.put(tuple.oID, tuple.timeStamp);
						parent.process(tuple, positionToParent);
					}
					else if (oppositeOp.numEntriesToCompState !=0) {
						((AdaptiveJoin)nextComplete).complete(tuple, oppositeOp); //-1 is a signal to do double join
						if (oppositeOp.state.containsKey(tuple.oID)) {
							if (tuple.sign == Sign.UPDATE) 
								state.put(tuple.oID, tuple.timeStamp);
							parent.process(tuple, positionToParent);
						}
					}
				}
			}
		}
	}

}
