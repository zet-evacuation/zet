/*
 * DefaultParameterSetTest.java
 * Created 03.05.2010, 21:34:43
 */
package statistics;

import org.zet.cellularautomaton.algorithm.parameter.DefaultParameterSet;
import org.zetool.rndutils.RandomUtils;
import org.zetool.rndutils.distribution.continuous.NormalDistribution;
import ds.PropertyContainer;
import org.zetool.components.property.PropertyLoadException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.zetool.components.property.PropertyTreeModelLoader;
import umontreal.iro.lecuyer.charts.XYLineChart;

/**
 * The class {@code DefaultParameterSetTest} tests the speed distribution.
 *
 * @author Jan-Philipp Kappmeier
 */
public class DefaultParameterSetTest {

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

    int runs = 10000;
    StairTests stairTests;

    /**
     *
     * @return
     */
    StairTests getStairTests() {
        return stairTests;
    }

    /**
     *
     * @param stairTests
     */
    void setStairTests(StairTests stairTests) {
        this.stairTests = stairTests;
    }

    int getRuns() {
        return runs;
    }

    void setRuns(int runs) {
        this.runs = runs;
    }

    double skip = 0.1;
    double min;
    double max;

    NormalDistribution ageDistribution = new NormalDistribution(50, 20, 10, 85);

