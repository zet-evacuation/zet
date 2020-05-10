/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package gui.visualization.draw.ca;

import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.ca.GLCellControl;

/**
 * This class represents the graphical representation of a {@link ds.ca.StairCell}.
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
	 * Overriden version of {@code updateFloorColor}. It can switch between
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
