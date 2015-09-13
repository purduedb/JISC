package plans;

import nonadaptive.DataScan;
import nonadaptive.Join;
import nonadaptive.Operator;
import nonadaptive.OutputView;
import nonadaptive.Range;
import nonadaptive.Tuple;

public class MovingStatePlan {

	private int numStreams;
	private DataScan dataScan;
	private OutputView outputView;
	
	private Join[] join;
	
	public MovingStatePlan(int numStreams) {
		this.numStreams = numStreams;
	}
	
	public void prepare() {
		dataScan = new DataScan();
		
		Range[] range = new Range[numStreams];
		for (int i = 0; i < numStreams; i++)
			range[i] = new Range(0, 85, 85, 0);
		
		join = new Join[numStreams - 1];
		for (int i = 0; i < numStreams - 1; i++)
			join[i] = new Join();
		
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
	
	public void adaptWorst() {
		Runtime r = Runtime.getRuntime();
		r.gc();
		long time = System.currentTimeMillis(); 
		for (int i = 0; i < numStreams - 1; i++) {
			join[i].state.clear();
		}

		for (int i = 0; i < numStreams - 1; i++) {
			for (int id : join[i].left.state.keySet()) {
				if (join[i].right.state.containsKey(id))
					join[i].state.put(id, null);
			}
		}

		System.out.println(System.currentTimeMillis() - time);
		
	}
	
}
