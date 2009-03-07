package util;

public class DebugFlags {
	
	//****************************************************************************
	// Personal Debug Flags
	
	public final static boolean MARTIN = false;
	public final static boolean MEL = false;
	public final static boolean MEL_LONG = MEL && false;
	public final static boolean GORDON = false;   // Switch some more informations as output for maxFlowOverTime on or off.
	public final static boolean GORDON_RE = true; //Switch the reduction with one super node on or off.
	public final static boolean TIMON = false;

	//****************************************************************************
	// General Debug Flags

	public final static boolean TEMFOT = MARTIN && false;
	public final static boolean PP = MARTIN && false;
	
	public final static boolean DXF = false;
	public final static boolean EVAPLANCHECKER = false;
	
	// The following two debug flags influence all classes having 'Transshipment' in their names. 
	public final static boolean TRANSSHIPMENT_SHORT = false;
	public final static boolean TRANSSHIPMENT_LONG = !TRANSSHIPMENT_SHORT && false;
	public final static boolean TRANSSHIPMENT_RESULT_FLOW = false;
	public final static boolean FLOWWRONG = false;
	public final static boolean FLOWWRONG_LONG = false;	// WARNING Outputs in GLEdge have been commented out!

	// Debug while calculating upper bound for time horizon in transshipment calculations
	public final static boolean BOUND_ESTIMATOR = false;
	public final static boolean BOUND_ESTIMATOR_LONG = false;
	public final static boolean BOUND_ESTIMATOR_STATIC_FLOW = false;

	// Switch output for earliest arrival transshipment on or off.
	public final static boolean EAT = MEL && false;
	// Switch short output for earliest arrival transshipment on or off.
	public final static boolean EAT_SHORT = EAT && false;
	// Switch long output for earliest arrival transshipment on or off.
	public final static boolean EAT_LONG = EAT_SHORT && false;

	public final static boolean RASTER = false;
	public final static boolean NODECREATION = false;
	public final static boolean NODECREATIONSMALL = false;
	public final static boolean GRAPH_DOORS = false;
	public final static boolean CONVERTING = false;
	// Switch output for earliest arrival transshipment on or off.
	public final static boolean TEST = MEL && false;
	// Switch short output for earliest arrival transshipment on or off.
	public final static boolean TEST_SHORT = TEST && false;
	// Switch long output for earliest arrival transshipment on or off.
	public final static boolean TEST_LONG = TEST_SHORT && false;
	
	// Debug switches for cellular automaton
	public final static boolean RULESET = false;
	public final static boolean CA_ALGO = false;
	public final static boolean CA_SWAP = false;
	public final static boolean CA_SWAP_USED_OUTPUT = false;	// Use this flag to show hints like "Quetschregel oder Individuum läuft doch nicht!!"
	
	// Debug switches for visualization
	public final static boolean VIS_CA = false;
}
