/**
 * StairPreset.java
 * Created: 07.05.2010 10:51:20
 */
package ds.z;


/**
 * A preset with data for stairs up and down to choose from in the graphical
 * user interface. This values we have discovered to be quite useful. They can
 * be used if no other data is given.
 * @author Jan-Philipp Kappmeier
 */
public enum StairPreset {
	Indoor( 0.356, 0.59, "stairIndoor", "Innentreppen" ),
	Outdoor( 0.404, 0.59, "stairOutdoor", "Außentreppen" );

	/** The speed multiplication factor for walking upwards. */
	double speedUp;
	/** The speed multiplication factor for walking downwards. */
	double speedDown;
	/** The name of the stair type, used by {@link #toString()} */
	String name;
	/** A description text for this type of stairs. */
	String text;

	private StairPreset( double speedUp, double speedDown, String name, String text ) {
		this.speedUp = speedUp;
		this.speedDown = speedDown;
		this.name = name;
		this.text = text;
	}


}
