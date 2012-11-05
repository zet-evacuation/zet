/**
 * FloorClickHandler.java
 * Created: Nov 2, 2012, 4:37:22 PM
 */
package zet.gui.main.tabs.editor;

import ds.z.ZControl;
import java.awt.Component;
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
	private final ZControl zcontrol;

	boolean mouseDown = false;

	protected FloorClickHandler( EditStatus editStatus, ZControl control ) {
		this.editStatus = editStatus;
		this.zcontrol = control;
		elementsUnderMouse = new LinkedList<>();
	}

	public void mouseDown( Point p, List<JPolygon> elements ) {
		editStatus.setLastClick( p );
		this.elementsUnderMouse = Collections.unmodifiableList( elements );
		mouseDown = true;
	}

	public void mouseUp( Point p, List<Component> components ) {
		mouseDown = false;
	}

	public void doubleClick( Point p, List<JPolygon> elements ) {

	}

	public abstract void mouseMove( Point p );

	public EditStatus getEditStatus() {
		return editStatus;
	}

	protected ZControl getZControl() {
		return zcontrol;
	}

}
