/**
 * AlgorithmControl.java
 * Created: 27.07.2010 14:49:25
 */
package gui;

import algo.ca.algorithm.evac.EvacuationCellularAutomatonAlgorithm;
import algo.ca.algorithm.evac.EvacuationSimulationProblem;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmListener;
import de.tu_berlin.math.coga.common.util.Formatter;
import de.tu_berlin.math.coga.common.util.Formatter.TimeUnits;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.AssignmentApplicationInstance;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.CellularAutomatonAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ConvertedCellularAutomaton;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter.ConversionNotSupportedException;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import de.tu_berlin.math.coga.zet.converter.graph.BaseZToGraphConverter;
import ds.CompareVisualizationResults;
import ds.GraphVisualizationResults;
import ds.PropertyContainer;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.z.AssignmentType;
import ds.z.ConcreteAssignment;
import ds.z.Project;
import io.visualization.BuildingResults;
import io.visualization.CAVisualizationResults;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import tasks.conversion.BuildingPlanConverter;
import zet.tasks.CellularAutomatonAlgorithmEnumeration;
import zet.tasks.CellularAutomatonTask;
import zet.tasks.CellularAutomatonTaskStepByStep;
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

	private BuildingResults buildingResults;
	private Project project;
	private EvacuationCellularAutomaton cellularAutomaton;
	private ConcreteAssignment concreteAssignment;
	private EvacuationCellularAutomatonAlgorithm caAlgo;
	private ZToCAMapping mapping;
	private ZToCARasterContainer container;
	private CAVisualizationResults caVisResults;
	private NetworkFlowModel networkFlowModel;
	private GraphVisualizationResults graphVisResults;
        private CompareVisualizationResults compVisResults;
	private CellularAutomatonTask cat = new CellularAutomatonTask();
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
	long conversionTime = 0;
	public void convertBuildingPlan( PropertyChangeListener pcl ) {
		final BuildingPlanConverter bpc = new BuildingPlanConverter();
		bpc.setProblem( project.getBuildingPlan() );

		final SerialTask st = new SerialTask();
		st.add( bpc );
		st.addPropertyChangeListener( new PropertyChangeListener() {

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
	
	public long getConversionRuntime() {
		return conversionTime;
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

	public EvacuationCellularAutomaton getCellularAutomaton() {
		return cellularAutomaton;
	}

	public ZToCARasterContainer getContainer() {
		return container;
	}

	public ZToCAMapping getMapping() {
		return mapping;
	}

	void performSimulation() {
		performSimulation( null, null );
	}

// AlgorithmListener control ) {
//		final GraphAlgorithmTask gat = new GraphAlgorithmTask( GraphAlgorithmEnumeration.SuccessiveEarliestArrivalAugmentingPathOptimized );
//		gat.setProblem( project );
		
//		gat.addAlgorithmListener( control );
			
	
	void performSimulation( PropertyChangeListener propertyChangeListener, AlgorithmListener listener ) {
		cat = new CellularAutomatonTask();
		cat.setCaAlgo( CellularAutomatonAlgorithmEnumeration.RandomOrder );
		cat.setProblem( project );
		cat.addAlgorithmListener( listener );

		final SerialTask st = new SerialTask( cat );
		st.addPropertyChangeListener( new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( st.isDone() ) {
					cellularAutomaton = cat.getCa();
					mapping = cat.getMapping();
					container = cat.getContainer();
					caVisResults = cat.getSolution();
					//EventServer.getInstance().dispatchEvent( new MessageEvent<>( this, MessageType.Status, "Simulation finished" ) );
					System.out.println( "Egress time: " + Formatter.formatTimeUnit( cellularAutomaton.getTimeStep() * cellularAutomaton.getSecondsPerStep(), TimeUnits.Seconds ) );
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
		CellularAutomatonAlgorithmEnumeration cellularAutomatonAlgo = CellularAutomatonAlgorithmEnumeration.RandomOrder;
		//caAlgo = cellularAutomatonAlgo.createTask( cellularAutomaton );
		caAlgo = cellularAutomatonAlgo.getAlgorithm();
		caAlgo.setProblem( new EvacuationSimulationProblem( ( cellularAutomaton) ) );
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
				catsbs = new CellularAutomatonTaskStepByStep();
				catsbs.setConvertedCellularAutomaton( new ConvertedCellularAutomaton( cellularAutomaton, mapping, container ) );
			} else
				catsbs = new CellularAutomatonTaskStepByStep();
				//return; // no auto-create so far
			catsbs.setCaAlgo( CellularAutomatonAlgorithmEnumeration.InOrder );
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
		convertGraph( null, GraphConverterAlgorithms.GreedyTSpannerNonGrid );
	}

	GraphConverterAlgorithms last = GraphConverterAlgorithms.NonGridGraph;
	
	public RunnableFuture<Void> convertGraph( PropertyChangeListener propertyChangeListener, GraphConverterAlgorithms Algo ) {
		if( project.getBuildingPlan().isRastered() == false ) {
			System.out.print( "Building is not rasterized. Rastering... " );
			project.getBuildingPlan().rasterize();
			System.out.println( " done." );
		}
		final BaseZToGraphConverter conv = Algo.converter();
		last = Algo;
		conv.setProblem( project.getBuildingPlan() );
		final SerialTask st = new SerialTask( conv );
		st.addPropertyChangeListener( new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( st.isDone() ) {
					networkFlowModel = conv.getSolution();
					System.out.println( "Nodes: " + networkFlowModel.getNetwork().getNodeCapacity() );
					System.out.println( "Edges: " + networkFlowModel.getNetwork().getEdgeCapacity() );
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
        
        public void performOptimizationCompare(PropertyChangeListener propertyChangeListener) {
		if( !project.getBuildingPlan().isRastered() ) {
			System.out.print( "Building is not rasterized. Rastering... " );
			project.getBuildingPlan().rasterize();
			System.out.println( " done." );
		}
            
            final CompareTask ct = new CompareTask(GraphAlgorithmEnumeration.SuccessiveEarliestArrivalAugmentingPathOptimizedCompare);
            ct.setProblem(project);
            
            //values for original network
            GraphConverterAlgorithms ConvOrig = GraphConverterAlgorithms.NonGridGraph; 
            ct.setConvOriginal(ConvOrig.converter());
            
            //values for thin network
            ct.setConvThinNet(last.converter());
            ct.setThinNetwork(networkFlowModel);
            
            
            final SerialTask st = new SerialTask( ct );
            st.addPropertyChangeListener( new PropertyChangeListener() {

			public void propertyChange( PropertyChangeEvent pce ) {
				if( st.isDone() ) {
					networkFlowModel = ct.getOriginal();
					compVisResults = ct.getSolution();
				}
			}
		});
		if( propertyChangeListener != null )
			st.addPropertyChangeListener( propertyChangeListener );
                System.out.println("done");
		st.execute();
                System.out.println("done");
        }

	public GraphVisualizationResults getGraphVisResults() {
		return graphVisResults;
	}

	public CompareVisualizationResults getCompVisResults() {
		return compVisResults;
	}
}
