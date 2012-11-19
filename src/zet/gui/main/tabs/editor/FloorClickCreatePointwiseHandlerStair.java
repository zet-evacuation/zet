/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zet.gui.main.tabs.editor;

import ds.z.ZControl;
import java.awt.Component;
import java.awt.Point;
import java.util.List;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FloorClickCreatePointwiseHandlerStair extends FloorClickCreatePointwiseHandler {
	private enum StairStates {
		Geometric,
		Lower,
		Upper;
	}
	
	private StairStates stairState = StairStates.Geometric;

	
	public FloorClickCreatePointwiseHandlerStair( EditStatus editStatus, ZControl control ) {
		super( editStatus, control );
		editStatus.setLastClick( null );
		System.out.println( "CREATIANG A NEW STAIR WITH LOWER AND UPPER SIDE" );
	}

	@Override
	public void mouseUp( Point p, List<Component> components ) {
		if( stairState == StairStates.Geometric ) {
			if( isCreationStarted() ) {
				// creation has started. If creation is not started anymore, we closed a polygon
				// and the upper and lower arcs have to be defined
				super.mouseUp( p, components );
				if( !isCreationStarted() )
					stairState = StairStates.Lower;
			} else
				super.mouseUp( p, components );
		} else {
			System.out.println( "Trying to add a lower side" );
			
		}
	}

	@Override
	public void rightClick() {
		if( stairState == StairStates.Geometric ) {
			if( isCreationStarted() ) {
				super.rightClick();
				if( !isCreationStarted() )
					stairState = StairStates.Lower;
			}
		}
	}
}
