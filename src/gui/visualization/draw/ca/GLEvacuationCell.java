package gui.visualization.draw.ca;

import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.ca.GLCellControl;


public class GLEvacuationCell extends GLCell {
	public GLEvacuationCell (GLCellControl control ) {
		super(control, VisualizationOptionManager.getEvacuationCellFloorColor()  );
	}
	
   @Override
    protected void updateFloorColor(){
        if(VisualizationOptionManager.getAlwaysDisplayCellType()){
            color = getDefaultColor();
        } else {
            super.updateFloorColor();
        }
    }
}

