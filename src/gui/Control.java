/*
 * Control.java
 * Created 16.07.2010, 12:18:41
 */

package gui;

import gui.editor.AreaVisibility;
import gui.editor.CoordinateTools;
import gui.editor.EditMode;
import gui.toolbar.JEditToolbar;
import java.awt.Rectangle;

/**
 * This class receives commands and gui changes from elements like toolbars,
 * menus etc. and delegates them to other classes.
 * @author Jan-Philipp Kappmeier
 */
public class Control {

	/** The editor */
	public JEditor editor;
	/** The edit toolbar */
	JEditToolbar editToolBar;

	/**
	 * Creates a new instance of <code>Control</code>.
	 */
	public Control() {

	}

	/**
	 * Sets the Zoom factor on the currently shown shown JFloor.
	 * @param zoomFactor the zoom factor
	 */
	public void setZoomFactor( double zoomFactor ) {
		double zoomChange = zoomFactor / CoordinateTools.getZoomFactor();
		Rectangle oldView = new Rectangle( editor.getEditView().getLeftPanel().getViewport().getViewRect() );
		oldView.x *= zoomChange;
		oldView.y *= zoomChange;
		if( zoomChange > 1 ) {
			// If we are zooming in, then we have to move our window to the "middle"
			// of what the user previously saw. Right now we are in the upper left edge
			// of what he previously saw, and now we are doing this "move"
			int widthIncrement = (int)(oldView.width * zoomChange) - oldView.width;
			int heightIncrement = (int)(oldView.height * zoomChange) - oldView.height;

			oldView.x += widthIncrement / 2;
			oldView.y += heightIncrement / 2;
		}

		// TODO give direct access to the left edit panel
		//CoordinateTools.setZoomFactor( zoomFactor );
		editor.getEditView().getLeftPanel().setZoomFactor( zoomFactor );
		editor.getEditView().getFloor().getPlanImage().update();
		editor.getEditView().updateFloorView();
//		if( worker != null ) {
//			 caView.getLeftPanel().setZoomFactor( zoomFactor );
//			caView.updateFloorView();
//		}

		//if( editToolBar != null )
		editToolBar.setZoomFactorText( zoomFactor );

		//Redisplay the same portion of the Floor as before (move scrollbars)
		editor.getEditView().getLeftPanel().getViewport().setViewPosition( oldView.getLocation() );
	}

		/**
	 * Displays a specified type of areas. The selection parameter of the
	 * menu entry is set correct, too.
	 * @param areaType the are type
	 */
	public void showArea( AreaVisibility areaType ) {
		switch( areaType ) {
			case Delay:
				editor.mnuDelayArea.setSelected( true );
				break;
			case Stair:
				editor.mnuStairArea.setSelected( true );
				break;
			case Evacuation:
				editor.mnuEvacuationArea.setSelected( true );
				break;
			case Inaccessible:
				editor.mnuInaccessibleArea.setSelected( true );
				break;
			case Save:
				editor.mnuSaveArea.setSelected( true );
				break;
			case Assignment:
				editor.mnuAssignmentArea.setSelected( true );
				break;
			case Teleport:
				// TODO
				break;
			default:
				JEditor.showErrorMessage( "Error", "Dieser Area-Typ wird nicht unterstützt." );
		}
		editor.updateAreaVisiblity();
	}

	public void setEditMode( EditMode mode ) {
			if( editor.getEditView() != null ) {
				editor.getEditView().setEditMode( mode );

				editToolBar.setEditSelectionSelected( false );
				editToolBar.setEditPointwiseSelected( mode.getType() == EditMode.Type.CreationPointwise );
				editToolBar.setEditRectangledSelected( mode.getType() == EditMode.Type.CreationRectangled );
			}
	}

	/**
	 * Exits the program.
	 */
	public void exit() {
		System.exit( 0 );
	}

	public void setEditToolbar( JEditToolbar aThis ) {
		editToolBar = aThis;
	}
}
