package nonadaptive;

import java.util.ArrayList;
import java.util.HashMap;

import nonadaptive.Tuple.Sign;

public class OutputView extends Operator {
	
	public ArrayList<Tuple> allTuples;
	
	private boolean correct = true; // To be removed later
	
	public OutputView() {
				
		state = new HashMap<Integer, Long>();
		allTuples = new ArrayList<Tuple>();
	}

	@Override
	public void process(Tuple tuple, int from) { // from is useless here
		//allTuples.add(tuple);
		
		if (tuple.sign == Sign.POSITIVE) {
			if (state.containsKey(tuple.oID) && correct) {System.out.println("Warning ID exists and a +ve is received for it"); correct=false;}
			state.put(tuple.oID, tuple.timeStamp);
		}
		else if (tuple.sign == Sign.UPDATE) {
			if (!state.containsKey(tuple.oID) && correct) {System.out.println("Warning ID doesn not exists and a U is received for it"); correct=false;}
			state.put(tuple.oID, tuple.timeStamp);
		}
			
		else {// Negative
			if (!state.containsKey(tuple.oID) && correct) {System.out.println("Warning ID doesn not exists and a -ve is received for it"); correct=false;}
			state.remove(tuple.oID);
		}
	}

}
