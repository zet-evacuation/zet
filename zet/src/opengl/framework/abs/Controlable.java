/**
 * Controlable.java
 * Created 04.03.2010, 18:48:16
 */

package opengl.framework.abs;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface Controlable {
	
	void addTime( long timeNanoSeconds );

	void setTime( long time );

	void resetTime();

	void delete();

	/**
	 * Checks whether all parts of the simulation are finished, or not.
	 *  @return true if the simulation is finished, false otherwise
	 */
	public boolean isFinished();

}
