/**
 * EditStatus.java
 * Created: Nov 1, 2012, 6:13:49 PM
 */
package zet.gui.main.tabs.editor;

import de.tu_berlin.coga.zet.model.PlanEdge;
import de.tu_berlin.coga.zet.model.Floor;
import de.tu_berlin.coga.zet.model.ZControl;
import gui.editor.CoordinateTools;
import java.awt.Point;
import java.util.List;
import java.util.Objects;
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
	JPolygon currentEditing;
	boolean popupsEnabled = true;

	final SelectedFloorElements selection;

	EditMode editMode = EditMode.Selection;
	/** Decides, whether the creation of an zet object has been started and not finished yet. */
	boolean creationStarted;
	/** Whether we are painting new mode in raster mode. */
	private boolean rasterizedPaintMode = true;
	/** This field stored where the new PlanPoint would be inserted in raster paint mode if the user clicked into the {@link JFloor}. */
	private Point highlightedPosition;

	FloorClickHandler current;
	private Point lastClick;

	private boolean mouseSelecting;
	private ZetObjectTypes zetObjectType;
	private Floor floor;

	public EditStatus( ZControl zcontrol, SelectedFloorElements selection ) {
		this.zcontrol = Objects.requireNonNull( zcontrol );
		this.selection = Objects.requireNonNull( selection );
	}

	public EditMode getEditMode() {
		return editMode;
	}

	public void setEditMode( EditMode editMode ) {
		this.editMode = editMode;
		switch( editMode ) {
			case Selection:
				current = new FloorClickSelectionHandler( this, zcontrol );
				break;
			case CreationRectangle:
				current = zetObjectType == ZetObjectTypes.Stair ? new FloorClickCreateRectangleHandler( this, zcontrol ) : new FloorClickCreateRectangleHandler( this, zcontrol );
				break;
			case CreationPointWise:
				current = zetObjectType == ZetObjectTypes.Stair ? new FloorClickCreatePointwiseHandlerStair( this, zcontrol ) : new FloorClickCreatePointwiseHandler( this, zcontrol );
				break;
			default:
				throw new AssertionError( "Unsupported edit mode: " + editMode );
		}
	}

	public void controlFloor( JFloor jfloor, Floor floor ) {
		this.controlled = Objects.requireNonNull( jfloor );
		this.floor =  Objects.requireNonNull( floor );
	}

	public JFloor getControlled() {
		return controlled;
	}

	public Floor getView() {
		return floor;
	}

	public FloorClickHandler getCurrentHandler() {
		return current == null ? current = new FloorClickSelectionHandler( this, zcontrol ) : current;
	}

	/** Efficiently De-selects all select polygons on the screen
	 */
	void clearSelection() {
		selection.clear();
		currentEditing = null;
	}

	void selectPolygon( JPolygon toSelect ) {
		selection.select( toSelect );
		currentEditing = selection.getSelected();
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
		return selection.getSelectedList();
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

	public ZetObjectTypes getZetObjectType() {
		return zetObjectType;
	}

	public void setZetObjectType( ZetObjectTypes zetObjectType ) {
		this.zetObjectType = Objects.requireNonNull( zetObjectType );
	}

	void addPolygon( JPolygon jPolygon ) {
		selection.add( jPolygon );
	}

	void addPolygon( List<JPolygon> toAdd ) {
		selection.add( toAdd );
	}

	public void setCurrentEditing( JPolygon currentEditing ) {
		System.out.println( "Set current editing was set." );
		if( selection.getSelected() != null )
			if( selection.getSelected() != currentEditing ) // if we want to set current editing the selection, ignore
				throw new IllegalStateException( "A polygon can only be set current editing, if no polygon is selected." );
		this.currentEditing = currentEditing;
	}

	public JPolygon getCurrentEditing() {
		return currentEditing;
	}

	void setPopupEnabled( boolean value ) {
		popupsEnabled = value;
	}

	public boolean isPopupEnabled() {
		return popupsEnabled;
	}

	/**
	 * Selectes a single edge in a given polygon.
	 * @param toSelect
	 * @param edge
	 */
	void selectEdge( JPolygon toSelect, PlanEdge edge ) {
		selection.selectEdge( toSelect, edge );
	}
}
