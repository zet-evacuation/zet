package gui.visualization.draw.ca;

import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.ca.GLCellControl;

public class GLDelayCell extends GLCell {

	public GLDelayCell( GLCellControl control ) {
		super( control, VisualizationOptionManager.getDelayCellFloorColor() );
	}

	@Override
	protected void updateFloorColor() {
		if( VisualizationOptionManager.getAlwaysDisplayCellType() )
			color = getDefaultColor();
		else
			super.updateFloorColor();
	}
}

