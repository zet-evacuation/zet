package algo.ca.parameter;

import org.zet.cellularautomaton.algorithm.parameter.DefaultParameterSet;
import org.zetool.rndutils.RandomUtils;
import org.zetool.rndutils.distribution.continuous.NormalDistribution;
import junit.framework.TestCase;
import ds.PropertyContainer;
import org.zetool.components.property.PropertyLoadException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.zetool.components.property.PropertyTreeModelLoader;
import umontreal.iro.lecuyer.charts.XYLineChart;
import umontreal.iro.lecuyer.stat.TallyStore;

/**
 * The class {@code DefaultParameterSetTest} tests the speed distribution.
 *
 * @author Jan-Philipp Kappmeier
 */
public class DefaultParameterSetTest extends TestCase {

    /**
     * Creates a new instance of {@code DefaultParameterSetTest}.
     */
    public DefaultParameterSetTest() {
        File propertyFile = new File("./properties/rimea.xml");
        try {
            PropertyTreeModelLoader loader = new PropertyTreeModelLoader();
            loader.applyParameters(new FileReader(propertyFile), PropertyContainer.getGlobal());
        } catch (PropertyLoadException | FileNotFoundException ex) {
            System.err.println("Property loading error");
        }
    }

    public void testAgeSpeed() {
        NormalDistribution age = new NormalDistribution(50, 20, 10, 85);
        DefaultParameterSet dps = new DefaultParameterSet();

        RandomUtils.getInstance().setSeed(System.nanoTime());

        for (int i = 1; i <= 1000; ++i) {
            double ret = age.getNextRandom();
            System.out.print("age: " + ret + " ");
            double speed = dps.getSpeedFromAge(ret);
            System.out.println("Speed: " + speed);
        }
        System.out.println(dps.cumulativeFemale / dps.counterFemale);
        System.out.println(dps.cumulativeMale / dps.counterMale);
        System.out.println((dps.cumulativeFemale + dps.cumulativeMale) / (dps.counterFemale + dps.counterMale));

    }

    int midCount;
    double midCum;
    int highCount;
    double highCum;
    int lowCount;
    double lowCum;

    NormalDistribution ageDistribution = new NormalDistribution(50, 20, 10, 85);

    public void testStairSpeed() {
        DefaultParameterSet dps = new DefaultParameterSet();

        RandomUtils.getInstance().setSeed(System.nanoTime());

        double skip = 0.01;
        double min = 0.3;
        double max = 0.6;
        final int len = (int) Math.floor((max - min) / skip);
        yLinear = new double[2][len];
        ySquared = new double[2][len];
        yMax = new double[2][len];
		//x = new double[(int)Math.floor( (max-min)/skip )];

        for (int i = 0; i < len; ++i) {
            yLinear[0][i] = i * skip + min;
            ySquared[0][i] = i * skip + min;
            yMax[0][i] = i * skip + min;
            //x[i] = i * skip + min;
            stairSpeed(i, i * skip + min, dps);
        }

        // Show Diagrams
        XYLineChart distribution = new XYLineChart("Title", "X", "Y", yLinear, ySquared, yMax);
        distribution.view(640, 480);

		//		double[][] distributionData = new double[2][100];
//		if( ageDist instanceof NormalDistribution ) {
//			NormalDistribution normalDist = (NormalDistribution)ageDist;
//			final double minX = 0.001;
//			final double maxX = 0.999;
//			for( int i = 1; i <= distributionData[0].length; ++i ) {
//				final double y = i * (maxX - minX) / (distributionData[0].length);
//				final double x = normalDist.getInverseCumulatedDensityFunction( y );//
//				distributionData[0][i-1] = x;
//				distributionData[1][i-1] = y;
//			}
//		}
//		for( double factor = min; factor <= max; factor = factor + skip ) {
//			System.out.println( "Teste mit factor = " + factor );
//			stairSpeed( factor, dps );
//			System.out.println();
//		}
        System.out.println();

        System.out.println("Min linear factor: " + minLinearFactor);
        System.out.println("Min squared factor: " + minSquaredFactor);
        System.out.println("Min max factor: " + minMaxFactor);

    }

    double minSquared = 10000000;
    double minLinear = 10000000;
    double minMax = 10000000;
    double minSquaredFactor;
    double minLinearFactor;
    double minMaxFactor;

    TallyStore linearTally = new TallyStore();
    TallyStore squaredTally = new TallyStore();
    TallyStore maxTally = new TallyStore();

    double[][] yLinear;
    double[][] ySquared;
    double[][] yMax;
    double[] x;

    public void stairSpeed(int index, double factor, DefaultParameterSet dps) {
        for (int i = 1; i <= 10000; ++i) {
            double age = ageDistribution.getNextRandom();
            double speed = dps.getSpeedFromAge(age);
			//System.out.print( "age: " + age + " " );
            //System.out.println( "Speed: " + speed );
            if (age < 30) {
                lowCount++;
                lowCum += speed * factor;
            } else if (age > 50) {
                highCount++;
                highCum += speed * factor;
            } else {
                midCount++;
                midCum += speed * factor;
            }

        }
//		System.out.println( dps.cumulativeFemale/dps.counterFemale );
//		System.out.println( dps.cumulativeMale/dps.counterMale );
//		System.out.println( (dps.cumulativeFemale+dps.cumulativeMale)/(dps.counterFemale+dps.counterMale) );
        double lowAv = (lowCum / lowCount);
        double midAv = (midCum / midCount);
        double highAv = (highCum / highCount);

        System.out.println("Average    - 30: " + lowAv);
        System.out.println("Average 30 - 50: " + midAv);
        System.out.println("Average 50 -   : " + highAv);

		// Test für Außentreppen
        // Test für Treppe hoch
//		double low = Math.abs( lowAv - 0.58 );
//		double mid = Math.abs( midAv - 0.58);
//		double high = Math.abs( highAv - 0.42);
        // Test für Treppe runter
//		double low = Math.abs( lowAv - 0.81 );
//		double mid = Math.abs( midAv - 0.78);
//		double high = Math.abs( highAv - 0.59);
		// Test für Innentreppen
        // Test für Treppe runter
//		double low = Math.abs( lowAv - 0.76 );
//		double mid = Math.abs( midAv - 0.65);
//		double high = Math.abs( highAv - 0.55);
        // Test für treppe hoch
        double lowAge = Math.abs(lowAv - 0.55);
        double midAge = Math.abs(midAv - 0.5);
        double highAge = Math.abs(highAv - 0.42);

        double linearSum = lowAge + highAge + midAge;
        double squareSum = lowAge * lowAge + midAge * midAge + highAge * highAge;
        double max = Math.max(lowAge, Math.max(midAge, highAge));
        System.out.println("Abweichung (linear): " + linearSum);
        System.out.println("Abweichung (squared): " + squareSum);
        System.out.println("Maximale Abweichung:" + max);

        yLinear[1][index] = linearSum;//new double[(int)Math.floor( (max-min)/skip )];
        ySquared[1][index] = squareSum; //new double[(int)Math.floor( (max-min)/skip )];
        yMax[1][index] = max; // = new double[(int)Math.floor( (max-min)/skip )];

        if (linearSum < minLinear) {
            minLinear = linearSum;
            minLinearFactor = factor;
        }
        if (squareSum < minSquared) {
            minSquared = squareSum;
            minSquaredFactor = factor;
        }
        if (max < minMax) {
            minMax = max;
            minMaxFactor = factor;
        }
    }

}
