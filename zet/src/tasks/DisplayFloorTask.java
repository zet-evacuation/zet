/**
 * DisplayFloorTask.java
 * Created: 04.02.2011, 16:47:28
 */
package tasks;

import ds.z.Floor;
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
	}

}
