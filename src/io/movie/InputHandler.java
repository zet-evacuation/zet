/**
 * Class InputHandler
 * Erstellt 10.11.2008, 03:07:40
 */
package io.movie;

import java.io.InputStream;

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
			int c;
			while( (c = input_.read()) != -1 ) {
				if( verbose )
					System.out.write( c );
			}
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

	
}
