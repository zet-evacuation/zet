/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.common.algorithm;

import java.util.concurrent.Callable;

/**
 *
 * @param <Problem> 
 * @param <Solution> 
 * @author Jan-Philipp Kappmeier
 */
public interface AlgorithmI<Problem, Solution> extends Runnable, Callable<Solution> {	

	/**
		* Returns the instance of the problem that is to be solved.
		* @return the instance of the problem that is to be solved.
		*/
	public Problem getProblem();

	/**
		* Specifies the instance of the problem this algorithm is going to solve.
		* @param problem the instance of the problem that is to be solved.
		* @throws IllegalStateException if the algorithm is running 
		*/
	public  void setProblem(Problem problem) throws IllegalStateException;

	/**
		* Returns the solution computed by the algorithm.
		* @return the solution to the algorithm.
		* @throws IllegalStateException if the problem has not been solved yet.
		*/
	public Solution getSolution() throws IllegalStateException;

	/**
		* Returns whether the algorithm is currently begin executed.
		* @return {@code true} if this algorithm is currently running and
		* {@code false} otherwise.
		*/
	public boolean isRunning();


	/**
		* <p>The framework method for executing the algorithm. It is responsible for
		* recording the runtime of the actual algorithm in addition to handling
		* exceptions and recording the solution to the problem instance.</p>
		* <p>Calling the method solves the problem, afterwords it can be accessed
		* using {@link #getSolution() }.</p>
		* @throws IllegalStateException if the instance of the problem has not been
		* specified yet.
		*/
	@Override
	public void run();
	
	/**
	 * A framework method for executing the algorithm and returns the result. 
	 * <p>Calling the method solves the problem and returns the solution. The
	 * solution is stored and can be accessed again using {@link #getSolution() }.</p>
 	 * @return the solution to the algorithm.
	 */
	@Override
	public Solution call();


	/**
		* The abstract method that needs to be implemented by sub-classes in order
		* to implement the actual algorithm.
		* @param problem an instance of the problem.
		* @return a solution to the specified problem.
		*/
	Solution runAlgorithm( Problem problem );
}

