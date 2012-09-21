/**
 * Templates.java
 * Created: 20.09.2012, 17:08:14
 */
package de.tu_berlin.math.coga.zet.template;

import java.util.ArrayList;
import java.util.Iterator;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Templates<T> implements Iterable<T> {
	ArrayList<T> templates;
	String name;

	public Templates( String name ) {
		templates = new ArrayList<>();
		this.name = name;
	}

	public void add( T template ) {
		templates.add( template );
	}
	
	public void remove( T template ) {
		templates.remove( template );
	}

	@Override
	public Iterator<T> iterator() {
		return templates.iterator();
	}

	public String getName() {
		return name;
	}
}
