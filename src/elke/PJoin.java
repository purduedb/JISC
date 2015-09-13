package elke;

import java.util.HashMap;

import nonadaptive.Operator;
import nonadaptive.Tuple;

public class PJoin extends Operator{
	
	public boolean updatesShouldAddToState; 
	
	public PJoin() {

		this.state = new HashMap<Integer, Long>();
	}

	
	@Override
	public void process(Tuple tuple, int from) {

		Operator oppositeOp;
		if (from == 0) // from left
			oppositeOp = right;
		else // from right
			oppositeOp = left;
		
		
		if (tuple.sign == Tuple.Sign.POSITIVE) {
			if (oppositeOp.state.containsKey(tuple.oID)) {
				state.put(tuple.oID, tuple.timeStamp);

				parent.process(tuple, positionToParent);
			}
			// else do nothing
		}
		else if (tuple.sign == Tuple.Sign.NEGATIVE) {
			if (state.containsKey(tuple.oID)) {
				state.remove(tuple.oID);
				
				parent.process(tuple, positionToParent);
			}
			// else do nothing
				
		}
		else { // UPDATE
			if (state.containsKey(tuple.oID)) {
				if (updatesShouldAddToState) state.put(tuple.oID, tuple.timeStamp);
				parent.process(tuple, positionToParent);
			}
			else if(updatesShouldAddToState){
				if (oppositeOp.state.containsKey(tuple.oID)) {
					state.put(tuple.oID, tuple.timeStamp);
					parent.process(tuple, positionToParent);
				}
			}
			// else do nothing
			
		}

	}

}
