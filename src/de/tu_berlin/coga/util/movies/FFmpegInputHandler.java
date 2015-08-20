/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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

package de.tu_berlin.coga.util.movies;

import java.io.InputStream;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FFmpegInputHandler extends InputHandler {

	FFmpegInputHandler( InputStream input, String name ) {
		super( input, name );
	}

	@Override
	public void giveOut( String stream, String line ) {
		// Give out the first line with information on the software
		if( line.startsWith( "FFmpeg" ) )
			System.out.println( "Starting " + line );
		else if( line.startsWith( "frame=" ) )
			System.out.println( "Converting frame " + line.substring(6, line.indexOf( " fps") ).trim() );
		else if( line.startsWith( "video" ) )
			System.out.println( line );
		else 
			if( isVerbose() )
				System.out.println( "------------------" + line );
	}
}
