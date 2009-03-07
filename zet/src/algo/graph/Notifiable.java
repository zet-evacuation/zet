package algo.graph;

import ds.Percentage;

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
