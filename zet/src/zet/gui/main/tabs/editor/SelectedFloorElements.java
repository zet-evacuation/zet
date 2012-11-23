/**
 * SelectedFloorElements.java
 * Created: 23.11.2012, 12:58:17
 */
package zet.gui.main.tabs.editor;

import de.tu_berlin.math.coga.common.util.SelectedElements;
import ds.z.Edge;
import zet.gui.main.tabs.base.JPolygon;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SelectedFloorElements extends SelectedElements<JPolygon> {
	Edge selectedEdge;
	JPolygon selectedElementPolygon;

	void selectEdge( JPolygon toSelect, Edge edge ) {
		clear();
		selectedEdge = edge;
		selectedElementPolygon = toSelect;
		toSelect.setSelectedEdge( edge );
		super.setChanged();
		super.notifyObservers( toSelect );
	}

	public Edge getSelectedEdge() {
		return selectedEdge;
	}

	@Override
	public void select( JPolygon toSelect ) {
		clearEdgeInternal();
		super.select( toSelect );
	}

	@Override
	public void clear() {
		clearEdgeInternal();
		super.clear();
	}

	private void clearEdgeInternal() {
		if( selectedElementPolygon != null )
			selectedElementPolygon.setSelectedEdge( null );
		selectedElementPolygon = null;
		selectedEdge = null;

	}
}
