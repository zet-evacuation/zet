/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package batch;

import ds.Project;
import ds.z.Assignment;
import gui.batch.EvacuationOptimizationType;
import gui.editor.properties.PropertyFilesSelectionModel.Property;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a batch job that consists of multiple batch entries. Each entry stands
 * for multiple runs of the entry's CA and a single run of the entry's graph algo,
 * where the graph algo is executed with the concrete assignment of the first CA run.
 *
 * @author Timon
 */
public class Batch {
	private ArrayList<BatchEntry> entries = new ArrayList<BatchEntry> ();
		
	/** Executes the whole batch.
	 * 
	 * @param storeEntriesInFiles Whether this batch should save it's result 
	 * entries in temporary files.
	 * @return The result of the batch computations.
	 * @throws Exception Thrown by the CA or Graph construction algos
	 */
	public BatchResult execute (boolean storeEntriesInFiles) throws Exception {
		BatchResult res = new BatchResult (storeEntriesInFiles);
		
		for (BatchEntry e : entries) {
			BatchResultEntry[] enres = e.execute();
			if (enres[0] != null) {
				res.addResult (enres[0]);
			}
			if (enres[1] != null) {
				res.addResult (enres[1]);
			}
		}
		System.gc ();
		
		return res;
	}
	
	/**
	 * Creates a new BatchEntry with the specified name which contains the given 
	 * project.
	 * @param name the name of the project
	 * @param project the project
	 * @param cycles the number of ca simulation runs
	 * @param ga the used graph algorithm for optimization
	 * @param caa the cellular automaton algorithm used for simulation
	 * @throws java.lang.IllegalArgumentException if an error occured
	 */
	public void addEntry (String name, Project project, int cycles, GraphAlgorithm ga, CellularAutomatonAlgorithm caa) throws IllegalArgumentException{
		entries.add (new BatchEntry (name, project, cycles, ga, caa));
	}
	
	/**
	 * Creates a new BatchEntry with the specified name which contains the given 
	 * project.
	 * @param name the name of the project
	 * @param project the project
	 * @param assignment one assignment of the project
	 * @param useCA indicates wheather ca simulation is used
	 * @param caTime the maximal time allowed for ca simulation
	 * @param cycles the number of ca simulation runs
	 * @param ga the used graph algorithm for optimization
	 * @param caa the cellular automaton algorithm used for simulation
	 * @param useGraph indicates wheather graph optimization is used
	 * @param graphMaxTime the time horizon, used for some graph algorithms
	 * @param eot the evacuation optimization type 
	 * @param eoRuns the number of optimization runs
	 * @param property the properties loaded befor the batch is executed
	 * @throws java.lang.IllegalArgumentException if an error occured
	 */
	public void addEntry(String name, Project project, Assignment assignment, boolean useCA, double caTime, int cycles, GraphAlgorithm ga, CellularAutomatonAlgorithm caa, boolean useGraph, int graphMaxTime, EvacuationOptimizationType eot, int eoRuns, Property property ) throws IllegalArgumentException {
		entries.add( new BatchEntry( name, project, assignment, useCA, caTime, cycles, ga, caa, useGraph, graphMaxTime, eot, eoRuns, property ) );
	}
	public void removeEntry (BatchEntry e) {
		entries.remove (e);
	}
	public List<BatchEntry> getEntries () {
		return Collections.unmodifiableList (entries);
	}
	public void clearEntries () {
		entries.clear ();
	}

	
	/** Sets the "cycle" parameter on all entries to the same, given number.
	 * 
	 * @param cycles The new number of cycles
	 */
	public void setCyclesForAllEntries (int cycles) {
		for (BatchEntry e : entries) {
			e.setCycles (cycles);
		}
	}
}