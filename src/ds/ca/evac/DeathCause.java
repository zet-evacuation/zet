/**
 * DeathCause.java
 * Created: 26.10.2012, 17:42:06
 */

package ds.ca.evac;

/**
 * Describes the cause of death if an individual dies.
 */
public enum DeathCause {
	/** If no exit is reachable. Happens if a person is surrounded by barriers. */
	ExitUnreachable,
	/** If the {@code Individual} is inside the building when the maximum evacuation time is over. */
	NotEnoughTime;
}
