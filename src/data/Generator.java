package data;

import java.util.Random;

import nonadaptive.Tuple;

public class Generator {
	
	private final int DATASIZE = 1000000;
	
	private int numStreams;
	
	private int idRange = 10000;
	
	private long time = 0;
	
	public Generator(int numStreams) {
		this.numStreams = numStreams;
		time = 0;
	}
	
	public Tuple[] generate () {
		//Runtime r = Runtime.getRuntime();
		//r.gc();
		Random rand = new Random();
		Tuple[] data;
		data = new Tuple[DATASIZE];

		for (int i = 0; i < DATASIZE; i++) {
			data[i] = new Tuple(numStreams);
			data[i].timeStamp = time++;
			data[i].streamIndex = rand.nextInt(numStreams);
			data[i].oID = rand.nextInt(idRange);
			data[i].xCoord = rand.nextInt(101);
		}
		
		return data;
	}

}
