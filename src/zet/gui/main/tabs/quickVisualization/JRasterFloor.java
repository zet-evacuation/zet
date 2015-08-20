/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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

package zet.gui.main.tabs.quickVisualization;

import algo.ca.PotentialController;
import algo.ca.SPPotentialController;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterSquare;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARoomRaster;
import org.zetool.common.util.Direction8;
import ds.PropertyContainer;
import de.zet_evakuierung.model.Floor;
import de.zet_evakuierung.model.Room;
import ds.ca.evac.EvacCell;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.ca.evac.DynamicPotential;
import ds.ca.evac.ExitCell;
import ds.ca.evac.PotentialManager;
import ds.ca.evac.SaveCell;
import ds.ca.evac.StairCell;
import ds.ca.evac.StaticPotential;
import de.zet_evakuierung.model.ZControl;
import zet.gui.main.tabs.base.AbstractFloor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.LinkedList;
import statistic.ca.CAStatistic;
import zet.gui.main.tabs.editor.control.FloorViewModel;

/**
 * Represents a rastered floor, all rooms have to be squares of the raster size
 * of a cellular automaton.
 * @author Jan-Philipp Kappmeier
 */
public class JRasterFloor extends AbstractFloor {
	// Main objects
	/** The displayed floor. */
	private Floor myFloor;
	private EvacuationCellularAutomaton ca;
	private ZToCAMapping mapping;
	private ZToCARasterContainer container;
	private CAStatistic cas;
	private ZControl zcontrol;
        

	public void setCAStatistic(CAStatistic cas){
		this.cas = cas;
	}

	public JRasterFloor() {
		super(new FloorViewModel(null));
                
		setLayout( null );
		setBackground( Color.black );
	}

	public ZControl getZcontrol() {
		return zcontrol;
	}

	public void setZcontrol( ZControl zcontrol ) {
		this.zcontrol = zcontrol;
	}

	public void setSimulationData( EvacuationCellularAutomaton ca, ZToCARasterContainer container, ZToCAMapping mapping ) {
		this.ca = ca;
		this.container = container;
		this.mapping = mapping;
	}

	public EvacuationCellularAutomaton getCa() {
		return ca;
	}

	public ZToCARasterContainer getContainer() {
		return container;
	}

	public ZToCAMapping getMapping() {
		return mapping;
	}

	/** @return The floor that is currently displayed by this JRasterFloor. */
	public Floor getFloor () {
		return myFloor;
	}

	boolean loaded = false;
	
	/**
	 * Opens a floor and adds the rooms as components to the floor.
	 * @param floor the displayed floor
	 */
	public void displayFloor( Floor floor ) {
		loaded = false;	// disable painting of children during display. would end up in exceptions due to the multi-thread approach
		boolean showPotentialValue = PropertyContainer.getInstance().getAsBoolean( "editor.options.cavis.staticPotential" );
		boolean showDynamicPotential = PropertyContainer.getInstance().getAsBoolean( "editor.options.cavis.dynamicPotential" );
		boolean showCellUtilization = false;
		showPotentialValue = true;
		showDynamicPotential = false;
		
		DynamicPotential dp = null;

		if( myFloor != null ) {
			//removeAll();
			final int componentCount = getComponentCount();
			int count = 0;
			for( Component c : getComponents() ) {
				remove( c );
				//AlgorithmTask.getInstance().setProgress( Math.min( 99, (++count*100)/componentCount ), "", "" );
			}
		}

		myFloor = floor;
		if( floor == null || ca == null ) {
			loaded = true;
			return;
		}

                
		updateOffsets( getFloorModel() ); //@// floor
		
		// TODO: Provide better implementation - Do not recreate everything each time
		PotentialManager pm = ca.getPotentialManager();
		PotentialController pc = new SPPotentialController( ca );
		StaticPotential sp = null;
		sp = pc.mergePotentials( new ArrayList<StaticPotential>( pm.getStaticPotentials() ) );
		dp = pm.getDynamicPotential();

		final int roomCount = floor.getRooms().size();
		int count = 0;
		for( Room r : floor.getRooms() ) {
			if( container != null ) {
				ZToCARoomRaster roomRaster = container.getRasteredRoom( r );
				LinkedList<ZToCARasterSquare> squares = roomRaster.getAccessibleSquares();
				for( ZToCARasterSquare square : squares ) {
					// Color depending of the cell type
					EvacCell cell = mapping.get( square );
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
					if( !cell.isPassable( Direction8.Left) )
						poly.addWall( Direction8.Left );
					if( !cell.isPassable( Direction8.Right) )
						poly.addWall( Direction8.Right );
					if( !cell.isPassable( Direction8.Top) )
						poly.addWall( Direction8.Top );
					if( !cell.isPassable( Direction8.Down ) )
						poly.addWall( Direction8.Down );
					//nicht zeichnen
					add( poly );
					poly.displayPolygon( square.getSquare() );
				}
			}
			//AlgorithmTask.getInstance().setProgress( (++count*100)/roomCount, "", "" );
		}

		loaded = true;	//allow painting again as we are finisched
		revalidate();
		repaint();
	}

	public void update() {
		for( Component component : getComponents() ) {
			((JCellPolygon)component).update();
		}
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

	@Override
	protected void paintChildren(Graphics g) {
		if( !loaded )
			return;
		super.paintChildren( g );
	}
}
