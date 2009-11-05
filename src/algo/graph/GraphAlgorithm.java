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
package algo.graph;

/**
 * Defines method graph algorithms must have.
 * Each algorithm has a class that has to be instantiated
 * for each run of the algorithm. The input is given to 
 * the algorithm in the constructor. The output shall be
 * calculated if the method <code>run()</code> is called.
 * The method <code>isProblemSolved()</code> returns whether
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
	public final boolean isProblemSolved(){
            return hasRun;
		//return isProblemSolved;
	}
	
}
