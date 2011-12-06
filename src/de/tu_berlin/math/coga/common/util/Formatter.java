/*
 * Formatter.java
 * Created 22.02.2010, 21:32:54
 */
package de.tu_berlin.math.coga.common.util;

import de.tu_berlin.math.coga.common.localization.DefaultLoc;
import de.tu_berlin.math.coga.datastructure.Tuple;
import java.text.NumberFormat;

/**
 * The class {@code Formatter} is a utility class that provides methods
 * for formatting texts.
 * @author Jan-Philipp Kappmeier
 */
public class Formatter {
	public enum BinaryUnits {
		Bit( "Bits", "Bits" ),
		Byte( "Bytes", "Bytes" ),
		KiB( "KiB", "Kibibyte" ),
		MiB( "MiB", "Mebibyte" ),
		GiB( "GiB", "Gibibyte" ),
		TiB( "TiB", "Tebibyte" ),
		PiB( "PiB", "Pebibyte" ),
		EiB( "EiB", "Exbibyte" ),
		ZiB( "ZiB", "Zebibyte" ),
		YiB( "YiB", "Yobibyte" );

		String rep;
		String longRep;

		private BinaryUnits( String rep, String longRep ) {
			this.rep = rep;
			this.longRep = longRep;
		}

		public boolean isOK( double value ) {
			switch( this ) {
				case Bit:
					return value <= 8 ? true : false;
				case Byte:
				case KiB:
				case MiB:
				case GiB:
				case TiB:
				case PiB:
				case EiB:
				case ZiB:
					return value >= 1 && value <= 1024 ? true : false;
				default:
					return value >= 1 ? true : false;
			}
		}

		public BinaryUnits getNextBetter( double value ) {
			switch( this ) {
				case Bit:
					return value <= 8 ? Bit : Byte;
				case Byte:
					if( value <= 1 )
						return Bit;
					else if( value >= 1024 )
						return KiB;
					return Byte;
				case KiB:
					if( value <= 1 )
						return Byte;
					else if( value >= 1024 )
						return MiB;
					return KiB;
				case MiB:
					if( value <= 1 )
						return KiB;
					else if( value >= 1024 )
						return GiB;
					return MiB;
				case GiB:
					if( value <= 1 )
						return MiB;
					else if( value >= 1024 )
						return TiB;
					return GiB;
				case TiB:
					if( value <= 1 )
						return GiB;
					else if( value >= 1024 )
						return PiB;
					return TiB;
				case PiB:
					if( value <= 1 )
						return TiB;
					else if( value >= 1024 )
						return EiB;
					return PiB;
				case EiB:
					if( value <= 1 )
						return PiB;
					else if( value >= 1024 )
						return ZiB;
					return EiB;
				case ZiB:
					if( value <= 1 )
						return EiB;
					else if( value >= 1024 )
						return YiB;
					return ZiB;
				default:
					if( value <= 1 )
						return GiB;
					return TiB;
			}
		}

		public double getNextBetterValue( double value ) {
			switch( this ) {
				case Bit:
					return value <= 8 ? value : value / 8;
				case Byte:
					if( value <= 1 )
						return value * 8;
					else if( value >= 1024 )
						return value / 1024;
					return value;
				case KiB:
				case MiB:
				case GiB:
				case TiB:
				case PiB:
				case EiB:
				case ZiB:
					if( value <= 1 )
						return value * 1024;
					else if( value >= 1024 )
						return value / 1024;
					return value;
				default:
					if( value <= 1 )
						return value * 1024;
					return value;
			}
		}

		public String getName() {
			return rep;
		}

		public String getLongName() {
			return longRep;
		}
	}

	/**
	 * An enumeration containing time units following the SI system for times less
	 * than a seconds. For larger times, the non-SI units minutes, hours etc. are
	 * used.
	 */
	public enum TimeUnits {
		/** One picosecond equals 10^-12 seconds. */
		PicoSeconds( "ps", "picosecond", 1000, null ),
		/** One picosecond equals 10^-9 seconds. */
		NanoSeconds( "ns", "nanoseconds", 1000, PicoSeconds ),
		/** One picosecond equals 10^-6 seconds. */
		Microsecond( "µs", "mycroseconds", 1000, NanoSeconds ),
		/** One picosecond equals 10^-3 seconds. */
		MilliSeconds( "ms", "milliseconds", 1000, Microsecond ),
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
		private TimeUnits pre;
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
			this.pre = pre;
		}

