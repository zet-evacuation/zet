package algo.graph.dynamicflow;

import util.DebugFlags;
import algo.graph.Notifiable;
import algo.graph.util.PathDecomposition;
import tasks.AlgorithmTask;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.graph.DynamicPath;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.flow.PathBasedFlow;
import ds.graph.StaticPath;
import ds.graph.flow.StaticPathFlow;
import ds.graph.TimeExpandedNetwork;
import util.ProgressBooleanFlags;

/** 
 * The class <code>TransshipmentWithTimeHorizon</code> provides a method to calculate 
 * a dynamic transshipment with certain properties by using the time-expanded network
 * if the method to compute a adequate transshipment in the time-expanded network is overridden. 
 */
public abstract class TransshipmentWithTimeHorizon extends DynamicFlowAlgorithm{

	/** The time horizon of the transshipment. */
	protected Integer timeHorizon;
	/** The supplies used by the algorithm. */ 
	protected IdentifiableIntegerMapping<Node> supplies;
	/** The name of the concrete transshipment algorithm. */
	protected String nameOfTransshipmentWithTimeHorizon;
	/** The receiver of progress information. */
	protected Notifiable receiver;
	/** Node capacities */
	protected IdentifiableIntegerMapping<Node> nodeCapacities;
	
	/**
	 * Creates a new instance of the transshipment algorithm with given network, transit times, capacities, supplies and a time horizon.
	 * @param network The network the transshipment algorithm shall work on.
	 * @param transitTimes The transit times the transshipment algorithm shall use.
	 * @param edgeCapacities The edge capacities the transshipment algorithm shall use.
	 * @param supplies The supplies the transshipment algorithm shall use.
	 * @param timeHorizon The time horizon for the wanted transshipment.
	 * @param nameOfTransshipmentWithTimeHorizon Name of the concrete transshipment algorithm.
	 */
	public TransshipmentWithTimeHorizon(Network network, IdentifiableIntegerMapping<Edge> transitTimes, IdentifiableIntegerMapping<Edge> edgeCapacities, IdentifiableIntegerMapping<Node> nodeCapacities, IdentifiableIntegerMapping<Node> supplies, Integer timeHorizon, String nameOfTransshipmentWithTimeHorizon){
		super(network, transitTimes, edgeCapacities);
		this.timeHorizon = timeHorizon;
		this.supplies = supplies;
		this.nameOfTransshipmentWithTimeHorizon = nameOfTransshipmentWithTimeHorizon;
		this.nodeCapacities = nodeCapacities;
	}
	
	/**
	 * Abstract method that has to be overridden with the concrete transshipment algorithm.
	 * @param network The (time expanded) network the algorithm works on.
	 * @return An edge based flow.
	 */
	protected abstract IdentifiableIntegerMapping<Edge> transshipmentWithTimeHorizon(TimeExpandedNetwork network);
	
