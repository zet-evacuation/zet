/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/**
 * Class BatchProjectEntry
 * Erstellt 25.11.2008, 20:11:22
 */

package batch.load;

import batch.CellularAutomatonAlgorithm;
import batch.GraphAlgorithm;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import gui.batch.EvacuationOptimizationType;

/**
 * A special batch tasks and all neccessary information for that, e.g. the
 * type of cellular automaton algorithm and graph algorithm, the number of runs
 * etc.
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias( "batchEntry" )
public class BatchProjectEntry {
	/** A path to a project file that should be loaded. May be ralative or static. */
	private String projectFile;
	/** The name used for the batch task. */
	private String name;
	/** An assignment in the project. Only the name of the assignment. */
	private String assignment;
	/** The cellular automaton algorithm that is used for simulation. */
	private CellularAutomatonAlgorithm cellularAutomatonAlgorithm;
	/** Indicates wheather simulation is used, or not. */
	private boolean useCellularAutomaton;
	/** The number of simulation runs. */
	private int cellularAutomatonRuns;
	/** The maximal time used for simulation. */
	private double cellularAutomanMaximalTime;
	/** Indicates wheather a graph optimization is used, or not. */
	private boolean useGraph;
	/** The graph algorithm that is used for optimization. */
	private GraphAlgorithm graphAlgorithm;
	/** The time horizon used by some graph algorithms. */
	private int graphTimeHorizon;
	/** The evacuation optimization type. */
	private EvacuationOptimizationType evacuationOptimizationType = EvacuationOptimizationType.None;
	/** The number of evacuation runs, if used. */
	private int evacuationOptimizationRuns;
	/** The properties loaded when the batch task execution starts. */
	private String property;

