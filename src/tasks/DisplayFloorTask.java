/**
 * DisplayFloorTask.java
 * Created: 04.02.2011, 16:47:28
 */
package tasks;

import batch.tasks.AlgorithmTask;
import de.tu_berlin.math.coga.common.util.Helper;
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
//		for( int i = 1; i < 100; ++i ) {
//			Helper.pause( 50 );
//			AlgorithmTask.getInstance().setProgress( i, "", "" );
//			System.out.println( "Step " + i + " executed." );
//		}
//
//		AlgorithmTask.getInstance().setProgress( 100, "", "" );
//
	}

}
