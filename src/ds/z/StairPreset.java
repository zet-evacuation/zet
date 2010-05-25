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
	Indoor( 0.352, 0.471, "Indoor" ),
	Outdoor( 0.381, 0.525, "Outdoor" );

	/** The speed multiplication factor for walking upwards. */
	private double speedFactorUp;
	/** The speed multiplication factor for walking downwards. */
	private double speedFactorDown;
	/** The name of the stair type, used by {@link #toString()} */
	private String name;
	/** A description text for this type of stairs. */
	private String text;

	private StairPreset( double speedFactorUp, double speedFactorDown, String name ) {
		this.speedFactorUp = speedFactorUp;
		this.speedFactorDown = speedFactorDown;
		this.name = "ds.z.StairArea.Preset." + name + ".name";
		this.text = "ds.z.StairArea.Preset." + name + ".desc";
	}

	/**
	 * Returns the name of this preset.
	 * @return the name of this preset
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the speed factor for downwards walking for this stair preset.
	 * @return the speed factor down
	 */
	public double getSpeedFactorDown() {
		return speedFactorDown;
	}

	/**
	 * Returns the speed factor for upwards walking for this stair preset
	 * @return the speed factor up
	 */
	public double getSpeedFactorUp() {
		return speedFactorUp;
	}

	/**
	 * Returns a describing text for this stair preset.
	 * @return a describing text for this stair preset
	 */
	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return getName();
	}



}
