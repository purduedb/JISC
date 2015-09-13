package nonadaptive;

import java.util.HashMap;

public class Join extends Operator{
	
	public Join() {

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
				state.put(tuple.oID, tuple.timeStamp);
				parent.process(tuple, positionToParent);
			}
			// else do nothing
			
		}

	}

}
