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
 * DebugStream.java
 * Created 06.03.2009, 19:48:32
 */
package util;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * The class <code>DebugStream</code> mirrors outputs on an {@link OutputStream}
 * to another {@link PrintStream}. This can be used to write both, file output
 * and command line output at the same time using the default
 * <code>System.out</code>
 * @author Jan-Philipp Kappmeier
 */
public class DebugStream extends PrintStream {
	/** The output stream that is used as a mirror. */
	PrintStream oldOutputStream = null;

	/**
	 * Creates a new instance of <code>DebugStream</code>.
	 * @param out the outputstream that is used
	 * @param oldOutputStream the printstream to which the outputs are sended, too
	 */
	public DebugStream( OutputStream out, PrintStream oldOutputStream ) {
		super( out );
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
		oldOutputStream.print( s);

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
	public void println( boolean x ) {
		super.println( x );
		oldOutputStream.println( "" );
	}

	/** {@inheritDoc} */
	@Override
	public void println( char x ) {
		super.println( x );
		oldOutputStream.println( "" );
	}

	/** {@inheritDoc} */
	@Override
	public void println( int x ) {
		super.println( x );
		oldOutputStream.println( "" );
	}

	/** {@inheritDoc} */
	@Override
	public void println( long x ) {
		super.println( x );
		oldOutputStream.println( "" );
	}

	/** {@inheritDoc} */
	@Override
	public void println( float x ) {
		super.println( x );
		oldOutputStream.println( "" );
	}

	/** {@inheritDoc} */
	@Override
	public void println( double x ) {
		super.println( x );
		oldOutputStream.println( "" );
	}

	/** {@inheritDoc} */
	@Override
	public void println( char x[] ) {
		super.println( x );
		oldOutputStream.println( "" );
	}

	/** {@inheritDoc} */
	@Override
	public void println( String x ) {
		super.println( x );
		oldOutputStream.println( "" );
	}

	/** {@inheritDoc} */
	@Override
	public void println( Object x ) {
		super.println( x );
		oldOutputStream.println( "" );
	}
}
