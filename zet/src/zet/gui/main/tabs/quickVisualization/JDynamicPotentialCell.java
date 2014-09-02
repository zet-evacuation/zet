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
/**
 * Class JDynamicPotentialCell
 * Created on 05.05.2008, 18:16:07
 */

package zet.gui.main.tabs.quickVisualization;

import ds.ca.evac.EvacCell;
import ds.ca.evac.EvacuationCellularAutomaton;
import zet.gui.main.tabs.base.AbstractFloor;
import java.awt.Color;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JDynamicPotentialCell extends JPotentialCell {

	public JDynamicPotentialCell( EvacCell cell, AbstractFloor floor, int potential, int maxPotential, EvacuationCellularAutomaton ca ) {
		super( cell, floor, potential, maxPotential, ca );
		Color fillColor = potential > 0 ? new Color( 1.0f, 1-(float)potential/maxPotential, 1-(float)potential/maxPotential ) : Color.white;
		this.setFillColor( fillColor );
	}
	
	public JDynamicPotentialCell( EvacCell cell, AbstractFloor floor, Color lineColor, int potential, int maxPotential, EvacuationCellularAutomaton ca ) {
		super( cell, floor, lineColor, potential, maxPotential, ca );
		Color fillColor = potential > 0 ? new Color( 1.0f, 1-(float)potential/maxPotential, 1-(float)potential/maxPotential ) : Color.white;
		this.setFillColor( fillColor );
	}
}