		/**
		 * Checks, if the value is good for the current unit, or if it can be scaled to fit better.
		 * @param value the value that is checked
		 * @return {@code true} if there is no need to scale the value to fit a better unit, {@code false} otherwise
		 */
		public boolean isOK( double value ) {
			return getNextBetter( value ) == this;
		}

		/**
		 * Returns the next better value to represent the time measure.
		 * @param value the current value (in the unit specified by the instance of the enumeration)
		 * @return the transformed time measure
		 */
		public TimeUnits getNextBetter( double value ) {
			if( value < 1 && pre != null )
				return this.pre;
			else if( value >= toNext )
				return next;
			else 
				return this;
		}

		/**
		 * Returns the next better time unit to represent the time measure
		 * @param value the current value (in the unit specified by the instance of the enumeration)
		 * @return the transformed time unit
		 */
		public double getNextBetterValue( double value ) {
			if( value < 1 && pre != null )
				return value * pre.toNext;
			else if( value >= toNext )
				return value / toNext;
			else return value;
		}

		/**
		 * The short scientific representation for this time unit
		 * @return the short scientific representation for this time unit
		 */
		public String getName() {
			return rep;
		}

		/**
		 * A longer representation of the time unit.
		 * @return a longer representation of the time unit
		 */
		public String getLongName() {
			return longRep;
		}
	}

	/** No instantiating of {@code ConversationTools} possible. */
	private Formatter() { }

	/**
	 * Formats a given number of some unit to the most fitting unit.
	 * @param value the value of the number to be formatted
	 * @param unit the unit of the number
	 * @return the string in the calculated unit with one decimal place and the shortcut for the unit
	 */
	public final static String fileSizeUnit( double value, BinaryUnits unit ) {
		return fileSizeUnit( value, unit, 1 );
	}

	/**
	 * Formats a given number of some unit to the most fitting unit.
	 * @param value the value of the number to be formatted
	 * @param unit the unit of the number
	 * @param digits the number of digits after the comma in the representation
	 * @return the string in the calculated unit with one decimal place and the shortcut for the unit
	 */
	public final static String fileSizeUnit( double value, BinaryUnits unit, int digits ) {
		while( !unit.isOK( value ) ) {
			final double newValue = unit.getNextBetterValue( value );
			unit = unit.getNextBetter( value );
			value = newValue;
		}
		final NumberFormat n = NumberFormat.getInstance();
		n.setMaximumFractionDigits( digits );
		return n.format( value ) + " " + unit.getName();
	}

	/**
	 * Formats a double value (between 0 and 1) into a percent value, always
	 * showing 2 fraction digits.
	 * @param value the decimal value
	 * @return a string containing the decimal value
	 */
	public final static String formatPercent( double value ) {
		NumberFormat nfPercent = DefaultLoc.getSingleton().getPercentConverter();
		nfPercent.setMaximumFractionDigits( 2 );
		nfPercent.setMinimumFractionDigits( 2 );
		return nfPercent.format( value );
	}

	/**
	 * Computes the correct value and fitting time unit for a given pair of value
	 * and time unit. The result is stored as a tuple.
	 * @param value the value of the number to be formatted
	 * @param unit the unit of the number
	 * @return the pair containing the transformed value and the fitting unit
	 */
	public final static Tuple<Double,TimeUnits> timeUnit( double value, TimeUnits unit ) {
		while( !unit.isOK( value ) ) {
			final double newValue = unit.getNextBetterValue( value );
			unit = unit.getNextBetter( value );
			value = newValue;
		}
		return new Tuple( value, unit );
	}

	/**
	 * Formats a given number of some unit to the most fitting unit.
	 * @param value the value of the number to be formatted
	 * @param unit the unit of the number
	 * @return the string in the calculated unit with one decimal place and the shortcut for the unit
	 */
	public final static String formatTimeUnit( double value, TimeUnits unit ) {
		return formatTimeUnit( value, unit, 2 );
	}

	/**
	 * Formats a given number of some unit to the most fitting unit.
	 * @param value the value of the number to be formatted
	 * @param unit the unit of the number
	 * @param digits the number of digits after the comma in the representation
	 * @return the string in the calculated unit with one decimal place and the shortcut for the unit
	 */
	public final static String formatTimeUnit( double value, TimeUnits unit, int digits ) {
		final Tuple<Double,TimeUnits> res = timeUnit( value, unit );
		final NumberFormat n = NumberFormat.getInstance();
		n.setMaximumFractionDigits( digits );
		return n.format( res.u ) + " " + res.v.getName() ;
	}
}
