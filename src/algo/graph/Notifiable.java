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

import ds.graph.Percentage;

/**
 * Defines an interface for classes that can receive progress information
 * from a graph algorithm.
 */
public interface Notifiable {

	/**
	 * Collects a progress information.
	 * @param progressInformation The new state of the sender.
	 * @param sender The algorithm sending the information.
	 */
	public void recieveProgressInformation(Percentage percentageDone, Sender sender);
	
}
