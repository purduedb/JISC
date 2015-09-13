package driver;

import nonadaptive.Tuple;
import plans.AdaptivePlan;
import plans.EddyPlan;
import plans.ParallelTrackPlan;
import data.Generator;

import java.io.*;



public class Freq_Comparer {

	
	static int[] resultsAdaptive = new int[11];
	static int[] resultsCACQ = new int[11];
	static int[] resultsParallel = new int[11];
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		boolean worstCase = true;
		
	

		try{
			// Create file 
			FileWriter fstream = new FileWriter("num-joins.csv");
			BufferedWriter out = new BufferedWriter(fstream);

			int iterations = 1;
			
			for (int adaptationFreq = 1; adaptationFreq <= 10; adaptationFreq+=1) {
				for (int j = 0; j < iterations; j++) {
					System.out.println(j + "th iteration in " + adaptationFreq + "frequency");
					run(20, adaptationFreq, worstCase, 70);
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

		int max = 20; 

		for (int i = 1; i <= max; i++) {

			tuples = gen.generate();

			r.gc();

			time = System.currentTimeMillis();
			eddyPlan.execute(tuples);
			timeEddy += (System.currentTimeMillis() - time);


			r.gc();


			if (i%adaptationFreq == 0 && i != 0) {

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
		
		resultsAdaptive[adaptationFreq] += timeAdaptive;
		resultsCACQ[adaptationFreq] += timeEddy;
		resultsParallel[adaptationFreq] += timeParallel;

	}

}
