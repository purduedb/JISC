package driver;

import java.util.ArrayList;

import nonadaptive.Tester;
import nonadaptive.Tuple;

public class Runner {

	private static void runTest() {
	
		//System.out.println("Starting Execution");
		
		Tester planner = new Tester(4);
		

		ArrayList<Tuple> planATuples = planner.runPlanA();
		System.out.println("Finished plan A");
		

		ArrayList<Tuple> planBTuples = planner.runPlanAdaptive();
		System.out.println("Finished plan B");
		
		
		ArrayList<Tuple> planCTuples = planner.runPlanEddy();
		System.out.println("Finished plan C");
		
		if (planATuples.size() != planBTuples.size() || planATuples.size() != planCTuples.size()) {
			System.out.println("Invalid!! A Size = " + planATuples.size() + " B Size = " + planBTuples.size() + " C Size = " + planCTuples.size());
			return;
		}
		
		for (int i = 0; i < planATuples.size(); i++) {
			
			if (planATuples.get(i).sign != planBTuples.get(i).sign || planATuples.get(i).sign != planCTuples.get(i).sign)
				System.out.println("Something wrong at tuple " + i + "signs => " + planATuples.get(i).sign + planBTuples.get(i).sign + planCTuples.get(i).sign );
			
			if (planATuples.get(i).oID != planBTuples.get(i).oID || planATuples.get(i).oID != planCTuples.get(i).oID)
				System.out.println("Something wrong at tuple " + i + "oID => " + planATuples.get(i).oID + planBTuples.get(i).oID  + planCTuples.get(i).oID);
			
			if (planATuples.get(i).timeStamp != planBTuples.get(i).timeStamp || planATuples.get(i).timeStamp != planCTuples.get(i).timeStamp)
				System.out.println("Something wrong at tuple " + i + "timeStamp => " + planATuples.get(i).timeStamp + planBTuples.get(i).timeStamp  + planCTuples.get(i).timeStamp);
			
			if (planATuples.get(i).xCoord != planBTuples.get(i).xCoord || planATuples.get(i).xCoord != planCTuples.get(i).xCoord)
				System.out.println("Something wrong at tuple " + i + "xCoord => " + planATuples.get(i).xCoord + planBTuples.get(i).xCoord  + planCTuples.get(i).xCoord);
		}
		

	}

	public static void main(String[] args) {
		
		for (int i = 0; i < 200; i++) {
			System.out.println("Test " + i);
			runTest();
		}

	}

}
