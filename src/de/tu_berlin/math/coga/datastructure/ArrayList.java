/**
 * ArrayList.java
 * Created: Sep 17, 2010,1:52:33 PM
 */
package de.tu_berlin.math.coga.datastructure;

import java.util.Collection;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ArrayList<E> extends java.util.ArrayList<E> {
	public ArrayList( Collection<? extends E> clctn ) {
		super( clctn );
	}

	public ArrayList() {
		super();
	}

	public ArrayList( int i ) {
		super( i );
	}

	public ArrayList( E[] e ) {
		this( e.length );
		for( int i = 0; i < e.length; ++i )
			add( e[i] );
	}
}
