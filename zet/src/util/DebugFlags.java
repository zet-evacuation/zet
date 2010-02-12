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
package util;

public class DebugFlags {
	
	
	public final static boolean DXF = false;
	public final static boolean EVAPLANCHECKER = false;

	public final static boolean RASTER = false;
	public final static boolean NODECREATION = false;
	public final static boolean NODECREATIONSMALL = false;
	public final static boolean GRAPH_DOORS = false;
	//public final static boolean CONVERTING = false;
	// Switch output for earliest arrival transshipment on or off.
	//public final static boolean TEST = MEL && false;
	// Switch short output for earliest arrival transshipment on or off.
	//public final static boolean TEST_SHORT = TEST && false;
	// Switch long output for earliest arrival transshipment on or off.
	//public final static boolean TEST_LONG = TEST_SHORT && false;
	
	// Debug switches for cellular automaton
	public final static boolean RULESET = false;
	public final static boolean CA_ALGO = false;
	public final static boolean CA_SWAP = false;
	public final static boolean CA_SWAP_USED_OUTPUT = false;	// Use this flag to show hints like "Quetschregel oder Individuum läuft doch nicht!!"
	
	// Debug switches for visualization
	public final static boolean VIS_CA = false;
}
