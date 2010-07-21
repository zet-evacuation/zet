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
package ds;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.graph.flow.PathBasedFlowOverTime;

/** The superclass of all tasks who execute graph algorithms. This class is
 * useful to access the graph algorithms in a generic way.
 *
 * @author Martin Groß
 */
public abstract class NetworkFlowModelAlgorithm extends Algorithm<NetworkFlowModel, PathBasedFlowOverTime> {

}
