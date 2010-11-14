/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/*
 * Localization.java
 * Created on 09.01.2008, 23:29:26
 */

package de.tu_berlin.math.coga.common.localization;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <code>Loalization</code> is a class that provides the ability to localize an application. It supports access to files
 * containing localized strings and number format conventions.
 * @author Jan-Philipp Kappmeier
 */
public abstract class Localization {
	/** The resource bundle that is selected (containing the localized strings). */
	private ResourceBundle bundle;
	/** A prefix that is added to the keys for localized strings. */
	private String prefix = "";
	/** Indicates if only the key is returned, if an unknown key was used. Otherwise some larger text is returned. */
	private boolean returnKeyOnly = true;
	/** The currently set locale (the information about the country).  */
	private static Locale currentLocale;
	/** A number formatter for floating point numbers. */
	private static NumberFormat nfFloat;
	/** A number formatter for integral numbers. */
	private static NumberFormat nfInteger;
	/** A number formatter for percent values. */
	private static NumberFormat nfPercent;
	/** The resource bundle that is used by this localization instance. */
	private final String bundleName;
	/** A list of all localization objects. */
	private static ArrayList<Localization> locs = new ArrayList<Localization>();

	/**
	 * Creates a new instance of the singleton and initializes with the default locale of the system.
	 * @param bundleName
	 * @throws MissingResourceException if no resource bundle for the default system locale is found
	 */
	protected Localization( String bundleName ) throws MissingResourceException {
		this.bundleName = bundleName;
		if( locs.isEmpty() ) {	// we have the first constructor call. Create static objects.
			currentLocale = Locale.getDefault();
			nfFloat = NumberFormat.getNumberInstance( currentLocale );
			nfInteger = NumberFormat.getIntegerInstance( currentLocale );
			nfPercent = NumberFormat.getPercentInstance( currentLocale );
		}
		bundle = ResourceBundle.getBundle( bundleName, currentLocale );
		add();
	}

	/**
	 * Adds this
	 */
	private void add() {
		locs.add( this );
	}
	
	/**
	 * Returns a localized string assigned to a key. The currently set prefix is added to the key.
	 * @param key the key specifying the loaded string
	 * @return the localized string
	 */
	public final String getString( String key ) {
		try {
			return key.isEmpty() ? "" : bundle.getString( prefix + key );
		} catch( MissingResourceException ex ) {
			return returnKeyOnly ? prefix + key : "Unknown Language key: '" + prefix + key + "'";
		}
	}

	/**
	 * Returns a localized string assigned to a key. No prefix is added to the key.
	 * @param key the key specifying the loaded string
	 * @return the localized string
	 */
	public final String getStringWithoutPrefix( String key ) {
		try {
			return key.isEmpty() ? "" : bundle.getString( key );
		} catch( MissingResourceException ex ) {
			return returnKeyOnly ? key : "Unknown Language key: '" + key + "'";
		}
	}

	/**
	 * Sets a prefix that is added to the key if {@link #getString(String)} is used.
	 * @param prefix the prefix that is added
	 */
	public final void setPrefix( String prefix ) {
		this.prefix = prefix;
	}

	/**
	 * Clears any prefix.
	 * @see #setPrefix(java.lang.String) 
	 */
	public final void clearPrefix() {
		setPrefix( "" );
	}

	/**
	 * Loads a new localization resource file from hard disk and sets new
	 * localized number converters. The language is set by a
	 * {@link java.util.Locale} object.
	 * <p>The localization file has to be found in the localization folder and has
	 * the name zevacuate.properties with the language information respectively.
	 * </p>
	 * @param locale the locale that should be used
	 * @throws java.util.MissingResourceException if the locale cannot be found
	 */
	public final void setLocale( Locale locale ) throws MissingResourceException {
		currentLocale = locale;
		nfFloat = NumberFormat.getNumberInstance( currentLocale );
		nfInteger = NumberFormat.getIntegerInstance( currentLocale );
		nfPercent = NumberFormat.getPercentInstance( currentLocale );
		for( Localization loc : locs )
			loc.setLocaleIntern( locale );
	}

	private void setLocaleIntern( Locale locale ) throws MissingResourceException {
		bundle = ResourceBundle.getBundle( bundleName, locale );
	}
	
	/**
	 * Returns the currently selected {@link java.util.Locale}, that allows to
	 * format and read localized numbers.
	 * @return The currently selected locale.
	 */
	public final Locale getLocale () {
		return currentLocale;
	}
	
	/**
	 * Returns a formatter to read and write system specific floating point numbers.
	 * @return a formatter to read and write system specific floating point numbers
	 */
	public final NumberFormat getFloatConverter() {
		return nfFloat;
	}
	
	/**
	 * Returns a formatter to read and write system specific integral numbers.
	 * @return a formatter to read and write system specific integral numbers
	 */
	public final NumberFormat getIntegerConverter() {
		return nfInteger;
	}

	/**
	 * Returns a formatter that reads and writes percent values in the current locale.
	 * @return a formatter that reads and writes percent values in the current locale
	 */
	public final NumberFormat getPercentConverter() {
		return nfPercent;
	}

	@Override
	public String toString() {
		return "Localization: " + bundleName;
	}

}
