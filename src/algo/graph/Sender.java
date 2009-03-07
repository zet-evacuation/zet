package algo.graph;

/**
 * Defines an interface for graph algorithms that send progress information.
 */
public interface Sender {

	/**
	 * Sets the object that shall receive the progress information from
	 * the graph algorithm.
	 * @param reciever the receiving object.
	 */
	public void setReciever(Notifiable receiver);
	
}
