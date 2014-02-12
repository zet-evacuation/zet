/*
 * Formatter.java
 * Created 22.02.2010, 21:32:54
 */
package de.tu_berlin.math.coga.common.util;

import de.tu_berlin.math.coga.common.localization.DefaultLocalization;
import de.tu_berlin.math.coga.common.util.units.UnitScale;
import de.tu_berlin.math.coga.datastructure.Tuple;
import java.awt.Color;
import java.text.NumberFormat;

/**
 * The class {@code Formatter} is a utility class that provides methods
 * for formatting texts.
 * @author Jan-Philipp Kappmeier
 */
public class Formatter {

	/** No instantiating of {@code Formatter} possible. */
	private Formatter() { }

	/**
	 * Formats a double value (between 0 and 1) into a percent value, always
	 * showing 2 fraction digits.
	 * @param value the decimal value
	 * @return a string containing the decimal value
	 */
	public static String formatPercent( double value ) {
		NumberFormat nfPercent = DefaultLocalization.getSingleton().getPercentConverter();
		nfPercent.setMaximumFractionDigits( 2 );
		nfPercent.setMinimumFractionDigits( 2 );
		return nfPercent.format( value );
	}

	/**
	 * Computes the correct value and fitting time unit for a given pair of value
	 * and time unit. The result is stored as a {@link Tuple}.
	 * @param <E> the type of time unit that is returned
	 * @param value the value of the number to be formatted
	 * @param unit the unit of the number
	 * @return the pair containing the transformed value and the fitting unit
	 */
	public static <E extends UnitScale<E>> Tuple<Double,E> unit( double value, E unit ) {
		while( !unit.isInRange( value ) ) {
			final double newValue = unit.getBetterUnitValue( value );
			unit = unit.getBetterUnit( value );
			value = newValue;
		}
		return new Tuple<>( value, unit );
	}

	/**
	 * Formats a given number of some unit to the most fitting unit.
	 * @param <E> the type of the unit
	 * @param value the value of the number to be formatted
	 * @param unit the unit of the number
	 * @return the string in the calculated unit with one decimal place and the shortcut for the unit
	 */
	public static <E extends UnitScale<E>> String formatUnit( double value, E unit ) {
		return formatUnit( value, unit, 2 );
	}

	/**
	 * Formats a given number of some unit to the most fitting unit.
	 * @param <E> the type of the time unit
	 * @param value the value of the number to be formatted
	 * @param unit the unit of the number
	 * @param digits the number of digits after the comma in the representation
	 * @return the string in the calculated unit with one decimal place and the shortcut for the unit
	 */
	public static <E extends UnitScale<E>> String formatUnit( double value, E unit, int digits ) {
		final Tuple<Double,E> res = unit( value, unit );
		final NumberFormat n = NumberFormat.getInstance();
		n.setMaximumFractionDigits( digits );
		return n.format( res.getU() ) + " " + res.getV().getName() ;
	}

	/**
	 * Creates a {@code String} containing an integer number with leading zeros.
	 * @param number the number that is converted to string representation
	 * @param digits the digits of the number
	 * @return the number with leading zeros
	 * @throws java.lang.IllegalArgumentException if the number has to many digits
	 */
	public static String fillLeadingZeros( int number, int digits ) throws IllegalArgumentException {
		String ret = Integer.toString( number );
		if( ret.length() > digits )
			throw new java.lang.IllegalArgumentException( "Number " + number + " is too long. Only " + digits + " digits are allowed." );
		while( ret.length() < digits )
			ret = "0" + ret;
		return ret;
	}

	public static String rgbToHex( int r, int g, int b ) {
		return String.format( "#%02x%02x%02x", r, g, b );
	}

	public static String colorToHex( Color c ) {
		return "#" + Integer.toHexString( c.getRGB() ).substring( 2 );
	}
}