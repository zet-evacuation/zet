/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A general thread as input handler. A given input stream is used and listened
 * on. If input arrives on the stream, it is given out to the console.
 * @author Jan-Philipp Kappmeier
 */
public class InputHandler extends Thread {
	boolean verbose = true;
	InputStream input_;
	/** Indicates that the stream should be closed. */
	boolean close;

	InputHandler( InputStream input, String name ) {
		super( name );
		input_ = input;
	}

	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(input_);
			BufferedReader br = new BufferedReader(isr);
			String line=null;
			while ( (line = br.readLine()) != null )
					giveOut( getName(), line );
			//if( close )
				input_.close(); //br.close();
		} catch( Throwable t ) {
			t.printStackTrace();
		}
	}

	/**
	 * Gives out the given line with information that it occured on the specified
	 * stream.
	 * @param stream the stream where the output occured
	 * @param line the line that was read
	 */
	public void giveOut( String stream, String line ) {
		if( isVerbose() )
			System.out.println( getName() + "> " + line);
	}

	/**
	 * Decides wheather verbose mode is active, or not. If it is active each
	 * line read from the input is given out to the {@code System.out} stream.
	 * @return {@code true} if verbose output to default out is active, {@code false} otherwise
	 */
	public boolean isVerbose() {
		return verbose;
	}

	/**
	 * Determines if verbose mode is active, or not. If it is activated each line
	 * read from the input is given out to the {@code System.out} stream.
	 * @param verbose determines if verbose mode should be activated, or not
	 */
	public void setVerbose( boolean verbose ) {
		this.verbose = verbose;
	}
}
