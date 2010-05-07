/*
 * DefaultParameterSetTest.java
 * Created 03.05.2010, 21:34:43
 */

package algo.ca.parameter;

import de.tu_berlin.math.coga.rndutils.RandomUtils;
import de.tu_berlin.math.coga.rndutils.distribution.continuous.NormalDistribution;
import gui.editor.properties.PropertyLoadException;
import junit.framework.TestCase;
import ds.PropertyContainer;
import java.io.File;

/**
 * The class <code>DefaultParameterSetTest</code> tests the speed distribution.
 * @author Jan-Philipp Kappmeier
 */
public class DefaultParameterSetTest extends TestCase {

	/**
	 * Creates a new instance of <code>DefaultParameterSetTest</code>.
	 */
	public DefaultParameterSetTest() {
		File propertyFile = new File( "./properties/rimea.xml" );
		try {
			PropertyContainer.getInstance().applyParameters( propertyFile );
		} catch( PropertyLoadException ex ) {
			System.err.println( "Property loading error" );
		}
	}

	public void testAgeSpeed() {
		NormalDistribution age = new NormalDistribution( 50, 20, 10, 85);
		DefaultParameterSet dps = new DefaultParameterSet();

		RandomUtils.getInstance().setSeed( System.nanoTime() );

		for( int i = 1; i <= 1000; ++i ) {
			double ret = age.getNextRandom();
			System.out.print( "age: " + ret + " " );
			double speed = dps.getSpeedFromAge( ret );
			System.out.println( "Speed: " + speed );
		}
		System.out.println( dps.cumulativeFemale/dps.counterFemale );
		System.out.println( dps.cumulativeMale/dps.counterMale );
		System.out.println( (dps.cumulativeFemale+dps.cumulativeMale)/(dps.counterFemale+dps.counterMale) );

	}

	int midCount;
	double midCum;
	int highCount;
	double highCum;
	int lowCount;
	double lowCum;

	NormalDistribution ageDistribution = new NormalDistribution( 50, 20, 10, 85);
	public void testStairSpeed() {
		DefaultParameterSet dps = new DefaultParameterSet();

		RandomUtils.getInstance().setSeed( System.nanoTime() );



		for( double factor = 0.62; factor <= 0.66; factor = factor + 0.001 ) {
			System.out.println( "Teste mit factor = " + factor );
			stairSpeed( factor, dps );
			System.out.println();
		}

		System.out.println();

		System.out.println( "Min linear factor: " + minLinearFactor );
		System.out.println( "Min squared factor: " + minSquaredFactor );
		System.out.println( "Min max factor: " + minMaxFactor );

	}

	double minSquared = 10000000;
	double minLinear = 10000000;
	double minMax = 10000000;
	double minSquaredFactor;
	double minLinearFactor;
	double minMaxFactor;

	public void stairSpeed( double factor, DefaultParameterSet dps ) {
		for( int i = 1; i <= 10000000; ++i ) {
			double age = ageDistribution.getNextRandom();
			double speed = dps.getSpeedFromAge( age );
			//System.out.print( "age: " + age + " " );
			//System.out.println( "Speed: " + speed );
			if( age < 30 ) {
				lowCount++;
				lowCum += speed*factor;
			} else if( age > 50 ) {
				highCount++;
				highCum += speed*factor;
			} else {
				midCount++;
				midCum += speed*factor;
			}

		}
//		System.out.println( dps.cumulativeFemale/dps.counterFemale );
//		System.out.println( dps.cumulativeMale/dps.counterMale );
//		System.out.println( (dps.cumulativeFemale+dps.cumulativeMale)/(dps.counterFemale+dps.counterMale) );
		double lowAv =  (lowCum/lowCount);
		double midAv = (midCum/midCount);
		double highAv = (highCum/highCount);

		System.out.println( "Average    - 30: " + lowAv );
		System.out.println( "Average 30 - 50: " + midAv );
		System.out.println( "Average 50 -   : " + highAv );

		// Test für Außentreppen
		// Test für Treppe hoch
//		double low = Math.abs( lowAv - 0.58 );
//		double mid = Math.abs( midAv - 0.58);
//		double high = Math.abs( highAv - 0.42);
		// Test für Treppe runter
		double low = Math.abs( lowAv - 0.81 );
		double mid = Math.abs( midAv - 0.78);
		double high = Math.abs( highAv - 0.59);



		// Test für Innentreppen
		// Test für Treppe runter
//		double low = Math.abs( lowAv - 0.76 );
//		double mid = Math.abs( midAv - 0.65);
//		double high = Math.abs( highAv - 0.55);
		// Test für treppe hoch
//		double low = Math.abs( lowAv - 0.55 );
//		double mid = Math.abs( midAv - 0.5);
//		double high = Math.abs( highAv - 0.42);

		double linearSum = low + high + mid;
		double squareSum = low*low + mid*mid + high*high;
		double max = Math.max( low, Math.max( mid, high ) );
		System.out.println( "Abweichung (linear): " + linearSum );
		System.out.println( "Abweichung (squared): " + squareSum );
		System.out.println( "Maximale Abweichung:" + max );
		if( linearSum < minLinear ) {
			minLinear = linearSum;
			minLinearFactor = factor;
		}
		if( squareSum < minSquared ) {
			minSquared = squareSum;
			minSquaredFactor = factor;
		}
		if( max < minMax ) {
			minMax = max;
			minMaxFactor = factor;
		}
	}


}
