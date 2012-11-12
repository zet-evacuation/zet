/**
 * SelectedElements.java
 * Created: Nov 12, 2012, 5:28:39 PM
 */
package zet.gui.main.tabs.editor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import zet.gui.main.tabs.base.JPolygon;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SelectedElements extends Observable {
	List<JPolygon> selectedPolygons = new LinkedList<>();

	void clear() {
		if( selectedPolygons.isEmpty() )
			return;
		clearInternal();
		super.setChanged();
		super.notifyObservers();
	}

	private void clearInternal() {
		for( JPolygon p : selectedPolygons )
			p.setSelected( false );
		selectedPolygons.clear();
	}

	void select( JPolygon toSelect ) {
		clearInternal();
		addInternal( toSelect );
		super.setChanged();
		super.notifyObservers( toSelect );
	}

	List<JPolygon> getSelectedList() {
		return Collections.unmodifiableList( selectedPolygons );
	}

	public JPolygon getSelected() {
		if( selectedPolygons.isEmpty() )
			return null;
		else
			return selectedPolygons.get( 0 );
	}

	void add( JPolygon jPolygon ) {
		addInternal( jPolygon );
		super.setChanged();
		super.notifyObservers();
	}

	void add( List<JPolygon> jPolygons ) {
		for( JPolygon poloy : jPolygons )
			addInternal( poloy );
		super.setChanged();
		super.notifyObservers();
	}

	private void addInternal( JPolygon jPolygon ) {
		jPolygon.setSelected( true );
		selectedPolygons.add( jPolygon );
	}
}
