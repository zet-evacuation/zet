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

/*
 * AlgorithmTerminatedEvent.java
 *
 */
package algo.graph;

/**
 * A special algorithm event that occurs when the algorithm terminates (either
 * by completing successfully or by an uncaught runtime exception).
 * 
 * @author Martin Groß
 */
public class AlgorithmTerminatedEvent extends AlgorithmEvent {

    /**
     * Creates an <code>AlgorithmTerminatedEvent</code> for the specified
     * algorithm.
     * @param algorithm the algorithm that has terminated.
     */
    public AlgorithmTerminatedEvent(Algorithm algorithm) {
        super(algorithm, algorithm.getStartTime() + algorithm.getRuntime());
    }
}
