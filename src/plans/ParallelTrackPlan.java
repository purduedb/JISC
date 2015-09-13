package plans;

import elke.*;

import nonadaptive.*;

public class ParallelTrackPlan {

	private int numStreams;
	private DataScan dataScan;
	private POutputView outputView;

	private PRange[] range;
	private PJoin[] oldJoin;
	private PJoin[] cuurentJoin;

	private long timeStampOfSwitch;
	private boolean oldDiscarded;

	private int intervalCount;

	public ParallelTrackPlan(int numStreams) {
		this.numStreams = numStreams;
		intervalCount = 0;
		oldDiscarded = true;
	}

	public void adapt() {
		timeStampOfSwitch = dataScan.timeStampOfLastChange;
		if (!oldDiscarded)
			return;
		
		for (int i = 0; i < numStreams - 1; i++) {
			oldJoin[i] = cuurentJoin[i];
		}
		range[0].oldParent = oldJoin[0];
		range[0].positionToOldParent = 0;
		range[0].oldPlanDiscarded = false;
		for (int i = 1; i < numStreams; i++) {
			range[i].oldParent = oldJoin[i - 1];
			range[i].positionToOldParent = 1;
			range[i].oldPlanDiscarded = false;
		}

		cuurentJoin = new PJoin[numStreams - 1];
		for (int i = 0; i < numStreams - 1; i++) {
			cuurentJoin[i] = new PJoin();
			cuurentJoin[i].updatesShouldAddToState = true;
		}

		cuurentJoin[0].left = range[0]; cuurentJoin[0].right = range[1];

		for (int i = 1; i < numStreams - 1; i++) {
			cuurentJoin[i].left = cuurentJoin[i - 1];
			cuurentJoin[i].right = range[i + 1];
		}

		range[0].parent = cuurentJoin[0];
		for (int i = 1; i < numStreams; i++)
			range[i].parent = cuurentJoin[i - 1];

		for (int i = 0; i < numStreams - 2; i++)
			cuurentJoin[i].parent = cuurentJoin[i + 1];

		cuurentJoin[numStreams - 2].parent = outputView;


		range[0].positionToParent = 0;
		for (int i = 1; i < numStreams; i++) {
			range[i].positionToParent = 1;
			cuurentJoin[i - 1].positionToParent = 0;
		}

		oldDiscarded = false;

	}
		
	public void prepare(int selectivity) {
		dataScan = new DataScan();

		range = new PRange[numStreams];
		for (int i = 0; i < numStreams; i++)
			range[i] = new PRange(0, selectivity, 100, 0);

		cuurentJoin = new PJoin[numStreams - 1];
		oldJoin = new PJoin[numStreams - 1];
		for (int i = 0; i < numStreams - 1; i++) {
			cuurentJoin[i] = new PJoin();
			oldJoin[i] = new PJoin();
		}

		outputView = new POutputView();

		cuurentJoin[0].left = range[0]; cuurentJoin[0].right = range[1];

		for (int i = 1; i < numStreams - 1; i++) {
			cuurentJoin[i].left = cuurentJoin[i - 1];
			cuurentJoin[i].right = range[i + 1];
		}

		range[0].parent = cuurentJoin[0];
		for (int i = 1; i < numStreams; i++)
			range[i].parent = cuurentJoin[i - 1];

		for (int i = 0; i < numStreams - 2; i++)
			cuurentJoin[i].parent = cuurentJoin[i + 1];

		cuurentJoin[numStreams - 2].parent = outputView;


		range[0].positionToParent = 0;
		for (int i = 1; i < numStreams; i++) {
			range[i].positionToParent = 1;
			cuurentJoin[i - 1].positionToParent = 0;
		}

		Operator[] streamTaggers = new Operator[numStreams];
		for (int i = 0; i < numStreams; i++)
			streamTaggers[i] = range[i];

		dataScan.streamTaggers = streamTaggers;
	}

	public void execute(Tuple[] tuples) {
		dataScan.getData(tuples);
		while (dataScan.hasNext()) {
			dataScan.process(null, 0);

			
			if (!oldDiscarded) {
				intervalCount = (intervalCount + 1);
				if (intervalCount == 8000) {
					//System.out.println("Tried");
					oldDiscarded = true;
					intervalCount = 0;
					for (PRange r : range) {
						for (long ts : r.state.values()) {
							if (ts < timeStampOfSwitch) {
								oldDiscarded = false;
								break;
							}
						}
						if (!oldDiscarded)
							break;
					}

					if (oldDiscarded) {
						System.out.println("Old Plan discarded in parallel track strategy");
						for (PRange r : range) {
							r.oldPlanDiscarded = true;
						}
						for (int i = 0; i < numStreams - 1; i++) {
							cuurentJoin[i].updatesShouldAddToState = false;
						}
					}
				}

			}
		}
	}


}
