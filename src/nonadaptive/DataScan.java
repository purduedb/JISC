package nonadaptive;

public class DataScan extends Operator{

	private int currPos;
	
	private Tuple[] data;
	
	public DataScan() {
	}
	
	public void getData(Tuple[] data) {
		currPos = 0;
		this.data = data;
	}
	
	public boolean hasNext() {
		return currPos != data.length;
	}
	
	public void process(Tuple tuple, int from) { // from is useless here
		
		// following 3 lines are necessary if more than 1 plan is run on the same data
		data[currPos].checkedStems.clear(); 
		data[currPos].isOld = false;
		data[currPos].isCompleting = false;
		
		streamTaggers[data[currPos].streamIndex].process(data[currPos++], positionToParent); // Index of stream has to be equal to its tagger operator in the allOperators array. Position to parent is useless here
		timeStampOfLastChange = data[currPos - 1].timeStamp;
	}

}
