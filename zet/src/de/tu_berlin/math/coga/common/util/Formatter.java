/*
 * Formatter.java
 * Created 22.02.2010, 21:32:54
 */
package de.tu_berlin.math.coga.common.util;

import de.tu_berlin.math.coga.common.localization.DefaultLoc;
import java.text.NumberFormat;

/**
 * The class <code>Formatter</code> is a utility class that provides methods
 * for formatting texts.
 * @author Jan-Philipp Kappmeier, Martin Groß
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
	 * No instantiating of <code>ConversationTools</code> possible.
	 */
	private Formatter() {
	}

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
	 * Helper method that converts an double seconds value in an string
	 * representing minutes and seconds.
	 * @param sec
	 * @return a string representing the time in minutes
	 */
	public final static String secToMin( double sec ) {
		int min = (int)Math.floor( sec / 60 );
		int secs = (int)Math.floor( sec - (60 * min) );
		String ssecs = secs < 10 ? "0" + Integer.toString( secs ) : Integer.toString( secs );
		return ssecs.length() >= 2 ? Integer.toString( min ) + ":" + ssecs.substring( 0, 2 ) + " Min" : Integer.toString( min ) + ":" + ssecs + " Min";
	}

	/**
	 * Converts a time specified in milliseconds into a time unit appropriate
	 * for the length of the specified time and formats it as a human readable
	 * string. For example, 1445997 ms would be converted and formatted to
	 * "1.445 s".
	 * @param timeInMilliseconds the time in milliseconds
	 * @return the converted and formatted string
	 */
	public final static String formatTimeMilliseconds( long timeInMilliseconds ) {
		return formatTimeNanoseconds( timeInMilliseconds * 1000 * 1000 );
	}

	/**
	 * Converts a time specified in nanoseconds into a time unit appropriate for
	 * the length of the specified time and formats it as a human readable
	 * string. For example, 1445997106 ns would be converted and formatted to
	 * "1.445 s".
	 * @param timeInNanoseconds the time in nanoseconds.
	 * @return the converted and formatted string.
	 */
	public final static String formatTimeNanoseconds( long timeInNanoseconds ) {
		NumberFormat nf = DefaultLoc.getSingleton().getFloatConverter();
		nf.setMaximumFractionDigits( 3 );
		nf.setMinimumFractionDigits( 0 );
		double time = timeInNanoseconds;
		int counter = 0;
		//int last = 0;
		while( time >= 1000 && counter < 3 ) {
			//last = (int)(time % 1000);
			time /= 1000;
			counter++;
		}
		switch( counter ) {
			case 0:
				return nf.format( time ) + " ns"; //String.format( "%1$s ns", time );
			case 1:
				return nf.format( time ) + " µs"; //String.format( "%1$s ns", time );
			case 2:
				return nf.format( time ) + " ms"; //String.format( "%1$s ns", time );
			case 3:
				if( time <= 60 )
					return nf.format( time ) + " s";
					//return String.format( "%1$s.%2$03d s", time, last );
				else if( time > 60 ) {
					//last = (int)(time % 60);
					time /= 60;
					return nf.format( time ) + " min"; //String.format( "%1$s.%2$02d min", time, last );
				} else
					throw new AssertionError( "This should not happen." );
			default:
				throw new AssertionError( "This should not happen." );
		}
	}

	public final static String formatTimeNanosecondsWithoutLocale( long timeInNanoseconds ) {
		long time = timeInNanoseconds;
		int counter = 0;
		int last = 0;
		while( time >= 1000 && counter < 3 ) {
			last = (int)(time % 1000);
			time /= 1000;
			counter++;
		}
		switch( counter ) {
			case 0:
				return String.format( "%1$s ns", time );
			case 1:
				return String.format( "%1$s.%2$03d µs", time, last );
			case 2:
				return String.format( "%1$s.%2$03d ms", time, last );
			case 3:
				if( time <= 60 )
					return String.format( "%1$s.%2$03d s", time, last );
				else if( time > 60 ) {
					last = (int)(time % 60);
					time /= 60;
					return String.format( "%1$s.%2$02d min", time, last );
				} else
					throw new AssertionError( "This should not happen." );
			default:
				throw new AssertionError( "This should not happen." );
		}
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
}
