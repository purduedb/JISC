package nonadaptive;

import java.util.ArrayList;
import java.util.Random;

import elke.*;

import adaptive.AdaptiveJoin;
import adaptive.Eddy;
import adaptive.Stem;

public class Tester {

	private final int DATASIZE = 1200000;
	private final int CUTOFF = 50000;

	private Tuple[] data;
	private int numStreams;

	private int rLeft = 10; private int rRight = 90; private int rTop = 90; private int rBottom = 10;
	private int sLeft = 10 ; private int sRight = 90; private int sTop = 90; private int sBottom = 10;
	private int tLeft = 10; private int tRight = 90; private int tTop = 90; private int tBottom = 10;
	private int uLeft = 10; private int uRight = 90; private int uTop = 90; private int uBottom = 10;

	private int idRange = 10000;

	public Tester(int numStreams) {
		this.numStreams = numStreams; 

		Random rand = new Random();
		int time = 0;
		data = new Tuple[DATASIZE];

		for (int i = 0; i < DATASIZE; i++) {
			data[i] = new Tuple(numStreams);
			data[i].streamIndex = rand.nextInt(numStreams);
			data[i].timeStamp = time++;
			data[i].oID = rand.nextInt(idRange);
			data[i].xCoord = rand.nextInt(101);
		}
	}

	public ArrayList<Tuple> runPlanA() {

		DataScan dataScan = new DataScan();
		dataScan.getData(data);
		Range rRange = new Range(rLeft, rRight, rTop, rBottom);
		Range sRange = new Range(sLeft, sRight, sTop, sBottom);
		Join joinRS = new Join();

		Range tRange = new Range(tLeft, tRight, tTop, tBottom);
		Join joinRST = new Join();
		
		Range uRange = new Range(uLeft, uRight, uTop, uBottom);
		Join joinRSTU = new Join();

		OutputView opView = new OutputView();

		// Setting lefts and rights as necessary
		joinRS.left = rRange; joinRS.right = sRange;
		joinRST.left = joinRS; joinRST.right = tRange;
		joinRSTU.left = joinRST; joinRSTU.right = uRange;
		
		// Setting parents as necessary
		rRange.parent = joinRS;
		sRange.parent = joinRS;
		
		joinRS.parent = joinRST;
		tRange.parent = joinRST;
		
		joinRST.parent = joinRSTU;
		uRange.parent = joinRSTU;
		
		joinRSTU.parent = opView;
		
		
		// Setting Positions as necessary
		rRange.positionToParent = 0;
		sRange.positionToParent = 1;
		
		joinRS.positionToParent = 0;
		tRange.positionToParent = 1;
		
		joinRST.positionToParent = 0;
		uRange.positionToParent = 1;
		
		joinRSTU.positionToParent = 0;

		Operator[] streamTaggers = new Operator[numStreams];
		streamTaggers[0] = rRange;
		streamTaggers[1] = sRange;
		streamTaggers[2] = tRange;
		streamTaggers[3] = uRange;

		dataScan.streamTaggers = streamTaggers;

		while(dataScan.hasNext()) {
			dataScan.process(null, 0);
		}

		return opView.allTuples;
	}	

	public ArrayList<Tuple> runPlanB() {

		DataScan dataScan = new DataScan();
		dataScan.getData(data);
		Range uRange = new Range(uLeft, uRight, uTop, uBottom);
		Range tRange = new Range(tLeft, tRight, tTop, tBottom);
		Join joinUT = new Join();

		Range sRange = new Range(sLeft, sRight, sTop, sBottom);
		Join joinUTS = new Join();
		
		Range rRange = new Range(rLeft, rRight, rTop, rBottom);
		Join joinUTSR = new Join();

		OutputView opView = new OutputView();

		// Setting lefts and rights as necessary
		joinUT.left = uRange; joinUT.right = tRange;
		joinUTS.left = joinUT; joinUTS.right = sRange;
		joinUTSR.left = joinUTS; joinUTSR.right = rRange;

		// Setting parents as necessary
		uRange.parent = joinUT;
		tRange.parent = joinUT;
		
		joinUT.parent = joinUTS;
		sRange.parent = joinUTS;
		
		joinUTS.parent = joinUTSR;
		rRange.parent = joinUTSR;
		
		joinUTSR.parent = opView;
		
		// Setting Positions as necessary
		uRange.positionToParent = 0;
		tRange.positionToParent = 1;
		
		joinUT.positionToParent = 0;
		sRange.positionToParent = 1;
		
		joinUTS.positionToParent = 0;
		rRange.positionToParent = 1;
		
		joinUTSR.positionToParent = 0;

		Operator[] streamTaggers = new Operator[numStreams];
		streamTaggers[0] = rRange;
		streamTaggers[1] = sRange;
		streamTaggers[2] = tRange;
		streamTaggers[3] = uRange;

		dataScan.streamTaggers = streamTaggers;

		while(dataScan.hasNext()) {
			dataScan.process(null, 0);
		}

		return opView.allTuples;
	}
	
