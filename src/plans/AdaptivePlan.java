package plans;

import adaptive.AdaptiveJoin;
import nonadaptive.DataScan;
import nonadaptive.Operator;
import nonadaptive.OutputView;
import nonadaptive.Range;
import nonadaptive.Tuple;

public class AdaptivePlan {

	private int numStreams;
	private DataScan dataScan;
	private OutputView outputView;

	private Range[] range;
	private AdaptiveJoin[] join;


	public AdaptivePlan(int numStreams) {
		this.numStreams = numStreams;
	}

	public void adaptWorst() {

		for (int i = 0; i < numStreams - 2; i++) {
			//if (join[i].state.size() > 1000)System.out.println("clearing " + join[i].state.size());
			join[i].state.clear();
			join[i].directionCausingIncompleteness = 1;
			join[i].numEntriesToCompState = join[i].right.state.size();
			join[i].timeStampOfLastChange = dataScan.timeStampOfLastChange;
			join[i].nextComplete = join[0];
		}
		join[numStreams - 2].numEntriesToCompState = 0;
		join[numStreams - 2].timeStampOfLastChange = dataScan.timeStampOfLastChange;
		join[numStreams - 2].nextComplete = join[0];

		for (int i = 0; i < numStreams; i++) {
			range[i].timeStampOfLastChange = dataScan.timeStampOfLastChange;
			range[i].numEntriesToCompState = 0;
		}

	}

	public void adaptBest() {

		join[numStreams - 3].state.clear();
		join[numStreams - 3].directionCausingIncompleteness = 1;
		join[numStreams - 3].numEntriesToCompState = join[numStreams - 3].right.state.size();
		join[numStreams - 3].timeStampOfLastChange = dataScan.timeStampOfLastChange;
		join[numStreams - 2].nextComplete = join[numStreams - 4];

		for (int i = 0; i < numStreams; i++) {
			range[i].timeStampOfLastChange = dataScan.timeStampOfLastChange;
			range[i].numEntriesToCompState = 0;
		}

	}
	
	public void adaptMed() {

		join[numStreams - 3].state.clear();
		join[numStreams - 3].directionCausingIncompleteness = 1;
		join[numStreams - 3].numEntriesToCompState = join[numStreams - 3].right.state.size();
		join[numStreams - 3].timeStampOfLastChange = dataScan.timeStampOfLastChange;
		join[numStreams - 2].nextComplete = join[numStreams - 4];
		
		
		join[numStreams - 9].state.clear();
		join[numStreams - 9].directionCausingIncompleteness = 1;
		join[numStreams - 9].numEntriesToCompState = join[numStreams - 9].right.state.size();
		join[numStreams - 9].timeStampOfLastChange = dataScan.timeStampOfLastChange;
		join[numStreams - 8].nextComplete = join[numStreams - 10];

		for (int i = 0; i < numStreams; i++) {
			range[i].timeStampOfLastChange = dataScan.timeStampOfLastChange;
			range[i].numEntriesToCompState = 0;
		}

	}


	public void prepare(int selectivity) {
		dataScan = new DataScan();

		range = new Range[numStreams];
		for (int i = 0; i < numStreams; i++)
			range[i] = new Range(0, selectivity, 100, 0);

		join = new AdaptiveJoin[numStreams - 1];
		for (int i = 0; i < numStreams - 1; i++)
			join[i] = new AdaptiveJoin();

		outputView = new OutputView();

		join[0].left = range[0]; join[0].right = range[1];

		for (int i = 1; i < numStreams - 1; i++) {
			join[i].left = join[i - 1];
			join[i].right = range[i + 1];
		}

		range[0].parent = join[0];
		for (int i = 1; i < numStreams; i++)
			range[i].parent = join[i - 1];

		for (int i = 0; i < numStreams - 2; i++)
			join[i].parent = join[i + 1];

		join[numStreams - 2].parent = outputView;


		range[0].positionToParent = 0;
		for (int i = 1; i < numStreams; i++) {
			range[i].positionToParent = 1;
			join[i - 1].positionToParent = 0;
		}

		Operator[] streamTaggers = new Operator[numStreams];
		for (int i = 0; i < numStreams; i++)
			streamTaggers[i] = range[i];

		dataScan.streamTaggers = streamTaggers;
	}

	public void execute(Tuple[] tuples) {
		dataScan.getData(tuples);
		while (dataScan.hasNext())
			dataScan.process(null, 0);
	}

}
