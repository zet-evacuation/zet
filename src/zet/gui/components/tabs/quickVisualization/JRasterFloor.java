/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/*
 * JRasterFloor.java
 * Created on 27.01.2008, 18:54:52
 */

package zet.gui.components.tabs.quickVisualization;

import algo.ca.PotentialController;
import algo.ca.SPPotentialController;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterSquare;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARoomRaster;
import de.tu_berlin.math.coga.common.util.Direction;
import ds.PropertyContainer;
import ds.z.Floor;
import ds.z.Room;
import ds.ca.Cell;
import ds.ca.CellularAutomaton;
import ds.ca.DynamicPotential;
import ds.ca.ExitCell;
import ds.ca.PotentialManager;
import ds.ca.SaveCell;
import ds.ca.StairCell;
import ds.ca.StaticPotential;
import zet.gui.components.tabs.base.AbstractFloor;
import zet.gui.components.tabs.base.JPolygon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.LinkedList;
import statistic.ca.CAStatistic;

/**
 * Represents a rastered floor, all rooms have to be squares of the raster size
 * of a cellular automaton.
 * @author Jan-Philipp Kappmeier
 */
public class JRasterFloor extends AbstractFloor /*implements ds.z.event.ChangeListener*/ {
	// Main objects
	/** The displayed floor. */
	private Floor myFloor;
	private CellularAutomaton ca;
	private ZToCAMapping mapping;
	private ZToCARasterContainer container	;
	private CAStatistic cas;
	
	public void setCAStatistic(CAStatistic cas){
		this.cas = cas;
	}

	public JRasterFloor() {
		setLayout( null );
		setBackground( Color.black );
	}

	public void setCa( CellularAutomaton ca ) {
		this.ca = ca;
	}

	public void setContainer( ZToCARasterContainer container ) {
		this.container = container;
	}

	public void setMapping( ZToCAMapping mapping ) {
		this.mapping = mapping;
	}

	/** @return The floor that is currently displayed by this JRasterFloor. */
	public Floor getFloor () {
		return myFloor;
	}
	
	/**
	 * Opens a floor and adds the rooms as components to the floor.
	 * @param floor the displayed floor
	 * @param mapping the mapping to the given cellular automaton
	 * @param container the container containing the raster squares
	 */
	public void displayFloor( Floor floor ) {
		boolean showPotentialValue = PropertyContainer.getInstance().getAsBoolean( "editor.options.cavis.staticPotential" );
		boolean showDynamicPotential = PropertyContainer.getInstance().getAsBoolean( "editor.options.cavis.dynamicPotential" );
		boolean showCellUtilization = false;
		showPotentialValue = true;
		showDynamicPotential = false;
		
		DynamicPotential dp = null;

		if( myFloor != null ) {
			for( Component c : getComponents() )
				if( c instanceof JPolygon )
					( (JPolygon)c ).displayPolygon( null );
			removeAll();
		}

		myFloor = floor;
		System.out.println( "Switch to floor " + floor.getName() );

		updateOffsets( floor );
		
		// TODO: Provide better implementation - Do not recreate everything each time
		//CellularAutomaton ca = ZToCAConverter.getInstance().getLatestCellularAutomaton();
		PotentialManager pm = ca.getPotentialManager();
		PotentialController pc = new SPPotentialController( ca );
		StaticPotential sp = null;
		sp = pc.mergePotentials( new ArrayList<StaticPotential>( pm.getStaticPotentials() ) );
		dp = pm.getDynamicPotential();

		for( Room r : floor.getRooms() ) {
			if( container != null ) {
				ZToCARoomRaster roomRaster = container.getRasteredRoom( r );
				LinkedList<ZToCARasterSquare> squares = roomRaster.getAccessibleSquares();
				for( ZToCARasterSquare square : squares ) {
					// Color depending of the cell type
					Cell cell = mapping.get( square );
					JCellPolygon poly = null;
					if( cell instanceof ExitCell )
						poly = new JCellPolygon( cell, this, Color.white, Color.black, ca );
					else if( cell instanceof StairCell )
						poly = new JCellPolygon( cell, this, Color.red, Color.black, ca );
					else if( cell instanceof SaveCell )
						poly = new JCellPolygon( cell, this, Color.yellow, Color.black, ca );
					else {
						Color c = Color.lightGray;
						if( showPotentialValue ) {
							if( sp != null ) {
								int pot = sp.getPotential( cell );
								poly = new JPotentialCell( cell, this, Color.black, pot, sp.getMaxPotential(), ca );	// border color white
							} else
								poly = new JCellPolygon( cell, this, Color.lightGray, Color.black, ca );
						} else if( showDynamicPotential ) {
							if( dp != null )
								poly = new JDynamicPotentialCell( cell, this, Color.black, dp.getPotential( cell ), dp.getMaxPotential(), ca );
							else
								poly = new JCellPolygon( cell, this, Color.lightGray, Color.black, ca );
						} else if( showCellUtilization ) {
								if(cas != null) poly = new JDynamicPotentialCell( cell, this, Color.black, cas.getCellStatistic().getCellUtilization(cell,ca.getTimeStep()), dp.getMaxPotential(), ca );
						} else
							poly = new JCellPolygon( cell, this, Color.lightGray, Color.black, ca );
					}
					if( !cell.isPassable( Direction.Left) )
						poly.addWall( Direction.Left );
					if( !cell.isPassable( Direction.Right) )
						poly.addWall( Direction.Right );
					if( !cell.isPassable( Direction.Top) )
						poly.addWall( Direction.Top );
					if( !cell.isPassable( Direction.Down ) )
						poly.addWall( Direction.Down );
					//nicht zeichnen
					add( poly );
					poly.displayPolygon( square.getSquare() );
				}
			}
		}

		revalidate();
		repaint();
	}

	public void update() {
		for( Component component : getComponents() ) {
			((JCellPolygon)component).update();
		}
		repaint();
	}

	/**
	 * Paints the panel in the graphics object. It is
	 * possible to pass any graphics object, but it is particularly
	 * used for painting this panel. This can be used to save as bitmap or jpeg
	 * @param g The graphics object
	 */
	@Override
	public void paintComponent( Graphics g ) {
		super.setBackground( Color.black);
		super.paintComponent( g );
	}
}