/**
 * AlgorithmControl.java
 * Created: 27.07.2010 14:49:25
 */
package gui;

import algo.ca.EvacuationCellularAutomatonAlgorithm;
import batch.CellularAutomatonAlgorithm;
import batch.GraphAlgorithm;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmListener;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.AssignmentApplicationInstance;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.CellularAutomatonAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ConvertedCellularAutomaton;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter.ConversionNotSupportedException;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.BaseZToGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToDijkstraSpannerConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGreedySpannerConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGridDijkstraConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGridGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGridGreedyConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToNonGridGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToSpanTreeConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGridSpanTreeConverter;
import ds.GraphVisualizationResults;
import ds.z.Project;
import ds.PropertyContainer;
import ds.ca.CellularAutomaton;
import ds.z.AssignmentType;
import ds.z.ConcreteAssignment;
import io.visualization.BuildingResults;
import io.visualization.CAVisualizationResults;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import tasks.CellularAutomatonTask;
import tasks.CellularAutomatonTaskStepByStep;
import tasks.GraphAlgorithmTask;
import tasks.SerialTask;
import tasks.conversion.BuildingPlanConverter;


/**
 * A class that starts, stops and pauses the algorithms that can be used in
 * ZET.
 * @author Jan-Philipp Kappmeier
 */
public class AlgorithmControl implements PropertyChangeListener {

	private BuildingResults buildingResults;
	private Project project;
	private CellularAutomaton cellularAutomaton;
	private ConcreteAssignment concreteAssignment;
	private EvacuationCellularAutomatonAlgorithm caAlgo;
	private ZToCAMapping mapping;
	private ZToCARasterContainer container;
	private CAVisualizationResults caVisResults;
	private NetworkFlowModel networkFlowModel;
	private GraphVisualizationResults graphVisResults;
	private final CellularAutomatonTask cat = new CellularAutomatonTask();
	//private boolean createdValid = false;
	private RuntimeException error;
	private CellularAutomatonTaskStepByStep catsbs;


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
	
	public void convertBuildingPlan( PropertyChangeListener pcl ) {
		final BuildingPlanConverter bpc = new BuildingPlanConverter();
		bpc.setProblem( project.getBuildingPlan() );

		final SerialTask st = new SerialTask();
		st.add( bpc );
		st.addPropertyChangeListener( new PropertyChangeListener() {

			public void propertyChange( PropertyChangeEvent pce ) {
				if( st.isDone() )
					buildingResults = bpc.getSolution();
			}
		});
		if( pcl != null)
			st.addPropertyChangeListener( pcl );
		st.execute();
	}

	public BuildingResults getBuildingResults() {
		return buildingResults;
	}

	public void convertCellularAutomaton( ) {
		convertCellularAutomaton( null );
	}

