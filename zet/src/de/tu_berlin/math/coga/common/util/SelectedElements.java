/**
 * SelectedElements.java
 * Created: Nov 12, 2012, 5:28:39 PM
 */
package de.tu_berlin.math.coga.common.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;


/**
 *
 * @param <E> the type of objects that can be selected
 * @author Jan-Philipp Kappmeier
 */
public class SelectedElements<E extends Selectable> extends Observable {
	private List<E> selected = new LinkedList<>();

	public void clear() {
		if( selected.isEmpty() )
			return;
		clearInternal();
		super.setChanged();
		super.notifyObservers();
	}

	private void clearInternal() {
		for( E p : selected )
			p.setSelected( false );
		selected.clear();
	}

	public void select( E toSelect ) {
		clearInternal();
		addInternal( toSelect );
		super.setChanged();
		super.notifyObservers( toSelect );
	}

	public List<E> getSelectedList() {
		return Collections.unmodifiableList( selected );
	}

	public E getSelected() {
		return selected.isEmpty() ? null : selected.get( 0 );
	}

	public void add( E element ) {
		addInternal( element );
		super.setChanged();
		super.notifyObservers();
	}

	public void add( List<E> elements ) {
		for( E element : elements )
			addInternal( element );
		super.setChanged();
		super.notifyObservers();
	}

	private void addInternal( E element ) {
		element.setSelected( true );
		selected.add( element );
	}
}
