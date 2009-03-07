package gui.visualization.draw.ca;

import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.ca.GLCellControl;

public class GLSaveCell extends GLCell {

	public GLSaveCell( GLCellControl control ) {
		super( control, VisualizationOptionManager.getSaveCellFloorColor() );
	}

	@Override
	protected void updateFloorColor() {
		if( VisualizationOptionManager.getAlwaysDisplayCellType() )
			color = getDefaultColor();
		else
			super.updateFloorColor();
	}
}

