/**
 * EditStatus.java
 * Created: Nov 1, 2012, 6:13:49 PM
 */
package zet.gui.main.tabs.editor;

import ds.z.ZControl;
import gui.editor.CoordinateTools;
import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import zet.gui.main.JZetWindow;
import zet.gui.main.tabs.base.JPolygon;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EditStatus {
	JFloor controlled;
	ZControl zcontrol;
	ZetObjectTypes nextZetObject = ZetObjectTypes.Room;
	int rasterSnap = 400;

	EditMode editMode = EditMode.Selection;
	/** Decides, whether the creation of an zet object has been started and not finished yet. */
	boolean creationStarted;
	/** Whether we are painting new mode in raster mode. */
	private boolean rasterizedPaintMode = true;
	/** This field stored where the new PlanPoint would be inserted in raster paint mode if the user clicked into the {@link JFloor}. */
	private Point highlightedPosition;

	FloorClickHandler current;
	private Point lastClick;

	List<JPolygon> selectedPolygons = new LinkedList<>();
	private boolean mouseSelecting;

	public EditStatus( ZControl zcontrol ) {
		this.zcontrol = zcontrol;
	}

	public EditMode getEditMode() {
		return editMode;
	}

	public void controlFloor( JFloor floor ) {
		this.controlled = floor;
	}

	public JFloor getControlled() {
		return controlled;
	}

	public FloorClickHandler getCurrentHandler() {
		return current == null ? current = new FloorClickSelectionHandler( this, zcontrol ) : current;
	}

	/** Efficiently De-selects all selected polygons on the screen
	 */
	void clearSelection() {
		for( JPolygon p : selectedPolygons )
			p.setSelected( false );
		selectedPolygons.clear();
		//if( selectedElementPolygon != null ) {
		//	selectedEdge = null;
		//	selectedPoint = null;
		//	selectedElementPolygon.setSelected( false );
		//	selectedElementPolygon = null;
		//}
		//fireActionEvent();
	}

	void selectPolygon( JPolygon toSelect ) {
		toSelect.setSelected( true );
		selectedPolygons.add( toSelect );
	}

	void setPointerPosition( Point point ) {
		Point real;
//		if( rasterizedPaintMode ) {
//			highlightedPosition = getNextRasterPoint( point );
//			real = CoordinateTools.translateToModel( highlightedPosition );
//		} else
		real = CoordinateTools.translateToModel( point );
		highlightedPosition = point;
		JZetWindow.sendMouse( real );
	}

	List<JPolygon> getSelectedPolygons() {
		return Collections.unmodifiableList( selectedPolygons );
	}

	/**
	 * Returns a raster point that is closest to the given point.
	 * @param p the point
	 * @return a raster point that is closest to the given point
	 */
	public Point getNextRasterPoint( Point p ) {
		int rasterWidth = CoordinateTools.translateToScreen( rasterSnap );
		return new Point( (int)Math.round( p.getX() / (double)rasterWidth ) * rasterWidth, (int)Math.round( p.getY() / (double)rasterWidth ) * rasterWidth );
	}

	public Point getHighlightedPosition() {
		return highlightedPosition;
	}

	boolean isRasterizedPaintMode() {
		return rasterizedPaintMode;
	}

	void setMouseSelecting( boolean b ) {
		mouseSelecting = b;
	}

	public boolean isMouseSelecting() {
		return mouseSelecting;
	}

	public Point getLastClick() {
		return lastClick;
	}

	void setLastClick( Point p ) {
		this.lastClick = p;
	}



}
