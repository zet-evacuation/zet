/*
 * Formatter.java
 * Created 22.02.2010, 21:32:54
 */
package de.tu_berlin.math.coga.common.util;

import de.tu_berlin.math.coga.common.localization.Localization;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * The class <code>Formatter</code> is a utility class that provides methods
 * for formatting texts.
 * @author Jan-Philipp Kappmeier, Martin Groß
 */
public class Formatter {
	/** An array containing text for several file size measures. */
	static final String[] fileSizes = {"Bytes", "KiB", "MiB", "GiB", "TiB"};

	/**
	 * No instantiating of <code>ConversationTools</code> possible.
	 */
	private Formatter() {
	}

	/**
	 * Formats a given number of bits to the largest possible unit. Supported
	 * units are "Bytes", "Kilobytes", "Megabytes" and "Gigabytes".
	 * @param bits the bits
	 * @return the string in the calculated unit with one decimal place and the shortcut for the unit
	 */
	public final static String bitToMaxFilesizeUnit( double bits ) {
		final NumberFormat n = NumberFormat.getInstance();
		n.setMaximumFractionDigits( 1 );
		double ret = bits / 8;	// rets is in bytes
		int i = 0;
		while( ret > 1024 && i++ < fileSizes.length - 1 )
			ret /= 1024;
		return n.format( ret ) + " " + fileSizes[Math.min( i, fileSizes.length - 1 )];
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
		NumberFormat nf = Localization.getInstance().getFloatConverter();
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
		NumberFormat nfPercent = Localization.getInstance().getPercentConverter();
		nfPercent.setMaximumFractionDigits( 2 );
		nfPercent.setMinimumFractionDigits( 2 );
		return nfPercent.format( value );
	}
}
