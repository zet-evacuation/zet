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
/**
 * Class IOTools
 * Erstellt 20.09.2008, 17:51:39
 */

package io;

import java.io.File;
import java.text.ParseException;
import localization.Localization;

/**
 * A set of helper methods for input and output operations.
 * @author Jan-Philipp Kappmeier
 */
public class IOTools {

	/**
	 * Creates a new filename of the type pathPrefix### where ### indicates an
	 * increasing number with <code>digits</code> digits. The new created filename
	 * has a greatest number. Gaps in the numbering are ignored.
	 * @param path the path
	 * @param filePrefix the prefix of the files
	 * @param digits the number of digits of the numbering
	 * @return the new filename including the path, the prefix and the number
	 * @throws java.lang.IllegalArgumentException if digits is less or equal to zero
	 * @throws java.lang.IllegalStateException if there are too many files beginning with prefix for the specified number of digits or if an error converting the digits occured.
	 */
	public static String getNextFreeNumberedFilepath( String path, String filePrefix, int digits ) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException {
		return path + getNextFreeNumberedFilename( path, filePrefix, digits );
	}
	
	/**
	 * Creates a new filename of the type pathPrefix### where ### indicates an
	 * increasing number with <code>digits</code> digits. The new created filename
	 * has a greatest number. Gaps in the numbering are ignored.
	 * @param path the path in which the file shall be created.
	 * @param filePrefix the prefix of the files
	 * @param digits the number of digits of the numbering
	 * @return the new filename without the path, the prefix and the number
	 * @throws java.lang.IllegalArgumentException if digits is less or equal to zero
	 * @throws java.lang.IllegalStateException if there are too many files beginning with prefix for the specified number of digits or if an error converting the digits occured.
	 */
	public static String getNextFreeNumberedFilename( String path, String filePrefix, int digits ) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException {
		if( digits <= 0 )
			throw new IllegalArgumentException( "Digits must not be negative." );
		int prefixLen = filePrefix.length();
		File[] files = new File( path ).listFiles();
		int lastIndex = 1;
		if (files != null) { // worked to get the list
			for( int i = 0; i < files.length; i++ ) {
				if( !files[i].isDirectory() && files[i].getName().length() >= prefixLen ) {
					String foundPrefix = files[i].getName().substring( 0, prefixLen );
					if( foundPrefix.equals( filePrefix ) ) {
						String foundNumber = files[i].getName().substring( prefixLen, prefixLen + digits );
						int number;
						try {
							number = Localization.getInstance().getIntegerConverter().parse( foundNumber ).intValue();
						} catch( ParseException ex ) {
							throw new java.lang.IllegalStateException( "File numbering wrong." );
						}
						if( number >= lastIndex )
							lastIndex = number + 1;
					}
				}
			}
		}
		try {
			return filePrefix + fillLeadingZeros( lastIndex, digits );
		} catch( IllegalArgumentException ex ) {
			throw new java.lang.IllegalStateException( "Too many files with number length " + digits );
		}
	}
	
	/**
	 * Creates a <code>String</code> containing an integer number with leading
	 * zeros.
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
}

