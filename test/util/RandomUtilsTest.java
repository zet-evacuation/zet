package util;

import static org.junit.Assert.*;

import org.junit.Test;

public class RandomUtilsTest {

	@Test
	public void testChooseRandomlyAbsolute() {
		final double NUMBER_OF_TRIES = 1000000;
		double[] probabilities = new double[10];
		double[] frequencies = new double[10];
		
		probabilities[0] =  5.0;
		probabilities[1] =  5.0;
		probabilities[2] =  5.0;
		probabilities[3] = 20.0;
		probabilities[4] = 15.0;
		probabilities[5] =  5.0;
		probabilities[6] =  0.0;
		probabilities[7] = 10.0;
		probabilities[8] = 20.0;
		probabilities[9] = 25.0;
		
		for(int i=0; i < NUMBER_OF_TRIES; i++){
			int randomIndex = util.random.RandomUtils.getInstance().chooseRandomlyAbsolute(probabilities);
			frequencies[randomIndex]++;
		}
		
		double sum = 0.0;
		for(int i=0; i < probabilities.length; i++){
			sum += probabilities[i];
		}
				
		for(int i=0; i < probabilities.length; i++){
			System.out.print("Index " + i + " "); 
			System.out.print("absolute value " + probabilities[i] + " ");
			System.out.print("probability " + (probabilities[i]/sum) + " ");
			System.out.println("frequency " + (frequencies[i] / (double)NUMBER_OF_TRIES));
		}
	}

}
