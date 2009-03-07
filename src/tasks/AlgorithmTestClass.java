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