	void convertCellularAutomaton( PropertyChangeListener propertyChangeListener ) {
		error = null;
		final ZToCAConverter conv = new ZToCAConverter();

		conv.setProblem( project.getBuildingPlan() );

		final SerialTask st = new SerialTask( conv );
		st.addPropertyChangeListener( new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( st.isDone() ) {
					if( st.isError() ) {
						error = st.getError();
					} else {
						cellularAutomaton = conv.getCellularAutomaton();
						mapping = conv.getMapping();
						container = conv.getContainer();
						caInitialized = true;
					}
				}
			}
		});
		if( propertyChangeListener != null )
			st.addPropertyChangeListener( propertyChangeListener );

		st.execute();
	}

	public void invalidateConvertedCellularAutomaton() {
		caInitialized = false;
	}

	public CellularAutomaton getCellularAutomaton() {
		return cellularAutomaton;
	}

	public ZToCARasterContainer getContainer() {
		return container;
	}

	public ZToCAMapping getMapping() {
		return mapping;
	}

	void performSimulation() {
		performSimulation( null );
	}

	void performSimulation( PropertyChangeListener propertyChangeListener ) {
		//final CellularAutomatonTask cat = new CellularAutomatonTask();
		cat.setCaAlgo( CellularAutomatonAlgorithm.RandomOrder );
		cat.setProblem( project );

		final SerialTask st = new SerialTask( cat );
		st.addPropertyChangeListener( new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( st.isDone() ) {
					cellularAutomaton = cat.getCa();
					mapping = cat.getMapping();
					container = cat.getContainer();
					caVisResults = cat.getSolution();
				}
			}
		});
		if( propertyChangeListener != null )
			st.addPropertyChangeListener( propertyChangeListener );
		st.execute();
	}

	void performSimulationQuick( PropertyChangeListener propertyChangeListener, AlgorithmListener listener ) {
		//final CellularAutomatonTask cat = new CellularAutomatonTask();
		
		if( catsbs == null ) {
			initStepByStep( propertyChangeListener, listener, false );
			System.out.println( "Start slow execution..." );
		} else {
			catsbs.setStopMode( false );
		}
	}

	public CAVisualizationResults getCaVisResults() {
		if( cat.isProblemSolved() )
			return cat.getSolution();
		else return null;
	}

	void createConcreteAssignment() throws IllegalArgumentException, ConversionNotSupportedException {
		for( AssignmentType at : project.getCurrentAssignment().getAssignmentTypes() )
			cellularAutomaton.setAssignmentType( at.getName(), at.getUid() );
		concreteAssignment = project.getCurrentAssignment().createConcreteAssignment( 400 );
		final CellularAutomatonAssignmentConverter cac = new CellularAutomatonAssignmentConverter();
		cac.setProblem( new AssignmentApplicationInstance( new ConvertedCellularAutomaton( cellularAutomaton, mapping, container ), concreteAssignment ) );
		cac.run();
	}

	void setUpSimulationAlgorithm() {
		CellularAutomatonAlgorithm cellularAutomatonAlgo = CellularAutomatonAlgorithm.RandomOrder;
		caAlgo = cellularAutomatonAlgo.createTask( cellularAutomaton );
		double caMaxTime = PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" );
		caAlgo.setMaxTimeInSeconds( caMaxTime );
	}

	private boolean caInitialized = false;

	void pauseStepByStep() {
		if( catsbs != null )
			catsbs.setStopMode( true );
	}
	
	void performOneStep( PropertyChangeListener propertyChangeListener, AlgorithmListener listener ) throws ConversionNotSupportedException {
		if( catsbs == null ) {			
			initStepByStep( propertyChangeListener, listener, true );
		} else {
			catsbs.setStopMode( true );
			catsbs.setPerformOneStep( true );
		}
	}
	
	private void initStepByStep( PropertyChangeListener propertyChangeListener, AlgorithmListener listener, boolean stopMode ) {
			if( caInitialized ) {
				System.out.println( "Do not convert automaton, already exists." );
				catsbs = new CellularAutomatonTaskStepByStep( new ConvertedCellularAutomaton( cellularAutomaton, mapping, container ) );
			} else
				catsbs = new CellularAutomatonTaskStepByStep();
				//return; // no auto-create so far
			catsbs.setCaAlgo( CellularAutomatonAlgorithm.InOrder );
			catsbs.setProblem( project );
			if( stopMode ) {
				catsbs.setStopMode( true );
				catsbs.setPerformOneStep( true );
			}
			catsbs.addAlgorithmListener( listener );
			final SerialTask st = new SerialTask( catsbs );
			st.addPropertyChangeListener( new PropertyChangeListener() {
				private boolean first = true;
				@Override
				public void propertyChange( PropertyChangeEvent pce ) {
					if( first ) {
						System.out.println( "Received event:" + pce.getPropertyName() );
						while( catsbs.getCa() == null ) {
							try {
								Thread.sleep( 100 );
							} catch( InterruptedException ex ) {
								Logger.getLogger( AlgorithmControl.class.getName() ).log( Level.SEVERE, null, ex );
							}
						}
						cellularAutomaton = catsbs.getCa();
						mapping = catsbs.getMapping();
						container = catsbs.getContainer();
						caVisResults = null;
						first = false;
						// todo: invalidate
						// invalidateConvertedCellularAutomaton();
					}
					if( st.isDone() ) {
						catsbs = null;
						//caInitialized = false;
					}
				}
			});
			if( propertyChangeListener != null )
				st.addPropertyChangeListener( propertyChangeListener );
			st.execute();
	}

	@Override
	public void propertyChange( PropertyChangeEvent pce ) {
		System.out.println( pce.getPropertyName() );
	}

	public void convertGraph() {
		convertGraph( null, "");
	}

	public void convertGraph( PropertyChangeListener propertyChangeListener, String Algo ) {
            final BaseZToGraphConverter conv;
            if (Algo == "GridGraph"){                
                 conv = new ZToGridGraphConverter();}
            else if (Algo == "NonGridGraph" ){
                 conv = new ZToNonGridGraphConverter(); }           
            else if (Algo == "MinSpanTree(Grid)"){
                 conv = new ZToGridSpanTreeConverter();}
            else if (Algo == "MinSpanTree(NonGrid)"){
                conv = new ZToSpanTreeConverter();}
            else if (Algo == "Greedy t-Spanner(Grid)"){
                 conv = new ZToGridGreedyConverter();}
            //Greedy fuer NonGridGraphen
            else if (Algo == "Dijkstra For Non Grid"){
                conv = new ZToDijkstraSpannerConverter();}
             else if (Algo == "Dijkstra For Grid"){
                conv = new ZToGridDijkstraConverter();}       
            else {
                 conv = new ZToGreedySpannerConverter();}
                
		conv.setProblem( project.getBuildingPlan() );
		final SerialTask st = new SerialTask( conv );
		st.addPropertyChangeListener( new PropertyChangeListener() {

			public void propertyChange( PropertyChangeEvent pce ) {
				if( st.isDone() ) {
					networkFlowModel = conv.getSolution();
				}
			}
		});
		if( propertyChangeListener != null )
			st.addPropertyChangeListener( propertyChangeListener );
		st.execute();
	}

	public NetworkFlowModel getNetworkFlowModel() {
		return networkFlowModel;
	}

	public void performOptimization() {
		performOptimization( null );
	}

	public void performOptimization( PropertyChangeListener propertyChangeListener ) {
		final GraphAlgorithmTask gat = new GraphAlgorithmTask( GraphAlgorithm.SuccessiveEarliestArrivalAugmentingPathOptimized );
		gat.setProblem( project );

		final SerialTask st = new SerialTask( gat );
		st.addPropertyChangeListener( new PropertyChangeListener() {

			public void propertyChange( PropertyChangeEvent pce ) {
				if( st.isDone() ) {
					networkFlowModel = gat.getNetworkFlowModel();
					graphVisResults = gat.getSolution();
				}
			}
		});
		if( propertyChangeListener != null )
			st.addPropertyChangeListener( propertyChangeListener );
		st.execute();
	}

	public GraphVisualizationResults getGraphVisResults() {
		return graphVisResults;
	}
}
