/**
 * AlgorithmControlCellularAutomaton.java
 * Created: 31.10.2012, 15:12:29
 */
package gui;

import algo.ca.algorithm.evac.EvacuationSimulationProblem;
import algo.ca.framework.EvacuationCellularAutomatonAlgorithm;
import algo.ca.framework.StepByStepAutomaton;
import org.zetool.common.algorithm.Algorithm;
import org.zetool.common.algorithm.AlgorithmEvent;
import org.zetool.common.algorithm.AlgorithmListener;
import org.zetool.common.algorithm.AlgorithmStartedEvent;
import org.zetool.common.util.Formatter;
import org.zetool.common.util.units.TimeUnits;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.AssignmentApplicationInstance;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.CellularAutomatonAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ConvertedCellularAutomaton;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import ds.PropertyContainer;
import ds.ca.evac.EvacuationCellularAutomaton;
import de.tu_berlin.coga.zet.model.AssignmentType;
import de.tu_berlin.coga.zet.model.BuildingPlan;
import de.tu_berlin.coga.zet.model.ConcreteAssignment;
import de.tu_berlin.coga.zet.model.Project;
import de.tu_berlin.math.coga.zet.converter.AssignmentConcrete;
import io.visualization.EvacuationSimulationResults;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import zet.tasks.CellularAutomatonAlgorithms;
import zet.tasks.CellularAutomatonTask;
import zet.tasks.SerialTask;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class AlgorithmControlCellularAutomaton {
	/** The logger of the main class. */
	private static final Logger log = Logger.getGlobal();
	//private EvacuationCellularAutomatonAlgorithm simulationAlgorithm;
	private CellularAutomatonAlgorithms simulationAlgorithm = CellularAutomatonAlgorithms.RandomOrder;
	private ZToCAMapping mapping;
	private ZToCARasterContainer container;
	private EvacuationSimulationResults caVisResults;
	private EvacuationCellularAutomaton cellularAutomaton;
	private CellularAutomatonTask cat = new CellularAutomatonTask();
	private ConcreteAssignment concreteAssignment;

	public CellularAutomatonAlgorithms getSimulationAlgorithm() {
		return simulationAlgorithm;
	}

	/**
	 * Sets the simulation algorithm that will be used for the next simulation runs.
	 * @param simulationAlgorithm
	 */
	public void setSimulationAlgorithm( CellularAutomatonAlgorithms simulationAlgorithm ) {
		this.simulationAlgorithm = simulationAlgorithm;
	}

	void convertCellularAutomaton( BuildingPlan buildingPlan, PropertyChangeListener propertyChangeListener ) {
		final ZToCAConverter conv = new ZToCAConverter();

		conv.setProblem( buildingPlan );

		final SerialTask st = new SerialTask( conv );
		st.addPropertyChangeListener( new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( st.isDone() ) {
					if( st.isError() ) {
						//error = st.getError();
					} else {
						cellularAutomaton = conv.getCellularAutomaton();
						mapping = conv.getMapping();
						container = conv.getContainer();
						//caInitialized = true;
					}
				}
			}
		});
		if( propertyChangeListener != null )
			st.addPropertyChangeListener( propertyChangeListener );

		st.execute();
	}

	public void invalidateConvertedCellularAutomaton() {
		//caInitialized = false;
	}

	public EvacuationCellularAutomaton getCellularAutomaton() {
		return cellularAutomaton;
	}

	public ZToCARasterContainer getContainer() {
		return container;
	}

	public ZToCAMapping getMapping() {
		return mapping;
	}

	void performSimulation( Project project, PropertyChangeListener propertyChangeListener, AlgorithmListener listener ) {
		cat = new CellularAutomatonTask();
		cat.setCaAlgo( CellularAutomatonAlgorithms.RandomOrder.getAlgorithm() );
		cat.setProblem( project );
		cat.addAlgorithmListener( listener );

		final SerialTask st = new SerialTask( cat );
		st.addPropertyChangeListener( new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( st.isDone() ) {
					if( st.isError() ) {
						//error = st.getError();
					} else {
						cellularAutomaton = cat.getCa();
						mapping = cat.getMapping();
						container = cat.getContainer();
						caVisResults = cat.getSolution();
						//EventServer.getInstance().dispatchEvent( new MessageEvent<>( this, MessageType.Status, "Simulation finished" ) );
						log.log( Level.INFO, "Egress time: {0}", Formatter.formatUnit( cellularAutomaton.getTimeStep() * cellularAutomaton.getSecondsPerStep(), TimeUnits.Seconds ));
					}
				}
			}
		});
		if( propertyChangeListener != null )
			st.addPropertyChangeListener( propertyChangeListener );
		st.execute();
	}

	void performSimulationQuick( Project project, AlgorithmListener listener ) {
		initStepByStep( project, listener, false );
	}

	public EvacuationSimulationResults getCaVisResults() {
		return caVisResults;
	}

	void createConcreteAssignment( Project project ) throws IllegalArgumentException, ZToCAConverter.ConversionNotSupportedException {
		for( AssignmentType at : project.getCurrentAssignment().getAssignmentTypes() )
			cellularAutomaton.setAssignmentType( at.getName(), at.getUid() );
		concreteAssignment = AssignmentConcrete.createConcreteAssignment( project.getCurrentAssignment(), 400 );
		final CellularAutomatonAssignmentConverter cac = new CellularAutomatonAssignmentConverter();
		cac.setProblem( new AssignmentApplicationInstance( new ConvertedCellularAutomaton( cellularAutomaton, mapping, container ), concreteAssignment ) );
		cac.run();
	}

	void setUpSimulationAlgorithm() {
		EvacuationCellularAutomatonAlgorithm cellularAutomatonAlgorithm = simulationAlgorithm.getAlgorithm();
		cellularAutomatonAlgorithm.setProblem( new EvacuationSimulationProblem( ( cellularAutomaton) ) );
		double caMaxTime = PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" );
		cellularAutomatonAlgorithm.setMaxTimeInSeconds( caMaxTime );
	}

	void pauseStepByStep() {
		if( ecasbs != null && ecasbs.isRunning() )
			ecasbs.setPaused( true );
	}

	void performOneStep( Project project, AlgorithmListener listener ) {
		initStepByStep( project, listener, true );
	}

	EvacuationCellularAutomatonAlgorithm eca = null;
	EvacuationCellularAutomatonAlgorithm ecasbs = null;
	class BackgroundTask<S> extends SwingWorker<S, AlgorithmEvent> {
		Algorithm<?,S> algo;
		private BackgroundTask( Algorithm<?,S> algorithm ) {
			this.algo = algorithm;
		}


		@Override
		protected S doInBackground() throws Exception {
			algo.run();
			S result = algo.getSolution();
			return result;
		}
	}

	private void initStepByStep( Project project, AlgorithmListener listener, boolean stopMode ) {
		if( ecasbs == null || !ecasbs.isRunning() ) {
			cat = new CellularAutomatonTask();
			eca = CellularAutomatonAlgorithms.RandomOrder.getAlgorithm();
			if( stopMode ) {
				log.info( "Initializing the algorithm for step-by-step execution..." ) ;
				ecasbs = StepByStepAutomaton.getStepByStepAlgorithm( eca );
			} else {
				log.info( "Initializing the algorithm for slow execution..." ) ;
				ecasbs = StepByStepAutomaton.getSlowAlgorithm( eca );
			}

			cat.setCaAlgo( ecasbs );
			cat.setProblem( project );

			/**
			 * Listens to the actual simulation algorithm and sets the data structures
			 * for the view accordingly. These datastructures are the cellular automaton,
			 * the container and the mapping. They can afterwards be accessed from
			 * the GUI to visualize the temporal simulation status.
			 */
			final AlgorithmListener al = new AlgorithmListener() {
				@Override
				public void eventOccurred( AlgorithmEvent event ) {
					if( event instanceof AlgorithmStartedEvent ) {
						cellularAutomaton = cat.getCa();
						mapping = cat.getMapping();
						container = cat.getContainer();
						assert cellularAutomaton != null;
					}
				}
			};

			ecasbs.addAlgorithmListener( al );
			ecasbs.addAlgorithmListener( listener );

			new BackgroundTask<>( cat ).execute(); // execute the algorithm in a new thread
		} else {
			if( ecasbs.isRunning() ) {
				log.info( "Continuing the algorithm..." );
				ecasbs.setPaused( false );
			} else
				log.info( "Ignoring, algorithm has finished." );
		}
	}
	
	void tempSetParametersFromEx( EvacuationSimulationResults caVis, EvacuationCellularAutomaton ca ) {
		this.caVisResults = caVis;
		this.cellularAutomaton = ca;
	}
}
