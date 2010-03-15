/**
 * FlowVisualization.java
 * Created: 15.03.2010, 12:29:16
 */
package zet.viewer;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.LongestShortestPathTimeHorizonEstimator;
import algo.graph.dynamicflow.eat.SEAAPAlgorithm;
import batch.tasks.AlgorithmTask;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmListener;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmProgressEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmStartedEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmTerminatedEvent;
import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.common.util.Formatter;
import ds.GraphVisualizationResult;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Node;
import ds.graph.flow.PathBasedFlowOverTime;
import gui.editor.GUIOptionManager;
import gui.visualization.Visualization;
import gui.visualization.control.graph.GLGraphControl;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import zet.FileFlow;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FlowVisualization extends JFrame implements AlgorithmListener {
	Localization loc = Localization.getInstance();

	public FlowVisualization() {
		super();
		loc.setLocale( Locale.getDefault() );

		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setSize( 800, 600 );
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( (d.width - getSize().width) / 2, (d.height - getSize().height) / 2 );

		this.setTitle( "Fluss-Visualisierung" );
		this.setSize( 800, 600 );
		this.setLayout( new BorderLayout() );
		Visualization<GLGraphControl> vis = new Visualization<GLGraphControl>(new GLCapabilities() );
		add( vis, BorderLayout.CENTER );
		vis.set3DView();

		//String path = "./testinstanz/4 rooms demo.dat";
		String path = "./testinstanz/swissold_1_10s.dat";

		IdentifiableIntegerMapping<Node> xPos = new IdentifiableIntegerMapping<Node> ( 0 );
		IdentifiableIntegerMapping<Node> yPos = new IdentifiableIntegerMapping<Node> ( 0 );

		EarliestArrivalFlowProblem eafp = null;
		try {
			// = new IdentifiableIntegerMapping<Node> ();
			eafp = FileFlow.read( path, xPos, yPos );

			int timeHorizon = eafp.getTimeHorizon() ;
			if( timeHorizon <= 0 ) {
				System.out.println( "Schätze Zeithorizont" );
				LongestShortestPathTimeHorizonEstimator estimator = new LongestShortestPathTimeHorizonEstimator();
				estimator.setProblem( eafp );
				estimator.run();
				System.out.println( "Geschätzter Zeithorizont: " + estimator.getSolution().getUpperBound() );
			}

			eafp = new EarliestArrivalFlowProblem(eafp.getEdgeCapacities(), eafp.getNetwork(), eafp.getNodeCapacities(), eafp.getSink(), eafp.getSources(), timeHorizon, eafp.getTransitTimes(), eafp.getSupplies() );
		} catch( FileNotFoundException ex ) {
			Logger.getLogger( FlowVisualization.class.getName() ).log( Level.SEVERE, null, ex );
			System.exit( 1 );
		} catch( IOException ex ) {
			Logger.getLogger( FlowVisualization.class.getName() ).log( Level.SEVERE, null, ex );
			System.exit( 1 );
		}

		// Fluss bestimmen
		PathBasedFlowOverTime df = computeFlow( eafp );

		// Control besorgen
		GraphVisualizationResult graphVisResult = new GraphVisualizationResult( eafp, xPos, yPos, df );
		GLGraphControl control = new GLGraphControl( graphVisResult );
		vis.setControl( control );
		vis.update();
		vis.repaint();
		vis.startAnimation();
	}

	public PathBasedFlowOverTime computeFlow( EarliestArrivalFlowProblem eat ) {
		SEAAPAlgorithm algo = new SEAAPAlgorithm();
		algo.setProblem( eat );
		algo.addAlgorithmListener( this );
		algo.run();
		//System.out.println( Formatter.formatTimeMilliseconds( algo.getRuntime() ) );
		PathBasedFlowOverTime df = algo.getSolution().getPathBased();
		String result = String.format( "Sent %1$s of %2$s flow units in %3$s time units successfully.", algo.getSolution().getFlowAmount(), eat.getTotalSupplies(), algo.getSolution().getTimeHorizon() );
		System.out.println( result );
		AlgorithmTask.getInstance().publish( 100, result, "" );

		return df;
	}

	public static void main( String[] arguments ) {
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				// Change look and feel to native
				GUIOptionManager.changeLookAndFeel( UIManager.getSystemLookAndFeelClassName() );

				// Start the viewer in the event-dispatch-thread
				FlowVisualization fv = new FlowVisualization();
				fv.setVisible( true );
			}
		} );
	}

	public void eventOccurred( AlgorithmEvent event ) {
		if( event instanceof AlgorithmProgressEvent )
			System.out.println( ((AlgorithmProgressEvent)event).getProgress() );
		else if( event instanceof AlgorithmStartedEvent )
			System.out.println( "Algorithmus startet." );
		else if( event instanceof AlgorithmTerminatedEvent )
			System.out.println( "Laufzeit Flussalgorithmus: " + Formatter.formatTimeMilliseconds( event.getAlgorithm().getRuntime() ) );
		else
			System.out.println( event.toString() );
	}
}