    public void testStairSpeed(double skip, double min, double max) {
        DefaultParameterSet dps = new DefaultParameterSet();

        RandomUtils.getInstance().setSeed(System.nanoTime());
        this.skip = skip;
        this.min = min;
        this.max = max;
        minSquared = 10000000;
        minLinear = 10000000;
        minMax = 10000000;

        final int len = (int) Math.ceil((max - min) / skip) + 1;
        yLinear = new double[2][len];
        ySquared = new double[2][len];
        yMax = new double[2][len];
        //x = new double[(int)Math.floor( (max-min)/skip )];

        totalMax = 0;

        int c = 1;
        for (int i = 0; i < len; ++i) {
            yLinear[0][i] = i * skip + min;
            ySquared[0][i] = i * skip + min;
            yMax[0][i] = i * skip + min;
            //x[i] = i * skip + min;
            stairSpeed(i, i * skip + min, dps);
            System.out.print(".");
            if (c++ % 100 == 0) {
                System.out.println();

            }
        }

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

    double[][] yLinear;
    double[][] ySquared;
    double[][] yMax;
    double[] x;
    double totalMax;

    public void stairSpeed(int index, double factor, DefaultParameterSet dps) {
        int midCount = 0;
        double midCum = 0;
        int highCount = 0;
        double highCum = 0;
        int lowCount = 0;
        double lowCum = 0;
        for (int i = 1; i <= runs; ++i) {
            double age = ageDistribution.getNextRandom();
            double speed = dps.getSpeedFromAge(age);
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
//
//		System.out.println( "Average    - 30: " + lowAv );
//		System.out.println( "Average 30 - 50: " + midAv );
//		System.out.println( "Average 50 -   : " + highAv );
        double lowAge = Math.abs(lowAv - stairTests.lowAgeReference);
        double midAge = Math.abs(midAv - stairTests.midAgeReference);
        double highAge = Math.abs(highAv - stairTests.highAgeReference);

        double linearSum = lowAge + highAge + midAge;
        double squareSum = lowAge * lowAge + midAge * midAge + highAge * highAge;
        double maxSum = Math.max(lowAge, Math.max(midAge, highAge));
//		System.out.println( "Abweichung (linear): " + linearSum );
//		System.out.println( "Abweichung (squared): " + squareSum );
//		System.out.println( "Maximale Abweichung:" + max );

        yLinear[1][index] = linearSum;//new double[(int)Math.floor( (max-min)/skip )];
        ySquared[1][index] = squareSum; //new double[(int)Math.floor( (max-min)/skip )];
        yMax[1][index] = maxSum; // = new double[(int)Math.floor( (max-min)/skip )];
        totalMax = Math.max(totalMax, Math.max(linearSum, Math.max(squareSum, maxSum)));

        if (linearSum < minLinear) {
            minLinear = linearSum;
            minLinearFactor = factor;
        }
        if (squareSum < minSquared) {
            minSquared = squareSum;
            minSquaredFactor = factor;
        }
        if (maxSum < minMax) {
            minMax = maxSum;
            minMaxFactor = factor;
        }
    }

    XYLineChart chart;

    public void showDiagram(String title) {
        // Show Diagrams
        chart = new XYLineChart(title, "Faktor", "Abweichung", yLinear, ySquared, yMax);
        chart.getXAxis().setLabels((max - min) / 10);
        double[] range = {min, max, 0, totalMax};
        chart.setManualRange(range);

        chart.view(640, 480);
    }

    public void latexOut(String filename, double width, double height) throws IOException {
        File file = new File(filename);
        FileWriter wr = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(wr);

        bw.write(chart.toLatex(width, height));

        bw.close();
        wr.close();
    }

    private void latexOut(String string) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private enum StairTests {

        InsideUp(0.55, 0.50, 0.42),
        InsideDown(0.76, 0.65, 0.55),
        OutsideUp(0.58, 0.58, 0.42),
        OutsideDown(0.81, 0.78, 0.59);

        double lowAgeReference;
        double midAgeReference;
        double highAgeReference;

        StairTests(double lowAge, double midAge, double highAge) {
            this.lowAgeReference = lowAge;
            this.midAgeReference = midAge;
            this.highAgeReference = highAge;
        }
    }

    public static void main(String[] arguments) throws IOException {
        DefaultParameterSetTest dpst = new DefaultParameterSetTest();
        dpst.setRuns(100000);
        dpst.setStairTests(StairTests.InsideUp);
        dpst.testStairSpeed(0.01, 0.0, 1.0);
        dpst.showDiagram("Innentreppen hoch");
        dpst.latexOut("./latex/Treppen - Innen aufwärts.tex", 10, 10);

        dpst.setRuns(10000000);
        dpst.testStairSpeed(0.001, 0.34, 0.36);
        dpst.showDiagram("Innentreppen hoch");
        dpst.latexOut("./latex/Treppen - Innen aufwärts (Detail).tex", 10, 10);

        dpst.setRuns(100000);
        dpst.setStairTests(StairTests.InsideDown);
        dpst.testStairSpeed(0.01, 0.0, 1.0);
        dpst.showDiagram("Innentreppen runter");
        dpst.latexOut("./latex/Treppen - Innen abwärts.tex", 10, 10);

        dpst.setRuns(10000000);
        dpst.testStairSpeed(0.001, 0.46, 0.48);
        dpst.showDiagram("Innentreppen runter");
        dpst.latexOut("./latex/Treppen - Innen abwärts (Detail).tex", 10, 10);

        dpst.setRuns(100000);
        dpst.setStairTests(StairTests.OutsideUp);
        dpst.testStairSpeed(0.01, 0.0, 1.0);
        dpst.showDiagram("Außentreppen hoch");
        dpst.latexOut("./latex/Treppen - Außen aufwärts.tex", 10, 10);

        dpst.setRuns(10000000);
        dpst.testStairSpeed(0.001, 0.37, 0.39);
        dpst.showDiagram("Außentreppen hoch");
        dpst.latexOut("./latex/Treppen - Außen aufwärts (Detail).tex", 10, 10);

        dpst.setRuns(100000);
        dpst.setStairTests(StairTests.OutsideDown);
        dpst.testStairSpeed(0.01, 0.0, 1.0);
        dpst.showDiagram("Außentreppen runter");
        dpst.latexOut("./latex/Treppen - Außen abwärts.tex", 10, 10);

        dpst.setRuns(10000000);
        dpst.testStairSpeed(0.001, 0.515, 0.535);
        dpst.showDiagram("Außentreppen runter");
        dpst.latexOut("./latex/Treppen - Außen abwärts (Detail).tex", 10, 10);
    }

}