	public ArrayList<Tuple> runPlanEddy() {

		DataScan dataScan = new DataScan();
		dataScan.getData(data);
		Range rRange = new Range(rLeft, rRight, rTop, rBottom);
		Range sRange = new Range(sLeft, sRight, sTop, sBottom);
		Range tRange = new Range(tLeft, tRight, tTop, tBottom);
		Range uRange = new Range(uLeft, uRight, uTop, uBottom);
		
		OutputView opView = new OutputView();
		
		Eddy eddy = new Eddy(opView);
		
		Stem[] stems = new Stem[numStreams];
		stems[0] = new Stem(rRange, eddy);
		stems[1] = new Stem(sRange, eddy);
		stems[2] = new Stem(tRange, eddy);
		stems[3] = new Stem(uRange, eddy);
		
		eddy.stems = stems;
				
		Operator[] streamTaggers = new Operator[numStreams];
		streamTaggers[0] = rRange;
		streamTaggers[1] = sRange;
		streamTaggers[2] = tRange;
		streamTaggers[3] = uRange;

		rRange.parent = eddy; 	rRange.myIndex = 0; rRange.positionToParent = 0;
		sRange.parent = eddy;		sRange.myIndex = 1; sRange.positionToParent = 1;
		tRange.parent = eddy;		tRange.myIndex = 2; tRange.positionToParent = 2;
		uRange.parent = eddy;		uRange.myIndex = 3; uRange.positionToParent = 3;
		
		dataScan.streamTaggers = streamTaggers;

		for (int i = 0; i < CUTOFF; i++) {
			dataScan.process(null, 0);
		}
		
		stems[0] = new Stem(uRange, eddy);
		stems[1] = new Stem(tRange, eddy);
		stems[2] = new Stem(sRange, eddy);
		stems[3] = new Stem(rRange, eddy);
		
		uRange.myIndex = 0; uRange.positionToParent = 0;
		tRange.myIndex = 1; tRange.positionToParent = 1;
		sRange.myIndex = 2; sRange.positionToParent = 2;
		rRange.myIndex = 3; rRange.positionToParent = 3;
				
		while(dataScan.hasNext()) {
			dataScan.process(null, 0);
		}

		return opView.allTuples;
	}

