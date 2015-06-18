
package zet.gui.main.tabs.editor;

import org.zetool.common.util.SelectedElements;
import de.zet_evakuierung.model.PlanEdge;
import zet.gui.main.tabs.base.JPolygon;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SelectedFloorElements extends SelectedElements<JPolygon> {
	PlanEdge selectedEdge;
	JPolygon selectedElementPolygon;

	void selectEdge( JPolygon toSelect, PlanEdge edge ) {
		clear();
		selectedEdge = edge;
		selectedElementPolygon = toSelect;
		toSelect.setSelectedEdge( edge );
		super.setChanged();
		super.notifyObservers( toSelect );
	}

	public PlanEdge getSelectedEdge() {
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

  public JPolygon getSelectedElementPolygon() {
    return selectedElementPolygon;
  }
}
