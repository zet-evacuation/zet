/**
 * Class GLStairCell
 * Erstellt 19.05.2008, 10:10:18
 */
package gui.visualization.draw.ca;

import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.ca.GLCellControl;

/**
 * This class represents the graphical representation of a {@link StairCell}.
 * The only difference between this type and the general cell type is that it
 * can have the color of stair cells.
 * @author Jan-Philipp Kappmeier
 */
public class GLStairCell extends GLCell {

	/**
	 * Creates a new instance of a stair cell.
	 * @param control
	 */
	public GLStairCell( GLCellControl control ) {
		super( control, VisualizationOptionManager.getStairCellFloorColor() );
	}

	/**
	 * Overriden version of <code>updateFloorColor</code>. It can switch between
	 * showing the natural stair color of the color of a given potential.
	 */
	@Override
	protected void updateFloorColor() {
		if( VisualizationOptionManager.getAlwaysDisplayCellType() )
			color = getDefaultColor();
		else
			super.updateFloorColor();
	}
}
