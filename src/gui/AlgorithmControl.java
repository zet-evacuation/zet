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
import de.tu_berlin.math.coga.zet.converter.graph.ZToNonGridGraphConverter;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.GraphVisualizationResults;
import ds.Project;
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

	BuildingResults buildingResults;
	Project project;
	CellularAutomaton cellularAutomaton;
	ConcreteAssignment concreteAssignment;
	EvacuationCellularAutomatonAlgorithm caAlgo;
	ZToCAMapping mapping;
	ZToCARasterContainer container;
	CAVisualizationResults caVisResults;
	NetworkFlowModel networkFlowModel;
	GraphVisualizationResults graphVisResults;
	final CellularAutomatonTask cat = new CellularAutomatonTask();


	public AlgorithmControl( Project project ) {
		this.project = project;
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
		final ZToCAConverter conv = new ZToCAConverter();
		conv.setProblem( project.getBuildingPlan() );

		final SerialTask st = new SerialTask( conv );
		st.addPropertyChangeListener( new PropertyChangeListener() {

			public void propertyChange( PropertyChangeEvent pce ) {
				if( st.isDone() ) {
					cellularAutomaton = conv.getCellularAutomaton();
					mapping = conv.getMapping();
					container = conv.getContainer();
				}
			}
		});
		if( propertyChangeListener != null )
			st.addPropertyChangeListener( propertyChangeListener );
		st.execute();
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

	void performSimulationA( PropertyChangeListener propertyChangeListener, AlgorithmListener listener ) {
		//final CellularAutomatonTask cat = new CellularAutomatonTask();
		final CellularAutomatonTaskStepByStep cat = new CellularAutomatonTaskStepByStep();

		cat.setCaAlgo( CellularAutomatonAlgorithm.InOrder );
		cat.setProblem( project );
		cat.addAlgorithmListener( listener );

		final SerialTask st = new SerialTask( cat );
		st.addPropertyChangeListener( new PropertyChangeListener() {
			boolean first = true;

			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				//System.out.println( "HAHA" );
				if( first ) {
					while( cat.getCa() == null ) {
						try {
							Thread.sleep( 100 );
						} catch( InterruptedException ex ) {
							Logger.getLogger( AlgorithmControl.class.getName() ).log( Level.SEVERE, null, ex );
						}
					}
					cellularAutomaton = cat.getCa();
					mapping = cat.getMapping();
					container = cat.getContainer();
					caVisResults = null;
					first = false;
				}
			}
		});
		if( propertyChangeListener != null )
			st.addPropertyChangeListener( propertyChangeListener );

		System.out.println( "Start slow execution..." );

		st.execute();
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

	boolean caInitialized = false;

	boolean performOneStep() throws ConversionNotSupportedException {
		if( !caInitialized ) {
			final ZToCAConverter conv = new ZToCAConverter( project );
			conv.run();
			cellularAutomaton = conv.getCellularAutomaton();
			mapping = conv.getMapping();
			container = conv.getContainer();
			createConcreteAssignment();
			setUpSimulationAlgorithm();
			caAlgo.setStepByStep( true );
			caAlgo.initialize();
			caInitialized = true;
			return true;
		} else {
			caAlgo.run();
			if( caAlgo.isFinished() )
				caInitialized = false;
			return false;
		}
	}

	@Override
	public void propertyChange( PropertyChangeEvent pce ) {
		System.out.println( pce.getPropertyName() );
	}

	public void convertGraph() {
		convertGraph( null );
	}

	public void convertGraph( PropertyChangeListener propertyChangeListener ) {
		final ZToNonGridGraphConverter conv = new ZToNonGridGraphConverter();
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
