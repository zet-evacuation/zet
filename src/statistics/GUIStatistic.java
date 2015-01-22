/**
 * GUIStatistic.java
 * input:
 * output:
 *
 * method:
 *
 * Created: May 12, 2010,3:50:11 PM
 */
package statistics;

import org.zetool.rndutils.distribution.Distribution;
import org.zetool.rndutils.distribution.continuous.NormalDistribution;
import de.tu_berlin.coga.zet.model.ZControl;
import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.statistics.HistogramBin;
import umontreal.iro.lecuyer.charts.BoxChart;
import umontreal.iro.lecuyer.charts.EmpiricalChart;
import umontreal.iro.lecuyer.charts.HistogramChart;
import umontreal.iro.lecuyer.charts.MultipleDatasetChart;
import umontreal.iro.lecuyer.charts.XYLineChart;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GUIStatistic {

	Statistic statistic;

	public GUIStatistic( Statistic statistic ) {
		this.statistic = statistic;
	}

	public JFreeChart getAgeHistogram() {
		double[] values = statistic.getAgeCollector().getCompleteDataSet().getArray();
//
//		// count
//		int counter = 0;
//
		final int range = 5;
//
		Arrays.sort( values );
//		for( int i = 0; i < values1.length; ++i )
//			if( values1[i] >= 18 && values1[i] <= 80 )
//				counter++;
//
//
//		double[] values = new double[counter];
//		int index = 0;
//		for( int i = 0; i < values1.length; ++i )
//			if( values1[i] >= 18 && values1[i] <= 80 )
//				values[index++] = values1[i];

		HistogramChart histogram = new HistogramChart( "Altersverteilung", "X", "Y", values );

		histogram.getSeriesCollection().setBins( 0, 14, 17.5, 87.5 );
		//histogram.getSeriesCollection().setBins( 0, 30, -25, 125 );
		//histogram.getSeriesCollection().setBins( 1, 30, -25, 125 );

		int max = 0;
		//List l;
		List<HistogramBin> list = histogram.getSeriesCollection().getBins( 0 );
		for( HistogramBin bin : list )
			max = Math.max( max, bin.getCount() );

		histogram.getSeriesCollection().setMargin( 0.1 );
//		System.out.println( Arrays.toString( values ) );
		//System.out.println( Helper.arrayToString( values ) );

		//chart.setChartMargin( 1 );
		//System.out.println( max );
		//chart.view( 640, 460 );

		double[][] densityData = new double[2][100];
		double[][] densityData2 = new double[2][100];
		final int count = Statistic.instance.getNumberOfPersons();
		final int count2 = Statistic.instance.getAgeTries();
		Distribution ageDist = ZControl.getDefaultAssignmentTypeDistribution( "age" );
		final double densityMinX = 10;
		final double densityMaxX = 90;
		for( int i = 0; i < densityData[0].length; ++i ) {
			double x = densityMinX + i * (densityMaxX - densityMinX) / (densityData[0].length - 1);
			densityData[0][i] = x;

			densityData[1][i] = (ageDist instanceof NormalDistribution ? ageDist.getDensityAt( x, true ) : ageDist.getDensityAt( x )) * count * range;
			densityData2[0][i] = x;
			densityData2[1][i] = (ageDist instanceof NormalDistribution ? ageDist.getDensityAt( x, true ) : ageDist.getDensityAt( x )) * count2 * range;
			max = Math.max( max, (int) Math.ceil( ageDist.getDensityAt( x ) * count * range ) );
		}


		// Create a new chart with the previous data series.
		XYLineChart density = new XYLineChart( "Title", "X", "Y", densityData );
		XYLineChart density2 = new XYLineChart( "Title", "X", "Y", densityData2 );


		MultipleDatasetChart mdc = new MultipleDatasetChart( "Histogramm der Altersverteilung", "Alter", "Häufigkeit");
		double[] visibleArea = { 10, 90, 0, max*1.1 };

		density.getJFreeChart().getXYPlot().getRenderer().setSeriesPaint( 0, Color.BLUE );
		density2.getJFreeChart().getXYPlot().getRenderer().setSeriesPaint( 0, Color.green );

		mdc.add( density.getSeriesCollection() );
		mdc.add( density2.getSeriesCollection() );
		mdc.add( histogram.getSeriesCollection() );
    mdc.setManualRange( visibleArea );
		try {
			toLatexFile( mdc, "./latex/Histogram.tex", 10, 10 );
		} catch( IOException ex ) {
			Logger.getLogger( GUIStatistic.class.getName() ).log( Level.SEVERE, null, ex );
		}
		//mdc.view( 640, 480 );


		EmpiricalChart empirical = new EmpiricalChart( "Empirisch", "X", "Y", values );
		

		double[][] distributionData = new double[2][100];
		if( ageDist instanceof NormalDistribution ) {
			NormalDistribution normalDist = (NormalDistribution)ageDist;
			final double minX = 0.001;
			final double maxX = 0.999;
			for( int i = 1; i <= distributionData[0].length; ++i ) {
				final double y = i * (maxX - minX) / (distributionData[0].length);
				final double x = normalDist.getInverseCumulatedDensityFunction( y );//
				distributionData[0][i-1] = x;
				distributionData[1][i-1] = y;
			}
		}

		// Create a new chart with the previous data series.
		XYLineChart distribution = new XYLineChart( "Title", "X", "Y", distributionData );

		MultipleDatasetChart mdcDistribution = new MultipleDatasetChart( "Altersverteilung", "Alter", "Vorkommen");

		mdcDistribution.add( distribution.getSeriesCollection() );
		mdcDistribution.add( empirical.getSeriesCollection() );
		//mdcDistribution.toLatex( minX, minX )
		distribution.getJFreeChart().getXYPlot().getRenderer().setSeriesPaint( 0, Color.BLUE );

		double[] visibleArea2 = { 10, 90, 0, 1 };
		mdcDistribution.setManualRange( visibleArea2 );
		mdcDistribution.getXAxis().setLabels( 10 );
//		try {
//			toLatexFile( mdcDistribution, "./latex/Distribution.tex", 10, 10 );
//		} catch( IOException ex ) {
//			Logger.getLogger( GUIStatistic.class.getName() ).log( Level.SEVERE, null, ex );
//		}
		//mdcDistribution.view( 640, 480 );




		double[][] data = new double[3][];
		double[] gesamt = statistic.speedCollector.getValues();
		// Speed stuff
		for( int i = 0; i < 3; ++i ) {
			statistic.speedCollector.printBin( i );
			data[i] = statistic.speedCollector.getValues( i );
		}
		BoxChart bc = new BoxChart("Boxplot1", "Series", "Y", data[0], data[1], data[2], gesamt );
		DatasetGroup dg = new DatasetGroup( "Test" );

		bc.getSeriesCollection().getSeriesCollection().setGroup( dg );
		CategoryPlot cp = (CategoryPlot)bc.getJFreeChart().getPlot();
		System.out.println( cp.getCategories().get( 0 ).getClass() );
//		if( bc.getJFreeChart().getPlot() instanceof org.jfree.chart.entity.PlotEntity
// ) {
//			System.out.println( "IS PLOT ENTITY" );
//		} else {
//			System.out.println( "IS NOT PLOT ENTITY" );
//
//		}

		//for( int i = 0; i < 3; ++i ) {
		//bc.getJFreeChart().getCategoryPlot()
//		cp.getCategories().set( 0, "< 30" );
//		cp.getCategories().set( 1, "30 - 50" );
//		cp.getCategories().set( 2, "> 50" );
//		cp.getCategories().set( 3, "Gesamt" );

		//}
		//;
		//System.out.println( cp.getCategories() );
		//System.out.println( cp.getCategories().get( 0 ).getClass() );
		//DefaultBoxAndWhiskerCategoryDataset dbawc = (DefaultBoxAndWhiskerCategoryDataset)cp.getDataset( 0 );
		//cp.getCategoriesForAxis( )
		//System.out.println( cp.getDataset( 0 ).toString() );
		//System.out.println( bc.getJFreeChart().getPlot() .getXYPlot().toString() );
		bc.getYAxis().setLabels( 0.1 );
		bc.view(600, 400);


		// detailliert
		statistic.speedCollector.setDetailed();
		data = new double[statistic.speedCollector.getNumberOfBins()][];
		for( int i = 0; i < statistic.speedCollector.getNumberOfBins(); ++i ) {
			statistic.speedCollector.printBin( i );
			data[i] = statistic.speedCollector.getValues( i );
		}
		BoxChart dbc = new BoxChart( "Detailliert", "Jahrgänge", "Geschwindigkeit" );
		for( int i = 0; i < statistic.speedCollector.getNumberOfBins(); ++i ) {
			dbc.add( data[i] );
		}
		dbc.add( gesamt );
		dbc.getYAxis().setLabels( 0.1 );
//		try {
//			toLatexFile( dbc.toLatex( 10, 10 ), "./latex/Distribution.tex" );
//		} catch( IOException ex ) {
//			Logger.getLogger( GUIStatistic.class.getName() ).log( Level.SEVERE, null, ex );
//		}
		dbc.view( 640, 480 );


		return null;
	}

	public void toLatexFile( MultipleDatasetChart chart, String filename, double width, double height ) throws IOException {
		toLatexFile( chart.toLatex( width, height ), filename );
	}

	public void toLatexFile( String latex, String filename ) throws IOException {
		File file = new File( filename );
		FileWriter wr = new FileWriter( file );
		BufferedWriter bw = new BufferedWriter( wr );

		bw.write( latex );

		bw.close();
		wr.close();
	}
}