	public ArrayList<Tuple> runPlanAdaptiveB() {

		DataScan dataScan = new DataScan();
		dataScan.getData(data);
		Range rRange = new Range(rLeft, rRight, rTop, rBottom);
		Range sRange = new Range(sLeft, sRight, sTop, sBottom);
		AdaptiveJoin joinRS = new AdaptiveJoin ();

		Range tRange = new Range(tLeft, tRight, tTop, tBottom);
		AdaptiveJoin  joinRST = new AdaptiveJoin ();
		
		Range uRange = new Range(uLeft, uRight, uTop, uBottom);
		AdaptiveJoin  joinRSTU = new AdaptiveJoin ();

		OutputView opView = new OutputView();

		// Setting lefts and rights as necessary
		joinRS.left = rRange; joinRS.right = sRange;
		joinRST.left = joinRS; joinRST.right = tRange;
		joinRSTU.left = joinRST; joinRSTU.right = uRange;
		
		// Setting parents as necessary
		rRange.parent = joinRS;
		sRange.parent = joinRS;
		
		joinRS.parent = joinRST;
		tRange.parent = joinRST;
		
		joinRST.parent = joinRSTU;
		uRange.parent = joinRSTU;
		
		joinRSTU.parent = opView;
		
		
		// Setting Positions as necessary
		rRange.positionToParent = 0;
		sRange.positionToParent = 1;
		
		joinRS.positionToParent = 0;
		tRange.positionToParent = 1;
		
		joinRST.positionToParent = 0;
		uRange.positionToParent = 1;
		
		joinRSTU.positionToParent = 0;

		Operator[] streamTaggers = new Operator[numStreams];
		streamTaggers[0] = rRange;
		streamTaggers[1] = sRange;
		streamTaggers[2] = tRange;
		streamTaggers[3] = uRange;

		dataScan.streamTaggers = streamTaggers;

		// Run to cutoff
		for (int i = 0; i < CUTOFF; i++) {
			dataScan.process(null, 0);
		}
		
		// Run new plan
		
		joinRS.state.clear();
		joinRS.numEntriesToCompState = sRange.state.size();
		joinRS.directionCausingIncompleteness = 1;
		
		joinRST.state.clear();
		joinRST.numEntriesToCompState = tRange.state.size();
		joinRST.directionCausingIncompleteness = 1;
		
		
		rRange.timeStampOfLastChange = dataScan.timeStampOfLastChange;
		sRange.timeStampOfLastChange = dataScan.timeStampOfLastChange;
		tRange.timeStampOfLastChange = dataScan.timeStampOfLastChange;
		uRange.timeStampOfLastChange = dataScan.timeStampOfLastChange;
		
		joinRSTU.timeStampOfLastChange = dataScan.timeStampOfLastChange;
		joinRST.timeStampOfLastChange = dataScan.timeStampOfLastChange;
		joinRS.timeStampOfLastChange = dataScan.timeStampOfLastChange;
		
		joinRSTU.numEntriesToCompState = 0;
		rRange.numEntriesToCompState = 0;
		sRange.numEntriesToCompState = 0;
		tRange.numEntriesToCompState = 0;
		uRange.numEntriesToCompState = 0;

		joinRSTU.nextComplete = joinRS;
		joinRST.nextComplete = joinRS;

		while(dataScan.hasNext()) {
			dataScan.process(null, 0);
		}

		return opView.allTuples;

	}
	
