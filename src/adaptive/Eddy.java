package adaptive;

import nonadaptive.Operator;
import nonadaptive.OutputView;
import nonadaptive.Tuple;

public class Eddy extends Operator {

	public Operator[] stems;
	private OutputView opView;

	public Eddy(OutputView opView) {
		this.opView = opView;
	}

	@Override
	public void process(Tuple tuple, int from) {
		if (tuple.checkedStems.cardinality() == stems.length) {
			//System.out.println(tuple.checkedStems.cardinality());
			//System.out.println(tuple.checkedStems);
			opView.process(tuple, 0);
		}
		else {
			//from = (from+1) % stems.length;
			from++; if (from == stems.length) from = 0;
			stems[from].process(tuple, from);
//			for (int i = 0; i < stems.length; i++)
//				if (!tuple.checkedStems.get(i)) {
//					stems[i].process(tuple, i);
//					return;
//				}
		}
	}

}
