package driver;

import nonadaptive.Tuple;
import plans.AdaptivePlan;
import plans.EddyPlan;
import plans.ParallelTrackPlan;
import data.Generator;

import java.io.*;



public class Num_Join_Comparer {

	
	static int[] resultsAdaptive = new int[11];
	static int[] resultsCACQ = new int[11];
	static int[] resultsParallel = new int[11];
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int adaptationFreq = 10;
		boolean worstCase = false;
		
		int[] inputParam = new int[11];
		inputParam[1] = 25; inputParam[2] = 50; inputParam[3] = 62; inputParam[4] = 70; inputParam[5] = 75;
		inputParam[6] = 79; inputParam[7] = 82; inputParam[8] = 84; inputParam[9] = 85; inputParam[10] = 87;
		

		try{
			// Create file 
			FileWriter fstream = new FileWriter("num-joins.csv");
			BufferedWriter out = new BufferedWriter(fstream);

			int iterations = 1;
			
			for (int numStreams = 10; numStreams <= 100; numStreams+=10) {
				for (int j = 0; j < iterations; j++) {
					System.out.println(j + "th iteration in " + numStreams + "streams" + " with sel param " + inputParam[numStreams/10]);
					run(numStreams, adaptationFreq, worstCase, inputParam[numStreams/10]);
				}
			}
			
			double factor = iterations * 1000;
			for (int i = 1; i <= 10; i++) {
				out.write((double)resultsAdaptive[i]/factor + "," + (double)resultsParallel[i]/factor + "," + (double)resultsCACQ[i]/factor);
				out.write("\r\n");
			}

			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	static void run(int numStreams, int adaptationFreq, boolean worstCase, int selectivity) {

		Generator gen = new Generator(numStreams);

		AdaptivePlan adaptivePlan = new AdaptivePlan(numStreams);
		adaptivePlan.prepare(selectivity);

		EddyPlan eddyPlan = new EddyPlan(numStreams);
		eddyPlan.prepare(selectivity);

		ParallelTrackPlan parallelPlan = new ParallelTrackPlan(numStreams);
		parallelPlan.prepare(selectivity);

		long time, timeAdaptive = 0, timeEddy = 0, timeParallel = 0;

		Tuple[] tuples;
		Runtime r = Runtime.getRuntime();

		int max = 25; 

		for (int i = 1; i <= max; i++) {

			tuples = gen.generate();

			r.gc();

			time = System.currentTimeMillis();
			eddyPlan.execute(tuples);
			timeEddy += (System.currentTimeMillis() - time);


			r.gc();


			if (i%adaptationFreq == 0 && i != 0 && (max - i) > adaptationFreq ) {

				if (worstCase)
					adaptivePlan.adaptWorst();
				else
					adaptivePlan.adaptBest();
				//adaptivePlan.adaptMed();

				parallelPlan.adapt();
			}


			time = System.currentTimeMillis();
			parallelPlan.execute(tuples);
			timeParallel += (System.currentTimeMillis() - time);

			r.gc();



			time = System.currentTimeMillis();
			adaptivePlan.execute(tuples);
			timeAdaptive += (System.currentTimeMillis() - time);


			if (i < adaptationFreq)
				timeParallel = timeAdaptive;


			
			System.out.println(timeAdaptive + "," + timeParallel + "," + timeEddy);

		}
		
		resultsAdaptive[numStreams/10] += timeAdaptive;
		resultsCACQ[numStreams/10] += timeEddy;
		resultsParallel[numStreams/10] += timeParallel;

	}

}