	public ArrayList<Tuple> runPlanAdaptive() {

		DataScan dataScan = new DataScan();
		dataScan.getData(data);
		Range rRange = new Range(rLeft, rRight, rTop, rBottom);
		Range sRange = new Range(sLeft, sRight, sTop, sBottom);
		AdaptiveJoin joinRS = new AdaptiveJoin ();

		Range tRange = new Range(tLeft, tRight, tTop, tBottom);
		AdaptiveJoin  joinRST = new AdaptiveJoin ();
		
		Range uRange = new Range(uLeft, uRight, uTop, uBottom);
		AdaptiveJoin  joinRSTU = new AdaptiveJoin ();

		OutputView opView = new OutputView();

		// Setting lefts and rights as necessary
		joinRS.left = rRange; joinRS.right = sRange;
		joinRST.left = joinRS; joinRST.right = tRange;
		joinRSTU.left = joinRST; joinRSTU.right = uRange;
		
		// Setting parents as necessary
		rRange.parent = joinRS;
		sRange.parent = joinRS;
		
		joinRS.parent = joinRST;
		tRange.parent = joinRST;
		
		joinRST.parent = joinRSTU;
		uRange.parent = joinRSTU;
		
		joinRSTU.parent = opView;
		
		
		// Setting Positions as necessary
		rRange.positionToParent = 0;
		sRange.positionToParent = 1;
		
		joinRS.positionToParent = 0;
		tRange.positionToParent = 1;
		
		joinRST.positionToParent = 0;
		uRange.positionToParent = 1;
		
		joinRSTU.positionToParent = 0;

		Operator[] streamTaggers = new Operator[numStreams];
		streamTaggers[0] = rRange;
		streamTaggers[1] = sRange;
		streamTaggers[2] = tRange;
		streamTaggers[3] = uRange;

		dataScan.streamTaggers = streamTaggers;

		// Run to cutoff
		for (int i = 0; i < CUTOFF; i++) {
			dataScan.process(null, 0);
		}
		
		// Run new plan
		AdaptiveJoin joinUT = new AdaptiveJoin();
		if (uRange.state.size() < tRange.state.size()) {
			joinUT.numEntriesToCompState = uRange.state.size();
			joinUT.directionCausingIncompleteness = 0;
		}
		else {
			joinUT.numEntriesToCompState = tRange.state.size();
			joinUT.directionCausingIncompleteness = 1;
		}
		//System.out.println("Dir causing incompleteness for UT ===> " + joinUT.directionCausingIncompleteness + " with size " + joinUT.numEntriesToCompState);
		
		AdaptiveJoin joinUTS = new AdaptiveJoin();
		joinUTS.numEntriesToCompState = sRange.state.size();
		joinUTS.directionCausingIncompleteness = 1;
		//System.out.println("Dir causing incompleteness for UTS ===> 1 as it has to be " + joinUTS.directionCausingIncompleteness + " with size " + joinUTS.numEntriesToCompState);
				
		// Setting lefts and rights as necessary
		joinUT.left = uRange; joinUT.right = tRange;
		joinUTS.left = joinUT; joinUTS.right = sRange;
		joinRSTU.left = joinUTS; joinRSTU.right = rRange; 
		
		// Setting parents as necessary
		uRange.parent = joinUT;
		tRange.parent = joinUT;
		
		joinUT.parent = joinUTS;
		sRange.parent = joinUTS;
		
		joinUTS.parent = joinRSTU;
		rRange.parent = joinRSTU;
				
		// Setting Positions as necessary
		uRange.positionToParent = 0;
		tRange.positionToParent = 1;
		
		joinUT.positionToParent = 0;
		sRange.positionToParent = 1;
		
		joinUTS.positionToParent = 0;
		rRange.positionToParent = 1;
		
		
		rRange.timeStampOfLastChange = dataScan.timeStampOfLastChange;
		sRange.timeStampOfLastChange = dataScan.timeStampOfLastChange;
		tRange.timeStampOfLastChange = dataScan.timeStampOfLastChange;
		uRange.timeStampOfLastChange = dataScan.timeStampOfLastChange;
		
		joinRSTU.timeStampOfLastChange = dataScan.timeStampOfLastChange;
		joinUTS.timeStampOfLastChange = dataScan.timeStampOfLastChange;
		joinUT.timeStampOfLastChange = dataScan.timeStampOfLastChange;
		
		joinRSTU.numEntriesToCompState = 0;
		rRange.numEntriesToCompState = 0;
		sRange.numEntriesToCompState = 0;
		tRange.numEntriesToCompState = 0;
		uRange.numEntriesToCompState = 0;
		
		joinRSTU.nextComplete = joinUT;
		joinUTS.nextComplete = joinUT;


		while(dataScan.hasNext()) {
			dataScan.process(null, 0);
		}

		return opView.allTuples;

	}

