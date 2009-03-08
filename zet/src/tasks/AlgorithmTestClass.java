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
 * AlgorithmTestClass.java
 * Created on 24.01.2008, 00:41:08
 */
package tasks;

import java.util.Random;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class AlgorithmTestClass implements Runnable {
	int sleepTime = 1000;

	public AlgorithmTestClass() {	}
	
	public AlgorithmTestClass( int sleepTime ) {
		setSleepTime( sleepTime );
	}
	
	public void run() {
		Random random = new Random();
		int progress = 0;
		//Initialize progress property.
		AlgorithmTask.getInstance().setProgress( 0, "Zufallsfortschritt", "" );

		while( progress < 100 ) {
			//Sleep a bit.
			try {
				Thread.sleep( random.nextInt( sleepTime ) );
			} catch( InterruptedException ignore ) {
			}
			//Make random progress.
			progress += random.nextInt( 10 );
			AlgorithmTask.getInstance().setProgress( Math.min( progress, 100 ), "Zufallsfortschritt", Math.min( progress, 100 ) + "%" );
		}
	}
	
	public int getSleepTime() {
		return sleepTime;
	}
	
	public void setSleepTime( int sleepTime ) {
		this.sleepTime = sleepTime;
	}
}
