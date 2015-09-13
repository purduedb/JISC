package driver;

import nonadaptive.Tuple;
import plans.AdaptivePlan;
import plans.EddyPlan;
import plans.ParallelTrackPlan;
import data.Generator;

import java.io.*;



public class Steady_Comparer {

	static FileWriter fstream;
	static BufferedWriter out;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int adaptationFreq = 5;
		boolean worstCase = false;

		try{
			// Create file 


			fstream = new FileWriter("steady.csv");
			out = new BufferedWriter(fstream);

			run(20, adaptationFreq, worstCase, 70);


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

		int max = 10; 

		for (int i = 1; i <= max; i++) {

			tuples = gen.generate();

			r.gc();

			time = System.currentTimeMillis();
			eddyPlan.execute(tuples);
			timeEddy += (System.currentTimeMillis() - time);


			r.gc();


			//if (i%adaptationFreq == 0 && i != 0 && (max - i) > adaptationFreq ) {
			if (i == adaptationFreq){
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


			try{
				System.out.println(i + "," + timeAdaptive/1000.0 + "," + timeParallel/1000.0 + "," + timeEddy/1000.0);
				out.write(timeAdaptive/1000.0 + "," + timeParallel/1000.0 + "," + timeEddy/1000.0);
				out.write("\r\n");
			} catch(Exception e) {

			}

		}


	}

}
