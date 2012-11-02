/**
 * FloorClickHandler.java
 * Created: Nov 2, 2012, 4:37:22 PM
 */
package zet.gui.main.tabs.editor;

import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import zet.gui.main.tabs.base.JPolygon;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class FloorClickHandler {
	private EditStatus editStatus;
	protected List<JPolygon> elementsUnderMouse;
	boolean marking = false;

	boolean mouseDown = false;

	protected FloorClickHandler( EditStatus editStatus ) {
		this.editStatus = editStatus;
		elementsUnderMouse = new LinkedList<>();
	}

	public void mouseDown( Point p, List<JPolygon> elements ) {
		editStatus.setLastClick( p );
		this.elementsUnderMouse = Collections.unmodifiableList( elements );
		mouseDown = true;
	}

	public void mouseUp() {
		mouseDown = false;
	}

	public abstract void mouseMove( Point p );

	public EditStatus getEditStatus() {
		return editStatus;
	}

}
