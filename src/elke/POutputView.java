package elke;

import java.util.ArrayList;
import java.util.HashMap;

import nonadaptive.Operator;
import nonadaptive.Tuple;
import nonadaptive.Tuple.Sign;

public class POutputView extends Operator {

	public ArrayList<Tuple> allTuples;

	private boolean correct = true; // To be removed later

	private long lastTS;

	public POutputView() {

		state = new HashMap<Integer, Long>();
		allTuples = new ArrayList<Tuple>();

		lastTS = -1;
	}

	@Override
	public void process(Tuple tuple, int from) { // from is useless here

		if (lastTS == tuple.timeStamp) {
			//System.out.println("prevented a duplication");
			return;
		}			

		lastTS = tuple.timeStamp;


		//allTuples.add(tuple);

		if (tuple.sign == Sign.POSITIVE) {
			if (state.containsKey(tuple.oID) && correct) {System.out.println("Warning ID exists and a +ve is received for it "); correct=false;}
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
