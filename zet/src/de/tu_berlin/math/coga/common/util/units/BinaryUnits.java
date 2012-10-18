/**
 * BinaryUnits.java
 * Created: 18.10.2012, 12:12:37
 */

package de.tu_berlin.math.coga.common.util.units;

import de.tu_berlin.math.coga.common.util.Helper;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
		
public enum BinaryUnits implements UnitScale<BinaryUnits> {
	Bit( "Bits", "Bits", 8, null ),
	Byte( "Bytes", "Bytes", Bit ),
	KiB( "KiB", "Kibibyte", Byte ),
	MiB( "MiB", "Mebibyte", KiB ),
	GiB( "GiB", "Gibibyte", MiB ),
	TiB( "TiB", "Tebibyte", GiB ),
	PiB( "PiB", "Pebibyte", TiB ),
	EiB( "EiB", "Exbibyte", PiB ),
	ZiB( "ZiB", "Zebibyte", EiB ),
	YiB( "YiB", "Yobibyte", ZiB );
	private String rep;
	private String longRep;
	private BinaryUnits previous;
	private BinaryUnits next;
	private double toNext;

	static {
		Bit.next = Byte;
		Byte.next = KiB;
		KiB.next = MiB;
		MiB.next = GiB;
		GiB.next = TiB;
		TiB.next = PiB;
		PiB.next = EiB;
		EiB.next = ZiB;
		ZiB.next = YiB;
		YiB.next = null;
	}

private BinaryUnits( String rep, String longRep, double toNext, BinaryUnits previous ) {
		this.rep = rep;
		this.longRep = longRep;
		this.toNext = toNext;
	}

	private BinaryUnits( String rep, String longRep, BinaryUnits previous ) {
		this( rep, longRep, 1024, previous );
	}

	@Override
	public double getRange() {
		return toNext;
	}

	@Override
	public boolean isInRange( double value ) {
		return getBetterUnit( value ) == this;
	}

	@Override
	public BinaryUnits getBetterUnit( double value ) {
		return Helper.getNextBetter( this, value );
	}

	@Override
	public double getBetterUnitValue( double value ) {
		return Helper.getNextBetterValue( this, value );
	}

	@Override
	public BinaryUnits getSmaller() {
		return previous;
	}

	@Override
	public BinaryUnits getLarger() {
		return next;
	}

	@Override
	public String getName() {
		return rep;
	}

	@Override
	public String getLongName() {
		return longRep;
	}
}