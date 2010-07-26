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
/**
 * Class JDynamicPotentialCell
 * Erstellt 05.05.2008, 18:16:07
 */

package zet.gui.components.tabs.quickVisualization;

import zet.gui.components.tabs.base.AbstractFloor;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JDynamicPotentialCell extends JPotentialCell {

	public JDynamicPotentialCell( AbstractFloor floor, int potential, int maxPotential ) {
		super( floor, potential, maxPotential );
		Color fillColor = potential > 0 ? new Color( 1.0f, 1-(float)potential/maxPotential, 1-(float)potential/maxPotential ) : Color.white;
		this.setFillColor( fillColor );
	}
	
	public JDynamicPotentialCell( AbstractFloor floor, Color lineColor, int potential, int maxPotential ) {
		super( floor, lineColor, potential, maxPotential );
		Color fillColor = potential > 0 ? new Color( 1.0f, 1-(float)potential/maxPotential, 1-(float)potential/maxPotential ) : Color.white;
		this.setFillColor( fillColor );
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
	}
}
