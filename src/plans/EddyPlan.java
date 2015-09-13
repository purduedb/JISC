package plans;

import adaptive.Eddy;
import adaptive.Stem;
import nonadaptive.DataScan;
import nonadaptive.Operator;
import nonadaptive.OutputView;
import nonadaptive.Range;
import nonadaptive.Tuple;

public class EddyPlan {

	private int numStreams;
	private DataScan dataScan;
	private OutputView outputView;
	
	public EddyPlan(int numStreams) {
		this.numStreams = numStreams;
	}
	
	public void prepare(int selectivity) {
		dataScan = new DataScan();
		
		Range[] range = new Range[numStreams];
		for (int i = 0; i < numStreams; i++)
			range[i] = new Range(0, selectivity, 100, 0);
			
		outputView = new OutputView();
		
		Eddy eddy = new Eddy(outputView);
		
		Stem[] stems = new Stem[numStreams];
		for (int i = 0; i < numStreams; i++)
			stems[i] = new Stem(range[i], eddy);
		
		eddy.stems = stems;
		
		Operator[] streamTaggers = new Operator[numStreams];
		for (int i = 0; i < numStreams; i++) {
			streamTaggers[i] = range[i];
			range[i].parent = eddy;
			range[i].myIndex = i;
			range[i].positionToParent = i;
		}

		dataScan.streamTaggers = streamTaggers;
	}
	
	public void execute(Tuple[] tuples) {
		dataScan.getData(tuples);
		while (dataScan.hasNext())
			dataScan.process(null, 0);
	}

}
