/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zet.gui.main.tabs.editor;

import ds.z.Edge;
import ds.z.PlanPoint;
import ds.z.PlanPolygon;
import ds.z.StairArea;
import ds.z.ZControl;
import ds.z.exception.StairAreaBoundaryException;
import java.awt.Component;
import java.awt.Point;
import java.util.List;
import javax.swing.SwingUtilities;
import zet.gui.main.tabs.base.JPolygon;

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
			JPolygon stair = getEditStatus().getCurrentEditing();
			if( !(stair.getPlanPolygon() instanceof StairArea) )
				throw new AssertionError( "No stair, but instead it is of type " + stair.getPlanPolygon().getClass() );

			Object obj = stair.findClickTargetAt( SwingUtilities.convertPoint( getEditStatus().getControlled(), p, stair ) );
			if( obj instanceof Edge ) {
				Edge e = (Edge)obj;
				//System.out.println( "FINALLY AN EDGE WAS HIT" );
				StairArea sa = (StairArea)stair.getPlanPolygon();
				if( stairState == StairStates.Lower ) {
					sa.setLowerLevel( e.getSource(), e.getTarget() );
					stairState = StairStates.Upper;
				} else if( stairState == StairStates.Upper ) {
					try {
						sa.setUpperLevel( e.getSource(), e.getTarget() );
						stairState = StairStates.Geometric;
					} catch( StairAreaBoundaryException ex ) {
						// ignore exception and mouse click. do nothing and wait for correct one.
						// TODO send an info to the user interface
					}
				} else
					throw new AssertionError( "In creation mode, illegally to define upper or lower edges!" );
			}
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