	@Override
	/**
	 * This static method computes a transshipment using the method <code>runTransshipment</code>
	 * that has to be implemented by subclasses.
	 * The algorithm creates a time expanded network, calls <code>runTransshipment</code> to compute
	 * a static flow and creates a dynamic flow from the result. 
	 * If <code>runTransshipment</code> returns <code>null</code>
	 * this method also returns <code>null</code>.
	 * @return A dynamic transshipment (with certain properties) if it exists, <code>null</code> otherwise.
	 */
	public void runAlgorithm(){
		/* Short debug output telling that a time expanded network is created. */
		if (DebugFlags.TRANSSHIPMENT_SHORT) {
			System.out.println("The "+nameOfTransshipmentWithTimeHorizon+" algorithm creates a time expanded network.");
			 AlgorithmTask.getInstance().publish( "Time-Expanded Network-Creation", "The "+nameOfTransshipmentWithTimeHorizon+" algorithm creates a time expanded network."); 
		}

		/* Create the time expanded network up to the given time horizon. */
		TimeExpandedNetwork tnetwork = new TimeExpandedNetwork(network,
				edgeCapacities, transitTimes, timeHorizon, supplies, false);
		
		/* Progress output. */
		if (ProgressBooleanFlags.ALGO_PROGRESS){
			System.out.print("Progress: The time expanded network was created, ");
		}

		/* Short debug output including the size of the created expanded network. */
		if (DebugFlags.TRANSSHIPMENT_SHORT && !DebugFlags.TRANSSHIPMENT_LONG){
			System.out.println("The time expanded network was created.");
			System.out.println("It has "+tnetwork.nodes().size()+" nodes and "+tnetwork.edges().size()+" edges.");
			 AlgorithmTask.getInstance().publish( "Time-Expanded Network created.", "It has "+tnetwork.nodes().size()+" nodes and "+tnetwork.edges().size()+" edges."); 
		}
		/* Long debug output including the complete expanded network.*/
		if (DebugFlags.TRANSSHIPMENT_LONG){
			System.out.println(tnetwork);
		}
		
		/* Progress output. */
		if (ProgressBooleanFlags.ALGO_PROGRESS){
			System.out.println("the "+nameOfTransshipmentWithTimeHorizon+" algorithm is called.. ");
		}
		/* Short debug output telling that the algorithm for the transshipment with time horizon is called. */
		if (DebugFlags.TRANSSHIPMENT_SHORT && !DebugFlags.TRANSSHIPMENT_LONG){
			System.out.println("The "+nameOfTransshipmentWithTimeHorizon+" algorithm is called.");
			AlgorithmTask.getInstance().publish( nameOfTransshipmentWithTimeHorizon+" algorithm", "The "+nameOfTransshipmentWithTimeHorizon+" algorithm is called.");
		}		
		
		/* Compute the static flow according to the specifit transshipment with time horizon. */
		IdentifiableIntegerMapping<Edge> flow = transshipmentWithTimeHorizon(tnetwork);
		
		/* Progress output. */
		if (ProgressBooleanFlags.ALGO_PROGRESS){
			System.out.print("Progress: .. call of the "+nameOfTransshipmentWithTimeHorizon+" algorithm finished. Time horizon is ");
			if (flow == null)
				System.out.print("not ");
			System.out.println("sufficient.");
		}
		
		/* Short debug output telling whether the current time horizon was sufficient. */		
		if (DebugFlags.TRANSSHIPMENT_SHORT){
			System.out.print("A time horizon of "+timeHorizon+" is ");
			if (flow == null)
				System.out.print("not ");
			System.out.println("sufficient.");
		}

		/* If flow==null, there does not exists a feasible static transshipment (with wished properties)
		 * and therefore there does not exist a feasible dynamic transshipment (with wished properties).*/
		if (flow == null){
			resultFlowPathBased = null;
			return;
		}
		
		/* Long debug output including the flow function of the found flow. */
		if (DebugFlags.TRANSSHIPMENT_LONG){
			System.out.println();
			System.out.println("Static transshipment as flow function:");
			System.out.println(flow);
		}

		/* Short Debug telling that a path decomposition is calculated.*/		
		if (DebugFlags.TRANSSHIPMENT_SHORT){
			System.out.println();
			System.out.println("Calculating path decomposition from sources "+tnetwork.sources()+" to sinks "+ tnetwork.sinks()+".");
		}
		
		/* Decompose the flow into static paths flows.*/
		PathBasedFlow decomposedFlow = PathDecomposition.calculatePathDecomposition(
				tnetwork, tnetwork.sources(), tnetwork.sinks(), flow);

		/* Long debug output containing the path flows.*/
		if (DebugFlags.TRANSSHIPMENT_LONG){
			System.out.println();
			System.out.println("Static transshipment path based:");
			System.out.println(decomposedFlow);
		}
		
		/* Translating the static flow into a dynamic flow.*/
		PathBasedFlowOverTime dynamicTransshipment = new PathBasedFlowOverTime();
		for (StaticPathFlow staticPathFlow : decomposedFlow) {
			if (staticPathFlow.getAmount()==0) continue;
			StaticPath staticPath = staticPathFlow.getPath();
			DynamicPath dynamicPath = tnetwork.translatePath(staticPath);
			// The rate of the dynamic path is the amount of the static path,
			// and the amount of the dynamic path is its rate * how long it flows,
			// but as all flows constructed in the time-expanded network have
			// length T-1, flow always flows for one time step, thus amount = rate.
			FlowOverTimePath dynamicPathFlow = new FlowOverTimePath(dynamicPath,
					staticPathFlow.getAmount(), staticPathFlow.getAmount());
			dynamicTransshipment.addPathFlow(dynamicPathFlow);
		}
		
		if (DebugFlags.TRANSSHIPMENT_LONG){
			System.out.println("Dynamic transshipment:");
			System.out.println(dynamicTransshipment);
		}

		resultFlowPathBased = dynamicTransshipment;
	}
	
	@Override
	/**
	 * Sets the object that will be informed by this algorithm. Only one receiver per time.
 	 * @param receiver Object that wants to receive progress information from this algorithm. 
	 */
	public void setReciever(Notifiable receiver){
		this.receiver = receiver;
	}
	
}
