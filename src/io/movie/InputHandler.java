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

/**
 * Class InputHandler
 * Erstellt 10.11.2008, 03:07:40
 */
package io.movie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
class InputHandler extends Thread {
	boolean verbose = true;
	InputStream input_;

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
			while ( verbose && (line = br.readLine()) != null )
					System.out.println( getName() + ">" + line);
		} catch( Throwable t ) {
			t.printStackTrace();
		}
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose( boolean verbose ) {
		this.verbose = verbose;
	}

	public void close() {
		try {
			input_.close();
		} catch( IOException ex ) {
			Logger.getLogger( InputHandler.class.getName() ).log( Level.SEVERE, null, ex );
		}
	}
}
