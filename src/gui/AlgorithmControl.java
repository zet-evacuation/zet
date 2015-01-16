/**
 * AlgorithmControl.java
 * Created: 27.07.2010 14:49:25
 */
package gui;

import algo.ca.algorithm.evac.EvacuationSimulationProblem;
import algo.ca.framework.EvacuationCellularAutomatonAlgorithm;
import algo.graph.exitassignment.Assignable;
import algo.graph.exitassignment.EarliestArrivalTransshipmentExitAssignment;
import algo.graph.exitassignment.ExitAssignment;
import org.zetool.common.algorithm.Algorithm;
import org.zetool.common.algorithm.AlgorithmListener;
import org.zetool.common.util.Formatter;
import org.zetool.common.util.Quantity;
import org.zetool.common.util.units.TimeUnits;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.AssignmentApplicationInstance;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.CellularAutomatonAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ConvertedCellularAutomaton;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import de.tu_berlin.math.coga.zet.converter.graph.GraphAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphRasterContainer;
import ds.CompareVisualizationResults;
import ds.GraphVisualizationResults;
import ds.PropertyContainer;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.ca.results.VisualResultsRecorder;
import de.tu_berlin.coga.zet.model.BuildingPlan;
import de.tu_berlin.coga.zet.model.ConcreteAssignment;
import de.tu_berlin.coga.zet.model.Project;
import evacuationplan.BidirectionalNodeCellMapping;
import evacuationplan.BidirectionalNodeCellMapping.CAPartOfMapping;
import exitdistributions.GraphBasedIndividualToExitMapping;
import io.visualization.BuildingResults;
import io.visualization.EvacuationSimulationResults;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import statistic.ca.CAStatistic;
import tasks.conversion.BuildingPlanConverter;
import zet.tasks.CellularAutomatonAlgorithms;
import zet.tasks.CompareTask;
import zet.tasks.GraphAlgorithmEnumeration;
import zet.tasks.GraphAlgorithmTask;
import zet.tasks.SerialTask;


/**
 * A class that starts, stops and pauses the algorithms that can be used in
 * ZET.
 * @author Jan-Philipp Kappmeier
 */
public class AlgorithmControl implements PropertyChangeListener {
	/** The logger of the main class. */
	private static final Logger log = Logger.getGlobal();

	private AlgorithmControlCellularAutomaton caControl = new AlgorithmControlCellularAutomaton();

	private BuildingResults buildingResults;
	private Project project;
	private NetworkFlowModel networkFlowModel;
	private GraphVisualizationResults graphVisResults;
  private CompareVisualizationResults compVisResults;
	//private boolean createdValid = false;
	private RuntimeException error;


	public AlgorithmControl( Project project ) {
		this.project = project;
	}

	public boolean isError() {
		return error != null;
	}

	public RuntimeException getError() {
		return error;
	}

	public void throwError() {
		throw error;
	}

	void setProject( Project project ) {
		this.project = project;
	}