	public ArrayList<Tuple> runPlanPrallelTrack() {
		DataScan dataScan = new DataScan();
		dataScan.getData(data);
		PRange rRange = new PRange(rLeft, rRight, rTop, rBottom);
		PRange sRange = new PRange(sLeft, sRight, sTop, sBottom);
		PJoin joinRS = new PJoin ();

		PRange tRange = new PRange(tLeft, tRight, tTop, tBottom);
		PJoin  joinRST = new PJoin ();
		
		PRange uRange = new PRange(uLeft, uRight, uTop, uBottom);
		PJoin  joinRSTU = new PJoin ();

		POutputView opView = new POutputView();

		// Setting lefts and rights as necessary
		joinRS.left = rRange; joinRS.right = sRange;
		joinRST.left = joinRS; joinRST.right = tRange;
		joinRSTU.left = joinRST; joinRSTU.right = uRange;
		
		// Setting parents as necessary
		rRange.parent = joinRS; rRange.oldParent = joinRS;
		sRange.parent = joinRS; sRange.oldParent = joinRS;
		
		joinRS.parent = joinRST;
		tRange.parent = joinRST; tRange.oldParent = joinRST;
		
		joinRST.parent = joinRSTU;
		uRange.parent = joinRSTU; uRange.oldParent = joinRSTU;
		
		joinRSTU.parent = opView;
		
				
		// Setting Positions as necessary
		rRange.positionToParent = 0; rRange.positionToOldParent = 0;
		sRange.positionToParent = 1; sRange.positionToOldParent = 1;
		
		joinRS.positionToParent = 0;
		tRange.positionToParent = 1; tRange.positionToOldParent = 1;
		
		joinRST.positionToParent = 0;
		uRange.positionToParent = 1; uRange.positionToOldParent = 1;
		
		joinRSTU.positionToParent = 0;

		Operator[] streamTaggers = new Operator[numStreams];
		streamTaggers[0] = rRange;
		streamTaggers[1] = sRange;
		streamTaggers[2] = tRange;
		streamTaggers[3] = uRange;

		dataScan.streamTaggers = streamTaggers;

		// Run to cutoff
		for (int i = 0; i < CUTOFF; i++) {
			dataScan.process(null, 0);
		}
		
		// Run new plan
		
		// Setting booleans for old plan not be discarded
		rRange.oldPlanDiscarded = false;
		sRange.oldPlanDiscarded = false;
		tRange.oldPlanDiscarded = false;
		uRange.oldPlanDiscarded = false;
		
		
		PJoin joinUT = new PJoin();
		PJoin joinUTS = new PJoin();
		PJoin joinUTSR = new PJoin();
				
		// Setting lefts and rights as necessary
		joinUT.left = uRange; joinUT.right = tRange;
		joinUTS.left = joinUT; joinUTS.right = sRange;
		joinUTSR.left = joinUTS; joinUTSR.right = rRange; 
		
		// Setting parents as necessary
		uRange.parent = joinUT;
		tRange.parent = joinUT;
		
		joinUT.parent = joinUTS;
		sRange.parent = joinUTS;
		
		joinUTS.parent = joinUTSR;
		rRange.parent = joinUTSR;
		
		joinUTSR.parent = opView;
				
		// Setting Positions as necessary
		uRange.positionToParent = 0;
		tRange.positionToParent = 1;
		
		joinUT.positionToParent = 0;
		sRange.positionToParent = 1;
		
		joinUTS.positionToParent = 0;
		rRange.positionToParent = 1;
		
		joinUTSR.positionToParent = 0;
		
		long timeStampOfSwitch = dataScan.timeStampOfLastChange;

		boolean discardOld = false;
		int intervalCount = 0;
		while(dataScan.hasNext()) {
			dataScan.process(null, 0);
			
			intervalCount++;
						
			if (intervalCount == 1000 && !discardOld) {
				discardOld = true;
				intervalCount = 0;
				for (long timeSt : rRange.state.values()) {
					if (timeSt < timeStampOfSwitch) {
						discardOld = false;
						break;
					}
				}
				if (discardOld)
				for (long timeSt : sRange.state.values()) {
					if (timeSt < timeStampOfSwitch) {
						discardOld = false;
						break;
					}
				}
				if (discardOld)
				for (long timeSt : tRange.state.values()) {
					if (timeSt < timeStampOfSwitch) {
						discardOld = false;
						break;
					}
				}
				if (discardOld)
				for (long timeSt : uRange.state.values()) {
					if (timeSt < timeStampOfSwitch) {
						discardOld = false;
						break;
					}
				}
//				if (discardOld)
//				for (long timeSt : joinRS.state.values()) {
//					if (timeSt < timeStampOfSwitch) {
//						discardOld = false;
//						break;
//					}
//				}
//				if (discardOld)
//				for (long timeSt : joinRST.state.values()) {
//					if (timeSt < timeStampOfSwitch) {
//						discardOld = false;
//						break;
//					}
//				}
//				if (discardOld)
//				for (long timeSt : joinRSTU.state.values()) {
//					if (timeSt < timeStampOfSwitch) {
//						discardOld = false;
//						break;
//					}
//				}
				if (discardOld) {
					System.out.println("Old Plan discarded in parallel track strategy");
					rRange.oldPlanDiscarded = true;
					sRange.oldPlanDiscarded = true;
					tRange.oldPlanDiscarded = true;
					uRange.oldPlanDiscarded = true;
				}
			}
		}

		return opView.allTuples;
	}

}
