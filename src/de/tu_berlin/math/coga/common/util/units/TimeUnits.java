/**
 * TimeUnits.java
 * Created: 18.10.2012, 13:42:54
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.tu_berlin.math.coga.common.util.units;

import de.tu_berlin.math.coga.common.util.Helper;

/**
 * An enumeration containing time units following the SI system for times less
 * than a seconds. For larger times, the non-SI units minutes, hours etc. are
 * used.
 */
public enum TimeUnits implements UnitScale<TimeUnits> {
	/** One picosecond equals 10^-12 seconds. */
	PicoSeconds( "ps", "picosecond", null ),
	/** One picosecond equals 10^-9 seconds. */
	NanoSeconds( "ns", "nanoseconds", PicoSeconds ),
	/** One picosecond equals 10^-6 seconds. */
	Microsecond( "µs", "mycroseconds", NanoSeconds ),
	/** One picosecond equals 10^-3 seconds. */
	MilliSeconds( "ms", "milliseconds", Microsecond ),
	/** The international second. */
	Seconds( "s", "seconds", 60, MilliSeconds ),
	/** The non-SI unit for 60 seconds. */
	Minutes( "min", "minutes", 60, Seconds ),
	/** The non-SI unit for 60 minutes. */
	Hours( "h", "hour", 24, Minutes ),
	/** The non-SI unit for 24 hours. */
	 Days( "d", "day", 365.25, Hours ),
	/** The non-SI unit for a Julian year which is 365.25 days. */
	Years( "a", "year", 100, Days ),
	/** The rarely used unit for a century, which are approximately 100 years. */
	Centuries( "c", "century", 10, Years ),
	/** A Julian millennium consists of 365,250 days. */
	Millenia( "m", "millenium", Integer.MAX_VALUE, Centuries );
	/** A scientific representation for the time unit. */
	private String rep;
	/** A longer representation for the time unit. */
	private String longRep;
	/** Describes how much of this unit gives one unit of the next larger scale. */
	private double toNext;
	/** The predecessor unit (next smaller scale). */
	private TimeUnits previous;
	/** The successor unit (next larger scale). */
	private TimeUnits next;
	static {
		PicoSeconds.next = NanoSeconds;
		NanoSeconds.next = Microsecond;
		Microsecond.next = MilliSeconds;
		MilliSeconds.next = Seconds;
		Seconds.next = Minutes;
		Minutes.next = Hours;
		Hours.next = Days;
		Days.next = Years;
		Years.next = null;
	}

	/**
	 * Initializes the time unit with the correct values.
	 * @param rep the short representation string
	 * @param longRep the long representation string
	 * @param toNext how much of the unit is the next larger scale
	 * @param pre the predecessor unit. Note that successor units are initialized in a static-initializer block
	 */
	private TimeUnits( String rep, String longRep, double toNext, TimeUnits pre ) {
		this.rep = rep;
		this.longRep = longRep;
		this.toNext = toNext;
		this.previous = pre;
	}

	/**
	 * Initializes the time unit with the correct values.
	 * @param rep the short representation string
	 * @param longRep the long representation string
	 * @param pre the predecessor unit. Note that successor units are initialized in a static-initializer block
	 */
	private TimeUnits( String rep, String longRep, TimeUnits pre ) {
		this( rep, longRep, 1000, pre );
	}

	@Override
	public double getRange() {
		return toNext;
	}

	/**
	 * Checks, if the value is good for the current unit, or if it can be scaled to fit better.
	 * @param value the value that is checked
	 * @return {@code true} if there is no need to scale the value to fit a better unit, {@code false} otherwise
	 */
	@Override
	public boolean isInRange( double value ) {
		return getBetterUnit( value ) == this;
	}

	/**
	 * Returns the next better value to represent the time measure.
	 * @param value the current value (in the unit specified by the instance of the enumeration)
	 * @return the transformed time measure
	 */
	@Override
	public TimeUnits getBetterUnit( double value ) {
		return Helper.getNextBetter( this, value );
	}

	/**
	 * Returns the next better time unit to represent the time measure
	 * @param value the current value (in the unit specified by the instance of the enumeration)
	 * @return the transformed time unit
	 */
	@Override
	public double getBetterUnitValue( double value ) {
		return Helper.getNextBetterValue( this, value );
	}

	@Override
	public TimeUnits getSmaller() {
		return previous;
	}

	@Override
	public TimeUnits getLarger() {
		return next;
	}

	/**
	 * The short scientific representation for this time unit
	 * @return the short scientific representation for this time unit
	 */
	@Override
	public String getName() {
		return rep;
	}

	/**
	 * A longer representation of the time unit.
	 * @return a longer representation of the time unit
	 */
	@Override
	public String getLongName() {
		return longRep;
	}
}
