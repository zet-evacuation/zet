/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * localization.java
 * Created on 09.01.2008, 23:29:26
 */

package localization;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *
 * @author Jan-Philipp Kappmeier, Timon Kelter
 */
public class Localization {
	private static Localization instance;
	private Locale currentLocale;
	private ResourceBundle bundle;
	private String prefix = "";
	private boolean returnKeyOnly = true;
	private static NumberFormat nfFloat;
	private static NumberFormat nfInteger;
					
	/**
	 * 
	 * @return the instance of the localization class
	 */
	public static Localization getInstance() {
		if( instance == null ) {
			instance = new Localization();
			instance.currentLocale = Locale.getDefault();
			nfFloat = NumberFormat.getNumberInstance( instance.getLocale() );
			nfInteger = NumberFormat.getIntegerInstance(instance.getLocale() );
		}
		return instance;
	}

	private Localization() throws MissingResourceException {
		bundle = ResourceBundle.getBundle( "localization.zevacuate", Locale.getDefault() );
	}
	
	public String getString( String key ) {
		if( key.equals( "" ) )
			return "";
		try {
			return bundle.getString( prefix + key );
		} catch( MissingResourceException ex ) {
			if( returnKeyOnly )
				return prefix + key;
			else
				return "Unknown Language key: '" + prefix + key + "'";
		}
	}

	public String getStringWithoutPrefix( String key ) {
		if( key.equals( "" ) )
			return "";
		try {
			return bundle.getString( key );
		} catch( MissingResourceException ex ) {
			if( returnKeyOnly )
				return key;
			else
				return "Unknown Language key: '" + key + "'";
		}
	}

	public void setPrefix( String prefix ) {
		this.prefix = prefix;
	}
	
	/**
	 * Loads a new localization ressource file from hard disk and sets new
	 * localized number converters. The language is set by a
	 * {@link java.util.Locale} object.
	 * <p>The localisation file has to be found in the localization folder and has
	 * the name zevacuate.properties with the language information respectiveley.
	 * </p>
	 * @param locale the locale that should be used
	 * @throws java.util.MissingResourceException if the locale cannot be found
	 */
	public void setLocale( Locale locale ) throws MissingResourceException {
		bundle = ResourceBundle.getBundle( "localization.zevacuate", locale );
		currentLocale = locale;
		nfFloat = NumberFormat.getNumberInstance( instance.getLocale() );
		nfInteger = NumberFormat.getIntegerInstance(instance.getLocale() );		
	}
	/**
	 * Returns the currently selected {@link java.util.Locale}, that allows to
	 * format and read localized numbers.
	 * @return The currently selected locale.
	 */
	public Locale getLocale () {
		return currentLocale;
	}
	
	/**
	 * 
	 * @return
	 */
	public final NumberFormat getFloatConverter() {
		return nfFloat;
	}
	
	/**
	 * 
	 * @return
	 */
	public final NumberFormat getIntegerConverter() {
		return nfInteger;
	}
}
