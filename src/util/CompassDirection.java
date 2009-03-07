/**
 * Class CompassDirection
 * Erstellt 14.10.2008, 23:34:16
 */
package util;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public enum CompassDirection {
	NORTH(),
	SOUTH( NORTH ),
	EAST(),
	WEST( EAST );
	private CompassDirection inverseDirection;

	private CompassDirection( CompassDirection inverseDirection ) {
		this.inverseDirection = inverseDirection;
		inverseDirection.setInverse( this );
	}

	private CompassDirection() {
	}

	public CompassDirection invert() {
		return inverseDirection;
	}

	private void setInverse( CompassDirection inverseDirection ) {
		this.inverseDirection = inverseDirection;
	}
}
