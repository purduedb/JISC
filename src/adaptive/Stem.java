package adaptive;

import nonadaptive.*;

public class Stem extends Operator{

	private Range rangeOp;
	private Eddy eddy;

	public Stem(Range rangeOp, Eddy eddy) {
		this.rangeOp = rangeOp;
		this.eddy = eddy;
	}

	@Override
	public void process(Tuple tuple, int from) {
		if (rangeOp.state.containsKey(tuple.oID)) {
			tuple.checkedStems.set(rangeOp.myIndex);
			//eddy.process(tuple, from);
			eddy.process(tuple, rangeOp.myIndex);
		}
	}

}