/**
 * Returns the assignment used by this batch task.
 * @return the assignment used by this batch task
 */
	public String getAssignment() {
		return assignment;
	}

	/**
	 * Sets the assignment used by this batch task. Note that the assignment must
	 * be contained in the project. The assignment is only accessed via its name,
	 * so it is not defined which assignment is used if two of them have the same
	 * name.
	 * @param assignment the assignment.
	 */
	public void setAssignment( String assignment ) {
		this.assignment = assignment;
	}

	/**
	 * Returns the maximal simulatin time for the cellular automaton.
	 * @return the maximal simulatin time for the cellular automaton
	 */
	public double getCellularAutomanMaximalTime() {
		return cellularAutomanMaximalTime;
	}

	/**
	 * Sets the maximal simulation time for the cellular automaton (in seconds).
	 * @param cellularAutomanMaximalTime the maximal simulation time
	 */
	public void setCellularAutomanMaximalTime( double cellularAutomanMaximalTime ) {
		this.cellularAutomanMaximalTime = cellularAutomanMaximalTime;
	}

	/**
	 * Returns the cellular automaton algorithm used for simulation.
	 * @return the cellular automaton algorithm used for simulation
	 */
	public CellularAutomatonAlgorithm getCellularAutomatonAlgorithm() {
		return cellularAutomatonAlgorithm;
	}

	/**
	 * Sets the cellular automaton algorithm used for simulation.
	 * @param cellularAutomatonAlgorithm the algorithm
	 */
	public void setCellularAutomatonAlgorithm( CellularAutomatonAlgorithm cellularAutomatonAlgorithm ) {
		this.cellularAutomatonAlgorithm = cellularAutomatonAlgorithm;
	}

	/**
	 * Returns the number of runs for the simulation.
	 * @return the number of runs for the simulation
	 */
	public int getCellularAutomatonRuns() {
		return cellularAutomatonRuns;
	}

	/**
	 * Sets the number of runs for the simulation.
	 * @param cellularAutomatonRuns the number of runs
	 */
	public void setCellularAutomatonRuns( int cellularAutomatonRuns ) {
		this.cellularAutomatonRuns = cellularAutomatonRuns;
	}

	
	/**
	 * Returns the number of runs for the evacuation optimization. Only used
	 * if evacuation optimization type is not set to {@link EvacuationOptimizationType.None}
	 * @return the number of runs for the evacuation optimization
	 */
	public int getEvacuationOptimizationRuns() {
		return evacuationOptimizationRuns;
	}

	/**
	 * Sets the number of runs for the evacuation optimization. Only used
	 * if evacuation optimization type is not set to {@link EvacuationOptimizationType.None}
	 * @param evacuationOptimizationRuns the number of runs for the evacuation optimization
	 */
	public void setEvacuationOptimizationRuns( int evacuationOptimizationRuns ) {
		this.evacuationOptimizationRuns = evacuationOptimizationRuns;
	}

	/**
	 * Returns the evacuation optimization type. It is never returned null, in that
	 * case {@link EvacuationOptimizationType.None} is returned.
	 * @return the evacuation optimization type.
	 */
	public EvacuationOptimizationType getEvacuationOptimizationType() {
		return evacuationOptimizationType;
	}

	/**
	 * Sets the evacuation optimization type. If null is submitted, the type is set to
	 * {@link EvacuationOptimizationType.None}.
	 * @param evacuationOptimizationType the evacuation optimization type
	 */
	public void setEvacuationOptimizationType( EvacuationOptimizationType evacuationOptimizationType ) {
		this.evacuationOptimizationType = evacuationOptimizationType == null ? EvacuationOptimizationType.None : evacuationOptimizationType;
	}

	/**
	 * Returns the graph algorithm used for optimization.
	 * @return the graph algorithm used for optimization
	 */
	public GraphAlgorithm getGraphAlgorithm() {
		return graphAlgorithm;
	}

	/**
	 * Sets the graph algorithm used for optimization.
	 * @param graphAlgorithm the graph algorithm
	 */
	public void setGraphAlgorithm( GraphAlgorithm graphAlgorithm ) {
		this.graphAlgorithm = graphAlgorithm;
	}

	/**
	 * Returns the time horizon used by some graph algorithms.
	 * @return the time horizon
	 */
	public int getGraphTimeHorizon() {
		return graphTimeHorizon;
	}

	/**
	 * Sets the time horizon used by some graph algorithms.
	 * @param graphTimeHorizon the time horizon
	 */
	public void setGraphTimeHorizon( int graphTimeHorizon ) {
		this.graphTimeHorizon = graphTimeHorizon;
	}

	/**
	 * Returns the name of the batch task.
	 * @return the name of the batch task
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the batch task.
	 * @param name the name
	 */
	public void setName( String name ) {
		this.name = name;
	}

	/**
	 * Seturns a path to a project file. Can be relative or absolute.
	 * @return the project file
	 */
	public String getProjectFile() {
		return projectFile;
	}

	/**
	 * Seturns a path to a project file. Can be relative or absolute.
	 * @param projectFile
	 */
	public void setProjectFile( String projectFile ) {
		this.projectFile = projectFile;
	}

	/**
	 * Returns the properties used by this task. Only the name of the property is used.
	 * @return the properties used by this task.
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * Sets the properties used by this task. Only the name of the property is used,
	 * so if the batch is loaded on a given system, a property-file using the specified
	 * name must exist.
	 * @param property the properties.
	 */
	public void setProperty( String property ) {
		this.property = property;
	}

	/**
	 * Returns if the simulation using cellular automaton is performed.
	 * @return true, if the simulation using cellular automaton is used.
	 */
	public boolean isUseCellularAutomaton() {
		return useCellularAutomaton;
	}

	/**
	 * Set if the simulation using cellular automaton is performed.
	 * @param useCellularAutomaton true if the cellular automaton should be used
	 */
	public void setUseCellularAutomaton( boolean useCellularAutomaton ) {
		this.useCellularAutomaton = useCellularAutomaton;
	}

	/**
	 * Returns if the optimization using the graph is performed.
	 * @return true, if the simulation using cellular automaton is used.
	 */
	public boolean isUseGraph() {
		return useGraph;
	}

	/**
	 * Set if the optimization using the graph is performed.
	 * @param useGraph true if the graph algorithm should be used.
	 */
	public void setUseGraph( boolean useGraph ) {
		this.useGraph = useGraph;
	}
}
