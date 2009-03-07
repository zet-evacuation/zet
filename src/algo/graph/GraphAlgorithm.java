package algo.graph;

/**
 * Defines method graph algorithms must have.
 * Each algorithm has a class that has to be instantiated
 * for each run of the algorithm. The input is given to 
 * the algorithm in the constructor. The output shall be
 * calculated if the method <code>run()</code> is called.
 * The method <code>hasRun()</code> returns whether
 * the algorithm has already run. Only one run should be made!
 * After the run the results can be obtained by algorithm
 * specific methods.
 */
public abstract class GraphAlgorithm implements Sender{

	/**
	 * A flag telling whether the algorithm has run.
	 */
	private boolean hasRun = false;
	
	/**
	 * Starts the algorithm. Only the first call has an effect.
	 */
	public final void run(){
		if (hasRun)
			return;
		else {
			runAlgorithm();
			hasRun = true;
		}
	}
	
	/**
	 * This method contains the algorithm itself. Has to be implemented by subclasses.
	 */	
	public abstract void runAlgorithm();
	
	
	/**
	 * Returns whether the algorithm has already run. Don't run it twice.
	 * @return whether the algorithm has already run.
	 */
	public final boolean hasRun(){
		return hasRun;
	}
	
}
