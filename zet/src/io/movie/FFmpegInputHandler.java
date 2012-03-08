/**
 * FFmpegInputHandler.java
 * input:
 * output:
 *
 * method:
 *
 * Created: Apr 21, 2010,6:15:18 PM
 */
package io.movie;

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
