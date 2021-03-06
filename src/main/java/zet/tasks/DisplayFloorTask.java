/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package zet.tasks;

import de.zet_evakuierung.model.Floor;
import zet.gui.main.tabs.quickVisualization.JRasterFloor;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class DisplayFloorTask implements Runnable {
	JRasterFloor rasterFloor;
	Floor floor;
	public DisplayFloorTask( JRasterFloor rasterFloor, Floor floor ) {
		this.rasterFloor = rasterFloor;
		this.floor = floor;
	}

	@Override
	public void run() {
		rasterFloor.displayFloor( floor );
		System.out.println( "100% for DisplayFloorTask sent" );
		//AlgorithmTask.getInstance().setProgress( 100, "", "" ); // in the end, send 100%
	}
}