	public void convertBuildingPlan( ) {
		convertBuildingPlan( null );
	}
	Quantity<TimeUnits> conversionTime;
	public void convertBuildingPlan( PropertyChangeListener pcl ) {
		final BuildingPlanConverter bpc = new BuildingPlanConverter();
		bpc.setProblem( project.getBuildingPlan() );

		final SerialTask st = new SerialTask();
		st.add( bpc );
		st.addPropertyChangeListener( new PropertyChangeListener() {

			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( st.isDone() ) {
					buildingResults = bpc.getSolution();
					conversionTime = bpc.getRuntime();
				}
			}
		});
		if( pcl != null)
			st.addPropertyChangeListener( pcl );
		st.execute();
		//bpc.run();
		//buildingResults = bpc.getSolution();
	}

	public Quantity<TimeUnits> getConversionRuntime() {
		return conversionTime;
	}

	public BuildingResults getBuildingResults() {
		return buildingResults;
	}



	@Override
	public void propertyChange( PropertyChangeEvent pce ) {
		System.out.println( pce.getPropertyName() );
	}

	public void convertGraph() {
		convertGraph( null, GraphConverterAlgorithms.NonGridGraph );
	}

	GraphConverterAlgorithms last = GraphConverterAlgorithms.NonGridGraph;

	public RunnableFuture<Void> convertGraph( PropertyChangeListener propertyChangeListener, GraphConverterAlgorithms Algo ) {
		if( project.getBuildingPlan().isRastered() == false ) {
			System.out.print( "Building is not rasterized. Rastering... " );
			project.getBuildingPlan().rasterize();
			System.out.println( " done." );
		}
		final Algorithm<BuildingPlan,NetworkFlowModel> conv = Algo.converter();
		last = Algo;
		conv.setProblem( project.getBuildingPlan() );
		final SerialTask st = new SerialTask( conv );
		st.addPropertyChangeListener( new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( st.isDone() ) {
					if( st.isError() ) {
						System.err.println( "An error occured:" );
						st.getError().printStackTrace( System.out );
					} else {
            System.out.println( st );
						networkFlowModel = conv.getSolution();
						System.out.println( "Nodes: " + networkFlowModel.numberOfNodes() );
						System.out.println( "Edges: " + networkFlowModel.numberOfEdges() );
					}
				}
			}
		} );
		if( propertyChangeListener != null )
			st.addPropertyChangeListener( propertyChangeListener );
		st.execute();
		return st;
	}

	public NetworkFlowModel getNetworkFlowModel() {
		return networkFlowModel;
	}

	public void performOptimization( GUIControl control) {
		performOptimization( null, control );
	}

	public void performOptimization( PropertyChangeListener propertyChangeListener, AlgorithmListener control ) {
		if( !project.getBuildingPlan().isRastered() ) {
			System.out.print( "Building is not rasterized. Rastering... " );
			project.getBuildingPlan().rasterize();
			System.out.println( " done." );
		}

		final GraphAlgorithmTask gat = new GraphAlgorithmTask( GraphAlgorithmEnumeration.SuccessiveEarliestArrivalAugmentingPathOptimized );
		gat.setProblem( project );

		gat.addAlgorithmListener( control );


		gat.setNetworkFlowModel( networkFlowModel );
		gat.setConv( last.converter() );

		final SerialTask st = new SerialTask( gat );
		st.addPropertyChangeListener( new PropertyChangeListener() {

			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( st.isDone() ) {
					if( st.isError() ) {
						System.err.print( "Error occured." );
						st.getError().printStackTrace( System.err );
					} else {
						networkFlowModel = gat.getNetworkFlowModel();
						graphVisResults = gat.getSolution();
					}
				}
			}
		});
		if( propertyChangeListener != null )
			st.addPropertyChangeListener( propertyChangeListener );
		st.execute();
	}

	public void performExitAssignmentEAT( PropertyChangeListener propertyChangeListener, AlgorithmListener control ) {

		if( networkFlowModel == null ) {
			log.severe( "No model created." );
			return;
		}

		EarliestArrivalTransshipmentExitAssignment eatAssignment;
		eatAssignment = new EarliestArrivalTransshipmentExitAssignment();
		//ZToGraphConverter.convertConcreteAssignment( concreteAssignments[runNumber], res.getNetworkFlowModel() );

		log.info( "Compute concrete assignment..." );
		ConcreteAssignment concreteAssignment;

		concreteAssignment = project.getCurrentAssignment().createConcreteAssignment( 400 );
		GraphAssignmentConverter cav = new GraphAssignmentConverter( networkFlowModel );
		cav.setProblem( concreteAssignment );
		cav.run();
		networkFlowModel = cav.getSolution();
		log.log( Level.INFO, "Persons: {0}", concreteAssignment.getPersons().size());
		log.info( "done." );

		eatAssignment.setProblem( networkFlowModel );
		log.info( "Compute exit assignment..." );
		eatAssignment.run();
		log.info( "done." );
		Assignable exitAssignmenta = eatAssignment;

		ExitAssignment exitAssignment = eatAssignment.getExitAssignment();

		log.info( "Computed ExitAssignment: " );
		log.info( exitAssignment.toString() );

		log.info( "Create Cellular Automaton according to the exit assignment..." );

		// convert
		final ZToCAConverter conv = new ZToCAConverter();
		conv.setProblem( project.getBuildingPlan() );
		conv.run();
		EvacuationCellularAutomaton ca = conv.getCellularAutomaton();
						ZToCARasterContainer container;
						EvacuationSimulationResults caVisResults;
						ZToCAMapping mapping;
		mapping = conv.getMapping();
		container = conv.getContainer();
		final ConvertedCellularAutomaton cca = new ConvertedCellularAutomaton( ca, mapping, container );

		CAPartOfMapping caPartOfMapping = conv.getLatestCAPartOfNodeCellMapping();//this.getLatestCAPartOfNodeCellMapping();

		final CellularAutomatonAssignmentConverter cac = new CellularAutomatonAssignmentConverter();

		cac.setProblem( new AssignmentApplicationInstance( cca, concreteAssignment ) );
		cac.run();
		ZToGraphRasterContainer graphRaster = getNetworkFlowModel().getZToGraphMapping().getRaster();
		BidirectionalNodeCellMapping nodeCellMapping = new BidirectionalNodeCellMapping( graphRaster, caPartOfMapping );
		//GraphBasedExitToCapacityMapping graphBasedExitToCapacityMapping = null;
		GraphBasedIndividualToExitMapping graphBasedIndividualToExitMapping;
		graphBasedIndividualToExitMapping = new GraphBasedIndividualToExitMapping(ca, nodeCellMapping, exitAssignment);
		graphBasedIndividualToExitMapping.calculate();
		ca.setIndividualToExitMapping( graphBasedIndividualToExitMapping );

		// Now, we have a CA

		//		EvacuationCellularAutomaton ca = super.convert(buildingPlan);
//		CAPartOfMapping caPartOfMapping = this.getLatestCAPartOfNodeCellMapping();
//		applyConcreteAssignment(concreteAssignment);
//		BidirectionalNodeCellMapping nodeCellMapping = new BidirectionalNodeCellMapping(graphRaster, caPartOfMapping);
//		graphBasedIndividualToExitMaping = new GraphBasedIndividualToExitMapping(ca, nodeCellMapping, exitAssignment);
//		graphBasedIndividualToExitMaping.calculate();
//		ca.setIndividualToExitMapping( graphBasedIndividualToExitMaping );
//		return ca;
		log.info( "done." );

		// Perform CA simulation
		log.info( "Performing Simulation..." );

		EvacuationCellularAutomatonAlgorithm caAlgo = CellularAutomatonAlgorithms.InOrder.getAlgorithm();
		caAlgo.setProblem( new EvacuationSimulationProblem( ( ca ) ) );
		double caMaxTime = PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" );
		caAlgo.setMaxTimeInSeconds( caMaxTime );
		ca.startRecording ();

		//caAlgo.addAlgorithmListener( this );

		caAlgo.run();	// hier wird initialisiert
		ca.stopRecording();

		// create results
		//CAVisualizationResults visResults = new EvacuationSimulationResults( mapping, ca.getPotentialManager() );
		// TODO visualResultsRecorder normal class, no singleton.
		EvacuationSimulationResults visResults = new EvacuationSimulationResults( VisualResultsRecorder.getInstance().getRecording(), mapping, ca );

    
    //caAlgo.getProblem().caStatisticWriter.getStoredCAStatisticResults().
    visResults.statistic = new CAStatistic (caAlgo.getProblem().caStatisticWriter.getStoredCAStatisticResults ());
    
    EvacuationCellularAutomaton cellularAutomaton = ca;
    //mapping = mapping;
    //container = cca;
    container = conv.getContainer();
    caVisResults = visResults;

    caControl.tempSetParametersFromEx( caVisResults, ca );

    //EventServer.getInstance().dispatchEvent( new MessageEvent<>( this, MessageType.Status, "Simulation finished" ) );
		log.log(Level.INFO, "Egress time: {0}", Formatter.formatUnit( cellularAutomaton.getTimeStep() * cellularAutomaton.getSecondsPerStep(), TimeUnits.Seconds ));

		log.info( "done." );
	}




	public void performOptimizationCompare( PropertyChangeListener propertyChangeListener ) {
		if( !project.getBuildingPlan().isRastered() ) {
			System.out.print( "Building is not rasterized. Rastering... " );
			project.getBuildingPlan().rasterize();
			System.out.println( " done." );
		}

		final CompareTask ct = new CompareTask( GraphAlgorithmEnumeration.SuccessiveEarliestArrivalAugmentingPathOptimizedCompare );
		ct.setProblem( project );

		//values for original network
		GraphConverterAlgorithms ConvOrig = GraphConverterAlgorithms.NonGridGraph;
		ct.setConvOriginal( ConvOrig.converter() );

		//values for thin network
		ct.setConvThinNet( last.converter() );
		ct.setThinNetwork( networkFlowModel );


		final SerialTask st = new SerialTask( ct );
		st.addPropertyChangeListener( new PropertyChangeListener() {
			public void propertyChange( PropertyChangeEvent pce ) {
				if( st.isDone() ) {
					networkFlowModel = ct.getOriginal();
					compVisResults = ct.getSolution();
				}
			}
		} );
		if( propertyChangeListener != null )
			st.addPropertyChangeListener( propertyChangeListener );
		System.out.println( "done" );
		st.execute();
		System.out.println( "done" );
	}

	public GraphVisualizationResults getGraphVisResults() {
		return graphVisResults;
	}

	public CompareVisualizationResults getCompVisResults() {
		return compVisResults;
	}

	void performSimulation( PropertyChangeListener propertyChangeListener, AlgorithmListener listener ) {
		caControl.performSimulation( project, propertyChangeListener, listener );
	}

	void pauseSimulation() {
		caControl.pauseStepByStep();
	}

	void performOneStep( AlgorithmListener listener ) {
		caControl.performOneStep( project, listener );
	}

	void performSimulationQuick( AlgorithmListener listener ) {
		caControl.performSimulationQuick( project, listener );
	}

	public EvacuationCellularAutomaton getCellularAutomaton() {
		return caControl.getCellularAutomaton();
	}

	public ZToCARasterContainer getContainer() {
		return caControl.getContainer();
	}

	public ZToCAMapping getMapping() {
		return caControl.getMapping();
	}

	void convertCellularAutomaton( PropertyChangeListener propertyChangeListener ) {
		caControl.convertCellularAutomaton( project.getBuildingPlan(), propertyChangeListener );
	}

	EvacuationSimulationResults getCaVisResults() {
		return caControl.getCaVisResults();
	}

	void setSimulationAlgorithm( CellularAutomatonAlgorithms cellularAutomaton ) {
		caControl.setSimulationAlgorithm( cellularAutomaton );
	}

	CellularAutomatonAlgorithms getSimulationAlgorithm() {
		return caControl.getSimulationAlgorithm();
	}


}
