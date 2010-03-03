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
 * You should have received messageType copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/*
 * DebugStream.java
 * Created 06.11.2009, 14:18:50
 */
package de.tu_berlin.math.coga.common.debug;

import event.MessageEvent.MessageType;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * A {@link DebugStream} that has verbose output. That means, any messages are
 * printed on the newly {@link OutputStream} and olso on an arbitrary different
 * {@link PrintStream}.
 * @author Jan-Philipp Kappmeier
 */
public class DebugStreamVerbose extends DebugStream {

	/** The output stream that is used as messageType mirror. */
	PrintStream oldOutputStream = null;

	/**
	 * Creates messageType new instance of <code>DebugStream</code>.
	 * @param out the outputstream that is used
	 * @param oldOutputStream the printstream to which the outputs are sended, too
	 */
	public DebugStreamVerbose( OutputStream out, PrintStream oldOutputStream ) {
		super( out );
		this.oldOutputStream = oldOutputStream;
	}

	/**
	 * Creates messageType new instance of <code>DebugStream</code>.
	 * @param out the outputstream that is used
	 * @param oldOutputStream the printstream to which the outputs are sended, too
	 * @param m the type of messages handeled by this debug stream.
	 */
	public DebugStreamVerbose( OutputStream out, PrintStream oldOutputStream, MessageType m ) {
		super( out, m );
		this.oldOutputStream = oldOutputStream;
	}

	/** {@inheritDoc} */
	@Override
	public void print( boolean b ) {
		super.print( b );
		oldOutputStream.print( b );
	}

	/** {@inheritDoc} */
	@Override
	public void print( char c ) {
		super.print( c );
		oldOutputStream.print( c );
	}

	/** {@inheritDoc} */
	@Override
	public void print( int i ) {
		super.print( i );
		oldOutputStream.print( i );
	}

	/** {@inheritDoc} */
	@Override
	public void print( long l ) {
		super.print( l );
		oldOutputStream.print( l );
	}

	/** {@inheritDoc} */
	@Override
	public void print( float f ) {
		super.print( f );
		oldOutputStream.print( f );
	}

	/** {@inheritDoc} */
	@Override
	public void print( double d ) {
		super.print( d );
		oldOutputStream.print( d );
	}

	/** {@inheritDoc} */
	@Override
	public void print( char s[] ) {
		super.print( s );
		oldOutputStream.print( s );

	}

	/** {@inheritDoc} */
	@Override
	public void print( String s ) {
		super.print( s );
		oldOutputStream.print( s );
	}

	/** {@inheritDoc} */
	@Override
	public void print( Object obj ) {
		super.print( obj );
		oldOutputStream.print( obj );
	}

	/** {@inheritDoc} */
	@Override
	public void println() {
		super.println();
		oldOutputStream.println( "" );
	}

	/** {@inheritDoc} */
	@Override
	public void println( boolean b ) {
		super.println( b );
		oldOutputStream.println();
	}

	/** {@inheritDoc} */
	@Override
	public void println( char c ) {
		super.println( c );
		oldOutputStream.println();
	}

	/** {@inheritDoc} */
	@Override
	public void println( int i ) {
		super.println( i );
		oldOutputStream.println();
	}

	/** {@inheritDoc} */
	@Override
	public void println( long l ) {
		super.println( l );
		oldOutputStream.println();
	}

	/** {@inheritDoc} */
	@Override
	public void println( float f ) {
		super.println( f );
		oldOutputStream.println();
	}

	/** {@inheritDoc} */
	@Override
	public void println( double d ) {
		super.println( d );
		oldOutputStream.println();
	}

	/** {@inheritDoc} */
	@Override
	public void println( char c[] ) {
		super.println( c );
		oldOutputStream.println();
	}

	/** {@inheritDoc} */
	@Override
	public void println( String s ) {
		super.println( s );
		oldOutputStream.println();
	}

	/** {@inheritDoc} */
	@Override
	public void println( Object obj ) {
		super.println( obj );
		oldOutputStream.println();
	}
}
